package com.example.shahrukhkhan.freelance.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Shahrukh Khan on 9/26/2017.
 */

public class Util {

    public static void snackBarOnUIThread(final String message, final Activity activity, final String textColorCode) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.RED);

                if (textColorCode != null) {
                    View snackBarView = snackbar.getView();
                    TextView textView =
                            snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    textView.setTextColor(Color.parseColor(textColorCode));
                }

                snackbar.show();

            }
        });
    }
}
