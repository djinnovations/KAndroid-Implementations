package com.goldadorn.main.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.fragments.ContactUsFragment;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.modules.people.FindPeopleFragment;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.webView.ApplicationDefaultWebView;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.fragments.BaseFragment;
import com.rey.material.widget.ProgressView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebActivity extends BaseActivity {

    private static final String TAG_CONTACT_US = "goladorn.ContactUs";
    private static final String TAG_PEOPLE = "goladorn.People";
    @Bind(R.id.mainHolder)
    FrameLayout mainHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String url = getIntent().getExtras().getString("URL");
        if (url == null)
            url = getIntent().getExtras().getString("url");

        String title = getIntent().getExtras().getString("TITLE");

        logAppropriateValue(title);

        if (title == null)
            title = url;

        setTitle(title + "");

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(), title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, url, ApplicationDefaultWebView.class);
        Map<String, String> params = new HashMap<>();
        params.put("URL", url);
        params.put("Title", title);
        navigationObject.setParam(params);

        if (title.equalsIgnoreCase(ResourceReader.getInstance(getApplicationContext())
                .getStringFromResource(R.string.contactUs))) {
            fragmentManager.beginTransaction().replace(mainHolder.getId(), new ContactUsFragment(), TAG_CONTACT_US).commit();
        } else if (title.equalsIgnoreCase(ResourceReader.getInstance(getApplicationContext())
                .getStringFromResource(R.string.findPeople))) {
            fragmentManager.beginTransaction().replace(mainHolder.getId(), new FindPeopleFragment(), TAG_PEOPLE).commit();
        } else {
            BaseFragment mActivePage = BaseFragment.newWebViewInstance(navigationObject);
            fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
        }

    }


    public ExtendedAjaxCallback getAjaxCallBackCustom(int requestId) {
        return getAjaxCallback(requestId);
    }

    public AQuery getAQueryCustom() {
        return getAQuery();
    }

    private void logAppropriateValue(String title) {

        if (title.equalsIgnoreCase(ResourceReader.getInstance(getApplicationContext())
                .getStringFromResource(R.string.contactUs))) {
            Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CONTACT_US");
            logEventsAnalytics(GAAnalyticsEventNames.CONTACT_US);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private final int CONTACT_US_CALL = IDUtils.generateViewId();

    private ProgressView pgView;

    public void sendContactUsInfoToServer(Map<String, String> params, ProgressView progressBar) {
        pgView = progressBar;
        showOverLayCustom(null, R.color.colorPrimaryDark);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(CONTACT_US_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQueryCustom().ajax(ApiKeys.getContactUsAPI(), params, String.class, ajaxCallback);
    }

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djweb", "url queried- WebActivity: " + url);
        Log.d("djweb", "response- WebActivity: " + json);
        dismissCustom();
        if (id == CONTACT_US_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mainHolder, this);
            if (success) {
                if (json == null) {
                    showDialogInfo("Your message could not be delivered", false);
                    return;
                }
                try {
                    ((ContactUsFragment) getSupportFragmentManager().findFragmentByTag(TAG_CONTACT_US)).clearForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showDialogInfo("Your message has been sent", true);
                //Toast.makeText(getApplicationContext(), "Your message has been sent", Toast.LENGTH_SHORT).show();
            } else {
                showDialogInfo("Your message could not be delivered", false);
            }
            //Toast.makeText(getApplicationContext(), "Sending failed", Toast.LENGTH_SHORT).show();
        } else super.serverCallEnds(id, url, json, status);
    }


    private Dialog overLayDialog;

    private void showOverLayCustom(String text, int colorResId) {
        if (overLayDialog == null) {
            // WindowUtils.marginForProgressViewInGrid = 6;
            WindowUtils.justPlainOverLay = true;
            pgView.setVisibility(View.VISIBLE);
            overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlay(this, text, colorResId,
                    WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        }
        overLayDialog.show();
    }

    private void dismissCustom() {
        if (overLayDialog != null) {
            //WindowUtils.marginForProgressViewInGrid = 5;
            if (overLayDialog.isShowing()) {
                WindowUtils.justPlainOverLay = false;
                pgView.setVisibility(View.INVISIBLE);
                overLayDialog.dismiss();
            }
        }
    }


   /* private void showDialogInfo(String msg, boolean isPositive){
        int color ;
        color = isPositive ? R.color.colorPrimary : R.color.Red;
        WindowUtils.getInstance(getApplicationContext()).genericInfoMsgWithOK(this, null, msg, color);
    }*/
}
