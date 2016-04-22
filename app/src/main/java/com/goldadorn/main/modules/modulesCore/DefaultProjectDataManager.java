package com.goldadorn.main.modules.modulesCore;

import android.content.Context;

import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.dataManagers.aQuery.DefaultJSONDataManager;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 1/11/16.
 */
public class DefaultProjectDataManager extends DefaultJSONDataManager
{
    private IDataManagerDelegate delegate;
    public DefaultProjectDataManager(Context context, IDataManagerDelegate delegate,List<Cookie> cookies)
    {
        super(context);
        //setCachingTime(15 * 60 * 1000);
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
    public void dataHandler(String url, Object value, Object status) {
        if(value!=null && value instanceof String)
        {
            String stringValue = value.toString();
            stringValue = stringValue.trim();
            //stringValue = stringValue.replace(",\"msg\":null","");


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