package com.goldadorn.main.model;

import com.goldadorn.main.assist.BadgeHelper;

import java.util.List;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class User {

    public static final int TYPE_INDIVIDUAL = 0;
    public static final int TYPE_BRAND = 1;

    public final int id;
    private String name;
    private String imageUrl,badgesJson;
    public final int type;
    public int likes_cnt, followers_cnt, following_cnt, collections_cnt, products_cnt;


    public User(int userid, int type) {
        this.id = userid;
        this.type = type;
    }

    public List<Badge> getBadges() {
        return BadgeHelper.extractBadges(badgesJson);
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
