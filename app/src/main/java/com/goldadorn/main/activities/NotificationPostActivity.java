package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.NotificationDataObject;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.eventBusEvents.AppActions;
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
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationPostActivity extends BaseActivity{

    @Bind(R.id.rlChild)
    RelativeLayout childLayout;

    private NotificationDataObject mNotificationDataObj;
    static int postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_post);
        ButterKnife.bind(this);

        mNotificationDataObj = getIntent().getParcelableExtra(IntentKeys.NOTIFICATION_OBJ);
        try {
            Log.d("djfeed","postId - onCreate()NotificationPostActivity: "+mNotificationDataObj.getPostId());
            postId = Integer.parseInt(mNotificationDataObj.getPostId());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            postId = 0;
        }
        setupToolbarCustom();
        displayFragment();
    }

    private void setupToolbarCustom() {

        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        setTitle(Html.fromHtml("<font color='#ffffff'>Notification</font>"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private final String TAG_NOTIFICATION_FRAG = "dj.NotificationFragment";
    private void displayFragment() {
        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
        ft.replace(childLayout.getId(), new NotificationFragment(), TAG_NOTIFICATION_FRAG);
        ft.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        NotificationFragment nf = (NotificationFragment) getSupportFragmentManager().findFragmentByTag(TAG_NOTIFICATION_FRAG);
        if (nf != null)
            nf.updateComments();
    }

    public static class NotificationFragment extends DefaultVerticalListView{

        protected boolean isRefreshingData = false;
        protected final int offset = -1;
        protected int refreshOffset = 0;
        private SocialPost commentSocialPost;
        private int commentPosition;


        private LikeHelper likeHelper;
        private VoteHelper voteHelper;
        private SelectHelper selectHelper;
        private FollowHelper followHelper;

        public Class getLoadedDataParsingAwareClass() {
            return SocilFeedResult.class;
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
                    getAdapter().notifyItemChanged(pos);
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
                else if (host instanceof FollowHelper)
                    getAdapter().notifyItemChanged(pos);
            }
        };


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


        protected DataManager createDataManager() {
            return new SocialFeedProjectDataManager(getActivity(), this, getApp().getCookies());
        }

        public Map<String, Object> getNextDataParams(PageData data) {
            /*if (isFirst) {*/
                Log.d("djfeed","postId - getNextDataParams: "+postId);
                Map<String, Object> params = new HashMap<>();
                params.put(URLHelper.LIKE_A_POST.OFFSET, offset);
                params.put(URLHelper.LIKE_A_POST.POST_ID, postId);

                return params;
            //}
            //else return null;
        }

        public Map<String, Object> getRefreshDataParams(PageData data) {
            Map<String, Object> params = new HashMap<>();
            params.put(URLHelper.LIKE_A_POST.OFFSET, refreshOffset);
            SocialPost sp = (SocialPost) getDataManager().get(0);
            params.put(URLHelper.LIKE_A_POST.POST_ID, sp.getPostId());
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


        boolean isFirst = true;
        public class SocialFeedProjectDataManager extends DefaultProjectDataManager {
            public SocialFeedProjectDataManager(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
                super(context, delegate, cookies);
            }

            public Map<String, Object> getNextDataServerCallParams(PageData data) {
                return getNextDataParams(data);
            }

            @Override
            public boolean canLoadNext() {
                if (isFirst){
                    isFirst = false;
                    return true;
                }
                else return false;
            }

            /*public Map<String, Object> getRefreshDataServerCallParams(PageData data) {
                return getRefreshDataParams(data);
            }*/

            protected void updatePagingData(BaseDataParser loadedDataVO) {
                /*try {

                    offset = loadedDataVO.getPageSize();
                    pageData.curruntPage += 1;
                    pageData.totalPage += 1;
                } catch (Exception e) {
                    pageData.curruntPage = pageData.totalPage = 1;
                }*/
            }

        }


        @Override
        public void garbageCollectorCall() {
            super.garbageCollectorCall();
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

        public void onViewCreated(View view) {
            ButterKnife.bind(this, view);

            likeHelper = new LikeHelper(getActivity(), getApp().getCookies(), postUpdateResult);
            voteHelper = new VoteHelper(getActivity(), getApp().getCookies(), postUpdateResult);
            selectHelper = new SelectHelper(getActivity(), getApp().getCookies(), postUpdateResult);
            followHelper = new FollowHelper(getActivity(), getApp().getCookies(), postUpdateResult);
        }


        protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view;
            /*if (false)
                view = inflater.inflate(R.layout.social_feed_fragment_recycler_with_swipe_refresh_layout, container, false);
            else*/
                view = inflater.inflate(R.layout.fragment_notification_post, container, false);

            //setUpGesture(view);
            return view;
        }



        public static class ViewTypes {
            public static final int VIEW_NORMAL = 5;
            public static final int VIEW_POLL = 10;
            public static final int VIEW_BEST_OF = 15;
            public static final int VIEW_RATE_US = 20;
        }

        public static boolean reateItemAdded = false;

        public void onCallEnd(List<?> dataList, boolean isRefreshData) {
            super.onCallEnd(dataList, /*isRefreshData*/ false);
            if (!reateItemAdded) {
                if (getDataManager().size() > 4) {
                    //RateApp rateApp = new RateApp();
                    //    getDataManager().add(getDataManager().size()-2,rateApp);
                    //reateItemAdded=true;
                }
            }
        }


        public void onItemClick(Object baseObject) {
            super.onItemClick(baseObject);
            // Toast.makeText(getActivity(), baseObject.toString(), Toast.LENGTH_SHORT).show();
        }

        public int getListItemViewType(int position, Object item) {
            SocialPost post = (SocialPost) item;
            if (post.getPostType() == SocialPost.POST_TYPE_BEST_OF)
                return ViewTypes.VIEW_BEST_OF;
            else if (post.getPostType() == SocialPost.POST_TYPE_POLL)
                return ViewTypes.VIEW_POLL;
            else
                return ViewTypes.VIEW_NORMAL;
        }

        public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container) {

            if (viewType == ViewTypes.VIEW_POLL)
                return inflater.inflate(R.layout.social_post_poll_item, null);
            else if (viewType == ViewTypes.VIEW_BEST_OF)
                return inflater.inflate(R.layout.social_post_best_of_item, null);
            return inflater.inflate(R.layout.social_post_item, null);
        }

        public BaseItemHolder getItemHolder(int viewType, View view) {
            if (viewType == ViewTypes.VIEW_POLL)
                return new PollPostItemHolder(view);
            else if (viewType == ViewTypes.VIEW_BEST_OF)
                return new BestOfPostItemHolder(view);
            return new NormalPostItemHolder(view);
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
                        //zoomImages(socialPost, 0);
                        // TODO: 04-06-2016
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
                TypefaceHelper.setFont(notBuyLabel, buyLabel, voteToView);


            }

            public void updatePostView(SocialPost item, View view, int position) {
                pollLabel.setVisibility(View.VISIBLE);
                votePostButton.setVisibility(View.VISIBLE);
                pollLabel.setText(item.getVoteCount() + getActivity().getString(R.string.voteCountLabel));
                votePostButton.setText("{hea_buy_or_not}");

                if (item.getImg1() != null && item.getImg1().url.trim().equals("") == false) {
                    ImageLoaderUtils.loadImage(getContext(), item.getImg1(), image, R.drawable.vector_image_logo_square_100dp);
                    detailsHolder.setVisibility(View.VISIBLE);
                    if (isProductLink(item.getImage1()) != null)
                        buyNow.setVisibility(View.VISIBLE);
                    else
                        buyNow.setVisibility(View.GONE);

                } else {
                    detailsHolder.setVisibility(View.GONE);
                }

                Boolean isVoted = isVoted(item, false);
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


            @Bind(R.id.buyNow1)
            View buyNow1;
            @Bind(R.id.buyNow2)
            View buyNow2;
            @Bind(R.id.buyNow3)
            View buyNow3;

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
                        //zoomImages(socialPost, 0);//// TODO: 04-06-2016
                    } else if (v == option2Image) {
                        //zoomImages(socialPost, 1);
                    } else if (v == option3Image) {
                        //zoomImages(socialPost, 2);
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

                TypefaceHelper.setFont(option1Label, option2Label, option3Label, voteToView);

            }

            public void updatePostView(SocialPost item, View view, int position) {

                pollLabel.setVisibility(View.VISIBLE);
                votePostButton.setVisibility(View.VISIBLE);
                pollLabel.setText(item.getVoteCount() + getActivity().getString(R.string.voteCountLabel));
                detailsHolder.setVisibility(View.VISIBLE);

                Boolean isVoted = isVoted(item, false);
                if (item.getImg1() != null && item.getImg1().url.trim().equals("") == false) {
                    ImageLoaderUtils.loadImage(getContext(), item.getImg1(), option1Image, R.drawable.vector_image_logo_square_100dp);
                    optionBox1.setVisibility(View.VISIBLE);
                    if (isVoted)
                        option1Label.setText(item.getBof3Percent1() + "%");
                    else
                        option1Label.setText("");

                    if (isProductLink(item.getImage1()) != null)
                        buyNow1.setVisibility(View.VISIBLE);
                    else
                        buyNow1.setVisibility(View.GONE);

                } else
                    optionBox1.setVisibility(View.GONE);

                if (item.getImg2() != null && item.getImg2().url.trim().equals("") == false) {
                    ImageLoaderUtils.loadImage(getContext(), item.getImg2(), option2Image, R.drawable.vector_image_logo_square_100dp);
                    optionBox2.setVisibility(View.VISIBLE);
                    if (isVoted)
                        option2Label.setText(item.getBof3Percent2() + "%");
                    else
                        option2Label.setText("");

                    if (isProductLink(item.getImage2()) != null)
                        buyNow2.setVisibility(View.VISIBLE);
                    else
                        buyNow2.setVisibility(View.GONE);
                } else
                    optionBox2.setVisibility(View.GONE);

                if (item.getImg3() != null && item.getImg3().url.trim().equals("") == false) {
                    ImageLoaderUtils.loadImage(getContext(), item.getImg3(), option3Image, R.drawable.vector_image_logo_square_100dp);
                    optionBox3.setVisibility(View.VISIBLE);
                    if (isVoted)
                        option3Label.setText(item.getBof3Percent3() + "%");
                    else
                        option3Label.setText("");

                    if (isProductLink(item.getImage3()) != null)
                        buyNow3.setVisibility(View.VISIBLE);
                    else
                        buyNow3.setVisibility(View.GONE);
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




        public class NormalPostItemHolder extends PostItemHolder {

            @Bind(R.id.image)
            ImageView image;

            int widthPixels;

            @Bind(R.id.detailsHolder)
            RelativeLayout detailsHolder;

            @Bind(R.id.buyNow)
            View buyNow;

            private View.OnClickListener itemClick = new View.OnClickListener() {
                public void onClick(View v) {
                    if (v == image && socialPost.getImg1() != null && socialPost.getImg1().url.trim().equals("") == false) {

                        //zoomImages(socialPost, 0);
                        //// TODO: 04-06-2016
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
                if (item.getImg1() != null && item.getImg1().url.trim().equals("") == false) {
                    detailsHolder.setVisibility(View.VISIBLE);
                    if (isProductLink(item.getImage1()) != null)
                        buyNow.setVisibility(View.VISIBLE);
                    else
                        buyNow.setVisibility(View.GONE);
                    ImageLoaderUtils.loadImage(getContext(), item.getImg1(), image, R.drawable.vector_image_logo_square_100dp);
                } else {
                    detailsHolder.setVisibility(View.GONE);
                }


            }

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


            private View.OnClickListener itemClick = new View.OnClickListener() {
                public void onClick(View v) {
                    if (v == userImage || v == userName) {
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
                        else
                            socialPost.setLikeCount(socialPost.getLikeCount() + 1);
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
                        String appPlayStoreURL = getString(R.string.palyStoreBasicURL) + getActivity().getPackageName();
                        Map<String, String> map = new HashMap<>();
                        map.put(Action.ATTRIBUTE_TITLE, getString(R.string.appShareTitle));
                        map.put(Action.ATTRIBUTE_DATA, getString(R.string.appShareBody) + appPlayStoreURL);
                        shareActionData.setParam(map);
                        if (getAppMainActivity() != null)
                            getAppMainActivity().action(shareActionData);
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

                TypefaceHelper.setFont(userName, age, details, recomandationLabel);

                TypefaceHelper.setFont(getResources().getString(R.string.font_name_text_secondary), likesLabel, pollLabel, commentsLabel, shareLabel);

            }

            final public void updateItemView(Object item, View view, int position) {
                socialPost = (SocialPost) item;
                userName.setText(socialPost.getUserName());
                votePostButton.setVisibility(View.GONE);
                shareLabel.setVisibility(View.GONE);
                age.setText(socialPost.getAge());

                pollLabel.setVisibility(View.GONE);
                details.setText(socialPost.getDescription());

                commentsLabel.setText(socialPost.getCommentCount() + " ");
                likesLabel.setText(socialPost.getLikeCount() + getActivity().getString(R.string.likesCountLabel));


                shareLabel.setText(socialPost.getShareCount() + getActivity().getString(R.string.shareCOuntLabel));
                Picasso.with(getContext())
                        .load(socialPost.getUserPic())
                        .tag(getContext())
                        .placeholder(R.drawable.vector_image_place_holder_profile_dark)
                        .resize(100, 100)
                        .into(userImage);
                updatePostView(socialPost, view, position);


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


                List<com.goldadorn.main.model.Image> recommendation = socialPost.getRecommendation();
                List<ImageView> imageView = new ArrayList<>();
                imageView.add(reco1);
                imageView.add(reco2);
                imageView.add(reco3);
                imageView.add(reco4);


                updateRecommendation(recommendation, imageView);
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
            NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(), "Likes by", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, LikesActivity.class);
            Map<String, Object> data = new HashMap<>();
            data.put("POST_ID", socialPost.getPostId());
            data.put("POST", b);
            navigationDataObject.setParam(data);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
        }


    }
}
