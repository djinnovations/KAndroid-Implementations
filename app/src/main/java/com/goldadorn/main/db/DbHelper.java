package com.goldadorn.main.db;

import android.content.ContentValues;
import android.content.Context;

import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.ApiFactory;
import com.goldadorn.main.server.response.LikeResponse;
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
                    cv.put(Tables.Products.DESCRIPTION, productObj.optString(Constants.JsonConstants.PRODUCTDESC));
                    cv.put(Tables.Products.USER_ID, productObj.optInt(Constants.JsonConstants.USERID, response.userId));
                    cv.put(Tables.Products.COLLECTION_ID, productObj.optInt(Constants.JsonConstants.COLLECTION_ID, response.collectionId));
                    cv.put(Tables.Products.IMAGEURL, ImageFilePath.getImageUrlForProduct(productObj.optInt(Constants.JsonConstants.PRODUCTID)));
                    cv.put(Tables.Collections.IMAGE_ASPECT_RATIO, productObj.optDouble(Constants.JsonConstants.ASPECTRATIO));

                    cv.put(Tables.Products.PRICE, productObj.optString(Constants.JsonConstants.PRODUCTPRICE));
                    cv.put(Tables.Products.PRICEUNIT, productObj.optString(Constants.JsonConstants.PRODUCTPRICEUNITS));
                    int updatecount = context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optInt(Constants.JsonConstants.PRODUCTID) + ""});
                    if (updatecount == 0)
                        context.getContentResolver().insert(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv);
                }
                context.getContentResolver().notifyChange(Tables.Products.CONTENT_URI, null);
            }
        }
    }

    public static void writeProductsSocial(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject dataObj = new JSONObject(response.responseContent);
            if (dataObj.has("products")) {
                JSONArray productsArray = dataObj.getJSONArray("products");
                if (productsArray.length() != 0) {
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productObj = productsArray.getJSONObject(i);
                        ContentValues cv = new ContentValues();
                        cv.put(Tables.Products.COUNT_LIKES, productObj.optInt(Constants.JsonConstants.LIKECOUNT));
                        cv.put(Tables.Products.IS_LIKED, productObj.optInt(Constants.JsonConstants.ISLIKED, 0));
                        int updatecount = context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optInt(Constants.JsonConstants.PRODUCTID) + ""});
                        if (updatecount == 0)
                            context.getContentResolver().insert(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv);
                        response.idsForProducts.put(productObj.optInt(Constants.JsonConstants.PRODUCTID));
                    }
                    context.getContentResolver().notifyChange(Tables.Products.CONTENT_URI, null);
                }
            }
        }
    }

    public static void writeProductBasicInfo(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject productObj = new JSONObject(response.responseContent);
            ContentValues cv = new ContentValues();
            cv.put(Tables.Products.BASIC_INFO, productObj.toString());
            response.info = ProductInfo.extractFromJson(productObj);
            context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optLong(Constants.JsonConstants.PRODUCTID) + ""});
        }
    }

    public static void writeProductCustomization(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject productObj = new JSONObject(response.responseContent);
            ContentValues cv = new ContentValues();
            cv.put(Tables.Products.CUSTOMIZATION_INFO, productObj.toString());
            response.options = ProductOptions.extractCustomization(productObj);
            context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optLong(Constants.JsonConstants.PRODUCTID) + ""});
        }
    }

    public static void writeDesignersSocial(Context context, TimelineResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject dataObj = new JSONObject(response.responseContent);
            if (dataObj.has(Constants.JsonConstants.DESIGNERS)) {
                JSONArray userlist = dataObj.getJSONArray(Constants.JsonConstants.DESIGNERS);
                if (userlist.length() != 0) {
                    for (int i = 0; i < userlist.length(); i++) {
                        JSONObject userObj = userlist.getJSONObject(i);
                        int userId = userObj.optInt(Constants.JsonConstants.USERID, -1);

                        JSONObject jsonforpost = new JSONObject();
                        jsonforpost.put("desgnId", userId);
                        User user = UserInfoCache.getInstance(context).getUserInfo(userId, true);
                        if (user == null) user = new User(userId, User.TYPE_DESIGNER);
                        user.name = userObj.optString(Constants.JsonConstants.USERNAME);
                        user.featured = userObj.optInt(Constants.JsonConstants.ISFEATURED) == 1;
                        user.trending = userObj.optInt(Constants.JsonConstants.ISTRENDIND) == 1;
                        user.likes_cnt = userObj.optInt(Constants.JsonConstants.TOTALLIKES, 0);
                        user.followers_cnt = userObj.optInt(Constants.JsonConstants.FOLLOWERS, 0);
                        user.following_cnt = userObj.optInt(Constants.JsonConstants.FOLLOWING, 0);
                        String url = userObj.optString(Constants.JsonConstants.USERPIC, null);
                        if (url != null) {
                            url = url.replace("../", ApiFactory.IMAGE_URL_HOST);
                        }
                        user.imageUrl = url;
                        UserInfoCache.updateCacheAndDb(context, user);
                        if (userObj.has(Constants.JsonConstants.COLLECTIONLIST) && userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST).length() != 0) {
                            JSONArray collarray = userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST);
                            JSONArray tempforpost = new JSONArray();
                            for (int j = 0; j < collarray.length(); j++) {
                                JSONObject collObj = collarray.getJSONObject(j);
                                ContentValues collcv = new ContentValues();
                                collcv.put(Tables.Collections.COUNT_LIKES, collObj.optLong(Constants.JsonConstants.TOTALLIKES));
                                collcv.put(Tables.Collections.IS_LIKED, collObj.optInt(Constants.JsonConstants.ISLIKED, 0));
                                int updatecollcnt = context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, collcv, Tables.Collections._ID + " = ? ", new String[]{collObj.optLong(Constants.JsonConstants.COLLECTION_ID) + ""});
                                if (updatecollcnt == 0)
                                    context.getContentResolver().insert(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, collcv);
                                tempforpost.put(collObj.optLong(Constants.JsonConstants.COLLECTION_ID));
                            }
                            jsonforpost.put("collIds", tempforpost);
                        }
                        response.idsForProducts.put(jsonforpost);
                    }
                    context.getContentResolver().notifyChange(Tables.Collections.CONTENT_URI, null);
                    context.getContentResolver().notifyChange(Tables.Users.CONTENT_URI, null);
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
                    int updatecnt = context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, usercv, Tables.Users._ID + " = ? ", new String[]{userId + ""});
                    if (updatecnt == 0)
                        context.getContentResolver().insert(Tables.Users.CONTENT_URI_NO_NOTIFICATION, usercv);


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
                            collcv.put(Tables.Collections.CATEGORY, collObj.optString(Constants.JsonConstants.COLLECTIONCATEGORY, null));
                            collcv.put(Tables.Collections.COUNT_PRODUCTS, collObj.optInt(Constants.JsonConstants.COLLECTIONPRODUCTCOUNT, 0));
                            int updatecollcnt = context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, collcv, Tables.Collections._ID + " = ? ", new String[]{collObj.optLong(Constants.JsonConstants.COLLECTION_ID) + ""});
                        }
                    }
                }
                context.getContentResolver().notifyChange(Tables.Collections.CONTENT_URI, null);
                context.getContentResolver().notifyChange(Tables.Users.CONTENT_URI, null);
            }


        }
    }

    public static void writeLike(Context context, LikeResponse response) {
        ContentValues cv = new ContentValues();
        if (response.productId != -1) {
            cv.put(Tables.Products.IS_LIKED, 1);
            context.getContentResolver().update(Tables.Products.CONTENT_URI, cv, Tables.Products._ID + " = ? ", new String[]{response.productId + ""});
        } else if (response.collectionId != -1) {
            cv.put(Tables.Collections.IS_LIKED, 1);
            context.getContentResolver().update(Tables.Collections.CONTENT_URI, cv, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""});
        } else if (response.userId != -1) {
            cv.put(Tables.Users.IS_LIKED, 1);
            context.getContentResolver().update(Tables.Users.CONTENT_URI, cv, Tables.Users._ID + " = ? ", new String[]{response.userId + ""});
        }
    }

    public static void writeUnLike(Context context, LikeResponse response) {
        ContentValues cv = new ContentValues();
        if (response.productId != -1) {
            cv.put(Tables.Products.IS_LIKED, 0);
            context.getContentResolver().update(Tables.Products.CONTENT_URI, cv, Tables.Products._ID + " = ? ", new String[]{response.productId + ""});
        } else if (response.collectionId != -1) {
            cv.put(Tables.Collections.IS_LIKED, 0);
            context.getContentResolver().update(Tables.Collections.CONTENT_URI, cv, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""});
        } else if (response.userId != -1) {
            cv.put(Tables.Users.IS_LIKED, 0);
            context.getContentResolver().update(Tables.Users.CONTENT_URI, cv, Tables.Users._ID + " = ? ", new String[]{response.userId + ""});
        }
    }
}
