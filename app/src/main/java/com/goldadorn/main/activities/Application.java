package com.goldadorn.main.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.facebook.FacebookSdk;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.cart.CartManagerActivity;
import com.goldadorn.main.activities.cart.LikelistActivity;
import com.goldadorn.main.activities.cart.MyOrdersActivity;
import com.goldadorn.main.activities.cart.WishListManagerActivity;
import com.goldadorn.main.activities.showcase.ShowcaseActivity;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.dj.fragments.FilterResultsFragment;
import com.goldadorn.main.dj.fragments.FilterSelectorFragment;
import com.goldadorn.main.dj.fragments.RedemptionFragment;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.support.DjphyPreferenceManager;
import com.goldadorn.main.dj.support.GAFacebookAnalytics;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.icons.GoldadornIconFont;
import com.goldadorn.main.icons.HeartIconFont;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.modules.sample.UnderDevelopment;
import com.goldadorn.main.modules.search.HashTagFragment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.AsyncRunnableTask;
import com.goldadorn.main.utils.URLHelper;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.model.BaseApplication;
import com.kimeeo.library.model.IFragmentData;
import com.kobakei.ratethisapp.RateThisApp;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.splunk.mint.Mint;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by bhavinpadhiyar on 1/11/16.
 */
public class Application extends BaseApplication {

    Map<Integer, IFragmentData> menu;

    private List<Cookie> cookies;
    private UserInfoCache mUserInfoCache;
    private Handler mUIHandler = new Handler();

    public People getPeople() {
        return people;
    }

    @Override
    public void onCreate(Fragment fragment) {
        ActivityLifecycleCallback.register(this);
        super.onCreate(fragment);
    }

    private People people;

    public User getUser() {
        return user;
    }

    private User user;

    private static User LOGIN_USER;

    public static User getLoginUser() {
        return LOGIN_USER;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
        URLHelper.getInstance().setCookies(cookies);
        Log.e("iii","iii");
    }

    private static Application ourInstance;
    public static Application getInstance(){
        return ourInstance;
    }

    private DjphyPreferenceManager mPrefInstance;
    public DjphyPreferenceManager getPrefManager(){

        if (mPrefInstance == null){
            mPrefInstance = DjphyPreferenceManager.getInstance(this);
        }
        return mPrefInstance;
    }

    private CleverTapAPI cleverTap;

    public CleverTapAPI getCleverTapInstance(){
        try {
            if (cleverTap == null) {
                cleverTap = CleverTapAPI.getInstance(getApplicationContext());
                CleverTapAPI.setDebugLevel(1277182231);
            }
            return cleverTap;
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        return cleverTap;
    }

    @Override
    public Object getSystemService(String name) {
        if (UserInfoCache.USER_INFO_SERVICE.equals(name)) {
            if (mUserInfoCache == null)
                mUserInfoCache = UserInfoCache.createInstance(getApplicationContext(), mUIHandler);
            return mUserInfoCache;
        }
        return super.getSystemService(name);
    }



    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    public URLHelper getUrlHelper() {
        return URLHelper.getInstance();
    }

    public Map<Integer, IFragmentData> getMainMenu() {
        return menu;
    }

    public Map<Integer, IFragmentData> configMainMenu() {
        menu = new HashMap<>();
        addItem(menu, R.id.nav_home, R.string.home, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, HomePage.class);

        addItem(menu, R.id.nav_feed, R.string.socialFeeds, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, SocialFeedFragment.class);
        //addItem(menu, R.id.nav_people, R.string.findPeople, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, FindPeopleFragment.class);
        addItem(menu, R.id.nav_filter_result, R.string.selecFilter, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, FilterResultsFragment.class);
        addItem(menu, R.id.nav_filter_selector, R.string.selecFilter, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, FilterSelectorFragment.class);
        addItem(menu, R.id.nav_showcase, R.string.showcase, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ShowcaseActivity.class);
        addItem(menu, R.id.nav_my_likes, R.string.myLikes, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, LikelistActivity.class);
        addItem(menu, R.id.nav_collections, R.string.collections, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, TestActivity.class);
        addItem(menu, R.id.nav_cart, R.string.cart, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, CartManagerActivity.class);
        addItem(menu, R.id.nav_wishlist, R.string.wishlist, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, WishListManagerActivity.class);
        addItem(menu, R.id.nav_my_orders, R.string.myOrder, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, MyOrdersActivity.class);


        //UsersTimeLineFragment
        addItem(menu, R.id.nav_timeline, R.string.timeline, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UserActivity.class);

        //addItem(menu, R.id.nav_timeline, R.string.timeline, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UnderDevelopment.class);


        addItem(menu, R.id.nav_shop_by, R.string.shopBy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, OurCollectionsActivity.class);
        addItem(menu, R.id.nav_my_collection, R.string.myCollections, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UnderDevelopment.class);

        addItem(menu, R.id.nav_my_profile, R.string.myProfile, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ProfileEditActivity.class);
        addItem(menu, R.id.nav_edit_profile, R.string.edit_profile, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ProfileEditActivity.class);
        addItem(menu, R.id.nav_order_tracking, R.string.orderTracking, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);


        addItem(menu, R.id.nav_my_notifications, R.string.notifications, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, NotificationsActivity.class);
        addItem(menu, R.id.nav_my_search, R.string.search, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);
        addItem(menu, R.id.nav_id_hashtag, R.string.hashtag, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, HashTagFragment.class);
        addItem(menu, R.id.nav_my_redemption, R.string.redemDetails, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, "dummy", WebActivity.class);


        String appPlayStoreURL = getString(R.string.palyStoreBasicURL) + getPackageName();
        addItem(menu, R.id.nav_share, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE, appPlayStoreURL);
        Map<String, String> map = new HashMap<>();
        map.put(Action.ATTRIBUTE_TITLE, getString(R.string.appShareTitle));
        map.put(Action.ATTRIBUTE_DATA, getString(R.string.appShareBody) + appPlayStoreURL);
        menu.get(R.id.nav_share).setParam(map);

        addItem(menu, R.id.nav_share_facebook, R.string.inviteFacebookFriends, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, ForgotPasswordActivity.class);

        addItem(menu, R.id.nav_rate_us, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_EXTERNAL, appPlayStoreURL);

        String htmlEndPoint = getUrlHelper().getHTMLEndPoint();
        //String urlForSizeGuide = "http://www.amazon.in/Ring-Size-Guides/b?ie=UTF8&node=5274290031";
        String urlForBuyBack = ApiKeys.getUrlForSideMenu("bbck");
        String urlForSizeGuide =  ApiKeys.getUrlForSideMenu("sizegd");
        String urlForReturnPolicy =  ApiKeys.getUrlForSideMenu("return-policy");
        String urlForDiamondQual = "http://goldadorn.tuxer5qf9ekl44m.netdna-cdn.com/o/idsg.jpg";

        addItem(menu, R.id.nav_contact_us, R.string.contactUs, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint + getString(R.string.contactUsURL), WebActivity.class);
        addItem(menu, R.id.nav_people, R.string.findPeople, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint, WebActivity.class);
        addItem(menu, R.id.nav_about_us, R.string.aboutUS, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint + getString(R.string.aboutUsURL), WebActivity.class);
        addItem(menu, R.id.nav_faqs, R.string.faqs, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint + getString(R.string.faqsURL), WebActivity.class);
        addItem(menu, R.id.nav_size_guide, R.string.sizeGuide, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, urlForSizeGuide, WebActivity.class);
        addItem(menu, R.id.nav_dia_qual_guide, R.string.diaQualGuide, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, urlForDiamondQual, WebActivity.class);
        addItem(menu, R.id.nav_buy_back_policy, R.string.buyBackPolicy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, urlForBuyBack, WebActivity.class);

        addItem(menu, R.id.nav_shipping_and_return, R.string.shippingAndReturnPolicy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, urlForReturnPolicy, WebActivity.class);
        addItem(menu, R.id.nav_privacy_policy, R.string.privacyPolicy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint + getString(R.string.privacyPolicyUTRL), WebActivity.class);
        addItem(menu, R.id.nav_terms_conditions, R.string.termsAndConditions, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint + getString(R.string.termsAndConditionsURL), WebActivity.class);


        addItem(menu, R.id.nav_settings, R.string.settings, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);
        addItem(menu, R.id.nav_logout, R.string.logout, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_LOGOUT, null);

        return menu;
    }

    private void addItem(Map<Integer, IFragmentData> menu, int id, int titleResID, String actionType, Class viewClass) {
        menu.put(id, new NavigationDataObject(id, getString(titleResID), actionType, viewClass));
    }

    private void addItem(Map<Integer, IFragmentData> menu, int id, int titleResID, String actionType, Object actionValue, Class viewClass) {
        menu.put(id, new NavigationDataObject(id, getString(titleResID), actionType, actionValue, viewClass));
    }

    private void addItem(Map<Integer, IFragmentData> menu, int id, String actionType, Object actionValue) {
        menu.put(id, new NavigationDataObject(id, actionType, actionValue));
    }

    private void addItem(Map<Integer, IFragmentData> menu, int id, String actionType) {
        menu.put(id, new NavigationDataObject(id, actionType));
    }
    public void configApplication() {
        ourInstance = this;
        Mint.initAndStartSession(this, "9bccadcc");
        ExtendedAjaxCallback.setReuseHttpClient(true);
        Iconics.registerFont(new FontAwesome());
        Iconics.registerFont(new GoldadornIconFont());
        Iconics.registerFont(new HeartIconFont());
        configMainMenu();
        //dont remove this code
        initGoogleAnalytics();
        initFacebook();
        initMixPanel();
        initAppRater();
    }

    //public int numOfDaysToWait = 5;
    //public int numOfUsesToWait = 5;

    private void initAppRater(){
        RateThisApp.Config config = new RateThisApp.Config();
// Custom title ,message and buttons names
        config.setTitle(R.string.rate_app_title);
        config.setMessage(R.string.rate_app_msg);
        config.setYesButtonText(R.string.rate_app_yes);
        config.setNoButtonText(R.string.rate_app_no);
        config.setCancelButtonText(R.string.rate_app_later);
        RateThisApp.init(config);
        getPrefManager().startDayCountForRating();
        //setAppRateCallBack();

        /*if (getPrefManager().getAppRatingStatus())
            RateThisApp.stopRateDialog(this);*/
    }

    private void initMixPanel(){
        Log.d("dj","mix panel init");
        mMixPanelInstance = MixpanelAPI.getInstance(this, Constants.MIX_PANEL_PROJECT_TOKEN);
    }

    public DisplayProperties getDisplayPropInstance(){
        return DisplayProperties.getInstance(this, DisplayProperties.ORIENTATION_PORTRAIT);
    }

    private GAFacebookAnalytics mFbAnalyticsInstance;
    private void initFacebook() {

        FacebookSdk.sdkInitialize(this);
        GAFacebookAnalytics.activateApp(this);
    }

    public GAFacebookAnalytics getFbAnalyticsInstance(){

        if (mFbAnalyticsInstance == null){
            mFbAnalyticsInstance = new GAFacebookAnalytics();
        }
        return mFbAnalyticsInstance;
    }

    private MixpanelAPI mMixPanelInstance;
    public MixpanelAPI getMixPanelInstance(){
        if (mMixPanelInstance == null){
            mMixPanelInstance = MixpanelAPI.getInstance(this, Constants.MIX_PANEL_PROJECT_TOKEN);
        }
        return mMixPanelInstance;
    }


    /*public void logEventMixPanel(String eventName, JSONObject propertyParams){

        Log.d(Constants.TAG_APP_EVENT, "AppEventLog - MixPanel: "+eventName);
        if (propertyParams == null)
            getMixPanelInstance().track(eventName);
        else
            getMixPanelInstance().track(eventName, propertyParams);
    }*/

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.app_tracker);
        }
        return mTracker;
    }
    private void initGoogleAnalytics() {
        getDefaultTracker();
        setExceptionHandler();
    }
    private void setExceptionHandler() {
        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                mTracker,                                        // Currently used Tracker.
                Thread.getDefaultUncaughtExceptionHandler(),    // Current default uncaught exception handler.
                this);                                         // Context of the application.
        // Make myHandler the new default uncaught exception handler.

        StandardExceptionParser exceptionParser =
                new StandardExceptionParser(getApplicationContext(), null) {
                    @Override
                    public String getDescription(String threadName, Throwable t) {
                        return "{" + threadName + "} " + Log.getStackTraceString(t);
                    }
                };
        ((ExceptionReporter) myHandler).setExceptionParser(exceptionParser);

        Thread.setDefaultUncaughtExceptionHandler(myHandler);
    }

    private void logUser(User user) {
        Mint.clearExtraData();
        Mint.addExtraData("User ID", user.id + "");
        Mint.addExtraData("User Photo", user.getImageUrl());
        Mint.addExtraData("User Name", user.getName());
    }

    public void setPeople(People people) {
        this.people = people;
    }

    public void setUser(User user) {
        this.user = user;
        logUser(user);

        people = new People();
        people.setFollowerCount(0);
        people.setFollowingCount(0);
        people.setUserName(getUser().getName());
        people.setProfilePic(getUser().getImageUrl());
        people.setUserId(getUser().id);
        people.setIsSelf(true);
        people.setIsFollowing(0);

        Map<String, Object> data = new HashMap<>();
        data.put("NAME", people.getUserName());
        data.put("ID", people.getUserId());
        data.put("FOLLOWER_COUNT", people.getFollowerCount());
        data.put("FOLLOWING_COUNT", people.getFollowingCount());

        if (people.getProfilePic() != null)
            data.put("PROFILE_PIC", people.getProfilePic());

        data.put("IS_DESIGNER", people.getIsDesigner());
        data.put("IS_SELF", people.isSelf());
        data.put("IS_FOLLOWING", people.getIsFollowing());

        LOGIN_USER = user;
        ((NavigationDataObject) menu.get(R.id.nav_timeline)).name = people.getUserName() + " 's Timeline";
        menu.get(R.id.nav_timeline).setParam(data);

    }

    public void postWork(Runnable runnable) {
        new AsyncRunnableTask(runnable).execute(AsyncTask.SERIAL_EXECUTOR);
    }

    public Handler getUIHandler() {
        return mUIHandler;
    }
}
