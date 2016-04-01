package com.goldadorn.main.modules.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.model.IFragmentData;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    protected View createIndicator(View rootView)
    {
        View indicator = rootView.findViewById(R.id.indicator);
        if(getActivity() instanceof MainActivity && ((MainActivity)getActivity()).getPageIndicator()!=null)
        {
            if(indicator!=null)
                indicator.setVisibility(View.GONE);
            return ((MainActivity)getActivity()).getPageIndicator();
        }
        return indicator;
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

    protected RecyclerTabLayout.Adapter<?> getRecyclerViewTabProvider(ViewPager viewPager) {
        return new TabIndicatorRecyclerViewAdapter(viewPager,getDataManager());
    }
    private void gotoData(Object data) {
        if(getDataManager()!=null && data!=null)
        {
            for (int i = 0; i < getDataManager().size(); i++) {
                if(getDataManager().get(i)==data)
                {
                    gotoItem(i,false);
                    break;
                }
            }
        }
    }

    public class TabIndicatorRecyclerViewAdapter extends com.kimeeo.library.listDataView.viewPager.TabIndicatorRecyclerViewAdapter {

        public TabIndicatorRecyclerViewAdapter(ViewPager viewPager, DataManager dataManager) {
            super(viewPager,dataManager);
        }
        protected  ViewHolder getViewHolder(View view)
        {
            return new MyViewHolder(view);
        }
        protected View getView(ViewGroup parent)
        {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_tab_view, parent, false);
        }
        public int getItemCount() {
            return getDataManager().size();
        }


        public class MyViewHolder extends ViewHolder {

            @Bind(R.id.textView)
            public TextView textView;

            @Bind(R.id.selected)
            public View selected;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        gotoData(data);
                    }
                });

                TypefaceHelper.setFont(itemView.getResources().getString(R.string.font_name_text_secondary),textView);
            }
            NavigationDataObject data;
            public void updatedSelectedItem(Object o) {
                data=(NavigationDataObject) o;
                textView.setText(data.getName());
                textView.setTextColor(textView.getResources().getColor(R.color.colorPrimary));
                selected.setVisibility(View.VISIBLE);
            }
            public void updatedNormalItem(Object o)
            {
                data=(NavigationDataObject) o;
                textView.setText(data.getName());
                textView.setTextColor(textView.getResources().getColor(R.color.colorPrimaryAlpha));
                selected.setVisibility(View.GONE);
            }
        }
    }
}
