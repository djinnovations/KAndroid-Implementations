package com.goldadorn.main.model;

import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;

import java.net.HttpRetryException;

/**
 * Created by bhavinpadhiyar on 2/17/16.
 */
public class LoginResult extends ServerError implements IParseableObject {
    private int userid;
    private String username;
    private String userpic;
    private String userpicFinal=null;
    public void dataLoaded(BaseDataParser entireData)
    {
        userpic= URLHelper.parseImageURL(userpic);
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public void setUserpic(String userpic) {

        userpicFinal = URLHelper.parseImageURL(userpic);
        this.userpic = userpic;
    }

    public int getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }


    public String getUserpic() {
        if(userpicFinal==null)
            userpicFinal = URLHelper.parseImageURL(userpic);
        return userpicFinal;
    }

}
