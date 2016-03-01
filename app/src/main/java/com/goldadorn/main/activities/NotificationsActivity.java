package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.goldadorn.main.R;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import org.github.images.CircularImageView;

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

    private void refresh(){
        String url = getUrlHelper().getNotificationsUrl();
        ExtendedAjaxCallback ajaxCallback =getAjaxCallback(postCallToken);
        ajaxCallback.setClazz(String.class);
//        ajaxCallback.setParams(params);
        ajaxCallback.method(AQuery.METHOD_POST);
//        getAQuery().ajax(url, params, String.class, ajaxCallback);
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
                holder.person = (CircularImageView) convertView.findViewById(R.id.personImage);
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
