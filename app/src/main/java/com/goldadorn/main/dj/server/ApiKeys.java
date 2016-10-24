package com.goldadorn.main.dj.server;

import com.goldadorn.main.BuildConfig;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.utils.URLHelper;

/**
 * Created by COMP on 5/6/2016.
 */
public class ApiKeys {

    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRAPH_VERSION = "graph_version";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SOCIAL_MEDIUM = "social_medium";

    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";

    public static final String RESPONSE_SUCCESS = "Success";
    public static final String RESPONSE_FAIL = "Failed";


    public static final String ENDPOINT_SOCIAL_LOGIN = BuildConfig.END_POINT_SOCIAL_LOGIN;
    public static final String ENDPOINT_PRODUCT_BASIC_INFO = BuildConfig.END_POINT_COMERCIAL +
            URLHelper.VERB.GET_BASIC_PRODUCT + "/";



    public static final String getPinCodeWhizAPI(){
        return "https://www.whizapi.com/api/v2/util/ui/in/indian-city-by-postal-code?";
       //return  "https://www.whizapi.com/api/v2/util/ui/in/indian-city-by-postal-code?pin="+pincode+"&project-app-key="+ Constants.WHIZ_API_PROJECT_KEY;
    }

    public static final String getCartAddressAPI(String type){
        return BuildConfig.END_POINT + VERB.GET_CART_ADDRESS_API + "/" +type;
    }

    public static final String getSetCartAddressAPI(){
        return BuildConfig.END_POINT + VERB.SET_CART_ADDRESS_API;
    }

    public static final String getContactUsAPI(){
        return BuildConfig.END_POINT + VERB.CONTACT_US_API;
    }

    public static final String getUpdateNotificationAPI(){
        return BuildConfig.END_POINT + VERB.UPDATE_NOTIFICATION_COUNT;
    }

    public static final String getUpdatePaymentAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.UPDATE_PAYMENT_API;
    }

    @Deprecated
    public static final String getDesc5NewAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.NEW5_DESC_API + "/" ;
    }

    public static final String getAppointmentAPI(){
        return BuildConfig.END_POINT + VERB.BOOK_APPOINTMENT_API;
    }

    public static final String getHidePostAPI(){
        return BuildConfig.END_POINT + VERB.POST_HIDE;
    }

    public static final String getDeletePostAPI(){
        return BuildConfig.END_POINT + VERB.POST_DELETE;
    }

    public static final String getUrlForSideMenu(String pageName){
       return BuildConfig.SHOWCASE_COLLECTION_HOST + "o/html/"+pageName+".html";
    }


    public static final String getChangePasswordAPI(){
        return BuildConfig.END_POINT + VERB.CHANGE_PASSWORD;
    }

    public static final String getPaymentHashesAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.PAYMENT_HASHES_API;
    }

    public static final String getProductsAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.GET_PRODUCTS;
    }

    public static final String getFollowerListAPI(int offset){
        return BuildConfig.END_POINT + VERB.GET_FOLLOWERS + "/"+ Application.getInstance().getUser().id
                +"/"+String.valueOf(offset);
    }

    public static final String getPriceForCustomizedProdAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.CUSTOMIZE_PRICE_API;
    }

    public static final String getProdInfoMetalStoneAPI(int prodId){
        return BuildConfig.END_POINT_COMERCIAL + VERB.INFO_METAL_STONE_API + "/" + String.valueOf(prodId);
    }

    public static final String getAddtoCartV27(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.ADD_TO_CART_V27;
    }

    public static final String getSendFcmTokenAAPI(){
        return BuildConfig.END_POINT + VERB.SEND_FCM_TOKEN_API;
    }

    public static final String getAutoMinCustomizeAPI(String prodId){
        return BuildConfig.END_POINT_COMERCIAL + VERB.MIN_CUST_API + "/"+prodId;
    }

    public static final String getCancelReqAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.CANCEL_REQ_API;
    }

    public static final String getDesignerSocial(String desId){
        return BuildConfig.END_POINT + VERB.DESIGNGER_SOCIAL_API + "/" + desId;
    }

    public static final String getSearchAPI(boolean isSocial){
        if (isSocial)
        return BuildConfig.END_POINT + VERB.SOCIAL_SEARCH_API;
        return BuildConfig.END_POINT_COMERCIAL + VERB.ECOM_SEARCH_API;
    }

    public static final String getLikesAPI(String userId){
        return BuildConfig.END_POINT + VERB.LIKES_API + "/" + userId;
    }
    //http://107.170.48.64/goldadorn_prod/rest/searchcommerce

    /*http://107.170.48.64/goldadorn_prod/rest/getproductbasicinfov27/373
http://107.170.48.64/goldadorn_prod/rest/getproductmetalstoneinfov27/373*/

    private static final class VERB{
        private static final String GET_CART_ADDRESS_API = "getuseraddress";
        private static final String SET_CART_ADDRESS_API = "setuseraddress";
        private static final String CONTACT_US_API = "contactus";
        private static final String UPDATE_NOTIFICATION_COUNT = "updatenotification";
        private static final String UPDATE_PAYMENT_API = "notifypaymentstatusv27";
        @Deprecated
        private static final String NEW5_DESC_API = "getproductbasicinfo";
        private static final String BOOK_APPOINTMENT_API = "bookappointment";
        private static final String POST_HIDE = "hidepost";
        private static final String POST_DELETE = "deletepost";
        private static final String CHANGE_PASSWORD = "changepassword";
        private static final String PAYMENT_HASHES_API = "generatepaymenthash";
        private static final String GET_PRODUCTS = "getproducts";
        private static final String GET_FOLLOWERS = "fetchfollowers";
        private static final String CUSTOMIZE_PRICE_API = "getpriceforcustomizedproduct";
        private static final String INFO_METAL_STONE_API = "getproductmetalstoneinfov27";
        private static final String ADD_TO_CART_V27 = "addproductstocartv27";
        private static final String SEND_FCM_TOKEN_API = "setCloudMessagingToken";
        private static final String MIN_CUST_API = "getproductminconfigurationv28";
        private static final String CANCEL_REQ_API = "cancelordersv28";
        private static final String DESIGNGER_SOCIAL_API = "getspecificdesignersocial";
        private static final String ECOM_SEARCH_API = "searchcommerce";
        private static final String SOCIAL_SEARCH_API = "searchsocial";
        private static final String LIKES_API = "mylikes";
    }
}
