package com.goldadorn.main.modules.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.people.DividerDecoration;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class NotificationFragment extends DefaultVerticalListView
{

    public int getColumnsPhone() {
        return 1;
    }

    public int getColumnsTablet10() {
        return 1;
    }

    public int getColumnsTablet7() {
        return 1;
    }

    protected void configDataManager(DataManager dataManager) {
        super.configDataManager(dataManager);
        dataManager.setRefreshEnabled(false);
    }
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }
    public static class ViewTypes {
        public static final int VIEW_USER = 5;
    }
    public void onItemClick(Object baseObject)
    {
        super.onItemClick(baseObject);
        gotoUser((People) baseObject);
    }

    public void onViewCreated(View view) {
    }
    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_USER;
    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_notification_item,null);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        return new NotificationItemHolder(view);
    }

    public Class getLoadedDataParsingAwareClass()
    {
        return NotificationResult.class;
    }
    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getNotificationsUrl();
        else
            return null;
    }
    public class NotificationItemHolder extends BaseItemHolder {

        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.data)
        TextView data;

        @Bind(R.id.time)
        TextView time;

        @Bind(R.id.contentImage)
        ImageView contentImage;

        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(v==userImage)
                {
                    gotoUser(people);
                }
            }
        };
        private People people;

        public NotificationItemHolder(View itemView)
        {
            super(itemView);
            userImage.setOnClickListener(itemClick);
        }
        public void updateItemView(Object item,View view,int position)
        {
            people =(People)item;
            Picasso.with(getContext())
                   .load(people.getProfilePic())
                   .placeholder(R.drawable.vector_image_place_holder_profile)
                   .tag(getContext())
                   .resize(100,100)
                   .into(userImage);
        }
    }



    public static class NotificationResult extends CodeDataParser
    {
        List<People> data;
        public List<?> getList()
        {
            return data;
        }
        public Object getData()
        {
            return data;
        }
        public void setData(Object data)
        {
            this.data=(List<People>)data;
        }
    }
}
