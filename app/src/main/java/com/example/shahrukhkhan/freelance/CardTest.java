package com.example.shahrukhkhan.freelance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shahrukhkhan.freelance.database.LocalDB;
import com.example.shahrukhkhan.freelance.interfaces.ListClickListener;
import com.example.shahrukhkhan.freelance.model.CardData;
import com.example.shahrukhkhan.freelance.model.TransactionData;
import com.example.shahrukhkhan.freelance.utils.Constants;
import com.example.shahrukhkhan.freelance.utils.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardTest extends AppCompatActivity implements ListClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<CardData> cardDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_test);
        fetchCards();
        recyclerView = (RecyclerView) findViewById(R.id.card_list_recyclerView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.card_refresh);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchCards();
            }
        });
    }

//    @Override
//    public void onItemClick(int position, boolean fabClicked, String requestAmount) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        cardDataList.get(position).setFabStatus(fabClicked);
//        if (!fabClicked) {
//            if (requestAmount.equals("") || Integer.parseInt(requestAmount) <= 0) {
//                Toast.makeText(getApplicationContext(), "Invalid Amount", Toast.LENGTH_SHORT).show();
//            } else {
//                if (Integer.parseInt(requestAmount) > preferences.getInt(Constants.MAIN_BALANCE, -1)) {
//                    Toast.makeText(getApplicationContext(), "Not enough balance to make this request", Toast.LENGTH_SHORT).show();
//                } else {
//                    makeRequest(cardDataList.get(position), Integer.parseInt(requestAmount), position);
//                }
//            }
//        } else {
//        }
//    }

    private void fetchCards() {
        String url = Constants.API_URL + "/api/GetCard";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                populateList(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                } else {
                    NetworkResponse resp = error.networkResponse;
                    if (resp != null && resp.data != null && resp.statusCode != 0) {
                        switch (resp.statusCode) {
                            // handle bad request
                            case 400:
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.bad_request),
                                        Toast.LENGTH_LONG).show();
                                break;
                            case 401:
                                break;
                            default:
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.server_error),
                                        Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        ) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "Bearer " + preferences.getString(Constants.TOKEN, ""));
                return headers;
            }
        };
        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void populateList(JSONArray cards) {
        cardDataList.clear();
        for (int i = 0; i < cards.length(); i++) {
            CardData cardData = new CardData();
            try {
                JSONObject card = cards.getJSONObject(i);
                cardData.setCardId(card.getString("CardID"));
                cardData.setCardName(card.getString("UserName"));
                cardData.setVehicleNumber(card.getString("Type"));
                cardData.setCardBalance(card.getInt("Balance"));
                cardData.setCardStatus(card.getInt("Status"));
                cardData.setFabStatus(false);
                cardDataList.add(cardData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        refreshLayout.setRefreshing(false);
    }

    private void makeRequest(final CardData card, final int amount, final int position) {
        String url = Constants.API_URL + "/api/AddRequest";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        JSONObject body = new JSONObject();
        try {
            body.put("CardID", card.getCardId());
            body.put("CardType", card.getVehicleNumber());
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
                    LocalDB.getmInstance(getApplicationContext()).putData(transactionData);
                    Toast.makeText(getApplicationContext(), "Request Successful!", Toast.LENGTH_SHORT).show();
                    cardDataList.get(position).setCardStatus(1);
                    editor.putInt(Constants.MAIN_BALANCE, response.getInt("Balance"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                } else {
                    NetworkResponse resp = error.networkResponse;
                    if (resp != null && resp.data != null && resp.statusCode != 0) {
                        switch (resp.statusCode) {
                            // handle bad request
                            case 400:
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.bad_request),
                                        Toast.LENGTH_LONG).show();
                                break;
                            case 401:
                                break;
                            default:
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.server_error),
                                        Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", "Bearer " + preferences.getString(Constants.TOKEN, ""));
                return headers;
            }
        };
        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onItemClick(int position, int activityType) {

    }
}
