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
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables.Users;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.User;
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
    private ShowcasePagerAdapter mShowCaseAdapter;
    private OverlayViewHolder mOverlayVH;


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
        getSupportLoaderManager().initLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);

        mRecyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mOverlayVH = new OverlayViewHolder(findViewById(R.id.container_designer_overlay));
        int value = getResources().getDimensionPixelSize(R.dimen.appDefaultMargin);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);


        CollectionsAdapter rcAdapter = new CollectionsAdapter(this);
        mRecyclerView.setAdapter(rcAdapter);

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

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            bindOverlay(mShowCaseAdapter.getUser(position));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }

    private void bindOverlay(User user) {
        mOverlayVH.name.setText(user.name);
    }

    class CollectionsAdapter extends RecyclerView.Adapter<CollectionHolder> {

        Context context;
        private View.OnClickListener mCollectionClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_collections);
                if (navigationDataObject != null)
                    action(navigationDataObject);
            }
        };

        public CollectionsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_showcase_brand_item, null);
            CollectionHolder rcv = new CollectionHolder(layoutView);
            rcv.itemView.setOnClickListener(mCollectionClick);
            return rcv;
        }

        @Override
        public void onBindViewHolder(CollectionHolder holder, int position) {
            holder.image.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) ((Math.random() + 1) * 100), getResources().getDisplayMetrics());
        }

        @Override
        public int getItemCount() {
            return 25;
        }
    }

    private static class CollectionHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView description;
        public TextView extra;
        public TextView likeCount;
        public ImageView image;
        public IconicsButton like;

        public CollectionHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.collection_name);
            description = (TextView) itemView.findViewById(R.id.collection_description);
            extra = (TextView) itemView.findViewById(R.id.extra);
            likeCount = (TextView) itemView.findViewById(R.id.collection_likes);
            like = (IconicsButton) itemView.findViewById(R.id.likeButton);
            image = (ImageView) itemView.findViewById(R.id.collection_image);
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
