package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.content.Intent;
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class CollectionsActivity extends BaseDrawerActivity {
    private final static String TAG = CollectionsActivity.class.getSimpleName();

    private final static String EXTRA_COLLECTION = "collection";
    private final static int UISTATE_PRODUCT = 0;
    private final static int UISTATE_SOCIAL = 1;
    private final static boolean DEBUG = true;
    private boolean DUMMY = false;
    private int mUIState = UISTATE_PRODUCT;

    @Bind(R.id.view_pager)
    ViewPager mPager;

    @Bind(R.id.frame)
    FrameLayout mFrame;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.scrollView)
    NestedScrollView mScrollView;

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

    @Bind(R.id.collection_description)
    TextView mBrandDescription;
    @Bind(R.id.collection_extra_description)
    TextView mBrandExtraDescription;

    @Bind(R.id.layout_1)
    View layout1;
    @Bind(R.id.layout_2)
    View layout2;
    @Bind(R.id.layout_3)
    View layout3;
    @Bind(R.id.top_layout)
    View topLayout;

    CollectionsPagerAdapter mCollectionAdapter;

    private final CollectionCallback mCollectionCallback = new CollectionCallback();

    private Context mContext;
    private Collection mCollection;
    private int mStartHeight, mCollapsedHeight;
    private ArrayList<CollectionChangeListener> mCollectionChangeListeners = new ArrayList<>(3);
    private int mVerticalOffset = 0;

    public static Intent getLaunchIntent(Context context, Collection collection) {
        Intent intent = new Intent(context, CollectionsActivity.class);
        intent.putExtra(EXTRA_COLLECTION, collection);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        mContext = this;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.7f * dm.heightPixels);
        mCollapsedHeight = (int) (.25f * dm.heightPixels);

        mPager.getLayoutParams().height = mStartHeight;
        topLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (b != null) {
            mCollection = (Collection) b.getSerializable(EXTRA_COLLECTION);
            if (mCollection != null) DUMMY = false;
        }

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(
                mCollectionAdapter = new CollectionsPagerAdapter(getSupportFragmentManager()));
        final int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);

        mPager.setPageMargin(-pad);
        mFrame.animate().setDuration(0).y(mStartHeight);

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
                    mBrandExtraDescription.setVisibility(visibility);
                    layout1.setVisibility(visibility);
                    layout2.setVisibility(visibility);
                    layout3.setVisibility(visibility);
                    mFrame.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                }
                mVerticalOffset = verticalOffset;
            }
        });
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mPager.getCurrentItem() - 1;
                int pos = current < 0 ? mCollectionAdapter.getCount() - 1 : current;
                mPager.setCurrentItem(pos);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = mPager.getCurrentItem() + 1;
                int pos = current > mCollectionAdapter.getCount() - 1 ? 0 : current;
                mPager.setCurrentItem(pos);
            }
        });

        initTabs();
        configureUI(mUIState);

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        ProductsFragment fragment = ProductsFragment.newInstance(ProductsFragment.MODE_COLLECTION,
                null, mCollection);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame, fragment);
        ft.commitAllowingStateLoss();
        getSupportLoaderManager().initLoader(mCollectionCallback.hashCode(), null,
                mCollectionCallback);

        final ProductResponse response = new ProductResponse();
        if (!DUMMY) response.collectionId = mCollection.id;
        UIController.getProducts(mContext, response, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                Log.d(TAG, "result : " + result.responseContent);
                if (result.success) DUMMY = false;
            }
        });

    }

    private void initTabs() {
        TabLayout.Tab tab = mTabLayout.newTab();
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
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(mCollectionCallback.hashCode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }

    public void registerCollectionChangeListener(CollectionChangeListener listener) {
        if (!mCollectionChangeListeners.contains(listener)) mCollectionChangeListeners.add(
                listener);
    }

    public void unRegisterCollectionChangeListener(CollectionChangeListener listener) {
        mCollectionChangeListeners.remove(listener);
    }

    private TabLayout.OnTabSelectedListener mTabSelectListener =
            new TabLayout.OnTabSelectedListener() {
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
            f = ProductsFragment.newInstance(ProductsFragment.MODE_COLLECTION, null, mCollection);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, f);
            fragmentTransaction.commit();
        }
    }

    private class CollectionsPagerAdapter extends FragmentStatePagerAdapter {
        private Cursor cursor;

        public CollectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (DUMMY) return 8;
            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        @Override
        public Fragment getItem(int position) {
            ShowcaseFragment f = new ShowcaseFragment();
            Bundle b = new Bundle(1);
            b.putInt(ShowcaseFragment.EXTRA_CATEGORY_POSITION, position);
            f.setArguments(b);
            return f;
        }

        public void changeCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        public Collection getCollection(int position) {
            if (DUMMY) return null;
            cursor.moveToPosition(position);
            return Collection.extractFromCursor(cursor);
        }
    }

    private class CollectionCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, Tables.Collections.CONTENT_URI, null,
                    Tables.Collections.USER_ID + " = ?",
                    new String[]{String.valueOf(mCollection == null ? -1 : mCollection.userId)},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
            mCollectionAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }
}
