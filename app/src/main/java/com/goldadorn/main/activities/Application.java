package com.goldadorn.main.activities;

import com.goldadorn.main.R;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.icons.GoldadornIconFont;
import com.goldadorn.main.icons.HeartIconFont;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.home.HomePage;
import com.goldadorn.main.modules.people.FindPeopleFragment;
import com.goldadorn.main.modules.sample.UnderDevelopment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.modules.timeLine.MyTimeLineFragment;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.model.User;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.model.BaseApplication;
import com.kimeeo.library.model.IFragmentData;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 1/11/16.
 */
public class Application extends BaseApplication {

    Map<Integer,IFragmentData> menu;

    private  List<Cookie> cookies;

    public People getPeople() {
        return people;
    }
    private People people;
    public User getUser() {
        return user;
    }
    private User user;

    private static User LOGIN_USER;
    public static User getLoginUser()
    {
         return LOGIN_USER;
    }

    public List<Cookie> getCookies()
    {
        return cookies;
    }
    public void setCookies(List<Cookie> cookies)
    {
        this.cookies=cookies;
        URLHelper.getInstance().setCookies(cookies);
    }

    public URLHelper getUrlHelper()
    {
        return URLHelper.getInstance();
    }

    public Map<Integer,IFragmentData> getMainMenu()
    {
        return menu;
    }
    public Map<Integer,IFragmentData> configMainMenu()
    {
        menu = new HashMap<>();
        addItem(menu, R.id.nav_home, R.string.home, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, HomePage.class);

        addItem(menu, R.id.nav_feed, R.string.socialFeeds, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, SocialFeedFragment.class);
        addItem(menu, R.id.nav_people, R.string.findPeople, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, FindPeopleFragment.class);


        //UsersTimeLineFragment
        addItem(menu, R.id.nav_timeline, R.string.timeline, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UserActivity.class);

        //addItem(menu, R.id.nav_timeline, R.string.timeline, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UnderDevelopment.class);


        addItem(menu, R.id.nav_shop_by,R.string.shopBy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UnderDevelopment.class);
        addItem(menu, R.id.nav_my_collection,R.string.myCollections, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UnderDevelopment.class);
        addItem(menu, R.id.nav_wishlist,R.string.wishlist, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UnderDevelopment.class);

        addItem(menu, R.id.nav_my_profile,R.string.myProfile, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);
        addItem(menu, R.id.nav_order_tracking,R.string.orderTracking, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);


        addItem(menu, R.id.nav_my_notifications,R.string.notifications, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, NotificationsActivity.class);
//        addItem(menu, R.id.nav_my_search, R.string.search, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);


        String appPlayStoreURL=getString(R.string.palyStoreBasicURL)+ getPackageName();
        addItem(menu, R.id.nav_share, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_TEXT_SHARE, appPlayStoreURL);
        Map<String,String> map = new HashMap<>();
        map.put(Action.ATTRIBUTE_TITLE,getString(R.string.appShareTitle));
        map.put(Action.ATTRIBUTE_DATA, getString(R.string.appShareBody) + appPlayStoreURL);
        menu.get(R.id.nav_share).setParam(map);

        addItem(menu, R.id.nav_share_facebook, R.string.inviteFacebookFriends, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);

        addItem(menu, R.id.nav_rate_us, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_EXTERNAL, appPlayStoreURL);

        String htmlEndPoint = getUrlHelper().getHTMLEndPoint();

        addItem(menu, R.id.nav_contact_us, R.string.contactUs, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint+getString(R.string.contactUsURL), WebActivity.class);
        addItem(menu, R.id.nav_about_us,R.string.aboutUS, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint+getString(R.string.aboutUsURL), WebActivity.class);
        addItem(menu, R.id.nav_faqs,R.string.faqs, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint+getString(R.string.faqsURL), WebActivity.class);

        addItem(menu, R.id.nav_shipping_and_return,R.string.shippingAndReturnPolicy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint+getString(R.string.shippingAndReturnPolicyURL),WebActivity.class);
        addItem(menu, R.id.nav_privacy_policy,R.string.privacyPolicy, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint+getString(R.string.privacyPolicyUTRL),WebActivity.class);
        addItem(menu, R.id.nav_terms_conditions,R.string.termsAndConditions, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_WEB_ACTIVITY, htmlEndPoint+getString(R.string.termsAndConditionsURL),WebActivity.class);


        addItem(menu, R.id.nav_settings,R.string.settings, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UnderDevelopmentActivity.class);
        addItem(menu, R.id.nav_logout,R.string.logout, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_LOGOUT,null);

        return menu;
    }

    private void addItem(Map<Integer, IFragmentData> menu, int id,int titleResID, String actionType,Class viewClass) {
        menu.put(id,new NavigationDataObject(id,getString(titleResID),actionType,viewClass));
    }
    private void addItem(Map<Integer, IFragmentData> menu, int id,int titleResID, String actionType, Object actionValue,Class viewClass) {
        menu.put(id,new NavigationDataObject(id,getString(titleResID),actionType,actionValue,viewClass));
    }
    private void addItem(Map<Integer, IFragmentData> menu, int id, String actionType, Object actionValue) {
        menu.put(id,new NavigationDataObject(id,actionType,actionValue));
    }
    private void addItem(Map<Integer, IFragmentData> menu, int id, String actionType) {
        menu.put(id,new NavigationDataObject(id,actionType));
    }
    public void configApplication()
    {
        ExtendedAjaxCallback.setReuseHttpClient(true);
        Iconics.registerFont(new FontAwesome());
        Iconics.registerFont(new GoldadornIconFont());
        Iconics.registerFont(new HeartIconFont());

        configMainMenu();
    }

    public void setUser(User user) {
        this.user = user;


        people = new People();
        people.setFollowerCount(0);
        people.setFollowingCount(0);
        people.setUserName(getUser().getUsername());
        people.setProfilePic(getUser().getUserpic());
        people.setUserId(getUser().getUserid());
        people.setIsSelf(true);
        people.setIsFollowing(0);

        Map<String, Object> data= new HashMap<>();
        data.put("NAME",people.getUserName());
        data.put("ID",people.getUserId());
        data.put("FOLLOWER_COUNT",people.getFollowerCount());
        data.put("FOLLOWING_COUNT",people.getFollowingCount());
        data.put("PROFILE_PIC",people.getProfilePic());
        data.put("IS_DESIGNER",people.getIsDesigner());
        data.put("IS_SELF", people.isSelf());
        data.put("IS_FOLLOWING", people.getIsFollowing());

        LOGIN_USER = user;
        ((NavigationDataObject)menu.get(R.id.nav_timeline)).name = people.getUserName()+" 's Timeline";
        menu.get(R.id.nav_timeline).setParam(data);

    }
}
