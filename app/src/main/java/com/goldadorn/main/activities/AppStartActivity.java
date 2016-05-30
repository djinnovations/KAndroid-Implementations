package com.goldadorn.main.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.facebook.FacebookSdk;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.support.gcm.Config;
import com.goldadorn.main.dj.support.gcm.GcmIntentService;
import com.goldadorn.main.model.LoginResult;
import com.goldadorn.main.model.User;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.rey.material.widget.ProgressView;

import org.apache.http.cookie.Cookie;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class AppStartActivity extends BaseActivity {

    private static final int INTERNET_PERMISSION_REQUEST_CODE = 1;
    private SharedPreferences sharedPreferences;

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        /*
        SVGImageView logo = (SVGImageView)findViewById(R.id.logo);
        logo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        int res = R.raw.logo_2;
        logo.setImageResource(res);
        */
        setUpGCM();


        ProgressView progressBar = (ProgressView) findViewById(R.id.progressBar);
        progressBar.setProgress(0f);
        progressBar.start();


        if (checkPermission()) {
            kickStart();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET))
                Toast.makeText(AppStartActivity.this, "This app must have internet permision.", Toast.LENGTH_SHORT).show();
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, INTERNET_PERMISSION_REQUEST_CODE);
        }
        //initFacebookSdk();
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private void setUpGCM() {

        Application.getInstance().getPrefManager().clearNotificationMsgs();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();
                    Log.d("dj", "GCM reg Token: " + token);
                    Log.d("dj","GCM reg Token: "+token);

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("djGcm", "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }




    private void initFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    /*private void initMixpanel() {

        if (((Application) this.getApplicationContext()).getUser() != null && ((Application) this.getApplicationContext()).getUser().id > 0) {
            String token = "7990b395d94157c30ca2d2e3a0e08a69";
            MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(this, token);
            mixpanelAPI.getPeople().identify(String.valueOf(((Application) this.getApplicationContext()).getUser().id));
        }
    }*/


    @Override
    public void onStart() {
        super.onStart();
        checkbranchio();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkbranchio();
    }

    private void checkbranchio() {
        Branch branch = Branch.getInstance(getApplicationContext());

        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                } else {
                    Log.i("MyApp", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }


    private void kickStart() {
        sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
        boolean isIntroSeen = sharedPreferences.getBoolean(AppSharedPreferences.LoginInfo.IS_INTRO_SEEN, false);
        isIntroSeen = true;
        if (isIntroSeen) {
            boolean isLoginDone = sharedPreferences.getBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, false);

            String userName = sharedPreferences.getString(AppSharedPreferences.LoginInfo.USER_NAME, "");
            String password = sharedPreferences.getString(AppSharedPreferences.LoginInfo.PASSWORD, "");

            if (isLoginDone && userName.equals("") == false && password.equals("") == false)
                login(userName, password);
            else
                gotoLandingPage();
        } else
            gotoIntro();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INTERNET_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    kickStart();
                } else {
                    finish();
                }

                break;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    private void gotoIntro() {
        new Action(this).launchActivity(Intro.class, true);
    }


    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        if (id == loginServiceCall) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            List<Cookie> cookies = status.getCookies();
            if (success) {
                Gson gson = new Gson();
                LoginResult loginResult = gson.fromJson((String) json, LoginResult.class);

                if (loginResult.getSuccess()) {
                    User user = new User(Integer.valueOf(loginResult.getUserid()), User.TYPE_INDIVIDUAL);
                    user.setName(loginResult.getUsername());
                    user.setImageUrl(loginResult.getUserpic());
                    getApp().setUser(user);
                    getApp().setCookies(cookies);

                    gotoApp();
                } else {
                    final Snackbar snackbar = Snackbar.make(layoutParent, loginResult.getMsg(), Snackbar.LENGTH_SHORT);
                    ColoredSnackbar.alert(snackbar).show();
                }
            } else {
                gotoLandingPage();
            }
        } else
            super.serverCallEnds(id, url, json, status);
    }

    final private int loginServiceCall = IDUtils.generateViewId();

    protected void login(String userName, String password) {
        String url = getUrlHelper().getLoginServiceURL();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(loginServiceCall);
        Map<String, String> params = new HashMap<>();
        params.put(URLHelper.LOGIN_PARAM.USER_NAME, userName);
        params.put(URLHelper.LOGIN_PARAM.PASSWORD, password);

        ajaxCallback.setParams(params);
        ajaxCallback.setClazz(String.class);
        getAQuery().ajax(url, params, String.class, ajaxCallback);


    }

    private void gotoLandingPage() {
        new Action(this).launchActivity(LandingPageActivity.class, true);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}