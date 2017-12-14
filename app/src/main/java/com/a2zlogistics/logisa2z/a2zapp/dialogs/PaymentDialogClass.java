package com.a2zlogistics.logisa2z.a2zapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.a2zlogistics.logisa2z.a2zapp.R;
import com.a2zlogistics.logisa2z.a2zapp.utils.Constants;
import com.a2zlogistics.logisa2z.a2zapp.utils.MyVolley;
import com.a2zlogistics.logisa2z.a2zapp.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shahrukh Khan on 10/1/2017.
 */

public class PaymentDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public AppCompatButton save;
    public AppCompatButton close;
    public AppCompatEditText amount, description;
    public AppCompatSpinner mode, account;
    public List<String> accounts = new ArrayList<>();

    public PaymentDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payment_dialog);
        amount = findViewById(R.id.payment_amount);
        mode = findViewById(R.id.payment_mode);
        account = findViewById(R.id.payment_account);
        description = findViewById(R.id.payment_description);
        save = findViewById(R.id.payment_save_button);
        close = findViewById(R.id.payment_close_button);
        save.setOnClickListener(this);
        close.setOnClickListener(this);
        fetchAccounts();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.payment_close_button) {
            dismiss();
        } else if (view.getId() == R.id.payment_save_button) {
            if (amount.getText().toString().equals("") || Integer.parseInt(amount.getText().toString()) <= 0) {
                Toast.makeText(c.getApplicationContext(), "Invalid Request.", Toast.LENGTH_SHORT).show();
            } else if (mode.getSelectedItem().toString().equals("Mode")) {
                Toast.makeText(c.getApplicationContext(), "Please Select Mode.", Toast.LENGTH_SHORT).show();
            } else {
                save.setEnabled(false);
                makeRequest();
            }
        }
    }

    private void makeRequest() {
        String url = Constants.API_URL + "/api/AddUserRequest";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        JSONObject body = new JSONObject();
        try {
            body.put("Username", preferences.getString(Constants.USERNAME, ""));
            body.put("Amount", Integer.parseInt(amount.getText().toString()));
            body.put("Name", preferences.getString(Constants.NAME, ""));
            body.put("Mode", mode.getSelectedItem().toString());
            body.put("UserDescription", description.getText().toString().toUpperCase());
            body.put("AdminDescription", account.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss();
                Util.snackBarOnUIThread("Request Successful!", c, "#a4c639");
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

    public void fetchAccounts() {
        String url = Constants.API_URL + "/api/getBankAccounts";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        accounts.add(obj.getString("BankNameAccountNo"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, accounts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                account.setAdapter(adapter);
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
