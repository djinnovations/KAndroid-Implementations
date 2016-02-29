package com.goldadorn.main.model;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class User {

    private int userid;
    private String username;
    private String userpic;


    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public int getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public String getUserpic() {
        return userpic;
    }
}
