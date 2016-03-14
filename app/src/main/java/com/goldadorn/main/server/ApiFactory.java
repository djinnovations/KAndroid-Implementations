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
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class ApiFactory extends ExtractResponse{


    private static final int PRODUCT_SHOWCASE_TYPE = 1;
    private static final int PRODUCTS_TYPE = 2;


    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String IMAGE_URL_HOST = "http://demo.eremotus-portal.com/goldadorn_dev/";
    private static final String HOST_NAME_DEV = "demo.eremotus-portal.com";
    private static final String HOST_NAME_PROD = "demo.eremotus-portal.com";
    public static final String HOST_NAME = Constants.isProduction ? HOST_NAME_PROD : HOST_NAME_DEV;

    private static String getUrl(Context context, UrlBuilder urlBuilder) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority(HOST_NAME);
        builder.appendPath("goldadorn_dev");
        builder.appendPath("rest");
        switch (urlBuilder.mUrlType) {
            case PRODUCT_SHOWCASE_TYPE: {
                builder.appendPath("getdesigners");
                builder.appendPath(urlBuilder.mResponse.mPageCount+"");
                break;
            }
            case PRODUCTS_TYPE: {
                builder.appendPath("getproducts");
                if(((ProductResponse)urlBuilder.mResponse).mCollectionId!=-1) {
                    builder.appendPath("c");
                    builder.appendPath(((ProductResponse)urlBuilder.mResponse).mCollectionId+"");
                    builder.appendPath(urlBuilder.mResponse.mPageCount+"");
                }else  if(((ProductResponse)urlBuilder.mResponse).mSellerId!=-1) {
                    builder.appendPath("d");
                    builder.appendPath(((ProductResponse)urlBuilder.mResponse).mSellerId+"");
                    builder.appendPath(urlBuilder.mResponse.mPageCount+"");
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
        if (response.mCookies==null||response.mCookies.isEmpty()) {
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
        if (response.mCookies==null||response.mCookies.isEmpty()) {
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
}
