package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.github.images.CircularImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
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
    @Bind(R.id.emptyView)
    View emptyView;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    @Bind(R.id.emptyViewMessage)
    TextView emptyTextView;


    final private int postCallToken = IDUtils.generateViewId();
    private JSONArray notificationJsons;
    private NotificationsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        mAdapter = new NotificationsAdapter(this);
        notificationsList.setAdapter(mAdapter);
        refresh();
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Cookie> cookies = getApp().getCookies();
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(URLHelper.getInstance().getNotificationsUrl());
                    Log.d("Notification url "," url "+URLHelper.getInstance().getNotificationsUrl());

                    try {

//                        List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                        String phpSession = "";

                        if (cookies.isEmpty()) {
                            Log.i("TAG", "None");
                        } else {
                            for (int i = 0; i < cookies.size(); i++) {
                                phpSession += cookies.get(i).getName() + "=" + cookies.get(i).getValue() + ";";

                            }
                            Log.i("session", phpSession);
                        }
                        httppost.addHeader("Cookie", phpSession);
//                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            SoftReference<InputStream> instream = new SoftReference<>(
                                    entity.getContent());
                            String content = convertStreamToString(instream);
                            Log.d("Response", content);
//                            notificationJsons = new JSONArray("[  \n" +
//                                    "   {  \n" +
//                                    "      \"postid\":\"65\",\n" +
//                                    "      \"commented\":\"\",\n" +
//                                    "      \"commentcount\":4\n" +
//                                    "   },\n" +
//                                    "   {  \n" +
//                                    "      \"postid\":\"65\",\n" +
//                                    "      \"liked\":\"nithin, k\",\n" +
//                                    "      \"likecount\":2\n" +
//                                    "   },\n" +
//                                    "   {  \n" +
//                                    "      \"postid\":\"64\",\n" +
//                                    "      \"liked\":\"nithin\",\n" +
//                                    "      \"likecount\":1\n" +
//                                    "   }\n" +
//                                    "]");
                            try {
                                notificationJsons = new JSONArray(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            notificationsList.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(notificationJsons!=null && notificationJsons.length()>0){
                                        notificationsList.setVisibility(View.VISIBLE);
                                        emptyView.setVisibility(View.GONE);
                                    }else{
                                        notificationsList.setVisibility(View.INVISIBLE);
                                        emptyView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        emptyTextView.setVisibility(View.VISIBLE);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
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
            return notificationJsons != null ? notificationJsons.length() : 0;
        }

        @Override
        public JSONObject getItem(int position) {
            try {
                return notificationJsons.getJSONObject(position);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
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
            JSONObject object = getItem(position);

            // todo nithin get person mImage
            String imageUrl = object.optString("image");
            if(!TextUtils.isEmpty(imageUrl))
            Picasso.with(context).load(imageUrl).into(holder.person);
            // todo nithin get timestamp
            long timestamp = object.optLong("timestamp",System.currentTimeMillis());
            holder.time.setText(DateUtils.getRelativeDateTimeString(context,timestamp,DateUtils.
                    SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL));
            holder.data.setText(createString(object));
//            holder.content.setImageResource(R.drawable.slide_1_image);
            return convertView;
        }

        private String createString(JSONObject object) {
            Log.d(Constants.TAG, "notification list obj resonse - createString: "+object);
            StringBuilder builder = new StringBuilder();
            String typeLabel = null;
            String type = "";
            int typeCount = 0;
            if (object.has("liked")) {
                type = "liked your post";
                typeLabel = object.optString("liked");
                typeCount = object.optInt("likecount");
            } else if (object.has("followers")) {
                type = "followed you";
                typeLabel = object.optString("followers");
                typeCount = object.optInt("followerscount");
            } else if (object.has("commented")) {
                type = "commented on your post";
                typeLabel = object.optString("commented");
                typeCount = object.optInt("commentcount");
            } else if (object.has("polled")) {
                type = "voted on your post";
                typeLabel = object.optString("polled");
                typeCount = object.optInt("pollcount");
            } else if (object.has("bof3polled")) {
                type = "voted on your post";
                typeLabel = object.optString("bof3polled");
                typeCount = object.optInt("bof3polledcount");
            }
            if (!TextUtils.isEmpty(typeLabel)) {
                String[] names = typeLabel.split(",");
                for (int i = 0; i < names.length; i++) {
                    if (i == names.length - 1 && i != 0) {
                        builder.append(" and ");
                    } else if (i != 0) builder.append(", ");
                    builder.append(names[i].trim());
                }
//                builder.append(typeLabel);
//                if(typeCount>0){
//                    if(typeCount==1){
//                        builder.append(" and 1 person");
//                    }else{
//                        builder.append(" and ");
//                        builder.append(Integer.toString(typeCount));
//                        builder.append(" people");
//                    }
//                }
            } else {
                builder.append(typeCount).append(" ").append(typeCount == 1 ? "person" : "people");
            }
            builder.append(" ").append(type).append(".");
            return builder.toString();
        }
    }

    class NotificationHolder {
        CircularImageView person;
        TextView data;
        TextView time;
        ImageView content;
    }
}
