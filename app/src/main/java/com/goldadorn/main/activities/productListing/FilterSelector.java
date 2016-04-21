package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BhavinPadhiyar on 21/04/16.
 */
public class FilterSelector extends BaseActivity {
    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    @Bind(R.id.applyFilters)
    Button applyFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder_filter);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Apply Filter");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_white);
    }
    @OnClick(R.id.applyFilters)void applyFilters()
    {
        Intent intent = new Intent();
        intent.putExtra("type", "");
        setResult(Activity.RESULT_OK, intent);
        finish();
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
