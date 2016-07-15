package com.goldadorn.main.dj.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;

public class DjphyPreferenceManager {
    // LogCat tag
    //private static Context _context;
    private SharedPreferences pref;
    private static DjphyPreferenceManager mCoachManager;

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
    private final String KEY_IS_SEARCH_DONE = "screen_create_search";
    private final String KEY_IS_NOTIFICATION_DONE = "screen_notification";
    private final String KEY_IS_PRODUCT_SHOWCASE_DONE = "screen_product_showcase";

    private static final String KEY_NOTIFICATIONS_GCM = "notifications";
    private final String KEY_IS_APP_RATING_DONE = "goldadorn.app_rating";
    private final String KEY_IS_INTIMATION_STOP = "intimation_stat";
    private final String KEY_INTIMATION_COUNT = "intimation_count";
    //private final String KEY_RATING_TRIGGER = "app_rate_trigger";
    private final String KEY_RATING_SESSION_COUNT = "app_rate_session_count";
    private final String KEY_RATING_IS_DAYS_COUNT_STARTED = "app_rate_is_day_count_started";
    private final String KEY_RATING_START_TIME = "app_rate_start_time";

    private DjphyPreferenceManager(Context appContext) {
        //DjphyPreferenceManager._context = context;
        pref = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public static DjphyPreferenceManager getInstance(Context appContext) {

        if (mCoachManager == null) {
            mCoachManager = new DjphyPreferenceManager(appContext);
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
        Log.d(Constants.TAG, "home screen coach mark stat - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setSearchTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_SEARCH_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "isSearchScreenTourDone : " + isDone);
    }


    public boolean isSearchScreenTourDone() {

        boolean stat = pref.getBoolean(KEY_IS_SEARCH_DONE, false);
        Log.d(Constants.TAG, "isSearchScreenTourDone - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setProductShowcaseTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_PRODUCT_SHOWCASE_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "isSearchScreenTourDone : " + isDone);
    }


    public boolean isProductShowcaseTourDone() {

        boolean stat = pref.getBoolean(KEY_IS_PRODUCT_SHOWCASE_DONE, false);
        Log.d(Constants.TAG, "isSearchScreenTourDone - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setNotificationTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_NOTIFICATION_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "setNotificationTourGuideStatus : " + isDone);
    }


    public boolean isNotificationTourDone() {

        boolean stat = pref.getBoolean(KEY_IS_NOTIFICATION_DONE, false);
        Log.d(Constants.TAG, "isNotificationTourDone - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setTimeLineTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_TIMELINE_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "timeline coach mark stat: " + isDone);
    }


    public boolean isTimeLineTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_TIMELINE_DONE, false);
        Log.d(Constants.TAG, "timeline coach mark stat - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setShowcaseTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_SHOWCASE_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "showcase coach mark stat: " + isDone);
    }


    public boolean isShowcaseTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_SHOWCASE_DONE, false);
        Log.d(Constants.TAG, "showcase coach mark stat - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setCollectionTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_COLLECTION_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "collection coach mark stat: " + isDone);
    }


    public boolean isCollectionTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_COLLECTION_DONE, false);
        Log.d(Constants.TAG, "collection coach mark stat - DjphyPreferenceManager: " + stat);
        return stat;
    }


    public void setProductTourGuideStatus(boolean isDone) {

        editor.putBoolean(KEY_IS_PRODUCTS_DONE, isDone);
        editor.commit();
        Log.d(Constants.TAG, "product coach mark stat: " + isDone);
    }


    public boolean isProductTourdone() {

        boolean stat = pref.getBoolean(KEY_IS_PRODUCTS_DONE, false);
        Log.d(Constants.TAG, "product coach mark stat - DjphyPreferenceManager: " + stat);
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

        if (pref.contains(KEY_NOTIFICATIONS_GCM)) {
            editor.remove(KEY_NOTIFICATIONS_GCM);
            editor.commit();
        }
    }

    public void setSocialLoginStat(boolean loginStat) {
        editor.putBoolean(AppSharedPreferences.LoginInfo.IS_SOCIAL_LOGIN, loginStat);
        editor.commit();
    }

    public boolean getSocialLoginStat() {
        Log.d("dj", "getSocialLoginStat - DjphyPreferenceManager: " +
                pref.getBoolean(AppSharedPreferences.LoginInfo.IS_SOCIAL_LOGIN, false));
        return pref.getBoolean(AppSharedPreferences.LoginInfo.IS_SOCIAL_LOGIN, false);
    }


    private final int INTIMATION_COUNT_LIMIT = 9;

    public void updateIntimationCount() {
        editor.putInt(KEY_INTIMATION_COUNT, (getIntimationCount() + 1));
        editor.commit();
        setIntimationStat();
    }

    public boolean getIsStopIntimation() {
        return pref.getBoolean(KEY_IS_INTIMATION_STOP, false);
    }

    private int getIntimationCount() {
        return pref.getInt(KEY_INTIMATION_COUNT, 0);
    }

    private void setIntimationStat() {
        if (getIntimationCount() == 0)
            return;
        if (getIntimationCount() > INTIMATION_COUNT_LIMIT) {
            editor.putBoolean(KEY_IS_INTIMATION_STOP, true);
        } else editor.putBoolean(KEY_IS_INTIMATION_STOP, false);
        editor.commit();
    }

    public void setAppRatingDone() {
        editor.putBoolean(KEY_IS_APP_RATING_DONE, true);
        editor.commit();
    }

    public boolean getIsAppRatingDone() {
        Log.d("dj", "getIsAppRatingDone- DjphyPreferenceManager: " + /*pref.getBoolean(KEY_IS_APP_RATING_DONE, false)*/"true");
        return /*pref.getBoolean(KEY_IS_APP_RATING_DONE, false)*/ true;
    }


    public boolean canStartForRatingApp() {
        if (getDaysDiffFromStarted() >= AppSharedPreferences.AppRater.DAYS_UNTIL_PROMPT
                || getSessionCount() >= AppSharedPreferences.AppRater.LAUNCHES_UNTIL_PROMPT) {
            if (getSessionCount() >= AppSharedPreferences.AppRater.LAUNCHES_UNTIL_PROMPT) {
                Log.d("dj", "canStartForRatingApp- DjphyPreferenceManager: true");
                return true;
            }
        }
        Log.d("dj", "canStartForRatingApp- DjphyPreferenceManager: false");
        return false;
    }

    /*private void setTriggerForRating() {
        editor.putBoolean(KEY_RATING_TRIGGER, true);
    }*/

    public void updateSessionCounts() {
        editor.putInt(KEY_RATING_SESSION_COUNT, getSessionCount() + 1);
        editor.commit();
    }

    public void resetAppRateSessionCount() {
        editor.putInt(KEY_RATING_SESSION_COUNT, 0);
        editor.commit();
    }

    private int getSessionCount() {
        return pref.getInt(KEY_RATING_SESSION_COUNT, 0);
    }

    public void startDayCountForRating() {
        if (getIsDaysCountStartedForRating())
            return;
        setDaysCountStartedForRating();
    }

    private int getDaysDiffFromStarted() {
        int difference = Math.abs((int) ((System.currentTimeMillis() / (24 * 60 * 60 * 1000))
                - (int) (getStartTimeForRating() / (24 * 60 * 60 * 1000))));
        Log.d("dj", "getDaysDiffFromStarted- DjphyPreferenceManager: " + difference);
        return difference;
    }

    private boolean getIsDaysCountStartedForRating() {
        return pref.getBoolean(KEY_RATING_IS_DAYS_COUNT_STARTED, false);
    }

    private void setDaysCountStartedForRating() {
        editor.putBoolean(KEY_RATING_IS_DAYS_COUNT_STARTED, true);
        editor.putLong(KEY_RATING_START_TIME, System.currentTimeMillis());
        editor.commit();
    }


    private long getStartTimeForRating() {
        return pref.getLong(KEY_RATING_START_TIME, System.currentTimeMillis());
    }
}
