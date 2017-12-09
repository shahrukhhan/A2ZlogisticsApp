package com.example.shahrukhkhan.freelance.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.shahrukhkhan.freelance.LoginActivity;
import com.example.shahrukhkhan.freelance.MainActivity;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.utils.Constants;

/**
 * Created by Shahrukh Khan on 9/14/2017.
 */

public class CustomDialogClass extends Dialog implements View.OnClickListener {

    private Activity c;
    private AppCompatButton ok;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SharedPreferences prefs;

    public CustomDialogClass(Activity a) {
        super(a);
        this.c = a;
        prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        ok = findViewById(R.id.language_button);
        radioGroup = findViewById(R.id.radio_group);
        ok.setOnClickListener(this);
        if (prefs.getString(Constants.LANGUAGE, "").equals("en")) {
            radioButton = findViewById(R.id.radio_button1);
            radioButton.setChecked(true);
        } else {
            radioButton = findViewById(R.id.radio_button2);
            radioButton.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        ok.setEnabled(false);
        if (view.getId() == R.id.language_button) {
            SharedPreferences.Editor editor = prefs.edit();
            int id = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(id);
            if (radioButton.getText().toString().substring(0, 1).equals(prefs.getString(Constants.LANGUAGE, ""))) {
                if (c.getComponentName().getShortClassName().equals(".LoginActivity")) {
                    Intent intent = new Intent(c, MainActivity.class);
                    c.startActivity(intent);
                    dismiss();
                    c.finish();
                } else {
                    dismiss();
                }
            } else {
                if (radioButton.getText().toString().equals("English"))
                    editor.putString(Constants.LANGUAGE, "en");
                else {
                    editor.putString(Constants.LANGUAGE, "hi");
                }
                editor.apply();
                if (c.getComponentName().getShortClassName().equals(".LoginActivity")) {
                    Intent intent = new Intent(c, MainActivity.class);
                    c.startActivity(intent);
                    dismiss();
                    c.finish();
                } else {
                    Intent intent = new Intent(c, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    c.startActivity(intent);
                    dismiss();
                }
            }
        }
    }
}