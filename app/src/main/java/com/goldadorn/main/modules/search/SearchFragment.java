package com.goldadorn.main.modules.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.utils.IQueryListener;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.model.IFragmentData;

/**
 * Created by Vijith Menon on 1/3/16.
 */
public class SearchFragment extends BaseHorizontalFragmentViewPager {
    private final static String TAG = SearchFragment.class.getSimpleName();
    private final static boolean DEBUG = true;


    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment_page_view, container, false);
        return rootView;
    }

    protected Application getApp() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        return baseActivity.getApp();
    }

    protected DataManager createDataManager() {
        DataManager dataManger = new StaticDataManger(getActivity());
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(
                R.id.nav_id_hashtag);
        dataManger.add(navigationDataObject);
        navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_people);
        dataManger.add(navigationDataObject);
        return dataManger;
    }

    public Fragment getItemFragment(int position, Object navigationObject) {
        if (navigationObject instanceof IFragmentData) {
            BaseFragment activePage = BaseFragment.newInstance((IFragmentData) navigationObject);
            return activePage;
        }
        return
                null;
    }

    @Override
    public String getItemTitle(int position, Object navigationObject) {
        if (navigationObject instanceof IFragmentData) {
            return ((IFragmentData) navigationObject).getName();
        }
        return super.getItemTitle(position, navigationObject);
    }

    @Override
    public void onStart() {
        super.onStart();
        //((MainActivity) getActivity()).registerQueryListener(mQueryListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //((MainActivity) getActivity()).unRegisterQueryListener(mQueryListener);
    }

    private IQueryListener mQueryListener = new IQueryListener() {
        @Override
        public void onQueryChange(String newQuery) {
        }

        @Override
        public void onQuerySubmit(String query) {
        // TODO search implementation
        }
    };

    @Override
    public boolean allowedBack() {
        //((MainActivity)getActivity()).goBack();
        return false;
    }
}
