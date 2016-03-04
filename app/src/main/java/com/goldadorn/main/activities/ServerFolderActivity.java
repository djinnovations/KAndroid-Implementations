package com.goldadorn.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.ImageSelectorFragment;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.fragments.BaseFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.rey.material.widget.ProgressView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class ServerFolderActivity extends BaseActivity{
    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.select)
    FloatingActionButton select;

    private ImageSelectorFragment mActivePage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = "Select from our collections";
        setTitle(title + "");

        int iconSize = 4;
        Drawable icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_check, R.color.white, iconSize);
        select.setIconDrawable(icon);
        select.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ImageSelectorFragment.class);
        mActivePage = (ImageSelectorFragment)BaseFragment.newInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();


        final View mainHolder = findViewById(R.id.mainHolder);
        final ProgressView progressBar = (ProgressView)findViewById(R.id.progressBar);


        mainHolder.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0f);
        progressBar.start();

        android.os.Handler handler = new android.os.Handler();
        final Runnable runnablelocal = new Runnable() {
            @Override
            public void run() {
                mainHolder.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        };
        handler.postDelayed(runnablelocal, 2000);
    }
    @OnClick(R.id.select)
    void onPostNow() {

    }

    @Subscribe
    public void onEvent(ServerFolderObject data)
    {
        processItem(data);
    }

    public void processItem(ServerFolderObject data) {

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
