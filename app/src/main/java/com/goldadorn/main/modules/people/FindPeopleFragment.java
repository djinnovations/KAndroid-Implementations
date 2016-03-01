package com.goldadorn.main.modules.people;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class FindPeopleFragment extends DefaultVerticalListView
{

    private void followPeope(People post,int pos) {
        followPeopleHelper.update(post, pos);
    }
    PeopleUpdateHelper.UpdateResult postUpdateResult = new PeopleUpdateHelper.UpdateResult() {
        @Override
        public void onFail(PeopleUpdateHelper host,People post, int pos) {
            if(host instanceof FollowPeopleHelper)
            {
                int isFollowing = post.getIsFollowing();
                isFollowing = isFollowing==0?1:0;
                post.setIsFollowing(isFollowing);
                getAdapter().notifyItemChanged(pos);
            }
        }
        @Override
        public void onSuccess(PeopleUpdateHelper host,People post, int pos) {
            if(host instanceof FollowPeopleHelper)
                getAdapter().notifyItemChanged(pos);
        }
    };
    private FollowPeopleHelper followPeopleHelper;

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
        followPeopleHelper = new FollowPeopleHelper(getActivity(), getApp().getCookies(),postUpdateResult);
    }
    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_USER;
    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.people_list_item,null);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        return new PeopleItemHolder(view);
    }

    public Class getLoadedDataParsingAwareClass()
    {
        return PeopleResult.class;
    }
    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFindPeopleServiceURL();
        else
            return null;
    }
    public class PeopleItemHolder extends BaseItemHolder {

        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;

        @Bind(R.id.designer)
        TextView designer;

        @Bind(R.id.countFollowing)
        TextView countFollowing;

        @Bind(R.id.countFollowers)
        TextView countFollowers;

        @Bind(R.id.followButton)
        Button followButton;

        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(v==userImage)
                {
                    gotoUser(people);
                }
                else if(v==userName)
                {
                    gotoUser(people);
                }
                else if(v==followButton)
                {
                    int isFollowing = people.getIsFollowing();
                    isFollowing = isFollowing==0?1:0;
                    people.setIsFollowing(isFollowing);
                    if(people.getIsFollowing()==1) {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                        //followButton.setSelected(true);
                    }
                    else {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                        //followButton.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(followButton);
                    followPeope(people, position);
                }
            }
        };
        private People people;

        public PeopleItemHolder(View itemView)
        {
            super(itemView);
            userImage.setOnClickListener(itemClick);
            userName.setOnClickListener(itemClick);
            followButton.setOnClickListener(itemClick);
        }
        public void updateItemView(Object item,View view,int position)
        {
            people =(People)item;

            userName.setText(people.getUserName());
            if(people.getIsDesigner()==1)
                designer.setText("Designer");

            countFollowing.setText(people.getFollowingCount()+"");
            countFollowers.setText(people.getFollowerCount()+"");

            Picasso.with(getContext())
                    .load(people.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(getContext())
                    .resize(100,100)
                    .into(userImage);

            if(people.isSelf()) {
                followButton.setVisibility(View.INVISIBLE);
            }
            else {
                followButton.setVisibility(View.VISIBLE);

                if(people.getIsFollowing()==1) {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                    //followButton.setSelected(true);
                }
                else {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                    //followButton.setSelected(false);
                }
            }
        }
    }



    public static class PeopleResult extends CodeDataParser
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