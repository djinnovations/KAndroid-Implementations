package com.goldadorn.main.activities.productListing;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.BR;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.bindings.BindingRecycleItemHolder;
import com.goldadorn.main.databinding.FilterPriceItemBinding;
import com.goldadorn.main.databinding.FilterTypeItemBinding;
import com.goldadorn.main.model.FilterPrice;
import com.goldadorn.main.model.FilterType;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.IListProvider;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.dataManagers.simpleList.ListDataManager;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.DefaultRecyclerViewAdapter;
import com.kimeeo.library.listDataView.recyclerView.IViewProvider;
import com.kimeeo.library.listDataView.viewHelper.RecyclerViewHelper;

import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bpa001 on 4/21/16.
 */
public class PriceHelper implements IViewProvider{
    private final RecyclerView recyclerView;
    public PriceHelper(BaseActivity activity, RecyclerView recyclerView, RecyclerViewHelper.OnItemClick selector)
    {
        this.recyclerView= recyclerView;
        DataManager dataManager= createDataManager(activity);
        RecyclerViewHelper helper=new RecyclerViewHelper();
        DefaultRecyclerViewAdapter adapter=new DefaultRecyclerViewAdapter(dataManager,null,this);
        adapter.supportLoader = true;
        helper.adapter(adapter);
        helper.layoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        helper.decoration(new DividerDecoration(activity));
        helper.with(recyclerView);
        helper.setOnItemClick(selector);
        helper.dataManager(dataManager);
        try
        {
            helper.create();
        }catch (Exception e){

        }
    }
    IListProvider listData = new IListProvider() {
        public List<?> getList(PageData data, Map<String, Object> param) {
            if (data.curruntPage == 1) {
                List<FilterPrice> list = new ArrayList<>();
                list.add(getPrice("1000", "5000"));
                list.add(getPrice("5000", "10000"));
                list.add(getPrice("10000", "15000"));
                list.add(getPrice("15000", "20000"));
                list.add(getPrice("20000", "25000"));
                list.add(getPrice("25000", "30000"));
                list.add(getPrice("30000", "40000"));
                list.add(getPrice("40000", "50000"));
                list.add(getPrice("50000", "60000"));
                list.add(getPrice("60000", "70000"));
                list.add(getPrice("70000", "80000"));
                list.add(getPrice("80000", "90000"));
                list.add(getPrice("90000", "100000"));
                list.add(getPrice("100000", "1000000"));
                return list;
            }
            return null;
        }

        private FilterPrice getPrice(String min, String max) {
            FilterPrice price = new FilterPrice();
            price.setMax(max);
            price.setMin(min);
            price.setMaxVal("INR "+max);
            price.setMinVal("INR "+min);
            price.setId(min+max);
            return price;
        }
    };

    //DATA HERE
    protected DataManager createDataManager(BaseActivity activity)
    {
        ListDataManager listData1= new ListDataManager(activity,listData);
        listData1.setRefreshEnabled(false);
        return listData1;
    }


    //VIEW HERE
    public static class ViewTypes {
        public static final int VIEW_ITEM = 5;
    }

    @Override
    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_ITEM;
    }

    @Override
    public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container)
    {
        View view =inflater.inflate(R.layout.filter_price_item,container,false);
        ViewDataBinding binding = DataBindingUtil.bind(view);
        return binding.getRoot();
    }
    @Override
    public BaseItemHolder getItemHolder(int i, View view) {
        return new BindingItemHolder(view, BR.price);
    }

    public static class BindingItemHolder<T extends FilterPriceItemBinding> extends BindingRecycleItemHolder<T>
    {
        public BindingItemHolder(View itemView,int variableID)
        {
            super(itemView, variableID);
        }
    }
}
