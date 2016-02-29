package com.goldadorn.main.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

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
import com.rey.material.widget.ProgressView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class UnderDevelopmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.under_development_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getExtras().getString("TITLE");

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