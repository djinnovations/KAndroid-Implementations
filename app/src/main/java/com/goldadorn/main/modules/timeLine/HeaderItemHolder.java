package com.goldadorn.main.modules.timeLine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.people.FollowPeopleHelper;
import com.goldadorn.main.modules.people.PeopleUpdateHelper;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BhavinPadhiyar on 01/04/16.
 */
public class HeaderItemHolder{


    @Bind(R.id.userImage)
    ImageView userImage;


    @Bind(R.id.coverImage)
    ImageView coverImage;


    @Bind(R.id.userName)
    TextView userName;

    @Bind(R.id.countFollowing)
    TextView countFollowing;

    @Bind(R.id.countFollowers)
    TextView countFollowers;

    @Bind(R.id.countFollowersLabel)
    TextView countFollowersLabel;

    @Bind(R.id.countFollowingLabel)
    TextView countFollowingLabel;

    @Bind(R.id.followLink)
    Button followLink;

    @Bind(R.id.designer)
    TextView designer;

    @OnClick(R.id.followLink) void onFollow()
    {
        int isFollowing = people.getIsFollowing();
        isFollowing = isFollowing==0?1:0;
        people.setIsFollowing(isFollowing);
        if(people.getIsFollowing()==0) {
            followLink.setText(context.getResources().getString(R.string.icon_follow_user));
            followLink.setSelected(true);
        }
        else {
            followLink.setText(context.getResources().getString(R.string.icon_un_follow_user));
            followLink.setSelected(false);
        }
        YoYo.with(Techniques.Landing).duration(300).playOn(followLink);
        followPeope(people, 0);
    }

    Activity context;
    People people;
    View itemView;

    public HeaderItemHolder(Activity context,Application app,View itemView,People user)
    {
        ButterKnife.bind(this,itemView);
        this.context =context;
        this.people =user;
        this.itemView =itemView;
        updateItemView(context, people, itemView);
        followPeopleHelper = new FollowPeopleHelper(context,app.getCookies(),postUpdateResult);


        TypefaceHelper.setFont(userName,followLink,countFollowers,countFollowing,countFollowersLabel,countFollowingLabel);
        TypefaceHelper.setFont(context.getString(R.string.font_name_text_secondary),countFollowers,countFollowing,designer);
        update(context, app);
    }

    public void update(Activity context,Application app)
    {
        String url = app.getUrlHelper().getUsersSocialFeedServiceURL();
        ExtendedAjaxCallback ajaxCallback =getAjaxCallback(app.getCookies());
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, 0);
        params.put(URLHelper.LIKE_A_POST.POST_ID, 0);
        params.put(URLHelper.LIKE_A_POST.USER_ID, people.getUserId());
        ajaxCallback.setParams(params);
        ajaxCallback.setClazz(String.class);
        AQuery aQuery = new AQuery(context);
        aQuery.ajax(url, params, String.class, ajaxCallback);

    }

    protected ExtendedAjaxCallback getAjaxCallback(List<Cookie> cookies ) {
        ExtendedAjaxCallback<String> ajaxCallback = new ExtendedAjaxCallback<String>() {
            public void callback(String url, String json, AjaxStatus status) {
                Gson gson = new Gson();
                Type t = UsersTimeLineFragment.TimelineFeedResult.class;
                UsersTimeLineFragment.TimelineFeedResult timeline = gson.fromJson(json,t);
                People userInfo=timeline.userinfo;
                if(userInfo!=null)
                {
                    people.setFollowerCount(userInfo.getFollowerCount());
                    people.setFollowingCount(userInfo.getFollowingCount());
                    people.setBackgroundPic(userInfo.getBackgroundPic());
                    people.setLastname(userInfo.getLastname());
                    people.setIsFollowing(userInfo.getIsFollowing());
                    updateItemView(context, people, itemView);
                }
            }
        };
        if(cookies!=null && cookies.size()!=0) {
            for (Cookie cookie : cookies) {
                ajaxCallback.cookie(cookie.getName(), cookie.getValue());
            }
        }
        return ajaxCallback;
    }


    private void followPeope(People post,int pos) {
        followPeopleHelper.update(post, pos);
    }
    PeopleUpdateHelper.UpdateResult postUpdateResult = new PeopleUpdateHelper.UpdateResult() {
        @Override
        public void onFail(PeopleUpdateHelper host,People post, int pos) {
            if(host instanceof FollowPeopleHelper)
            {
                int isFollowing = people.getIsFollowing();
                isFollowing = isFollowing==0?1:0;
                people.setIsFollowing(isFollowing);
                updateItemView(context, people, itemView);
            }
        }
        @Override
        public void onSuccess(PeopleUpdateHelper host,People post, int pos) {

        }
    };
    private FollowPeopleHelper followPeopleHelper;

    public void updateItemView(final Context context, People people,View view)
    {
        userName.setText(people.getUserName());

        if(people.getIsDesigner()==1) {
            designer.setText("Designer");
            designer.setVisibility(View.GONE);
        }
        else
        {
            designer.setVisibility(View.GONE);
        }

        countFollowing.setText(people.getFollowingCount() + "");
        countFollowers.setText(people.getFollowerCount() + "");

        if(people.isSelf())
            followLink.setVisibility(View.GONE);
        else {
            followLink.setVisibility(View.VISIBLE);
            if(people.getIsFollowing()==0)
                followLink.setText(context.getResources().getString(R.string.icon_follow_user));
            else
                followLink.setText(context.getResources().getString(R.string.icon_un_follow_user));

        }

        people.setBackgroundPic("http://i.huffpost.com/gen/857659/images/o-FABULOUS-FINDS-APPRAISING-GOLD-JEWELRY-facebook.jpg");
        if(people.getBackgroundPic()!=null && people.getBackgroundPic().equals("")==false)
            Picasso.with(context).load(people.getBackgroundPic()).into(coverImage);

        //people.setProfilePic("http://whatatimeline.com/covers/1330597507de0/balloons-sunset-view-facebook-cover.jpg");
        try {
            Picasso.with(context)
                    .load(people.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile_dark)
                    .tag(context)
                    .resize(100, 100)
                    .into(userImage);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(context)
                    .load(R.drawable.vector_image_place_holder_profile_dark)
                    .tag(context)
                    .resize(100, 100)
                    .into(userImage);
        }
    }

}