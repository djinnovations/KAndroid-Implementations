package com.goldadorn.main.dj.utils;

import android.os.Build;

import com.goldadorn.main.BuildConfig;

/**
 * Created by COMP on 5/4/2016.
 */
public class Constants {

    public static final String ENDPOINT_SOCIAL_LOGIN = BuildConfig.END_POINT_SOCIAL_LOGIN;

    public static final String PLATFORM_GOOGLE = "gl";
    public static final String PLATFORM_FACEBOOK = "fb";
    public static final String PLATFORM_TWITTER = "tw";
    public final static String TAG = "dj";


    public static final String CLIENT_ID_FB = "406364016222124";
    public static final String CLIENT_SECRET_FB = "c5382eacbeeffa47d0703ca2441fe893";
    //// TODO: 5/6/2016  change the values; for testing using DJphy (Darshan) account
    public static final String API_KEY_TW = "XMIqXGDZWxxAtiuZh9ZN1CQJ4";
    public static final String API_SECRET_TW = "5ylrV8w5kETXKqbQDTygEgSVWslRD32usq0tJUxM91zFjMJP2u";

    public static final int CURRENT_API_LEVEL = Build.VERSION.SDK_INT;
    public static final int REQUEST_TIMEOUT_SOCIAL_LOGIN = 6000;

    public static final String ERR_MSG_1 = "Something went wrong";
    public static final String ERR_MSG_NETWORK = "network error";

}
