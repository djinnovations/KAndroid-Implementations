package com.goldadorn.main.modules.people;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class FindPeopleFragment extends DefaultVerticalListView {

    private void followPeope(People post, int pos) {
        Log.d("djpeople", "followPeople");
        followPeopleHelper.update(post, pos);
    }

    PeopleUpdateHelper.UpdateResult postUpdateResult = new PeopleUpdateHelper.UpdateResult() {
        @Override
        public void onFail(PeopleUpdateHelper host, People post, int pos) {
            Log.d("djpeople", "onFail");
            if (host instanceof FollowPeopleHelper) {
                int isFollowing = post.getIsFollowing();
                isFollowing = isFollowing == 0 ? 1 : 0;
                post.setIsFollowing(isFollowing);
                getAdapter().notifyItemChanged(pos);
            }
        }

        @Override
        public void onSuccess(PeopleUpdateHelper host, People post, int pos) {
            if (host instanceof FollowPeopleHelper) {
                /*People mySelf = getApp().getPeople();
                Log.d("djpeople", "myselfremoved?: " + getDataManager().remove(mySelf));*/
                int followercount;
                //int followingcount = 0;
                Log.d("djpeople", "onSuccess - pre-followercount: " + post.getFollowerCount());
               // Log.d("djpeople", "onSuccess - pre-followingcount: " + mySelf.getFollowingCount());
                int isFollowing = post.getIsFollowing();
                if (isFollowing == 0) {//if the user was already following then isFollowing 0 else 1
                    followercount = post.getFollowerCount() - 1;
                   /* if (mySelf.getFollowingCount() == 0) {
                        followercount = 0;
                    } else followingcount = mySelf.getFollowingCount() - 1;*/
                } else {
                    followercount = post.getFollowerCount() + 1;
                    //followingcount = mySelf.getFollowingCount() + 1;
                }
                //mySelf.setFollowingCount(followingcount);
               // getDataManager().add((pos + 1), mySelf);
                //getDataManager().
                post.setFollowerCount(followercount);
                Log.d("djpeople", "onSuccess - post-followercount: " + post.getFollowerCount());
                //Log.d("djpeople", "onSuccess - post-followingcount: " + mySelf.getFollowingCount());
                getAdapter().notifyItemChanged(pos);
            }

        }
    };

    public void updatePeopleObjIfPresent(People peopleObj){
        People existingPeopleObj = getPeopleObjFromList(peopleObj);
        Log.d("djpeople", "contains that people? - updatePeopleObjIfPresent: "+existingPeopleObj == null? "false" : "true");
        if (existingPeopleObj != null){
            int position = getDataManager().indexOf(existingPeopleObj);
            //getting already available people data and filling up
            if (peopleObj.getIsFollowing() == 0)
                existingPeopleObj.setFollowerCount((existingPeopleObj.getFollowerCount() - 1));
            else existingPeopleObj.setFollowerCount((existingPeopleObj.getFollowerCount() + 1));

            existingPeopleObj.setIsFollowing(peopleObj.getIsFollowing());
            getDataManager().set(position, existingPeopleObj);
            getAdapter().notifyItemChanged(position);
            Log.d("djpeople", "people obj updated - updatePeopleObjIfPresent - position: "+position);
        }
    }


    private People getPeopleObjFromList(People newPeople){
        for (Object obj: getDataManager()){
            if (((People) obj).getUserId() == newPeople.getUserId())
                return (People) obj;
        }
        return null;
    }


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

    public void onItemClick(Object baseObject) {
        super.onItemClick(baseObject);
        gotoUser((People) baseObject);
    }

    private void logEventsAnalytics(String eventName) {
        getApp().getFbAnalyticsInstance().logCustomEvent(getActivity(), eventName);
    }

    public void onViewCreated(View view) {

        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: People");
        logEventsAnalytics(GAAnalyticsEventNames.PEOPLE);

        followPeopleHelper = new FollowPeopleHelper(getActivity(), getApp().getCookies(), postUpdateResult);
    }

    public int getListItemViewType(int position, Object item) {
        return ViewTypes.VIEW_USER;
    }

    public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.people_list_item, null);
    }

    public BaseItemHolder getItemHolder(int viewType, View view) {
        return new PeopleItemHolder(view);
    }

    private int offset = 0;

    @Override
    protected DataManager createDataManager() {
        return new DefaultProjectDataManager1(getActivity(), this, getApp().getCookies());
    }

    public class DefaultProjectDataManager1 extends com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager {
        public DefaultProjectDataManager1(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
            super(context, delegate, cookies);
        }

        @Override
        protected void updatePagingData(BaseDataParser loadedDataVO) {
            try {
                if (loadedDataVO != null && loadedDataVO instanceof PeopleResult) {
                    PeopleResult result = (PeopleResult) loadedDataVO;
                    if (result.offset != -1 && offset != result.offset) {
                        offset = result.offset;
                        pageData.curruntPage += 1;
                        pageData.totalPage += 1;
                    } else {
                        pageData.totalPage = pageData.curruntPage;
                    }
                } else {
                    pageData.totalPage = pageData.curruntPage;
                }
            } catch (Exception e) {
                pageData.curruntPage = pageData.totalPage = 1;
            }
        }


        public int getPeoplePosition(People people) {
            int position = -1;

            for (int value = 0; value < this.size(); ++value) {
                if (this.get(value) == people) {
                    position = value;
                    break;
                }
            }

            return position;
        }
    }

    public Class getLoadedDataParsingAwareClass() {
        return PeopleResult.class;
    }

    public String getNextDataURL(PageData pageData) {
        Log.d("djpeople", "pagination offset FindPeopleFragment: " + offset);
        /*if(pageData.curruntPage==1)*/
        return getApp().getUrlHelper().getFindPeopleServiceURL(offset);
        /*else
            return null;*/
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

        @Bind(R.id.countFollowingLabel)
        TextView countFollowingLabel;

        @Bind(R.id.countFollowersTitle)
        TextView countFollowersTitle;


        @Bind(R.id.followButton)
        Button followButton;

        private View.OnClickListener itemClick = new View.OnClickListener() {
            public void onClick(View v) {
                if (v == userImage) {
                    gotoUser(people);
                } else if (v == userName) {
                    gotoUser(people);
                } else if (v == followButton) {
                    int isFollowing = people.getIsFollowing();
                    isFollowing = isFollowing == 0 ? 1 : 0;
                    people.setIsFollowing(isFollowing);
                    if (people.getIsFollowing() == 1) {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                        //followButton.setSelected(true);
                    } else {
                        followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                        //followButton.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(followButton);
                    followPeope(people, getAdapterPosition());
                }
            }
        };
        private People people;

        public PeopleItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            userImage.setOnClickListener(itemClick);
            userName.setOnClickListener(itemClick);
            followButton.setOnClickListener(itemClick);

            TypefaceHelper.setFont(getResources().getString(R.string.font_name_text_secondary), designer, countFollowing, countFollowers);
            TypefaceHelper.setFont(userName, countFollowingLabel, countFollowersTitle);
        }

        public void updateItemView(Object item, View view, int position) {
            people = (People) item;
            userName.setText(people.getUserName());
            if (people.getIsDesigner() == 1)
                designer.setText("Designer");
            else
                designer.setText("");

            countFollowing.setText(people.getFollowingCount() + "");
            countFollowers.setText(people.getFollowerCount() + "");

            Picasso.with(getContext())
                    .load(people.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(getContext())
                    .resize(100, 100)
                    .into(userImage);

            if (people.isSelf()) {
                //mySelfPeople = people;
                getApp().setPeople(people);
                followButton.setVisibility(View.INVISIBLE);
            } else {
                followButton.setVisibility(View.VISIBLE);
                if (people.getIsFollowing() == 1) {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_un_follow_user));
                    //followButton.setSelected(true);
                } else {
                    followButton.setText(getActivity().getResources().getString(R.string.icon_follow_user));
                    //followButton.setSelected(false);
                }
            }
            /*int myPos = ((DefaultProjectDataManager1) getDataManager()).getPeoplePosition(getApp().getPeople());
            Log.d("djpeople", "myPos: " + myPos);
            ((People) getDataManager().get(myPos)).setFollowingCount(getApp().getPeople().getFollowingCount());*/
        }
    }


    public static class PeopleResult extends CodeDataParser {
        List<People> people;
        public int offset;

        public List<?> getList() {
            return people;
        }

        public Object getData() {
            return this;
        }

        public void setData(Object data) {
            this.people = (List<People>) data;
        }
    }
}
