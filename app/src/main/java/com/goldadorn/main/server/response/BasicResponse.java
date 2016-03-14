package com.goldadorn.main.server.response;

import android.content.Context;

import java.io.Serializable;


/**
 * Created by Nithin John on 26-07-2015.
 */
public class BasicResponse implements Serializable {

    public final static int OKAY = 200;
    public final static int CREATED = 201;
    public final static int ACCEPTED = 202;
    public final static int BAD_REQUEST = 400;
    public final static int FORBIDDEN = 403;
    public final static int NOT_FOUND = 404;
    public final static int REQUEST_TIMEOUT = 408;
    public final static int DUPLICATE_ENTRY = 409;
    public final static int TOKEN_EXPIRED = 412;
    public final static int INTERNAL_SERVER_ERROR = 500;
    public final static int THROTTLED = 503;


    public final static int NO_RESULTS = 1004;
    public final static int IO_EXE = 1001; //response code for SQLiteDiskIOException, IOException
    public final static int JSON_EXE = 1003;

    public String mCookies = "";
    public boolean success;
    public String responseContent;
    public int responseCode;
    public int mPageCount = 0;



    public String getCustomErrorMessage(Context context) {
//        switch (responseCode) {
//            case OKAY: {
//                return customErrorMessage;
//            }
//            case BAD_REQUEST: {
//                return context.getString(R.string.bad_request);
//            }
//            case FORBIDDEN: {
//                return context.getString(R.string.forbidden);
//            }
//            case NOT_FOUND: {
//                return context.getString(R.string.not_implemented);
//            }
//            case REQUEST_TIMEOUT: {
//                return context.getString(R.string.io_error);
//            }
//            case DUPLICATE_ENTRY: {
//                return context.getString(R.string.duplicate_entry);
//            }
//            case INTERNAL_SERVER_ERROR: {
//                return context.getString(R.string.default_error);
//            }
//            case THROTTLED: {
//                return context.getString(R.string.throttled);
//            }
//            case IO_EXE: {
//                return context.getString(R.string.please_check_internet_settings);
//            }
//        }
        return "Unknown error";
    }
}
