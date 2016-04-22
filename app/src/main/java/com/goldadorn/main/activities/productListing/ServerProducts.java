package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
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
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class ServerProducts extends BaseActivity {

    public static final int FILTER_APPLY = 2;
    private SelectorHelper selectorHelper;

    public ViewGroup getLayoutParent() {
        return layoutParent;
    }

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    private ProductsFragment mActivePage;

    @Bind(R.id.applyFilters)
    Button applyFilters;

    @Subscribe
    public void onEvent(FilterProductListing data)
    {
        processItem(data);
    }
    public void processItem(FilterProductListing data) {

    }
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

        refreshView(filters);

        RecyclerView selectorRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        selectorHelper= new SelectorHelper(this,selectorRecyclerView);
        selectorHelper.onRemoveListner(onRemoveListner);
        TypefaceHelper.setFont(applyFilters);
    }
    SelectorHelper.OnRemoveListner onRemoveListner = new SelectorHelper.OnRemoveListner()
    {
        @Override
        public void remove(Object o) {
            System.out.println(o);
            for (Parcelable filter : filters) {
                if(o instanceof Parcelable && (Parcelable)o==filter)
                {
                    filters.remove((Parcelable)o);
                    refreshView(filters);
                    break;
                }
            }
        }
    };
    private void refreshView(ArrayList<Parcelable> filters) {
        if(filters!=null)
        {
            this.filters=filters;
            selectorHelper.removeAll();
            selectorHelper.addAll(filters);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),"", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ProductsFragment.class);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        mActivePage.setFilters(filters);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
    @OnClick(R.id.applyFilters)void applyFilters()
    {
        Intent intent = new Intent(this, FilterSelector.class);
        if(filters!=null)
            intent.putParcelableArrayListExtra("filters",filters);
        startActivityForResult(intent, FILTER_APPLY);
    }
    ArrayList<Parcelable> filters;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==FILTER_APPLY && resultCode== Activity.RESULT_OK)
        {
            final ArrayList<Parcelable> filters = data.getParcelableArrayListExtra("filters");
            Handler h = new Handler();
            Runnable r= new Runnable() {
                @Override
                public void run() {
                    refreshView(filters);
                }
            };
            h.postDelayed(r,1000);

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
