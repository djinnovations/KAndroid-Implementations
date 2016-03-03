package com.goldadorn.main.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.fragments.BaseFragment;
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
    @Subscribe
    public void onEvent(AppActions data) {
        action(data.navigationDataObject);
    }
    public boolean action(NavigationDataObject navigationDataObject) {
        Action action =new Action(this);
        if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_LOGOUT)) {
            logout();
            return true;
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY))
        {
            String url =(String) navigationDataObject.getActionValue();
            if(url!=null && url.equals("")==false)
            {
                Class target = navigationDataObject.getView();
                if(target==null)
                    target = WebActivity.class;
                Map<String, Object> data=new HashMap<>();
                data.put("URL",url);
                data.put("TITLE", navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_CHROME))
        {
            String url =(String) navigationDataObject.getActionValue();
            if(url!=null && url.equals("")==false)
            {
                Class target = navigationDataObject.getView();
                if(target==null)
                    target = WebActivity.class;
                Map<String, Object> data=new HashMap<>();
                data.put("URL",url);
                data.put("TITLE", navigationDataObject.getName());
                //action.launchActivity(target, null, data, false);
                action.openChromeTab(url);
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
                action.launchActivity(target, null, data, false);
                //overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
                return true;
            }

        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE))
        {

            Map<String,String> map =(Map<String,String>) navigationDataObject.getParam();
            action.textShare(map.get(Action.ATTRIBUTE_DATA),map.get(Action.ATTRIBUTE_TITLE));
            return true;
        }
        else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_EXTERNAL))
        {
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
}
