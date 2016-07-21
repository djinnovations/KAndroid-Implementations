package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.os.Bundle;

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

    public void logCustomEvent(Activity activity, String eventName, Bundle bundle){

        AppEventsLogger logger = AppEventsLogger.newLogger(activity);
        logger.logEvent(eventName, bundle);
    }


    public void logCustomEvent(Activity activity, String eventName, double valueToSum){

        AppEventsLogger logger = AppEventsLogger.newLogger(activity);
        logger.logEvent(eventName, valueToSum);
    }


    public void logCustomEvent(Activity activity, String eventName, double valueToSum, Bundle bundle){

        AppEventsLogger logger = AppEventsLogger.newLogger(activity);
        logger.logEvent(eventName, valueToSum, bundle);
    }


    //not be called by activities; only called by the application's onCreate
    public static void activateApp(Application baseApplication){

        AppEventsLogger.activateApp(baseApplication, Constants.CLIENT_ID_FB);
    }


}
