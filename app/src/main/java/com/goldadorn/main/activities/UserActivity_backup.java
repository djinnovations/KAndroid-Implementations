package com.goldadorn.main.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.goldadorn.main.R;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.timeLine.MyTimeLineFragment;
import com.goldadorn.main.modules.timeLine.UsersTimeLineFragment;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.fragments.BaseFragment;

import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class UserActivity_backup extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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

        setTitle(title + "");
        FragmentManager fragmentManager = getSupportFragmentManager();



        NavigationDataObject navigationObject;
        if(isSelf)
            navigationObject= new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, MyTimeLineFragment.class);
        else
            navigationObject= new NavigationDataObject(IDUtils.generateViewId(),title, NavigationDataObject.ACTION_TYPE.ACTION_TYPE_FRAGMENT_VIEW, UsersTimeLineFragment.class);
        navigationObject.setParam(people);

        BaseFragment mActivePage = BaseFragment.newWebViewInstance(navigationObject);
        fragmentManager.beginTransaction().replace(R.id.mainHolder, mActivePage).commit();
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
