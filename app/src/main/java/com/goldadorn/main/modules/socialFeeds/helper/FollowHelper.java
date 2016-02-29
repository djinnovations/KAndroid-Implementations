package com.goldadorn.main.modules.socialFeeds.helper;

import android.app.Activity;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.utils.URLHelper;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 2/25/16.
 */
public class FollowHelper extends PostUpdateHelper
{
    public FollowHelper(Activity activity, List<Cookie> cookies, PostUpdateResult postUpdateResult)
    {
        super(activity, cookies, postUpdateResult);
    }
    protected void fillParams(SocialPost item,Map params)
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
