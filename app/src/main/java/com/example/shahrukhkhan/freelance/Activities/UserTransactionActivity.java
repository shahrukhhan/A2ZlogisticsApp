package com.example.shahrukhkhan.freelance.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.shahrukhkhan.freelance.Adapter.UserTransactionAdapter;
import com.example.shahrukhkhan.freelance.Database.LocalDB;
import com.example.shahrukhkhan.freelance.Dialogs.CustomDialogClass;
import com.example.shahrukhkhan.freelance.Dialogs.PasswordDialogClass;
import com.example.shahrukhkhan.freelance.Dialogs.PaymentDialogClass;
import com.example.shahrukhkhan.freelance.LoginActivity;
import com.example.shahrukhkhan.freelance.Model.UserTransactionData;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.Utils.Constants;
import com.example.shahrukhkhan.freelance.Utils.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTransactionActivity extends AppCompatActivity {

    private List<UserTransactionData> userTransactionDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserTransactionAdapter userTransactionAdapter;
    private TextView addPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction);
        addPayment = (TextView) findViewById(R.id.add_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView = (RecyclerView) findViewById(R.id.transaction_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        userTransactionAdapter = new UserTransactionAdapter(userTransactionDataList, UserTransactionActivity.this);
        recyclerView.setAdapter(userTransactionAdapter);
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentDialogClass paymentDialogClass = new PaymentDialogClass(UserTransactionActivity.this);
                paymentDialogClass.setCanceledOnTouchOutside(false);
                paymentDialogClass.show();
            }
        });
        fetchUserTransactions();
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
                PasswordDialogClass passwordDialogClass = new PasswordDialogClass(UserTransactionActivity.this);
                passwordDialogClass.setCanceledOnTouchOutside(false);
                passwordDialogClass.show();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchUserTransactions() {
        userTransactionDataList.clear();
        String url = Constants.API_URL + "/api/GetUserTransactions";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    UserTransactionData userTransactionData = new UserTransactionData();
                    try {
                        JSONObject transaction = response.getJSONObject(i);
                        userTransactionData.setUserTxnDesc(transaction.getString("AdminDescription"));
                        userTransactionData.setUserTxnDate(transaction.getString("TimeStamp"));
                        userTransactionData.setUserTxnAmt(transaction.getInt("Amount"));
                        userTransactionDataList.add(userTransactionData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                userTransactionAdapter.notifyDataSetChanged();

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
