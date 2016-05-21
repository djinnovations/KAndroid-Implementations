package com.goldadorn.main.dj.support;

import android.app.Activity;

import com.facebook.appevents.AppEventsLogger;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.dj.utils.Constants;


/**
 * Created by User on 21-05-2016.
 */
public class GAFacebookAnalytics {


    public void logCustomEvent(Activity activity, String eventName){

        AppEventsLogger logger = AppEventsLogger.newLogger(activity);
        logger.logEvent(eventName);
    }


    //not be called by activities; only called by the application's onCreate
    public static void activateApp(Application baseApplication){

        AppEventsLogger.activateApp(baseApplication, Constants.CLIENT_ID_FB);
    }



}
