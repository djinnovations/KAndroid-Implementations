package com.goldadorn.main.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.model.LoginResult;
import com.goldadorn.main.model.User;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.gson.Gson;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.rey.material.widget.ProgressView;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

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
        initMixpanel();
    }

    private void initMixpanel() {
        String token = "7990b395d94157c30ca2d2e3a0e08a69";
        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(this, token);
        mixpanelAPI.getPeople().identify(String.valueOf(((Application) this.getApplicationContext()).getUser().id));
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
                    User user = new User(loginResult.getUserid(), User.TYPE_INDIVIDUAL);
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
}