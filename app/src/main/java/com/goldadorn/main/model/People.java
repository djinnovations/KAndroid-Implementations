package com.goldadorn.main.model;

import android.text.TextUtils;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class People extends ServerError implements IParseableObject {


    private String username;
    private String lastname;
    private String backgroundPic;

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    private boolean isSelf;
    private int userId;
    private String userName;
    private int isFollowing;
    private String profilePic;
    private int followerCount;
    private int followingCount;
    private int isDesigner;

    public void dataLoaded(BaseDataParser entireData)
    {
        if (!TextUtils.isEmpty(profilePic)) {
            profilePic = URLHelper.parseImageURL(profilePic);
        }
        backgroundPic= URLHelper.parseImageURL(backgroundPic);
        //backgroundPic  = "http://store.messness.com/wp-content/uploads/sites/2/2013/04/pocket-square-floral-pattern2-1.jpg";
        if(userId== Application.getLoginUser().id)
            isSelf=true;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setIsFollowing(int isFollowing) {
        this.isFollowing = isFollowing;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public void setIsDesigner(int isDesigner) {
        this.isDesigner = isDesigner;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getIsFollowing() {
        return isFollowing;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getIsDesigner() {
        return isDesigner;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setBackgroundPic(String backgroundPic) {
        this.backgroundPic = backgroundPic;
    }

    public String getUsername() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getBackgroundPic() {
        return backgroundPic;
    }
}
