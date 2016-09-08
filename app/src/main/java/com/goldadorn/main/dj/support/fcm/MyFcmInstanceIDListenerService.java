package com.goldadorn.main.dj.support.fcm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.support.gcm.GcmIntentService;
import com.goldadorn.main.model.User;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;

public class MyFcmInstanceIDListenerService extends FirebaseInstanceIdService {
 
    private static final String TAG = "djgcm";
 
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.

        UserSession.getInstance().setFcmToken(refreshedToken);
        //sendRefreshTokenToServer(refreshedToken);


        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        /*Intent intent = new Intent(this, GcmIntentService.class);
        startService(intent);*/
    }


    private final String value_property = "$android_devices";
    SharedPreferences sharedPreferences;
    private void sendRefreshTokenToServer(final String token) {
        // Send the registration token to Mix panel
        Log.d(TAG, "sendRefreshTokenToServer called");

        sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
        boolean isLoginDone = sharedPreferences.getBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, false);
        int userId = -1;
        if (isLoginDone)
            userId = sharedPreferences.getInt(AppSharedPreferences.LoginInfo.USER_ID, -1);

        //User user = Application.getInstance().getUser();
        if(userId != -1){
            Log.d(TAG, "user id picked up from shared pref");
            MixpanelAPI.People people = Application.getInstance().getMixPanelInstance().getPeople();
            people.identify(String.valueOf(userId));
            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray.put(0, token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            people.union(value_property, jsonArray);
            Log.d(TAG, "refresh token sent to Mix panel");
        }
        else {
            User user = Application.getInstance().getUser();
            if (user != null) {
                Log.d(TAG, "user id picked up from application cache");
                MixpanelAPI.People people = Application.getInstance().getMixPanelInstance().getPeople();
                people.identify(String.valueOf(user.id));
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray.put(0, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                people.union(value_property, jsonArray);
                Log.d(TAG, "refresh token sent to Mix panel");
            }
        }
        //// TODO: 30-05-2016
        // people.setPushRegistrationId(registrationId);
    }
}