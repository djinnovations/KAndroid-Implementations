package com.goldadorn.main.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Menu;

import com.goldadorn.main.R;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class CollectionsActivity extends BaseDrawerActivity {
    private final static String TAG = CollectionsActivity.class.getSimpleName();
    private final static boolean DEBUG = true;

    @Bind(R.id.view_pager)
    ViewPager mPager;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.scrollView)
    NestedScrollView mScrollView;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        mContext = this;

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(new CollectionsPagerAdapter(getSupportFragmentManager()));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG, "offset : " + verticalOffset);
            }
        });

        mPager.getLayoutParams().height =
                (int) (.7f*getResources().getDisplayMetrics().heightPixels);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        CollectionsFragment fragment = new CollectionsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame,fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }


    private class CollectionsPagerAdapter extends FragmentStatePagerAdapter {
        public CollectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            ShowcaseFragment f = new ShowcaseFragment();
            Bundle b = new Bundle(1);
            b.putInt(ShowcaseFragment.EXTRA_CATEGORY_POSITION, position);
            f.setArguments(b);
            return f;
        }
    }
}
