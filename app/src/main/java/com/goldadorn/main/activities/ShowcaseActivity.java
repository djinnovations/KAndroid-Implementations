package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.goldadorn.main.R;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class ShowcaseActivity extends BaseDrawerActivity {
    private final static String TAG = ShowcaseActivity.class.getSimpleName();
    private final static boolean DEBUG = true;

    @Bind(R.id.view_pager)
    ViewPager mPager;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);
        mContext = this;

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(new ShowcasePagerAdapter(getSupportFragmentManager()));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG,"offset : "+verticalOffset);
            }
        });

        mPager.getLayoutParams().height =
                (int) (.7f*getResources().getDisplayMetrics().heightPixels);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }

    private class ShowcasePagerAdapter extends FragmentStatePagerAdapter {
        public ShowcasePagerAdapter(FragmentManager fm) {
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
