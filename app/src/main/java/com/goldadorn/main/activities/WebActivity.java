package com.goldadorn.main.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.webView.ApplicationDefaultWebView;
import com.kimeeo.library.fragments.BaseFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class WebActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String url = getIntent().getExtras().getString("URL");
        if(url==null)
            url = getIntent().getExtras().getString("url");

        String title = getIntent().getExtras().getString("TITLE");

        logAppropriateValue(title);

        if(title==null)
            title = url;

        setTitle(title + "");

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, url,ApplicationDefaultWebView.class);
        Map<String,String> params = new HashMap<>();
        params.put("URL",url);
        params.put("Title",title);
        navigationObject.setParam(params);

        BaseFragment mActivePage = BaseFragment.newWebViewInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
    }


    private void logAppropriateValue(String title){

        if (title.equalsIgnoreCase(ResourceReader.getInstance(getApplicationContext())
                .getStringFromResource(R.string.contactUs))){
            Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CONTACT_US");
            logEventsAnalytics(GAAnalyticsEventNames.CONTACT_US);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
