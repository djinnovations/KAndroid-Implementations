package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.imagePicker.PickServerProducts;
import com.goldadorn.main.activities.post.ImageSelectorFragment;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.TypefaceHelper;
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

    public static final int FILTER_APPLY = 2;

    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    private ProductsFragment mActivePage;

    @Bind(R.id.applyFilters)
    Button applyFilters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder2);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Selected Product");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_white);


        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),"", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ProductsFragment.class);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
        TypefaceHelper.setFont(applyFilters);
    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
    @OnClick(R.id.applyFilters)void applyFilters()
    {
        Intent intent = new Intent(this, FilterSelector.class);
        startActivityForResult(intent, FILTER_APPLY);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==FILTER_APPLY && resultCode== Activity.RESULT_OK)
        {
            System.out.println(data);
        }
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
