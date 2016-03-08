package com.goldadorn.main.activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.mikepenz.iconics.view.IconicsButton;

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
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Context mContext;
    private final ShowCaseCallback mShowCaseCallback = new ShowCaseCallback();
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;


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
                Log.d(TAG, "offset : " + verticalOffset);
            }
        });

        mPager.getLayoutParams().height =
                (int) (.7f*getResources().getDisplayMetrics().heightPixels);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        getSupportLoaderManager().initLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);

        mRecyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        int value = getResources().getDimensionPixelSize(R.dimen.appDefaultMargin);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);


        CollectionsAdapter rcAdapter = new CollectionsAdapter(this);
        mRecyclerView.setAdapter(rcAdapter);

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

    private class ShowCaseCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(mContext, Tables.Users.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    class CollectionsAdapter extends RecyclerView.Adapter<CollectionHolder>{

        Context context;

        public CollectionsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_showcase_brand_item, null);
            CollectionHolder rcv = new CollectionHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(CollectionHolder holder, int position) {
            holder.image.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) ((Math.random()+1)*100),getResources().getDisplayMetrics());
        }

        @Override
        public int getItemCount() {
            return 25;
        }
    }

    private class CollectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView description;
        public TextView extra;
        public TextView likeCount;
        public ImageView image;
        public IconicsButton like;

        public CollectionHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.collection_name);
            description = (TextView) itemView.findViewById(R.id.collection_description);
            extra = (TextView) itemView.findViewById(R.id.extra);
            likeCount = (TextView) itemView.findViewById(R.id.collection_likes);
            like = (IconicsButton) itemView.findViewById(R.id.likeButton);
            image = (ImageView) itemView.findViewById(R.id.collection_image);
        }


        @Override
        public void onClick(View v) {

        }
    }
}
