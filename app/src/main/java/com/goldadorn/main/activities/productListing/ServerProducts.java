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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.nineoldandroids.animation.Animator;

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


    @Bind(R.id.sortBestSelling)
    Button sortBestSelling;
    @OnClick(R.id.sortBestSelling)
    void onSortBestSelling(){updateSort(sortBestSelling);}

    @Bind(R.id.sortPrice)
    Button sortPrice;
    @OnClick(R.id.sortPrice)
    void onSortPrice(){updateSort(sortPrice);}

    @Bind(R.id.sortNew)
    Button sortNew;

    @Bind(R.id.filterPanel)
    View filterPanel;




    @OnClick(R.id.sortNew)
    void onSortNew(){updateSort(sortNew);}

    @OnClick(R.id.closeFilter)
    void onCloseFilter(){
        YoYo.with(Techniques.FadeOutDown).duration(300).withListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                filterPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(filterPanel);
    }


    Button lastSorted;
    private void updateSort(Button btn) {
        if(lastSorted!=btn) {
            lastSorted.setSelected(false);
            lastSorted.setAlpha(Float.parseFloat(".3"));
            btn.setSelected(true);
            btn.setAlpha(Float.parseFloat("1"));
            lastSorted = btn;
            if (btn ==sortBestSelling)
                sort = SORT_SOLD_UNITS;
            else if (btn ==sortNew)
                sort = SORT_DATE_ADDED;
            else if (btn ==sortPrice)
                sort = SORT_PROD_DEFAULT_PRICE;
            refreshView(filters,sort);
        }
    }

    @Subscribe
    public void onEvent(FilterProductListing data)
    {
        processItem(data);
    }
    public void processItem(FilterProductListing data) {

    }

    public static final String SORT_PROD_DEFAULT_PRICE ="prodDefaultPrice";
    public static final String SORT_DATE_ADDED="dateAdded";
    public static final String SORT_SOLD_UNITS="soldUnits";
    public String sort=SORT_SOLD_UNITS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector_holder2);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Our Products");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_white);

        sortBestSelling.setSelected(true);
        sortBestSelling.setAlpha(Float.parseFloat("1"));
        sortNew.setAlpha(Float.parseFloat(".3"));
        sortPrice.setAlpha(Float.parseFloat(".3"));
        lastSorted=sortBestSelling;
        refreshView(filters,sort);

        RecyclerView selectorRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        selectorHelper= new SelectorHelper(this,selectorRecyclerView);
        selectorHelper.onRemoveListner(onRemoveListner);
        TypefaceHelper.setFont(applyFilters);
    }


    SelectorHelper.OnRemoveListner onRemoveListner = new SelectorHelper.OnRemoveListner()
    {
        @Override
        public void remove(Object o) {
            for (Parcelable filter : filters) {
                if(o instanceof Parcelable && (Parcelable)o==filter)
                {
                    filters.remove((Parcelable)o);

                    refreshView(filters,sort);
                    break;
                }
            }
        }
    };
    private void refreshView(ArrayList<Parcelable> filters,String sort) {
        if(filters!=null)
        {
            this.filters=filters;
            selectorHelper.removeAll();
            selectorHelper.addAll(filters);
            if(filters.size()==0)
                setTitle("Our Products");
            else
                setTitle("Your Selections");
        }
        else
            setTitle("Our Products");

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject = new NavigationDataObject(IDUtils.generateViewId(),"", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, ProductsFragment.class);
        mActivePage = (ProductsFragment) BaseFragment.newInstance(navigationObject);
        mActivePage.setFilters(filters,sort);
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
                    refreshView(filters,sort);
                }
            };
            h.postDelayed(r,200);

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
