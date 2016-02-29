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
public class SelectHelper extends PostUpdateHelper
{
    public SelectHelper(Activity activity,List<Cookie> cookies,PostUpdateResult postUpdateResult)
    {
        super(activity,cookies, postUpdateResult);
    }
    protected void fillParams(SocialPost item,Map params)
    {
        params.put(URLHelper.LIKE_A_POST.SELECT, item.getIsVoted());
        params.put(URLHelper.LIKE_A_POST.POST_ID, item.getPostId());
    }
    protected boolean isSucess(Object json, AjaxStatus status)
    {
        if(json!=null && json.toString().toLowerCase().indexOf("bof")!=-1)
            return true;
        return false;
    }
    protected String getURL()
    {
        return URLHelper.getInstance().getVoteBof3PostServiceURL();
    }
    protected void updateSuccess(String json,SocialPost post, int posision) {
        PollResult result = gson.fromJson(json, PollResult.class);
        post.setBof3Percent1(result.getBof1());
        post.setBof3Percent2(result.getBof2());
        post.setBof3Percent3(result.getBof3());
        post.setVoteCount(result.getTotal());
    }
    public class PollResult
    {


        private int Bof1;
        private int Bof2;
        private int Bof3;
        private int Total;

        public void setBof1(int Bof1) {
            this.Bof1 = Bof1;
        }

        public void setBof2(int Bof2) {
            this.Bof2 = Bof2;
        }

        public void setBof3(int Bof3) {
            this.Bof3 = Bof3;
        }

        public void setTotal(int Total) {
            this.Total = Total;
        }

        public int getBof1() {
            return Bof1;
        }

        public int getBof2() {
            return Bof2;
        }

        public int getBof3() {
            return Bof3;
        }

        public int getTotal() {
            return Total;
        }
    }
}