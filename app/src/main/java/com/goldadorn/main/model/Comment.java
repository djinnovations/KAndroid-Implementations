package com.goldadorn.main.model;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bhavinpadhiyar on 2/27/16.
 */
public class Comment implements IParseableObject {
    private String userName;
    private String profilePic;
    private String commentText;
    private int userId;
    private Long timestamp;

    public String getRedableDate() {
        return redableDate;
    }

    public void setRedableDate(String redableDate) {
        this.redableDate = redableDate;
    }

    private String redableDate;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    private String age;

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    private boolean isSelf;
    DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm");
    public void dataLoaded(BaseDataParser entireData)
    {
        if(userId== Application.getLoginUser().getUserid())
            isSelf=true;

        profilePic = URLHelper.parseImageURL(profilePic);
        updateRedableDate(timestamp);

        age =strElapsed(timestamp);
    }

    public void updateRedableDate(Long timestamp) {
        redableDate = sdf.format(new Date(timestamp));
        redableDate = redableDate.replace(",",", at ");
                //+" "+strElapsed(timestamp)+" ago";
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getCommentText() {
        return commentText;
    }

    public int getUserId() {
        return userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String strElapsed(Long timestamp) {
        Long difference = new Date().getTime()-timestamp;
        long seconds;
        long minutes;
        long hours;
        long days;
        long x = difference / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        x /= 24;
        days = x;
        if(days!=0)
            return "d"+days+" h"+hours+" m"+minutes+" s"+seconds;
        else if(hours!=0)
            return " h"+hours+" m"+minutes+" s"+seconds;
        else if(minutes!=0)
            return " m"+minutes+" s"+seconds;
        else
            return " m0  s"+seconds;
    }
}
