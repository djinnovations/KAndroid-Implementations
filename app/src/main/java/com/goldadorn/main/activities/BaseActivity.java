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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.facebook.appevents.AppEventsConstants;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.TemporaryCreatePostObj;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.utils.TemporarySocialPostParser;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.URLHelper;
import com.hitherejoe.tabby.CustomTabActivityHelper;
import com.hitherejoe.tabby.WebViewActivity;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.actions.OpenCharomeTab;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.model.BaseApplication;

import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 7/17/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void setupToolbar() {

    }

    public URLHelper getUrlHelper() {
        return getApp().getUrlHelper();
    }

    public Application getApp() {
        return ((Application) getApplication());
    }

    public BaseApplication getBaseApplication() {
        return ((BaseApplication) getApplication());
    }


    private View viewForSnackBar;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        viewForSnackBar = findViewById(android.R.id.content).getRootView();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {

        }

    }

    @Override
    public void onStop() {
        try {
            EventBus.getDefault().unregister(this);
        } catch (EventBusException e) {

        }
        super.onStop();
    }

    protected ExtendedAjaxCallback getAjaxCallback(final int id) {
        ExtendedAjaxCallback<Object> ajaxCallback = new ExtendedAjaxCallback<Object>() {
            public void callback(String url, Object json, AjaxStatus status) {
                serverCallEnds(id, url, json, status);
            }
        };
        List<Cookie> cookies = getApp().getCookies();
        if (cookies != null && cookies.size() != 0) {
            for (Cookie cookie : cookies) {
                ajaxCallback.cookie(cookie.getName(), cookie.getValue());
            }
        }
        return ajaxCallback;
    }


    protected void sendFcmToken() {
        String token = UserSession.getInstance().getFcmToken();
        if (token == null)
            return;
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        getAQuery().ajax(ApiKeys.getSendFcmTokenAAPI(), params, String.class, getAjaxCallback(SEND_FCM_TOKEN_CALL));
    }

    protected void gotoApp() {
        new Action(this).launchActivity(MainActivity.class, true);
    }

    private AQuery aQuery;

    protected AQuery getAQuery() {
        if (aQuery == null)
            aQuery = new AQuery(this);
        return aQuery;
    }


    final private int postCallToken = IDUtils.generateViewId();
    private int recentlyPostedPost = -1;
    TemporaryCreatePostObj tempPostObj;
    protected boolean uploadInProgress;

    private boolean isMainActivity;

    protected void setIsMainActivityBase(boolean isMain) {
        isMainActivity = isMain;
    }

    public static final int SEND_FCM_TOKEN_CALL = IDUtils.generateViewId();

    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {

        Log.d("djmain", "url queried- MainActivity: " + url);
        Log.d("djmain", "response- MainActivity: " + json);
        if (id == logoutCallToken) {
            Log.d(com.goldadorn.main.dj.utils.Constants.TAG_APP_EVENT, "AppEventLog: LOGOUT");
            logEventsAnalytics(GAAnalyticsEventNames.LOGOUT);
            SharedPreferences sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, false)
                    .putString(AppSharedPreferences.LoginInfo.USER_NAME, "")
                    .putString(AppSharedPreferences.LoginInfo.PASSWORD, "").commit();
            SocialLoginUtil.getInstance(getBaseApplication()).performFbLogout();
            SocialLoginUtil.getInstance(getBaseApplication()).performGoogleLogout();
            SocialLoginUtil.getInstance(getBaseApplication()).indicateSignedOut();
            stopProgress();
            new Action(this).launchActivity(LandingPageActivity.class, true);

        } else if (id == SEND_FCM_TOKEN_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, viewForSnackBar, this);
            if (success)
                Log.d("djfcm", "fcm refresh token successfully sent to server");
            else Log.d("djfcm", "fcm refresh token failed to be sent to server");
        } else if (id == postCallToken) {
            SocialFeedFragment socialFeedFragment = UserSession.getInstance().getSocialFeedFragment();
            uploadInProgress = false;
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, viewForSnackBar, this);
            if (success && socialFeedFragment != null) {
                int postId = -1;
                try {
                    postId = new JSONObject(json.toString()).getInt("postid");
                } catch (JSONException e) {
                    e.printStackTrace();
                    postId = -1;
                }
                /*if (recentlyPostedPost != -1 && recentlyPostedPost == com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST) {
                    if (isMainActivity) {
                        socialFeedFragment.postAdded(null, String.valueOf(postId));
                    }*//*else {
                        if (socialFeedFragment.getDataManagerCustom() != null)
                            socialFeedFragment.getDataManagerCustom().add(0, );
                    }*//*
                } else {*/
                    tempPostObj.setPostId(postId);
                    com.goldadorn.main.model.SocialPost socialPost = TemporarySocialPostParser.getSocialPostObj(tempPostObj);
                    if (isMainActivity) {
                        /*if (activePage instanceof HomePage) {
                            ((HomePage) activePage).socialFeedFragmentpage.postAdded(socialPost);
                        }*/
                        socialFeedFragment.postAdded(socialPost, String.valueOf(postId));
                    } else if (socialFeedFragment.getDataManagerCustom() != null) {
                        UserSession.getInstance().setIsBonbRefreshPending(true);
                        socialFeedFragment.getDataManagerCustom().add(0, socialPost);
                    }
                //}
                if (isMainActivity)
                    stopUploadProgress();

                Log.d(com.goldadorn.main.dj.utils.Constants.TAG_APP_EVENT, "AppEventLog: CREATE_POST_SUCCESS");
                logEventsAnalytics(GAAnalyticsEventNames.CREATE_POST_SUCCESS);
                Toast.makeText(this.getApplicationContext(), "Successfully Posted on Wall", Toast.LENGTH_SHORT).show();

            } else {
                if (isMainActivity)
                    stopUploadProgress();
            }

        } else if (id == POST_DELETE_CALL || id == POST_HIDE_CALL || id == POST_REPORT_CALL) {
            SocialFeedFragment socialFeedFragment = UserSession.getInstance().getSocialFeedFragment();
            if (position != -1) {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                        viewForSnackBar, this);
                if (success) {
                    if (id == POST_REPORT_CALL)
                        showDialogInfo("Thanks for notifying us! Our team will review this post and" +
                                " take corrective action. Meanwhile, we are hiding this post from your feed", true);
                    /*if (activePage instanceof HomePage) {
                        ((HomePage) activePage).socialFeedFragmentpage.updatePostList(position);
                    }*/
                    socialFeedFragment.updatePostList(position);
                }
            }
            position = -1;
        }

        if (isMainActivity)
            stopUploadProgress();
    }


    ProgressBar progressBar;

    protected void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    protected void startUploadProgress() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    protected void stopUploadProgress() {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    public void showDialogInfo(String msg, boolean isPositive) {
        int color;
        color = isPositive ? R.color.colorPrimary : R.color.Red;
        WindowUtils.getInstance(getApplicationContext()).genericInfoMsgWithOK(this, null, msg, color);
    }

    final private int logoutCallToken = IDUtils.generateViewId();

    ProgressDialog pd;

    protected void logout() {

        String url = getUrlHelper().getLogoutServiceURL();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(logoutCallToken);
        ajaxCallback.setClazz(String.class);
        getAQuery().ajax(url, String.class, ajaxCallback);

        showProgress("Logout from Application");

    }

    private void showProgress(String title) {
        showProgress(title, null);
    }

    protected void showProgress(String title, String msg) {
        stopProgress();
        pd = new ProgressDialog(this);
        pd.setTitle(title);
        if (msg != null)
            pd.setMessage(msg);
        pd.show();
    }

    protected void stopProgress() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }


    private void postLog(String paramName) {

        Bundle bundle = new Bundle();
        bundle.putString(AppEventsConstants.EVENT_PARAM_LEVEL, paramName);
        Log.d(com.goldadorn.main.dj.utils.Constants.TAG_APP_EVENT, "AppEventLog: Nav_Slidemenu and itemSelected: " + paramName);
        logEventsAnalytics(GAAnalyticsEventNames.NAVIGATION_SLIDER_MENU, bundle);
    }


    public final int POST_DELETE_CALL = IDUtils.generateViewId();
    public final int POST_HIDE_CALL = IDUtils.generateViewId();
    public final int POST_REPORT_CALL = IDUtils.generateViewId();
    private int position = -1;

    public void updatePostForThisUser(int what, String postId, int position) {
        if (isMainActivity)
            startUploadProgress();
        ExtendedAjaxCallback ajaxCallback = null;
        this.position = position;
        Map<String, String> params = new HashMap<>();
        params.put("postid", postId);
        if (what == POST_DELETE_CALL) {
            ajaxCallback = getAjaxCallback(POST_DELETE_CALL);
            getAQuery().ajax(ApiKeys.getDeletePostAPI(), params, String.class, ajaxCallback);
        } else if (what == POST_HIDE_CALL) {
            params.put("report", String.valueOf(0));
            ajaxCallback = getAjaxCallback(POST_HIDE_CALL);
            getAQuery().ajax(ApiKeys.getHidePostAPI(), params, String.class, ajaxCallback);
        } else if (what == POST_REPORT_CALL) {
            //// TODO: 25-06-2016
            params.put("report", String.valueOf(1));
            ajaxCallback = getAjaxCallback(POST_REPORT_CALL);
            getAQuery().ajax(ApiKeys.getHidePostAPI(), params, String.class, ajaxCallback);
        }
        Log.d("djmain", "req params - updatePostForThisUser: " + params);
        //ajaxCallback.method(AQuery.METHOD_POST);
    }


    final public static int POST_FEED = 1111;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("djpost", "onActResult");
        if (requestCode == POST_FEED && resultCode == Activity.RESULT_OK) {
            if (isMainActivity)
                startUploadProgress();
            try {
                //String fileData=data.getStringExtra("fileData");
                int type = recentlyPostedPost = data.getIntExtra("type", -1);
                if (type != -1) {
                    tempPostObj = new TemporaryCreatePostObj();
                    tempPostObj.setPostType(type);
                    String msg = data.getStringExtra("msg");
                    tempPostObj.setMsg(msg);
                    MultipartEntity reqEntity = new MultipartEntity();

                    //// TODO: 28-06-2016
                    //change string[] to list or json array.
                    /*if (data.getExtras().get("") != null) {
                        String[]  = (String[])data.getExtras().get("");
                    }*/
                    try {

                        if (data.getExtras().get("files") != null) {
                            File[] files = (File[]) data.getExtras().get("files");
                            tempPostObj.setFileList(files);
                            File file;
                            int count = 1;
                            for (int i = 0; i < files.length; i++) {
                                file = files[i];
                                if (file != null && file.exists() && file.canRead()) {
                                    reqEntity.addPart("file" + count, new FileBody(file));
                                    count++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (data.getExtras().get("filesURIs") != null) {
                            String[] uris = (String[]) data.getExtras().get("filesURIs");
                            tempPostObj.setFileUriList(uris);
                            File file;
                            int count = 1;
                            for (int i = 0; i < uris.length; i++) {
                                file = new File(uris[i]);
                                ;
                                if (file != null && file.exists() && file.canRead()) {
                                    reqEntity.addPart("file" + count, new FileBody(file));
                                    count++;
                                }
                            }
                        }
                    }

                    if (data.getExtras().get("links") != null) {
                        String[] links = (String[]) data.getExtras().get("links");
                        tempPostObj.setLinksList(links);
                        String link;
                        int count = 1;
                        for (int i = 0; i < links.length; i++) {
                            link = links[i];
                            if (link != null && link.equals("") == false) {
                                reqEntity.addPart("link" + count, new StringBody(link + ""));
                                count++;
                            }
                        }
                    }

                    /*if (data.getExtras().get("collIdList") != null){
                        int[] collIdArr = (int[]) data.getExtras().get("collIdList");
                        *//*JSONArray jsonArray = new JSONArray();
                        try {
                            for (int colId : collIdArr){
                                jsonArray.put(colId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jsonArray.length() != 0){
                            reqEntity.addPart("createpost_collids", new );
                        }*//*
                        int collId;
                        int count = 1;
                        for (int i = 0; i < collIdArr.length; i++){
                            collId = collIdArr[i];
                            if (collId != -1){
                                reqEntity.addPart("createpost_collid"+count, new StringBody(String.valueOf(collId)));
                                count++;
                            }
                        }
                    }

                    if (data.getExtras().get("desIdList") != null){
                        int[] desIdArr = (int[]) data.getExtras().get("desIdList");
                        *//*JSONArray jsonArray = new JSONArray();
                        try {
                            for (int colId : collIdArr){
                                jsonArray.put(colId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jsonArray.length() != 0){
                            reqEntity.addPart("createpost_collids", new );
                        }*//*
                        int desId;
                        int count = 1;
                        for (int i = 0; i < desIdArr.length; i++){
                            desId = desIdArr[i];
                            if (desId != -1){
                                reqEntity.addPart("createpost_desgnid"+count, new StringBody(String.valueOf(desId)));
                                count++;
                            }
                        }
                    }*/


                    if (data.getExtras().get("clubbed") != null) {
                        String[] clubbedArr = (String[]) data.getExtras().get("clubbed");
                        tempPostObj.setClubbedList(clubbedArr);
                        String clubbedTxt;
                        int count = 1;
                        for (int i = 0; i < clubbedArr.length; i++) {
                            clubbedTxt = clubbedArr[i];
                            if (clubbedTxt != null) {
                                reqEntity.addPart("p" + count, new StringBody(clubbedTxt));
                                count++;
                            }
                        }
                    }


                    if (msg != null && msg.equals("") == false)
                        reqEntity.addPart("createpost_message", new StringBody(msg));
                    reqEntity.addPart("createpost_type", new StringBody(type + ""));
                    //reqEntity.addPart("createpost_", new StringBody(type + ""));
                    Map<String, Object> params = new HashMap<>();
                    params.put(AQuery.POST_ENTITY, reqEntity);


                    String url = getUrlHelper().getCreatePostServiceURL();
                    ExtendedAjaxCallback ajaxCallback = getAjaxCallback(postCallToken);
                    ajaxCallback.setClazz(String.class);
                    ajaxCallback.setParams(params);
                    ajaxCallback.method(AQuery.METHOD_POST);
                    getAQuery().ajax(url, params, String.class, ajaxCallback);
                    uploadInProgress = true;
                }


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }
    }

    @Subscribe
    public void onEvent(AppActions data) {
        action(data.navigationDataObject);
    }

    public boolean action(NavigationDataObject navigationDataObject) {
        Log.d("dj", "action - BaseActivity; navDataObj type: " + navigationDataObject.getActionType());
        Action action = new Action(this);
        if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_LOGOUT)) {
            postLog(GAAnalyticsEventNames.LOGOUT);
            logout();
            return true;
        } else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY)) {
            String url = (String) navigationDataObject.getActionValue();
            Log.d("dj", "action - BaseActivity : ACTION_TYPE_WEB_ACTIVITY - url: " + url);
            if (url != null && url.equals("") == false) {
                Class target = navigationDataObject.getView();
                Log.d("dj", "menuAction: - target: " + target.toString());
                if (target == null)
                    target = WebActivity.class;
                Map<String, Object> data = new HashMap<>();
                data.put("URL", url);
                data.put("TITLE", navigationDataObject.getName());
                postLog(navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                return true;
            }

        } else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_CHROME)) {
            String url = (String) navigationDataObject.getActionValue();
            Log.d("dj", "action - BaseActivity; ACTION_TYPE_WEB_CHROME- url: " + url);
            if (url != null && url.equals("") == false) {
                Class target = navigationDataObject.getView();
                if (target == null)
                    target = WebActivity.class;
                Map<String, Object> data = new HashMap<>();
                data.put("URL", url);
                data.put("TITLE", navigationDataObject.getName());
                postLog(navigationDataObject.getName());
                //action.launchActivity(target, null, data, false);


                OpenCharomeTab action1 = new OpenCharomeTab(this);
                action1.setWebActivityClass(target);

                final Class targetWeb = target;
                CustomTabActivityHelper.CustomTabFallback customTabFallback = new CustomTabActivityHelper.CustomTabFallback() {
                    public void openUri(Activity activity, Uri uri) {
                        Intent intent = new Intent(activity, targetWeb);
                        intent.putExtra(WebViewActivity.EXTRA_URL, uri.toString());
                        Log.d("dj", "action - BaseActivity; - CustomTabActivityHelper.CustomTabFallback-url: " + uri.toString());
                        intent.putExtra("URL", uri.toString());
                        activity.startActivity(intent);
                    }
                };
                action1.setCustomTabFallback(customTabFallback);
                action1.perform(url);
                return true;
            }

        } else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY)) {

            Class target = navigationDataObject.getView();
            if (target != null) {
                Map<String, Object> data = null;
                if (navigationDataObject.getParam() != null)
                    data = (Map<String, Object>) navigationDataObject.getParam();
                if (data == null)
                    data = new HashMap<>();
                if (data.get("TITLE") == null)
                    data.put("TITLE", navigationDataObject.getName());
                postLog(navigationDataObject.getName());
                action.launchActivity(target, null, data, false);
                //overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
                return true;
            }

        } else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE)) {
            postLog("Share");
            Map<String, String> map = (Map<String, String>) navigationDataObject.getParam();
            action.textShare(map.get(Action.ATTRIBUTE_DATA), map.get(Action.ATTRIBUTE_TITLE));
            return true;
        } else if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_EXTERNAL)) {
            postLog(navigationDataObject.getName());
            String url = (String) navigationDataObject.getActionValue();
            if (url != null && url.equals("") == false) {
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

    protected void logEventsAnalytics(String eventName, Bundle bundle) {
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName, bundle);
    }

    protected void logEventsAnalytics(String eventName, double valueToSum) {
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName, valueToSum);
    }

    protected void logEventsAnalytics(String eventName, double valueToSum, Bundle bundle) {
        getApp().getFbAnalyticsInstance().logCustomEvent(this, eventName, valueToSum, bundle);
    }
}
