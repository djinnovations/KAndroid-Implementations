package com.goldadorn.main.server;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.response.ObjectResponse;
import com.goldadorn.main.utils.L;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
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

    public static void doPostRequestForImage(Context context, String url, HashMap<String, String> nameValuePairs, ObjectResponse<ProfileData> responseresult) throws IOException {
        final HttpParams httpParams = createHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);

        DefaultHttpClient postClient = CreateDefaultHttpClient(httpParams);
        postClient
                .setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
                        0, false));
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response;
        L.d("doPostRequest " + url);
        try {
            if (nameValuePairs != null) {
                for (Map.Entry<String, String> entry : nameValuePairs.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        httpPost.addHeader(entry.getKey(),
                                entry.getValue());
                    }

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ProfileData profileData = responseresult.object;
        MultipartEntity entity = new MultipartEntity();
        if (!TextUtils.isEmpty(profileData.email))
            entity.addPart("prof_username", new StringBody(profileData.email));
        if (!TextUtils.isEmpty(profileData.firstName))
            entity.addPart("prof_fname", new StringBody(profileData.firstName));
        if (!TextUtils.isEmpty(profileData.lastName))
            entity.addPart("prof_lname", new StringBody(profileData.lastName));
        if (profileData.dob != -1 && profileData.dob > 0)
            entity.addPart("prof_birthday", new StringBody(profileData.dob + ""));
        if (!TextUtils.isEmpty(profileData.phone))
            entity.addPart("prof_phone", new StringBody(profileData.phone));
        if (!TextUtils.isEmpty(profileData.address1))
            entity.addPart("prof_address1", new StringBody(profileData.address1));
        if (!TextUtils.isEmpty(profileData.address2))
            entity.addPart("prof_address2", new StringBody(profileData.address2));
        if (!TextUtils.isEmpty(profileData.country))
            entity.addPart("prof_country", new StringBody(profileData.country));
        if (!TextUtils.isEmpty(profileData.city))
            entity.addPart("prof_city", new StringBody(profileData.city));
        if (!TextUtils.isEmpty(profileData.state))
            entity.addPart("prof_state", new StringBody(profileData.state));
        if (!TextUtils.isEmpty(profileData.pincode))
            entity.addPart("prof_pincode", new StringBody(profileData.pincode));
        if (profileData.genderType > 0)
            entity.addPart("prof_gender", new StringBody(profileData.genderType + ""));


        if (profileData.imageToUpload != null) {
//            String filename = profileData.imageToUpload.getPath().substring(profileData.imageToUpload.getPath().lastIndexOf("/") + 1);
//            entity.addPart("file1", new FileBody(profileData.imageToUpload));
            FileInputStream fileInputStream = new FileInputStream(profileData.imageToUpload);

            entity.addPart("image", new InputStreamBody(fileInputStream, "image/png", "file1"));
        }
        httpPost.setEntity(entity);

        response = postClient.execute(httpPost);
        responseresult.responseCode = response.getStatusLine().getStatusCode();
        HttpEntity httpentity = response.getEntity();
        if (httpentity != null) {
            SoftReference<InputStream> instream = new SoftReference<InputStream>(
                    httpentity.getContent());
            responseresult.responseContent = convertStreamToString(instream);
            instream.get().close();
        }
        L.d("setBasicProfile " + "Code :" + responseresult.responseCode + " content", responseresult.responseContent);
        httpPost.abort();
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
