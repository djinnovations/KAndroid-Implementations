package com.goldadorn.main.dj.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.goldadorn.main.dj.utils.Constants;

public class MyPreferenceManager {
    // LogCat tag
    //private static Context _context;
    private SharedPreferences pref;
    private static MyPreferenceManager mCoachManager;

    private Editor editor;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    // All Shared Preferences Keys
    /*private static final String KEY_USER_ID_GCM = "user_id";
	private static final String KEY_USER_NAME_GCM = "user_name";
	private static final String KEY_USER_EMAIL_GCM = "user_email";
	private static final String KEY_NOTIFICATIONS_GCM = "notifications";*/

    private final String PREF_NAME = Constants.COACH_MARK_PREF_NAME;

    private final String KEY_IS_HOMESCREEN_DONE = "screen_home";
    private final String KEY_IS_TIMELINE_DONE = "screen_timeline";
    private final String KEY_IS_SHOWCASE_DONE = "screen_showcase";
    private final String KEY_IS_COLLECTION_DONE = "screen_collection";
    private final String KEY_IS_PRODUCTS_DONE = "screen_product";
    private static final String KEY_NOTIFICATIONS_GCM = "notifications";


    private MyPreferenceManager(Context appContext) {

        //MyPreferenceManager._context = context;
        pref = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public static MyPreferenceManager getInstance(Context appContext) {

        if (mCoachManager == null) {
            mCoachManager = new MyPreferenceManager(appContext);
        }

        return mCoachManager;
    }


    public void setHomeScreenTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_HOMESCREEN_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "home screen coach mark stat: " + isDone);
    }


    public boolean isHomeScreenTourDone() {

        boolean stat = pref.getBoolean(KEY_IS_HOMESCREEN_DONE, false);
        Log.d(Constants.TAG, "home screen coach mark stat - MyPreferenceManager: " + stat);
        return stat;
    }


    public void setTimeLineTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_TIMELINE_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "timeline coach mark stat: " + isDone);
    }


    public boolean isTimeLineTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_TIMELINE_DONE, false);
        Log.d(Constants.TAG, "timeline coach mark stat - MyPreferenceManager: " + stat);
        return stat;
    }


    public void setShowcaseTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_SHOWCASE_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "showcase coach mark stat: " + isDone);
    }


    public boolean isShowcaseTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_SHOWCASE_DONE, false);
        Log.d(Constants.TAG, "showcase coach mark stat - MyPreferenceManager: " + stat);
        return stat;
    }



    public void setCollectionTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_COLLECTION_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "collection coach mark stat: " + isDone);
    }


    public boolean isCollectionTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_COLLECTION_DONE, false);
        Log.d(Constants.TAG, "collection coach mark stat - MyPreferenceManager: " + stat);
        return stat;
    }


    public void setProductTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_PRODUCTS_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "product coach mark stat: " + isDone);
    }


    public boolean isProductTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_PRODUCTS_DONE, false);
        Log.d(Constants.TAG, "product coach mark stat - MyPreferenceManager: " + stat);
        return stat;
    }


    public void addNotification(String notification) {

        // get old notifications
        Log.d("dj", "add notification: ");
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS_GCM, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {

        Log.d("dj", "get notification: " + pref.getString(KEY_NOTIFICATIONS_GCM, null));
        return pref.getString(KEY_NOTIFICATIONS_GCM, null);
    }

    public void clearNotificationMsgs() {

        if(pref.contains(KEY_NOTIFICATIONS_GCM)) {
            editor.remove(KEY_NOTIFICATIONS_GCM);
            editor.commit();
        }
    }


}
