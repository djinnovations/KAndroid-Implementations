package com.goldadorn.main.activities.productListing;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.goldadorn.main.BR;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.bindings.BindingRecycleItemHolder;
import com.goldadorn.main.databinding.ProductPickGridItemBinding;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.DefaultDividerDecoration;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.ResponsiveView;

import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class ProductsFragment extends ResponsiveView implements DefaultProjectDataManager.IDataManagerDelegate
{
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }


    protected Application getApp() {
        BaseActivity baseActivity =(BaseActivity)getActivity();
        return baseActivity.getApp();
    }
    public void onCallEnd(List<?> dataList, boolean isRefreshData) {
        super.onCallEnd(dataList,isRefreshData);
        if(dataList!=null && dataList.size()!=0)
        {
            String list="";
            for (int i = 0; i < dataList.size(); i++) {
                if(dataList.get(i) instanceof FilterProductListing)
                {
                    FilterProductListing item = (FilterProductListing)dataList.get(i);
                    list +=item.getProdId()+"";
                    if(i!=(dataList.size()-1))
                    {
                        list +=",";
                    }
                }
            }
            System.out.println(list);
        }
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


    private String getParam(int offset)
    {
        if(filters==null)
            return "{\"offset\" : "+offset+"}";
        else
            return "{\"offset\" : \""+offset+"\"}";
    }

    private String filters;
    private int offset = 0;

    public class DataManager extends DefaultProjectDataManager
    {
        public DataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
        {
            super(context,delegate,cookies);
            setIsConfigurableObject(true);
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
        return Result.class;
    }


    @Override
    public View getItemView(int i, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View view =layoutInflater.inflate(R.layout.product_pick_grid_item, viewGroup, false);
        ViewDataBinding binding = DataBindingUtil.bind(view);
        return binding.getRoot();
    }
    @Override
    public BaseItemHolder getItemHolder(int i, View view) {
        return new BindingItemHolder(view, BR.product);
    }

    public static class BindingItemHolder<T extends ProductPickGridItemBinding> extends BindingRecycleItemHolder<T>
    {
        public BindingItemHolder(View itemView,int variableID)
        {
            super(itemView, variableID);
        }
    }

    public static class Result extends CodeDataParser
    {
        List<FilterProductListing> data;
        private int offset;
        public List<?> getList()
        {
            return data;
        }
        public List<FilterProductListing> getData()
        {
            return data;
        }
        public void setData(List<FilterProductListing> data){this.data=data;}

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}