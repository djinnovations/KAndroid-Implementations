package com.goldadorn.main.activities.productListing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.ServerFolderActivity;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.ResponsiveView;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.model.IFragmentData;
import com.nshmura.recyclertablayout.RecyclerTabLayout;
import com.rey.material.widget.ProgressView;

import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class ProductsFragment extends ResponsiveView implements DefaultProjectDataManager.IDataManagerDelegate
{
    protected Application getApp() {
        BaseActivity baseActivity =(BaseActivity)getActivity();
        return baseActivity.getApp();
    }
    protected DataManager createDataManager()
    {
        return new DataManager(getActivity(),this,getApp().getCookies());
    }
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        String param = getParam(offset);

        try {
            params.put(AQuery.POST_ENTITY,new StringEntity(param));
        }catch (Exception e){}

        return params;
    }

    private String getParam(String offset)
    {
        if(filters==null)
            return "{\"offset\" : "+offset+"}";
        else
            return "{\"offset\" : \""+offset+"\"}";
    }

    private String filters;
    private String offset = "0";

    public class DataManager extends DefaultProjectDataManager
    {
        public DataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
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
        return getApp().getUrlHelper().getApplyfilterServiceURL();
    }

    @Override
    public String getRefreshDataURL(PageData pageData) {
        return null;
    }

    public Class getLoadedDataParsingAwareClass()
    {
        return FolderResult.class;
    }


    @Override
    public View getItemView(int i, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return null;
    }
    @Override
    public BaseItemHolder getItemHolder(int i, View view) {
        return null;
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
