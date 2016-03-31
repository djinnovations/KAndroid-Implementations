package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class CollectionsActivity extends BaseDrawerActivity {
    private final static String TAG = CollectionsActivity.class.getSimpleName();

    private final static String EXTRA_DESIGNER = "designer";
    private final static String EXTRA_COLLECTION = "collection";
    private final static int UISTATE_PRODUCT = 0;
    private final static int UISTATE_SOCIAL = 1;
    private final static boolean DEBUG = true;
    private int mUIState = UISTATE_PRODUCT;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.view_pager_dummy)
    View mPagerDummy;
    @Bind(R.id.overlay)
    View mOverlay;

    @Bind(R.id.frame)
    FrameLayout mFrame;
    @Bind(R.id.frame_scroll_dummy)
    FrameLayout mFrameScrollDummy;
    @Bind(R.id.frame_no_scroll_dummy)
    FrameLayout mFrameNoScrollDummy;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabs)
    View mTabLayout;

    @Bind(R.id.previous)
    ImageView mPrevious;
    @Bind(R.id.next)
    ImageView mNext;
    @Bind(R.id.container_designer_overlay)
    LinearLayout mBrandButtonsLayout;

    @Bind(R.id.top_layout)
    View topLayout;

    @Bind(R.id.progress_frame)
    View mProgressLayout;

    OverlayViewHolder mOverlayViewHolder;

    CollectionsPagerAdapter mCollectionAdapter;

    private final CollectionCallback mCollectionCallback = new CollectionCallback();

    private Context mContext;
    private Collection mCollection;
    private int mStartHeight, mCollapsedHeight;
    private ArrayList<CollectionChangeListener> mCollectionChangeListeners = new ArrayList<>(3);
    private int mVerticalOffset = 0;
    private int mCurrentPosition = 0;
    private TabViewHolder mTabViewHolder;
    private Handler mHandler = new Handler();

    public static Intent getLaunchIntent(Context context, Collection collection) {
        Intent intent = new Intent(context, CollectionsActivity.class);
        intent.putExtra(EXTRA_COLLECTION, collection);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
        drawerLayout.setBackgroundColor(Color.WHITE);
        mContext = this;
        mOverlayViewHolder = new OverlayViewHolder(mBrandButtonsLayout);
        initTabs();
        final DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.7f * dm.heightPixels);
        mCollapsedHeight = (int) (.25f * dm.heightPixels);

        mPagerDummy.getLayoutParams().height = mStartHeight;
        mRecyclerView.getLayoutParams().height = mStartHeight;
        mOverlay.getLayoutParams().height = mStartHeight;
        topLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (b != null) {
            mCollection = (Collection) b.getSerializable(EXTRA_COLLECTION);
        }

        final int tabStart = mStartHeight - getResources().getDimensionPixelSize(
                R.dimen.tab_height) + getResources().getDimensionPixelSize(R.dimen.shadow_height);
        final int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);
        final int maxPad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, dm);
        final int maxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, dm);

        mRecyclerView.setAdapter(mCollectionAdapter =
                new CollectionsPagerAdapter(mContext, dm.widthPixels - 2 * pad, mStartHeight));
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mFrame.animate().setDuration(0).y(mStartHeight);
        mTabLayout.animate().setDuration(0).y(tabStart);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mVerticalOffset != verticalOffset) {

                    Log.d(TAG, "offset : " + verticalOffset);
                    boolean change = Math.abs(verticalOffset) <= .1f * mStartHeight;
                    int visibility = change ? View.VISIBLE : View.GONE;
                    topLayout.getLayoutParams().height = change ? mStartHeight : mCollapsedHeight;
                    mOverlayViewHolder.setVisisbility(visibility);
                    mOverlay.getLayoutParams().height = mStartHeight + verticalOffset;
                    mRecyclerView.getLayoutParams().height = mStartHeight + verticalOffset;
                    int p = (int) (((maxPad * 0.25f * verticalOffset) / maxHeight) - pad);
                    mCollectionAdapter.setDimens(dm.widthPixels + 2 * p,
                            mStartHeight + verticalOffset);
                    mRecyclerView.getLayoutManager().offsetChildrenHorizontal(-p);
                    mRecyclerView.setPadding(-p, 0, -p, 0);
                    mRecyclerView.scrollToPosition(mCurrentPosition);
                    mFrame.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    mTabLayout.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    mTabViewHolder.setSides(-p);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mVerticalOffset == 0) {
                                mTabLayout.animate().setDuration(0).y(tabStart);
                            }
                        }
                    }, 180);
                }
                mVerticalOffset = verticalOffset;
            }
        });
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition--;
                if (mCurrentPosition < 0) mCurrentPosition = mCollectionAdapter.getItemCount() - 1;
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition++;
                if (mCurrentPosition > mCollectionAdapter.getItemCount() - 1) mCurrentPosition = 0;
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
            }
        });


        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        configureUI(mUIState);
        getSupportLoaderManager().initLoader(mCollectionCallback.hashCode(), null,
                mCollectionCallback);
    }

    private void initTabs() {
        mTabViewHolder = new TabViewHolder(mContext, mTabLayout);
        mTabViewHolder.initTabs(getString(R.string.products), getString(R.string.social), null,
                new TabViewHolder.TabClickListener() {
                    @Override
                    public void onTabClick(int position) {
                        configureUI(position);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView.addOnScrollListener(mPageChangeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecyclerView.addOnScrollListener(mPageChangeListener);
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

    private RecyclerView.OnScrollListener mPageChangeListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    mCollection = mCollectionAdapter.getCollection(mCurrentPosition);
                    bindOverlay(mCollection);
                    for (CollectionChangeListener l : mCollectionChangeListeners)
                        l.onCollectionChange(mCollection);
                }
            };


    private void bindOverlay(Collection collection) {
        mOverlayViewHolder.name.setText(collection.name);
        User user = UserInfoCache.getInstance(mContext).getUserInfo(collection.userId,true);
        String t = user!=null?user.getName():"";
        mOverlayViewHolder.ownerName.setText(t);
        mOverlayViewHolder.followButton.setVisibility(TextUtils.isEmpty(t)?View.GONE:View.VISIBLE);
        mOverlayViewHolder.description.setText(collection.description);
        mOverlayViewHolder.likesCount.setText(
                String.format(Locale.getDefault(), "%d", collection.likecount));
        mOverlayViewHolder.extra.setText("");
        mOverlayViewHolder.setBadges(collection.isFeatured, collection.isTrending);

        mTabViewHolder.setCounts(collection.productcount, -1);

    }

    private void configureUI(int uiState) {
        Fragment f = null;
        int id = R.id.frame;
        mFrame.setVisibility(View.VISIBLE);
        mFrameScrollDummy.setVisibility(View.INVISIBLE);
        mFrameNoScrollDummy.setVisibility(View.INVISIBLE);
        if (uiState == UISTATE_SOCIAL) {
            f = new SocialFeedFragment();
            id = R.id.frame_no_scroll_dummy;
            mFrame.setVisibility(View.INVISIBLE);
            mFrameScrollDummy.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);

        } else if (uiState == UISTATE_PRODUCT) {
            f = ProductsFragment.newInstance(ProductsFragment.MODE_COLLECTION, null, mCollection);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, f);
            fragmentTransaction.commit();
        }
    }

    private class CollectionsPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Context context;
        private int height;
        Cursor cursor = null;
        private int width;

        public CollectionsPagerAdapter(Context context, int width, int height) {
            this.context = context;
            this.width = width;
            this.height = height;
        }

        public void changeCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(
                    getLayoutInflater().inflate(R.layout.layout_image, parent, false)) {
            };
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Collection collection = getCollection(position);
            ImageView image = (ImageView) holder.itemView.findViewById(R.id.image);
            Picasso.with(mContext).load(collection.getImageUrl()).placeholder(R.drawable.designer)
                   .into(image);
            image.getLayoutParams().width = this.width;
            image.getLayoutParams().height = this.height;
            image.requestLayout();
        }

        @Override
        public int getItemCount() {
            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        public void setDimens(int width, int height) {
            this.width = width;
            this.height = height;
            notifyDataSetChanged();
        }


        public Collection getCollection(int position) {
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
            if (data.getCount() > 0) {
                mPageChangeListener.onScrolled(mRecyclerView, 0, 0);
                mOverlayViewHolder.itemView.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }

    static class OverlayViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.collection_name)
        TextView name;
        @Bind(R.id.collection_owner_name)
        TextView ownerName;
        @Bind(R.id.followButton)
        IconicsButton followButton;
        @Bind(R.id.collection_description)
        TextView description;
        @Bind(R.id.collection_extra_description)
        TextView extra;

        @Bind(R.id.layout_1)
        View layout1;
        @Bind(R.id.layout_2)
        View layout2;
        @Bind(R.id.layout_3)
        View layout3;

        @Bind(R.id.badge_1)
        ImageView mFeatured;
        @Bind(R.id.badge_2)
        ImageView mTrending;

        @Bind(R.id.likes_count)
        TextView likesCount;
        @Bind(R.id.likeButton)
        IconicsButton like;
        @Bind(R.id.shareButton)
        IconicsButton share;

        public void setBadges(boolean featured, boolean trending) {
            mFeatured.setVisibility(featured ? View.VISIBLE : View.GONE);
            mTrending.setVisibility(trending ? View.VISIBLE : View.GONE);
        }

        public OverlayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setVisisbility(int visibility) {
            ownerName.setVisibility(visibility);
            description.setVisibility(visibility);
            extra.setVisibility(visibility);
            layout1.setVisibility(visibility);
            layout2.setVisibility(visibility);
            layout3.setVisibility(visibility);
            ((LinearLayout)this.itemView).setGravity(visibility == View.GONE ? Gravity.TOP :
                    Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        }

    }
}
