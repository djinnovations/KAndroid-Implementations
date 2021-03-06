package com.goldadorn.main.dj.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.AppStartActivity;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.showcase.CollectionsActivity;
import com.goldadorn.main.activities.showcase.ProductActivity;
import com.goldadorn.main.activities.showcase.ShowcaseActivity;
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.model.Address;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.server.ApiFactory;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.IDUtils;
import com.kobakei.ratethisapp.RateThisApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 13-06-2016.
 */
public class RandomUtils {

    private static String INDIAN_CURRENCY_SYMBOL = "₹";

    public static String getIndianCurrencyFormat(double amount, boolean canStripTrailingZeros) {

        //Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        //String amountScissored = StringUtils.stripStart(String.valueOf(amount), "0");
        //String amtToReturn = format.format(new BigDecimal(/*amountScissored*/String.valueOf(amount)));
        if (amount < 0)
            return "NA";
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


    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static String getAddressTxt(Address address){
        StringBuilder sbr = new StringBuilder();
        sbr.append(address.name).append("\n");
        sbr.append(address.street).append(", ").append(address.city).append(", ").append(address.state)
                .append(". Pin:").append(address.pincode).append("\n").append("Ph: ").append(address.phoneNumber);
        return sbr.toString();
    }

    public static void exitFromAPP() {
        System.exit(0);
    }

    public static String getIndianCurrencyFormat(String amount, boolean canStripTrailingZeros) {

        try {
            return getIndianCurrencyFormat(Double.parseDouble(amount), true);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        } catch (Exception ex) {
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


    public static double getVat(String totalCost){
        double vat;
        try {
            double displayPrice = Double.parseDouble(totalCost);
            vat = (displayPrice - (displayPrice / 1.01));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            vat = 0.0;
        }
        return vat;
    }


    public static int getIdFromImageUrl(String url){
        String modified = url.substring( (url.lastIndexOf('/') + 1), url.indexOf("-1.jpg"));
        Log.d("djrand", "prodid -getIdFromImageUrl: "+modified);
        return Integer.parseInt(modified);
    }


    public static void set3LineEllipsizedText(String originalText, TextView textView) {
        /*int numOfLines = textView.getLineCount();
        if (numOfLines > 3){
            String text = textView.getText().toString().trim();
            int numOfChars = text.length();

        }*/
        try {
            String fullText = originalText.trim();
            int i = 1;
            textView.setText("");
            while (fullText.length() > 0) {
                int totalCharstoFit = textView.getPaint().breakText(fullText, 0, fullText.length(),
                        true, textView.getWidth(), null);
                if (textView.getWidth() == 0)
                    return;

                /*if (totalCharstoFit == 0)
                    textView.setText("");*/
                String subString = fullText.substring(0, totalCharstoFit);
                textView.append(subString);
                fullText = fullText.substring(subString.length(), fullText.length());
                i++;
                if (i > 3)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Map<String, String> addPlatformParams(Map<String, String> paramsMap) {
        paramsMap.put("platform", "Android");
        Log.d("djrand", "OS version: " + Constants.CURRENT_OS_VERSION);
        paramsMap.put("version", Constants.CURRENT_OS_VERSION);
        return paramsMap;
    }

    public static JSONObject getJSONFromString(String mapOfValues) {
        StringBuilder sb = new StringBuilder(mapOfValues);
        String jsontext = sb.insert(0, "{").insert(sb.length(), "}").toString();
        try {
            JSONObject jsonObject = new JSONObject(jsontext.replaceAll("=", ":"));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int delayToStartApp = 500;

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, AppStartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int mPendingIntentId = IDUtils.generateViewId();
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), mPendingIntentId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, (System.currentTimeMillis() + delayToStartApp), mPendingIntent);
        System.exit(0);
    }

    public static double getOfferPrice(double discountPercent, String totalPrice){

        try {
            double off = Double.parseDouble(totalPrice) * (discountPercent/100.0);
            return (Double.parseDouble(totalPrice) - off);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static final String TAG = "RandomUtils";
    public static ArrayList<String> getHashTagStrings(String fullText){
        if (TextUtils.isEmpty(fullText))
            return null;
        Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
        Matcher mat = MY_PATTERN.matcher(fullText);
        HashSet<String> strs=new HashSet<>();
        while (mat.find()) {
            Log.d(TAG , "HashTags: "+mat.group(1));
            strs.add(mat.group(1).toLowerCase());
        }
        return new ArrayList<>(strs);
    }

    public static void performAppRateTask() {
        Application.getInstance().getPrefManager().setAppRatingDone();
        Intent toPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Application.getInstance().getPackageName()));
        toPlayStore.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        toPlayStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Application.getInstance().startActivity(toPlayStore);
        RateThisApp.stopRateDialog(Application.getInstance());
    }

    public static void launchDesignerScreen(Activity activity, int userId) {
        Intent intent = new Intent(activity, ShowcaseActivity.class);
        intent.putExtra(IntentKeys.DESIGNER_ID, userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void launchCollectionScreen(Activity activity, int desId, int collId) {

        if (activity instanceof ProductActivity) {
            ((ProductActivity) activity).showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        }
        Collection colObj = getCollectionObjFromDb(collId);
        if (colObj != null) {
            proceedToCollectionScreen(activity, colObj);
        } else {
            /*Collection colObj2 = */queryServerForCollObj(activity, desId, collId);
            /*if (colObj2 == null) {
                showDialogInfo(activity, "Link couldn't be Established! Please visit our Showcase section", false);
                return;
            }
            proceedToCollectionScreen(activity, colObj2);*/
        }
    }

    private static void showDialogInfo(Activity activity, String msg, boolean isPositive) {
        int color;
        color = isPositive ? R.color.colorPrimary : R.color.Red;
        WindowUtils.getInstance(Application.getInstance()).genericInfoMsgWithOK(activity, null, msg, color);
    }

    private static void queryServerForCollObj(final Activity activity, final int desId, final int collId) {

        Runnable runnable = new Runnable() {
            public void run() {
                TimelineResponse response = new TimelineResponse();
                try {
                    Log.d("djrandom", "before server call");
                    response.idsForProducts.put(new JSONObject().put("desgnId", desId).put("collIds", new JSONArray().put(collId)));
                    ApiFactory.getDesigners(Application.getInstance(), response);
                    Log.d("djrandom", "after server call");
                    if (response.success && response.responseContent != null) {
                        DbHelper.writeProductShowcaseData(Application.getInstance(), response);
                        Collection colObj2 = getCollectionObjFromDb(collId);
                        if (colObj2 == null) {
                            showDialogInfo(activity, "Link couldn't be Established! Please visit our Showcase section", false);
                            return;
                        }
                        proceedToCollectionScreen(activity, colObj2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
        //return null;
    }

    private static String unreadCount = "0";

    public static void setUnreadCount(String unreadCount) {
        if (!TextUtils.isEmpty(unreadCount)) {
            if (unreadCount.length() > 1) {
                RandomUtils.unreadCount = "9+";
            } else RandomUtils.unreadCount = String.valueOf(unreadCount);
        }
    }

    public static String getUnreadCount() {
        return unreadCount;
    }


    public static Cursor getUserInfoCursor(int userId){
        String selection = Tables.Users._ID + "=?";
        String[] selArgs = new String[]{String.valueOf(userId)};
        Cursor cursor = Application.getInstance().getContentResolver()
                .query(Tables.Users.CONTENT_URI, null, selection, selArgs, null);
        return cursor;
    }

    private static Collection getCollectionObjFromDb(int collId) {
        String selection = Tables.Collections._ID + "=?";
        String[] selArgs = new String[]{String.valueOf(collId)};
        Cursor collectionCursor = Application.getInstance().getContentResolver()
                .query(Tables.Collections.CONTENT_URI, null, selection, selArgs, null);
        if (collectionCursor != null) {
            collectionCursor.moveToFirst();
            Log.d(Constants.TAG, "cursor count- zoomImages: " + collectionCursor.getCount());
            if (collectionCursor.getCount() != 0) {
                return/*Collection collection =*/ Collection.extractFromCursor(collectionCursor);
            }
            return null;
        }
        return null;
    }


    private static void proceedToCollectionScreen(Activity activity, Collection collection) {
        Intent intent = /*new Intent(activity, CollectionsActivity.class)*/CollectionsActivity.getLaunchIntent(activity, collection);// TODO: 07-07-2016
        intent.putExtra(IntentKeys.COLLECTION_ID, collection.id);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (activity instanceof ProductActivity) {
            ((ProductActivity) activity).dismissOverLay();
        }
        activity.startActivity(intent);
        //activity.finish();
    }
}
