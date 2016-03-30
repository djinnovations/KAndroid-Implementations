package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.mikepenz.iconics.view.IconicsButton;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductActivity extends BaseDrawerActivity {
    private final static int UISTATE_CUSTOMIZE = 0;
    private final static int UISTATE_SOCIAL = 1;
    private final static int UISTATE_PRODUCT = 2;
    private static final String TAG = ProductActivity.class.getName();

    private int mUIState = UISTATE_CUSTOMIZE;

    @Bind(R.id.view_pager)
    ViewPager mPager;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;


    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.container_designer_overlay)
    LinearLayout mBrandButtonsLayout;

    @Bind(R.id.progress_frame)
    View mProgressFrame;

    private Context mContext;
    private OverlayViewHolder mOverlayVH;
    private int mStartHeight;
    private int mCollapsedHeight;
    private ProductPagerAdapter mProductAdapter;
    private int mVerticalOffset = 0;

    public static Intent getLaunchIntent(Context context) {
        Intent intent = new Intent(context, ProductActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        mContext = this;
        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.9f * dm.heightPixels);
        mCollapsedHeight = (int) (.4f * dm.heightPixels);

        mPager.getLayoutParams().height = mStartHeight;
//        mBrandButtonsLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;
        final int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(mProductAdapter = new ProductPagerAdapter(getSupportFragmentManager()));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mVerticalOffset != verticalOffset) {
                    Log.d(TAG, "offset : " + verticalOffset);
                    boolean change = Math.abs(verticalOffset) <= .1f * mStartHeight;
                    int visibility = change ? View.VISIBLE : View.GONE;
//                    mBrandButtonsLayout.getLayoutParams().height =
//                            change ? mStartHeight : mCollapsedHeight;
                    CollapsingToolbarLayout.LayoutParams lp =
                            ((CollapsingToolbarLayout.LayoutParams) mTabLayout.getLayoutParams());
                    lp.leftMargin = lp.rightMargin = (int) (pad + (Math.abs(verticalOffset) * .1));
                    mTabLayout.setLayoutParams(lp);
                    mOverlayVH.setVisisbility(visibility);
                }
                mVerticalOffset = verticalOffset;
            }
        });

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);


        initTabs();
        configureUI(UISTATE_CUSTOMIZE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }

    private void initTabs() {
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setTag(UISTATE_CUSTOMIZE);
        tab.setText("Customize");
        mTabLayout.addTab(tab);
        tab = mTabLayout.newTab();
        tab.setTag(UISTATE_SOCIAL);
        tab.setText(getString(R.string.social));
        mTabLayout.addTab(tab);
        tab = mTabLayout.newTab();
        tab.setTag(UISTATE_PRODUCT);
        tab.setText(getString(R.string.products));
        mTabLayout.addTab(tab);
        mTabLayout.setOnTabSelectedListener(mTabSelectListener);
    }


    private TabLayout.OnTabSelectedListener mTabSelectListener =
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Log.d("OnTabSelectedListener", "" + tab.getText());
                    int uiState = (int) tab.getTag();
                    configureUI(uiState);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };

    private void configureUI(int uiState) {
        Fragment f = null;
        if (uiState == UISTATE_SOCIAL) {
            f = new SocialFeedFragment();
            mTabLayout.getTabAt(1).select();
        } else if (uiState == UISTATE_PRODUCT) {
            f = new ProductInfoFragment();
            mTabLayout.getTabAt(2).select();
        } else {
            f = new CustomizeFragment();
            mTabLayout.getTabAt(0).select();
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, f);
            fragmentTransaction.commit();
        }
    }

    private class ProductPagerAdapter extends FragmentStatePagerAdapter {
        Cursor cursor = null;

        public ProductPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return 8;
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

    static class OverlayViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.indicator)
        CirclePageIndicator indicator;

        @Bind(R.id.product_name)
        TextView mProductName;
        @Bind(R.id.product_owner_name)
        TextView mProductOwner;
        @Bind(R.id.followButton)
        IconicsButton followButton;

        @Bind(R.id.product_collection_name)
        TextView mProductCollection;
        @Bind(R.id.product_cost)
        TextView mProductCost;
        @Bind(R.id.product_collection_name_2)
        TextView mProductCollection2;
        @Bind(R.id.product_cost_2)
        TextView mProductCost2;

        @Bind(R.id.layout_1)
        View layout1;
        @Bind(R.id.layout_2)
        View layout2;
        @Bind(R.id.layout_3)
        View layout3;
        @Bind(R.id.layout_product_actions)
        View layout4;


        @Bind(R.id.likes_count)
        TextView mLikesCount;


        public OverlayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setVisisbility(int visibility) {
            int oppositeVisibility = View.VISIBLE==visibility?View.GONE:View.VISIBLE;
            layout1.setVisibility(visibility);
            layout2.setVisibility(visibility);
            mProductCollection.setVisibility(visibility);
            mProductCollection2.setVisibility(oppositeVisibility);
            mProductCost.setVisibility(visibility);
            mProductCost2.setVisibility(oppositeVisibility);
            mProductName.setVisibility(visibility);
            mProductOwner.setVisibility(visibility);
        }

    }
}
