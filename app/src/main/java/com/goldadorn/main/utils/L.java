package com.goldadorn.main.utils;

/**
 * @author Kiran BH
 */

import android.util.Log;

import com.goldadorn.main.constants.Constants;

public class L {

    public static void d(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.d("lookup " + tag, msg);
    }

    public static void d(String tag, String msg, int newLines) {
        if (Constants.isLogEnabled) {
            for (int i = 0; i < newLines; i++)
                Log.d("", ".            ");
            Log.d("lookup " + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.i("lookup " + tag, msg);
    }

    public static void w(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.w("lookup " + tag, msg);
    }

    public static void e(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.e("lookup " + tag, msg);
    }

    public static void d(String msg) {
        if (Constants.isLogEnabled)
            Log.d("lookup", msg);
    }

    public static void i(String msg) {
        if (Constants.isLogEnabled)
            Log.i("lookup", msg);
    }
}