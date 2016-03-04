package com.goldadorn.main.activities.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.GridView;
import com.squareup.picasso.Picasso;

import org.apache.http.cookie.Cookie;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class GaleryGrid_backup extends GridView implements DefaultProjectDataManager.IDataManagerDelegate{


    public void onItemClick(Object baseObject) {
        ServerFolderObject file = (ServerFolderObject)baseObject;
        EventBus.getDefault().post(file);
    }

    public int getColumnsPhone() {
        return 3;
    }

    public int getColumnsTablet10() {
        return 5;
    }

    public int getColumnsTablet7() {
        return 3;
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
    @Override
    public View getItemView(int viewType,LayoutInflater inflater,ViewGroup container)
    {
        return inflater.inflate(R.layout.server_gallery_cell,null);
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
    public BaseItemHolder getItemHolder(int viewType,View view)
    {
        return new ItemHolder(view);
    }
    public class ItemHolder extends BaseItemHolder {

        @Bind(R.id.image)
        ImageView image;

        public ItemHolder(View itemView)
        {
            super(itemView);
        }
        public void updateItemView(Object item,View view,int position)
        {
            ServerFolderObject file=(ServerFolderObject)item;
            Picasso.with(getContext())
                    .load(file.getPreview())
                    .placeholder(R.drawable.vector_image_logo_square_100dp)
                    .tag(getContext())
                    .resize(200,200)
                    .into(image);
        }
    }
}
