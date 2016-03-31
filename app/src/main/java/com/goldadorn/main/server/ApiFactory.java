package com.goldadorn.main.server;

import android.content.Context;
import android.net.Uri;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.model.ProductDetail;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.L;
import com.goldadorn.main.utils.NetworkUtilities;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class ApiFactory extends ExtractResponse {


    private static final int PRODUCT_SHOWCASE_TYPE = 1;
    private static final int PRODUCTS_TYPE = 2;
    private static final int PRODUCT_BASIC_INFO_TYPE = 3;
    private static final int PRODUCT_CUSTOMIZATION_TYPE = 4;
    private static final int PRODUCT_PRICE_CUSTOMIZATION_TYPE = 5;
    private static final int GETDESIGNERS_SOCIAL_TYPE = 6;
    private static final int PRODUCTS_SOCIAL_TYPE = 7;
    private static final int CART_DETAIL_TYPE = 8;
    private static final int ADD_CART_TYPE = 9;
    private static final int REMOVE_CART_TYPE = 10;
    private static final int LIKE_TYPE = 11;
    private static final int UNLIKE_TYPE = 12;
    private static final int FOLLOW_TYPE = 13;
    private static final int NOTIFY_PAYMENT_TYPE = 14;

    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String IMAGE_URL_HOST = "http://demo.eremotus-portal.com/";
    private static final String HOST_NAME_DEV = "demo.eremotus-portal.com";
    private static final String HOST_NAME_PROD = "demo.eremotus-portal.com";
    public static final String HOST_NAME = Constants.isProduction ? HOST_NAME_PROD : HOST_NAME_DEV;

    private static String getUrl(Context context, UrlBuilder urlBuilder) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority(HOST_NAME);
        switch (urlBuilder.mUrlType) {
            case PRODUCT_SHOWCASE_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("getdesigners");
                builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                break;
            }
            case ADD_CART_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("addproductstocart");
                break;
            }
            case NOTIFY_PAYMENT_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("notifypaymentstatus");
                break;
            }
            case LIKE_TYPE: {
                builder.appendPath("goldadorn_dev");
                builder.appendPath("rest");
                builder.appendPath("like");
                break;
            }
            case FOLLOW_TYPE: {
                builder.appendPath("goldadorn_dev");
                builder.appendPath("rest");
                builder.appendPath("follow");
                break;
            }
            case UNLIKE_TYPE: {
                builder.appendPath("goldadorn_dev");
                builder.appendPath("rest");
                builder.appendPath("unlike");
                break;
            }
            case REMOVE_CART_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("removeproductsfromcart");
                break;
            }
            case CART_DETAIL_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("getcartdetails");
                builder.appendPath(((Application) context.getApplicationContext()).getUser().id + "");
                builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                break;
            }
            case GETDESIGNERS_SOCIAL_TYPE: {
                builder.appendPath("goldadorn_dev");
                builder.appendPath("rest");
                builder.appendPath("getdesignerssocial");
                builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                break;
            }
            case PRODUCT_BASIC_INFO_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("getproductbasicinfo");
                builder.appendPath(((ProductResponse) urlBuilder.mResponse).productId + "");
                break;
            }
            case PRODUCT_CUSTOMIZATION_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("getproductcustomization");
                builder.appendPath(((ProductResponse) urlBuilder.mResponse).productId + "");
                break;
            }
            case PRODUCT_PRICE_CUSTOMIZATION_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("getpriceforcustomizedproduct");
                break;
            }
            case PRODUCTS_TYPE: {
                builder.appendPath("goldadorn_prod");
                builder.appendPath("rest");
                builder.appendPath("getproducts");
                if (((ProductResponse) urlBuilder.mResponse).collectionId != -1) {
                    builder.appendPath("c");
                    builder.appendPath(((ProductResponse) urlBuilder.mResponse).collectionId + "");
                    builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                } else if (((ProductResponse) urlBuilder.mResponse).userId != -1) {
                    builder.appendPath("d");
                    builder.appendPath(((ProductResponse) urlBuilder.mResponse).userId + "");
                    builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                }
                break;
            }
            case PRODUCTS_SOCIAL_TYPE: {
                builder.appendPath("goldadorn_dev");
                builder.appendPath("rest");
                builder.appendPath("getproductssocial");
                if (((ProductResponse) urlBuilder.mResponse).collectionId != -1) {
                    builder.appendPath("c");
                    builder.appendPath(((ProductResponse) urlBuilder.mResponse).collectionId + "");
                    builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                } else if (((ProductResponse) urlBuilder.mResponse).userId != -1) {
                    builder.appendPath("d");
                    builder.appendPath(((ProductResponse) urlBuilder.mResponse).userId + "");
                    builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                }
                break;
            }
        }
        return builder.build().toString();
    }

    private static HashMap<String, String> getHeaders(Context context, ParamsBuilder paramsBuilder) {
        HashMap<String, String> headers = new HashMap<>();
        if (paramsBuilder.mResponse.mCookies != null)
            headers.put("Cookie", paramsBuilder.mResponse.mCookies);
        return headers;
    }

    protected static void getDesigners(Context context, TimelineResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = PRODUCT_SHOWCASE_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCT_SHOWCASE_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getDesigners " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void getDesignersSocial(Context context, TimelineResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = GETDESIGNERS_SOCIAL_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = GETDESIGNERS_SOCIAL_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getDesignersSocial " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void getProducts(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = PRODUCTS_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCTS_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getProducts " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void getProductsSocial(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = PRODUCTS_SOCIAL_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCTS_SOCIAL_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getProductsSocial " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void getProductBasicInfo(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = PRODUCT_BASIC_INFO_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCT_BASIC_INFO_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getProductBasicInfo " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }


    protected static void getPriceForCustomization(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = PRODUCT_PRICE_CUSTOMIZATION_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCT_PRICE_CUSTOMIZATION_TYPE;

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prodId", response.productToAdd.id);
            ProductDetail productDetail = (ProductDetail) response.productToAdd;
            jsonObject.put(Constants.JsonConstants.PRIMARYMETAL, productDetail.primaryMetal);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETALPURITY, productDetail.primaryMetalPurity);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETALCOLOR, productDetail.primaryMetalColor);
            jsonObject.put(Constants.JsonConstants.CENTERSTONE, productDetail.centerStoneSelected);
            jsonObject.put(Constants.JsonConstants.ACCENTSTONE, productDetail.accentStoneSelected);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getPriceForCustomization " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void getProductCustomization(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = PRODUCT_PRICE_CUSTOMIZATION_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCT_PRICE_CUSTOMIZATION_TYPE;

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prodId", response.productToAdd.id);
            ProductDetail productDetail = (ProductDetail) response.productToAdd;
            jsonObject.put(Constants.JsonConstants.PRIMARYMETAL, productDetail.primaryMetal);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETALPURITY, productDetail.primaryMetalPurity);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETALCOLOR, productDetail.primaryMetalColor);
            jsonObject.put(Constants.JsonConstants.CENTERSTONE, productDetail.centerStoneSelected);
            jsonObject.put(Constants.JsonConstants.ACCENTSTONE, productDetail.accentStoneSelected);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getPriceForCustomization " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }


    protected static void getCartDetails(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = CART_DETAIL_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = CART_DETAIL_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getCartDetails " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }


    protected static void addToCart(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = ADD_CART_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = ADD_CART_TYPE;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prodId", response.productToAdd.id);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("addToCart " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void removeFromCart(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = REMOVE_CART_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = REMOVE_CART_TYPE;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prodId", response.productToAdd.id);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("removeFromCart " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void notifyPayment(Context context, ProductResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = NOTIFY_PAYMENT_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = NOTIFY_PAYMENT_TYPE;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prodId", response.productToAdd.id);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("notifyPayment " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void like(Context context, LikeResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = LIKE_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = LIKE_TYPE;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            if (response.productId != -1) {
                jsonObject.put("product", response.productId);
                jsonObject.put("collection", response.collectionId);
                jsonObject.put("designer", response.userId);
            } else if (response.collectionId != -1) {
                jsonObject.put("collection", response.collectionId);
                jsonObject.put("designer", response.userId);
            }
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("like " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void unLike(Context context, LikeResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = LIKE_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = LIKE_TYPE;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            if (response.productId != -1) {
                jsonObject.put("product", response.productId);
                jsonObject.put("collection", response.collectionId);
                jsonObject.put("designer", response.userId);
            } else if (response.collectionId != -1) {
                jsonObject.put("collection", response.collectionId);
                jsonObject.put("designer", response.userId);
            }
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("unLike " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

    protected static void follow(Context context, LikeResponse response) throws IOException, JSONException {
        if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return;
        }
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = FOLLOW_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = FOLLOW_TYPE;


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("follow", response.userId);
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Response httpResponse = ServerRequest.doPostRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("follow " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }

}
