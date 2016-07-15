package com.goldadorn.main.dj.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.goldadorn.main.dj.model.FilterPostParams;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.socialFeeds.FilterEmptyViewHelper;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DJphy on 18-06-2016.
 */
public class FilterTimelineFragment extends SocialFeedFragment {

    static FilterPostParams fpp;


    public static FilterTimelineFragment newInstance(FilterPostParams fpp) {
        FilterTimelineFragment f = new FilterTimelineFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentKeys.FILTER_POST_PARAMS, fpp);
        FilterTimelineFragment.fpp = fpp;
        f.setArguments(bundle);
        return f;
    }

    public void onViewCreated(View view) {
        Log.d("djtimeline", "onViewCreated ");
        super.onViewCreated(view);
        //fpp = getArguments().getParcelable(IntentKeys.FILTER_POST_PARAMS);
        getFloatingActionsMenu().setVisibility(View.GONE);
        getFabBackImage().setVisibility(View.GONE);
        //followPeopleHelper = new FollowPeopleHelper(getActivity(), getApp().getCookies(),postUpdateResult);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateComments();
    }

    @Override
    protected boolean allowPostOptions() {
        //return super.allowPostOptions();
        return false;
    }

    @Override
    protected EmptyViewHelper createEmptyViewHelper() {
        EmptyViewHelper helper = new FilterEmptyViewHelper(this.getActivity(), this.createEmptyView(this.mRootView), this,
                this.showInternetError(), this.showInternetRetryButton());
        return helper;
    }
    @Override
    public void retry() {
        //super.retry();
    }



    protected DataManager createDataManager() {
        return new FilterDataManager(getActivity(), this, getApp().getCookies());
    }

    public class FilterDataManager extends SocialFeedProjectDataManager {
        public FilterDataManager(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
            super(context, delegate, cookies);
        }

        @Override
        public Map<String, Object> getRefreshDataServerCallParams(PageData data) {
            //return super.getRefreshDataServerCallParams(data);
            return null;
        }

    }

    protected void configDataManager(DataManager dataManager) {
        //People userData=crateUser();
        /*if(userData!=null) {
            setUser(userData);*/
        //dataManager.setRefreshItemPos(1);
        dataManager.setRefreshEnabled(false);
        //    dataManager.add(userData);
        //}
    }

    @Override
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.OFFSET, offset);
        params.put(URLHelper.LIKE_A_POST.POST_ID, 0);
        params.put(URLHelper.LIKE_A_POST.USER_ID, fpp.getUserid());
        Log.d("djtimeline", "req params: " + params);
        return params;
    }

    public Map<String, Object> getRefreshDataParams(PageData data) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(URLHelper.LIKE_A_POST.OFFSET, refreshOffset);
            SocialPost sp = (SocialPost) getDataManager().get(1);
            params.put(URLHelper.LIKE_A_POST.POST_ID, sp.getPostId());
            params.put(URLHelper.LIKE_A_POST.USER_ID, fpp.getUserid());
            return params;
        } catch (Exception e) {
        }
        return null;
    }


    public String getNextDataURL(PageData pageData) {
        Log.d("djtimeline", "getNextDataURL ");
        isRefreshingData = false;
        return getApp().getUrlHelper().getUsersSocialFeedServiceURL();
    }

    public String getRefreshDataURL(PageData pageData) {
        isRefreshingData = false;
        return getApp().getUrlHelper().getUsersSocialFeedServiceURL();
    }


}
