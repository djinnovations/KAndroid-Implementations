package com.goldadorn.main.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;

public class UnderDevelopmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.under_development_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getExtras().getString("TITLE");
        if (title.equalsIgnoreCase("Search")){
            logEventsAnalytics(GAAnalyticsEventNames.SEARCH);
            Log.d(Constants.TAG_APP_EVENT, "AppEventLog: SEARCH");
        }
        if(title!=null)
        {
            setTitle(title + "");
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