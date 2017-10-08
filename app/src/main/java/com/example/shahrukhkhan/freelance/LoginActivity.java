package com.example.shahrukhkhan.freelance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.shahrukhkhan.freelance.Dialogs.CustomDialogClass;
import com.example.shahrukhkhan.freelance.Utils.Constants;
import com.example.shahrukhkhan.freelance.Utils.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText userName, password;
    private TextView errorText;
    private Button login;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getString(Constants.TOKEN, "").equals("")) {
            if (preferences.getString(Constants.LANGUAGE, "").equals("English"))
                setLocale("");
            else
                setLocale("hi");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        errorText = (TextView) findViewById(R.id.wrong_credentials);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userName.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    login();
                } else {
                    if (userName.getText().toString().equals(""))
                        errorText.setText(R.string.empty_username);
                    else if (password.getText().toString().equals(""))
                        errorText.setText(R.string.empty_password);
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void login() {
        String url = Constants.API_URL + "/Token";
        final String requestBody = "username=" + userName.getText().toString() + "&password=" + password.getText().toString() + "&grant_type=password";
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Constants.TOKEN, res.getString("access_token"));
                            editor.putString(Constants.USERNAME, res.getString("userName"));
                            editor.putString(Constants.TOKEN_EXPIRY, res.getString(".expires"));
                            editor.putInt(Constants.LOGIN, 1);
                            editor.apply();
                            errorText.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            openDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
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
                                        errorText.setText(R.string.error_incorrect_creds);
                                        errorText.setVisibility(View.VISIBLE);
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
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        MyVolley.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void openDialog() {
        CustomDialogClass dialogClass = new CustomDialogClass(LoginActivity.this);
        dialogClass.setCanceledOnTouchOutside(false);
        dialogClass.show();
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}