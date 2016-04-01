package com.goldadorn.main.activities.post;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.activities.ServerFolderActivity;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.fragments.BaseFragment;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.DataManager;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.viewPager.fragmentPager.BaseHorizontalFragmentViewPager;
import com.kimeeo.library.listDataView.viewPager.viewPager.HorizontalViewPager;
import com.kimeeo.library.model.IFragmentData;
import com.nshmura.recyclertablayout.RecyclerTabLayout;
import com.rey.material.widget.ProgressView;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class ImageSelectorFragment extends BaseHorizontalFragmentViewPager implements DefaultProjectDataManager.IDataManagerDelegate
{
    ProgressView progressBar;
    protected View createRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.collection_fragment_page_view, container, false);
        View disableApp = rootView.findViewById(R.id.disableApp);
        disableApp.setVisibility(View.GONE);

        progressBar= (ProgressView)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0f);
        progressBar.start();

        View indicator =rootView.findViewById(R.id.indicator);
        View viewPager=rootView.findViewById(R.id.viewPager);
        indicator.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        return rootView;
    }

    public void onCallEnd(List<?> dataList, final boolean isRefreshData) {
        super.onCallEnd(dataList,isRefreshData);
        android.os.Handler handler = new android.os.Handler();
        final Runnable runnablelocal = new Runnable() {
            @Override
            public void run() {
                try {
                    //getRootView().findViewById(R.id.indicator).setVisibility(View.VISIBLE);

                    getRootView().findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }catch(Exception e)
                {

                }

            }
        };
        handler.postDelayed(runnablelocal, 1000);
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
        Map<String, Object> params = new HashMap<>();
        params.put(URLHelper.LIKE_A_POST.GALLERY, 0);
        params.put(URLHelper.LIKE_A_POST.PATH, null);
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

    public String getNextDataURL(PageData pageData)
    {
        if(pageData.curruntPage==1)
            return getApp().getUrlHelper().getFolderServiceURL();
        return null;
    }
    public Class getLoadedDataParsingAwareClass()
    {
        return FolderResult.class;
    }
    public String getPath()
    {
        return null;
    }
    public Fragment getItemFragment(int position,Object navigationObject)
    {
        if(navigationObject instanceof IFragmentData)
        {
            BaseFragment activePage = BaseFragment.newInstance((IFragmentData)navigationObject);
            return activePage;
        }
        return
                null;
    }
    @Override
    public String getItemTitle(int position,Object navigationObject)
    {
        if(navigationObject instanceof IFragmentData)
        {
            return ((IFragmentData)navigationObject).getName();
        }
        return super.getItemTitle(position,navigationObject);
    }
    public static class FolderResult extends CodeDataParser
    {
        List<ServerFolderObject> data;
        Object data1;
        public List<?> getList()
        {
            return data;
        }
        public Object getData()
        {
            return this;
        }
        public void setData(Object data)
        {
            this.data1=this;
        }
    }


    protected View createIndicator(View rootView)
    {
        View indicator = rootView.findViewById(R.id.indicator);
        if(getActivity() instanceof ServerFolderActivity && ((ServerFolderActivity)getActivity()).getPageIndicator()!=null)
        {
            if(indicator!=null)
                indicator.setVisibility(View.GONE);
            return ((ServerFolderActivity)getActivity()).getPageIndicator();
        }
        return indicator;
    }




    protected RecyclerTabLayout.Adapter<?> getRecyclerViewTabProvider(ViewPager viewPager) {
        return new TabIndicatorRecyclerViewAdapter(viewPager,getDataManager());
    }

    private void gotoData(Object data) {
        if(getDataManager()!=null && data!=null)
        {
            for (int i = 0; i < getDataManager().size(); i++) {
                if(getDataManager().get(i)==data)
                {
                    gotoItem(i,false);
                    break;
                }
            }
        }
    }

    public class TabIndicatorRecyclerViewAdapter extends com.kimeeo.library.listDataView.viewPager.TabIndicatorRecyclerViewAdapter {

        public TabIndicatorRecyclerViewAdapter(ViewPager viewPager, DataManager dataManager) {
            super(viewPager,dataManager);
        }
        protected  ViewHolder getViewHolder(View view)
        {
            return new MyViewHolder(view);
        }
        protected View getView(ViewGroup parent)
        {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_tab_view, parent, false);
        }
        public int getItemCount() {
            return getDataManager().size();
        }


        public class MyViewHolder extends ViewHolder {

            @Bind(R.id.textView)
            public TextView textView;

            @Bind(R.id.selected)
            public View selected;

            public MyViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        gotoData(data);
                    }
                });
                TypefaceHelper.setFont(itemView.getResources().getString(R.string.font_name_text_secondary), textView);
            }



            ServerFolderObject data;
            public void updatedNormalItem(Object o)
            {
                if(o instanceof ServerFolderObject) {
                    data = (ServerFolderObject) o;
                    textView.setText(data.getName());
                    textView.setTextColor(textView.getResources().getColor(R.color.colorPrimaryAlpha));
                    selected.setVisibility(View.GONE);
                }
            }
            public void updatedSelectedItem(Object o) {
                if(o instanceof ServerFolderObject) {
                    data = (ServerFolderObject) o;
                    textView.setText(data.getName());
                    textView.setTextColor(textView.getResources().getColor(R.color.colorPrimary));
                    selected.setVisibility(View.VISIBLE);
                }
            }
            public void hide() {
                itemView.setVisibility(View.GONE);
            }

            public void show() {
                itemView.setVisibility(View.VISIBLE);
            }


        }
    }
}
