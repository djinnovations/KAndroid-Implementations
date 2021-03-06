package com.goldadorn.main.modules.people;

import android.app.Activity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
//import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.model.People;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 2/25/16.
 */
abstract public class PeopleUpdateHelper {
    protected Gson gson;
    List<Cookie> cookies;
    UpdateResult postUpdateResult;
    int posision=-1;
    People people;
    boolean isCallInprogress= false;
    private AQuery aQuery;

    protected void serverCallEnds(int id,String url, Object json, AjaxStatus status)
    {
        if(postUpdateResult !=null)
        {
            if(isSucess(json,status))
            {
                updateSuccess(json.toString(),people,posision);
                postUpdateResult.onSuccess(this,people,posision);
            }
            else
            {
                updateFail(status,people, posision);
                postUpdateResult.onFail(this,people, posision);
            }

        }
    }

    abstract protected boolean isSucess(Object json, AjaxStatus status);


    protected void updateFail(AjaxStatus status,People people, int posision) {

    }

    protected void updateSuccess(String json,People people, int posision) {

    }

    protected AQuery getAQuery() {
        return aQuery;
    }

    public PeopleUpdateHelper(Activity activity, List<Cookie> cookies, UpdateResult postUpdateResult)
    {
        if(aQuery==null)
            aQuery = new AQuery(activity);
        this.cookies= cookies;
        this.postUpdateResult = postUpdateResult;
        gson= new Gson();
    }
    public void clear ()
    {
        cookies=null;
        postUpdateResult =null;
        aQuery=null;
        people=null;
        gson=null;
    }
    public interface UpdateResult
    {
        void onFail(PeopleUpdateHelper host, People post, int pos);
        void onSuccess(PeopleUpdateHelper host, People post, int pos);
    }
    public void update(People item,int pos)
    {
        if(!isCallInprogress)
        {
            people =item;
            posision = pos;
            String url = getURL();
            ExtendedAjaxCallback ajaxCallback =getAjaxCallback(pos);
            Map params=new HashMap<>();
            fillParams(item,params);
            ajaxCallback.setParams(params);
            ajaxCallback.setClazz(String.class);
            getAQuery().ajax(url, params, String.class, ajaxCallback);
            isCallInprogress=true;
        }
        else if(postUpdateResult !=null) {
            isCallInprogress=false;
            postUpdateResult.onFail(this,item, pos);
        }
    }
    protected abstract String getURL();

    protected abstract void fillParams(People item,Map params);


    protected ExtendedAjaxCallback getAjaxCallback(final int id) {
        ExtendedAjaxCallback<Object> ajaxCallback = new ExtendedAjaxCallback<Object>() {
            public void callback(String url, Object json, AjaxStatus status) {
                serverCallEnds(id,url,json,status);
                posision=-1;
                people=null;
                isCallInprogress=false;
            }
        };
        if(cookies!=null && cookies.size()!=0) {
            for (Cookie cookie : cookies) {
                ajaxCallback.cookie(cookie.getName(), cookie.getValue());
            }
        }
        return ajaxCallback;
    }
}
