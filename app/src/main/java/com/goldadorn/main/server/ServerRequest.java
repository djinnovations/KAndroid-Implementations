package com.goldadorn.main.server;

import android.content.Context;

import com.goldadorn.main.utils.L;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by nithinjohn on 02/08/15.
 */
public class ServerRequest {

    public static Response doPostRequest(Context context, String url, HashMap<String, String> nameValuePairs, RequestBody requestBody) throws IOException {

        Headers.Builder headers = new Headers.Builder();
        try {
            if (nameValuePairs != null) {
                for (Map.Entry<String, String> entry : nameValuePairs.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        headers.add(entry.getKey(),
                                entry.getValue());
                    }

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Request.Builder requstBuild = new Request.Builder();
        requstBuild.url(url);
        requstBuild.headers(headers.build());
        if (requestBody != null)
            requstBuild.post(requestBody);

        return new OkHttpClient().newCall(requstBuild.build()).execute();
    }

    public static Response doPutRequest(Context context, String url, HashMap<String, String> nameValuePairs, RequestBody requestBody) throws IOException {
        Headers.Builder headers = new Headers.Builder();
        try {
            if (nameValuePairs != null) {
                for (Map.Entry<String, String> entry : nameValuePairs.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        headers.add(entry.getKey(),
                                entry.getValue());
                    }

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Request.Builder requstBuild = new Request.Builder();
        requstBuild.url(url);
        requstBuild.headers(headers.build());
        if (requestBody != null)
            requstBuild.put(requestBody);

        return new OkHttpClient().newCall(requstBuild.build()).execute();
    }

    public static Response doGetRequest(Context context, String url, HashMap<String, String> nameValuePairs) throws IOException {
        L.d("GetRequest "+url);
        Headers.Builder headers = new Headers.Builder();
        try {
            if (nameValuePairs != null) {
                for (Map.Entry<String, String> entry : nameValuePairs.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        headers.add(entry.getKey(),
                                entry.getValue());
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(url)
                .headers(headers.build())
                .build();
        return new OkHttpClient().newCall(request).execute();
    }
}
