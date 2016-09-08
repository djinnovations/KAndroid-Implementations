package com.goldadorn.main.utils;


import com.goldadorn.main.BuildConfig;

import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class URLHelper {
    // Private constructor prevents instantiation from other classes
    public final String endPointSocial;
    public final String  endPointCommercial;
    public final String  iamgeEndPoint;
    public final String  websiteEndPoint;

    public final String  htmlEndPoint;
    public final String productImageEndPoint;
    public final String collectionImageEndPoint;
    public final String productTypesImageEndPoint;



    public List<Cookie> getCookies() {
        return cookies;
    }

    private List<Cookie> cookies;

    private URLHelper() {

        endPointCommercial= BuildConfig.END_POINT_COMERCIAL;
        endPointSocial = BuildConfig.END_POINT;
        htmlEndPoint= BuildConfig.HTML_END_POINT;
        iamgeEndPoint= BuildConfig.IMAGE_END_POINT;
        productImageEndPoint= BuildConfig./*PRODUCT_IMAGE_END_POINT*/SHOWCASE_COLLECTION_HOST;
        collectionImageEndPoint=BuildConfig.COLLECTION_IMAGE_END_POINT;
        websiteEndPoint= BuildConfig.WEBSITE_PRODUCT_END_POINT;
        productTypesImageEndPoint=BuildConfig.PRODUCT_TYPES_IMAGE_END_POINT;
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

    public String getProductsLikes() {
        return endPointSocial+VERB.GET_PRODUCTS_LIKES;
    }

    public String getDesignersFilter(int offset) {
        return endPointSocial+VERB.GET_DESIGNERS_FILTER+"/"+offset;
    }

   /* @Deprecated
    public String getDesignersFilter(int offset) {
        String newEndPoint = "http://goldadorn.cloudapp.net/goldadorn_dev/rest/";
        return newEndPoint+VERB.GET_DESIGNERS_FILTER+"/"+offset;
    }*/

    public String getCollectionData(int offset) {
        return endPointCommercial+VERB.GET_SCROLL_DATA+"/c/"+offset;
    }
    public String getProductType(int offset) {
        return endPointCommercial+VERB.GET_SCROLL_DATA+"/t/"+offset;
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
        if(url!=null) {
            if(url.contains(/*"products/"*/"defaults/")/*!=-1*/ || url.contains("products/")) {
                url = getInstance().productImageEndPoint + url;
                //url = url.replace("/products/","/");
                return url;
            }
            else
                return getInstance().iamgeEndPoint + url;
        }
        return null;
    }

    @Deprecated
    public static String parseImageURLDesignersTemp(String url)
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
        if(url!=null) {
            if(url.indexOf("products/")!=-1) {
                url = getInstance().productImageEndPoint + url;
                url = url.replace("/products/","/");
                return url;
            }
            else{
                String newEndPointImages = BuildConfig.END_POINT_NO_REST;
                return newEndPointImages + url;
            }

        }
        return null;
    }


    public String getLoginServiceURL()
    {
        return endPointSocial +VERB.LOGIN;
    }
    public String getForgotPasswordServiceURL()
    {
        return endPointSocial +VERB.FORGOTPASSWORD;
    }

    public String getCreatePostServiceURL()
    {
        return endPointSocial +VERB.CREATE_POST;
    }

    public String getSetBasicProfileURL()
    {
        return endPointSocial+VERB.SET_BASIC_PROFILE;
    }

    public String getFindPeopleServiceURL(int offset)
    {
        return endPointSocial +VERB.PEOPLE+"/"+offset;
    }
    public String getSocialFeedServiceURL()
    {
        return endPointSocial +VERB.SOCIAL_FEED;
    }
    public String getFolderServiceURL()
    {
        return endPointSocial +VERB.FETCH_GALLERY;
    }

    public String getApplyfilterServiceURL()
    {
        return endPointCommercial +VERB.APPLY_FILTER;
    }


    public String getSocialFeedRefreshServiceURL()
    {
        return endPointSocial +VERB.SOCIAL_FEED;
    }
    public String getUsersSocialFeedServiceURL()
    {
        return endPointSocial +VERB.FETCH_TIME_LINE;
    }
    public String getLogoutServiceURL()
    {
        return endPointSocial +VERB.LOGOUT;
    }
    public String getRegisterServiceURL()
    {
        return endPointSocial +VERB.REGISTER;
    }
    public String getLikeAPostServiceURL()
    {
        return endPointSocial +VERB.LIKE;
    }
    public String getFollowPeopleServiceURL()
    {
        return endPointSocial +VERB.FOLLOW;
    }

    public String getCommentOnPostServiceURL()
    {
        return endPointSocial +VERB.COMMENT;
    }
    public String getFetchCommentsServiceURL()
    {
        return endPointSocial +VERB.FETCH_COMMENTS;
    }
    public String getFetchLikesServiceURL()
    {
        return endPointSocial +VERB.FETCH_LIKES;
    }

    public String getPollPostServiceURL()
    {
        return endPointSocial +VERB.POLL;
    }
    public String getVoteBof3PostServiceURL()
    {
        return endPointSocial +VERB.VOTE_BOF_3;
    }
    public String getFetchVotersServiceURL()
    {
        return endPointSocial +VERB.FETCH_VOTERS;
    }

    public String getNotificationsUrl()
    {
        return endPointSocial +VERB.FETCH_NOTIFICATIONS;
    }

    public static class VERB
    {
        //Author DJphy
        public static final String GET_BASIC_PRODUCT = "getproductbasicinfo";

        public static final String REGISTER="register";
        public static final String LOGIN="login";
        public static final String FORGOTPASSWORD="forgotpassword";

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
        public static final String APPLY_FILTER="applyfilter";


        public static final String GET_PRODUCTS_LIKES = "getproductslikes";
        public static String GET_DESIGNERS_FILTER="getdesignersfilter";
        public static String GET_SCROLL_DATA="getscrolldata";

    }
    public static class LOGIN_PARAM
    {
        public static final String USER_NAME="login_userId";
        public static final String PASSWORD="login_password";
        public static final String USERNAME="username";

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
