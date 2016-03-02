package com.goldadorn.main.modules.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.model.IFragmentData;

public class HomePage extends BaseHorizontalFragmentViewPager
{
    View disableApp;
    protected void garbageCollectorCall() {
        super.garbageCollectorCall();
        disableApp=null;
        socialFeedFragmentpage=null;
    }
    public void updateComments() {
        if(socialFeedFragmentpage!=null)
            socialFeedFragmentpage.updateComments();
    }
    public boolean allowedBack() {
        if(socialFeedFragmentpage!=null) {
            return socialFeedFragmentpage.allowedBack();
        }
        else
            return super.allowedBack();
    }
    SocialFeedFragment socialFeedFragmentpage;
    public void onItemCreated(Fragment page) {
        if(page instanceof SocialFeedFragment)
        {
            socialFeedFragmentpage = ((SocialFeedFragment) page);
            ((SocialFeedFragment)page).addDisableColver(disableApp);
        }
    }
    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment_page_view, container, false);
        disableApp = rootView.findViewById(R.id.disableApp);
        return rootView;
    }
    protected Application getApp() {
        BaseActivity baseActivity =(BaseActivity)getActivity();
        return baseActivity.getApp();
    }

    protected DataManager createDataManager()
    {
        DataManager dataManger =new StaticDataManger(getActivity());
        NavigationDataObject navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_feed);
        dataManger.add(navigationDataObject);
        navigationDataObject =(NavigationDataObject)getApp().getMainMenu().get(R.id.nav_people);
        dataManger.add(navigationDataObject);
        return dataManger;
    }
    public Fragment getItemFragment(int position,Object navigationObject)
    {
        if(navigationObject instanceof IFragmentData)
        {
            BaseFragment activePage = BaseFragment.newInstance((IFragmentData)navigationObject);
            return activePage;
        }
        return
                null;
    }
    @Override
    public String getItemTitle(int position,Object navigationObject)
    {
        if(navigationObject instanceof IFragmentData)
        {
            return ((IFragmentData)navigationObject).getName();
        }
        return super.getItemTitle(position,navigationObject);
    }

}
