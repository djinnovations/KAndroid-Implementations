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
import com.goldadorn.main.databinding.FilterDesignerItemBinding;
import com.goldadorn.main.model.Designer;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.DefaultRecyclerViewAdapter;
import com.kimeeo.library.listDataView.recyclerView.IViewProvider;
import com.kimeeo.library.listDataView.viewHelper.RecyclerViewHelper;

import org.apache.http.cookie.Cookie;
import java.util.List;
/**
 * Created by bpa001 on 4/21/16.
 */
public class DesignerHelper implements IViewProvider,DefaultProjectDataManager.IDataManagerDelegate {
    private final RecyclerView recyclerView;
    public DesignerHelper(BaseActivity activity, RecyclerView recyclerView, RecyclerViewHelper.OnItemClick selector)
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

    //DATA HERE
    protected DataManager createDataManager(BaseActivity activity)
    {
        return new ListDataManager(activity,this,activity.getApp().getCookies());
    }

    private int offset=0;
    @Override
    public String getNextDataURL(PageData pageData) {
        String url=URLHelper.getInstance().getDesignersFilter(offset);
        return url;
    }
    @Override
    public String getRefreshDataURL(PageData pageData) {return null;}
    @Override
    public Class getLoadedDataParsingAwareClass() {return Result.class;}

    public class ListDataManager extends DefaultProjectDataManager
    {
        public ListDataManager(Context context, IDataManagerDelegate delegate, List<Cookie> cookies)
        {
            super(context,delegate,cookies);
            setRefreshEnabled(false);
            setIsConfigurableObject(true);
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
    public static class Result extends CodeDataParser
    {
        List<Designer> designers;
        private int offset=-1;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        private String status;
        public List<?> getList()
        {
            return designers;
        }
        public List<Designer> getData()
        {
            return designers;
        }
        public void setData(List<Designer> data){this.designers=data;}

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
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
        View view =inflater.inflate(R.layout.filter_designer_item,container,false);
        ViewDataBinding binding = DataBindingUtil.bind(view);
        return binding.getRoot();
    }
    @Override
    public BaseItemHolder getItemHolder(int i, View view) {
        return new BindingItemHolder(view, BR.designer);
    }

    public static class BindingItemHolder<T extends FilterDesignerItemBinding> extends BindingRecycleItemHolder<T>
    {
        public BindingItemHolder(View itemView,int variableID)
        {
            super(itemView, variableID);
        }
    }
}
