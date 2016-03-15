package com.goldadorn.main.activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.showcase.CollectionsFragment;
import com.goldadorn.main.activities.showcase.ProductsFragment;
import com.goldadorn.main.activities.showcase.SocialFragment;
import com.goldadorn.main.activities.showcase.UserChangeListener;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables.Products;
import com.goldadorn.main.db.Tables.Users;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class ShowcaseActivity extends BaseDrawerActivity {
    private final static int UISTATE_COLLECTION = 0;
    private final static int UISTATE_PRODUCT = 1;
    private final static int UISTATE_SOCIAL = 2;
    private final static String TAG = ShowcaseActivity.class.getSimpleName();
    private final static boolean DEBUG = true;

    private int mUIState = UISTATE_COLLECTION;
    @Bind(R.id.view_pager)
    ViewPager mPager;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;


    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    private Context mContext;
    private final ShowCaseCallback mShowCaseCallback = new ShowCaseCallback();
    private final ProductCallback mProductCallback = new ProductCallback();
    private ShowcasePagerAdapter mShowCaseAdapter;
    private OverlayViewHolder mOverlayVH;
    private User mUser;
    private List<UserChangeListener> mUserChangeListeners = new ArrayList<>(4);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);
        mContext = this;

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(mShowCaseAdapter = new ShowcasePagerAdapter(getSupportFragmentManager()));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(TAG, "offset : " + verticalOffset);

            }
        });

        mPager.getLayoutParams().height =
                (int) (.7f * getResources().getDisplayMetrics().heightPixels);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        mOverlayVH = new OverlayViewHolder(findViewById(R.id.container_designer_overlay));

        initTabs();
        configureUI(mUIState);

        UIController.getProductShowCase(mContext, new TimelineResponse(), new IResultListener<TimelineResponse>() {
            @Override
            public void onResult(TimelineResponse result) {
                Log.d(TAG, "result : " + result.responseContent);
            }
        });
        getSupportLoaderManager().initLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);
        getSupportLoaderManager().initLoader(mProductCallback.hashCode(), null, mProductCallback);
    }

    private void initTabs() {
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setTag(UISTATE_COLLECTION);
        tab.setText(getString(R.string.collections));
        mTabLayout.addTab(tab);
        tab = mTabLayout.newTab();
        tab.setTag(UISTATE_PRODUCT);
        tab.setText(getString(R.string.products));
        mTabLayout.addTab(tab);
        tab = mTabLayout.newTab();
        tab.setTag(UISTATE_SOCIAL);
        tab.setText(getString(R.string.social));
        mTabLayout.addTab(tab);
        mTabLayout.setOnTabSelectedListener(mTabSelectListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPager.removeOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(mShowCaseCallback.hashCode());
        getSupportLoaderManager().destroyLoader(mProductCallback.hashCode());
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }

    public void registerUserChangeListener(UserChangeListener listener) {
        if (!mUserChangeListeners.contains(listener)) mUserChangeListeners.add(listener);
    }

    public void unRegisterUserChangeListener(UserChangeListener listener) {
        mUserChangeListeners.remove(listener);
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mUser = mShowCaseAdapter.getUser(position);
            bindOverlay(mUser);
            for (UserChangeListener l : mUserChangeListeners) l.onUserChange(mUser);
            getSupportLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);

            ProductResponse response = new ProductResponse();
            response.userId = mUser.id;
            UIController.getProducts(mContext, response, new IResultListener<ProductResponse>() {
                @Override
                public void onResult(ProductResponse result) {
                    Log.d(TAG, "result : " + result.responseContent);
                }
            });
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private TabLayout.OnTabSelectedListener mTabSelectListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Log.d("OnTabSelectedListener", (String) tab.getText());
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
            f = new SocialFragment();
        } else if (uiState == UISTATE_PRODUCT) {
            f = new ProductsFragment();
        } else {
            f = new CollectionsFragment();
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, f);
            fragmentTransaction.commit();
        }
    }

    public User getCurrentUser() {
        return mUser;
    }


    private class ShowCaseCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, Users.CONTENT_URI, UserInfoCache.PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
            mShowCaseAdapter.changeCursor(data);
            mPageChangeListener.onPageSelected(mPager.getCurrentItem());
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }


    private class ProductCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, Products.CONTENT_URI, null, Products.USER_ID + " = ?", new String[]{String.valueOf(mUser == null ? -1 : mUser.id)}, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
//            mCollectionAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }

    private void bindOverlay(User user) {
        mOverlayVH.name.setText(user.name);
    }

    private class ShowcasePagerAdapter extends FragmentStatePagerAdapter {
        Cursor cursor = null;

        public ShowcasePagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        @Override
        public Fragment getItem(int position) {
            User user = getUser(position);
            ShowcaseFragment f = new ShowcaseFragment();
            Bundle b = new Bundle(1);
            b.putInt(ShowcaseFragment.EXTRA_CATEGORY_POSITION, position);
            b.putString(ShowcaseFragment.EXTRA_IMAGE_URL, user.imageUrl);
            f.setArguments(b);
            return f;
        }

        public User getUser(int position) {
            cursor.moveToPosition(position);
            return UserInfoCache.extractFromCursor(null, cursor);
        }

        public void changeCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }


    private static class OverlayViewHolder extends RecyclerView.ViewHolder {

        public ImageView badge1, badge2;
        public TextView name, description;

        public OverlayViewHolder(View itemView) {
            super(itemView);
            badge1 = (ImageView) itemView.findViewById(R.id.badge_1);
            badge2 = (ImageView) itemView.findViewById(R.id.badge_2);
            name = (TextView) itemView.findViewById(R.id.brand_name);
            description = (TextView) itemView.findViewById(R.id.brand_description);
        }

    }

}
