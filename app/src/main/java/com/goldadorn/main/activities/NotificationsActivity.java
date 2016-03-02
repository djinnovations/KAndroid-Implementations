package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.utils.IDUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.github.images.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 1/3/16.
 */
public class NotificationsActivity extends BaseActivity {
    private final static String TAG = NotificationsActivity.class.getSimpleName();
    private final static boolean DEBUG = true;


    @Bind(R.id.notificationsList)
    ListView notificationsList;
    final private int postCallToken = IDUtils.generateViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        NotificationsAdapter adapter = new NotificationsAdapter(this);
        notificationsList.setAdapter(adapter);

        refresh();
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Cookie> cookies = getApp().getCookies();
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://goldadorn.cloudapp.net/goldadorn_dev/rest/notifications");

                    try {

                        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                        String phpSession = "";

                        if (cookies.isEmpty()) {
                            Log.i("TAG", "None");
                        } else {
                            for (int i = 0; i < cookies.size(); i++) {
                                phpSession += cookies.get(i).getName()+"="+cookies.get(i).getValue()+";";

                            }
                            Log.i("session", phpSession);
                        }
                        httppost.addHeader("Cookie", phpSession);
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            SoftReference<InputStream> instream = new SoftReference<InputStream>(
                                    entity.getContent());
                            String content = convertStreamToString(instream);
                            Log.d("Response", content);
                            instream.get().close();
                        }
                    } catch (Exception e) {
                        // Catch Protocol Exception
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


//        Map<String, Object> params = new HashMap<>();
//        params.put("notification", "0");
//        String url = getUrlHelper().getNotificationsUrl();
//        ExtendedAjaxCallback ajaxCallback =getAjaxCallback(postCallToken);
//        ajaxCallback.setClazz(String.class);
//        ajaxCallback.setParams(params);
//        ajaxCallback.method(AQuery.METHOD_POST);
//        getAQuery().ajax(url, params, String.class, ajaxCallback);
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

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        super.serverCallEnds(id, url, json, status);
    }

    private class NotificationsAdapter extends BaseAdapter {

        private final Context context;

        public NotificationsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 23;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NotificationHolder holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_notification_item, null);
                holder = new NotificationHolder();
                holder.person = (CircularImageView) convertView.findViewById(R.id.userImage);
                holder.data = (TextView) convertView.findViewById(R.id.data);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.content = (ImageView) convertView.findViewById(R.id.contentImage);
                convertView.setTag(holder);
            } else {
                holder = (NotificationHolder) convertView.getTag();
            }
            holder.person.setImageResource(R.drawable.intro_screen_3_image_1);
            holder.time.setText(Integer.toString(position));
            holder.data.setText("name dhcfdsh klsdhfh dsofhcidsf");
            holder.content.setImageResource(R.drawable.slide_1_image);
            return convertView;
        }
    }

    class NotificationHolder {
        CircularImageView person;
        TextView data;
        TextView time;
        ImageView content;
    }
}
