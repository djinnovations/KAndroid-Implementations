package com.goldadorn.main.model;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

/**
 * Created by bhavinpadhiyar on 2/28/16.
 */
public class Liked implements IParseableObject {

    private int userId;
    private String userName;
    private String profilePic;

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    private boolean isSelf;

    public void dataLoaded(BaseDataParser entireData)
    {
        if(userId== Application.getLoginUser().id)
            isSelf=true;

        profilePic = URLHelper.parseImageURL(profilePic);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
