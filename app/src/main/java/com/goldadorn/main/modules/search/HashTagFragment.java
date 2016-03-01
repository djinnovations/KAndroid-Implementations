package com.goldadorn.main.modules.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.people.DividerDecoration;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 1/3/16.
 */
public class HashTagFragment extends DefaultVerticalListView {
    private final static String TAG = HashTagFragment.class.getSimpleName();
    private final static boolean DEBUG = true;

    public int getColumnsPhone() {
        return 1;
    }

    public int getColumnsTablet10() {
        return 1;
    }

    public int getColumnsTablet7() {
        return 1;
    }

    protected void configDataManager(DataManager dataManager) {
        super.configDataManager(dataManager);
        dataManager.setRefreshEnabled(false);
    }
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }
    public static class ViewTypes {
        public static final int VIEW_USER = 6;
    }
    public void onItemClick(Object baseObject)
    {
        super.onItemClick(baseObject);
        gotoUser((People) baseObject);
    }

    public void onViewCreated(View view) {
    }
    public int getListItemViewType(int position,Object item)
    {
        return ViewTypes.VIEW_USER;
    }
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_hashtag,null);
    }
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        return new HashtagItemHolder(view);
    }

    public Class getLoadedDataParsingAwareClass()
    {
        return HashTagResult.class;
    }
    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFindPeopleServiceURL();
        else
            return null;
    }
    public class HashtagItemHolder extends BaseItemHolder {

        @Bind(R.id.tag)
        TextView tag;

        private View.OnClickListener itemClick = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(v== tag)
                {
                    gotoUser(people);
                }
            }
        };
        private People people;

        public HashtagItemHolder(View itemView)
        {
            super(itemView);
            tag.setOnClickListener(itemClick);
        }
        public void updateItemView(Object item,View view,int position)
        {
            people =(People)item;

            tag.setText(people.getUserName());
        }
    }



    public static class HashTagResult extends CodeDataParser
    {
        List<People> data;
        public List<?> getList()
        {
            return data;
        }
        public Object getData()
        {
            return data;
        }
        public void setData(Object data)
        {
            this.data=(List<People>)data;
        }
    }
}
