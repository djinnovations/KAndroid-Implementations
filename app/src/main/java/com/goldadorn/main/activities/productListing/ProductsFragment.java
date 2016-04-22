package com.goldadorn.main.activities.productListing;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.BR;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.bindings.BindingRecycleItemHolder;
import com.goldadorn.main.databinding.ProductPickGridItemBinding;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.ResultFormating;
import com.goldadorn.main.utils.URLHelper;
import com.google.gson.Gson;
import com.kimeeo.library.actions.LoadDataAQuery;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.ResponsiveView;

import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;

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
        loadLikes(dataList);


    }

    private void loadLikes(final List<?> dataList) {
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




            try {
                LoadDataAQuery data = new LoadDataAQuery(getActivity());
                data.setCookies(getApp().getCookies());
                LoadDataAQuery.Result result= new LoadDataAQuery.Result()
                {
                    @Override
                    public void done(String url, Object json, Object status) {
                            if(json!=null && json instanceof String) {
                                json = ResultFormating.formating((String) json);
                                Gson gson = new Gson();
                                ProductLikeData data= gson.fromJson((String) json,ProductLikeData.class);
                                if(data.data!=null && data.data.size()!=0)
                                {
                                    FilterProductListing filterProductListing;
                                    for (ProductLikeData.ProductLike productLike : data.data) {
                                        for (int i = 0; i < dataList.size(); i++) {
                                            if(dataList.get(i) !=null && dataList.get(i) instanceof FilterProductListing)
                                            {
                                                filterProductListing= (FilterProductListing)dataList.get(i);
                                                if(filterProductListing.getProdId()==productLike.getProductId())
                                                {
                                                    filterProductListing.setLikeCount(productLike.getLikeCount()+"");
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                };


                Map<String,Object> params= new HashMap<>();

                list = "{\"prodIds\":["+list+"]}";
                params.put(AQuery.POST_ENTITY,new StringEntity(list));
                String url = URLHelper.getInstance().getProductsLikes();
                data.perform(url,result,params,"");

            }catch (Exception e){}
        }
    }
    public class ProductLikeData
    {
        public List<ProductLike> data;
        public class ProductLike
        {
            private int productId;
            private int likeCount;

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public int getLikeCount() {
                return likeCount;
            }

            public void setLikeCount(int likeCount) {
                this.likeCount = likeCount;
            }
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
            if(loadedDataVO!=null && loadedDataVO instanceof Result)
            {
                Result result = (Result) loadedDataVO;
                if(result.offset!=-1 && offset != result.offset) {
                    offset = result.offset;
                    pageData.curruntPage += 1;
                    pageData.totalPage += 1;
                }
                else
                {
                    pageData.totalPage=pageData.curruntPage;
                }
            }
            else
            {
                pageData.totalPage=pageData.curruntPage;
            }

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
        private int offset=-1;
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