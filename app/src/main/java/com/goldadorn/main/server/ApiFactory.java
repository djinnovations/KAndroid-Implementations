package com.goldadorn.main.server;

import android.content.Context;
import android.net.Uri;

import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.server.response.BasicResponse;
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


    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String IMAGE_URL_HOST = "http://demo.eremotus-portal.com/goldadorn_prod/";
    private static final String HOST_NAME_DEV = "demo.eremotus-portal.com";
    private static final String HOST_NAME_PROD = "demo.eremotus-portal.com";
    public static final String HOST_NAME = Constants.isProduction ? HOST_NAME_PROD : HOST_NAME_DEV;

    private static String getUrl(Context context, UrlBuilder urlBuilder) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority(HOST_NAME);
        builder.appendPath("goldadorn_prod");
        builder.appendPath("rest");
        switch (urlBuilder.mUrlType) {
            case PRODUCT_SHOWCASE_TYPE: {
                builder.appendPath("getdesigners");
                builder.appendPath(urlBuilder.mResponse.mPageCount + "");
                break;
            }
            case PRODUCT_BASIC_INFO_TYPE: {
                builder.appendPath("getproductbasicinfo");
                builder.appendPath(((ProductResponse) urlBuilder.mResponse).productId + "");
                break;
            }
            case PRODUCT_CUSTOMIZATION_TYPE: {
                builder.appendPath("getproductcustomization");
                builder.appendPath(((ProductResponse) urlBuilder.mResponse).productId + "");
                break;
            }
            case PRODUCT_PRICE_CUSTOMIZATION_TYPE: {
                builder.appendPath("getpriceforcustomizedproduct");
                break;
            }
            case PRODUCTS_TYPE: {
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
        }
        return builder.build().toString();
    }

    private static HashMap<String, String> getHeaders(Context context, ParamsBuilder paramsBuilder) {
        HashMap<String, String> headers = new HashMap<>();
        if (paramsBuilder.mResponse.mCookies != null)
            headers.put("Cookie", paramsBuilder.mResponse.mCookies);
        return headers;
    }

    protected static void getProductShowCase(Context context, TimelineResponse response) throws IOException, JSONException {
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
            L.d("getProductShowCase " + "Code :" + response.responseCode + " content", response.responseContent.toString());
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
            jsonObject.put("prodId", response.productDetail.mId);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETAL, response.productDetail.mPrimaryMetal);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETALPURITY, response.productDetail.mPrimaryMetalPurity);
            jsonObject.put(Constants.JsonConstants.PRIMARYMETALCOLOR, response.productDetail.mPrimaryMetalColor);
            jsonObject.put(Constants.JsonConstants.CENTERSTONE, response.productDetail.mCenterStoneSelected);
            jsonObject.put(Constants.JsonConstants.ACCENTSTONE1, response.productDetail.mAccentStoneSelected);

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
            urlBuilder.mUrlType = PRODUCT_CUSTOMIZATION_TYPE;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = PRODUCT_CUSTOMIZATION_TYPE;


            Response httpResponse = ServerRequest.doGetRequest(context, getUrl(context, urlBuilder), getHeaders(context, paramsBuilder));
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getProductCustomization " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            extractBasicResponse(context, response);
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
        }
    }
}
