package com.goldadorn.main.dj.support.gcm;

public class GcmIntentService /*extends IntentService*/ {
 
    /*private static final String TAG = "djgcm";
 
    public GcmIntentService() {
        super(TAG);
    }
 
    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";
 
 
    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(KEY);
        switch (key) {
            case SUBSCRIBE:
                // subscribe to a topic
                String topic = intent.getStringExtra(TOPIC);
                subscribeToTopic(topic);
                break;
            case UNSUBSCRIBE:
                String topic1 = intent.getStringExtra(TOPIC);
                unsubscribeFromTopic(topic1);
                break;
            default:
                // if key is not specified, register with GCM
                registerGCM();
        }
 
    }
 
    *//**
     * Registering with GCM and obtaining the gcm registration id
     *//*
    private void registerGCM() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;
 
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM SenderID: " + getString(R.string.gcm_defaultSenderId));
            Log.i(TAG, "GCM Registration Token: " + token);
 
            // sending the registration id to our server
            //sendRefreshTokenToServer(token);
            MixPanelHelper.getInstance().setGCMRefreshToken(token);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
 
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
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
 
    *//**
     * Subscribe to a topic
     *//*
    public void subscribeToTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.subscribe(token, "/topics/" + topic, null);
                Log.i(TAG, "Subscribed to topic: " + topic);
            } else {
                Log.i(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
 
    public void unsubscribeFromTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.unsubscribe(token, "");
                Log.i(TAG, "Unsubscribed from topic: " + topic);
            } else {
                Log.i(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/
}