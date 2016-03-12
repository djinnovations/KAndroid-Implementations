package com.goldadorn.main.server;

import android.content.Context;

import com.goldadorn.main.server.response.BasicResponse;

/**
 * this class is used to generate http headers for each api calls. The header values will change
 * in different apis.
 */
public class ParamsBuilder {
    public Context mContext;
    public BasicResponse mResponse = null;
    public int mApiType;

    public ParamsBuilder build(BasicResponse responseCode) {
        mResponse = responseCode;
        return this;
    }
}
