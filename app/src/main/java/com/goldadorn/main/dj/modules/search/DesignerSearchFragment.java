package com.goldadorn.main.dj.modules.search;

import android.content.Context;
import android.util.Log;

import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.model.People;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.server.Api;
import com.goldadorn.main.server.response.SearchResponse;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 24-10-2016.
 */
public class DesignerSearchFragment extends BaseSearchFragment {

    private static final String TAG = "DesignerSearchFragment";
    private int offset;

    @Override
    public String getNextDataURL(PageData data) {
        return ApiKeys.getSearchAPI(true);
    }

    @Override
    public String getRefreshDataURL(PageData pageData) {
        return getNextDataURL(null);
    }


    public void callLoad() {
        getDataManager().removeAll(getDataManager());
        loadRefreshData();
    }

    //"designer":"1","tag":"go","designerOffset":"0"
    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        params.put("designer", "1");
        params.put("tag", ((SearchActivity) getActivity()).getTag());
        params.put("designerOffset", String.valueOf(offset));
        Log.d(TAG, "searchdesignertab- req params: " + params);
        return params;
    }

    public Class getLoadedDataParsingAwareClass() {
        return DesignerResults.class;
    }

    @Override
    protected DataManager createDataManager() {
        return new DefaultProjectDataManager1(getActivity(), this, getApp().getCookies());
    }


    public class DefaultProjectDataManager1 extends com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager {
        public DefaultProjectDataManager1(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
            super(context, delegate, cookies);
        }

        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }

        @Override
        public Map<String, Object> getRefreshDataServerCallParams(PageData data) {
            offset = 0;
            data.curruntPage = 1;
            data.totalPage = 2;
            return getNextDataParams(data);
        }

        protected boolean isRefreshPage(PageData pageData, String url) {
            try {
                return false;
            } catch (Exception var4) {
                return false;
            }
        }

        @Override
        protected void updatePagingData(BaseDataParser loadedDataVO) {
            try {
                if (loadedDataVO != null && loadedDataVO instanceof DesignerResults) {
                    DesignerResults result = (DesignerResults) loadedDataVO;
                    if (result.designersOffset != -1 && offset != result.designersOffset) {
                        offset = result.designersOffset;
                        pageData.curruntPage += 1;
                        pageData.totalPage += 1;
                    } else {
                        pageData.totalPage = pageData.curruntPage;
                    }
                } else {
                    pageData.totalPage = pageData.curruntPage;
                }
            } catch (Exception e) {
                pageData.curruntPage = pageData.totalPage = 1;
            }
        }


       /* public int getPeoplePosition(People people) {
            int position = -1;

            for (int value = 0; value < this.size(); ++value) {
                if (this.get(value) == people) {
                    position = value;
                    break;
                }
            }

            return position;
        }*/
    }

    public static class DesignerResults extends CodeDataParser {
        private List<SearchDataObject.UserSearchData> designers;
        public int designersOffset;

        public List<?> getList() {
            return designers;
        }

        public Object getData() {
            return this;
        }

        public void setData(Object data) {
            this.designers = (List<SearchDataObject.UserSearchData>) data;
        }
    }

}
