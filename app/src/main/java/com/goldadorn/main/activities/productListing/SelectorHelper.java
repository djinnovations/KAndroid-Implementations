package com.goldadorn.main.activities.productListing;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goldadorn.main.BR;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.bindings.BindingRecycleItemHolder;
import com.goldadorn.main.model.Designer;
import com.goldadorn.main.model.FilterCollection;
import com.goldadorn.main.model.FilterPrice;
import com.goldadorn.main.model.FilterType;
import com.goldadorn.main.model.IIDInterface;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.StaticDataManger;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.DefaultRecyclerViewAdapter;
import com.kimeeo.library.listDataView.recyclerView.IViewProvider;
import com.kimeeo.library.listDataView.viewHelper.RecyclerViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpa001 on 4/21/16.
 */
public class SelectorHelper implements IViewProvider {
    DataManager dataManager;
    DefaultRecyclerViewAdapter adapter;
    public List<Object> list()
    {
        return dataManager;
    }
    public boolean add(IIDInterface data)
    {
        boolean isAdded=false;
        for (Object o : dataManager) {
            if(o instanceof IIDInterface && ((IIDInterface)o).getId().equals(data.getId())) {
                isAdded = true;
                break;
            }
        }
        if(isAdded==false) {
            dataManager.add(0,data);
            if(recyclerView !=null && dataManager.size()!=0)
                recyclerView.setVisibility(View.VISIBLE);
            else if(recyclerView !=null && dataManager.size()==0)
                recyclerView.setVisibility(View.GONE);
            return true;
        }
        Toast.makeText(recyclerView.getContext(), "Item is already added to filters", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void addAll(ArrayList<?> data)
    {
        dataManager.addAll(data);
        if(recyclerView !=null && dataManager.size()!=0)
            recyclerView.setVisibility(View.VISIBLE);
        else if(recyclerView !=null && dataManager.size()==0)
            recyclerView.setVisibility(View.GONE);
    }
    public void removeAll()
    {
        dataManager.removeAll(dataManager);
        recyclerView.setVisibility(View.GONE);
    }

    public void remove(Object o)
    {
        try {
            for (int i = 0; i < dataManager.size(); i++) {
                if(dataManager.get(i)==o)
                {
                    dataManager.remove(i);
                    if(onRemoveListner!=null)
                        onRemoveListner.remove(o);
                    if(recyclerView !=null && dataManager.size()!=0)
                        recyclerView.setVisibility(View.VISIBLE);
                    else if(recyclerView !=null && dataManager.size()==0)
                        recyclerView.setVisibility(View.GONE);
                    break;
                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    RecyclerViewHelper.OnItemClick selector = new RecyclerViewHelper.OnItemClick()
    {
        @Override
        public void onItemClick(Object o)
        {
            remove(o);
        }
    };
    /*
    RecyclerViewHelper helper = new RecyclerViewHelper(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                super.onItemClick(parent, view, position, id);
            }catch (Exception e){
                System.out.println(e);
            }
        }
    };*/
    /*
    public class DefaultRecyclerViewAdapter1 extends DefaultRecyclerViewAdapter
    {
        public DefaultRecyclerViewAdapter1(DataManager dataManager, OnCallService onCallService, IViewProvider viewProvider)
        {
            super(dataManager, onCallService, viewProvider);
        }

        public void itemsRemoved(int position, List items) {
            if(items != null && items.size() != 0)
                this.notifyDataSetChanged();
        }
    }*/
    RecyclerView recyclerView;
    public SelectorHelper(BaseActivity activity, RecyclerView recyclerView)
    {

        this.recyclerView=recyclerView;
        dataManager= createDataManager(activity);
        RecyclerViewHelper helper=new RecyclerViewHelper();
        adapter=new DefaultRecyclerViewAdapter(dataManager,null,this);
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

            if(recyclerView !=null && dataManager.size()!=0)
                recyclerView.setVisibility(View.VISIBLE);
            else if(recyclerView !=null && dataManager.size()==0)
                recyclerView.setVisibility(View.GONE);
        }catch (Exception e){

        }
    }

    //DATA HERE
    protected DataManager createDataManager(BaseActivity activity)
    {
        StaticDataManger listData1= new StaticDataManger(activity);
        listData1.setRefreshEnabled(false);
        return listData1;
    }
    OnRemoveListner onRemoveListner;
    public void onRemoveListner(OnRemoveListner onRemoveListner) {
        this.onRemoveListner=onRemoveListner;
    }

    public void removePrice() {
        for (int i = 0; i < dataManager.size(); i++) {
            if(dataManager.get(i) instanceof FilterPrice)
            {
                dataManager.remove(i);
                break;
            }
        }
    }

    public interface OnRemoveListner
    {
        void remove(Object o);
    }


    //VIEW HERE
    public static class ViewTypes {
        public static final int VIEW_ITEM_PRICE = 5;
        public static final int VIEW_ITEM_DESIGNER = 10;
        public static final int VIEW_ITEM_COLLECTION = 15;
        public static final int VIEW_ITEM_PRODUCT_TYPE = 20;
    }

    @Override
    public int getListItemViewType(int position,Object item)
    {
        if(item instanceof FilterPrice)
            return ViewTypes.VIEW_ITEM_PRICE;
        else if(item instanceof Designer)
            return ViewTypes.VIEW_ITEM_DESIGNER;
        else if(item instanceof FilterCollection)
            return ViewTypes.VIEW_ITEM_COLLECTION;
        else if(item instanceof FilterType)
            return ViewTypes.VIEW_ITEM_PRODUCT_TYPE;
        return -1;
    }

    @Override
    public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container)
    {
        View view=null;
        if(viewType==ViewTypes.VIEW_ITEM_PRICE)
            view=inflater.inflate(R.layout.filter_price_item_selected,container,false);
        else if(viewType==ViewTypes.VIEW_ITEM_DESIGNER)
            view=inflater.inflate(R.layout.filter_designer_item_selected,container,false);
        else if(viewType==ViewTypes.VIEW_ITEM_COLLECTION)
            view=inflater.inflate(R.layout.filter_collection_item_selected,container,false);
        else if(viewType==ViewTypes.VIEW_ITEM_PRODUCT_TYPE)
            view=inflater.inflate(R.layout.filter_type_item_selected,container,false);

        if(view!=null) {
            ViewDataBinding binding = DataBindingUtil.bind(view);
            return binding.getRoot();
        }
        return null;
    }
    @Override
    public BaseItemHolder getItemHolder(int viewType, View view) {
        if(viewType==ViewTypes.VIEW_ITEM_PRICE)
            return new BindingItemHolder(view, BR.price);
        else if(viewType==ViewTypes.VIEW_ITEM_DESIGNER)
            return new BindingItemHolder(view, BR.designer);
        else if(viewType==ViewTypes.VIEW_ITEM_COLLECTION)
            return new BindingItemHolder(view, BR.collection);
        else if(viewType==ViewTypes.VIEW_ITEM_PRODUCT_TYPE)
            return new BindingItemHolder(view, BR.productType);

        return null;
    }
    public static class BindingItemHolder<T extends ViewDataBinding> extends BindingRecycleItemHolder<T>
    {
        public BindingItemHolder(View itemView,int variableID)
        {
            super(itemView, variableID);
        }
    }
}
