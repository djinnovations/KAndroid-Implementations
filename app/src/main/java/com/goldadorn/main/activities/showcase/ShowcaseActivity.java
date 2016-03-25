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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables.Users;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.L;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

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


    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.coordinatorlayout)
    CoordinatorLayout mCoordinatorLayout;


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
    View mProgressFrame;

    @Bind(R.id.frame_content)
    FrameLayout mFrame;

    @Bind(R.id.frame_content_dummy)
    View mFrameDummy;
    @Bind(R.id.view_pager_dummy)
    View mPagerDummy;

    @Bind(R.id.overlay)
    View mOverlay;


    private Context mContext;
    private final ShowCaseCallback mShowCaseCallback = new ShowCaseCallback();
    private ShowcasePagerAdapter mShowCaseAdapter;
    private OverlayViewHolder mOverlayVH;
    private TabViewHolder mTabViewHolder;
    private User mUser;
    private List<UserChangeListener> mUserChangeListeners = new ArrayList<>(4);

    private int mStartHeight, mCollapsedHeight;
    private int mVerticalOffset = 0;
    private int mCurrentPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);
        mContext = this;
        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout);
        mTabViewHolder = new TabViewHolder(mTabLayout);
        final DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.7f * dm.heightPixels);
        mCollapsedHeight = (int) (.25f * dm.heightPixels);

        mPagerDummy.getLayoutParams().height = mStartHeight;
        mRecyclerView.getLayoutParams().height = mStartHeight;
        mOverlay.getLayoutParams().height = mStartHeight;
        topLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        final int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, dm);
        final int maxPad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, dm);
        final int maxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, dm);

        mRecyclerView.setAdapter(mShowCaseAdapter = new ShowcasePagerAdapter(mContext,dm.widthPixels-2*pad,mStartHeight));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));

        mFrame.animate().setDuration(0).y(mStartHeight);
        mTabLayout.animate().setDuration(0).y(mStartHeight-getResources().getDimensionPixelSize(R.dimen.tab_height));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mVerticalOffset != verticalOffset) {
                    Log.d(TAG, "offset : " + verticalOffset);
                    boolean change = Math.abs(verticalOffset) <= .1f * mStartHeight;
                    int visibility = change ? View.VISIBLE : View.GONE;
                    topLayout.getLayoutParams().height = change ? mStartHeight : mCollapsedHeight;
                    mOverlayVH.setVisisbility(visibility);
                    mOverlay.getLayoutParams().height = mStartHeight+verticalOffset;
                    mRecyclerView.getLayoutParams().height = mStartHeight+verticalOffset;
                    int p= (int) (((maxPad*0.25f*verticalOffset)/maxHeight)-pad);
                    mShowCaseAdapter.setDimens(dm.widthPixels+2*p,mStartHeight+verticalOffset);
                    mRecyclerView.getLayoutManager().offsetChildrenHorizontal(-p);
                    mRecyclerView.setPadding(-p,0,-p,0);
                    mRecyclerView.scrollToPosition(mCurrentPosition);
                    mFrame.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    mTabLayout.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mTabLayout.getLayoutParams();
                    lp.leftMargin = lp.rightMargin = -p;
                }
                mVerticalOffset = verticalOffset;
            }
        });


        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition--;
                if(mCurrentPosition<0)
                    mCurrentPosition =mShowCaseAdapter.getItemCount() - 1;
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPosition++;
                if(mCurrentPosition>mShowCaseAdapter.getItemCount()-1)
                    mCurrentPosition =0;
                mRecyclerView.smoothScrollToPosition(mCurrentPosition);
            }
        });

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);


        if (!DUMMY)
            mOverlayVH.itemView.setVisibility(View.INVISIBLE);
        UIController.getDesigners(mContext, new TimelineResponse(), new IResultListener<TimelineResponse>() {
            @Override
            public void onResult(TimelineResponse result) {
                mProgressFrame.setVisibility(View.GONE);
                if (result.success) {
                    DUMMY = false;
                }
                mUser = mShowCaseAdapter.getUser(0);
                configureUI(mUIState);
            }
        });
        getSupportLoaderManager().initLoader(mShowCaseCallback.hashCode(), null, mShowCaseCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView.addOnScrollListener(mPageChangeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecyclerView.removeOnScrollListener(mPageChangeListener);
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

    private RecyclerView.OnScrollListener mPageChangeListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (DUMMY)
                return;
            mUser = mShowCaseAdapter.getUser(mCurrentPosition);
            bindOverlay(mUser);
            for (UserChangeListener l : mUserChangeListeners) l.onUserChange(mUser);
            ProductResponse response = new ProductResponse();
            response.userId = mUser.id;
            L.d("USER id " + mUser.id);
            UIController.getProducts(mContext, response, new IResultListener<ProductResponse>() {
                @Override
                public void onResult(ProductResponse result) {
                }
            });
        }
    };
    private TabLayout.OnTabSelectedListener mTabSelectListener = new TabLayout.OnTabSelectedListener() {
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
        int id = R.id.frame_content_dummy;
        mFrameDummy.setVisibility(View.VISIBLE);
        mFrame.setVisibility(View.INVISIBLE);
        if (uiState == UISTATE_SOCIAL) {
            f = new SocialFeedFragment();
        } else if (uiState == UISTATE_PRODUCT) {
            f = ProductsFragment.newInstance(ProductsFragment.MODE_USER, mUser, null);
            id = R.id.frame_content;
            mFrameDummy.setVisibility(View.INVISIBLE);
            mFrame.setVisibility(View.VISIBLE);
        } else {
            f = CollectionsFragment.newInstance(mUser);
        }
        mTabViewHolder.setSelected(uiState);
        if (f != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, f);
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
                mPageChangeListener.onScrolled(mRecyclerView,0,0);
                mOverlayVH.itemView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }


    private void bindOverlay(User user) {
        mOverlayVH.mBrandName.setText(user.name);
        mOverlayVH.mLikesCount.setText(String.format(Locale.getDefault(), "%d", user.likes_cnt));
        mOverlayVH.mFollowersCount.setText(String.format(Locale.getDefault(), "%d", user.followers_cnt));
        mOverlayVH.mFollowingCount.setText(String.format(Locale.getDefault(), "%d", user.following_cnt));

        mTabViewHolder.setCounts(user.collections_cnt,user.products_cnt);
    }

    private class ShowcasePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Context context;
        private int height;
        Cursor cursor = null;
        private int width;

        public ShowcasePagerAdapter(Context context,int width,int height) {
            this.context = context;
            this.width = width;
            this.height = height;
        }

        public User getUser(int position) {
            if (!DUMMY && cursor.moveToPosition(position))
                return UserInfoCache.extractFromCursor(null, cursor);
            else return null;
        }

        public void changeCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.layout_image,parent,false)) {
            };
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            User user = getUser(position);
            ImageView image  = (ImageView) holder.itemView.findViewById(R.id.image);
            Picasso.with(mContext).load(user.imageUrl).placeholder(R.drawable.designer).into(image);
            image.getLayoutParams().width = this.width;
            image.getLayoutParams().height = this.height;
            image.requestLayout();
        }

        @Override
        public int getItemCount() {
            if (DUMMY) return 8;
           return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        public void setDimens(int width,int height){
            this.width = width;
            this.height = height;
            notifyDataSetChanged();
        }
    }


    static class OverlayViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.brand_name)
        TextView mBrandName;
        @Bind(R.id.brand_description)
        TextView mBrandDescription;

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
        TextView mLikesCount;
        @Bind(R.id.followers_count)
        TextView mFollowersCount;
        @Bind(R.id.following_count)
        TextView mFollowingCount;

        public OverlayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setVisisbility(int visibility) {
            mBrandDescription.setVisibility(visibility);
            layout1.setVisibility(visibility);
            layout2.setVisibility(visibility);
            layout3.setVisibility(visibility);
        }

    }

    class TabViewHolder implements View.OnClickListener{
        private final float INTERVAL_TEXT_SIZE = 1;//in sp
        @Bind(R.id.tab1)
        View tab1;
        @Bind(R.id.tab2)
        View tab2;
        @Bind(R.id.tab3)
        View tab3;

        @Bind(R.id.tab1_name)
        TextView tabName1;
        @Bind(R.id.tab2_name)
        TextView tabName2;
        @Bind(R.id.tab3_name)
        TextView tabName3;
        @Bind(R.id.tab1_count)
        TextView tabCount1;
        @Bind(R.id.tab2_count)
        TextView tabCount2;

        @Bind(R.id.divider1)
        View divider1;
        @Bind(R.id.divider2)
        View divider2;

        int topMargin=0;

        public TabViewHolder(View itemView) {
            ButterKnife.bind(this,itemView);
            topMargin = getResources().getDimensionPixelSize(R.dimen.tab_top_margin);
            tab1.setOnClickListener(this);
            tab2.setOnClickListener(this);
            tab3.setOnClickListener(this);
        }

        public void setCounts(int count1,int count2){
            tabCount1.setText(String.format(Locale.getDefault(),"%d",count1));
            tabCount2.setText(String.format(Locale.getDefault(),"%d",count2));
        }

        public void setSelected(int pos){
            switch (pos){
                case 0:
                    setView(tabName1,tabCount1,true);
                    setView(tabName2,tabCount2,false);
                    setView(tabName3,null,false);
                    divider1.setVisibility(View.GONE);
                    divider2.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    setView(tabName1,tabCount1,false);
                    setView(tabName2,tabCount2,true);
                    setView(tabName3,null,false);
                    divider1.setVisibility(View.GONE);
                    divider2.setVisibility(View.GONE);
                    break;
                case 2:
                    setView(tabName1,tabCount1,false);
                    setView(tabName2,tabCount2,false);
                    setView(tabName3,null,true);
                    divider1.setVisibility(View.VISIBLE);
                    divider2.setVisibility(View.GONE);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id==R.id.tab1){
                configureUI(UISTATE_COLLECTION);
            }else if(id==R.id.tab2){
                configureUI(UISTATE_PRODUCT);
            }else if(id==R.id.tab3){
                configureUI(UISTATE_SOCIAL);
            }
        }

        private void setView(TextView view1,TextView view2,boolean selected){
            view1.setTextColor(selected?Color.BLACK:Color.WHITE);
            if(view2!=null) {
                view2.setTextColor(selected ? Color.BLACK : Color.WHITE);
                view2.setTextSize(TypedValue.COMPLEX_UNIT_SP,selected?34:12);
            }
            View parent = ((ViewGroup)view1.getParent());
            parent.setBackgroundColor(selected?Color.WHITE:Color.BLACK);
            ((LinearLayout.LayoutParams)parent.getLayoutParams()).topMargin=selected?0:topMargin;
        }
    }

}
