package com.goldadorn.main.utils;

/**
 * @author Kiran BH
 */

import android.util.Log;

import com.goldadorn.main.constants.Constants;

public class L {

    public static void d(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.d("goldadorn " + tag, msg);
    }

    public static void d(String tag, String msg, int newLines) {
        if (Constants.isLogEnabled) {
            for (int i = 0; i < newLines; i++)
                Log.d("", ".            ");
            Log.d("goldadorn " + tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.i("goldadorn " + tag, msg);
    }

    public static void w(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.w("goldadorn " + tag, msg);
    }

    public static void e(String tag, String msg) {
        if (Constants.isLogEnabled)
            Log.e("goldadorn " + tag, msg);
    }

    public static void d(String msg) {
        if (Constants.isLogEnabled)
            Log.d("goldadorn", msg);
    }

    public static void i(String msg) {
        if (Constants.isLogEnabled)
            Log.i("goldadorn", msg);
    }
}