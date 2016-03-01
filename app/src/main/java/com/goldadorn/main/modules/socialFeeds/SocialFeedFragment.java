package com.goldadorn.main.modules.socialFeeds;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.CommentsActivity;
import com.goldadorn.main.activities.ImageZoomActivity;
import com.goldadorn.main.activities.LikesActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.VotersActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.icons.GoldadornIconFont;
import com.goldadorn.main.icons.HeartIconFont;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.socialFeeds.helper.FollowHelper;
import com.goldadorn.main.modules.socialFeeds.helper.LikeHelper;
import com.goldadorn.main.modules.socialFeeds.helper.PostUpdateHelper;
import com.goldadorn.main.modules.socialFeeds.helper.SelectHelper;
import com.goldadorn.main.modules.socialFeeds.helper.VoteHelper;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.ImageLoaderUtils;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class SocialFeedFragment extends DefaultVerticalListView
{
    protected boolean isRefreshingData=false;
    protected int offset=0;
    protected int refreshOffset=0;

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
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
        public void onFail(PostUpdateHelper host,SocialPost post, int pos) {
            if(host instanceof LikeHelper)
            {
                int isLiked = post.getIsLiked();
                isLiked = isLiked==0?1:0;
                if(isLiked==0)
                    post.setLikeCount(post.getLikeCount()-1);
                else
                    post.setLikeCount(post.getLikeCount()+1);
                post.setIsLiked(isLiked);
                getAdapter().notifyItemChanged(pos);
            }
            else if(host instanceof VoteHelper)
            {
                post.setIsVoted(0);
                getAdapter().notifyItemChanged(pos);
            }
            else if(host instanceof SelectHelper)
            {
                post.setIsVoted(0);
                getAdapter().notifyItemChanged(pos);
            }
            if(host instanceof FollowHelper)
            {
                int isFollowing = post.getIsFollowing();
                isFollowing = isFollowing==0?1:0;
                post.setIsFollowing(isFollowing);
                getAdapter().notifyItemChanged(pos);
            }

        }
        @Override
        public void onSuccess(PostUpdateHelper host,SocialPost post, int pos) {
            if(host instanceof LikeHelper)
                getAdapter().notifyItemChanged(pos);
            else if(host instanceof VoteHelper)
                getAdapter().notifyItemChanged(pos);
            else if(host instanceof SelectHelper)
                getAdapter().notifyItemChanged(pos);
            else if(host instanceof FollowHelper)
                getAdapter().notifyItemChanged(pos);
        }
    };
    private void likeAPost(SocialPost post,int pos) {
        likeHelper.update(post, pos);
    }
    private void voteAPost(SocialPost post,int pos) {
        voteHelper.update(post, pos);
    }
    private void selectAPost(SocialPost post,int pos) {
        selectHelper.update(post, pos);
    }
    private void gotoVotes(SocialPost socialPost,int pos) {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),"Votes by", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, VotersActivity.class);
        Map<String, Object> data= new HashMap<>();
        data.put("POST_ID",socialPost.getPostId());
        navigationDataObject.setParam(data);
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }

    private void followPeope(SocialPost post,int pos) {
        followHelper.update(post, pos);
    }

    public boolean allowedBack() {
        if(mFloatingActionsMenu.isExpanded()) {
            closeMenu();
            return false;
        }
        else
            return super.allowedBack();
    }
    protected DataManager createDataManager()
    {
        return new SocialFeedProjectDataManager(getActivity(),this,getApp().getCookies());
    }
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, offset);
        params.put(URLHelper.LIKE_A_POST.POST_ID, 0);

        return params;
    }
    public Map<String, Object> getRefreshDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, refreshOffset);
        SocialPost sp =(SocialPost)getDataManager().get(0);
        params.put(URLHelper.LIKE_A_POST.POST_ID, sp.getPostId());
        return params;
    }

    public String getNextDataURL(PageData pageData)
    {
        isRefreshingData= false;
        return getApp().getUrlHelper().getSocialFeedServiceURL();
    }
    public String getRefreshDataURL(PageData pageData)
    {
        isRefreshingData= true;
        return getApp().getUrlHelper().getSocialFeedServiceURL();
    }

    public class SocialFeedProjectDataManager extends DefaultProjectDataManager
    {
        public SocialFeedProjectDataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
        {
            super(context,delegate,cookies);
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
        protected void updatePagingData(BaseDataParser loadedDataVO)
        {
            try
            {

                offset=loadedDataVO.getPageSize();
                pageData.curruntPage +=1;
                pageData.totalPage +=1;
            }catch (Exception e)
            {
                pageData.curruntPage=pageData.totalPage=1;
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

    @OnClick(R.id.refreshTriger)void onRefreshTrigerClick()
    {
        refreshPosts();
        fadeOutRefreshTriger();

    }

    private void fadeOutRefreshTriger() {
        if(refreshTrigerhandler!=null)
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


    @OnClick(R.id.post)void onPostClick()
    {
        EventBus.getDefault().post(new com.goldadorn.main.eventBusEvents.SocialPost(SocialPost.POST_TYPE_NORMAL_POST,this));
        closeMenu();
    }

    @OnClick(R.id.poll)void onPollClick()
    {
        EventBus.getDefault().post(new com.goldadorn.main.eventBusEvents.SocialPost(SocialPost.POST_TYPE_POLL,this));
        closeMenu();
    }

    @OnClick(R.id.bestof)void onBestofClick()
    {
        EventBus.getDefault().post(new com.goldadorn.main.eventBusEvents.SocialPost(SocialPost.POST_TYPE_BEST_OF, this));
        closeMenu();
    }

    View disableApp;
    View disableApp1;
    @Bind(R.id.disableApp)
    View disableAppCover;


    @Override
    public void garbageCollectorCall()
    {
        super.garbageCollectorCall();
        disableApp=null;
        disableAppCover=null;
        refreshTriger=null;
        disableApp1=null;
        if(likeHelper!=null)
            likeHelper.clear();
        likeHelper=null;
        if(voteHelper!=null)
            voteHelper.clear();
        voteHelper=null;

        if(selectHelper!=null)
            selectHelper.clear();
        selectHelper=null;

    }
    @OnClick(R.id.disableApp)void onDisableAppCover()
    {

        closeMenu();
    }
    public void onViewCreated(View view) {
        ButterKnife.bind(this, view);

        if(getAppMainActivity()!=null) {
            disableApp = getAppMainActivity().getDisableApp();
            if(disableApp!=null)
            {
                disableApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeMenu();
                    }
                });
            }
        }

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

        likeHelper = new LikeHelper(getActivity(), getApp().getCookies(),postUpdateResult);
        voteHelper= new VoteHelper(getActivity(), getApp().getCookies(),postUpdateResult);
        selectHelper= new SelectHelper(getActivity(), getApp().getCookies(),postUpdateResult);
        followHelper= new FollowHelper(getActivity(), getApp().getCookies(),postUpdateResult);
    }

    public void closeMenu()
    {
        if (mFloatingActionsMenu.isExpanded())
            mFloatingActionsMenu.collapse();

        if(disableApp!=null) {
            YoYo.with(Techniques.FadeOut).duration(500).withListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator var1) {

                }

                public void onAnimationEnd(Animator var1) {
                    disableApp.setVisibility(View.GONE);
                    if(disableAppCover!=null)
                        disableAppCover.setVisibility(View.GONE);
                    if(disableApp1!=null)
                        disableApp1.setVisibility(View.GONE);
                }

                public void onAnimationCancel(Animator var1) {

                }

                public void onAnimationRepeat(Animator var1) {

                }
            }).playOn(disableApp);

            YoYo.with(Techniques.FadeOut).duration(500).playOn(disableAppCover);

            if(disableApp1!=null)
                YoYo.with(Techniques.FadeOut).duration(500).playOn(disableApp1);

        }
    }



    protected View createRootView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(getDataManager().getRefreshEnabled())
            return inflater.inflate(R.layout.social_feed_fragment_recycler_with_swipe_refresh_layout, container, false);
        else
            return inflater.inflate(R.layout.social_feed_fragment_recycler, container, false);
    }

    protected MainActivity getBaseActivity() {
        return (MainActivity)getActivity();
    }

    public void postAdded(SocialPost socialPost) {
        refreshPosts(0);
        //askToRefresh();
    }
    Handler refreshTrigerhandler = new Handler();
    Runnable runnablelocal = new Runnable() {
        @Override
        public void run() {
            fadeOutRefreshTriger();
        }
    };
    public void askToRefresh()
    {
        if(refreshTriger!=null)
        {
            refreshTriger.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn).duration(500).playOn(refreshTriger);

            if(refreshTrigerhandler!=null)
                refreshTrigerhandler.removeCallbacks(runnablelocal);
            refreshTrigerhandler.postDelayed(runnablelocal,7000);
        }
    }
    public void refreshPosts()
    {
        refreshPosts(0);
    }
    protected void loadRefreshData() {
        super.loadRefreshData();
    }
    public void refreshPosts(int offset)
    {
        refreshOffset =offset;
        if(getSwipeRefreshLayout()!=null)
        {
            if(getDataManager().canLoadRefresh()) {
                loadRefreshData();
            } else {
                getSwipeRefreshLayout().setRefreshing(false);
            }
            getSwipeRefreshLayout().setEnabled(getDataManager().hasScopeOfRefresh());
        }
        else if(getDataManager().canLoadRefresh() && getDataManager().hasScopeOfRefresh())
            loadRefreshData();
    }
    protected void configSwipeRefreshLayout(SwipeRefreshLayout view) {
        super.configSwipeRefreshLayout(view);
        if(view != null) {
            view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    if(getDataManager().canLoadRefresh()) {
                        refreshOffset=0;
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
    }

    public void addDisableColver(View disableApp) {
        disableApp1 = disableApp;
        if(disableApp1!=null)
        {
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
        public static final int VIEW_RATE_US= 20;
    }

    public static boolean reateItemAdded=false;
    public void onCallEnd(List<?> dataList, boolean isRefreshData) {
        super.onCallEnd(dataList, isRefreshData);
        if(!reateItemAdded)
        {
            if(getDataManager().size()>4)
            {
                //RateApp rateApp = new RateApp();
            //    getDataManager().add(getDataManager().size()-2,rateApp);
                //reateItemAdded=true;
            }
        }
    }
    public static class RateApp
    {

    }

    public void onItemClick(Object baseObject)
    {
        super.onItemClick(baseObject);
       // Toast.makeText(getActivity(), baseObject.toString(), Toast.LENGTH_SHORT).show();
    }
    public int getListItemViewType(int position,Object item)
    {
        if(item instanceof RateApp) {
            return ViewTypes.VIEW_RATE_US;
        }
        else
        {
            SocialPost post = (SocialPost) item;
            if (post.getPostType() == SocialPost.POST_TYPE_BEST_OF)
                return ViewTypes.VIEW_BEST_OF;
            else if (post.getPostType() == SocialPost.POST_TYPE_POLL)
                return ViewTypes.VIEW_POLL;
            else
                return ViewTypes.VIEW_NORMAL;
        }

    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {

        if(viewType== ViewTypes.VIEW_RATE_US)
            return inflater.inflate(R.layout.rate_app_card,null);
        else if(viewType== ViewTypes.VIEW_POLL)
            return inflater.inflate(R.layout.social_post_poll_item,null);
        else if(viewType== ViewTypes.VIEW_BEST_OF)
            return inflater.inflate(R.layout.social_post_best_of_item,null);
        return inflater.inflate(R.layout.social_post_item,null);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        if(viewType== ViewTypes.VIEW_RATE_US)
            return new RateAppItemHolder(view);
        else if(viewType== ViewTypes.VIEW_POLL)
            return new PollPostItemHolder(view);
        else if(viewType== ViewTypes.VIEW_BEST_OF)
            return new BestOfPostItemHolder(view);
        return new NormalPostItemHolder(view);
    }



    public Class getLoadedDataParsingAwareClass()
    {
        return SocilFeedResult.class;
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


        private boolean isVoted(SocialPost socialPost) {
            if(socialPost.getIsVoted()!=0)
            {
                Toast.makeText(getActivity(), "You can only vote once. You cannot change your vote, once voted", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }


        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == buy) {
                    if(!isVoted(socialPost)) {
                        socialPost.setIsVoted(1);
                        buy.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(buy);
                        buy.setSelected(true);
                        notBuy.setSelected(false);
                        voteAPost(socialPost, position);
                    }
                }
                else if (v == notBuy) {
                    if(!isVoted(socialPost)) {
                        socialPost.setIsVoted(2);
                        notBuy.setText("{hea_broken_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(notBuy);
                        buy.setSelected(false);
                        notBuy.setSelected(true);
                        voteAPost(socialPost, position);
                    }

                }
                else if (v == image) {
                    zoomImages(socialPost,0);
                }
                else if (v == votePostButton) {
                    gotoVotes(socialPost, position);
                }
                else if (v == pollLabel) {
                    gotoVotes(socialPost, position);
                }

            }
        };
        public PollPostItemHolder(View itemView)
        {
            super(itemView);
            buy.setOnClickListener(itemClick);
            notBuy.setOnClickListener(itemClick);
            pollLabel.setOnClickListener(itemClick);
            votePostButton.setOnClickListener(itemClick);
            image.setOnClickListener(itemClick);
        }
        public void updatePostView(SocialPost item,View view,int position)
        {
            pollLabel.setVisibility(View.VISIBLE);
            votePostButton.setVisibility(View.VISIBLE);
            pollLabel.setText(getActivity().getString(R.string.voteCountLabel) + item.getVoteCount() + "");

            if(item.getImg1()!=null && item.getImg1().url.trim().equals("")==false)
            {
                ImageLoaderUtils.loadImage(getContext(), item.getImg1(), image, R.drawable.vector_image_logo_square_100dp);
                detailsHolder.setVisibility(View.VISIBLE);
            }
            else {
                detailsHolder.setVisibility(View.GONE);
            }

            buyLabel.setText("Buy: "+item.getYesPercent()+"%");
            notBuyLabel.setText("Not Buy: " + item.getNoPercent() + "%");

            if(item.getIsVoted()==0)
            {
                buy.setText("{hea_heart_out_line}");
                notBuy.setText("{hea_broken_heart_out_line}");
                buy.setSelected(false);
                notBuy.setSelected(false);
            }
            else if(item.getIsVoted()==1)
            {
                buy.setText("{hea_heart_fill}");
                notBuy.setText("{hea_broken_heart_out_line}");
                buy.setSelected(true);
                notBuy.setSelected(false);
            }
            else if (item.getIsVoted() == 2) {
                buy.setText("{hea_heart_out_line}");
                notBuy.setText("{hea_broken_heart_fill}");
                buy.setSelected(false);
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

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == option1Button) {
                    if(!isVoted(socialPost))
                    {
                        socialPost.setIsVoted(1);
                        option1Button.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(option1Button);
                        option1Button.setSelected(true);
                        option2Button.setSelected(false);
                        option3Button.setSelected(false);
                        selectAPost(socialPost, position);
                    }
                }
                else if (v == option2Button) {
                    if(!isVoted(socialPost))
                    {
                        socialPost.setIsVoted(2);
                        option2Button.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(option2Button);
                        option1Button.setSelected(false);
                        option2Button.setSelected(true);
                        option3Button.setSelected(false);

                        selectAPost(socialPost, position);
                    }
                }
                else if (v == option3Button) {
                    if(!isVoted(socialPost))
                    {
                        socialPost.setIsVoted(3);
                        option3Button.setText("{hea_heart_fill}");
                        YoYo.with(Techniques.Landing).duration(300).playOn(option3Button);
                        option1Button.setSelected(false);
                        option2Button.setSelected(false);
                        option3Button.setSelected(true);
                        selectAPost(socialPost, position);
                    }
                }
                else if (v == option1Image) {
                    zoomImages(socialPost,0);
                }
                else if (v == option2Image) {
                    zoomImages(socialPost,1);
                }
                else if (v == option3Image) {
                    zoomImages(socialPost,2);
                }
                else if (v == votePostButton) {
                    gotoVotes(socialPost, position);
                }
                else if (v == pollLabel) {
                    gotoVotes(socialPost, position);
                }
            }
        };

        private boolean isVoted(SocialPost socialPost) {
            if(socialPost.getIsVoted()!=0)
            {
                Toast.makeText(getActivity(), "You can only vote once. You cannot change your vote, once voted", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }


        public BestOfPostItemHolder(View itemView)
        {
            super(itemView);
            option1Button.setOnClickListener(itemClick);
            option2Button.setOnClickListener(itemClick);
            option3Button.setOnClickListener(itemClick);
            option1Image.setOnClickListener(itemClick);
            option2Image.setOnClickListener(itemClick);
            option3Image.setOnClickListener(itemClick);
            pollLabel.setOnClickListener(itemClick);
            votePostButton.setOnClickListener(itemClick);
        }
        public void updatePostView(SocialPost item,View view,int position)
        {

            pollLabel.setVisibility(View.VISIBLE);
            votePostButton.setVisibility(View.VISIBLE);
            pollLabel.setText(getActivity().getString(R.string.voteCountLabel) + item.getVoteCount() + "");

            detailsHolder.setVisibility(View.VISIBLE);
            if(item.getImg1()!=null && item.getImg1().url.trim().equals("")==false)
            {
                ImageLoaderUtils.loadImage(getContext(), item.getImg1(), option1Image, R.drawable.vector_image_logo_square_100dp);
                optionBox1.setVisibility(View.VISIBLE);
                option1Label.setText(item.getBof3Percent1()+"%");
            }
            else
                optionBox1.setVisibility(View.GONE);

            if(item.getImg2()!=null && item.getImg2().url.trim().equals("")==false)
            {
                ImageLoaderUtils.loadImage(getContext(), item.getImg2(), option2Image, R.drawable.vector_image_logo_square_100dp);
                optionBox2.setVisibility(View.VISIBLE);
                option2Label.setText(item.getBof3Percent2() + "%");
            }
            else
                optionBox2.setVisibility(View.GONE);

            if(item.getImg3()!=null && item.getImg3().url.trim().equals("")==false)
            {
                ImageLoaderUtils.loadImage(getContext(), item.getImg3(), option3Image, R.drawable.vector_image_logo_square_100dp);
                optionBox3.setVisibility(View.VISIBLE);
                option3Label.setText(item.getBof3Percent3()+"%");
            }
            else
                optionBox3.setVisibility(View.GONE);


            if(item.getIsVoted()==0)
            {
                option1Button.setText("{hea_heart_out_line}");
                option2Button.setText("{hea_heart_out_line}");
                option3Button.setText("{hea_heart_out_line}");

                option1Button.setSelected(false);
                option2Button.setSelected(false);
                option3Button.setSelected(false);
            }
            else if (item.getIsVoted()==1)
            {
                option1Button.setText("{hea_heart_fill}");
                option2Button.setText("{hea_heart_out_line}");
                option3Button.setText("{hea_heart_out_line}");
                option1Button.setSelected(true);
                option2Button.setSelected(false);
                option3Button.setSelected(false);

            }
            else if (item.getIsVoted()==2)
            {
                option1Button.setText("{hea_heart_out_line}");
                option2Button.setText("{hea_heart_fill}");
                option3Button.setText("{hea_heart_out_line}");
                option1Button.setSelected(false);
                option2Button.setSelected(true);
                option3Button.setSelected(false);

            }
            else if (item.getIsVoted()==3)
            {
                option1Button.setText("{hea_heart_out_line}");
                option2Button.setText("{hea_heart_out_line}");
                option3Button.setText("{hea_heart_fill}");
                option1Button.setSelected(false);
                option2Button.setSelected(false);
                option3Button.setSelected(true);
            }

        }
    }

    private void zoomImages(SocialPost socialPost,int index) {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),"Best of", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ImageZoomActivity.class);
        Map<String, Object> data= new HashMap<>();
        data.put("IMAGES", socialPost.getImages());
        data.put("POST_ID", socialPost.getPostId());
        data.put("DETAILS", socialPost.getDescription());
        data.put("TYPE", socialPost.getPostType());
        data.put("INDEX", index);

        //data.put("VOTES", socialPost.getVotes().toString());
        //data.put("TOTAL_VOTES", socialPost.getVoteCount());
        navigationDataObject.setParam(data);
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }

    public class NormalPostItemHolder extends PostItemHolder {

        @Bind(R.id.image)
        ImageView image;

        int widthPixels;

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == image && socialPost.getImg1()!=null && socialPost.getImg1().url.trim().equals("")==false){

                    zoomImages(socialPost,0);
                }
            }
        };

        public NormalPostItemHolder(View itemView)
        {
            super(itemView);
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            widthPixels = metrics.widthPixels;
            image.setOnClickListener(itemClick);

        }
        public void updatePostView(final SocialPost item,View view,int position)
        {
            if(item.getImg1()==null || item.getImg1().url.trim().equals(""))
                image.setVisibility(View.GONE);
            else {
                image.setVisibility(View.VISIBLE);
                ImageLoaderUtils.loadImage(getContext(), item.getImg1(), image, R.drawable.vector_image_logo_square_100dp);
            }
        }

    }

    abstract public class PostItemHolder extends BaseItemHolder{

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

        @Bind(R.id.reco1)
        ImageView reco1;

        @Bind(R.id.reco2)
        ImageView reco2;

        @Bind(R.id.reco3)
        ImageView reco3;

        @Bind(R.id.reco4)
        ImageView reco4;

        @Bind(R.id.div)
        View recoDiv;

        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(v==userImage)
                {
                    People people = new People();
                    people.setUserName(socialPost.getUserName());
                    people.setProfilePic(socialPost.getUserPic());
                    people.setFollowingCount(0);
                    people.setFollowerCount(0);
                    people.setIsSelf(socialPost.isSelf());
                    people.setIsDesigner(socialPost.getIsDesigner());
                    gotoUser(people);
                }
                else if(v==userName)
                {
                    People people = new People();
                    people.setUserName(socialPost.getUserName());
                    people.setProfilePic(socialPost.getUserPic());
                    people.setFollowingCount(0);
                    people.setFollowerCount(0);
                    people.setIsSelf(socialPost.isSelf());
                    people.setIsDesigner(socialPost.getIsDesigner());
                    gotoUser(people);
                }
                else if(v==followButton)
                {
                    int isFollowing = socialPost.getIsFollowing();
                    isFollowing = isFollowing==0?1:0;
                    socialPost.setIsFollowing(isFollowing);
                    if(socialPost.getIsFollowing()==1) {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                        //followButton.setSelected(true);
                    }
                    else {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                        //followButton.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(followButton);
                    followPeope(socialPost, position);
                }
                else if(v==likesLabel)
                {
                    gotoLikes(socialPost,false);


                }
                else if(v==shareLabel)
                {

                }
                else if(v==commentsLabel)
                {
                    gotoComments(socialPost,false);
                }
                else if(v==commentPostButton)
                {
                    gotoComments(socialPost,true);
                }
                else if(v==votePostButton)
                {

                }
                else if(v==likePostButton)
                {
                    int isLiked = socialPost.getIsLiked();
                    isLiked = isLiked==0?1:0;
                    if(isLiked==0)
                        socialPost.setLikeCount(socialPost.getLikeCount()-1);
                    else
                        socialPost.setLikeCount(socialPost.getLikeCount() + 1);
                    likesLabel.setText(getActivity().getString(R.string.likesCountLabel) + socialPost.getLikeCount() + "");

                    socialPost.setIsLiked(isLiked);
                    if(socialPost.getIsLiked()==1) {
                        likePostButton.setText(getActivity().getResources().getString(R.string.icon_liked_post));
                        likePostButton.setSelected(true);
                    }
                    else {
                        likePostButton.setText(getActivity().getResources().getString(R.string.icon_likes_post));
                        likePostButton.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(likePostButton);

                    likeAPost(socialPost, position);

                }
                else if(v==sharePostButton)
                {
                    String appPlayStoreURL=getString(R.string.palyStoreBasicURL)+ getActivity().getPackageName();
                    Map<String,String> map = new HashMap<>();
                    map.put(Action.ATTRIBUTE_TITLE,getString(R.string.appShareTitle));
                    map.put(Action.ATTRIBUTE_DATA, getString(R.string.appShareBody) + appPlayStoreURL);
                    shareActionData.setParam(map);
                    getAppMainActivity().action(shareActionData);
                }


            }
        };
        protected SocialPost socialPost;
        NavigationDataObject shareActionData;
        public PostItemHolder(View itemView)
        {
            super(itemView);

            String appPlayStoreURL=getString(R.string.palyStoreBasicURL)+ getActivity().getPackageName();
            shareActionData=new NavigationDataObject(IDUtils.generateViewId(), NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE, appPlayStoreURL);


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

        }
        final public void updateItemView(Object item,View view, int position) {
            socialPost =(SocialPost)item;
            userName.setText(socialPost.getUserName());
            votePostButton.setVisibility(View.GONE);
            shareLabel.setVisibility(View.GONE);
            age.setText(socialPost.getAge());

            pollLabel.setVisibility(View.GONE);
            details.setText(socialPost.getDescription());

            commentsLabel.setText(getActivity().getString(R.string.commentCountLabel)+socialPost.getCommentCount()+"");
            likesLabel.setText(getActivity().getString(R.string.likesCountLabel) + socialPost.getLikeCount() + "");
            shareLabel.setText(getActivity().getString(R.string.shareCOuntLabel) + 12 + "");
            Picasso.with(getContext())
                    .load(socialPost.getUserPic())
                    .tag(getContext())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .resize(100,100)
                    .into(userImage);
            updatePostView(socialPost, view, position);


            if(socialPost.getIsLiked()==1) {
                likePostButton.setText(getActivity().getResources().getString(R.string.icon_liked_post));
                likePostButton.setSelected(true);
            }
            else {
                likePostButton.setText(getActivity().getResources().getString(R.string.icon_likes_post));
                likePostButton.setSelected(false);
            }

            if(socialPost.isSelf()) {
                followButton.setVisibility(View.INVISIBLE);
            }
            else {
                followButton.setVisibility(View.VISIBLE);

                if(socialPost.getIsFollowing()==1) {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                    //followButton.setSelected(true);
                }
                else {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                    //followButton.setSelected(false);
                }
            }


            List<com.goldadorn.main.model.Image> recommendation=socialPost.getRecommendation();
            List<ImageView> imageView=new ArrayList<>();
            imageView.add(reco1);
            imageView.add(reco2);
            imageView.add(reco3);
            imageView.add(reco4);


            updateRecommendation(recommendation, imageView);
        }

        protected void updateRecommendation(List<com.goldadorn.main.model.Image> recommendation, List<ImageView> imageView) {
            if(recommendation!=null && recommendation.size()!=0)
            {
                for (int i = 0; i < 4; i++) {
                    try
                    {
                        com.goldadorn.main.model.Image img =recommendation.get(i);
                        if(img!=null && img.url!=null && img.url.equals("")==false)
                        {
                            imageView.get(i).setVisibility(View.VISIBLE);
                            Picasso.with(getContext())
                                    .load(img.url)
                                    .tag(getContext())
                                    .resize(100,100)
                                    .into(imageView.get(i));
                        }
                        else
                            imageView.get(i).setVisibility(View.GONE);
                    }catch(Exception e)
                    {
                        imageView.get(i).setVisibility(View.GONE);
                    }
                }
                recomandationLabel.setVisibility(View.VISIBLE);
                recoDiv.setVisibility(View.VISIBLE);
            }
            else {
                recomandationLabel.setVisibility(View.GONE);
                recoDiv.setVisibility(View.GONE);
            }
        }

        abstract public void updatePostView(SocialPost item,View view,int position);
    }

    private void gotoComments(SocialPost socialPost, boolean b) {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),"Comments", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, CommentsActivity.class);
        Map<String, Object> data= new HashMap<>();
        data.put("POST_ID",socialPost.getPostId());
        data.put("POST",b);
        navigationDataObject.setParam(data);
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }
    private void gotoLikes(SocialPost socialPost, boolean b) {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),"Likes by", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, LikesActivity.class);
        Map<String, Object> data= new HashMap<>();
        data.put("POST_ID",socialPost.getPostId());
        data.put("POST",b);
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

        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Action action =new Action(getActivity());
                if(v==feedback)
                {
                    action.mailTo("mailto:soni94@gmail.com?subject=App Feedback&body=here is the feedback");
                }
                else if(v==remove)
                {

                }
                else if(v==nav_rate_us)
                {
                    NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_rate_us);
                    if(navigationDataObject !=null)
                        getBaseActivity().action(navigationDataObject);
                }
                for (int i = 0; i < getDataManager().size(); i++) {
                    if(getDataManager().get(i)==item)
                    {
                        getDataManager().remove(i);
                        break;
                    }
                }
            }
        };
        private Object item;

        public RateAppItemHolder(View itemView)
        {
            super(itemView);
            feedback.setOnClickListener(itemClick);
            nav_rate_us.setOnClickListener(itemClick);
            remove.setOnClickListener(itemClick);
        }

        public void updateItemView(Object item,View view,int position)
        {
            this.item=item;
        }
    }


    public static class SocilFeedResult extends CodeDataParser
    {
        List<SocialPost> feeds;
        Object data;
        private String status;
        private int offset;
        public int getPageSize(){return offset;}
        public List<?> getList()
        {
            return feeds;
        }
        public Object getData()
        {
            return this;
        }
        public void setData(Object data)
        {
            this.data=data;
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