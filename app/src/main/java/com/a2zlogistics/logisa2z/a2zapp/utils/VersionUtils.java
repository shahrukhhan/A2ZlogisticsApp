package com.a2zlogistics.logisa2z.a2zapp.utils;

/**
 * Created by Shahrukh Khan on 12/9/2017.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;

public final class VersionUtils {

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    public static boolean isAfter25() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    public static boolean isAfter24() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isAfter23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isAfter22() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static boolean isAfter21() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAfter20() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;
    }

    public static boolean isAfter19() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isAfter18() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isAfter17() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isAfter16() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
