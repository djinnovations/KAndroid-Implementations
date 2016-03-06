package com.goldadorn.main.model;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class User {

    public static final int TYPE_INDIVIDUAL=0;
    public static final int TYPE_BRAND=1;

    public final int id;
    private String name;
    private String image;
    public final int type;
    public int likes_cnt, followers_cnt, following_cnt, collections_cnt, products_cnt;


    public User(int userid,int type) {
        this.id = userid;
        this.type=type;
    }


    public void setUsername(String username) {
        this.name = username;
    }

    public void setUserpic(String userpic) {
        this.image = userpic;
    }

    public String getUsername() {
        return name;
    }

    public String getUserpic() {
        return image;
    }
}
