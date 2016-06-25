package com.goldadorn.main.dj.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.widget.TextView;

import com.goldadorn.main.activities.AppStartActivity;
import com.goldadorn.main.utils.IDUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.Format;
import java.util.Locale;

/**
 * Created by User on 13-06-2016.
 */
public class RandomUtils {

    private static String INDIAN_CURRENCY_SYMBOL = "₹";

    public static String getIndianCurrencyFormat(double amount, boolean canStripTrailingZeros) {

        //Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        //String amountScissored = StringUtils.stripStart(String.valueOf(amount), "0");
        //String amtToReturn = format.format(new BigDecimal(/*amountScissored*/String.valueOf(amount)));
        try {
            String amtToReturn = rupeeFormat(String.valueOf(Math.round(amount)));
            return amtToReturn;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        /*if (!canStripTrailingZeros)
            return amtToReturn;
        if (amtToReturn.contains(".")){
            String amtArr = amtToReturn.substring(0, (amtToReturn.length() - 3));
            return amtArr;
        }else return amtToReturn;*/
    }

    public static String getIndianCurrencyFormat(String amount, boolean canStripTrailingZeros) {

        try {
            return getIndianCurrencyFormat(Double.parseDouble(amount), true);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        } catch (Exception ex){
            return "";
        }
    }


    private static String rupeeFormat(String value) {
        value = value.replace(",", "");
        char lastDigit = value.charAt(value.length() - 1);
        String result = "";
        int len = value.length() - 1;
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--) {
            result = value.charAt(i) + result;
            nDigits++;
            if (((nDigits % 2) == 0) && (i > 0)) {
                result = "," + result;
            }
        }
        String indianFormat = result + lastDigit;
        return INDIAN_CURRENCY_SYMBOL + " " + indianFormat;
    }


    public static void underLineTv(TextView textview) {
        textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    public static JSONObject getJSONFromString(String mapOfValues){
        StringBuilder sb = new StringBuilder(mapOfValues);
        String jsontext = sb.insert(0, "{").insert(sb.length(), "}").toString();
        try {
            JSONObject jsonObject = new JSONObject(jsontext.replaceAll("=",":"));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int delayToStartApp = 1000;
    public static void restartApp(Activity activity){
        Intent intent = new Intent(activity, AppStartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int mPendingIntentId = IDUtils.generateViewId();
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), mPendingIntentId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, (System.currentTimeMillis() + delayToStartApp), mPendingIntent);
        System.exit(0);
    }
}
