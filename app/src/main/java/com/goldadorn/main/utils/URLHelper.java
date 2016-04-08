package com.goldadorn.main.utils;


import com.goldadorn.main.BuildConfig;

import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class URLHelper {
    // Private constructor prevents instantiation from other classes
    public final String  endPoint;
    public final String  iamgeEndPoint;
    public final String  websiteEndPoint;

    public final String  htmlEndPoint;

    public List<Cookie> getCookies() {
        return cookies;
    }

    private List<Cookie> cookies;

    private URLHelper() {
        endPoint= BuildConfig.END_POINT;
        htmlEndPoint= BuildConfig.HTML_END_POINT;
        iamgeEndPoint= BuildConfig.IMAGE_END_POINT;
        websiteEndPoint= BuildConfig.WEBSITE_PRODUCT_END_POINT;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public String getHTMLEndPoint() {
            return htmlEndPoint;
    }

    public String getWebSiteProductEndPoint() {
        return websiteEndPoint;
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final URLHelper INSTANCE = new URLHelper();

    }

    public static URLHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }



    public static String parseImageURL(String url)
    {
        if(url!=null && url.trim().startsWith("../")) {
            url = url.trim().replace("../", "");
        }
        else if(url!=null && url.trim().startsWith(":../")) {
            url = url.trim().replace(":../", "");
        }
        else if(url!=null && url.trim().startsWith(".../")) {
            url = url.trim().replace(".../", "");
        }
        if(url!=null)
            return getInstance().iamgeEndPoint+url;
        return null;
    }

    public String getLoginServiceURL()
    {
        return endPoint+VERB.LOGIN;
    }

    public String getCreatePostServiceURL()
    {
        return endPoint+VERB.CREATE_POST;
    }

    public String getSetBasicProfileURL()
    {
        return "http://demo.eremotus-portal.com/goldadorn_dev/rest/setbasicprofile";

        //return endPoint+VERB.SET_BASIC_PROFILE;
    }

    public String getFindPeopleServiceURL()
    {
        return endPoint+VERB.PEOPLE;
    }
    public String getSocialFeedServiceURL()
    {
        return endPoint+VERB.SOCIAL_FEED;
    }
    public String getFolderServiceURL()
    {
        return endPoint+VERB.FETCH_GALLERY;
    }
    public String getSocialFeedRefreshServiceURL()
    {
        return endPoint+VERB.SOCIAL_FEED;
    }
    public String getUsersSocialFeedServiceURL()
    {
        return endPoint+VERB.FETCH_TIME_LINE;
    }
    public String getLogoutServiceURL()
    {
        return endPoint+VERB.LOGOUT;
    }
    public String getRegisterServiceURL()
    {
        return endPoint+VERB.REGISTER;
    }
    public String getLikeAPostServiceURL()
    {
        return endPoint+VERB.LIKE;
    }
    public String getFollowPeopleServiceURL()
    {
        return endPoint+VERB.FOLLOW;
    }

    public String getCommentOnPostServiceURL()
    {
        return endPoint+VERB.COMMENT;
    }
    public String getFetchCommentsServiceURL()
    {
        return endPoint+VERB.FETCH_COMMENTS;
    }
    public String getFetchLikesServiceURL()
    {
        return endPoint+VERB.FETCH_LIKES;
    }

    public String getPollPostServiceURL()
    {
        return endPoint+VERB.POLL;
    }
    public String getVoteBof3PostServiceURL()
    {
        return endPoint+VERB.VOTE_BOF_3;
    }
    public String getFetchVotersServiceURL()
    {
        return endPoint+VERB.FETCH_VOTERS;
    }

    public String getNotificationsUrl()
    {
        return endPoint+VERB.FETCH_NOTIFICATIONS;
    }

    public static class VERB
    {
        public static final String REGISTER="register";
        public static final String LOGIN="login";
        public static final String LOGOUT="logout";
        public static final String PEOPLE="people";
        public static final String SOCIAL_FEED="socialfeed";
        public static final String CREATE_POST="createpost";
        public static final String SET_BASIC_PROFILE="setbasicprofile";

        public static final String LIKE="like";
        public static final String POLL="poll";
        public static final String VOTE_BOF_3="votebof3";
        public static final String COMMENT="comment";
        public static final String FOLLOW="follow";
        public static final String FETCH_COMMENTS="fetchcomments";
        public static final String FETCH_TIME_LINE="fetchtimeline";
        public static final String FETCH_LIKES="fetchlikes";
        public static final String FETCH_VOTERS="fetchvoters";
        public static final String FETCH_NOTIFICATIONS="notifications";
        public static final String FETCH_GALLERY="fetchgallery";


    }
    public static class LOGIN_PARAM
    {
        public static final String USER_NAME="login_userId";
        public static final String PASSWORD="login_password";
    }
    public static class LIKE_A_POST
    {
        public static final String LIKE="like";
        public static final String TEXT="text";

        public static final String POLL="poll";
        public static final String SELECT="select";
        public static final String USER_ID="userid";
        public static final String POST_ID="postid";
        public static final String OFFSET="offset";
        public static final String FOLLOW="follow";
        public static final String PATH="path";
        public static final String GALLERY="gallery";


    }

    public static class REGISTER_PARAM
    {
        public static final String REGISTER_FIRST_NAME="register_firstName";
        public static final String REGISTER_LAST_NAME="register_lastName";
        public static final String REGISTER_EMAIL="register_email";
        public static final String REGISTER_PASSWORD="register_password";
        public static final String REGISTER_CONFIRM_PASSWORD="register_confirmPassword";
        public static final String REGISTER_GENDER="register_gender";

        public static final String REGISTER_GENDER_MALE="1";
        public static final String REGISTER_GENDER_FEMALE="2";
    }

}
