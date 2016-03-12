package com.goldadorn.main.db;

import android.content.ContentValues;
import android.content.Context;

import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.server.response.TimelineResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class DbHelper {

    public static void writeProductShowcaseData(Context context, TimelineResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray userlist = new JSONArray(response.responseContent);
            if (userlist != null && userlist.length() != 0) {
                for (int i = 0; i < userlist.length(); i++) {
                    JSONObject userObj = userlist.getJSONObject(i);
                    ContentValues usercv = new ContentValues();
                    usercv.put(Tables.Users.NAME, userObj.optString(Constants.JsonConstants.USERNAME, null));
                    long userId = userObj.optLong(Constants.JsonConstants.USERID, -1);
                    usercv.put(Tables.Users._ID, userId);
                    usercv.put(Tables.Users.IMAGEURL, userObj.optString(Constants.JsonConstants.USERPIC, null));
                    usercv.put(Tables.Users.COUNT_LIKES, userObj.optInt(Constants.JsonConstants.TOTALLIKES, 0));
                    usercv.put(Tables.Users.COUNT_FOLLOWERS, userObj.optInt(Constants.JsonConstants.FOLLOWERS, 0));
                    usercv.put(Tables.Users.COUNT_FOLLOWING, userObj.optInt(Constants.JsonConstants.FOLLOWING, 0));
                    usercv.put(Tables.Users.COUNT_COLLECTIONS, userObj.optInt(Constants.JsonConstants.COLLECTIONCOUNT, 0));
                    usercv.put(Tables.Users.COUNT_PRODUCTS, userObj.optInt(Constants.JsonConstants.PRODUCTS, 0));
                    int updatecnt = context.getContentResolver().update(Tables.Users.CONTENT_URI, usercv, Tables.Users._ID + " = ? ", new String[]{userId + ""});
                    if (updatecnt == 0)
                        context.getContentResolver().insert(Tables.Users.CONTENT_URI, usercv);


                    if (userObj.has(Constants.JsonConstants.COLLECTIONLIST) && userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST).length() != 0) {
                        JSONArray collarray = userObj.getJSONArray(Constants.JsonConstants.COLLECTIONLIST);
                        for (int j = 0; j < collarray.length(); j++) {
                            JSONObject collObj = collarray.getJSONObject(j);
                            ContentValues collcv = new ContentValues();
                            collcv.put(Tables.Collections.USER_ID, userId);
                            collcv.put(Tables.Collections.NAME, collObj.optString(Constants.JsonConstants.COLLECTIONTITLE,null));
                            collcv.put(Tables.Collections.IMAGEURL, collObj.optString(Constants.JsonConstants.COLLECTIONIMAGE,null));
                            collcv.put(Tables.Collections.COUNT_PRODUCTS, collObj.optInt(Constants.JsonConstants.COLLECTIONPRODUCTCOUNT,0));
                            context.getContentResolver().insert(Tables.Collections.CONTENT_URI, collcv);

                        }
                    }
                }

            }

        }
    }
}
