package com.goldadorn.main.modules.people;

import android.app.Activity;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.model.People;
import com.goldadorn.main.utils.URLHelper;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 2/27/16.
 */
public class FollowPeopleHelper extends PeopleUpdateHelper{
    public FollowPeopleHelper(Activity activity,List<Cookie> cookies,UpdateResult postUpdateResult)
    {
        super(activity, cookies, postUpdateResult);
    }
    protected void fillParams(People item,Map params)
    {
        params.put(URLHelper.LIKE_A_POST.FOLLOW, item.getUserId());
    }
    protected boolean isSucess(Object json, AjaxStatus status)
    {
        if(json!=null && json.toString().toLowerCase().indexOf("success")!=-1)
            return true;
        return false;
    }
    protected String getURL()
    {
        return URLHelper.getInstance().getFollowPeopleServiceURL();
    }
}
