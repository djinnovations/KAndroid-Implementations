package com.goldadorn.main.modules.likes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.UserActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.Liked;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.socialFeeds.CommentDividerDecoration;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/26/16.
 */
public class VotersView extends FreeFlowLayout implements DefaultProjectDataManager.IDataManagerDelegate
{
    protected Application getApp() {
        BaseActivity baseActivity =(BaseActivity)getActivity();
        return baseActivity.getApp();
    }
    protected MainActivity getAppMainActivity() {
        if(getActivity() instanceof MainActivity)
            return (MainActivity)getActivity();
        return null;
    }

    public String getRefreshDataURL(PageData pageData)
    {
        return null;
    }
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.POST_ID, getPostID());
        params.put(URLHelper.LIKE_A_POST.OFFSET,0);
        return params;
    }
    protected DataManager createDataManager()
    {
        return new CommentDataManager(getActivity(),this,getApp().getCookies());
    }

    public String getPostID() {
        return (String)getPramas();
    }
    public class CommentDataManager extends DefaultProjectDataManager
    {
        public CommentDataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
        {
            super(context,delegate,cookies);
        }
        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }
    }
    protected void configDataManager(DataManager dataManager) {
        super.configDataManager(dataManager);
        dataManager.setRefreshEnabled(false);
    }
    public static class ViewTypes {
        public static final int VIEW_VOTES = 5;
    }
    public void onItemClick(Object baseObject)
    {
        super.onItemClick(baseObject);
    }
    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_VOTES;
    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.liked_list_item,null);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        return new LikeItemHolder(view);
    }
    public Class getLoadedDataParsingAwareClass()
    {
        return LikesResult.class;
    }
    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFetchVotersServiceURL();
        return null;
    }

    public void gotoUser(People people)
    {
        NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),people.getUserName()+"'s Timeline", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UserActivity.class);
        Map<String, Object> data= new HashMap<>();
        data.put("NAME",people.getUserName());
        data.put("ID",people.getUserId());
        data.put("FOLLOWER_COUNT",people.getFollowerCount());
        data.put("FOLLOWING_COUNT",people.getFollowingCount());
        data.put("PROFILE_PIC",people.getProfilePic());
        data.put("IS_DESIGNER",people.getIsDesigner());
        data.put("IS_SELF",people.isSelf());
        data.put("IS_FOLLOWING",people.getIsFollowing());
        navigationDataObject.setParam(data);
        EventBus.getDefault().post(new AppActions(navigationDataObject));
    }
    public class LikeItemHolder extends BaseItemHolder {

        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;
        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                People p = new People();
                p.setUserId(liked.getUserId());
                p.setUserName(liked.getUserName());
                p.setProfilePic(liked.getProfilePic());
                p.setIsSelf(liked.isSelf());
                if(v==userImage)
                {
                    gotoUser(p);
                }
                else if(v==userName)
                {
                    gotoUser(p);
                }
            }
        };
        private Liked liked;

        public LikeItemHolder(View itemView)
        {
            super(itemView);
            userImage.setOnClickListener(itemClick);
            userName.setOnClickListener(itemClick);
        }
        public void updateItemView(Object item,View view,int position)
        {
            liked =(Liked)item;
            userName.setText(liked.getUserName());
            Picasso.with(getContext())
                    .load(liked.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(getContext())
                    .resize(100,100)
                    .into(userImage);
        }
    }
    public static class LikesResult extends CodeDataParser
    {
        List<Liked> voters;
        String status;
        int offset;
        public List<?> getList()
        {
            return voters;
        }
        public Object getData()
        {
            return voters;
        }
        public void setData(Object data)
        {
            this.voters=(List<Liked>)data;
        }
    }
}