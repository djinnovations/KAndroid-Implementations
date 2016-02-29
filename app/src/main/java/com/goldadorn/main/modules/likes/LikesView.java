package com.goldadorn.main.modules.likes;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.model.Comment;
import com.goldadorn.main.model.Liked;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.socialFeeds.CommentDividerDecoration;
import com.goldadorn.main.modules.socialFeeds.helper.PostCommentHelper;
import com.goldadorn.main.modules.socialFeeds.helper.PostUpdateHelper;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bhavinpadhiyar on 2/26/16.
 */
public class LikesView extends FreeFlowLayout implements DefaultProjectDataManager.IDataManagerDelegate
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
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new CommentDividerDecoration(this.getActivity());
    }
    public static class ViewTypes {
        public static final int VIEW_COMMENT = 5;
    }
    public void onItemClick(Object baseObject)
    {
        super.onItemClick(baseObject);
    }
    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_COMMENT;
    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.liked_list_item,null);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        return new CommentItemHolder(view);
    }
    public Class getLoadedDataParsingAwareClass()
    {
        return CommentResult.class;
    }
    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFetchLikesServiceURL();
        return null;
    }
    public class CommentItemHolder extends BaseItemHolder {

        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;
        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {

            }
        };
        private Liked comment;

        public CommentItemHolder(View itemView)
        {
            super(itemView);
            userImage.setOnClickListener(itemClick);
            userName.setOnClickListener(itemClick);
        }
        public void updateItemView(Object item,View view,int position)
        {
            comment =(Liked)item;
            userName.setText(comment.getUserName());
            Picasso.with(getContext())
                    .load(comment.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(getContext())
                    .resize(100,100)
                    .into(userImage);
        }
    }
    public static class CommentResult extends CodeDataParser
    {
        List<Liked> data;
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
            this.data=(List<Liked>)data;
        }
    }
}