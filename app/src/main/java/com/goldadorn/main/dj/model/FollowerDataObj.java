package com.goldadorn.main.dj.model;

import com.goldadorn.main.activities.Application;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

/**
 * Created by User on 28-07-2016.
 */
public class FollowerDataObj implements IParseableObject {

    private int userId;
    private String userName;
    private String profilePic;
    private boolean isSelf;

    @Override
    public void dataLoaded(BaseDataParser baseDataParser) {

        if(userId== Application.getLoginUser().id)
            isSelf=true;

        profilePic = URLHelper.parseImageURL(profilePic);
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

}
