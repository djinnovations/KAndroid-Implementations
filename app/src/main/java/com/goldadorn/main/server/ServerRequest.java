package com.goldadorn.main.server;

import android.content.Context;
import android.os.Build;

import com.goldadorn.main.utils.L;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by nithinjohn on 02/08/15.
 */
public class ServerRequest {

    public static Response doPostRequest(Context context, String url, HashMap<String, String> nameValuePairs, RequestBody requestBody) throws IOException {
        L.d("doPostRequest " + url);
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
        L.d("doPutRequest " + url);
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
        L.d("GetRequest " + url);
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


    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
        HttpConnectionParams.setSoTimeout(params, 60 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        return params;
    }

    public static DefaultHttpClient CreateDefaultHttpClient(
            HttpParams httpParams) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            final SchemeRegistry supportedSchemes = new SchemeRegistry();
            // trustAllHosts();
            // Register the "http" protocol scheme, it is required
            // by the default operator to look up socket factories.
            final SocketFactory sf = PlainSocketFactory.getSocketFactory();
            supportedSchemes.register(new Scheme("http", sf, 80));
            final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    httpParams, supportedSchemes);
            return new DefaultHttpClient(ccm, httpParams);
        } else {
            return new DefaultHttpClient(httpParams);
        }
    }

    public static String convertStreamToString(SoftReference<InputStream> is)
            throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        // this is storage overwritten on each iteration with bytes
        int bufferSize = 32678;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the
        // byteBuffer
        int len = 0;
        while ((len = is.get().read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toString();
    }
}
