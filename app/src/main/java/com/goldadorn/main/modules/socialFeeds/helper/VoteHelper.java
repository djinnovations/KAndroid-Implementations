package com.goldadorn.main.modules.socialFeeds.helper;

import android.app.Activity;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.utils.URLHelper;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 2/25/16.
 */
public class VoteHelper extends PostUpdateHelper
{
    public VoteHelper(Activity activity,List<Cookie> cookies,PostUpdateResult postUpdateResult)
    {
        super(activity,cookies, postUpdateResult);
    }
    protected void fillParams(SocialPost item,Map params)
    {
        params.put(URLHelper.LIKE_A_POST.POLL, post.getIsVoted());
        params.put(URLHelper.LIKE_A_POST.POST_ID, post.getPostId());
    }
    protected boolean isSucess(Object json, AjaxStatus status)
    {
        if(json!=null && json.toString().toLowerCase().indexOf("yes")!=-1)
            return true;
        return false;
    }
    protected String getURL()
    {
        return URLHelper.getInstance().getPollPostServiceURL();
    }
    protected void updateSuccess(String json,SocialPost post, int posision) {
        PollResult result = gson.fromJson((String)json, PollResult.class);
        post.setYesPercent(result.getYes());
        post.setNoPercent(result.getNo());
        post.setVoteCount(result.getTotal());
    }
    public class PollResult
    {

        private int Yes;
        private int No;
        private int Total;

        public void setYes(int Yes) {
            this.Yes = Yes;
        }

        public void setNo(int No) {
            this.No = No;
        }

        public void setTotal(int Total) {
            this.Total = Total;
        }

        public int getYes() {
            return Yes;
        }

        public int getNo() {
            return No;
        }

        public int getTotal() {
            return Total;
        }
    }
}
