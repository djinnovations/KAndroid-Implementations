package com.goldadorn.main.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.ApiFactory;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.server.response.TimelineResponse;

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
                    int productID = productObj.optInt(Constants.JsonConstants.PRODUCTID);
                    if (response.writeToDb) {
                        ContentValues cv = new ContentValues();
                        cv.put(Tables.Products._ID, productID);
//                    cv.put(Tables.Products.COUNT_LIKES, productObj.optInt(Constants.JsonConstants.LIKECOUNT));
                        cv.put(Tables.Products.NAME, productObj.optString(Constants.JsonConstants.PRODUCTLABEL));
                        cv.put(Tables.Products.DESCRIPTION, productObj.optString(Constants.JsonConstants.PRODUCTDESC));
                        cv.put(Tables.Collections.IMAGE_ASPECT_RATIO, productObj.optDouble(Constants.JsonConstants.ASPECTRATIO));

                        cv.put(Tables.Products.PRICE, productObj.optString(Constants.JsonConstants.PRODUCTPRICE));
                        cv.put(Tables.Products.PRICEUNIT, productObj.optString(Constants.JsonConstants.PRODUCTPRICEUNITS));
                        //cv.put(Tables.Products.DEF_METAl, productObj.optString("defaultMetal"));
                        context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{productObj.optInt(Constants.JsonConstants.PRODUCTID) + ""});
                    }
                    if (response.productArray != null) {
                        Product temp = new Product(productID);
                        int index = response.productArray.indexOf(temp);
                        if (index != -1) {
                            Product p = response.productArray.get(index);
                            p.name = productObj.optString(Constants.JsonConstants.PRODUCTLABEL);
                            p.description = productObj.optString(Constants.JsonConstants.PRODUCTDESC);
                            p.image_a_r = (float) productObj.optDouble(Constants.JsonConstants.ASPECTRATIO);
                            p.unitPrice = productObj.optLong(Constants.JsonConstants.PRODUCTPRICE);
                            p.priceUnit = productObj.optString(Constants.JsonConstants.PRODUCTPRICEUNITS);
                            //p.defMetal = productObj.optString("defaultMetal");
                        }
                    }
                }
                if (response.writeToDb)
                    context.getContentResolver().notifyChange(Tables.Products.CONTENT_URI, null);
            }
        }

    }

    public static int productCountPerCall = -1;
    public static void writeProductsSocial(Context context, ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject dataObj = new JSONObject(response.responseContent);
            if (dataObj.has("products")) {
                JSONArray productsArray = dataObj.getJSONArray("products");
                if (productsArray.length() != 0) {
                    if (response.mPageCount == 0)
                        context.getContentResolver().delete(Tables.Products.CONTENT_URI_NO_NOTIFICATION, null, null);
                    productCountPerCall = productsArray.length();
                    for (int i = 0; i < productsArray.length(); i++) {

                        JSONObject productObj = productsArray.getJSONObject(i);
                        ContentValues cv = new ContentValues();
                        cv.put(Tables.Products._ID, productObj.optInt(Constants.JsonConstants.PRODUCT_ID, 0));
                        cv.put(Tables.Products.USER_ID, productObj.optInt(Constants.JsonConstants.USERID, response.userId));
                        cv.put(Tables.Products.COLLECTION_ID, productObj.optInt(Constants.JsonConstants.COLLECTION_ID, response.collectionId));
                        cv.put(Tables.Products.COUNT_LIKES, productObj.optInt(Constants.JsonConstants.LIKECOUNT));
                        cv.put(Tables.Products.IS_LIKED, productObj.optInt(Constants.JsonConstants.ISLIKED, 0));
                        updateInsert(context, Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv);
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

    public static SparseArray<Integer> mapOfUserIds;;

    public static void writeDesignersSocial(Context context, TimelineResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject dataObj = new JSONObject(response.responseContent);
            if (dataObj.has(Constants.JsonConstants.DESIGNERS)) {
                JSONArray userlist = dataObj.getJSONArray(Constants.JsonConstants.DESIGNERS);
                mapOfUserIds = new SparseArray<>();
                for (int i= 0; i<userlist.length(); i++){
                    try {
                        mapOfUserIds.put(userlist.getJSONObject(i).getInt(Constants.JsonConstants.USERID), i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (userlist.length() != 0) {
                    if (response.mPageCount == 0) {
                        context.getContentResolver().delete(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, null, null);
                        context.getContentResolver().delete(Tables.Users.CONTENT_URI_NO_NOTIFICATION, null, null);
                    }
                    for (int i = 0; i < userlist.length(); i++) {
                        JSONObject userObj = userlist.getJSONObject(i);
                        int userId = userObj.optInt(Constants.JsonConstants.USERID, -1);
                        JSONObject jsonforpost = new JSONObject();
                        jsonforpost.put("desgnId", userId);
                        ContentValues cv = new ContentValues();
                        cv.put(Tables.Users._ID, userId);
                        cv.put(Tables.Users.TYPE, User.TYPE_DESIGNER);
                        cv.put(Tables.Users.NAME, userObj.optString(Constants.JsonConstants.USERNAME));
                        cv.put(Tables.Users.TRENDING, userObj.optInt(Constants.JsonConstants.ISTRENDING));
                        cv.put(Tables.Users.FEATURED, userObj.optInt(Constants.JsonConstants.ISFEATURED));
                        cv.put(Tables.Users.COUNT_FOLLOWERS, userObj.optInt("followerCount"));
                        cv.put(Tables.Users.COUNT_FOLLOWING, userObj.optInt("followingCount"));
                        cv.put(Tables.Users.COUNT_LIKES, userObj.optInt(Constants.JsonConstants.TOTALLIKES));
                        cv.put(Tables.Users.COUNT_BOOKAPPOINT, userObj.optInt("numAppts"));//changed DJphy

                        cv.put(Tables.Users.COUNT_COLLECTIONS, userObj.optInt(Constants.JsonConstants.COLLECTIONCOUNT));
                        cv.put(Tables.Users.COUNT_PRODUCTS, userObj.optInt(Constants.JsonConstants.PRODUCTCOUNT));
                        cv.put(Tables.Users.IS_FOLLOWING, userObj.optInt(Constants.JsonConstants.ISFOLLOWING));
                        cv.put(Tables.Users.IS_LIKED, userObj.optInt(Constants.JsonConstants.ISLIKED));
                        String url = userObj.optString(Constants.JsonConstants.USERPIC, null);
                        if (url != null) {
                            url = url.replace("../", ApiFactory.IMAGE_URL_HOST);
                        }
                        cv.put(Tables.Users.IMAGEURL, url);
                        cv.put(Tables.Users.DATAVERSION, System.currentTimeMillis());
                        updateInsert(context, Tables.Users.CONTENT_URI_NO_NOTIFICATION, cv);
                        if (userObj.has(Constants.JsonConstants.COLLECTIONLIST) && userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST).length() != 0) {
                            JSONArray collarray = userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST);
                            JSONArray tempforpost = new JSONArray();
                            for (int j = 0; j < collarray.length(); j++) {
                                JSONObject collObj = collarray.getJSONObject(j);
                                long collId = collObj.optLong(Constants.JsonConstants.COLLECTION_ID);
                                ContentValues collcv = new ContentValues();
                                collcv.put(Tables.Collections._ID, collId);
                                collcv.put(Tables.Collections.USER_ID, userId);
                                collcv.put(Tables.Collections.COUNT_LIKES, collObj.optLong(Constants.JsonConstants.TOTALLIKES));
                                collcv.put(Tables.Collections.COUNT_BOOKAPPOINT, collObj.optInt("numAppts"));
                                collcv.put(Tables.Collections.IS_LIKED, collObj.optInt(Constants.JsonConstants.ISLIKED, 0));
                                int updatecollcnt = context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, collcv, Tables.Collections._ID + " = ? ", new String[]{collId + ""});
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

    private static void updateInsert(Context context, Uri uri, ContentValues cv) {
        String id = cv.getAsString("_id");
        int cnt = context.getContentResolver().update(uri, cv, "_id = " + id, null);
        if (cnt == 0) {
            try {
                context.getContentResolver().insert(uri, cv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateInsertLikes(Context context,int mUser_d,int FollwerCount,int isFollow) {
        ContentValues collcv = new ContentValues();
       // collcv.put(Tables.Users._ID, mUser_d);
        collcv.put(Tables.Users.COUNT_FOLLOWERS, FollwerCount);
        collcv.put(Tables.Users.IS_FOLLOWING, isFollow);

        int updatecollcnt = context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, collcv, Tables.Users._ID + " = ? ", new String[]{(mUser_d) + ""});

    }

    public static void writeProductShowcaseData(Context context, TimelineResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray userlist = new JSONArray(response.responseContent);
            if (userlist.length() != 0) {
                for (int i = 0; i < userlist.length(); i++) {
                    JSONObject userObj = userlist.getJSONObject(i);
                    ContentValues usercv = new ContentValues();
                    long userId = userObj.optLong(Constants.JsonConstants.USERID, -1);
                    usercv.put(Tables.Users._ID, userId);
                    String url = userObj.optString(Constants.JsonConstants.USERPIC, null);
                    if (url != null) {
                        url = url.replace("../", ApiFactory.IMAGE_URL_HOST);
                        usercv.put(Tables.Users.IMAGEURL, url);
                    }
                    usercv.put(Tables.Users.COUNT_COLLECTIONS, userObj.optInt(Constants.JsonConstants.COLLECTIONCOUNT, 0));
                    //usercv.put(Tables.Users.COUNT_PRODUCTS, userObj.optInt(Constants.JsonConstants.PRODUCTCOUNT, 0));
                    int updatecnt = context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, usercv, Tables.Users._ID + " = ? ", new String[]{userId + ""});
                    if (updatecnt == 0)
                        context.getContentResolver().insert(Tables.Users.CONTENT_URI_NO_NOTIFICATION, usercv);


                    if (userObj.has(Constants.JsonConstants.COLLECTIONLIST) && userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST).length() != 0) {
                        JSONArray collarray = userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST);
                        for (int j = 0; j < collarray.length(); j++) {
                            JSONObject collObj = collarray.getJSONObject(j);
                            ContentValues collcv = new ContentValues();
                            collcv.put(Tables.Collections._ID, collObj.optLong(Constants.JsonConstants.COLLECTION_ID));
                            collcv.put(Tables.Collections.USER_ID, userId);

                            /*Cursor cursor = context.getContentResolver().query(Tables.Collections.CONTENT_URI, null, Tables.Collections._ID + " = ? ", new String[]{Constants.JsonConstants.COLLECTION_ID + ""}, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                collcv.put(Tables.Collections.IS_LIKED, cursor.getInt(cursor.getColumnIndex(Tables.Collections.IS_LIKED)));
                            }*/

                            collcv.put(Tables.Collections.NAME, collObj.optString(Constants.JsonConstants.COLLECTIONTITLE, null));
                            collcv.put(Tables.Collections.IMAGE_ASPECT_RATIO, collObj.optDouble(Constants.JsonConstants.COLLECTIONIMAGEAR));
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



    public static void writeFollow(Context context, LikeResponse response) {
        Log.e("iiii--follow-",response.userId+"");
        ContentValues cv = new ContentValues();
        if (response.productId != -1) {
            cv.put(Tables.Products.IS_LIKED, 1);
            Cursor cursor = context.getContentResolver().query(Tables.Products.CONTENT_URI, null, Tables.Products._ID + " = ? ", new String[]{response.productId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Products.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Products.COUNT_LIKES)) + 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{response.productId + ""});

        } else if (response.collectionId != -1) {
            cv.put(Tables.Collections.IS_LIKED, 1);
            Cursor cursor = context.getContentResolver().query(Tables.Collections.CONTENT_URI, null, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Collections.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_LIKES)) + 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""});
        } else if (response.userId != -1) {
            cv.put(Tables.Users.IS_FOLLOWING, 1);
            Cursor cursor = context.getContentResolver().query(Tables.Users.CONTENT_URI, null, Tables.Users._ID + " = ? ", new String[]{response.userId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Users.COUNT_FOLLOWERS, (cursor.getInt(cursor.getColumnIndex(Tables.Users.COUNT_FOLLOWERS)) + 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Users._ID + " = ? ", new String[]{response.userId + ""});
        }
    }

    public static void writeUnFollow(Context context, LikeResponse response) {
        Log.e("iiii--Unfollow-",response.userId+"");
        ContentValues cv = new ContentValues();
        if (response.productId != -1) {
            cv.put(Tables.Products.IS_LIKED, 0);
            Cursor cursor = context.getContentResolver().query(Tables.Products.CONTENT_URI, null, Tables.Products._ID + " = ? ", new String[]{response.productId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Products.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Products.COUNT_LIKES)) - 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{response.productId + ""});
        } else if (response.collectionId != -1) {
            cv.put(Tables.Collections.IS_LIKED, 0);
            Cursor cursor = context.getContentResolver().query(Tables.Collections.CONTENT_URI, null, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Collections.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_LIKES)) - 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""});
        } else if (response.userId != -1) {
            cv.put(Tables.Users.IS_FOLLOWING, 0);
            Cursor cursor = context.getContentResolver().query(Tables.Users.CONTENT_URI, null, Tables.Users._ID + " = ? ", new String[]{response.userId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Users.COUNT_FOLLOWERS, (cursor.getInt(cursor.getColumnIndex(Tables.Users.COUNT_FOLLOWERS)) - 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Users._ID + " = ? ", new String[]{response.userId + ""});
        }
    }


    public static void writeLike(Context context, LikeResponse response) {
        Log.e("iiii---",response.userId+"");
        ContentValues cv = new ContentValues();
        if (response.productId != -1) {
            cv.put(Tables.Products.IS_LIKED, 1); //0 = no action; 1 = liked and -1 is disliked.
            Cursor cursor = context.getContentResolver().query(Tables.Products.CONTENT_URI, null, Tables.Products._ID + " = ? ", new String[]{response.productId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Products.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Products.COUNT_LIKES)) + response.currentLikeCountToWrite));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{response.productId + ""});

        } else if (response.collectionId != -1) {
            cv.put(Tables.Collections.IS_LIKED, 1);
            Cursor cursor = context.getContentResolver().query(Tables.Collections.CONTENT_URI, null, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Collections.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_LIKES)) + 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""});
        } else if (response.userId != -1) {
            cv.put(Tables.Users.IS_LIKED, 1);
            Cursor cursor = context.getContentResolver().query(Tables.Users.CONTENT_URI, null, Tables.Users._ID + " = ? ", new String[]{response.userId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Users.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Users.COUNT_LIKES)) + 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Users._ID + " = ? ", new String[]{response.userId + ""});
        }
    }


    public static void writeBookingCount(final int collId){
        Runnable runnable = new Runnable() {
            public void run() {
                ContentValues cv = new ContentValues();
                if (collId != -1) {
                    //0 = no action; 1 = liked and -1 is disliked.
                    Cursor cursor = Application.getInstance().getContentResolver().query(Tables.Collections.CONTENT_URI, null, Tables.Collections._ID + " = ? ", new String[]{collId + ""}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        cv.put(Tables.Collections.COUNT_BOOKAPPOINT, (cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_BOOKAPPOINT)) + 1));
                    }
                    if (cursor != null)
                        cursor.close();
                    Application.getInstance().getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Collections._ID + " = ? ", new String[]{collId + ""});

                }
            }
        };
        new Thread(runnable).start();
    }

    public static void writeUnLike(Context context, LikeResponse response) {
        ContentValues cv = new ContentValues();
        if (response.productId != -1) {
            cv.put(Tables.Products.IS_LIKED, -1);//here 0 = no action; 1 = liked and -1 is disliked.
            Cursor cursor = context.getContentResolver().query(Tables.Products.CONTENT_URI, null, Tables.Products._ID + " = ? ", new String[]{response.productId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Products.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Products.COUNT_LIKES)) - response.currentLikeCountToWrite));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Products.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Products._ID + " = ? ", new String[]{response.productId + ""});
        } else if (response.collectionId != -1) {
            cv.put(Tables.Collections.IS_LIKED, 0);
            Cursor cursor = context.getContentResolver().query(Tables.Collections.CONTENT_URI, null, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Collections.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_LIKES)) - 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Collections.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Collections._ID + " = ? ", new String[]{response.collectionId + ""});
        } else if (response.userId != -1) {
            cv.put(Tables.Users.IS_LIKED, 0);
            Cursor cursor = context.getContentResolver().query(Tables.Users.CONTENT_URI, null, Tables.Users._ID + " = ? ", new String[]{response.userId + ""}, null);
            if (cursor != null && cursor.moveToFirst()) {
                cv.put(Tables.Users.COUNT_LIKES, (cursor.getInt(cursor.getColumnIndex(Tables.Users.COUNT_LIKES)) - 1));
            }
            if (cursor != null)
                cursor.close();
            context.getContentResolver().update(Tables.Users.CONTENT_URI_NO_NOTIFICATION, cv, Tables.Users._ID + " = ? ", new String[]{response.userId + ""});
        }
    }
}
