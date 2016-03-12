package com.goldadorn.main.server;

import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.TimelineResponse;
import com.goldadorn.main.utils.L;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class Api {

    private static void generateUserCredentials(Context context,
                                                BasicResponse response) {
        List<Cookie> cookies = ((Application) context.getApplicationContext()).getCookies();
        if (!cookies.isEmpty()) {
            for (int i = 0; i < cookies.size(); i++) {
                response.mCookies += cookies.get(i).getName() + "=" + cookies.get(i).getValue() + ";";
            }
        }
        L.d("User credentials "+response.mCookies);
    }

    public static void getProductShowCase(Context context, TimelineResponse response, int retryCount) {
        try {
            generateUserCredentials(context, response);
            ApiFactory.getProductShowCase(context, response);
            if (response.success && response.responseContent != null) {
                DbHelper.writeProductShowcaseData(context, response);
            }
        } catch (Exception e) {
            extractException(context, response, e);
            e.printStackTrace();
        }
    }

    /**
     * this function will handle all the exceptions related to http calls to server. the exception
     * message will be extracted and exception type will be saved in response class.
     *
     * @param context
     * @param response {@link BasicResponse} response object saves the
     *                 exception details
     * @param e        exception to extract for the details.
     */
    private static void extractException(final Context context, BasicResponse response, Exception e) {
        if (e instanceof UnknownHostException) {
            response.responseCode = BasicResponse.IO_EXE;
            response.success = false;
        } else if (e instanceof SQLiteDiskIOException) {
            response.responseCode = BasicResponse.IO_EXE;
            response.success = false;
        } else if (e instanceof IOException) {
            response.responseCode = BasicResponse.IO_EXE;
            response.success = false;
        } else if (e instanceof JSONException) {
            response.responseCode = BasicResponse.JSON_EXE;
            response.success = false;
        }
    }
}
