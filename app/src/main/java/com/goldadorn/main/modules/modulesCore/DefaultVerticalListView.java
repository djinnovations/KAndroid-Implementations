package com.goldadorn.main.modules.modulesCore;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.UserActivity;
import com.goldadorn.main.eventBusEvents.AppActions;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.People;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.ResponsiveView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by bhavinpadhiyar on 12/26/15.
 */
abstract public class DefaultVerticalListView extends ResponsiveView implements DefaultProjectDataManager.IDataManagerDelegate
{
    public int getColumnsPhone() {
        return 1;
    }

    public int getColumnsTablet10() {
        return 2;
    }

    public int getColumnsTablet7() {
        return 2;
    }

    protected Application getApp() {
        BaseActivity baseActivity =(BaseActivity)getActivity();
        return baseActivity.getApp();
    }
    protected MainActivity getAppMainActivity() {
        if(getActivity() instanceof MainActivity)
            return (MainActivity)getActivity();
        return null;
    }
    protected DataManager createDataManager()
    {
        return new DefaultProjectDataManager(getActivity(),this,getApp().getCookies());
    }
    abstract public String getNextDataURL(PageData data);
    public String getRefreshDataURL(PageData pageData)
    {
        return null;
    }
    abstract public Class<BaseDataParser> getLoadedDataParsingAwareClass();



    public void gotoUser(People people)
    {
        if(people.isSelf())
        {
            NavigationDataObject navigationDataObject = (NavigationDataObject)getApp().getMainMenu().get(R.id.nav_timeline);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
        }
        else
        {
            NavigationDataObject navigationDataObject = new NavigationDataObject(IDUtils.generateViewId(),people.getUserName()+"'s Timeline", NavigationDataObject.ACTION_TYPE.ACTION_TYPE_ACTIVITY, UserActivity.class);
            Map<String, Object> data= new HashMap<>();
            data.put("NAME",people.getUserName());
            data.put("ID",people.getUserId());
            data.put("FOLLOWER_COUNT",people.getFollowerCount());
            data.put("FOLLOWING_COUNT",people.getFollowingCount());
            if(people.getProfilePic()!=null)
                data.put("PROFILE_PIC",people.getProfilePic());
            data.put("IS_DESIGNER",people.getIsDesigner());
            data.put("IS_SELF",people.isSelf());
            data.put("IS_FOLLOWING",people.getIsFollowing());
            navigationDataObject.setParam(data);
            EventBus.getDefault().post(new AppActions(navigationDataObject));
        }
    }
}
