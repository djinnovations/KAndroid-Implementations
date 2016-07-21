package com.goldadorn.main.dj.support.gcm;

import android.util.Log;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.model.User;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 20-07-2016.
 */
public class MixPanelHelper {

    private final String TAG = "djgcm";

    private String gcmRefreshToken;
    private static MixPanelHelper ourInstance = new MixPanelHelper();
    private MixpanelAPI mMixpanel;

    public static MixPanelHelper getInstance() {
        return ourInstance;
    }

    private MixPanelHelper() {
        mMixpanel = Application.getInstance().getMixPanelInstance();
    }



    public void setGCMRefreshToken(String token){
        gcmRefreshToken = token;
    }

    public void logEventMixPanel(String eventName, JSONObject propertyParams){
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog - MixPanel: "+eventName);
        if (propertyParams == null)
            mMixpanel.track(eventName);
        else
            mMixpanel.track(eventName, propertyParams);
    }

    private final String value_property = "$android_devices";
    //SharedPreferences sharedPreferences;
    public void sendRefreshTokenToMixPanel(){
        Log.d(TAG, "sendRefreshTokenToServer called");

        /*sharedPreferences = Application.getInstance().getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
        boolean isLoginDone = sharedPreferences.getBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, false);
        int userId = -1;
        if (isLoginDone)
            userId = sharedPreferences.getInt(AppSharedPreferences.LoginInfo.USER_ID, -1);

        //User user = Application.getInstance().getUser();
        if(userId != -1){
            Log.d(TAG, "user id picked up from shared pref");
            MixpanelAPI.People people = Application.getInstance().getMixPanelInstance().getPeople();
            Application.getInstance().getMixPanelInstance().identify(String.valueOf(userId));
            people.identify(String.valueOf(userId));
            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray.put(0, gcmRefreshToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            people.union(value_property, jsonArray);
            Log.d(TAG, "refresh token sent to Mix panel");
        }
        else {*/
            User user = Application.getInstance().getUser();
            if (user != null) {
                Log.d(TAG, "user id picked up from application cache");
                MixpanelAPI.People people = mMixpanel.getPeople();
                mMixpanel.identify(String.valueOf(user.id));
                people.identify(String.valueOf(user.id));
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray.put(0, gcmRefreshToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                people.union(value_property, jsonArray);
                Log.d(TAG, "refresh token sent to Mix panel");
            }
        //}
    }


    public void flushDataToMixPanel(){
        mMixpanel.flush();
    }

}
