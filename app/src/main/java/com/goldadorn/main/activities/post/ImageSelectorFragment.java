package com.goldadorn.main.activities.post;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.listDataView.viewPager.viewPager.HorizontalViewPager;
import com.kimeeo.library.model.IFragmentData;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class ImageSelectorFragment extends BaseHorizontalFragmentViewPager implements DefaultProjectDataManager.IDataManagerDelegate
{
    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.collection_fragment_page_view, container, false);
        View disableApp = rootView.findViewById(R.id.disableApp);
        disableApp.setVisibility(View.GONE);
        return rootView;
    }
    protected Application getApp() {
        BaseActivity baseActivity =(BaseActivity)getActivity();
        return baseActivity.getApp();
    }
    protected DataManager createDataManager()
    {
        return new SocialFeedProjectDataManager(getActivity(),this,getApp().getCookies());
    }
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.GALLERY, 0);
        params.put(URLHelper.LIKE_A_POST.PATH, null);
        return params;
    }

    public class SocialFeedProjectDataManager extends DefaultProjectDataManager
    {
        public SocialFeedProjectDataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
        {
            super(context,delegate,cookies);
        }
        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }
        protected boolean isRefreshPage(PageData pageData, String url) {
            return false;
        }
        protected void updatePagingData(BaseDataParser loadedDataVO)
        {
            pageData.curruntPage +=1;
            pageData.totalPage +=1;
        }
    }

    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFolderServiceURL();
        return null;
    }
    public Class getLoadedDataParsingAwareClass()
    {
        return FolderResult.class;
    }
    public String getPath()
    {
        return null;
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
    public static class FolderResult extends CodeDataParser
    {
        List<ServerFolderObject> data;
        Object data1;
        public List<?> getList()
        {
            return data;
        }
        public Object getData()
        {
            return this;
        }
        public void setData(Object data)
        {
            this.data1=this;
        }
    }
}
