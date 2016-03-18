package com.goldadorn.main.activities.showcase;

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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables.Users;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.L;

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
    private static boolean DUMMY = false;

    private int mUIState = UISTATE_COLLECTION;


    @Bind(R.id.view_pager)
    ViewPager mPager;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;


    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.previous)
    ImageView mPrevious;
    @Bind(R.id.next)
    ImageView mNext;
    @Bind(R.id.container_designer_overlay)
    LinearLayout mBrandButtonsLayout;

    @Bind(R.id.brand_description)
    TextView mBrandDescription;

    @Bind(R.id.layout_1)
    View layout1;
    @Bind(R.id.layout_2)
    View layout2;
    @Bind(R.id.layout_3)
    View layout3;
    @Bind(R.id.top_layout)
    View topLayout;

    private Context mContext;
    private final ShowCaseCallback mShowCaseCallback = new ShowCaseCallback();
    private ShowcasePagerAdapter mShowCaseAdapter;
    private OverlayViewHolder mOverlayVH;
    private User mUser;
    private List<UserChangeListener> mUserChangeListeners = new ArrayList<>(4);

    private int mStartHeight, mCollapsedHeight;
    private int mVerticalOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);
        mContext = this;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.7f * dm.heightPixels);
        mCollapsedHeight = (int) (.25f * dm.heightPixels);

        mPager.getLayoutParams().height = mStartHeight;
        topLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(mShowCaseAdapter = new ShowcasePagerAdapter(getSupportFragmentManager()));
        final int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);

        mPager.setPageMargin(-pad);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mVerticalOffset != verticalOffset) {
                    Log.d(TAG, "offset : " + verticalOffset);
                    boolean change = Math.abs(verticalOffset) <= .1f * mStartHeight;
                    int visibility = change ? View.VISIBLE : View.GONE;
                    topLayout.getLayoutParams().height = change ? mStartHeight : mCollapsedHeight;
                    CollapsingToolbarLayout.LayoutParams lp =
                            ((CollapsingToolbarLayout.LayoutParams) mTabLayout.getLayoutParams());
                    lp.leftMargin = lp.rightMargin = (int) (pad + (Math.abs(verticalOffset) * .1));
                    mTabLayout.setLayoutParams(lp);
                    mBrandDescription.setVisibility(visibility);
                    layout1.setVisibility(visibility);
                    layout2.setVisibility(visibility);
                    layout3.setVisibility(visibility);
                }
                mVerticalOffset = verticalOffset;
            }
        });


        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mPager.getCurrentItem() - 1;
                int pos = current < 0 ? mShowCaseAdapter.getCount() - 1 : current;
                mPager.setCurrentItem(pos);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mPager.getCurrentItem() + 1;
                int pos = current > mShowCaseAdapter.getCount() - 1 ? 0 : current;
                mPager.setCurrentItem(pos);
            }
        });

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout);

        initTabs();
        configureUI(mUIState);
        if (!DUMMY)
            mOverlayVH.itemView.setVisibility(View.INVISIBLE);
        UIController.getProductShowCase(mContext, new TimelineResponse(), new IResultListener<TimelineResponse>() {
            @Override
            public void onResult(TimelineResponse result) {
                Log.d(TAG, "result : " + result.responseContent);
                if (result.success)
                    DUMMY = false;
            }
        });
        getSupportLoaderManager().initLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);
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
            if (DUMMY)
                return;
            mUser = mShowCaseAdapter.getUser(position);
            bindOverlay(mUser);
            for (UserChangeListener l : mUserChangeListeners) l.onUserChange(mUser);
            ProductResponse response = new ProductResponse();
            response.userId = mUser.id;
            L.d("USER id " + mUser.id);
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
            f = ProductsFragment.newInstance(ProductsFragment.MODE_USER, mUser, null);
        } else {
            f = CollectionsFragment.newInstance(mUser);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, f);
            fragmentTransaction.commit();
        }
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
            if (DUMMY)
                return;
            mShowCaseAdapter.changeCursor(data);
            if (data.getCount() > 0) {
                mPageChangeListener.onPageSelected(mPager.getCurrentItem());
                mOverlayVH.itemView.setVisibility(View.VISIBLE);
            }
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
            if (DUMMY)
                return 8;
            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        @Override
        public Fragment getItem(int position) {
            ShowcaseFragment f = new ShowcaseFragment();
            if (!DUMMY) {
                User user = getUser(position);
                Bundle b = new Bundle(1);
                b.putInt(ShowcaseFragment.EXTRA_CATEGORY_POSITION, position);
                b.putString(ShowcaseFragment.EXTRA_IMAGE_URL, user.imageUrl);
                f.setArguments(b);
            }
            return f;
        }

        public User getUser(int position) {
            if (cursor.moveToPosition(position))
                return UserInfoCache.extractFromCursor(null, cursor);
            else return null;
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
