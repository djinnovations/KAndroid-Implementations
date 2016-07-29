package com.goldadorn.main.dj.fragments;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.showcase.FollowerListActivity;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.model.Liked;
import com.goldadorn.main.modules.likes.LikesView;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.modules.socialFeeds.helper.FollowerListEmptyViewHelper;
import com.kimeeo.library.listDataView.EmptyViewHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 28-07-2016.
 */
public class FollowViewFragment extends LikesView{

    private int offset=0;
    @Override
    public Map<String, Object> getNextDataParams(PageData data) {
        //return super.getNextDataParams(data);
        return null;
    }


    @Override
    protected DataManager createDataManager() {
        return new FollowerDataManager(this, Application.getInstance().getCookies());
    }


    @Override
    public Class getLoadedDataParsingAwareClass() {
        return FollowerResult.class;
    }


    @Override
    public String getNextDataURL(PageData pageData) {
        return ApiKeys.getFollowerListAPI(offset);
    }


    @Override
    protected EmptyViewHelper createEmptyViewHelper() {
        EmptyViewHelper helper = new FollowerListEmptyViewHelper(this.getActivity(), this.createEmptyView(this.mRootView),
                this, this.showInternetError(), this.showInternetRetryButton());
        return helper;
    }


    public class FollowerDataManager extends DefaultProjectDataManager
    {
        public FollowerDataManager(IDataManagerDelegate delegate, List<Cookie> cookies)
        {
            super(getActivity(),delegate,cookies);
        }
        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }
        @Override
        protected void updatePagingData(BaseDataParser loadedDataVO)
        {
            try
            {
                if(loadedDataVO!=null && loadedDataVO instanceof FollowerResult)
                {
                    FollowerResult result = (FollowerResult) loadedDataVO;
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
            }catch (Exception e)
            {
                pageData.curruntPage=pageData.totalPage=1;
            }
        }
    }


    public static class FollowerResult extends CodeDataParser
    {
        public List<Liked> followers;
        public int offset;
        public List<?> getList()
        {
            return followers;
        }
        public Object getData()
        {
            return followers;
        }
        public void setData(Object data)
        {
            this.followers=(List<Liked>)data;
        }
    }
}
