package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.post.ImageSelectorFragment;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.fragments.BaseFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class ServerProducts extends BaseActivity {
    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.select)
    FloatingActionButton select;

    private ProductsFragment mActivePage;

    @Bind(R.id.indicator)
    View indicator;

    public View getPageIndicator()
    {
        return indicator;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getPageTitle();
        setTitle("");

        int iconSize = 4;
        Drawable icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_check, R.color.white, iconSize);
        select.setIconDrawable(icon);
        select.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ProductsFragment.class);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
    }

    protected String getPageTitle() {
        return "Select from our collections";
    }

    @OnClick(R.id.select)
    void onPostNow() {

    }
/*
    @Subscribe
    public void onEvent(ServerFolderObject data)
    {
        processItem(data);
    }
    */

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
