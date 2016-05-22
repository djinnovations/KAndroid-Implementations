package com.goldadorn.main.dj.utils;

import android.os.Build;

import com.goldadorn.main.BuildConfig;

/**
 * Created by COMP on 5/4/2016.
 */
public class Constants {

    public static final String ENDPOINT_SOCIAL_LOGIN = BuildConfig.END_POINT_SOCIAL_LOGIN;

    public final static String TAG = "dj";
    public final static String TAG_APP_EVENT = "djAppEvent";

    public static final String PLATFORM_GOOGLE = "go";
    public static final String PLATFORM_FACEBOOK = "fb";
    public static final String PLATFORM_TWITTER = "tw";


    public static final String CLIENT_ID_FB = "406364016222124";
    public static final String CLIENT_SECRET_FB = "c5382eacbeeffa47d0703ca2441fe893";
    //// TODO: 5/6/2016  change the values; for testing using DJphy (Darshan) account
    public static final String API_KEY_TW = "XMIqXGDZWxxAtiuZh9ZN1CQJ4";
    public static final String API_SECRET_TW = "5ylrV8w5kETXKqbQDTygEgSVWslRD32usq0tJUxM91zFjMJP2u";

    //public static final String OAUTH_CLIENT_ID_GL = "1067734393749-ojd6omgjp0f9a797918vpnic2k8uhovp.apps.googleusercontent.com";

    public static final String OAUTH_WEBCLIENT_ID_GL = "1067734393749-0a02a40m8bib8f92o8o4n6i1fc5nng0t.apps.googleusercontent.com";

    public static final int CURRENT_API_LEVEL = Build.VERSION.SDK_INT;
    public static final int REQUEST_TIMEOUT_SOCIAL_LOGIN = 6000;

    public static final String ERR_MSG_1 = "Something went wrong";
    public static final String ERR_MSG_NETWORK = "network error";

    public static final String COACH_MARK_PREF_NAME = "gold_adorn_coach_mark";
}
