package com.example.shahrukhkhan.freelance.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shahrukhkhan.freelance.Database.LocalDB;
import com.example.shahrukhkhan.freelance.Dialogs.CustomDialogClass;
import com.example.shahrukhkhan.freelance.Dialogs.PasswordDialogClass;
import com.example.shahrukhkhan.freelance.Fragments.TransactionFragment;
import com.example.shahrukhkhan.freelance.LoginActivity;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.Utils.Constants;

public class TransactionActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TransactionFragment transactionFragmentAll;
    private TransactionFragment transactionFragmentPaid;
    private TransactionFragment transactionFragmentRecharged;
    private TextView cardUsageText;
    public String name, number, vehicle;
    public int activityType;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        number = intent.getStringExtra("number");
        vehicle = intent.getStringExtra("vehicle");
        activityType = intent.getIntExtra(Constants.ACTIVITY_ID, 0);
        cardUsageText = (TextView) findViewById(R.id.card_usage_text);
        init();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void init() {
        if (activityType == R.id.cards_usage_icon) {
            cardUsageText.setVisibility(View.VISIBLE);
            String text = name + " - " + number + " | " + vehicle;
            cardUsageText.setText(text);
        } else {
            cardUsageText.setVisibility(View.GONE);
        }
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
                    return "ALL";
                case 1:
                    return "PAID";
                case 2:
                    return "RECHARGED";
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
}
