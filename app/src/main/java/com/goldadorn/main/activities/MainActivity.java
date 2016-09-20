package com.goldadorn.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.androidquery.callback.AjaxStatus;
import com.facebook.appevents.AppEventsConstants;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.PostBestOfActivity;
import com.goldadorn.main.activities.post.PostNormalActivity;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.dj.gesture.MyGestureListener;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.support.GARaterUpdateHelper;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.support.gcm.MixPanelHelper;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.eventBusEvents.SocialPost;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.modules.people.FindPeopleFragment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.fragments.BaseFragment;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseDrawerActivity {
    private BaseFragment activePage;
    private NavigationDataObject activePageData;
    private List<NavigationDataObject> history = new ArrayList<>();
    @Bind(R.id.disableApp)
    View disableApp;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;

    static RelativeLayout rlMain;
    @Bind(R.id.indicator)
    View indicator;

    @Bind(R.id.transView)
    View transView;
    @Bind(R.id.transViewSearch)
    View transViewSearch;
    @Bind(R.id.transViewNotify)
    View transViewNotify;
    @Bind(R.id.transViewPeople)
    View transViewPeople;
    @Bind(R.id.transViewPost)
    View transViewPost;

    public final int INTIMATION_COUNT_FOR_SESSION = 3;


/*    //    private ResourceReader resRdr;
//    private DjphyPreferenceManager coachMarkMgr;
//
//    private final String msgWelcome = "You have landed on the social feed\n"
//            +"where you can see all the social\nactivity happening in the app";
//    private final String msgSearch = "for designers, products\ncollections, trends and more";
//    private final String msgNotification = "check here";
//    private final String msgPeople = "See the user community at GoldAdorn";
//    private final String msgPost = "Create a Post";*/


    public View getPageIndicator() {
        return indicator;
    }

    //private boolean uploadInProgress;
    //private WeakReference<SocialFeedFragment> socialPostHost;
    private boolean backEntry;

    public static RelativeLayout getRlMain() {
        return rlMain;
    }

    private GestureDetector gd;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //gd.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
        /*if (gd.onTouchEvent(ev))
            return true;
        else
            return super.dispatchTouchEvent(ev);*/
    }

    public View getDisableApp() {
        //return null;
        return disableApp;
    }

    public FindPeopleFragment getPeopleFragment() {
        return ((HomePage) activePage).findPeopleFragment;
    }


    private MyGestureListener myGestureListener = new MyGestureListener() {
        @Override
        public void onSwipeLeftToRight() {

            Log.d("djgest", "onSwipeLeftToRight");
            //Toast.makeText(TestPayment.this.getApplicationContext(), "swipe right", Toast.LENGTH_SHORT).show();
            boolean isActive = ((HomePage) activePage).socialFeedFragmentpage.getUserVisibleHint();
            Log.d("djgest", "isSocialfeedactive?: " + isActive);
            if (isActive) {
                menuAction(R.id.nav_showcase);
            }
        }

        @Override
        public void onSwipeRightToLeft() {
            Log.d("djgest", "onSwipeRightToLeft");
            //Toast.makeText(TestPayment.this.getApplicationContext(), "swipe left", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSwipeBottomToTop() {
            Log.d("djgest", "onSwipeBottomToTop");
            //Toast.makeText(TestPayment.this.getApplicationContext(), "SwipeTop", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSwipeTopToBottom() {
            Log.d("djgest", "onSwipeTopToBottom");
            //Toast.makeText(TestPayment.this.getApplicationContext(), "Swipe bottom", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("djmain", "onPause-MainActivity");
        setIsMainActivity(false);
        setIsMainActivityBase(false);
    }


    /*public View getCenterView(){
        return transView;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        gd = new GestureDetector(this, myGestureListener);
        MixPanelHelper.getInstance().sendRefreshTokenToMixPanel();
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_home);
        if (navigationDataObject != null)
            action(navigationDataObject);

        logEventsAnalytics(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL);

        sendFcmToken();
        tourThisScreen();

        checkIfAppUpdated();
        getApp().getPrefManager().updateSessionCounts();
        //testCode();
    }

    /*private void testCode() {
        String toSend = "mihpayid=700000000011019008,mode=CC,status=success,unmappedstatus=captured," +
                "key=gtKFFx,txnid=1466756499377,amount=10.00,cardCategory=domestic,discount=0.00,net_amount_debit=10," +
                "addedon=2016-06-24 13:52:27,productinfo=myproduct,firstname=firstname,lastname=,address1=,address2=," +
                "city=,state=,country=,zipcode=,email=me@itsme.com,phone=,udf1=udf1,udf2=udf2,udf3=udf3,udf4=udf4," +
                "udf5=udf5,udf6=,udf7=,udf8=,udf9=,udf10=," +
                "hash=a439b0edce709dfcd090959a42abd7dce0a9909c9747d20e43e2880f8eefffb1009999c26e4e0ff1a3b1894efffffb3611" +
                "d4e6975bc92b985b3c62f0eed0b034,field1=617624148004,field2=999999,field3=8153088531361761,field4=-1,field5=," +
                "field6=,field7=,field8=,field9=SUCCESS,payment_source=payu,PG_TYPE=HDFCPG,bank_ref_num=8153088531361761," +
                "bankcode=CC,error=E000,error_Message=No Error";

    }*/
    ;


    private int intimationCountForThisSession = 1;

    public void displayDialogForIntimation() {
        if (getApp().getPrefManager().getIsStopIntimation())
            return;
        if (intimationCountForThisSession > INTIMATION_COUNT_FOR_SESSION)
            return;
        getApp().getPrefManager().updateIntimationCount();
        showDialogInfo("Great choice! Now tap on the product's image to check it out in our Showcase", true);
        intimationCountForThisSession++;
    }


    private GARaterUpdateHelper updateHelper;

    public void checkIfAppUpdated() {
        updateHelper = GARaterUpdateHelper.getInstance();
        updateHelper.checkForUpdates(this);
        updateHelper.rateApp(this);
    }

    private AppTourGuideHelper mTourHelper;

    private void tourThisScreen() {

        /*resRdr = ResourceReader.getInstance(getApplicationContext());
        coachMarkMgr = DjphyPreferenceManager.getInstance(getApplicationContext());*/
        mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*if (!coachMarkMgr.isHomeScreenTourDone())
                    testTourGuide();*/
                mTourHelper.displayHomeTour(MainActivity.this, new View[]{transView, transViewPeople, transViewSearch,
                        transViewNotify, transViewPost});
            }
        }, 2000);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (activePage != null && activePage.allowedBack() == false) {

        } else if (activePage != null && activePage.allowedBack()) {
            final Snackbar snackbar = Snackbar.make(layoutParent, "Are you sure you want to exit", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Yes", new View.OnClickListener() {
                public void onClick(View v) {
                    snackbar.dismiss();
                    MixPanelHelper.getInstance().flushDataToMixPanel();
                    SocialLoginUtil.getInstance(getBaseApplication()).performFbLogout();
                    SocialLoginUtil.getInstance(getBaseApplication()).performGoogleLogout();
                    SocialLoginUtil.getInstance(getBaseApplication()).indicateSignedOut();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();//// TODO: 19-07-2016
                        }
                    }, 1000);
                }
            });
            ColoredSnackbar.alert(snackbar).show();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        //SocialLoginUtil.getInstance(getBaseApplication()).indicateSignedOut();
        super.onDestroy();
    }

    //final public static int POST_FEED = 1;

    @Subscribe
    public void onEvent(SocialPost data) {

        if (!uploadInProgress) {
            Intent intent = null;
            if (data.type == com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST)
                intent = new Intent(MainActivity.this, PostNormalActivity.class);
            else if (data.type == com.goldadorn.main.model.SocialPost.POST_TYPE_POLL)
                intent = new Intent(MainActivity.this, PostPollActivity.class);
            else if (data.type == com.goldadorn.main.model.SocialPost.POST_TYPE_BEST_OF)
                intent = new Intent(MainActivity.this, PostBestOfActivity.class);
            if (intent != null) {
                Log.d(Constants.TAG_APP_EVENT, "AppEventLog: Create_post_initiation");
                logEventsAnalytics(GAAnalyticsEventNames.CREATE_POST_INITIATION);
                //socialPostHost = new WeakReference<>(data.host);
                People people = getApp().getPeople();
                intent.putExtra("NAME", people.getUserName());
                intent.putExtra("FOLLOWER_COUNT", people.getFollowerCount());
                intent.putExtra("FOLLOWING_COUNT", people.getFollowingCount());
                intent.putExtra("PROFILE_PIC", people.getProfilePic());
                intent.putExtra("IS_DESIGNER", people.getIsDesigner());
                intent.putExtra("ID", people.getUserId());
                intent.putExtra("IS_SELF", people.isSelf());
                intent.putExtra("backEnabled", true);
                startActivityForResult(intent, POST_FEED);
            }
        } else {
            final Snackbar snackbar = Snackbar.make(layoutParent, "Upload is in progress", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.info(snackbar).show();
        }

    }

    /*final private int postCallToken = IDUtils.generateViewId();
    private int recentlyPostedPost = -1;
    TemporaryCreatePostObj tempPostObj;*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setProgressBar(progressBar);
        setIsMainActivityBase(true);
        super.onActivityResult(requestCode, resultCode, data);
       /* Log.d("djpost", "onActResult");
        if (requestCode == POST_FEED && resultCode == Activity.RESULT_OK) {
            try {
                //String fileData=data.getStringExtra("fileData");
                int type = recentlyPostedPost = data.getIntExtra("type", -1);
                if (type != -1) {
                    tempPostObj = new TemporaryCreatePostObj();
                    tempPostObj.setPostType(type);
                    String msg = data.getStringExtra("msg");
                    tempPostObj.setMsg(msg);
                    MultipartEntity reqEntity = new MultipartEntity();

                    //// TODO: 28-06-2016
                    //change string[] to list or json array.
                    if (data.getExtras().get("price") != null) {
                        String[] price = (String[])data.getExtras().get("price");
                    }
                    try {

                        if (data.getExtras().get("files") != null) {
                            File[] files = (File[]) data.getExtras().get("files");
                            tempPostObj.setFileList(files);
                            File file;
                            int count = 1;
                            for (int i = 0; i < files.length; i++) {
                                file = files[i];
                                if (file != null && file.exists() && file.canRead()) {
                                    reqEntity.addPart("file" + count, new FileBody(file));
                                    count++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (data.getExtras().get("filesURIs") != null) {
                            String[] uris = (String[]) data.getExtras().get("filesURIs");
                            tempPostObj.setFileUriList(uris);
                            File file;
                            int count = 1;
                            for (int i = 0; i < uris.length; i++) {
                                file = new File(uris[i]);
                                ;
                                if (file != null && file.exists() && file.canRead()) {
                                    reqEntity.addPart("file" + count, new FileBody(file));
                                    count++;
                                }
                            }
                        }
                    }

                    if (data.getExtras().get("links") != null) {
                        String[] links = (String[]) data.getExtras().get("links");
                        tempPostObj.setLinksList(links);
                        String link;
                        int count = 1;
                        for (int i = 0; i < links.length; i++) {
                            link = links[i];
                            if (link != null && link.equals("") == false) {
                                reqEntity.addPart("link" + count, new StringBody(link + ""));
                                count++;
                            }
                        }
                    }

                    if (data.getExtras().get("collIdList") != null){
                        int[] collIdArr = (int[]) data.getExtras().get("collIdList");
                        JSONArray jsonArray = new JSONArray();
                        try {
                            for (int colId : collIdArr){
                                jsonArray.put(colId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jsonArray.length() != 0){
                            reqEntity.addPart("createpost_collids", new );
                        }
                        int collId;
                        int count = 1;
                        for (int i = 0; i < collIdArr.length; i++){
                            collId = collIdArr[i];
                            if (collId != -1){
                                reqEntity.addPart("createpost_collid"+count, new StringBody(String.valueOf(collId)));
                                count++;
                            }
                        }
                    }

                    if (data.getExtras().get("desIdList") != null){
                        int[] desIdArr = (int[]) data.getExtras().get("desIdList");
                        JSONArray jsonArray = new JSONArray();
                        try {
                            for (int colId : collIdArr){
                                jsonArray.put(colId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jsonArray.length() != 0){
                            reqEntity.addPart("createpost_collids", new );
                        }
                        int desId;
                        int count = 1;
                        for (int i = 0; i < desIdArr.length; i++){
                            desId = desIdArr[i];
                            if (desId != -1){
                                reqEntity.addPart("createpost_desgnid"+count, new StringBody(String.valueOf(desId)));
                                count++;
                            }
                        }
                    }


                    if (data.getExtras().get("clubbed") != null) {
                        String[] clubbedArr = (String[]) data.getExtras().get("clubbed");
                        tempPostObj.setClubbedList(clubbedArr);
                        String clubbedTxt;
                        int count = 1;
                        for (int i = 0; i < clubbedArr.length; i++) {
                            clubbedTxt = clubbedArr[i];
                            if (clubbedTxt != null) {
                                reqEntity.addPart("p" + count, new StringBody(clubbedTxt));
                                count++;
                            }
                        }
                    }


                    if (msg != null && msg.equals("") == false)
                        reqEntity.addPart("createpost_message", new StringBody(msg));
                    reqEntity.addPart("createpost_type", new StringBody(type + ""));
                    //reqEntity.addPart("createpost_price", new StringBody(type + ""));
                    Map<String, Object> params = new HashMap<>();
                    params.put(AQuery.POST_ENTITY, reqEntity);


                    String url = getUrlHelper().getCreatePostServiceURL();
                    ExtendedAjaxCallback ajaxCallback = getAjaxCallback(postCallToken);
                    ajaxCallback.setClazz(String.class);
                    ajaxCallback.setParams(params);
                    ajaxCallback.method(AQuery.METHOD_POST);
                    getAQuery().ajax(url, params, String.class, ajaxCallback);
                    uploadInProgress = true;
                    startUploadProgress();
                }


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }*/
    }


    /*public final int POST_DELETE_CALL = IDUtils.generateViewId();
    public final int POST_HIDE_CALL = IDUtils.generateViewId();
    public final int POST_REPORT_CALL = IDUtils.generateViewId();

    private int position = -1;*/

    public void updatePostForThisUser(int what, String postId, int position) {
        super.updatePostForThisUser(what, postId, position);
        /*ExtendedAjaxCallback ajaxCallback = null;
        this.position = position;
        Map<String, String> params = new HashMap<>();
        params.put("postid", postId);
        if (what == POST_DELETE_CALL) {
            ajaxCallback = getAjaxCallback(POST_DELETE_CALL);
            getAQuery().ajax(ApiKeys.getDeletePostAPI(), params, String.class, ajaxCallback);
        } else if (what == POST_HIDE_CALL) {
            params.put("report", String.valueOf(0));
            ajaxCallback = getAjaxCallback(POST_HIDE_CALL);
            getAQuery().ajax(ApiKeys.getHidePostAPI(), params, String.class, ajaxCallback);
        } else if (what == POST_REPORT_CALL) {
            //// TODO: 25-06-2016
            params.put("report", String.valueOf(1));
            ajaxCallback = getAjaxCallback(POST_REPORT_CALL);
            getAQuery().ajax(ApiKeys.getHidePostAPI(), params, String.class, ajaxCallback);
        }
        Log.d("djmain", "req params - updatePostForThisUser: " + params);*/
        //ajaxCallback.method(AQuery.METHOD_POST);
    }

    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
       /* Log.d("djmain", "url queried- MainActivity: " + url);
        Log.d("djmain", "response- MainActivity: " + json);
        if (id == postCallToken) {
            uploadInProgress = false;
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            if (success) {
                *//*if (socialPostHost != null && socialPostHost.get() != null)
                    socialPostHost.get().postAdded(new com.goldadorn.main.model.SocialPost());

                socialPostHost = null;*//*
                *//*if (activePage instanceof HomePage) {
                    ((HomePage) activePage).socialFeedFragmentpage.postAdded();
                }*//*

                // TODO: 08-07-2016
                *//*if (recentlyPostedPost != -1 && recentlyPostedPost == com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST) {
                    if (socialPostHost != null && socialPostHost.get() != null)
                        socialPostHost.get().postAdded(new com.goldadorn.main.model.SocialPost());
                    socialPostHost = null;
                } else {
                    Fragment frg = getSupportFragmentManager().findFragmentByTag(TAG_FOR_HOME_FRAGMENT);
                    Log.d("djmain", "updatingpost - fragment val" +frg);
                    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }*//*
                if (recentlyPostedPost != -1 && recentlyPostedPost == com.goldadorn.main.model.SocialPost.POST_TYPE_NORMAL_POST) {

                    if (socialPostHost != null && socialPostHost.get() != null)
                        socialPostHost.get().postAdded(null);
                } else {
                    int postId = -1;
                    try {
                        postId = new JSONObject((String) json).getInt("postid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        postId = -1;
                    }
                    tempPostObj.setPostId(postId);
                    com.goldadorn.main.model.SocialPost socialPost = TemporarySocialPostParser.getSocialPostObj(tempPostObj);
                    if (activePage instanceof HomePage) {
                        ((HomePage) activePage).socialFeedFragmentpage.postAdded(socialPost);
                    }
                }
                stopUploadProgress(true);

                Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CREATE_POST_SUCCESS");
                logEventsAnalytics(GAAnalyticsEventNames.CREATE_POST_SUCCESS);
                Toast.makeText(MainActivity.this, "Successfully Posted on Wall", Toast.LENGTH_SHORT).show();

            } else {
                stopUploadProgress(success);
            }

        } else if (id == POST_DELETE_CALL || id == POST_HIDE_CALL || id == POST_REPORT_CALL) {
            if (position != -1) {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                        layoutParent, this);
                if (success) {
                    if (json == null)
                        return;
                    if (id == POST_REPORT_CALL)
                        showDialogInfo("Thanks for notifying us! Our team will review this post and" +
                                " take corrective action. Meanwhile, we are hiding this post from your feed", true);
                    if (activePage instanceof HomePage) {
                        ((HomePage) activePage).socialFeedFragmentpage.updatePostList(position);
                    }
                }
            }
            position = -1;
        } else*/
        super.serverCallEnds(id, url, json, status);
    }

    public void showDialogInfo(String msg, boolean isPositive) {
        int color;
        color = isPositive ? R.color.colorPrimary : R.color.Red;
        WindowUtils.getInstance(getApplicationContext()).genericInfoMsgWithOK(this, null, msg, color);
    }

    /*private void startUploadProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopUploadProgress(boolean success) {
        progressBar.setVisibility(View.GONE);
    }*/

    private final String TAG_FOR_HOME_FRAGMENT = "goldadorn.homefragment";

    public boolean action(NavigationDataObject navigationDataObject) {
        if (navigationDataObject.isType(NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW)) {
            Action action = new Action(this);
            Boolean isAdded = false;

            if (activePageData != null && activePageData.getIdInt() == navigationDataObject.getIdInt()) {
                isAdded = true;
            }

            if (!isAdded) {
                BaseFragment fragment = BaseFragment.newInstance(navigationDataObject);
                if (fragment != null) {
                    //setTitle(navigationDataObject.getTitle());
                    setTitle("");
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, fragment, TAG_FOR_HOME_FRAGMENT);
                    ft.commit();
                    activePage = fragment;
                    activePageData = navigationDataObject;
                    if (backEntry == false)
                        history.add(activePageData);
                    backEntry = false;
                    return true;
                }
            }
        }
        return super.action(navigationDataObject);
    }

    protected void onResume() {
        super.onResume();
        Log.d("djmain", "onResume-MainActivity");
        setIsMainActivity(true);
        setIsMainActivityBase(true);

        if (activePage instanceof HomePage) {
            SocialFeedFragment tempFeed = ((HomePage) activePage).socialFeedFragmentpage;
            /*((HomePage) activePage)*/
            if (tempFeed != null) {
                tempFeed.updateComments();
                 boolean flag = UserSession.getInstance().getIsBonbRefreshPending();
                if (flag) {
                    tempFeed.refreshSelf();
                    UserSession.getInstance().setIsBonbRefreshPending(false);
                }
            }
        }
    }


    public void setSocialFeedFragment(SocialFeedFragment tempFeed){
        UserSession.getInstance().setSocialFeedFragment(tempFeed);
    }


    /*@Override
    public void onStart() {
        Log.d("djmain", "onstart-MainActivity");
        super.onStart();
    }*/
}
