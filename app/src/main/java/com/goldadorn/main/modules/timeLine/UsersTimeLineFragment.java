package com.goldadorn.main.modules.timeLine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.icons.GoldadornIconFont;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.people.FollowPeopleHelper;
import com.goldadorn.main.modules.people.PeopleUpdateHelper;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class UsersTimeLineFragment extends SocialFeedFragment {

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



    private People user;
    protected DataManager createDataManager()
    {
        return new TimelineDataManager(getActivity(),this,getApp().getCookies());
    }
    public class TimelineDataManager extends SocialFeedProjectDataManager
    {
        public TimelineDataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
        {
            super(context,delegate,cookies);
        }
        protected void dataIn(BaseDataParser value) {
            super.dataIn(value);
            if(value instanceof TimelineFeedResult)
            {
                People userInfo=((TimelineFeedResult)value).userinfo;
                if(userInfo!=null)
                {
                    userInfo.dataLoaded(value);
                    user.setFollowerCount(userInfo.getFollowerCount());
                    user.setFollowingCount(userInfo.getFollowingCount());
                    user.setBackgroundPic(userInfo.getBackgroundPic());
                    user.setLastname(userInfo.getLastname());
                    user.setIsFollowing(userInfo.getIsFollowing());
                    getAdapter().notifyItemChanged(0);

                }
            }
        }

    }

    /*
    protected View createRootView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(getDataManager().getRefreshEnabled())
            return inflater.inflate(R.layout.timeline_fragment_recycler_with_swipe_refresh_layout, container, false);
        else
            return inflater.inflate(R.layout.timeline_fragment_recycler, container, false);
    }
     public void onViewCreated(View view) {
        super.onViewCreated(view);
        getFloatingActionsMenu().setVisibility(View.GONE);

        View timelineHeader =view.findViewById(R.id.timelineHeader);
        HeaderItemHolder headerItemHolder = new HeaderItemHolder(timelineHeader);
        headerItemHolder.updateItemView(getUser(),timelineHeader,0);

    }
    */
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, offset);
        params.put(URLHelper.LIKE_A_POST.POST_ID, 0);
        params.put(URLHelper.LIKE_A_POST.USER_ID, user.getUserId());
        return params;
    }
    public Map<String, Object> getRefreshDataParams(PageData data) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(URLHelper.LIKE_A_POST.OFFSET, refreshOffset);
            SocialPost sp =(SocialPost)getDataManager().get(1);
            params.put(URLHelper.LIKE_A_POST.POST_ID, sp.getPostId());
            params.put(URLHelper.LIKE_A_POST.USER_ID, user.getUserId());
            return params;
        }catch (Exception e){}
        return null;

    }
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        getFloatingActionsMenu().setVisibility(View.GONE);
        followPeopleHelper = new FollowPeopleHelper(getActivity(), getApp().getCookies(),postUpdateResult);
    }
    protected void configDataManager(DataManager dataManager) {
        People userData=crateUser();
        if(userData!=null) {
            setUser(userData);
            dataManager.setRefreshItemPos(1);
            dataManager.setRefreshEnabled(true);
            dataManager.add(userData);
        }
    }
    protected People crateUser()
    {
        try
        {
            People people =(People)getPramas();
            return people ;
        }
        catch (Exception e)
        {

        }
        return null;
    }
    public People getUser() {
        return user;
    }
    public void setUser(People user) {
        this.user = user;
    }


    public static class ViewTypes {
        public static final int VIEW_USER_HEADER = 25;
    }

    public int getListItemViewType(int position,Object item)
    {
        if(item instanceof People)
            return ViewTypes.VIEW_USER_HEADER;
        else
            return super.getListItemViewType(position, item);
    }

    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {

        if(viewType== ViewTypes.VIEW_USER_HEADER)
            return inflater.inflate(R.layout.user_time_line_header,null);
        else
            return super.getItemView(viewType, inflater, container);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        if(viewType== ViewTypes.VIEW_USER_HEADER)
            return new HeaderItemHolder(view);
        else
            return super.getItemHolder(viewType, view);
    }

    public Class getLoadedDataParsingAwareClass()
    {
        return TimelineFeedResult.class;
    }

    public String getNextDataURL(PageData pageData)
    {
        isRefreshingData= false;
        return getApp().getUrlHelper().getUsersSocialFeedServiceURL();
    }
    public String getRefreshDataURL(PageData pageData)
    {
        isRefreshingData= true;
        return getApp().getUrlHelper().getUsersSocialFeedServiceURL();
    }

    @Override
    public void gotoUser(People people)
    {

    }


    public static class TimelineFeedResult extends SocilFeedResult
    {
        public People getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(People userinfo) {
            this.userinfo = userinfo;
        }

        People userinfo;
    }

    public class HeaderItemHolder extends BaseItemHolder {


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

        @Bind(R.id.followLink)
        TextView followLink;

        @Bind(R.id.designer)
        TextView designer;

        People people;
        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v) {
                if (v == followLink)
                {
                    int isFollowing = people.getIsFollowing();
                    isFollowing = isFollowing==0?1:0;
                    people.setIsFollowing(isFollowing);
                    if(people.getIsFollowing()==1) {
                        followLink.setText("Unfollow");
                        followLink.setSelected(true);
                    }
                    else {
                        followLink.setText("Follow");
                        followLink.setSelected(false);
                    }
                    YoYo.with(Techniques.Landing).duration(300).playOn(followLink);
                    followPeope(people, 0);
                }

            }
        };

        public HeaderItemHolder(View itemView)
        {
            super(itemView);
            followLink.setOnClickListener(itemClick);
        }

        public void updateItemView(Object item,View view,int position)
        {
            people=(People)item;
            userName.setText(people.getUserName());

            if(people.getIsDesigner()==1) {
                designer.setText("Designer");
                designer.setVisibility(View.VISIBLE);
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
                    followLink.setText("Follow");
                else
                    followLink.setText("Unfollow");
            }


            if(people.getBackgroundPic()!=null && people.getBackgroundPic().equals("")==false)
            {
                AQuery aq = new AQuery(getActivity());
                aq.id(coverImage).image(people.getBackgroundPic(), true, true, 0, 0, new BitmapAjaxCallback(){

                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
                        Bitmap bit =blurRenderScript(getActivity(),bm,100);
                        iv.setImageBitmap(bit);
                    }
                });


                Picasso.with(getContext())
                        .load(people.getBackgroundPic())
                        .into(coverImage);
            }


            Picasso.with(getContext())
                    .load(people.getProfilePic())
                    .placeholder(R.drawable.vector_image_place_holder_profile)
                    .tag(getContext())
                    .resize(100, 100)
                    .into(userImage);
        }


        public Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
            try {
                smallBitmap = RGB565toARGB888(smallBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bitmap bitmap = Bitmap.createBitmap(
                    smallBitmap.getWidth(), smallBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);

            RenderScript renderScript = RenderScript.create(context);

            Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
            Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));
            blur.setInput(blurInput);
            blur.setRadius(radius); // radius must be 0 < r <= 25
            blur.forEach(blurOutput);

            blurOutput.copyTo(bitmap);
            renderScript.destroy();

            return bitmap;

        }

        private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
            int numPixels = img.getWidth() * img.getHeight();
            int[] pixels = new int[numPixels];

            //Get JPEG pixels.  Each int is the color values for one pixel.
            img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

            //Create a Bitmap of the appropriate format.
            Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

            //Set RGB pixels.
            result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
            return result;
        }
    }
}
