package com.goldadorn.main.activities.post;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.socialFeeds.DividerDecoration;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.BaseProfileRecyclerView;
import com.kimeeo.library.listDataView.recyclerView.DefaultDividerDecoration;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.GridView;
import com.kimeeo.library.listDataView.recyclerView.viewProfiles.BaseViewProfile;
import com.kimeeo.library.listDataView.recyclerView.viewProfiles.VerticalGrid;
import com.kimeeo.library.listDataView.recyclerView.viewProfiles.VerticalList;
import com.kimeeo.library.listDataView.recyclerView.viewProfiles.VerticalStaggeredGrid;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class GaleryGrid extends BaseProfileRecyclerView implements DefaultProjectDataManager.IDataManagerDelegate{


    public void onItemClick(Object baseObject) {
        ServerFolderObject file = (ServerFolderObject)baseObject;
        EventBus.getDefault().post(file);
    }




    public static class ViewTypes {
        public static final int VIEW_ITEM = 5;
    }

    List<BaseViewProfile> viewProfileList =null;
    GridViewProfile gridViewProfile;
    ListViewProfile listViewProfile;
    BaseViewProfile selected;
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        if(viewProfileList==null)
        {
            viewProfileList = new ArrayList<>();
            gridViewProfile=new GridViewProfile("Grid View",this);
            viewProfileList.add(gridViewProfile);
            listViewProfile =new ListViewProfile("List View",this);
            viewProfileList.add(listViewProfile);
            //viewProfileList.add(new StaggeredGridViewProfile("Staggered View",this));
        }

        selected =viewProfileList.get(0);
        applyProfile(selected);
        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();
        loadNext();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.view_porfiles, menu);
        if(selected==gridViewProfile)
        {
            menu.findItem(R.id.nav_list).setVisible(true);
            menu.findItem(R.id.nav_grid).setVisible(false);
        }
        else if(selected==listViewProfile)
        {
            menu.findItem(R.id.nav_list).setVisible(false);
            menu.findItem(R.id.nav_grid).setVisible(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.nav_list)
            selected=listViewProfile;
        else if(item.getItemId()==R.id.nav_grid)
            selected=gridViewProfile;

        applyProfile(selected);
        getActivity().supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
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
        ServerFolderObject folder=(ServerFolderObject) getFragmentData();
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.GALLERY, 0);
        params.put(URLHelper.LIKE_A_POST.PATH, folder.getPath());
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

    protected void configDataManager(DataManager dataManager) {
        dataManager.setRefreshEnabled(false);
    }



    public  class ListViewProfile extends VerticalList
    {
        public RecyclerView.ItemDecoration createItemDecoration(Activity context) {
            return new DividerDecoration(getActivity());
        }

        public ListViewProfile(String name, BaseProfileRecyclerView host)
        {
            super(name,host);
        }

        public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
        {
            return inflater.inflate(R.layout.server_gallery_list_view_row,null);
        }
        public BaseItemHolder getItemHolder(int viewType,View view)
        {
            return new ItemHolder(view);
        }

        public int getListItemViewType(int position,Object item)
        {
            return ViewTypes.VIEW_ITEM;
        }



        public class ItemHolder extends BaseItemHolder {

            @Bind(R.id.image)
            ImageView image;

            public ItemHolder(View itemView)
            {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
            public void updateItemView(Object item,View view,int position)
            {
                ServerFolderObject file=(ServerFolderObject)item;
                Picasso.with(getContext())
                        .load(file.getPreview())
                        .placeholder(R.drawable.vector_image_logo_square_100dp)
                        .tag(getContext())
                        .into(image);
            }
        }
    }


    public  class GridViewProfile extends VerticalGrid
    {

        public int getColumnsPhone() {
            return 3;
        }

        public int getColumnsTablet10() {
            return 5;
        }

        public int getColumnsTablet7() {
            return 3;
        }

        public GridViewProfile(String name, BaseProfileRecyclerView host)
        {
            super(name,host);
        }

        public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
        {
            return inflater.inflate(R.layout.server_gallery_cell,null);
        }
        public BaseItemHolder getItemHolder(int viewType,View view)
        {
            return new ItemHolder(view);
        }

        public int getListItemViewType(int position,Object item)
        {
            return ViewTypes.VIEW_ITEM;
        }


        public class ItemHolder extends BaseItemHolder {

            @Bind(R.id.image)
            ImageView image;

            public ItemHolder(View itemView)
            {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
            public void updateItemView(Object item,View view,int position)
            {
                ServerFolderObject file=(ServerFolderObject)item;
                Picasso.with(getContext())
                        .load(file.getPreview())
                        .placeholder(R.drawable.vector_image_logo_square_100dp)
                        .tag(getContext())
                        .resize(200, 200)
                        .into(image);
            }
        }
    }




    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFolderServiceURL();
        return null;
    }
    public String getRefreshDataURL(PageData pageData){return null;}
    public Class getLoadedDataParsingAwareClass()
    {
        return ImageSelectorFragment.FolderResult.class;
    }



}
