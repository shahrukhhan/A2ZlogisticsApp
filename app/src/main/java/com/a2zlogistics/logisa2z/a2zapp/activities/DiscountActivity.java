package com.a2zlogistics.logisa2z.a2zapp.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.a2zlogistics.logisa2z.a2zapp.LoginActivity;
import com.a2zlogistics.logisa2z.a2zapp.R;
import com.a2zlogistics.logisa2z.a2zapp.adapter.DiscountAdapter;
import com.a2zlogistics.logisa2z.a2zapp.database.LocalDB;
import com.a2zlogistics.logisa2z.a2zapp.dialogs.CustomDialogClass;
import com.a2zlogistics.logisa2z.a2zapp.dialogs.PasswordDialogClass;
import com.a2zlogistics.logisa2z.a2zapp.model.DiscountData;
import com.a2zlogistics.logisa2z.a2zapp.utils.Constants;
import com.a2zlogistics.logisa2z.a2zapp.utils.MyContextWrapper;
import com.a2zlogistics.logisa2z.a2zapp.utils.MyVolley;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiscountActivity extends AppCompatActivity {

    private List<DiscountData> discountDataList = new ArrayList<>();
    private List<DiscountData> newList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DiscountAdapter discountAdapter;
    private TextView noDiscountTransText;
    private AppCompatEditText fromDate, toDate;
    public int dateType;
    private final static int TYPE_FROM = 0;
    private final static int TYPE_TO = 1;

    private static Date initialDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        noDiscountTransText = findViewById(R.id.no_discount_trans_text);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        progressBar = findViewById(R.id.discount_progress_bar);
        fromDate = findViewById(R.id.from_discount_date);
        toDate = findViewById(R.id.to_discount_date);
        fromDate.setFocusable(false);
        toDate.setFocusable(false);
        recyclerView = findViewById(R.id.discount_recycler_view);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        discountAdapter = new DiscountAdapter(newList, DiscountActivity.this);
        recyclerView.setAdapter(discountAdapter);

        String str = "01/09/2017";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy", Locale.US);
        try {
            initialDate = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(fromDate.getWindowToken(), 0);
                dateType = TYPE_FROM;
                DialogFragment newFragment = new DiscountActivity.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(toDate.getWindowToken(), 0);
                dateType = TYPE_TO;
                DialogFragment newFragment = new DiscountActivity.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        fetchDiscountTransactions();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase.getApplicationContext());
        Locale languageType = new Locale(prefs.getString(Constants.LANGUAGE, "en"));
        super.attachBaseContext(MyContextWrapper.wrap(newBase, languageType));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
                PasswordDialogClass passwordDialogClass = new PasswordDialogClass(DiscountActivity.this);
                passwordDialogClass.setCanceledOnTouchOutside(false);
                passwordDialogClass.show();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchDiscountTransactions() {
        discountDataList.clear();
        String url = Constants.API_URL + "/api/GetDiscountTransactions";
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    DiscountData discountData = new DiscountData();
                    try {
                        JSONObject discount = response.getJSONObject(i);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date date = null;
                        try {
                            date = sdf.parse(discount.getString("TimeStamp"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        discountData.setCardId(discount.getString("CardType") + " - " + discount.getString("CardID"));
                        discountData.setAmount(Float.parseFloat(discount.getString("Amount")));
                        discountData.setDiscountId(discount.getString("DiscountID"));
                        discountData.setDiscountAmount(Float.parseFloat(discount.getString("DiscountAmount")));
                        discountData.setDiscountPercent(Float.parseFloat(discount.getString("DiscountPercent")));
                        discountData.setTimeStamp(date.getTime());
                        discountDataList.add(discountData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                newList.addAll(discountDataList);
                if (newList.isEmpty())
                    noDiscountTransText.setVisibility(View.VISIBLE);
                else
                    noDiscountTransText.setVisibility(View.GONE);
                discountAdapter.notifyDataSetChanged();
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

        DiscountActivity activity;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            activity = (DiscountActivity) getActivity();
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            long minDate = System.currentTimeMillis();
            // Create a new instance of DatePickerDialog and return it
            try {
                String dateString = "01/09/2017";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = sdf.parse(dateString);
                minDate = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.getDatePicker().setMinDate(minDate);
            return datePicker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = day + "/" + (month + 1) + "/" + year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            Date cal = null;
            String dispDate = null;
            try {
                cal = sdf.parse(date);
                dispDate = formatter.format(cal);
            } catch (Exception e) {
                e.printStackTrace();
            }
            activity.displayDate(dispDate);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            activity.updateList();
        }
    }

    public void displayDate(String date) {
        if (dateType == TYPE_FROM) {
            fromDate.setText(date);
        } else {
            toDate.setText(date);
        }
    }

    public void updateList() {
        String from = fromDate.getText().toString();
        String to = toDate.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        long fromDate = System.currentTimeMillis();
        long toDate = System.currentTimeMillis();
        try {
            if (from.equals("")) {
                fromDate = initialDate.getTime();
            } else {
                fromDate = sdf.parse(from).getTime();
            }
            if (to.equals("")) {
                toDate = System.currentTimeMillis();
            } else {
                toDate = sdf.parse(to).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newList.clear();
        for (DiscountData data : discountDataList) {
            if (data.getTimeStamp() >= fromDate && data.getTimeStamp() <= toDate)
                newList.add(data);
        }
        if(newList.isEmpty())
            noDiscountTransText.setVisibility(View.VISIBLE);
        else
            noDiscountTransText.setVisibility(View.GONE);
        discountAdapter.notifyDataSetChanged();
    }
}
