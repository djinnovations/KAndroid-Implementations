package com.goldadorn.main.dj.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.goldadorn.main.activities.AppStartActivity;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.utils.IDUtils;
import com.kobakei.ratethisapp.RateThisApp;

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


    public static void underLineTv(TextView textview, int start, int end) {
        try {
            String udata=/*"Underlined Text"*/ textview.getText().toString().trim();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), start, end, 0);
            textview.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
            textview.setText("");
        }
        //textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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

    public static void setPaddingLeftRight(TextView textView){
        DisplayProperties dispProp = Application.getInstance().getDisplayPropInstance();
        textView.setPadding((int) (5*dispProp.getXPixelsPerCell()), 0,
                (int) (5*dispProp.getXPixelsPerCell()), (int) (5*dispProp.getYPixelsPerCell()));
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


    public static void performAppRateTask(){
        Application.getInstance().getPrefManager().setAppRatingDone();
        Intent toPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Application.getInstance().getPackageName()));
        toPlayStore.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        toPlayStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Application.getInstance().startActivity(toPlayStore);
        RateThisApp.stopRateDialog(Application.getInstance());
    }
}
