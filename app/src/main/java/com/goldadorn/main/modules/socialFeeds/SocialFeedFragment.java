package com.goldadorn.main.modules.socialFeeds;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.CommentsActivity;
import com.goldadorn.main.activities.HashTagResultActivity;
import com.goldadorn.main.activities.ImageZoomActivity;
import com.goldadorn.main.activities.LikesActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.VotersActivity;
import com.goldadorn.main.activities.showcase.ProductActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.dj.adapter.RecommendedProductsAdapter;
import com.goldadorn.main.dj.adapter.TrendingHashTagAdapter;
import com.goldadorn.main.dj.model.ProductTemp;
import com.goldadorn.main.dj.model.RecommendedProduct;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.server.RequestJson;
import com.goldadorn.main.dj.support.EmojisHelper;
import com.goldadorn.main.dj.support.SocialUtils;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.icons.GoldadornIconFont;
import com.goldadorn.main.icons.HeartIconFont;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.people.FindPeopleFragment;
import com.goldadorn.main.modules.socialFeeds.helper.FollowHelper;
import com.goldadorn.main.modules.socialFeeds.helper.LikeHelper;
import com.goldadorn.main.modules.socialFeeds.helper.PostUpdateHelper;
import com.goldadorn.main.modules.socialFeeds.helper.SelectHelper;
import com.goldadorn.main.modules.socialFeeds.helper.VoteHelper;
import com.goldadorn.main.server.Api;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.ImageLoaderUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class SocialFeedFragment extends DefaultVerticalListView {
    protected boolean isRefreshingData = false;
    protected int offset = 0;
    protected int refreshOffset = 0;
    private SocialPost commentSocialPost;
    private int commentPosition;

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }


    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) { }
        else {  }
    }*/

    public DataManager getDataManagerCustom() {
        return this.getDataManager();
    }

    public void refreshSelf() {
        getAdapter().notifyDataSetChanged();
    }


    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    public FloatingActionsMenu getFloatingActionsMenu() {
        return mFloatingActionsMenu;
    }

    @Bind(R.id.floatingActionsMenu)
    FloatingActionsMenu mFloatingActionsMenu;
    private LikeHelper likeHelper;
    private VoteHelper voteHelper;
    private SelectHelper selectHelper;
    private FollowHelper followHelper;

    PostUpdateHelper.PostUpdateResult postUpdateResult = new PostUpdateHelper.PostUpdateResult() {
        @Override
        public void onFail(PostUpdateHelper host, SocialPost post, int pos) {
            if (host instanceof LikeHelper) {
                int isLiked = post.getIsLiked();
                isLiked = isLiked == 0 ? 1 : 0;
                if (isLiked == 0)
                    post.setLikeCount(post.getLikeCount() - 1);
                else
                    post.setLikeCount(post.getLikeCount() + 1);
                post.setIsLiked(isLiked);
                getAdapter().notifyItemChanged(pos);
            } else if (host instanceof VoteHelper) {
                post.setIsVoted(0);
                getAdapter().notifyItemChanged(pos);
            } else if (host instanceof SelectHelper) {
                post.setIsVoted(0);
                getAdapter().notifyItemChanged(pos);
            }
            if (host instanceof FollowHelper) {
                int isFollowing = post.getIsFollowing();
                isFollowing = isFollowing == 0 ? 1 : 0;
                post.setIsFollowing(isFollowing);
                //getAdapter().notifyItemChanged(pos);
                getAdapter().notifyDataSetChanged();
            }

        }

        @Override
        public void onSuccess(PostUpdateHelper host, SocialPost post, int pos) {
            if (host instanceof LikeHelper)
                getAdapter().notifyItemChanged(pos);
            else if (host instanceof VoteHelper)
                getAdapter().notifyItemChanged(pos);
            else if (host instanceof SelectHelper)
                getAdapter().notifyItemChanged(pos);
            else if (host instanceof FollowHelper) {
                getAdapter().notifyItemChanged(pos);
                updateFollowStatus(post);
                //frameApeopleObj(post);
            }
        }
    };

    protected boolean isHashTagFunctionAllowed() {
        return true;
    }

    private void frameApeopleObj(SocialPost post) {
        People people = new People();
        people.setUserId(post.getUserId());
        people.setIsFollowing(post.getIsFollowing());
        MainActivity mainActivity = getAppMainActivity();
        if (mainActivity == null)
            return;
        if (mainActivity.getPeopleFragment() instanceof FindPeopleFragment)
            mainActivity.getPeopleFragment().updatePeopleObjIfPresent(people);
    }


    protected void updateFollowStatus(SocialPost feedPost) {
        int userIdOfIncomingPost = feedPost.getUserId();
        List<Integer> listOfPosition = new ArrayList<>();
        for (Object obj : getDataManager()) {
            if (((SocialPost) obj).getUserId() == userIdOfIncomingPost) {
                listOfPosition.add(getDataManager().indexOf(obj));
                ((SocialPost) obj).setIsFollowing(feedPost.getIsFollowing());
            }
        }
        if (listOfPosition.size() == 0)
            return;
        for (int position : listOfPosition)
            getAdapter().notifyItemChanged(position);
    }

    public void updatePostList(int pos) {
        //// TODO: 25-06-2016
        Log.d("djfeed", "updatePostList");
        getDataManager().remove(pos);
        getAdapter().notifyItemRemoved(pos);
    }


    public void postAdded(SocialPost socialPost, String postId) {
        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();*/
        //askToRefresh();
        if (socialPost == null) {
            Log.d("djfeed", "normal postAdded: postid: " + postId);
            postIdMain = postId;
            refreshPosts(-1);
        } else {
            Log.d("djfeed", "postAdded- custom");
            postIdMain = postId;
            getDataManager().add(0, socialPost);
            getAdapter().notifyItemInserted(0);
            //getAdapter().notifyDataSetChanged();
        }
    }


    private void likeAPost(SocialPost post, int pos) {
        likeHelper.update(post, pos);
    }

    private void voteAPost(SocialPost post, int pos) {
        voteHelper.update(post, pos);
    }

    private void selectAPost(SocialPost post, int pos) {
        selectHelper.update(post, pos);
    }

    private void gotoVotes(SocialPost socialPost, int pos) {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(), "Votes by", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, VotersActivity.class);
        Map<String, Object> data = new HashMap<>();
        data.put("POST_ID", socialPost.getPostId());
        navigationDataObject.setParam(data);
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }

    private void followPeople(SocialPost post, int pos) {
        followHelper.update(post, pos);
    }

    public boolean allowedBack() {
        if (mFloatingActionsMenu.isExpanded()) {
            closeMenu();
            return false;
        } else
            return super.allowedBack();
    }

    protected DataManager createDataManager() {
        return new SocialFeedProjectDataManager(getActivity(), this, getApp().getCookies());
    }

    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, offset);
        params.put(URLHelper.LIKE_A_POST.POST_ID, 0);
        params.put("reco", 1);
        params.put("mlp", 1);
        params.put("trend", 1);
        Log.d("djfeed", "socialfeed- req params: " + params);
        return params;
    }

    public Map<String, Object> getRefreshDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, refreshOffset);
        //SocialPost sp = (SocialPost) getDataManager().get(0);
        //params.put(URLHelper.LIKE_A_POST.POST_ID, postIdMain);
        params.put(URLHelper.LIKE_A_POST.POST_ID, "0");
        params.put("reco", 1);
        params.put("mlp", 1);
        params.put("trend", 1);
        Log.d("djfeed", "req params: " + params.toString());
        /*getDataManager().clear();
        Log.d("djfeed", "size of dataManager: "+getDataManager().size());*/
        return params;
    }

    public String getNextDataURL(PageData pageData) {
        isRefreshingData = false;
        return getApp().getUrlHelper().getSocialFeedServiceURL();
    }

    public String getRefreshDataURL(PageData pageData) {
        isRefreshingData = true;
        return getApp().getUrlHelper().getSocialFeedServiceURL();
    }


    public class SocialFeedProjectDataManager extends DefaultProjectDataManager {
        public SocialFeedProjectDataManager(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
            super(context, delegate, cookies);
        }

        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }

        public Map<String, Object> getRefreshDataServerCallParams(PageData data) {
            return getRefreshDataParams(data);
        }

        protected boolean isRefreshPage(PageData pageData, String url) {
            try {
                return isRefreshingData;
            } catch (Exception var4) {
                return false;
            }
        }

        protected void updatePagingData(BaseDataParser loadedDataVO) {
            try {
                offset = loadedDataVO.getPageSize();
                pageData.curruntPage += 1;
                pageData.totalPage += 1;
            } catch (Exception e) {
                pageData.curruntPage = pageData.totalPage = 1;
            }
        }
    }

    @Bind(R.id.post)
    FloatingActionButton post;

    @Bind(R.id.poll)
    FloatingActionButton poll;

    @Bind(R.id.bestof)
    FloatingActionButton bestof;

    @Bind(R.id.refreshTriger)
    Button refreshTriger;

    /*@OnClick(R.id.refreshTriger)
    void onRefreshTrigerClick() {
        refreshPosts();
        fadeOutRefreshTriger();

    }*/


    private void fadeOutRefreshTriger() {
        if (refreshTrigerhandler != null)
            refreshTrigerhandler.removeCallbacks(runnablelocal);
        YoYo.with(Techniques.FadeOut).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                refreshTriger.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).playOn(refreshTriger);
    }


    @OnClick(R.id.post)
    void onPostClick() {
        EventBus.getDefault().post(new com.goldadorn.main.eventBusEvents.SocialPost(SocialPost.POST_TYPE_NORMAL_POST, this));
        closeMenu();
    }

    @OnClick(R.id.poll)
    void onPollClick() {
        EventBus.getDefault().post(new com.goldadorn.main.eventBusEvents.SocialPost(SocialPost.POST_TYPE_POLL, this));
        closeMenu();
    }

    @OnClick(R.id.bestof)
    void onBestofClick() {
        EventBus.getDefault().post(new com.goldadorn.main.eventBusEvents.SocialPost(SocialPost.POST_TYPE_BEST_OF, this));
        closeMenu();
    }

    View disableApp;
    View disableApp1;
    @Bind(R.id.disableApp)
    View disableAppCover;

    @Bind(R.id.fabBackImage)
    View fabBackImage;

    public View getFabBackImage() {
        return fabBackImage;
    }

    @Override
    public void garbageCollectorCall() {
        super.garbageCollectorCall();
        disableApp = null;
        disableAppCover = null;
        refreshTriger = null;
        disableApp1 = null;
        if (likeHelper != null)
            likeHelper.clear();
        likeHelper = null;
        if (voteHelper != null)
            voteHelper.clear();
        voteHelper = null;

        if (selectHelper != null)
            selectHelper.clear();
        selectHelper = null;

    }

    @OnClick(R.id.disableApp)
    void onDisableAppCover() {

        closeMenu();
    }

    private void logEventsAnalytics(String eventName) {
        getApp().getFbAnalyticsInstance().logCustomEvent(getActivity(), eventName);
    }

    private void setUserInfoCache() {

       /* ApiFactory.getDesignersSocial(context, response);
        if (response.success && response.responseContent != null) {
            DbHelper.writeDesignersSocial(context, response);*/
        UIController.getShowCase(null, getContext(),
                new IResultListener<TimelineResponse>() {
                    @Override
                    public void onResult(TimelineResponse result) {
                        Log.d("djfeed", "userInfoCache updation: " + result.success);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getAppMainActivity() != null) {
            if (getUserVisibleHint()) {
                getAppMainActivity().getFilterPanel().setVisibility(View.GONE);
            }
            getAppMainActivity().setSocialFeedFragment(this);
        }
    }

    SocialUtils socialUtils;
    public void onViewCreated(View view) {
        if (getActivity() instanceof MainActivity)
            setUserInfoCache();
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: Social feed");
        logEventsAnalytics(GAAnalyticsEventNames.SOCIAL_FEED);
        //setErrorFrames();
        ButterKnife.bind(this, view);

        if (getAppMainActivity() != null) {
            disableApp = getAppMainActivity().getDisableApp();
            if (disableApp != null) {
                disableApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeMenu();
                    }
                });
            }
        }

        socialUtils = SocialUtils.getInstance();
        int iconSize = 4;
        Drawable icon = IconsUtils.getFontIconDrawable(getActivity(), GoldadornIconFont.Icon.gol_post, R.color.white, iconSize);
        post.setIconDrawable(icon);


        icon = IconsUtils.getFontIconDrawable(getActivity(), HeartIconFont.Icon.hea_buy_or_not, R.color.white, iconSize);
        poll.setIconDrawable(icon);

        icon = IconsUtils.getFontIconDrawable(getActivity(), GoldadornIconFont.Icon.gol_best_of, R.color.white, iconSize);
        bestof.setIconDrawable(icon);

        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            public void onMenuExpanded() {
                if (disableApp != null) {
                    disableApp.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(disableApp);
                }
                if (disableApp1 != null) {
                    disableApp1.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(disableApp1);
                }

                if (disableAppCover != null) {
                    disableAppCover.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(disableAppCover);
                }

            }

            public void onMenuCollapsed() {
                closeMenu();
            }
        });

        likeHelper = new LikeHelper(getActivity(), getApp().getCookies(), postUpdateResult);
        voteHelper = new VoteHelper(getActivity(), getApp().getCookies(), postUpdateResult);
        selectHelper = new SelectHelper(getActivity(), getApp().getCookies(), postUpdateResult);
        followHelper = new FollowHelper(getActivity(), getApp().getCookies(), postUpdateResult);
    }

    /*private View setErrorFrames(View view) {
        *//*TextView errTextView = (TextView) view.findViewById(R.id.emptyViewMessage);
        errTextView.setText("Oops! Looks like we had a glitch. Please restart the app to get back on");*//*
        Button btnRefresh = (Button) view.findViewById(R.id.retry);
        btnRefresh.setText("Restart App");
        return view;
    }


    @Override
    public void onDataLoadError(String url, Object status) {
        //super.onDataLoadError(url, status);
        Log.d("djfeed", "onDataLoadError");
        mempHelper.updateView(this.getDataManager());
        updateSwipeRefreshLayout(false);
    }

    private class MyEmptyViewHelper extends EmptyViewHelper {

        private boolean showInetnetError;
        private boolean showRetryButton;

        public MyEmptyViewHelper(Context context, View emptyView, IEmptyViewHelper emptyViewHelper,
                                 boolean showInetnetError, boolean showRetryButton) {
            super(context, emptyView, emptyViewHelper, showInetnetError, showRetryButton);
            this.showInetnetError = showInetnetError;
            this.showRetryButton = showRetryButton;
        }

        @Override
        public void updateView(List dataManager) {
            Log.d("djfeed", "updateView - visibilty: " + mEmptyView.getVisibility());
            if (mEmptyView != null && dataManager != null) {
                Log.d("djfeed", "updateView- pt2: " + mEmptyView.getVisibility());
                if (dataManager.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    Log.d("djfeed", "updateView- pt3: " + mEmptyView.getVisibility());
                    if (this.showInetnetError && !NetworkUtilities.isConnected(this.context)) {
                        Log.d("djfeed", "updateView- pt4: " + mEmptyView.getVisibility());
                        if (mEmptyViewImage != null) {
                            mEmptyViewImage.setImageDrawable(mEmptyViewHelper.getInternetViewDrawable());
                        }

                        if (mEmptyViewMessage != null) {
                            mEmptyViewMessage.setText(mEmptyViewHelper.getInternetViewMessage());
                        }

                       *//* if (this.mRetry != null) {
                            if (this.showRetryButton) {
                                this.mRetry.setVisibility(0);
                            } else {
                                this.mRetry.setVisibility(8);
                            }
                        }*//*
                    } else {
                        Log.d("djfeed", "updateView- pt5: " + mEmptyView.getVisibility());
                        if (mEmptyViewImage != null) {
                            mEmptyViewImage.setImageDrawable(mEmptyViewHelper.getEmptyViewDrawable());
                        }

                        if (mEmptyViewMessage != null) {
                            mEmptyViewMessage.setText(mEmptyViewHelper.getEmptyViewMessage());
                        }
                        *//*if (this.mRetry != null) {
                            this.mRetry.setVisibility(8);
                        }*//*
                    }
                    Log.d("djfeed", "updateView- pt6: " + mEmptyView.getVisibility());

                    if (mRetry != null) {
                        if (this.showRetryButton) {
                            mRetry.setVisibility(0);
                        } else {
                            mRetry.setVisibility(8);
                        }
                    }
                    mEmptyView.setVisibility(0);
                    Log.d("djfeed", "updateView- pt7: " + mEmptyView.getVisibility());

                } else {
                    Log.d("djfeed", "updateView- pt8: " + mEmptyView.getVisibility());
                    mEmptyView.setVisibility(8);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            Log.d("djfeed", "updateView- recycler view visiblity: " + recyclerView.getVisibility());
        }

    }


    DefaultErrorHandler errorHandler = new DefaultErrorHandler() {
        @Override
        public Resources getResources() {
            return getAppMainActivity().getResources();
        }

        @Override
        public void retry() {
            RandomUtils.restartApp(getAppMainActivity());
        }

        @Override
        public String getEmptyViewMessage() {
            //return super.getEmptyViewMessage();
            return "Oops! Looks like we had a glitch. Please restart the app to get back on";
        }
    };

    MyEmptyViewHelper mempHelper;
    */
    @Override
    protected EmptyViewHelper createEmptyViewHelper() {
        EmptyViewHelper helper = new SocialFragmentEmptyViewHelper(this.getActivity(), this.createEmptyView(this.mRootView), this, this.showInternetError(), this.showInternetRetryButton());
        return helper;
    }

    @Override
    public void retry() {
        //super.retry();
        RandomUtils.restartApp(getAppMainActivity());
    }


    public void closeMenu() {
        if (mFloatingActionsMenu.isExpanded())
            mFloatingActionsMenu.collapse();

        if (disableApp != null) {
            YoYo.with(Techniques.FadeOut).duration(500).withListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator var1) {

                }

                public void onAnimationEnd(Animator var1) {
                    disableApp.setVisibility(View.GONE);
                    if (disableAppCover != null)
                        disableAppCover.setVisibility(View.GONE);
                    if (disableApp1 != null)
                        disableApp1.setVisibility(View.GONE);
                }

                public void onAnimationCancel(Animator var1) {

                }

                public void onAnimationRepeat(Animator var1) {

                }
            }).playOn(disableApp);

            YoYo.with(Techniques.FadeOut).duration(500).playOn(disableAppCover);

            if (disableApp1 != null)
                YoYo.with(Techniques.FadeOut).duration(500).playOn(disableApp1);

        }
    }

    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (getDataManager().getRefreshEnabled())
            view = inflater.inflate(R.layout.social_feed_fragment_recycler_with_swipe_refresh_layout, container, false);
        else
            view = inflater.inflate(R.layout.social_feed_fragment_recycler, container, false);

        return view;
    }


    protected MainActivity getBaseActivity() {
        return (MainActivity) getActivity();
    }


    Handler refreshTrigerhandler = new Handler();
    Runnable runnablelocal = new Runnable() {
        @Override
        public void run() {
            fadeOutRefreshTriger();
        }
    };

    public void askToRefresh() {
        if (refreshTriger != null) {
            refreshTriger.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn).duration(500).playOn(refreshTriger);

            if (refreshTrigerhandler != null)
                refreshTrigerhandler.removeCallbacks(runnablelocal);
            refreshTrigerhandler.postDelayed(runnablelocal, 7000);
        }
    }

    String postIdMain = "0";
    /*public void refreshPosts() {
        postIdMain = *//*((SocialPost) getDataManager().get(0)).getPostId()*//*"0";
        getDataManager().clear();
        refreshPosts(0);
    }*/

    protected void loadRefreshData() {
        super.loadRefreshData();
    }

    public void refreshPosts(int offset) {
        refreshOffset = offset;
        if (getSwipeRefreshLayout() != null) {
            if (getDataManager().canLoadRefresh()) {
                loadRefreshData();
            } else {
                getSwipeRefreshLayout().setRefreshing(false);
            }
            getSwipeRefreshLayout().setEnabled(getDataManager().hasScopeOfRefresh());
        } else if (getDataManager().canLoadRefresh() && getDataManager().hasScopeOfRefresh())
            loadRefreshData();
    }

    /*protected void configSwipeRefreshLayout(SwipeRefreshLayout view) {
        super.configSwipeRefreshLayout(view);
        if (view != null) {
            view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    if (getDataManager().canLoadRefresh()) {
                        refreshOffset = -2;
                        postIdMain = ((SocialPost) getDataManager().get(0)).getPostId();
                        loadRefreshData();
                    } else {
                        getSwipeRefreshLayout().setRefreshing(false);
                    }

                    getSwipeRefreshLayout().setEnabled(getDataManager().hasScopeOfRefresh());
                }
            });
            boolean refreshEnabled = this.getDataManager().getRefreshEnabled();
            getSwipeRefreshLayout().setEnabled(refreshEnabled);
            getSwipeRefreshLayout().setColorSchemeColors(new int[]{com.kimeeo.library.R.array.progressColors});
        }
    }*/


    protected void configSwipeRefreshLayout(SwipeRefreshLayout view) {
        super.configSwipeRefreshLayout(view);
        if (view != null) {
            view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    Handler h = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() instanceof MainActivity) {
                                MainActivity mainView = (MainActivity) getActivity();
                                mainView.forceRefresh();
                            }
                        }
                    };
                    h.postDelayed(r, 1000);
                    /*

                    if (getDataManager().canLoadRefresh()) {

                        refreshOffset = 0;

                        loadRefreshData();

                    } else {

                        getSwipeRefreshLayout().setRefreshing(false);

                    }


                    getSwipeRefreshLayout().setEnabled(getDataManager().hasScopeOfRefresh());

                    */
                }

            });
            boolean refreshEnabled = this.getDataManager().getRefreshEnabled();
            view.setEnabled(refreshEnabled);
            view.setColorSchemeColors(new int[]{com.kimeeo.library.R.array.progressColors});
        }
    }


    public void addDisableColver(View disableApp) {
        disableApp1 = disableApp;
        if (disableApp1 != null) {
            disableApp1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });
        }
    }

    public static class ViewTypes {
        public static final int VIEW_NORMAL = 5;
        public static final int VIEW_POLL = 10;
        public static final int VIEW_BEST_OF = 15;
        public static final int VIEW_RATE_US = 20;
        public static final int VIEW_RECO_PROD = 25;
        public static final int VIEW_ATC = 30;
        public static final int VIEW_RATC = 35;
        public static final int VIEW_MOST_LIKED = 40;
        public static final int VIEW_TRENDING_HASHTAG = 45;
    }

    public static boolean reateItemAdded = false;

    public void onCallEnd(List<?> dataList, boolean isRefreshData) {
        super.onCallEnd(dataList, isRefreshData);
        if (!reateItemAdded) {
            if (getDataManager().size() > 4) {
                //RateApp rateApp = new RateApp();
                //    getDataManager().add(getDataManager().size()-2,rateApp);
                //reateItemAdded=true;
            }
        }
    }

    public static class RateApp {

    }

    public void onItemClick(Object baseObject) {
        super.onItemClick(baseObject);
        // Toast.makeText(getActivity(), baseObject.toString(), Toast.LENGTH_SHORT).show();
    }

    public int getListItemViewType(int position, Object item) {
        if (item instanceof RateApp) {
            return ViewTypes.VIEW_RATE_US;
        }/*else if (item instanceof )*/// TODO: 30-07-2016
        else {
            SocialPost post = (SocialPost) item;
            if (post.getPostType() == SocialPost.POST_TYPE_BEST_OF)
                return ViewTypes.VIEW_BEST_OF;
            else if (post.getPostType() == SocialPost.POST_TYPE_POLL)
                return ViewTypes.VIEW_POLL;
            else if (post.getPostType() == SocialPost.POST_RECOMMENDED_PRODS)
                return ViewTypes.VIEW_RECO_PROD;
            else if (post.getPostType() == SocialPost.POST_RATC)
                return ViewTypes.VIEW_RATC;
            else if (post.getPostType() == SocialPost.POST_MOST_LIKED)
                return ViewTypes.VIEW_MOST_LIKED;
            else if (post.getPostType() == SocialPost.POST_ATC)
                return ViewTypes.VIEW_ATC;
            else if (post.getPostType() == SocialPost.POST_TREDING_TAGS)
                return ViewTypes.VIEW_TRENDING_HASHTAG;
            else
                return ViewTypes.VIEW_NORMAL;
        }

    }

    public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container) {
        Log.d("dj", "viewType: " + viewType);
        if (viewType == ViewTypes.VIEW_RATE_US)
            return inflater.inflate(R.layout.rate_app_card, null);
        else if (viewType == ViewTypes.VIEW_POLL)
            return inflater.inflate(R.layout.social_post_poll_item, null);
        else if (viewType == ViewTypes.VIEW_BEST_OF)
            return inflater.inflate(R.layout.social_post_best_of_item, null);
        return inflater.inflate(R.layout.social_post_item, null);
    }

    public BaseItemHolder getItemHolder(int viewType, View view) {
        if (viewType == ViewTypes.VIEW_RATE_US)
            return new RateAppItemHolder(view);
        else if (viewType == ViewTypes.VIEW_POLL)
            return new PollPostItemHolder(view);
        else if (viewType == ViewTypes.VIEW_BEST_OF)
            return new BestOfPostItemHolder(view);
        else if ((viewType == ViewTypes.VIEW_RECO_PROD) || (viewType == ViewTypes.VIEW_RATC) ||
                (viewType == ViewTypes.VIEW_MOST_LIKED)) {
            String title = "Recommended Products For You";
            /*if (viewType == ViewTypes.VIEW_RECO_PROD)
            title = "Recommended Products For You";*/
            if (viewType == ViewTypes.VIEW_RATC)
                title = "Recently Added To Cart";
            else if (viewType == ViewTypes.VIEW_MOST_LIKED)
                title = "Most Liked Products By Users";
            return new RecommendedProductsItemHolder(view, title);
        } else if ((viewType == ViewTypes.VIEW_TRENDING_HASHTAG))
            return new TrendingHashtagItemHolder(view, "Trending Hashtags");
        else if ((viewType == ViewTypes.VIEW_ATC))
            return new AddToCartPost(view);
        return new NormalPostItemHolder(view);
    }


    public Class getLoadedDataParsingAwareClass() {
        return SocilFeedResult.class;
    }

    public boolean isVoted(SocialPost socialPost, boolean showMSG) {
        if (socialPost.getIsVoted() != 0) {
            if (showMSG)
                Toast.makeText(getActivity(), "You can only vote once. You cannot change your vote, once voted", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    public class PollPostItemHolder extends PostItemHolder {

        @Bind(R.id.image)
        ImageView image;

        @Bind(R.id.detailsHolder)
        ViewGroup detailsHolder;

        @Bind(R.id.notBuy)
        Button notBuy;

        @Bind(R.id.notBuyLabel)
        TextView notBuyLabel;

        @Bind(R.id.buy)
        Button buy;

        @Bind(R.id.buyLabel)
        TextView buyLabel;


        @Bind(R.id.stackBar)
        LinearLayout stackBar;


        @Bind(R.id.stack1)
        TextView stack1;

        @Bind(R.id.stack2)
        TextView stack2;


        @Bind(R.id.buyNow)
        View buyNow;

        @Bind(R.id.voteToView)
        TextView voteToView;

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == buy) {
                    if (!isVoted(socialPost, true)) {
                        if (getActivity() instanceof MainActivity) {
                            if (isProductLink(socialPost.getImage1loc()) != null)
                                ((MainActivity) getActivity()).displayDialogForIntimation();
                        }
                        socialPost.setIsVoted(1);
                        buy.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(buy);
                        buy.setSelected(true);
                        notBuy.setSelected(true);
                        voteToView.setVisibility(View.GONE);
                        voteAPost(socialPost, getAdapterPosition());
                    }
                } else if (v == notBuy) {
                    if (!isVoted(socialPost, true)) {
                        socialPost.setIsVoted(2);
                        notBuy.setText("{hea_broken_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(notBuy);
                        buy.setSelected(true);
                        notBuy.setSelected(true);
                        voteToView.setVisibility(View.GONE);
                        voteAPost(socialPost, getAdapterPosition());
                    }

                } else if (v == image) {
                    zoomImages(socialPost, 0);
                } else if (v == votePostButton) {
                    gotoVotes(socialPost, getAdapterPosition());
                } else if (v == pollLabel) {
                    gotoVotes(socialPost, getAdapterPosition());
                }

            }
        };

        public PollPostItemHolder(View itemView) {
            super(itemView);
            buy.setOnClickListener(itemClick);
            notBuy.setOnClickListener(itemClick);
            pollLabel.setOnClickListener(itemClick);
            votePostButton.setOnClickListener(itemClick);
            image.setOnClickListener(itemClick);
            Typeface temp = TypefaceHelper.getTypeFace(Application.getInstance(),
                    ResourceReader.getInstance(Application.getInstance()).getStringFromResource(R.string.font_name_text_normal));
            voteToView.setTypeface(temp, Typeface.BOLD);
            TypefaceHelper.setFont(notBuyLabel, buyLabel);
        }

        public void updatePostView(SocialPost item, View view, int position) {
            pollLabel.setVisibility(View.VISIBLE);
            votePostButton.setVisibility(View.VISIBLE);
            pollLabel.setText(item.getVoteCount() + getActivity().getString(R.string.voteCountLabel));
            votePostButton.setText("{hea_buy_or_not}");

            updatePriceAndDiscount(item, false);
            Log.d("djfeed", "socialPost1 imageUrl - onBind(): " + socialPost.getImage1loc());

            if (socialPost.getIsVoted() == 1) {
                //Log.d("djfeed", "poll post isVoted - must set color to pink: ");
                votePostButton.setSelected(true);
            } else votePostButton.setSelected(false);

            if (item.getImg1loc() != null && item.getImg1loc().url.trim().equals("") == false) {
                ImageLoaderUtils.loadImage(getContext(), item.getImg1loc(), image, R.drawable.vector_image_logo_square_100dp, true);
                detailsHolder.setVisibility(View.VISIBLE);
                if (isProductLink(item.getImage1loc()) != null) {
                    //
                    buyNow.setVisibility(View.VISIBLE);
                } else {
                    //
                    buyNow.setVisibility(View.GONE);
                }

            } else {
                detailsHolder.setVisibility(View.GONE);
            }

            Boolean isVoted = isVoted(item, false);
           /* if (isVoted) {
                votePostButton.setSelected(true);
            }*/
            if (isVoted) {
                buyLabel.setText("Buy: " + item.getYesPercent() + "%");
                notBuyLabel.setText("Not Buy: " + item.getNoPercent() + "%");
                voteToView.setVisibility(View.GONE);


                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, Float.parseFloat(item.getNoPercent().toString()));

                stack1.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, Float.parseFloat(item.getYesPercent().toString()));
                stack2.setLayoutParams(param);
                stackBar.setVisibility(View.VISIBLE);
            } else {
                buyLabel.setText("Buy");
                notBuyLabel.setText("Not Buy");
                voteToView.setVisibility(View.VISIBLE);
                stackBar.setVisibility(View.GONE);
            }


            if (item.getIsVoted() == 0) {
                buy.setText("{hea_heart_fill}");
                notBuy.setText("{hea_broken_heart_fill}");
                buy.setSelected(false);
                notBuy.setSelected(false);
            } else if (item.getIsVoted() == 1) {
                buy.setText("{hea_heart_fill}");
                notBuy.setText("{hea_broken_heart_fill}");
                buy.setSelected(true);
                notBuy.setSelected(true);
            } else if (item.getIsVoted() == 2) {
                buy.setText("{hea_heart_fill}");
                notBuy.setText("{hea_broken_heart_fill}");
                buy.setSelected(true);
                notBuy.setSelected(true);
            }
        }
    }

    public class BestOfPostItemHolder extends PostItemHolder {

        @Bind(R.id.optionBox1)
        ViewGroup optionBox1;
        @Bind(R.id.optionBox2)
        ViewGroup optionBox2;
        @Bind(R.id.optionBox3)
        ViewGroup optionBox3;

        @Bind(R.id.option1Image)
        ImageView option1Image;
        @Bind(R.id.option2Image)
        ImageView option2Image;
        @Bind(R.id.option3Image)
        ImageView option3Image;

        @Bind(R.id.option1Button)
        Button option1Button;
        @Bind(R.id.option2Button)
        Button option2Button;
        @Bind(R.id.option3Button)
        Button option3Button;

        @Bind(R.id.option1Label)
        TextView option1Label;
        @Bind(R.id.option2Label)
        TextView option2Label;
        @Bind(R.id.option3Label)
        TextView option3Label;
        @Bind(R.id.detailsHolder)
        ViewGroup detailsHolder;

        /*@Bind(R.id.tvBOT1)
        TextView tvBOT1;
        @Bind(R.id.tvBOT2)
        TextView tvBOT2;
        @Bind(R.id.tvBOT3)
        TextView tvBOT3;*/

        /*@Bind(R.id.buyNow1)
        View buyNow1;
        @Bind(R.id.buyNow2)
        View buyNow2;
        @Bind(R.id.buyNow3)
        View buyNow3;*/

        @Bind(R.id.voteToView)
        TextView voteToView;

        @Bind(R.id.stackBar)
        LinearLayout stackBar;


        @Bind(R.id.stack1)
        TextView stack1;

        @Bind(R.id.stack2)
        TextView stack2;

        @Bind(R.id.stack3)
        TextView stack3;

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == option1Button) {
                    if (!isVoted(socialPost, true)) {
                        if (getActivity() instanceof MainActivity) {
                            if (isProductLink(socialPost.getImage1loc()) != null)
                                ((MainActivity) getActivity()).displayDialogForIntimation();
                        }
                        socialPost.setIsVoted(1);
                        option1Button.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(option1Button);
                        option1Button.setSelected(true);
                        option2Button.setSelected(true);
                        option3Button.setSelected(true);
                        voteToView.setVisibility(View.GONE);
                        selectAPost(socialPost, getAdapterPosition());
                    }
                } else if (v == option2Button) {
                    if (!isVoted(socialPost, true)) {
                        if (getActivity() instanceof MainActivity) {
                            if (isProductLink(socialPost.getImage2loc()) != null)
                                ((MainActivity) getActivity()).displayDialogForIntimation();
                        }
                        socialPost.setIsVoted(2);
                        option2Button.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(option2Button);
                        option1Button.setSelected(true);
                        option2Button.setSelected(true);
                        option3Button.setSelected(true);
                        voteToView.setVisibility(View.GONE);
                        selectAPost(socialPost, getAdapterPosition());
                    }
                } else if (v == option3Button) {
                    if (!isVoted(socialPost, true)) {
                        if (getActivity() instanceof MainActivity) {
                            if (isProductLink(socialPost.getImage3loc()) != null)
                                ((MainActivity) getActivity()).displayDialogForIntimation();
                        }
                        socialPost.setIsVoted(3);
                        option3Button.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(option3Button);
                        option1Button.setSelected(true);
                        option2Button.setSelected(true);
                        option3Button.setSelected(true);
                        voteToView.setVisibility(View.GONE);
                        selectAPost(socialPost, getAdapterPosition());
                    }
                } else if (v == option1Image) {
                    zoomImages(socialPost, 0);
                } else if (v == option2Image) {
                    zoomImages(socialPost, 1);
                } else if (v == option3Image) {
                    zoomImages(socialPost, 2);
                } else if (v == votePostButton) {
                    gotoVotes(socialPost, getAdapterPosition());
                } else if (v == pollLabel) {
                    gotoVotes(socialPost, getAdapterPosition());
                }
            }
        };

        public BestOfPostItemHolder(View itemView) {
            super(itemView);
            option1Button.setOnClickListener(itemClick);
            option2Button.setOnClickListener(itemClick);
            option3Button.setOnClickListener(itemClick);
            option1Image.setOnClickListener(itemClick);
            option2Image.setOnClickListener(itemClick);
            option3Image.setOnClickListener(itemClick);
            pollLabel.setOnClickListener(itemClick);
            votePostButton.setOnClickListener(itemClick);
            Typeface temp = TypefaceHelper.getTypeFace(Application.getInstance(),
                    ResourceReader.getInstance(Application.getInstance()).getStringFromResource(R.string.font_name_text_normal));
            voteToView.setTypeface(temp, Typeface.BOLD);
            TypefaceHelper.setFont(option1Label, option2Label, option3Label, tvDiscountOnRed1, tvDiscountOnRed2, tvDiscountOnRed3);
        }

        @Bind(R.id.rlCircleRed1)
        View discountHolder1;
        @Bind(R.id.tvDiscountOnRed1)
        TextView tvDiscountOnRed1;
        @Bind(R.id.rlCircleRed2)
        View discountHolder2;
        @Bind(R.id.tvDiscountOnRed2)
        TextView tvDiscountOnRed2;
        @Bind(R.id.rlCircleRed3)
        View discountHolder3;
        @Bind(R.id.tvDiscountOnRed3)
        TextView tvDiscountOnRed3;

        private void updateSelfDiscount(SocialPost post) {

            double discount1 = -1;
            double discount2 = -1;
            double discount3 = -1;
            try {
                if (!TextUtils.isEmpty(post.getDiscount1()))
                    discount1 = Double.parseDouble(post.getDiscount1());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                discount1 = -1;
            }
            try {
                if (!TextUtils.isEmpty(post.getDiscount2()))
                    discount2 = Double.parseDouble(post.getDiscount2());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                discount2 = -1;
            }
            try {
                if (!TextUtils.isEmpty(post.getDiscount3()))
                    discount3 = Double.parseDouble(post.getDiscount3());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                discount3 = -1;
            }

            /*if (true){
                discountHolder1.setVisibility(View.GONE);
                discountHolder2.setVisibility(View.GONE);
                discountHolder3.setVisibility(View.GONE);
                return;
            }*/

            if (discount1 > 0) {//for bot1
                discountHolder1.setVisibility(View.VISIBLE);
                tvDiscountOnRed1.setText("-" + String.valueOf(Math.round(discount1)) + "%");
            } else {
                discountHolder1.setVisibility(View.GONE);
            }
            if (discount2 > 0) {//for bot2
                discountHolder2.setVisibility(View.VISIBLE);
                tvDiscountOnRed2.setText("-" + String.valueOf(Math.round(discount2)) + "%");
            } else {
                discountHolder2.setVisibility(View.GONE);
            }
            if (discount3 > 0) {//for bot3
                discountHolder3.setVisibility(View.VISIBLE);
                tvDiscountOnRed3.setText("-" + String.valueOf(Math.round(discount3)) + "%");
            } else {
                discountHolder3.setVisibility(View.GONE);
            }
        }

        public void updatePostView(SocialPost item, View view, int position) {

            pollLabel.setVisibility(View.VISIBLE);
            votePostButton.setVisibility(View.VISIBLE);
            pollLabel.setText(item.getVoteCount() + getActivity().getString(R.string.voteCountLabel));
            detailsHolder.setVisibility(View.VISIBLE);

            updatePriceAndDiscount(item, true);
            updateSelfDiscount(item);
            /*if (socialPost.getIsVoted() == 1){
                votePostButton.setSelected(true);
            }else votePostButton.setSelected(false);*/

            Log.d("djfeed", "socialPost1 imageUrl - onBind(): " + socialPost.getImage1loc());
            Log.d("djfeed", "socialPost2 imageUrl - onBind(): " + socialPost.getImage2loc());
            Log.d("djfeed", "socialPost3 imageUrl - onBind(): " + socialPost.getImage3loc());

            Boolean isVoted = isVoted(item, false);
            Log.d("djfeed", "BOT-isVoted value: " + isVoted);
            if (isVoted) {
                Log.d("djfeed", "BOT isVoted - must set color to pink: " + isVoted);
                votePostButton.setSelected(true);
            } else votePostButton.setSelected(false);

            if (item.getImg1loc() != null && item.getImg1loc().url.trim().equals("") == false) {
                /*String urlnew = Product.getImageUrl(RandomUtils.getIdFromImageUrl(item.getImage1loc()),
                        );
                Log.d("djfeed", "image1 url-BOT: " + );*/
                ImageLoaderUtils.loadImage(getContext(), item.getImg1loc(), option1Image, R.drawable.vector_image_logo_square_100dp, true);
                optionBox1.setVisibility(View.VISIBLE);
                if (isVoted)
                    option1Label.setText(item.getBof3Percent1() + "%");
                else
                    option1Label.setText("");

               /* if (isProductLink(item.getImage1()) != null)
                    buyNow1.setVisibility(View.VISIBLE);
                else
                    buyNow1.setVisibility(View.GONE);*/

            } else
                optionBox1.setVisibility(View.GONE);

            if (item.getImg2loc() != null && item.getImg2loc().url.trim().equals("") == false) {
                ImageLoaderUtils.loadImage(getContext(), item.getImg2loc(), option2Image, R.drawable.vector_image_logo_square_100dp, true);
                optionBox2.setVisibility(View.VISIBLE);

               /* if (item.getImg1() == null) {
                    tvBOT2.setText(String.valueOf(1));
                }*/

                if (isVoted)
                    option2Label.setText(item.getBof3Percent2() + "%");
                else
                    option2Label.setText("");

                /*if (isProductLink(item.getImage2()) != null)
                    buyNow2.setVisibility(View.VISIBLE);
                else
                    buyNow2.setVisibility(View.GONE);*/
            } else
                optionBox2.setVisibility(View.GONE);

            if (item.getImg3loc() != null && item.getImg3loc().url.trim().equals("") == false) {
                ImageLoaderUtils.loadImage(getContext(), item.getImg3loc(), option3Image, R.drawable.vector_image_logo_square_100dp, true);
                optionBox3.setVisibility(View.VISIBLE);
                /*if (item.getImg2() == null) {
                    tvBOT3.setText(String.valueOf(1));
                } else */
                /*if (item.getImg1() == null) {
                    tvBOT3.setText(String.valueOf(2));
                }*/
                if (isVoted)
                    option3Label.setText(item.getBof3Percent3() + "%");
                else
                    option3Label.setText("");

               /* if (isProductLink(item.getImage3()) != null)
                    buyNow3.setVisibility(View.VISIBLE);
                else
                    buyNow3.setVisibility(View.GONE);*/
            } else
                optionBox3.setVisibility(View.GONE);


            if (item.getIsVoted() != 0) {
                LinearLayout.LayoutParams param;

                param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Float.parseFloat(item.getBof3Percent1().toString()));
                stack1.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Float.parseFloat(item.getBof3Percent2().toString()));
                stack2.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, Float.parseFloat(item.getBof3Percent3().toString()));
                stack3.setLayoutParams(param);

                /*if (item.getImg1() == null) {
                    stack2.setText(String.valueOf(1));
                }
                if (item.getImg1() == null) {
                    stack3.setText(String.valueOf(2));
                }*/

                stackBar.setWeightSum(100);
            }


            if (item.getIsVoted() == 0) {

                option1Button.setText("{hea_heart_fill}");
                option2Button.setText("{hea_heart_fill}");
                option3Button.setText("{hea_heart_fill}");

                option1Button.setSelected(false);
                option2Button.setSelected(false);
                option3Button.setSelected(false);
                voteToView.setVisibility(View.VISIBLE);
                stackBar.setVisibility(View.GONE);

            } else if (item.getIsVoted() == 1) {
                option1Button.setText("{hea_heart_fill}");
                option2Button.setText("{hea_heart_fill}");
                option3Button.setText("{hea_heart_fill}");
                option1Button.setSelected(true);
                option2Button.setSelected(true);
                option3Button.setSelected(true);
                voteToView.setVisibility(View.GONE);
                stackBar.setVisibility(View.VISIBLE);

            } else if (item.getIsVoted() == 2) {
                option1Button.setText("{hea_heart_fill}");
                option2Button.setText("{hea_heart_fill}");
                option3Button.setText("{hea_heart_fill}");
                option1Button.setSelected(true);
                option2Button.setSelected(true);
                option3Button.setSelected(true);
                voteToView.setVisibility(View.GONE);
                stackBar.setVisibility(View.VISIBLE);

            } else if (item.getIsVoted() == 3) {
                option1Button.setText("{hea_heart_fill}");
                option2Button.setText("{hea_heart_fill}");
                option3Button.setText("{hea_heart_fill}");
                option1Button.setSelected(true);
                option2Button.setSelected(true);
                option3Button.setSelected(true);
                voteToView.setVisibility(View.GONE);
                stackBar.setVisibility(View.VISIBLE);
            }

        }
    }

    protected String isProductLink(String path) {
        //path = "http://goldadorn.cloudapp.net/goldadorn_dev/gallery/earrings/GALINK_1_the_an_anshar_ring.jpg";

        if (path != null && path.equals("") == false && path.indexOf("/products/") != -1) {
            /*
            String link = path.substring(path.indexOf("/products/") + "/products/".length());
            link = link.substring(0,link.indexOf("."));
            try{
                String firstChar = link.substring(0,link.indexOf("_"));
                int val=Integer.parseInt(firstChar);
                link = link.substring(link.indexOf("_")+1);
            }
            catch (Exception e)
            {

            }
            if(link.indexOf("_")!=-1)
                link = link.replaceAll("_","-");
            */
            return path;

        }
        return null;
    }


    public void zoomImages(SocialPost socialPost, int index) {

        String imageURL = null;
        if (index == 0 && socialPost.getImage1loc() != null && socialPost.getImage1loc().equals("") == false)
            imageURL = socialPost.getImage1loc();
        else if (index == 1 && socialPost.getImage2loc() != null && socialPost.getImage2loc().equals("") == false)
            imageURL = socialPost.getImage2loc();
        else if (index == 2 && socialPost.getImage3loc() != null && socialPost.getImage3loc().equals("") == false)
            imageURL = socialPost.getImage3loc();


        //DJphy
        if (imageURL != null && isProductLink(imageURL) != null) {
            Log.d(Constants.TAG, "Image URL - zoomImages: " + imageURL);
            String id = /*imageURL.substring(imageURL.indexOf("/products/") + 10, imageURL.length());*/
                    String.valueOf(RandomUtils.getIdFromImageUrl(imageURL));
            //id = id.substring(0, id.indexOf("/"));
            Log.d(Constants.TAG, "ID - zoomImages: " + id);

            if (socialPost.getPostType() == SocialPost.POST_TYPE_NORMAL_POST) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: Normal post");
                logEventsAnalytics(GAAnalyticsEventNames.POST_NORMAL);
            } else if (socialPost.getPostType() == SocialPost.POST_TYPE_POLL) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: BONB post");
                logEventsAnalytics(GAAnalyticsEventNames.POST_BONB);
            } else if (socialPost.getPostType() == SocialPost.POST_TYPE_BEST_OF) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: BOT post");
                logEventsAnalytics(GAAnalyticsEventNames.POST_BOT);
            }

            String selection = Tables.Products._ID + "=?";
            String[] selArgs = new String[]{id.trim()};

            Cursor prodCursor = getContext().getContentResolver().query(Tables.Products.CONTENT_URI, null, selection, selArgs, null);
            if (prodCursor != null) {
                prodCursor.moveToFirst();
                Log.d(Constants.TAG, "cursor count- zoomImages: " + prodCursor.getCount());
                if (prodCursor.getCount() != 0) {
                    Product product = Product.extractFromCursor(prodCursor);
                    proceedToProductActivity(product);
                    return;
                }
            }
            productInfoFromServer(socialPost, id.trim());

            /*
            String profuctLink=URLHelper.getInstance().getWebSiteProductEndPoint()+isProductLink(imageURL)+".html";
            NavigationDataObject navigationDataObject =new NavigationDataObject(IDUtils.generateViewId(),"Our Collection",NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_CHROME,profuctLink,WebActivity.class);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
            */
        } else {
            NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(), "Best of", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ImageZoomActivity.class);
            Map<String, Object> data = new HashMap<>();
            data.put("IMAGES", socialPost.getImages());
            data.put("POST_ID", socialPost.getPostId());
            data.put("DETAILS", socialPost.getDescription());
            data.put("TYPE", socialPost.getPostType());
            data.put("INDEX", index);
            navigationDataObject.setParam(data);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
        }
    }


    private void gotoRecomendedProd(SocialPost socialPost, String imageURL) {

        //DJphy
        if (imageURL != null && isProductLink(imageURL) != null) {
            Log.d(Constants.TAG, "Image URL - gotoRecomendedProd: " + imageURL);
            String id = /*imageURL.substring(imageURL.indexOf("/products/") + 10, imageURL.length());*/
                    String.valueOf(RandomUtils.getIdFromImageUrl(imageURL));
            //id = id.substring(0, id.indexOf("/"));
            Log.d(Constants.TAG, "ID - gotoRecomendedProd: " + id);

            if (socialPost.getPostType() == SocialPost.POST_TYPE_NORMAL_POST) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: Normal post");
                logEventsAnalytics(GAAnalyticsEventNames.POST_NORMAL);
            } else if (socialPost.getPostType() == SocialPost.POST_TYPE_POLL) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: BONB post");
                logEventsAnalytics(GAAnalyticsEventNames.POST_BONB);
            } else if (socialPost.getPostType() == SocialPost.POST_TYPE_BEST_OF) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: BOT post");
                logEventsAnalytics(GAAnalyticsEventNames.POST_BOT);
            } else if (socialPost.getPostType() == SocialPost.POST_RECOMMENDED_PRODS) {
                Log.d(Constants.TAG_APP_EVENT, "AppEvent: RECOMMENDED Post click");
                logEventsAnalytics(GAAnalyticsEventNames.POST_RECO);
            }




            /*
            String profuctLink=URLHelper.getInstance().getWebSiteProductEndPoint()+isProductLink(imageURL)+".html";
            NavigationDataObject navigationDataObject =new NavigationDataObject(IDUtils.generateViewId(),"Our Collection",NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_CHROME,profuctLink,WebActivity.class);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
            */
        } else {
            /*NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(), "Best of", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ImageZoomActivity.class);
            Map<String, Object> data = new HashMap<>();
            data.put("IMAGES", socialPost.getImages());
            data.put("POST_ID", socialPost.getPostId());
            data.put("DETAILS", socialPost.getDescription());
            data.put("TYPE", socialPost.getPostType());
            data.put("INDEX", index);
            navigationDataObject.setParam(data);
            EventBus.getDefault().post(new AppActions(navigationDataObject));*/
        }
    }


    private void checkProdInDb(String prodId, SocialPost socialPost) {
        String selection = Tables.Products._ID + "=?";
        String[] selArgs = new String[]{prodId.trim()};

        Cursor prodCursor = getContext().getContentResolver().query(Tables.Products.CONTENT_URI, null, selection, selArgs, null);
        if (prodCursor != null) {
            prodCursor.moveToFirst();
            Log.d(Constants.TAG, "cursor count- gotoRecomendedProd: " + prodCursor.getCount());
            if (prodCursor.getCount() != 0) {
                Product product = Product.extractFromCursor(prodCursor);
                proceedToProductActivity(product);
                return;
            }
        }
        productInfoFromServer(socialPost, prodId.trim());
    }


    private void proceedToProductActivity(Product product) {
        if (getActivity() instanceof ProductActivity) {
            Toast.makeText(getContext(), "Please visit The " + product.name + " Product In Our Showcase", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = ProductActivity.getLaunchIntent(getActivity(), product);
        intent.putExtra(IntentKeys.CALLER_SOCIAL_FEED, true);
        startActivity(intent);
    }


    private Dialog displayOverlay(String infoMsg, int colorResId) {

        Dialog dialog = WindowUtils.getInstance(getApp()).displayOverlayLogo(getActivity(), null, 0,
                WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER); /*new Dialog(getActivity());
        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		*//*tempParams.width = dialogWidthInPx;
        tempParams.height = dialogHeightInPx;*//*
        tempParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = 0.0f;

        View overLay = LayoutInflater.from(getContext()).inflate(R.layout.dialog_overlay, null);
        TextView tvTemp = (TextView) overLay.findViewById(R.id.tvOverlayInfo);
        if (infoMsg != null) {
            tvTemp.setText(infoMsg);
            tvTemp.setTextColor(ResourceReader.getInstance(getContext()).getColorFromResource(colorResId));
        } else tvTemp.setVisibility(View.GONE);
        dialog.setContentView(overLay);
        dialog.setCancelable(false);

        dialog.getWindow().setAttributes(tempParams);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);*/
        return dialog;
    }


    public void getSpecificDesigner(final Product product, final Dialog dialog) {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, ApiKeys.getDesignerSocial(String.valueOf(product.userId)),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                Log.d(Constants.TAG, "response - getSpecificDesigner: " + json);
                boolean success = false;
                if (json != null) {
                    try {
                        String status = json.getString("status");
                        if (!TextUtils.isEmpty(status)) {
                            if (status.equalsIgnoreCase("success"))
                                success = true;
                            else success = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "No Product Details Available", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    if (success) {
                        final TimelineResponse response = new TimelineResponse();
                        response.success = true;
                        response.responseContent = json.toString();
                        Api.getDesignerEcom(Application.getInstance(), response, new IResultListener<TimelineResponse>() {
                            @Override
                            public void onResult(TimelineResponse result) {
                                dialog.dismiss();
                                if (result.success) {
                                    User mUser = UserInfoCache.getInstance(getContext()).getUserInfoDB(product.userId, true);
                                    if (mUser == null) {
                                        Toast.makeText(getContext(), "No Product Details Available", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    proceedToProductActivity(product);
                                }
                            }
                        });
                    }
                }else{
                    dialog.dismiss();
                    Toast.makeText(getContext(), "No Product Details Available", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Log.d(Constants.TAG, "volley error: ");
                        error.printStackTrace();
                        //genericInfo(Constants.ERR_MSG_1);
                        Toast.makeText(getContext(), "No Product Details Available", Toast.LENGTH_SHORT).show();
                        setErrSnackBar(Constants.ERR_MSG_NETWORK);
                    }
                });

        DefaultHttpClient httpclient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(Application.getInstance().getCookies().get(0));
        httpclient.setCookieStore(cookieStore);
        HttpStack httpStack = new HttpClientStack(httpclient);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext(), httpStack);
        requestQueue.add(req);


    }


    private void productInfoFromServer(final SocialPost socialPost, String productId) {

        final Dialog dialog = displayOverlay(null, R.color.colorAccent);
        dialog.show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, ApiKeys.ENDPOINT_PRODUCT_BASIC_INFO + productId,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(Constants.TAG, "response - productInfoFromServer: " + response);
                String status = "N/A";
                String message = "N/A";

                try {
                    /*status = response.getString(ApiKeys.STATUS);
                    message = response.getString(ApiKeys.MESSAGE);
                    Log.d(Constants.TAG, "status: " + status);
                    Log.d(Constants.TAG, "message: " + message);*/
                    //dialog.dismiss();
                    /*String json_string = new Gson().toJson(response);

                    Gson gson = new Gson();
                    ProductTemp productTemp = gson.fromJson(json_string, ProductTemp.class);*/
                    ProductTemp productTemp = RequestJson.parseProduct(response);
                    if (productTemp != null) {
                        Log.d(Constants.TAG, "productTemp - productInfoFromServer: " + productTemp.toString());
                        evaluateResults(productTemp, socialPost, dialog);
                    } else setErrSnackBar(Constants.ERR_MSG_1);
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dismiss();
                    //genericInfo(Constants.ERR_MSG_1);
                    setErrSnackBar(Constants.ERR_MSG_1);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d(Constants.TAG, "volley error: ");
                error.printStackTrace();
                //genericInfo(Constants.ERR_MSG_1);
                setErrSnackBar(Constants.ERR_MSG_NETWORK);
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(
                Constants.REQUEST_TIMEOUT_SOCIAL_LOGIN,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }


    private void setErrSnackBar(String errMsg) {

        if (mFloatingActionsMenu != null) {
            final Snackbar snackbar = Snackbar.make(mFloatingActionsMenu, errMsg,
                    Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }


    private void evaluateResults(ProductTemp productTemp, SocialPost socialPost, Dialog dialog) {

        try {
            Product product = Product.extractFromProductTemp(productTemp, socialPost.getIsLiked() == 1, socialPost.getLikeCount());
            User mUser = UserInfoCache.getInstance(getContext()).getUserInfoDB(product.userId, true);
            if (mUser == null) {
                getSpecificDesigner(product, dialog);
                //Toast.makeText(getContext(), "No Product Details Available", Toast.LENGTH_SHORT).show();
                return;
            }
            //startActivity(ProductActivity.getLaunchIntent(getActivity(), product));
            dialog.dismiss();
            proceedToProductActivity(product);
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(getContext(), "No Product Details Available", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public class NormalPostItemHolder extends PostItemHolder {

        @Bind(R.id.image)
        ImageView image;

        int widthPixels;

        @Bind(R.id.detailsHolder)
        RelativeLayout detailsHolder;

        @Bind(R.id.buyNow)
        View buyNow;


        @Bind(R.id.productCollectionLogo)
        ImageView productCollectionLogo;

        /*@Bind(R.id.prodPrice)
        TextView productPrice;*/

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == image && socialPost.getImg1loc() != null && socialPost.getImg1loc().url.trim().equals("") == false) {
                    zoomImages(socialPost, 0);
                }
            }
        };

        public NormalPostItemHolder(View itemView) {
            super(itemView);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            widthPixels = metrics.widthPixels;
            image.setOnClickListener(itemClick);

        }

        public void updatePostView(final SocialPost item, View view, int position) {
            Log.d("djfeed", "socialPost1 imageUrl - onBind(): " + socialPost.getImage1loc());
            updatePriceAndDiscount(item, false);
            if (item.getImg1loc() != null && item.getImg1loc().url.trim().equals("") == false) {
                detailsHolder.setVisibility(View.VISIBLE);
                if (isProductLink(item.getImage1loc()) != null) {
                    //productPrice.setText(item.getPrice1());
                    prodPrice.setVisibility(View.VISIBLE);
                    productCollectionLogo.setVisibility(View.VISIBLE);
                    buyNow.setVisibility(View.VISIBLE);
                } else {
                    productCollectionLogo.setVisibility(View.GONE);
                    prodPrice.setVisibility(View.GONE);
                    buyNow.setVisibility(View.GONE);
                }
                ImageLoaderUtils.loadImage(getContext(), item.getImg1loc(), image, R.drawable.vector_image_logo_square_100dp, true);
            } else {
                detailsHolder.setVisibility(View.GONE);
            }


        }

    }


    protected boolean allowPostOptions() {
        return true;
    }


    public class TrendingHashtagItemHolder extends RecommendedProductsItemHolder {

        SocialPost post;
        TrendingHashTagAdapter adapter;

        public TrendingHashtagItemHolder(View itemView, String title) {
            super(itemView, title);
        }

        TrendingHashTagAdapter.HashtagClick itemClickChild/* = new TrendingHashTagAdapter.HashtagClick() {
            @Override
            public void onItemClick(String hashtag) {
                //// TODO: 05-10-2016
                Toast.makeText(Application.getInstance(), "Hashtag: " + hashtag, Toast.LENGTH_SHORT).show();
            }
        }*/;

        @Override
        public void updateItemView(Object item, View view, int i) {
            //super.updateItemView(item, view, i);
            String rawTags = ((SocialPost) item).getTags();
            String[] tagArr = null;
            if (!TextUtils.isEmpty(rawTags))
                tagArr = ((SocialPost) item).getTags().trim().split("\\$-\\$-\\$");
            int index = 0;
            if (tagArr != null) {
                for (String str : tagArr) {
                    tagArr[index] = "#" + str;
                    index++;
                }
                List<String> list = Arrays.asList(tagArr);
                /*list.add("#earrings");
                list.add("#rings");
                list.add("#necklace");
                list.add("#diamonds");
                list.add("#chains");
                list.add("#nosepins");*/
                adapter.addList(list);
            }
        }

        @Override
        protected void initRecomendProdRecyclerView() {
            super.initRecomendProdRecyclerView();
            itemClickChild = new TrendingHashTagAdapter.HashtagClick() {
                @Override
                public void onItemClick(String hashtag) {
                    //// TODO: 05-10-2016
                    //Toast.makeText(Application.getInstance(), "Hashtag: " + hashtag, Toast.LENGTH_SHORT).show();
                    launchHashtagScreen(hashtag.trim().replace("#", ""));
                }
            };
            adapter = new TrendingHashTagAdapter(new ArrayList<String>(), itemClickChild);
            mRecyclerView.setAdapter(adapter);
        }
    }


    public class RecommendedProductsItemHolder extends BaseItemHolder {

        @Bind(R.id.headerHolder)
        View headerHolder;
        @Bind(R.id.socialElementsHolder)
        View socialElementsHolder;
        @Bind(R.id.detailsHolder)
        View detailsHolder;
        @Bind(R.id.recoItemHolder)
        View recoItemHolder;
        @Bind(R.id.recomandationLabel)
        TextView recomandationLabel;
        @Bind(R.id.recyclerView)
        RecyclerView mRecyclerView;
        /*@Bind(R.id.fabNext)
        android.support.design.widget.FloatingActionButton ivNextSetScroll;*/

        SocialPost socialPost;
        RecommendedProductsAdapter mRecoProdAdapter;

        RecommendedProductsAdapter.RecommendedPostClick itemClick = new RecommendedProductsAdapter.RecommendedPostClick() {
            @Override
            public void onRecommendedPostClick(String url, int isLiked, int likeCounts) {
                socialPost.setIsLiked(isLiked);
                socialPost.setLikeCount(likeCounts);
                gotoRecomendedProd(socialPost, url);
            }
        };

        /*int mCurrentPosition = 3;

        View.OnClickListener mClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* if (v.getId() == ivNextSetScroll.getId()) {
                    *//**//*if (mCurrentPosition == 9){
                        UiRandomUtils.collapse(ivNextSetScroll);
                    }else*//**//*
                    mRecyclerView.scrollToPosition(mCurrentPosition);
                    mCurrentPosition = mCurrentPosition + 3;
                    if (mCurrentPosition == 9){
                        mCurrentPosition = 3;
                    }
                }*//*
            }
        };*/

        @Override
        public void updateItemView(Object item, View view, int i) {
            try {
                socialPost = (SocialPost) item;
                mRecoProdAdapter.addList(/*getUrlList(*/socialPost.getRecoProducts()/*)*/);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*private List<String> getUrlList(List<RecommendedProduct> products) {
            List<String> urlList = new ArrayList<>();
            for (RecommendedProduct reco : products)
                urlList.add(ImageFilePath.getImageUrlForProduct(reco.getProductId()));
            return urlList;
        }*/

        public RecommendedProductsItemHolder(View itemView, String title) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
                initRecomendProdRecyclerView();
                headerHolder.setVisibility(View.GONE);
                socialElementsHolder.setVisibility(View.GONE);
                detailsHolder.setVisibility(View.GONE);
                recoItemHolder.setVisibility(View.VISIBLE);
                recomandationLabel.setText(title);
                //ivNextSetScroll.setOnClickListener(mClick);
                TypefaceHelper.setFont(recomandationLabel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LinearLayoutManager mLayoutManager;

        protected void initRecomendProdRecyclerView() {
            mRecyclerView.setHasFixedSize(false);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecoProdAdapter = new RecommendedProductsAdapter(new ArrayList<RecommendedProduct>(), itemClick);
            mRecyclerView.setAdapter(mRecoProdAdapter);
            /*int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.colorBlackDimText);
            ivNextSetScroll.setImageDrawable(new IconicsDrawable(Application.getInstance())
                    .icon(GoogleMaterial.Icon.gmd_keyboard_arrow_right)
                    .color(color)
                    .sizeDp(20));*/
            //setScrollUpdates();
        }

        /*private void setScrollUpdates() {
            mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean shouldRefresh = (mLayoutManager.findFirstCompletelyVisibleItemPosition() >= 5);
                    Log.d("djanim", "onScrolled- firstVisibleView Pos: "+mLayoutManager.findFirstCompletelyVisibleItemPosition());
                    if (shouldRefresh) {
                        Log.d("djanim", "collapse button");
                        UiRandomUtils.collapse(ivNextSetScroll);
                    }
                }
            });
        }*/
    }


    public class AddToCartPost extends NormalPostItemHolder {


        public AddToCartPost(View itemView) {
            super(itemView);
        }

        @Override
        public void updatePostView(SocialPost item, View view, int position) {
            super.updatePostView(item, view, position);
            //((TextView) view.findViewById(R.id.userName)).setText("New ATC type of post");
        }
    }


    public void launchHashtagScreen(String hashTag) {
        Intent intent = new Intent(getActivity(), HashTagResultActivity.class);
        intent.putExtra(IntentKeys.HASHTAG_NAME, hashTag);
        startActivity(intent);
    }

    abstract public class PostItemHolder extends BaseItemHolder {

        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;

        @Bind(R.id.age)
        TextView age;

        @Bind(R.id.details)
        TextView details;


        @Bind(R.id.followButton)
        Button followButton;

        @Bind(R.id.likesLabel)
        TextView likesLabel;

        @Bind(R.id.pollLabel)
        TextView pollLabel;


        @Bind(R.id.shareLabel)
        TextView shareLabel;

        @Bind(R.id.commentsLabel)
        TextView commentsLabel;

        @Bind(R.id.votePostButton)
        Button votePostButton;

        @Bind(R.id.likePostButton)
        Button likePostButton;

        @Bind(R.id.sharePostButton)
        Button sharePostButton;

        @Bind(R.id.commentPostButton)
        Button commentPostButton;

        @Bind(R.id.recomandationLabel)
        TextView recomandationLabel;

       /* @Bind(R.id.reco1)
        ImageView reco1;

        @Bind(R.id.reco2)
        ImageView reco2;

        @Bind(R.id.reco3)
        ImageView reco3;

        @Bind(R.id.reco4)
        ImageView reco4;*/

        @Bind(R.id.div)
        View recoDiv;

        @Bind(R.id.ivDropdown)
        ImageView ivDropdown;

        PopupMenu.OnMenuItemClickListener postOptionsClick = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d("djpost", "onMenuItemClick - item: " + item.getItemId());
                if (item.getItemId() == POST_OPTION_HIDE) {
                    getAppMainActivity().updatePostForThisUser(getAppMainActivity().POST_HIDE_CALL,
                            ((SocialPost) getDataManager().get(getAdapterPosition())).getPostId(), getAdapterPosition());
                } else if (item.getItemId() == POST_OPTION_DELETE) {
                    getAppMainActivity().updatePostForThisUser(getAppMainActivity().POST_DELETE_CALL,
                            ((SocialPost) getDataManager().get(getAdapterPosition())).getPostId(), getAdapterPosition());
                } else if (item.getItemId() == POST_OPTION_REPORT) {
                    getAppMainActivity().updatePostForThisUser(getAppMainActivity().POST_REPORT_CALL,
                            ((SocialPost) getDataManager().get(getAdapterPosition())).getPostId(), getAdapterPosition());
                }
                return true;
            }
        };

        PopupMenu.OnDismissListener postOptionDismiss = new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                ivDropdown.animate().rotation(0).start();
                Log.d("djpost", "onDismiss - postOptions");
            }
        };

        PopupMenu postMenu;
        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == userImage || v == userName) {
                    /*if (socialPost.getIsDesigner() == 1){
                        RandomUtils.launchDesignerScreen(getActivity(), socialPost.getUserId());
                        return;
                    }*/
                    People people = new People();
                    people.setUserName(socialPost.getUserName());
                    if (socialPost.getUserPic() != null)
                        people.setProfilePic(socialPost.getUserPic());
                    people.setFollowingCount(0);
                    people.setFollowerCount(0);
                    people.setIsSelf(socialPost.isSelf());
                    people.setUserId(socialPost.getUserId());
                    people.setIsDesigner(socialPost.getIsDesigner());
                    gotoUser(people);
                } else if (v.getId() == ivDropdown.getId()) {
                    ivDropdown.animate().rotation(-180).start();
                    //if (postMenu != null) postMenu.dismiss();
                    if (socialPost.isSelf()) {
                        updateDeleteItemToPopUp(true);
                    } else updateDeleteItemToPopUp(false);
                    postMenu.show();
                } else if (v == followButton) {
                    int isFollowing = socialPost.getIsFollowing();
                    isFollowing = isFollowing == 0 ? 1 : 0;
                    socialPost.setIsFollowing(isFollowing);
                    if (socialPost.getIsFollowing() == 1) {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                        //followButton.setSelected(true);
                    } else {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                        //followButton.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(followButton);
                    followPeople(socialPost, getAdapterPosition());
                } else if (v == likesLabel) {
                    gotoLikes(socialPost, false);


                } else if (v == shareLabel) {


                } else if (v == commentsLabel) {
                    gotoComments(socialPost, false, getAdapterPosition());
                } else if (v == commentPostButton) {
                    gotoComments(socialPost, true, getAdapterPosition());
                } else if (v == votePostButton) {

                } else if (v == likePostButton) {
                    int isLiked = socialPost.getIsLiked();
                    isLiked = isLiked == 0 ? 1 : 0;
                    if (isLiked == 0)
                        socialPost.setLikeCount(socialPost.getLikeCount() - 1);
                    else {
                        if (socialPost.getPostType() == SocialPost.POST_TYPE_NORMAL_POST) {
                            if (getActivity() instanceof MainActivity) {
                                if (isProductLink(socialPost.getImage1loc()) != null)
                                    ((MainActivity) getActivity()).displayDialogForIntimation();
                            }
                        }
                        socialPost.setLikeCount(socialPost.getLikeCount() + 1);
                    }
                    likesLabel.setText(socialPost.getLikeCount() + getActivity().getString(R.string.likesCountLabel));

                    socialPost.setIsLiked(isLiked);
                    if (socialPost.getIsLiked() == 1) {
                        likePostButton.setText(getActivity().getResources().getString(R.string.icon_liked_post));
                        likePostButton.setSelected(true);
                    } else {
                        likePostButton.setText(getActivity().getResources().getString(R.string.icon_likes_post));
                        likePostButton.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(likePostButton);

                    likeAPost(socialPost, getAdapterPosition());

                } else if (v == sharePostButton) {
                    /*String appPlayStoreURL = getString(R.string.palyStoreBasicURL) + getActivity().getPackageName();
                    Map<String, String> map = new HashMap<>();
                    map.put(Action.ATTRIBUTE_TITLE, getString(R.string.appShareTitle));
                    map.put(Action.ATTRIBUTE_DATA, getString(R.string.appShareBody) + appPlayStoreURL);
                    shareActionData.setParam(map);
                    if (getAppMainActivity() != null)
                        getAppMainActivity().action(shareActionData);*/
                    String discnt = socialPost.getDiscount1();
                    if (TextUtils.isEmpty(discnt))
                        discnt = "";
                    else if (discnt.equalsIgnoreCase("0.0"))
                        discnt = "";
                    else discnt = discnt + " off";
                    socialUtils.publishLinkPost(getActivity(), "http://www.goldadorn.com", "GoldAdorn Jewelry",
                            socialPost.getRange1() + discnt, socialPost.getImage1loc());
                }


            }
        };

        protected SocialPost socialPost;
        NavigationDataObject shareActionData;

        public PostItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            String appPlayStoreURL = getString(R.string.palyStoreBasicURL) + getActivity().getPackageName();
            shareActionData = new NavigationDataObject(IDUtils.generateViewId(), NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE, appPlayStoreURL);

            setUpPostOptions();
            userImage.setOnClickListener(itemClick);
            userName.setOnClickListener(itemClick);
            followButton.setOnClickListener(itemClick);
            likesLabel.setOnClickListener(itemClick);
            shareLabel.setOnClickListener(itemClick);
            commentsLabel.setOnClickListener(itemClick);
            votePostButton.setOnClickListener(itemClick);
            likePostButton.setOnClickListener(itemClick);
            sharePostButton.setOnClickListener(itemClick);
            commentPostButton.setOnClickListener(itemClick);
            ivDropdown.setOnClickListener(itemClick);
            ivDropdown.setVisibility(allowPostOptions() ? View.VISIBLE : View.INVISIBLE);

            //postIdMain = ((SocialPost) getDataManager().get(0)).getPostId();
            TypefaceHelper.setFont(userName, age, details, recomandationLabel, tvDiscountOnRed, prodPrice);

            TypefaceHelper.setFont(getResources().getString(R.string.font_name_text_secondary), likesLabel, pollLabel, commentsLabel, shareLabel);


            HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.staceColor2),
                    hashTagClick);
            mTextHashTagHelper.handle(details);
        }

        HashTagHelper.OnHashTagClickListener hashTagClick = new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                //Toast.makeText(Application.getInstance(),"hastag: "+hashTag, Toast.LENGTH_SHORT).show();
                if (isHashTagFunctionAllowed()) {
                    launchHashtagScreen(hashTag);
                }
            }
        };


        @Bind(R.id.discountHolder)
        View discountHolder;
        @Bind(R.id.tvDiscountOnRed)
        TextView tvDiscountOnRed;
        @Bind(R.id.prodPrice)
        TextView prodPrice;

        protected void updatePriceAndDiscount(SocialPost post, boolean isBot) {
            /*if (true){
                discountHolder.setVisibility(View.GONE);
                prodPrice.setVisibility(View.GONE);
                return;
            }*/
            if (isBot) {
                discountHolder.setVisibility(View.GONE);
                prodPrice.setVisibility(View.GONE);
                return;
            }
            double discount = -1;
            try {
                if (!TextUtils.isEmpty(post.getDiscount1()))
                    discount = Double.parseDouble(post.getDiscount1());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                discount = -1;
            }
            if (discount > 0) {//for discount
                discountHolder.setVisibility(View.VISIBLE);
                tvDiscountOnRed.setText(String.valueOf(Math.round(discount)) + "%\noff");
            } else {
                discountHolder.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(post.getRange1()) && !post.getRange1().equals("null")) {//price check here
                prodPrice.setVisibility(View.VISIBLE);
                prodPrice.setText(post.getRange1());
            } else {
                prodPrice.setVisibility(View.GONE);
            }
        }

        private final int POST_OPTION_HIDE = 1991;
        private final int POST_OPTION_DELETE = 1992;
        private final int POST_OPTION_REPORT = 1993;

        private void setUpPostOptions() {
            postMenu = new PopupMenu(ivDropdown.getContext(), ivDropdown);
            /*Menu menu = postMenu.getMenu();
            menu.add(Menu.NONE, POST_OPTION_HIDE, 1, "Hide");
            menu.add(Menu.NONE, POST_OPTION_REPORT, 2, "Report Inappropriate");*/
            postMenu.setOnMenuItemClickListener(postOptionsClick);
            postMenu.setOnDismissListener(postOptionDismiss);
        }

        private void updateDeleteItemToPopUp(boolean canshow) {
            if (canshow) {
                /*if (postMenu.getMenu().findItem(POST_OPTION_DELETE) == null) {*/
                postMenu.getMenu().clear();
                postMenu.getMenu().add(Menu.NONE, POST_OPTION_DELETE, Menu.NONE, "Delete");
                //}
            } else {
                //postMenu.getMenu().removeItem(POST_OPTION_DELETE);
                postMenu.getMenu().clear();
                Menu menu = postMenu.getMenu();
                menu.add(Menu.NONE, POST_OPTION_HIDE, 1, "Hide");
                menu.add(Menu.NONE, POST_OPTION_REPORT, 2, "Report Inappropriate");
            }
        }

        final public void updateItemView(Object item, View view, int position) {

            /*if (getAppMainActivity() instanceof MainActivity){
                getAppMainActivity().getFilterPanel().setVisibility(View.GONE);
            }*/
            socialPost = (SocialPost) item;
            userName.setText(socialPost.getUserName());
            votePostButton.setVisibility(View.GONE);
            shareLabel.setVisibility(View.GONE);
            age.setText(socialPost.getAge());

            pollLabel.setVisibility(View.GONE);
            details.setText(/*socialPost.getDescription()*/ EmojisHelper.getSpannedText(getContext(), socialPost.getDescription()));
            Log.d("djemoticon", "post Title: " + socialPost.getDescription());

            commentsLabel.setText(socialPost.getCommentCount() + " ");
            likesLabel.setText(socialPost.getLikeCount() + getActivity().getString(R.string.likesCountLabel));


            shareLabel.setText(socialPost.getShareCount() + getActivity().getString(R.string.shareCOuntLabel));
            Picasso.with(getContext())
                    .load(socialPost.getUserPic())
                    .tag(getContext())
                    .placeholder(R.drawable.vector_image_place_holder_profile_dark)
                    .resize(100, 100)
                    .into(userImage);

            if (getAppMainActivity() != null){
                getAppMainActivity().isShowFilterPanel(false);
            }
            updatePostView(socialPost, view, position);

            votePostButton.setText(getActivity().getResources().getString(R.string.icon_poll_post));
            /*if (socialPost.getIsVoted() == 1){
                votePostButton.setSelected(true);
            }else votePostButton.setSelected(false);*/
            //// TODO: 16-06-2016
            //if (socialPost.comment) //for comment needs to be rectified
            //if (socialPost.shar) // for share needs to be rectified
            if (socialPost.getIsLiked() == 1) {
                likePostButton.setText(getActivity().getResources().getString(R.string.icon_liked_post));
                likePostButton.setSelected(true);
            } else {
                likePostButton.setText(getActivity().getResources().getString(R.string.icon_likes_post));
                likePostButton.setSelected(false);
            }

            if (socialPost.isSelf()) {
                followButton.setVisibility(View.INVISIBLE);
            } else {
                followButton.setVisibility(View.VISIBLE);

                if (socialPost.getIsFollowing() == 1) {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                    //followButton.setSelected(true);
                } else {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                    //followButton.setSelected(false);
                }
            }


            /*List<com.goldadorn.main.model.Image> recommendation = socialPost.getRecommendation();
            List<ImageView> imageView = new ArrayList<>();
            imageView.add(reco1);
            imageView.add(reco2);
            imageView.add(reco3);
            imageView.add(reco4);*/


            //updateRecommendation(recommendation, imageView);
        }

        protected void updateRecommendation(List<com.goldadorn.main.model.Image> recommendation, List<ImageView> imageView) {
            if (recommendation != null && recommendation.size() != 0) {
                for (int i = 0; i < 4; i++) {
                    try {
                        com.goldadorn.main.model.Image img = recommendation.get(i);
                        if (img != null && img.url != null && img.url.equals("") == false) {
                            imageView.get(i).setVisibility(View.VISIBLE);
                            Picasso.with(getContext())
                                    .load(img.url)
                                    .tag(getContext())
                                    .resize(100, 100)
                                    .into(imageView.get(i));
                        } else
                            imageView.get(i).setVisibility(View.GONE);
                    } catch (Exception e) {
                        imageView.get(i).setVisibility(View.GONE);
                    }
                }
                recomandationLabel.setVisibility(View.VISIBLE);
                recoDiv.setVisibility(View.VISIBLE);
            } else {
                recomandationLabel.setVisibility(View.GONE);
                recoDiv.setVisibility(View.GONE);
            }
        }

        abstract public void updatePostView(SocialPost item, View view, int position);
    }


    @Override
    protected void onDataScroll(RecyclerView recyclerView, int dx, int dy) {
        super.onDataScroll(recyclerView, dx, dy);
        if (getAppMainActivity() != null){
            getAppMainActivity().isShowFilterPanel(false);
        }
    }

    public void updateComments() {
        if (commentSocialPost != null && commentPosition != -1 && CommentsActivity.commentCount != -1) {
            commentSocialPost.setCommentCount(CommentsActivity.commentCount);
            getAdapter().notifyItemChanged(commentPosition);
            commentSocialPost = null;
            commentPosition = -1;
            CommentsActivity.commentCount = -1;
        }
    }

    private void gotoComments(SocialPost socialPost, boolean b, int position) {
        CommentsActivity.commentCount = -1;
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(), "Comments", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, CommentsActivity.class);
        Map<String, Object> data = new HashMap<>();
        data.put("POST_ID", socialPost.getPostId());
        data.put("COMMENT", true);
        data.put("POST", b);
        navigationDataObject.setParam(data);
        commentSocialPost = socialPost;
        commentPosition = position;
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }

    private void gotoLikes(SocialPost socialPost, boolean b) {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),
                "Likes by", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, LikesActivity.class);
        Map<String, Object> data = new HashMap<>();
        data.put("POST_ID", socialPost.getPostId());
        data.put("POST", b);
        navigationDataObject.setParam(data);
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }

    public class RateAppItemHolder extends BaseItemHolder {
        @Bind(R.id.feedback)
        Button feedback;
        @Bind(R.id.nav_rate_us)
        Button nav_rate_us;
        @Bind(R.id.remove)
        Button remove;

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                Action action = new Action(getActivity());
                if (v == feedback) {
                    action.mailTo("mailto:soni94@gmail.com?subject=App Feedback&body=here is the feedback");
                } else if (v == remove) {

                } else if (v == nav_rate_us) {
                    NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_rate_us);
                    if (navigationDataObject != null)
                        getBaseActivity().action(navigationDataObject);
                }
                for (int i = 0; i < getDataManager().size(); i++) {
                    if (getDataManager().get(i) == item) {
                        getDataManager().remove(i);
                        break;
                    }
                }
            }
        };
        private Object item;

        public RateAppItemHolder(View itemView) {
            super(itemView);
            feedback.setOnClickListener(itemClick);
            nav_rate_us.setOnClickListener(itemClick);
            remove.setOnClickListener(itemClick);
        }

        public void updateItemView(Object item, View view, int position) {
            this.item = item;
        }
    }


    public static class SocilFeedResult extends CodeDataParser {
        List<SocialPost> feeds;
        Object data;
        private String status;
        private int offset;

        public int getPageSize() {
            return offset;
        }

        public List<?> getList() {
            return feeds;
        }

        public Object getData() {
            return this;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getStatus() {
            return status;
        }

        public int getOffset() {
            return offset;
        }
    }
}