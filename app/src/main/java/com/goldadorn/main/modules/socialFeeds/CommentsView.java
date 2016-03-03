package com.goldadorn.main.modules.socialFeeds;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.goldadorn.main.activities.UserActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.Comment;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.socialFeeds.helper.PostCommentHelper;
import com.goldadorn.main.modules.socialFeeds.helper.PostUpdateHelper;
import com.goldadorn.main.utils.EmoHelper;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Collection;
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
public class CommentsView extends DefaultVerticalListView
{

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

    public Integer getCommentCount() {
        if(getDataManager()!=null)
            return getDataManager().size();
        return -1;
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

        protected void dataLoadingDone(List<?> list, boolean isRefreshPage) {
            if (list != null) {
                Collections.reverse(list);
            }
            super.dataLoadingDone(list,isRefreshPage);
        }
    }
    private PostCommentHelper postCommentHelper;

    protected View createRootView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(getDataManager().getRefreshEnabled())
            return inflater.inflate(R.layout.feed_comments_fragment_recycler_with_swipe_refresh_layout, container, false);
        else
            return inflater.inflate(R.layout.feed_comments_fragment_recycler, container, false);
    }

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
        return new CommentDividerDecoration(this.getActivity());
    }
    public static class ViewTypes {
        public static final int VIEW_COMMENT = 5;
    }
    public void onItemClick(Object baseObject)
    {
        super.onItemClick(baseObject);
    }
    @Bind(R.id.details)
    EditText details;

    @Bind(R.id.send)
    Button send;
    PostUpdateHelper.PostUpdateResult postUpdateResult = new PostUpdateHelper.PostUpdateResult() {
        @Override
        public void onFail(PostUpdateHelper host,SocialPost post, int pos) {

        }
        @Override
        public void onSuccess(PostUpdateHelper host,SocialPost post, int pos) {
            Comment comment = new Comment();
            comment.setUserId(getApp().getUser().getUserid());
            comment.setUserName(getApp().getUser().getUsername());
            comment.setProfilePic(getApp().getUser().getUserpic());
            comment.setCommentText(post.getDescription());
            comment.setTimestamp(new Date().getTime() - 1000);
            comment.updateRedableDate(comment.getTimestamp());
            comment.setAge(comment.strElapsed(comment.getTimestamp()));
            getDataManager().add(comment);
            getRecyclerView().scrollToPosition(getDataManager().size()-1);
        }
    };


    @OnClick(R.id.send)
    void onSendClick() {
        if (details.getText().toString().trim().equals("") == false) {
            String id = getPostID();
            SocialPost socialPost = new SocialPost();
            socialPost.setPostId(id);

            socialPost.setDescription(details.getText().toString());
            postCommentHelper.update(socialPost, 10);
            details.setText("");
        }
    }


    public void onViewCreated(View view) {
        ButterKnife.bind(this, view);
        postCommentHelper = new PostCommentHelper(getActivity(), getApp().getCookies(),postUpdateResult);
        details.addTextChangedListener(new DataTextWatcher(details));
        details.setBackgroundColor(Color.parseColor("#00000000"));
    }

    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_COMMENT;
    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.comments_list_item,null);
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
            return getApp().getUrlHelper().getFetchCommentsServiceURL();
        return null;
    }



    public class CommentItemHolder extends BaseItemHolder {

        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;

        @Bind(R.id.time)
        TextView time;

        @Bind(R.id.details)
        TextView details;




        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                People p = new People();
                p.setUserId(comment.getUserId());
                p.setUserName(comment.getUserName());
                p.setProfilePic(comment.getProfilePic());
                p.setIsSelf(comment.isSelf());
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
        private Comment comment;

        public CommentItemHolder(View itemView)
        {
            super(itemView);
            userImage.setOnClickListener(itemClick);
            userName.setOnClickListener(itemClick);
        }
        public void updateItemView(Object item,View view,int position)
        {
            comment =(Comment)item;
            userName.setText(comment.getUserName());

            time.setText(comment.getRedableDate());

            if(comment.getCommentText()!=null && comment.getCommentText().equals("")==false)
                details.setText('"'+comment.getCommentText()+'"');
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
        List<Comment> data;
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
            this.data=(List<Comment>)data;
        }
    }


    private class DataTextWatcher implements TextWatcher {

        private View view;

        private DataTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            if(view.getId() == R.id.details)
            {
                if(details.getText().toString().trim().equals(""))
                {
                    send.setEnabled(false);
                }
                else
                    send.setEnabled(true);
            }
        }
    }
}