package com.goldadorn.main.dj.server;

import com.goldadorn.main.BuildConfig;
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


    public static final String getChangePasswordAPI(){
        return BuildConfig.END_POINT + VERB.CHANGE_PASSWORD;
    }

    public static final String getPaymentHashesAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.PAYMENT_HASHES_API;
    }

    public static final String getProductsAPI(){
        return BuildConfig.END_POINT_COMERCIAL + VERB.GET_PRODUCTS;
    }

    private static final class VERB{
        private static final String GET_CART_ADDRESS_API = "getuseraddress";
        private static final String SET_CART_ADDRESS_API = "setuseraddress";
        private static final String CONTACT_US_API = "contactus";
        private static final String UPDATE_NOTIFICATION_COUNT = "updatenotification";
        private static final String UPDATE_PAYMENT_API = "notifypaymentstatus";
        @Deprecated
        private static final String NEW5_DESC_API = "getproductbasicinfo";
        private static final String BOOK_APPOINTMENT_API = "bookappointment";
        private static final String POST_HIDE = "hidepost";
        private static final String POST_DELETE = "deletepost";
        private static final String CHANGE_PASSWORD = "changepassword";
        private static final String PAYMENT_HASHES_API = "generatepaymenthash";
        private static final String GET_PRODUCTS = "getproducts";
    }
}
