package com.goldadorn.main.db;

import android.content.ContentValues;
import android.content.Context;

import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.server.ApiFactory;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.ImageFilePath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class DbHelper {

    public static void writeProducts(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray productsArray = new JSONArray(response.responseContent);
            if (productsArray.length() != 0) {
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject productObj = productsArray.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(Tables.Products._ID, productObj.optInt(Constants.JsonConstants.PRODUCTID));
//                    cv.put(Tables.Products.COUNT_LIKES, productObj.optInt(Constants.JsonConstants.LIKECOUNT));
                    cv.put(Tables.Products.NAME, productObj.optString(Constants.JsonConstants.PRODUCTLABEL));
                    cv.put(Tables.Products.USER_ID, productObj.optInt(Constants.JsonConstants.USERID, response.userId));
                    cv.put(Tables.Products.COLLECTION_ID, productObj.optInt(Constants.JsonConstants.COLLECTION_ID, response.collectionId));
                    cv.put(Tables.Products.IMAGEURL, ImageFilePath.getImageUrlForProduct(productObj.optInt(Constants.JsonConstants.PRODUCTID)));
                    cv.put(Tables.Collections.IMAGE_ASPECT_RATIO, productObj.optDouble(Constants.JsonConstants.ASPECTRATIO));

                    cv.put(Tables.Products.PRICE, productObj.optString(Constants.JsonConstants.PRODUCTPRICE));
                    int updatecount = context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optInt(Constants.JsonConstants.PRODUCTID) + ""});
                    if (updatecount == 0)
                        context.getContentResolver().insert(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv);
                }
            }
        }
    }

    public static void writeProductsSocial(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray productsArray = new JSONArray(response.responseContent);
            if (productsArray.length() != 0) {
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject productObj = productsArray.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put(Tables.Products.COUNT_LIKES, productObj.optInt(Constants.JsonConstants.LIKECOUNT));
                    context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optInt(Constants.JsonConstants.PRODUCTID) + ""});
                }
            }
        }
    }

    public static void writeProductBasicInfo(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject productObj = new JSONObject(response.responseContent);
            ContentValues cv = new ContentValues();
            cv.put(Tables.Products.BASIC_INFO, productObj.toString());
            context.getContentResolver().update(Tables.Products.CONTENT_URI, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optLong(Constants.JsonConstants.PRODUCTID) + ""});
        }
    }

    public static void writeProductCustomization(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject productObj = new JSONObject(response.responseContent);
            ContentValues cv = new ContentValues();
            cv.put(Tables.Products.CUSTOMIZATION_INFO, productObj.toString());
            context.getContentResolver().update(Tables.Products.CONTENT_URI, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optLong(Constants.JsonConstants.PRODUCTID) + ""});
        }
    }

    public static void writeDesignersSocial(Context context, TimelineResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray userlist = new JSONArray(response.responseContent);
            if (userlist.length() != 0) {
                for (int i = 0; i < userlist.length(); i++) {
                    JSONObject userObj = userlist.getJSONObject(i);
                    ContentValues usercv = new ContentValues();
                    long userId = userObj.optLong(Constants.JsonConstants.USERID, -1);
                    usercv.put(Tables.Users.COUNT_LIKES, userObj.optInt(Constants.JsonConstants.TOTALLIKES, 0));
                    usercv.put(Tables.Users.COUNT_FOLLOWERS, userObj.optInt(Constants.JsonConstants.FOLLOWERS, 0));
                    usercv.put(Tables.Users.COUNT_FOLLOWING, userObj.optInt(Constants.JsonConstants.FOLLOWING, 0));
                    context.getContentResolver().update(Tables.Users.CONTENT_URI, usercv, Tables.Users._ID + " = ? ", new String[]{userId + ""});
                    if (userObj.has(Constants.JsonConstants.COLLECTIONLIST) && userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST).length() != 0) {
                        JSONArray collarray = userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST);
                        for (int j = 0; j < collarray.length(); j++) {
                            JSONObject collObj = collarray.getJSONObject(j);
                            ContentValues collcv = new ContentValues();
                            collcv.put(Tables.Collections.COUNT_LIKES, collObj.optLong(Constants.JsonConstants.TOTALLIKES));
                            context.getContentResolver().update(Tables.Collections.CONTENT_URI, collcv, Tables.Collections._ID + " = ? ", new String[]{collObj.optLong(Constants.JsonConstants.COLLECTION_ID) + ""});

                        }
                    }
                }
            }
        }
    }

    public static void writeProductShowcaseData(Context context, TimelineResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray userlist = new JSONArray(response.responseContent);
            if (userlist.length() != 0) {
                for (int i = 0; i < userlist.length(); i++) {
                    JSONObject userObj = userlist.getJSONObject(i);
                    ContentValues usercv = new ContentValues();
                    usercv.put(Tables.Users.NAME, userObj.optString(Constants.JsonConstants.USERNAME, null));
                    long userId = userObj.optLong(Constants.JsonConstants.USERID, -1);
                    usercv.put(Tables.Users._ID, userId);
                    String url = userObj.optString(Constants.JsonConstants.USERPIC, null);
                    if (url != null) {
                        url = url.replace("../", ApiFactory.IMAGE_URL_HOST);
                        usercv.put(Tables.Users.IMAGEURL, url);
                    }
                    usercv.put(Tables.Users.COUNT_COLLECTIONS, userObj.optInt(Constants.JsonConstants.COLLECTIONCOUNT, 0));
                    usercv.put(Tables.Users.COUNT_PRODUCTS, userObj.optInt(Constants.JsonConstants.PRODUCTCOUNT, 0));
                    int updatecnt = context.getContentResolver().update(Tables.Users.CONTENT_URI, usercv, Tables.Users._ID + " = ? ", new String[]{userId + ""});
                    if (updatecnt == 0)
                        context.getContentResolver().insert(Tables.Users.CONTENT_URI, usercv);


                    if (userObj.has(Constants.JsonConstants.COLLECTIONLIST) && userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST).length() != 0) {
                        JSONArray collarray = userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST);
                        for (int j = 0; j < collarray.length(); j++) {
                            JSONObject collObj = collarray.getJSONObject(j);
                            ContentValues collcv = new ContentValues();
                            collcv.put(Tables.Collections.USER_ID, userId);
                            collcv.put(Tables.Collections.NAME, collObj.optString(Constants.JsonConstants.COLLECTIONTITLE, null));
                            collcv.put(Tables.Collections.IMAGEURL, ImageFilePath.getImageUrlForCollection(collObj.optLong(Constants.JsonConstants.COLLECTION_ID)));
                            collcv.put(Tables.Collections.IMAGE_ASPECT_RATIO, collObj.optDouble(Constants.JsonConstants.COLLECTIONIMAGEAR));
                            collcv.put(Tables.Collections._ID, collObj.optLong(Constants.JsonConstants.COLLECTION_ID));
                            collcv.put(Tables.Collections.DESCRIPTION, collObj.optString(Constants.JsonConstants.COLLECTIONDESC, null));
                            collcv.put(Tables.Collections.COUNT_PRODUCTS, collObj.optInt(Constants.JsonConstants.COLLECTIONPRODUCTCOUNT, 0));
                            int updatecollcnt = context.getContentResolver().update(Tables.Collections.CONTENT_URI, collcv, Tables.Collections._ID + " = ? ", new String[]{collObj.optLong(Constants.JsonConstants.COLLECTION_ID) + ""});
                            if (updatecollcnt == 0)
                                context.getContentResolver().insert(Tables.Collections.CONTENT_URI, collcv);

                        }
                    }
                }

            }

        }
    }
}
