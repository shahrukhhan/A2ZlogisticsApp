package com.example.shahrukhkhan.freelance.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.shahrukhkhan.freelance.LoginActivity;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.database.LocalDB;
import com.example.shahrukhkhan.freelance.dialogs.CustomDialogClass;
import com.example.shahrukhkhan.freelance.dialogs.PasswordDialogClass;
import com.example.shahrukhkhan.freelance.fragments.TransactionFragment;
import com.example.shahrukhkhan.freelance.utils.Constants;
import com.example.shahrukhkhan.freelance.utils.MyContextWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TransactionFragment transactionFragmentAll;
    private TransactionFragment transactionFragmentPaid;
    private TransactionFragment transactionFragmentRecharged;
    private TextView cardUsageText, cardUsageBalance;
    public AppCompatEditText fromDate, toDate;
    public String name, number, vehicle;
    public int activityType, dateType;
    public float balance;
    private final static int TYPE_FROM = 0;
    private final static int TYPE_TO = 1;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        vehicle = intent.getStringExtra("vehicle");
        balance = intent.getFloatExtra("balance", 0.0f);
        activityType = intent.getIntExtra(Constants.ACTIVITY_ID, 0);
        fromDate = findViewById(R.id.from_trans_date);
        toDate = findViewById(R.id.to_trans_date);
        fromDate.setFocusable(false);
        toDate.setFocusable(false);
        cardUsageText = findViewById(R.id.card_usage_text);
        cardUsageBalance = findViewById(R.id.txn_card_balance);
        init();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(fromDate.getWindowToken(), 0);
                dateType = TYPE_FROM;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(toDate.getWindowToken(), 0);
                dateType = TYPE_TO;
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase.getApplicationContext());
        Locale languageType = new Locale(prefs.getString(Constants.LANGUAGE, "en"));
        super.attachBaseContext(MyContextWrapper.wrap(newBase, languageType));
    }

    public void init() {
        if (activityType == R.id.cards_usage_icon) {
            cardUsageText.setVisibility(View.VISIBLE);
            cardUsageBalance.setVisibility(View.VISIBLE);
            String text = name + " - " + number + " | " + vehicle;
            String bal = getResources().getString(R.string.card_balance_text) + String.format(Locale.US, "%.2f", balance);
            cardUsageText.setText(text);
            cardUsageBalance.setText(bal);
        } else {
            cardUsageText.setVisibility(View.GONE);
            cardUsageBalance.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.call_for_support:
                break;
            case R.id.change_language:
                CustomDialogClass dialogClass = new CustomDialogClass(this);
                dialogClass.setCanceledOnTouchOutside(false);
                dialogClass.show();
                break;
            case R.id.change_password:
                PasswordDialogClass passwordDialogClass = new PasswordDialogClass(TransactionActivity.this);
                passwordDialogClass.setCanceledOnTouchOutside(false);
                passwordDialogClass.show();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            switch (position) {
                case 0:
                    if (transactionFragmentAll == null) {
                        transactionFragmentAll = new TransactionFragment();
                        args.putInt("type", 0);
                        transactionFragmentAll.setArguments(args);
                    }
                    return transactionFragmentAll;
                case 1:
                    if (transactionFragmentPaid == null) {
                        transactionFragmentPaid = new TransactionFragment();
                        args.putInt("type", 1);
                        transactionFragmentPaid.setArguments(args);
                    }
                    return transactionFragmentPaid;
                case 2:
                    if (transactionFragmentRecharged == null) {
                        transactionFragmentRecharged = new TransactionFragment();
                        args.putInt("type", 2);
                        transactionFragmentRecharged.setArguments(args);
                    }
                    return transactionFragmentRecharged;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.all);
                case 1:
                    return getResources().getString(R.string.paid);
                case 2:
                    return getResources().getString(R.string.recharged);
            }
            return null;
        }
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

        TransactionActivity activity;
        String calDate;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Locale locale = Locale.getDefault();
            // Use the current date as the default date in the picker
            activity = (TransactionActivity) getActivity();
            if (activity.dateType == TYPE_FROM)
                calDate = activity.fromDate.getText().toString();
            else
                calDate = activity.toDate.getText().toString();
            Date sdfDate = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            try {
                sdfDate = dateFormat.parse(calDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int year, month, day;
            if (calDate.equals("")) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = Integer.parseInt(new SimpleDateFormat("yyyy").format(sdfDate));
                month = Integer.parseInt(new SimpleDateFormat("MM").format(sdfDate));
                month -= 1;
                day = Integer.parseInt(new SimpleDateFormat("dd").format(sdfDate));
            }
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
            datePicker.updateDate(year, month, day);
            return datePicker;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = day + "/" + (month + 1) + "/" + year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            Date cal;
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
            super.onCancel(dialog);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            activity.updateDate();
        }
    }

    public void displayDate(String date) {
        if (dateType == TYPE_FROM) {
            fromDate.setText(date);
        } else {
            toDate.setText(date);
        }
    }

    public void updateDate() {
        if (transactionFragmentAll != null)
            transactionFragmentAll.populateList();
        if (transactionFragmentPaid != null)
            transactionFragmentPaid.populateList();
        if (transactionFragmentRecharged != null)
            transactionFragmentRecharged.populateList();
    }
}
