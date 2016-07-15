package com.goldadorn.main.modules.modulesCore;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.dataManagers.aQuery.DefaultJSONDataManager;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 1/11/16.
 */
public class DefaultProjectDataManager extends DefaultJSONDataManager
{

    public interface NotificationCountChangeListener{

        void onNotificationCountChanged(String count);
    }


    private IDataManagerDelegate delegate;
    Context context;
    public DefaultProjectDataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
    {
        super(context);
        //setCachingTime(15 * 60 * 1000);
        this.context = context;
        setCachingTime(-1);
        this.delegate = delegate;
        setRefreshEnabled(true);
        setCookies(cookies);
    }
    protected String getRefreshDataURL(PageData pageData){return delegate.getRefreshDataURL(pageData);}
    protected String getNextDataURL(PageData pageData)
    {
        return delegate.getNextDataURL(pageData);
    }
    public Class getLoadedDataParsingAwareClass()
    {
        return delegate.getLoadedDataParsingAwareClass();
    }
    public static interface IDataManagerDelegate
    {
        String getNextDataURL(PageData pageData);
        String getRefreshDataURL(PageData pageData);
        Class getLoadedDataParsingAwareClass();
    }

    private String notifyCount;
    public void dataHandler(String url, Object value, Object status) {
        Log.d(Constants.TAG, "social feed url: "+url);
        if(value!=null && value instanceof String)
        {
            String stringValue = value.toString();
            stringValue = stringValue.trim();

            Log.d(Constants.TAG, "social feed: "+stringValue);
            // stringValue = stringValue.replace(",\"msg\":null","");
            try {
                JSONObject jsonObject = new JSONObject(stringValue);
                notifyCount = jsonObject.isNull("noticount") ? null : jsonObject.getString("noticount");
                RandomUtils.setUnreadCount(notifyCount);
                Log.d(Constants.TAG, "notificationCount: "+notifyCount);
                if (notifyCount != null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (notifyCount.length() > 1){
                                //BaseDrawerActivity.displayUnreadData(context, "9+");
                                if (context instanceof BaseDrawerActivity){
                                    ((BaseDrawerActivity) context).onNotificationCountChanged("9+");
                                    return;
                                }
                            }
                            else {
                                //BaseDrawerActivity.displayUnreadData(context, notifyCount);
                                if (context instanceof BaseDrawerActivity){
                                    ((BaseDrawerActivity) context).onNotificationCountChanged(notifyCount);
                                    return;
                                }
                            }
                        }
                    }, 500);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(stringValue.indexOf("[")==0) {
                stringValue = "{"+'"'+"data"+'"'+":"+stringValue+"}";
                super.dataHandler(url, stringValue, status);
            }
            else if(stringValue.indexOf("[[{")!=-1) {
                stringValue = stringValue.replace("[[{","[{");
                stringValue = stringValue.replace("}]]","}]");
                super.dataHandler(url, stringValue, status);
            }
            else
                super.dataHandler(url,stringValue,status);
        }
        else
            super.dataHandler(url,value,status);
    }
    protected boolean isRefreshPage(PageData pageData, String url) {
        try {
            return this.getRefreshDataURL(pageData).equals(url);
        } catch (Exception var4) {
            return false;
        }
    }
    protected void updatePagingData(BaseDataParser loadedDataVO)
    {
        try
        {
            pageData.curruntPage +=1;
            pageData.totalPage +=1;
        }catch (Exception e)
        {
            pageData.curruntPage=pageData.totalPage=1;
        }
    }

}