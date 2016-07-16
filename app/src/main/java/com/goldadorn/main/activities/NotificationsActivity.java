package com.goldadorn.main.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.NotificationDataObject;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.dj.utils.SmartTimeAgo;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Bind(R.id.transView)
    View transView;


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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_icon_cross_brown);
        getSupportActionBar().setTitle("");

        //refresh();
        requestNotificationPaginate(offsetMain);
        setUpListenerListView();

        tourThisScreen();
        // TODO: 26-05-2016
    }

    private AppTourGuideHelper mTourHelper;

    private void tourThisScreen() {
        mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*if (!coachMarkMgr.isHomeScreenTourDone())
                    testTourGuide();*/
                mTourHelper.displayNotificationScreenTour(NotificationsActivity.this, transView);
            }
        }, 1200);
    }


    NotificationDataObject lastClicked;

    private void setUpListenerListView() {

        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClicked = mAdapter.getItem(position);
                int actionTypeInt = getIdFromActionType(lastClicked.getActionType());
                /*if (actionTypeInt == 5)
                    return;*/
                int idTouse = /*actionTypeInt == 5 ? getApp().getUser().id : */Integer.parseInt(lastClicked.getPostId());
                Map<String, Integer> params = new HashMap<>();
                params.put("type", actionTypeInt);
                params.put("id", idTouse);
                sendNotifyClickToServer(params);
                /*Intent intent = new Intent(NotificationsActivity.this, NotificationPostActivity.class);
                intent.putExtra(IntentKeys.NOTIFICATION_OBJ, mAdapter.getItem(position));
                startActivity(intent);*/
            }
        });
    }


    private int getIdFromActionType(String actionType) {

        switch (actionType) {
            case "L":
                return 1;
            case "C":
                return 2;
            case "P":
                return 3;
            case "B":
                return 4;
            case "F":
                return 5;
            default:
                return -1;
        }
    }

    private final int NOTIFICATION_CLICK_CALL = IDUtils.generateViewId();

    private void sendNotifyClickToServer(Map<String, Integer> params) {
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(NOTIFICATION_CLICK_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        Log.d("djnotify", "req params - sendNotifyClickToServer: " + params);
        getAQuery().ajax(ApiKeys.getUpdateNotificationAPI(), params, String.class, ajaxCallback);
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

    private void requestNotificationPaginate(int offset) {

        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(notificationReg);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("offset", String.valueOf(offset));

        String url = URLHelper.getInstance().getNotificationsUrl();
        Log.d("djnotify", "offset - requestNotificationPaginate: " + offset);
        Log.d("djnotify", " notification url - NotificationActivity(requestNotificationPaginate): " + url);

        getAQuery().ajax(url, paramsMap, String.class, ajaxCallback);
    }


    private boolean firstTime = true;

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djnotify", "url queried- NotificationsActivity: " + url);
        Log.d("djnotify", "response - NotificationsActivity: " + json);
        if (id == notificationReg) {

            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    notificationsList, this);
            if (success) {
                try {
                    JSONObject jsonObj = new JSONObject(json.toString());
                    int offset = jsonObj.getInt("offset");
                    ArrayList<NotificationDataObject> notificationData = getDataForAdapter(jsonObj.getJSONArray("likes"));
                    if (firstTime) {
                        mAdapter = new NotificationsAdapter(this, notificationData);
                        notificationsList.setAdapter(mAdapter);
                        updateUi(notificationData);
                        setUpPaginate();
                        firstTime = false;
                    } else if (offset > offsetMain) {
                        mAdapter.addNotifyData(notificationData);
                    } /*else if (!canContinueToPaginate()) {
                        seenAllNotification = true;
                    }*/
                    if (offset == offsetMain) {
                        stopPagination();
                    }
                    offsetMain = offset;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (id == NOTIFICATION_CLICK_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    notificationsList, this);
            String txt = success ? "notification count updated" : "notification count updation failed";
            Log.d("djnotify", "notification click: " + txt);
            if (success) {
                try {
                    JSONObject jsonObject = new JSONObject(json.toString());
                    RandomUtils.setUnreadCount(jsonObject.getString("count"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (getIdFromActionType(lastClicked.getActionType()) == 5)
                    return;
                Intent intent = new Intent(NotificationsActivity.this, NotificationPostActivity.class);
                intent.putExtra(IntentKeys.NOTIFICATION_OBJ, lastClicked);
                startActivity(intent);
            } else Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

        } else super.serverCallEnds(id, url, json, status);
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


    private ArrayList<NotificationDataObject> getDataForAdapter(JSONArray jsonArray) {

        ArrayList<NotificationDataObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
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
        List<String> postImageUrl = null;
        String dateTime = null;
        String postContent = null;
        String postId = null;
        String actionType = "";

        if (!object.isNull("postid")) {
            try {
                postId = object.getString("postid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!object.isNull("profilePic")) {
            try {
                peopleImageUrl = object.getString("profilePic");
            } catch (Exception ex) {

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

        if (!object.isNull("actionType")) {
            try {
                actionType = object.getString("actionType");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // todo nithin get timestamp
        long timestamp = object.optLong("timestamp", System.currentTimeMillis());
        dateTime = /*DateUtils.getRelativeDateTimeString(this,timestamp,DateUtils.
                SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL).toString();*/
                SmartTimeAgo.getSmartTime(/*getApplicationContext(), */timestamp/*, false*/);
        //DateTimeUtils.getFormattedTimestamp("dd-MM-yyyy hh:mm a", timestamp);

        postContent = createString(object);
        return new NotificationDataObject(peopleImageUrl, postContent, dateTime, postImageUrl, botPost, postId, actionType);
    }


    private void updateUi(ArrayList<NotificationDataObject> list) {

        if (list != null && list.size() > 0) {
            notificationsList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            notificationsList.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }


    private void stopPagination() {
        Log.d("djpg", "stopPagination requested");
        seenAllNotification = true;
        loading = false;
        paginate.setHasMoreDataToLoad(false);
    }


    private boolean loading;
    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            // Load next page of data (e.g. network or database)
            Log.d("djpg", "onLoadMore- offestMain val= " + offsetMain);
            if (canContinueToPaginate()) {
                Log.d("djpg", "onLoadMore - canContinueToPaginate?: true");
                loading = true;
                requestNotificationPaginate(offsetMain);
            } else {
                /*loading = false;
                paginate.setHasMoreDataToLoad(false);*/
                stopPagination();
            }
        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            Log.d("djpg", "isLoading: " + loading);
            return loading;
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            Log.d("djpg", "hasLoadedAllItems: " + seenAllNotification);
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
                holder.ivPostImage1 = (ImageView) convertView.findViewById(R.id.contentImage);
                holder.ivPostImage2 = (ImageView) convertView.findViewById(R.id.contentImage2);
                holder.ivPostImage3 = (ImageView) convertView.findViewById(R.id.contentImage3);
                convertView.setTag(holder);
            } else {
                holder = (NotificationHolder) convertView.getTag();
            }

            NotificationDataObject dataObject = getItem(position);
            String peopleImageUrl = dataObject.getPeopleImageUrl();
            List<String> postImageUrl = dataObject.getPostImageUrl();
            String notifyContent = dataObject.getNotifyContent();
            String dateTime = dataObject.getDateTime();
            boolean botPost = dataObject.isBotPost();

            /*if (peopleImageUrl!=null) {*/
            try {
                    /*Picasso.with(context)
                            .load(URLHelper.parseImageURL(peopleImageUrl))
                            //.placeholder(R.drawable.vector_image_place_holder_profile_dark)
                            .into(holder.person);*/
                Glide.with(NotificationsActivity.this)
                        .load(URLHelper.parseImageURL(peopleImageUrl))
                        //.centerCrop()
                        .placeholder(R.drawable.vector_image_place_holder_profile)
                        .crossFade()
                        .into(holder.person);
            } catch (Exception ex) {
                    /*Picasso.with(context)
                            .load(R.drawable.vector_image_place_holder_profile_dark)
                            .into(holder.person);*/
                Glide.with(NotificationsActivity.this)
                        .load(R.drawable.vector_image_place_holder_profile)
                        //.centerCrop()
                        //.placeholder(R.drawable.vector_image_place_holder_profile_dark)
                        .crossFade()
                        .into(holder.person);
                // holder.person.setImageResource(R.drawable.vector_image_place_holder_profile_dark);
            }
           /* } else {
                //holder.person.setImageResource(R.drawable.vector_image_place_holder_profile);
                Glide.with(NotificationsActivity.this)
                        .load(R.drawable.vector_image_place_holder_profile)
                        //.centerCrop()
                        //.placeholder(R.drawable.vector_image_place_holder_profile_dark)
                        .crossFade()
                        .into(holder.person);
            }*/

            holder.ivPostImage3.setVisibility(View.GONE);
            holder.ivPostImage2.setVisibility(View.GONE);
            if (postImageUrl != null) {
                if (postImageUrl.size() != 0) {
                    try {
                        if (!dataObject.isBotPost())
                            Glide.with(context).load(postImageUrl.get(0)).into(holder.ivPostImage1);
                        else setPostImages(postImageUrl, holder);
                        //Picasso.with(context).load(postImageUrl).into(holder.ivPostImage1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }/* else Picasso.with(context).load(R.drawable.ic_poll_topic).into(holder.ivPostImage1);*/

            if (dateTime != null)
                holder.time.setText(dateTime);
            if (notifyContent != null)
                holder.data.setText(notifyContent);
            if (getIdFromActionType(dataObject.getActionType()) == 5)
                holder.ivPostImage1.setVisibility(View.GONE);

            return convertView;
        }

        private void setPostImages(List<String> urlList, NotificationHolder holder) {
            /*if (urlList.size() == 1){
                if (!TextUtils.isEmpty(urlList.get(0)))
                    Picasso.with(context).load(urlList.get(0)).into(holder.ivPostImage1);
                else holder.ivPostImage1.setVisibility(View.GONE);}*/
            if (urlList.size() == 1)
                Glide.with(context).load(urlList.get(0)).into(holder.ivPostImage1);
            else if (urlList.size() == 2) {
                Glide.with(context).load(urlList.get(0)).into(holder.ivPostImage1);
                holder.ivPostImage2.setVisibility(View.VISIBLE);
                Glide.with(context).load(urlList.get(1)).into(holder.ivPostImage2);
            } else if (urlList.size() == 3) {
                Glide.with(context).load(urlList.get(0)).into(holder.ivPostImage1);
                holder.ivPostImage2.setVisibility(View.VISIBLE);
                Glide.with(context).load(urlList.get(1)).into(holder.ivPostImage2);
                holder.ivPostImage3.setVisibility(View.VISIBLE);
                Glide.with(context).load(urlList.get(2)).into(holder.ivPostImage3);
            }
        }
    }


    private boolean canContinueToPaginate() {
        if (offsetMain % 11 == 0)
            return true;
        else {
            seenAllNotification = true;
            return false;
        }
    }

    private String createString(JSONObject object) {
        //Log.d("djnotify", "notification list obj resonse - createString: "+object);
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


    private boolean isBotPost(JSONObject jsonObject) {
        if (!jsonObject.isNull("type")) {
            int type = 0;
            try {
                type = jsonObject.getInt("type");
            } catch (JSONException e) {
                e.printStackTrace();
                type = 0;
            }
            if (type == 3) {
                return true;
            } else return false;
        }
        return false;
    }


    private List<String> getPostImageUrl(JSONArray jsonArray) {
        /*if (jsonArray.length() > 1) {
            return "BOT";
        } else if (jsonArray.length() == 0) {
            return null;
        } else {*/
        List<String> urlList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                String url = null;
                if (!jsonArray.isNull(i)) {
                    url = jsonArray.getString(i);
                    //if (!TextUtils.isEmpty(url))
                    urlList.add(URLHelper.parseImageURL(url));
                }
            }
            return urlList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //}
    }


    class NotificationHolder {
        CircularImageView person;
        TextView data;
        TextView time;
        ImageView ivPostImage1, ivPostImage2, ivPostImage3;
    }
}
