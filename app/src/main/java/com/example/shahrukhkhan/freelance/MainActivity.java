package com.example.shahrukhkhan.freelance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shahrukhkhan.freelance.Activities.CardActivity;
import com.example.shahrukhkhan.freelance.Activities.TransactionActivity;
import com.example.shahrukhkhan.freelance.Activities.UserTransactionActivity;
import com.example.shahrukhkhan.freelance.Database.LocalDB;
import com.example.shahrukhkhan.freelance.Dialogs.CustomDialogClass;
import com.example.shahrukhkhan.freelance.Dialogs.PasswordDialogClass;
import com.example.shahrukhkhan.freelance.Utils.Constants;
import com.example.shahrukhkhan.freelance.Utils.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView mainBalance, rewardPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainBalance = (TextView) findViewById(R.id.main_balance);
        rewardPoints = (TextView) findViewById(R.id.reward_points);
        fetchUser();
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
                CustomDialogClass dialogClass = new CustomDialogClass(MainActivity.this);
                dialogClass.setCanceledOnTouchOutside(false);
                dialogClass.show();
                break;
            case R.id.change_password:
                PasswordDialogClass passwordDialogClass = new PasswordDialogClass(MainActivity.this);
                passwordDialogClass.setCanceledOnTouchOutside(false);
                passwordDialogClass.show();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchUser() {
        String url = Constants.API_URL + "/api/GetUser";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = preferences.edit();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("hello", response+"");
                try {
                    String balance = Constants.MAIN_BALANCE + response.getInt("Balance");
                    String discount = Constants.REWARD_POINTS + response.getInt("Discount");
                    mainBalance.setText(balance);
                    rewardPoints.setText(discount);
                    editor.putInt(Constants.ACCOUNT_BALANCE, response.getInt("Balance"));
                    editor.putString(Constants.NAME, response.getString("Name"));
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
                                logout();
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
        MyVolley.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void logout() {
        LocalDB.getmInstance(getApplicationContext()).deleteTransactions();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USERNAME, "");
        editor.putString(Constants.TOKEN, "");
        editor.putString(Constants.TOKEN_EXPIRY, "");
        editor.putString(Constants.NAME, "");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void openCardView(View v) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, v.getId());
        startActivity(intent);
    }

    public void openTransactionView(View v) {
        Intent intent = new Intent(this, TransactionActivity.class);
        intent.putExtra(Constants.ACTIVITY_ID, v.getId());
        startActivity(intent);
    }

    public void openUserTransactionView(View v) {
        Intent intent = new Intent(this, UserTransactionActivity.class);
        startActivity(intent);
    }
}
