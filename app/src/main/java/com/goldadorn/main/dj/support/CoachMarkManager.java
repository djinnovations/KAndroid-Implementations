package com.goldadorn.main.dj.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.goldadorn.main.dj.utils.Constants;

public class CoachMarkManager {
    // LogCat tag
    //private static Context _context;
    private SharedPreferences pref;
    private static CoachMarkManager mCoachManager;

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

    private final String KEY_IS_SCREEN1_DONE = "screen_one";

    private CoachMarkManager(Context appContext) {

        //CoachMarkManager._context = context;
        pref = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public static CoachMarkManager getInstance(Context appContext) {

        if (mCoachManager == null) {
            mCoachManager = new CoachMarkManager(appContext);
        }

        return mCoachManager;
    }


    public void setScreen1Status(boolean isDone) {

        editor.putBoolean(KEY_IS_SCREEN1_DONE, isDone);
        editor.commit();

        Log.d(Constants.TAG, "screen 1 coach mark stat: " + isDone);
    }


    public boolean getScreen1Status() {

        boolean stat = pref.getBoolean(KEY_IS_SCREEN1_DONE, false);
        Log.d(Constants.TAG, "screen 1 coach mark stat - CoachMarkManager: " + stat);
        return stat;
    }

}
