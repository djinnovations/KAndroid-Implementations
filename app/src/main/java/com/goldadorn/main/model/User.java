package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.db.Tables.Users;

import java.io.Serializable;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class User implements Serializable {

    public static final int TYPE_INDIVIDUAL = 0;
    public static final int TYPE_DESIGNER = 0;
    public static final int TYPE_BRAND = 1;

    public final int id;
    public String name, imageUrl, description;
    public boolean featured, trending;
    public final int type;
    public int likes_cnt, followers_cnt, following_cnt, collections_cnt, products_cnt;
    public long dataVersion;
    public boolean isLiked,isFollowed;


    public User(int userid, int type) {
        this.id = userid;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof User)
            return ((User) o).id == id;
        else return false;
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

    public static User extractFromCursor(Cursor cursor) {
        User info = new User(cursor.getInt(cursor.getColumnIndex(Users._ID)), cursor.getInt(cursor.getColumnIndex(Users.TYPE)));
        info.name = cursor.getString(cursor.getColumnIndex(Users.NAME));
        info.description = cursor.getString(cursor.getColumnIndex(Users.DESCRIPTION));
        info.imageUrl = cursor.getString(cursor.getColumnIndex(Users.IMAGEURL));
        info.likes_cnt = cursor.getInt(cursor.getColumnIndex(Users.COUNT_LIKES));
        info.followers_cnt = cursor.getInt(cursor.getColumnIndex(Users.COUNT_FOLLOWERS));
        info.following_cnt = cursor.getInt(cursor.getColumnIndex(Users.COUNT_FOLLOWING));
        info.collections_cnt = cursor.getInt(cursor.getColumnIndex(Users.COUNT_COLLECTIONS));
        info.products_cnt = cursor.getInt(cursor.getColumnIndex(Users.COUNT_PRODUCTS));
        info.trending = cursor.getInt(cursor.getColumnIndex(Users.TRENDING)) == 1;
        info.featured = cursor.getInt(cursor.getColumnIndex(Users.FEATURED)) == 1;
        info.isLiked = cursor.getInt(cursor.getColumnIndex(Users.IS_LIKED)) == 1;
        info.isFollowed = cursor.getInt(cursor.getColumnIndex(Users.IS_FOLLOWING)) == 1;
        info.dataVersion = cursor.getLong(cursor.getColumnIndex(Users.DATAVERSION));

        return info;
    }

}
