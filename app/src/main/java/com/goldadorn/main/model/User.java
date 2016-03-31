package com.goldadorn.main.model;

import java.io.Serializable;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class User implements Serializable{

    public static final int TYPE_INDIVIDUAL = 0;
    public static final int TYPE_DESIGNER = 0;
    public static final int TYPE_BRAND = 1;

    public final int id;
    public String name,imageUrl, description;
    public boolean featured,trending;
    public final int type;
    public int likes_cnt, followers_cnt, following_cnt, collections_cnt, products_cnt;
    public long dataVersion;


    public User(int userid, int type) {
        this.id = userid;
        this.type = type;
    }

    public void setName(String username) {
        this.name = username;
    }

    public void setImageUrl(String userpic) {
        this.imageUrl = userpic;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
