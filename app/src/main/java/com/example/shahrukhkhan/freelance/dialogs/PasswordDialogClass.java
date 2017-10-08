package com.example.shahrukhkhan.freelance.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.utils.Constants;
import com.example.shahrukhkhan.freelance.utils.MyVolley;
import com.example.shahrukhkhan.freelance.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shahrukh Khan on 9/27/2017.
 */

public class PasswordDialogClass extends Dialog implements View.OnClickListener {
    private Activity c;
    private EditText currPwd, newPwd, confirmPwd;
    private AppCompatButton save, close;

    public PasswordDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.password_dialog);
        currPwd = findViewById(R.id.current_password);
        newPwd = findViewById(R.id.new_password);
        confirmPwd = findViewById(R.id.confirm_password);
        save = findViewById(R.id.password_save_button);
        close = findViewById(R.id.password_close_button);
        save.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.password_close_button) {
            dismiss();
        } else if(view.getId() == R.id.password_save_button) {
            if (currPwd.getText().toString().equals("") || newPwd.getText().toString().equals("")
                    || confirmPwd.getText().toString().equals("")) {
                Toast.makeText(c.getApplicationContext(), "Field cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                makeRequest();
            }
        }
    }

    public void makeRequest() {
        String url = Constants.API_URL + "/api/GetUser";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        JSONObject body = new JSONObject();
        try {
            body.put("OldPassword", currPwd.getText().toString());
            body.put("NewPassword", newPwd.getText().toString());
            body.put("ConfirmPassword", confirmPwd.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss();
                Util.snackBarOnUIThread("Password Successfully Changed!", c, "#a4c639");
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
