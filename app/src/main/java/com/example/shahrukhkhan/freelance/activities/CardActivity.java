package com.example.shahrukhkhan.freelance.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.shahrukhkhan.freelance.LoginActivity;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.adapter.CardsAdapter;
import com.example.shahrukhkhan.freelance.database.LocalDB;
import com.example.shahrukhkhan.freelance.dialogs.CustomDialogClass;
import com.example.shahrukhkhan.freelance.dialogs.PasswordDialogClass;
import com.example.shahrukhkhan.freelance.dialogs.RechargeDialogClass;
import com.example.shahrukhkhan.freelance.model.CardData;
import com.example.shahrukhkhan.freelance.utils.Constants;
import com.example.shahrukhkhan.freelance.utils.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardActivity extends AppCompatActivity {

    private List<CardData> cardDataList = new ArrayList<>();
    private CardsAdapter adapter;
    private GridView gridview;
    private ProgressBar progressBar;
    private int activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        gridview = findViewById(R.id.grid_view);
        progressBar = findViewById(R.id.card_progress_bar);
        gridview.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        activityType = getIntent().getIntExtra(Constants.ACTIVITY_ID, 0);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (activityType == R.id.cards_usage_icon) {
                    Intent intent = new Intent(CardActivity.this, TransactionActivity.class);
                    intent.putExtra(Constants.ACTIVITY_ID, activityType);
                    intent.putExtra("name", cardDataList.get(i).getCardName());
                    intent.putExtra("number", cardDataList.get(i).getCardId());
                    intent.putExtra("vehicle", cardDataList.get(i).getVehicleNumber());
                    intent.putExtra("balance", cardDataList.get(i).getCardBalance());
                    startActivity(intent);
                } else {
                    RechargeDialogClass dialogClass = new RechargeDialogClass(CardActivity.this, cardDataList.get(i));
                    dialogClass.setCanceledOnTouchOutside(false);
                    dialogClass.show();
                }
            }
        });
        fetchCards();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.call_for_support:
                break;
            case R.id.change_language:
                CustomDialogClass dialogClass = new CustomDialogClass(this);
                dialogClass.setCanceledOnTouchOutside(false);
                dialogClass.show();
                break;
            case R.id.change_password:
                PasswordDialogClass passwordDialogClass = new PasswordDialogClass(CardActivity.this);
                passwordDialogClass.setCanceledOnTouchOutside(false);
                passwordDialogClass.show();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchCards() {
        String url = Constants.API_URL + "/api/GetCard";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                gridview.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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
                cardData.setCardName(card.getString("CardType"));
                cardData.setVehicleNumber(card.getString("VehicleNumber"));
                cardData.setCardBalance(card.getInt("Balance"));
                cardDataList.add(cardData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new CardsAdapter(CardActivity.this, cardDataList, activityType);
        gridview.setAdapter(adapter);
    }

    private void logout() {
        LocalDB.getmInstance(getApplicationContext()).deleteTransactions();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USERNAME, "");
        editor.putString(Constants.TOKEN, "");
        editor.putString(Constants.TOKEN_EXPIRY, "");
        editor.putString(Constants.NAME, "");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
