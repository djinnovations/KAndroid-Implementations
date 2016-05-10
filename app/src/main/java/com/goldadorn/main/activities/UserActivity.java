package com.goldadorn.main.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.timeLine.HeaderItemHolder;
import com.goldadorn.main.modules.timeLine.MyTimeLineFragment;
import com.goldadorn.main.modules.timeLine.UsersTimeLineFragment;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.fragments.BaseFragment;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class UserActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener{
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;

    TextView titleText;

    //Author DJphy
    @Bind(R.id.transView)
    View transView;
    /*private ResourceReader resRdr;
    private CoachMarkManager coachMarkMgr;

    private final String msgTimeLine = "You are on a user's timeline\n"
            +"all the recent activities of the \n user can be viewed here";*/

    private AppTourGuideHelper mTourHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.toolbarIconColor), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        String title = getIntent().getExtras().getString("TITLE");
        String name = getIntent().getExtras().getString("NAME");
        int followerCount = getIntent().getExtras().getInt("FOLLOWER_COUNT");
        int followingCount = getIntent().getExtras().getInt("FOLLOWING_COUNT");
        String profilePic = getIntent().getExtras().getString("PROFILE_PIC");
        int isDesigner = getIntent().getExtras().getInt("IS_DESIGNER");
        Boolean isSelf= getIntent().getExtras().getBoolean("IS_SELF");
        int id = getIntent().getExtras().getInt("ID");
        int isFollowing = getIntent().getExtras().getInt("IS_FOLLOWING");

        People people=new People();
        people.setUserName(name);
        people.setProfilePic(profilePic);
        people.setFollowingCount(followingCount);
        people.setFollowerCount(followerCount);
        people.setIsSelf(isSelf);
        people.setUserId(id);
        people.setIsDesigner(isDesigner);
        people.setIsFollowing(isFollowing);

        //setTitle(name + "");
        titleText= (TextView) findViewById(R.id.titleText);
        titleText.setText(name);
        setTitle("");
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavigationDataObject navigationObject;
        if(isSelf)
            navigationObject= new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, MyTimeLineFragment.class);
        else
            navigationObject= new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UsersTimeLineFragment.class);
        navigationObject.setParam(people);

        BaseFragment mActivePage = BaseFragment.newWebViewInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();

        new HeaderItemHolder(this,getApp(),findViewById(R.id.userHeader),people);

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(titleText, 0, View.INVISIBLE);

        tourThisScreen();
    }

    private void tourThisScreen() {

        /*resRdr = ResourceReader.getInstance(getApplicationContext());
        coachMarkMgr = CoachMarkManager.getInstance(getApplicationContext());*/
        mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*if (!coachMarkMgr.isTimeLineTourdone())
                    testTourGuide();*/
                mTourHelper.displayTimeLineTour(UserActivity.this, transView);
            }
        }, 2000);
    }


    /*private void testTourGuide() {

        Animation toolTipAnimation = new TranslateAnimation(0f, 0f, 200f, 0f);
        toolTipAnimation.setDuration(1000);
        toolTipAnimation.setFillAfter(true);
        toolTipAnimation.setInterpolator(new BounceInterpolator());

        Animation mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);

        ToolTip toolTip = new ToolTip()
                .setTitle("Time line")
                .setDescription(msgTimeLine)
                .setTextColor(resRdr.getColorFromResource(R.color.white))
                .setBackgroundColor(resRdr.getColorFromResource(R.color.colorAccent))
                .setShadow(true)
                .setEnterAnimation(mEnterAnimation)
                .setGravity(Gravity.BOTTOM | Gravity.CENTER);

        Pointer pointer = new Pointer().setColor(Color.RED).setGravity(Gravity.CENTER);

        final TourGuide tg = TourGuide.init(this)
                .setToolTip(toolTip)
                .setOverlay(new Overlay()
                        .setBackgroundColor(Color.parseColor("#AAE2E4E7")))
                .setPointer(pointer)
                .playOn(transView);

        tg.getOverlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tg.cleanUp();
            }
        });
        coachMarkMgr.setTimeLineTourGuideStatus(true);
    }*/

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        handleAlphaOnTitle(percentage);
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            startAlphaAnimation(titleText, 0, View.VISIBLE);

        } else {
            startAlphaAnimation(titleText, 0, View.INVISIBLE);

        }
    }


    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)? new AlphaAnimation(0f, 1f): new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Subscribe
    public void onEvent(AppActions data) {
        action(data.navigationDataObject);
    }
}
