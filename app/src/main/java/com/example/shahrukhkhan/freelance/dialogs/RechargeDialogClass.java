package com.example.shahrukhkhan.freelance.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.database.LocalDB;
import com.example.shahrukhkhan.freelance.model.CardData;
import com.example.shahrukhkhan.freelance.model.TransactionData;
import com.example.shahrukhkhan.freelance.utils.Constants;
import com.example.shahrukhkhan.freelance.utils.MyVolley;
import com.example.shahrukhkhan.freelance.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shahrukh Khan on 9/23/2017.
 */

public class RechargeDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public AppCompatButton save;
    public AppCompatButton close;
    public CardData cardData;
    public AppCompatEditText cardNo, registeredVehicle, amount;

    public RechargeDialogClass(Activity a, CardData cardData) {
        super(a);
        this.c = a;
        this.cardData = cardData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recharge_dialog);
        cardNo = findViewById(R.id.card_no);
        registeredVehicle = findViewById(R.id.registered_vehicle_no);
        amount = findViewById(R.id.amount_to_recharge);
        save = findViewById(R.id.recharge_save_button);
        close = findViewById(R.id.recharge_close_button);
        save.setOnClickListener(this);
        close.setOnClickListener(this);
        String name = cardData.getCardName() + " - " + cardData.getCardId();
        String vehicle = String.valueOf(cardData.getVehicleNumber());
        cardNo.setText(name);
        registeredVehicle.setText(vehicle);
        cardNo.setEnabled(false);
        registeredVehicle.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        if (view.getId() == R.id.recharge_close_button) {
            dismiss();
        } else if (view.getId() == R.id.recharge_save_button) {
            if (amount.getText().toString().equals("") || Integer.parseInt(amount.getText().toString()) <= 0) {
                Toast.makeText(c.getApplicationContext(),"Invalid Request",Toast.LENGTH_SHORT).show();
            } else {
                int requestAmount = Integer.parseInt(amount.getText().toString());
                if (requestAmount > preferences.getInt(Constants.ACCOUNT_BALANCE, -1)) {
                    Toast.makeText(c.getApplicationContext(),"Not enough balance to make this request",Toast.LENGTH_SHORT).show();
                } else {
                    makeRequest(cardData, requestAmount);
                }
            }
        }
    }

    private void makeRequest(final CardData card, final int amount) {
        String url = Constants.API_URL + "/api/AddRequest";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        JSONObject body = new JSONObject();
        try {
            body.put("CardID", card.getCardId());
            body.put("CardType", card.getCardName());
            body.put("Amount", amount);
            body.put("UserName", preferences.getString(Constants.USERNAME, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                TransactionData transactionData = new TransactionData();
                try {
                    transactionData.setTxnId(response.getString("RequestID"));
                    transactionData.setCardName(card.getCardName());
                    transactionData.setCardStatus(2);
                    transactionData.setCardType(response.getString("CardType"));
                    transactionData.setCardId(response.getString("CardID"));
                    transactionData.setCardAmount(response.getInt("Amount"));
                    transactionData.setCardTimeStamp(response.getString("TimeStamp"));
                    transactionData.setCardRemarks("Pending");
                    transactionData.setUserID(response.getString("UserName"));
                    LocalDB.getmInstance(c.getApplicationContext()).putData(transactionData);
                    editor.putInt(Constants.ACCOUNT_BALANCE, response.getInt("Balance"));
                    editor.apply();
                    dismiss();
                    Util.snackBarOnUIThread("Request Successful!", c, "#a4c639");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Util.snackBarOnUIThread(c.getString(R.string.network_error), c, "#B94A48");
                } else {
                    NetworkResponse resp = error.networkResponse;
                    if (resp != null && resp.data != null && resp.statusCode != 0) {
                        switch (resp.statusCode) {
                            // handle bad request
                            case 400:
                                Util.snackBarOnUIThread(c.getString(R.string.bad_request), c, "#B94A48");
                                break;
                            case 401:
                                break;
                            default:
                                Util.snackBarOnUIThread(c.getString(R.string.server_error), c, "#B94A48");
                        }
                    }
                }
                dismiss();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "Bearer " + preferences.getString(Constants.TOKEN, ""));
                return headers;
            }
        };
        MyVolley.getInstance(c.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
