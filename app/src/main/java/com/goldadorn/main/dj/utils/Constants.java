package com.goldadorn.main.dj.utils;

import android.os.Build;

import com.goldadorn.main.BuildConfig;
import com.goldadorn.main.utils.URLHelper;

/**
 * Created by COMP on 5/4/2016.
 */
public class Constants {

    public static final String ENDPOINT_SOCIAL_LOGIN = BuildConfig.END_POINT_SOCIAL_LOGIN;
    public static final String ENDPOINT_PRODUCT_BASIC_INFO = BuildConfig.END_POINT_COMERCIAL +
            URLHelper.VERB.GET_BASIC_PRODUCT + "/";


    public static final int REQUEST_READ_EXTERNAL_STORAGE = 2901;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2902;
    public static final int REQUEST_CAMERA = 2903;
    public static final int REQUEST_SMS_READ = 2904;
    public static final int REQUEST_SMS_RECEIVE = 2905;

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
    public static final String SERVER_KEY_GL = "AIzaSyBS7zC9tJFmYzF8HmK-aQNMgV06mO09kLI";

    public static final String MIX_PANEL_PROJECT_TOKEN = "7990b395d94157c30ca2d2e3a0e08a69";
    public static final String SENDER_ID_PROJECT_NUMBER = "1067734393749";


    public static final int CURRENT_API_LEVEL = Build.VERSION.SDK_INT;
    public static final int REQUEST_TIMEOUT_SOCIAL_LOGIN = 6000;

    public static final String ERR_MSG_1 = "Something went wrong";
    public static final String ERR_MSG_NETWORK = "network error";

    public static final String INFO_MSG_PERM_DENIED = "Operation terminated!, cannot continue without the permission";
    public static final String INFO_MSG_READ_GALLERY = "Please grant permission to access your gallery to pick images to post";
    public static final String INFO_MSG_CAMERA = "Please grant permission to access your camera to click a pic to post";
    public static final String INFO_MSG_WRITE = "Picture captured for uploading will be stored into the device";


    public static final String COACH_MARK_PREF_NAME = "gold_adorn_coach_mark";
    public static final String GCM_CALL = "GCM_APP_STARTED";


}
