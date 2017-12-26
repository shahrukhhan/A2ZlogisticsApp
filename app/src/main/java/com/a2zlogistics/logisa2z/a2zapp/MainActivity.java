package com.a2zlogistics.logisa2z.a2zapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zlogistics.logisa2z.a2zapp.activities.DiscountActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.a2zlogistics.logisa2z.a2zapp.activities.CardActivity;
import com.a2zlogistics.logisa2z.a2zapp.activities.TransactionActivity;
import com.a2zlogistics.logisa2z.a2zapp.activities.UserTransactionActivity;
import com.a2zlogistics.logisa2z.a2zapp.database.LocalDB;
import com.a2zlogistics.logisa2z.a2zapp.dialogs.CustomDialogClass;
import com.a2zlogistics.logisa2z.a2zapp.dialogs.PasswordDialogClass;
import com.a2zlogistics.logisa2z.a2zapp.utils.Constants;
import com.a2zlogistics.logisa2z.a2zapp.utils.MyContextWrapper;
import com.a2zlogistics.logisa2z.a2zapp.utils.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView mainBalance, rewardPoints;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private LinearLayout linearLayout;
    private GridLayout gridLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progressBar = findViewById(R.id.main_progress_bar);
        linearLayout = findViewById(R.id.main_linear_layout);
        gridLayout = findViewById(R.id.main_grid_view);
        linearLayout.setVisibility(View.INVISIBLE);
        gridLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mainBalance = findViewById(R.id.main_balance);
        rewardPoints = findViewById(R.id.reward_points);
        fetchUser();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase.getApplicationContext());
        Locale languageType = new Locale(prefs.getString(Constants.LANGUAGE, "en"));
        super.attachBaseContext(MyContextWrapper.wrap(newBase, languageType));
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
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
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+917891088099"));
                    startActivity(intent);
                }
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
                progressBar.setVisibility(View.INVISIBLE);
                gridLayout.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                try {
                    String balance = getResources().getString(R.string.main_balance_text) + String.format(Locale.US, "%.2f", response.getDouble("Balance"));
                    String discount = getResources().getString(R.string.reward_points_text) + String.format(Locale.US, "%.2f", response.getDouble("Discount"));
                    mainBalance.setText(balance);
                    rewardPoints.setText(discount);
                    editor.putFloat(Constants.ACCOUNT_BALANCE, (Float.parseFloat(response.getString("Balance"))));
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

    public void openDiscountView(View v) {
        Intent intent = new Intent(this, DiscountActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALL_PHONE: {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED ||
                        grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+917891088099"));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please give permission to make call", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
