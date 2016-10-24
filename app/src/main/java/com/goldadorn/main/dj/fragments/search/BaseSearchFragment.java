package com.goldadorn.main.dj.fragments.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.modules.modulesCore.DefaultVerticalListView;
import com.goldadorn.main.modules.people.DividerDecoration;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 20-10-2016.
 */
public abstract class BaseSearchFragment extends DefaultVerticalListView{

    /*public abstract void getQueryUrl();

    public abstract void getListTitle();*/

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.)
        //return super.onCreateView(inflater, container, savedInstanceState);
    }*/

    public static class ViewTypes {
        static final int VIEW_SEARCH_ITEM = 1111;
    }

    public int getListItemViewType(int position, Object item) {
        return ViewTypes.VIEW_SEARCH_ITEM;
    }

    public View getItemView(int viewType, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.adapter_base_search, container, false);
    }

    protected void configDataManager(DataManager dataManager) {
        super.configDataManager(dataManager);
        dataManager.setRefreshEnabled(false);
    }

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }


    public BaseItemHolder getItemHolder(int viewType, View view) {
        return new BaseSearchViewHolder(view);
    }

    public class BaseSearchViewHolder extends BaseItemHolder {

        @Bind(R.id.llNameHolder)
        View llNameHolder;
        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.userImage)
        ImageView userImage;

        @Bind(R.id.userName)
        TextView userName;
        @Bind(R.id.designer)
        TextView designer;


        public BaseSearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void updateItemView(Object o, View view, int i) {

        }
    }


}
