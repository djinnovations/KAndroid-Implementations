package com.goldadorn.main.dj.support.fcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.goldadorn.main.activities.AppStartActivity;
import com.goldadorn.main.dj.support.gcm.Config;
import com.goldadorn.main.dj.support.gcm.NotificationUtils;
import com.goldadorn.main.dj.utils.DateTimeUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "djgcm";
    private NotificationUtils notificationUtils;


    @Override
    public void onMessageReceived(RemoteMessage messageMain) {

        String from = messageMain.getFrom();
        Map<String, String> databundle = messageMain.getData();

        String title = null;
        String message = null;
        //String image = null;
        String timestamp = null;
        String bannerImgUrl = null;
        String deepLinkData = null;
        for (String key : databundle.keySet()) {
            Log.d("djgcm", key + " is a key in the bundle");
        }

        if (databundle != null) {
            message = databundle.get("mp_message");
            bannerImgUrl = databundle.get("mp_icnm");
            deepLinkData = databundle.get("mp_cta");
            title = databundle.get("mp_title");
        } else {
            Log.d(TAG, "bundle is null");
        }
        timestamp = DateTimeUtils.getCurrentDateTime("hh:mm a");//dd/MM/yyyy hh:mm a
        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Title: " + title);
        Log.i(TAG, "message: " + message);
        Log.i(TAG, "ctaData: " + deepLinkData);
        Log.i(TAG, "timestamp: " + timestamp);

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            // app is in foreground, broadcast the push message
            Log.e(TAG, "App is in foreground");
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils();
            notificationUtils.playNotificationSound();
        } else {

            Log.e(TAG, "App is in background");
            Intent resultIntent = new Intent(getApplicationContext(), AppStartActivity.class);
            resultIntent.putExtra("message", message);

            if (TextUtils.isEmpty(bannerImgUrl)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            } else {
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, bannerImgUrl);
            }
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}