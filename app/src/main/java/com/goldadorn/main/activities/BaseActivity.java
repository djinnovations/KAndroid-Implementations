package com.goldadorn.main.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.model.BaseApplication;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 7/17/15.
 */
public class BaseActivity extends AppCompatActivity {

    public URLHelper getUrlHelper() {
        return getApp().getUrlHelper();
    }

    public Application getApp() {
        return ((Application)getApplication());
    }

    public BaseApplication getBaseApplication() {
        return ((BaseApplication)getApplication());
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        }catch (EventBusException e)
        {

        }

    }

    @Override
    public void onStop() {
        try {
            EventBus.getDefault().unregister(this);
        }catch (EventBusException e)
        {

        }
        super.onStop();
    }

    protected ExtendedAjaxCallback getAjaxCallback(final int id) {
        ExtendedAjaxCallback<Object> ajaxCallback = new ExtendedAjaxCallback<Object>() {
            public void callback(String url, Object json, AjaxStatus status) {
                serverCallEnds(id,url,json,status);
            }
        };
        List<Cookie> cookies = getApp().getCookies();
        if(cookies!=null && cookies.size()!=0) {
            for (Cookie cookie : cookies) {
                ajaxCallback.cookie(cookie.getName(), cookie.getValue());
            }
        }
        return ajaxCallback;
    }


    protected void gotoApp() {
        new Action(this).launchActivity(MainActivity.class, true);
    }

    private AQuery aQuery;
    protected AQuery getAQuery() {
        if(aQuery==null)
            aQuery = new AQuery(this);
        return aQuery;
    }
    public void serverCallEnds(int id,String url, Object json, AjaxStatus status) {
        if (id == logoutCallToken) {
            SharedPreferences sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, false)
                    .putString(AppSharedPreferences.LoginInfo.USER_NAME, "")
                    .putString(AppSharedPreferences.LoginInfo.PASSWORD, "").commit();

            stopProgress();
            new Action(this).launchActivity(LandingPageActivity.class,true);
        }
    }
    final private int logoutCallToken= IDUtils.generateViewId();

    ProgressDialog pd;
    protected void logout() {

        String url = getUrlHelper().getLogoutServiceURL();
        ExtendedAjaxCallback ajaxCallback =getAjaxCallback(logoutCallToken);
        ajaxCallback.setClazz(String.class);
        getAQuery().ajax(url, String.class, ajaxCallback);

        showProgress("Logout from Application");

    }
    private void showProgress(String title) {
        showProgress(title,null);
    }
    protected void showProgress(String title,String msg) {
        stopProgress();
        pd = new ProgressDialog(this);
        pd.setTitle(title);
        if(msg!=null)
            pd.setMessage(msg);
        pd.show();
    }

    protected void stopProgress() {
        if(pd!=null)
        {
            pd.dismiss();
            pd=null;
        }
    }


}
