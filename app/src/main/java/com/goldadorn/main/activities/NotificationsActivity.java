package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.NotificationDataObject;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.paginate.Paginate;
import com.squareup.picasso.Picasso;

import org.github.images.CircularImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    //private JSONArray notificationJsons;
    private NotificationsAdapter mAdapter;
    private int offsetMain;
    private Paginate paginate;
    private boolean seenAllNotification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);

        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: Notification");
        logEventsAnalytics(GAAnalyticsEventNames.NOTIFICATION);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_white);
        getSupportActionBar().setTitle("");

        //refresh();
        requestNotificationPaginate(offsetMain);
        // TODO: 26-05-2016
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private final int notificationReg = IDUtils.generateViewId();
    private void requestNotificationPaginate(int offset){

        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(notificationReg);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("offset", String.valueOf(offset));

        String url = URLHelper.getInstance().getNotificationsUrl();
        Log.d(Constants.TAG,"offset - requestNotificationPaginate: "+offset);
        Log.d(Constants.TAG," notification url - NotificationActivity(requestNotificationPaginate): "+url);

        getAQuery().ajax(url, paramsMap, String.class, ajaxCallback);
    }


    private boolean firstTime = true;
    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {

        if (id == notificationReg){
            Log.d(Constants.TAG, "serverCallEnds - notification API response: " + json);
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    notificationsList , this);
            if (success) {
                try {
                    JSONObject jsonObj = new JSONObject(json.toString());
                    int offset = jsonObj.getInt("offset");
                    ArrayList<NotificationDataObject> notificationData = getDataForAdapter(jsonObj.getJSONArray("likes"));
                    if (firstTime){
                        mAdapter = new NotificationsAdapter(this, notificationData);
                        notificationsList.setAdapter(mAdapter);
                        updateUi(notificationData);
                        setUpPaginate();
                        firstTime = false;
                    }
                    else if (offset > offsetMain){
                        mAdapter.addNotifyData(notificationData);
                    }else if (offset == offsetMain){
                        seenAllNotification = true;
                    }
                    offsetMain = offset;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else super.serverCallEnds(id, url, json, status);
    }

    private int trigger = 1;
    private void setUpPaginate() {
        paginate = Paginate.with(notificationsList, callbacks)
                .setOnScrollListener(scrollListener)
                .setLoadingTriggerThreshold(trigger)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(null)
                .build();
    }


    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };


    private ArrayList<NotificationDataObject> getDataForAdapter(JSONArray jsonArray){

        ArrayList<NotificationDataObject> list = new ArrayList<>();
        for (int i = 0; i<jsonArray.length(); i++){
            try {
                list.add(extractJsonContent(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private NotificationDataObject extractJsonContent(JSONObject object) {

        String peopleImageUrl = null;
        boolean botPost = false;
        String postImageUrl = null;
        String dateTime = null;
        String postContent = null;

        if (!object.isNull("profilePic")){
            try {
                peopleImageUrl = object.getString("profilePic");
            }catch (Exception ex){

            }
        }

        botPost = isBotPost(object);

        if (!object.isNull("postImage")) {
            try {
                JSONArray postImageJsonArr = object.getJSONArray("postImage");
                postImageUrl = getPostImageUrl(postImageJsonArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // todo nithin get timestamp
        long timestamp = object.optLong("timestamp",System.currentTimeMillis());
        dateTime = DateUtils.getRelativeDateTimeString(this,timestamp,DateUtils.
                SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL).toString();
        postContent = createString(object);
        return new NotificationDataObject(peopleImageUrl, postContent, dateTime, postImageUrl, botPost);

    }


    private void updateUi(ArrayList<NotificationDataObject> list){

        if(list!=null && list.size()>0){
            notificationsList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }else{
            notificationsList.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }


    private boolean loading;
    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            // Load next page of data (e.g. network or database)
            loading = true;
            Log.d("djpg", "onLoadMore");
            requestNotificationPaginate(offsetMain);
        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            Log.d("djpg", "isLoading: "+loading);
            return loading;
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            Log.d("djpg", "hasLoadedAllItems: ");
            return seenAllNotification;
        }
    };



    private class NotificationsAdapter extends BaseAdapter {

        private final Context context;
        private ArrayList<NotificationDataObject> mList;

        public NotificationsAdapter(Context context, ArrayList<NotificationDataObject> mList) {
            this.context = context;
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return (mList != null ? mList.size() : 0);
        }

        @Override
        public NotificationDataObject getItem(int position) {
            try {
                return mList.get(position);
            } catch (Exception e) {
                return null;
            }
        }

        public void addNotifyData(ArrayList<NotificationDataObject> data) {
            this.mList.addAll(data);
            notifyDataSetChanged();
            loading = false;
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
                holder.ivPostImage = (ImageView) convertView.findViewById(R.id.contentImage);
                convertView.setTag(holder);
            } else {
                holder = (NotificationHolder) convertView.getTag();
            }

            NotificationDataObject dataObject = getItem(position);
            String peopleImageUrl = dataObject.getPeopleImageUrl();
            String postImageUrl = dataObject.getPostImageUrl();
            String notifyContent = dataObject.getNotifyContent();
            String dateTime = dataObject.getDateTime();
            boolean botPost =  dataObject.isBotPost();

            if (peopleImageUrl != null){
                try {
                    Picasso.with(context).load(URLHelper.parseImageURL(peopleImageUrl)).placeholder(R.drawable
                            .vector_image_place_holder_profile_dark).into(holder.person);
                }catch (Exception ex){
                    Picasso.with(context).load(R.drawable
                            .vector_image_place_holder_profile_dark).into(holder.person);
                }
            }else Picasso.with(context).load(R.drawable
                    .vector_image_place_holder_profile_dark).into(holder.person);


            if (!botPost) {
                if (postImageUrl != null) {
                    try {
                        Log.d(Constants.TAG, "postImage url- : " + postImageUrl);
                        Picasso.with(context).load(postImageUrl).into(holder.ivPostImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else Picasso.with(context).load(R.drawable.ic_poll_topic).into(holder.ivPostImage);

            if (dateTime != null)
                holder.time.setText(dateTime);
            if (notifyContent != null)
                holder.data.setText(notifyContent);

            return convertView;
        }


    }



    private String createString(JSONObject object) {
        //Log.d(Constants.TAG, "notification list obj resonse - createString: "+object);
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


    private boolean isBotPost(JSONObject jsonObject){
        if (!jsonObject.isNull("type")){
            int type = 0;
            try {
                type = jsonObject.getInt("type");
            } catch (JSONException e) {
                e.printStackTrace();
                type = 0;
            }
            if (type == 3){
                return true;
            }
            else return false;
        }
        return false;
    }



    private String getPostImageUrl(JSONArray jsonArray){
        Log.d(Constants.TAG, "jsonArrLength - getPostImageUrl- : "+jsonArray.length());
        if (jsonArray.length() > 1){
            return "BOT";
        }else if (jsonArray.length() == 0){
            return null;
        }
        else{
            try {
                String imageUrl = jsonArray.getString(0);
                return URLHelper.parseImageURL(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    class NotificationHolder {
        CircularImageView person;
        TextView data;
        TextView time;
        ImageView ivPostImage;
    }
}
