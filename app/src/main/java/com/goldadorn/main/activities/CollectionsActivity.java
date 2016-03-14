package com.goldadorn.main.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.Menu;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class CollectionsActivity extends BaseDrawerActivity {
    private final static String TAG = CollectionsActivity.class.getSimpleName();

    private final static String EXTRA_COLLECTION = "collection";
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

    CollectionsPagerAdapter mCollectionAdapter;

    private final ProductCallback mProductCallback = new ProductCallback();

    private Context mContext;
    private Collection mCollection;

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
        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (b != null) {
            mCollection = (Collection) b.getSerializable(EXTRA_COLLECTION);
        }

        mPager.setOffscreenPageLimit(4);
        mPager.setAdapter(mCollectionAdapter = new CollectionsPagerAdapter(getSupportFragmentManager()));

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

        CollectionsFragment fragment = new CollectionsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frame, fragment);
        ft.commitAllowingStateLoss();
        getSupportLoaderManager().initLoader(mProductCallback.hashCode(), null, mProductCallback);

        ProductResponse response = new ProductResponse();
        response.collectionId = -1;
        UIController.getProducts(mContext, response, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                Log.d(TAG, "result : " + result.responseContent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(mProductCallback.hashCode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        return value;
    }


    private class CollectionsPagerAdapter extends FragmentStatePagerAdapter {
        private Cursor cursor;

        public CollectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        @Override
        public Fragment getItem(int position) {
            Collection collection = getCollection(position);
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
            cursor.moveToPosition(position);
            return Collection.extractFromCursor(cursor);
        }
    }

    private class ProductCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, Tables.Products.CONTENT_URI, null, Tables.Products.COLLECTION_ID + " = ?", new String[]{String.valueOf(mCollection == null ? -1 : mCollection.id)}, null);
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
