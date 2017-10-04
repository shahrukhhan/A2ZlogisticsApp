package com.example.shahrukhkhan.freelance.Dialogs;

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

import com.example.shahrukhkhan.freelance.MainActivity;
import com.example.shahrukhkhan.freelance.R;
import com.example.shahrukhkhan.freelance.Utils.Constants;

/**
 * Created by Shahrukh Khan on 9/14/2017.
 */

public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity c;
    public AppCompatButton ok;
    RadioGroup radioGroup;
    RadioButton radioButton;

    public CustomDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        ok = findViewById(R.id.language_button);
        radioGroup = findViewById(R.id.radio_group);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.language_button) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            int id = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(id);
            editor.putString(Constants.LANGUAGE,radioButton.getText().toString());
            if(c.getComponentName().getShortClassName().equals(".LoginActivity")) {
                Intent intent = new Intent(c, MainActivity.class);
                c.startActivity(intent);
                dismiss();
                c.finish();
            } else {
                dismiss();
            }
        }
    }
}