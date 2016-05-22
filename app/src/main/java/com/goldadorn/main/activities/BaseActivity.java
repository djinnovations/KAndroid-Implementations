package com.goldadorn.main.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.facebook.appevents.AppEventsConstants;
import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.hitherejoe.tabby.CustomTabActivityHelper;
import com.hitherejoe.tabby.WebViewActivity;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.actions.OpenCharomeTab;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.model.BaseApplication;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 7/17/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void setupToolbar(){

    }

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
            Log.d(com.goldadorn.main.dj.utils.Constants.TAG_APP_EVENT, "AppEventLog: LOGOUT");
            logEventsAnalytics(GAAnalyticsEventNames.LOGOUT);
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
        showProgress(title, null);
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


    private void postLog(String paramName){

        Bundle bundle = new Bundle();
        bundle.putString(AppEventsConstants.EVENT_PARAM_LEVEL, paramName);
        Log.d(com.goldadorn.main.dj.utils.Constants.TAG_APP_EVENT, "AppEventLog: Nav_Slidemenu and itemSelected: "+paramName);
        logEventsAnalytics(GAAnalyticsEventNames.NAVIGATION_SLIDER_MENU, bundle);
    }


    @Subscribe
    public void onEvent(AppActions data) {
        action(data.navigationDataObject);
    }
    public boolean action(NavigationDataObject navigationDataObject) {
        Log.d("dj", "action - BaseActivity; navDataObj type: "+navigationDataObject.getActionType());
        Action action =new Action(this);
        if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_LOGOUT)) {
            postLog(GAAnalyticsEventNames.LOGOUT);
            logout();
            return true;
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY))
        {
            String url =(String) navigationDataObject.getActionValue();
            Log.d("dj", "action - BaseActivity : ACTION_TYPE_WEB_ACTIVITY - url: "+url);
            if(url!=null && url.equals("")==false)
            {
                Class target = navigationDataObject.getView();
                Log.d("dj", "menuAction: - target: "+target.toString());
                if(target==null)
                    target = WebActivity.class;
                Map<String, Object> data=new HashMap<>();
                data.put("URL",url);
                data.put("TITLE", navigationDataObject.getName());
                postLog(navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_CHROME))
        {
            String url =(String) navigationDataObject.getActionValue();
            Log.d("dj", "action - BaseActivity; ACTION_TYPE_WEB_CHROME- url: "+url);
            if(url!=null && url.equals("")==false)
            {
                Class target = navigationDataObject.getView();
                if(target==null)
                    target = WebActivity.class;
                Map<String, Object> data=new HashMap<>();
                data.put("URL",url);
                data.put("TITLE", navigationDataObject.getName());
                postLog(navigationDataObject.getName());
                //action.launchActivity(target, null, data, false);


                OpenCharomeTab action1 =new OpenCharomeTab(this);
                action1.setWebActivityClass(target);

                final Class targetWeb=target;
                CustomTabActivityHelper.CustomTabFallback customTabFallback = new CustomTabActivityHelper.CustomTabFallback()
                {
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(activity, targetWeb);
                        intent.putExtra(WebViewActivity.EXTRA_URL, uri.toString());
                        Log.d("dj", "action - BaseActivity; - CustomTabActivityHelper.CustomTabFallback-url: "+uri.toString());
                        intent.putExtra("URL", uri.toString());
                        activity.startActivity(intent);
                    }
                };
                action1.setCustomTabFallback(customTabFallback);
                action1.perform(url);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY))
        {

            Class target = navigationDataObject.getView();
            if(target!=null)
            {
                Map<String, Object> data=null;
                if(navigationDataObject.getParam()!=null)
                    data =(Map<String,Object>) navigationDataObject.getParam();
                if(data==null)
                    data = new HashMap<>();
                if(data.get("TITLE")==null)
                    data.put("TITLE", navigationDataObject.getName());
                postLog(navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                //overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE))
        {
            postLog("Share");
            Map<String,String> map =(Map<String,String>) navigationDataObject.getParam();
            action.textShare(map.get(Action.ATTRIBUTE_DATA),map.get(Action.ATTRIBUTE_TITLE));
            return true;
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_EXTERNAL))
        {
            postLog(navigationDataObject.getName());
            String url =(String) navigationDataObject.getActionValue();
            if(url!=null && url.equals("")==false)
            {
                action.openURL(url);
                return true;
            }
        }
        return false;
    }
    public void finish() {
        super.finish();
        //overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
    }


    protected void logEventsAnalytics(String eventName) {
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName);
    }

    protected void logEventsAnalytics(String eventName, Bundle bundle){
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName, bundle);
    }

    protected void logEventsAnalytics(String eventName, double valueToSum){
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName, valueToSum);
    }

    protected void logEventsAnalytics(String eventName, double valueToSum, Bundle bundle){
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName, valueToSum, bundle);
    }
}
