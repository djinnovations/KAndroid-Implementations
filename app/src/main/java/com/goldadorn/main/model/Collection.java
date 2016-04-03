package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.db.Tables;

import java.io.Serializable;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Collection implements Serializable {
    public final int id;
    public int userId;
    public String name, description, category;
    public float image_a_r = 1;
    public int likecount = 0, productcount = 0;
    public boolean isTrending,isFeatured;
    public boolean isLiked;

    public Collection(int id) {
        this.id = id;
    }

    public static Collection extractFromCursor(Cursor cursor) {
        Collection t = new Collection(cursor.getInt(cursor.getColumnIndex(Tables.Collections._ID)));
        t.name = cursor.getString(cursor.getColumnIndex(Tables.Collections.NAME));
        t.description = cursor.getString(cursor.getColumnIndex(Tables.Collections.DESCRIPTION));
        t.userId = cursor.getInt(cursor.getColumnIndex(Tables.Collections.USER_ID));
        t.likecount = cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_LIKES));
        t.isLiked = cursor.getInt(cursor.getColumnIndex(Tables.Collections.IS_LIKED))==1;
        t.image_a_r = cursor.getFloat(cursor.getColumnIndex(Tables.Collections.IMAGE_ASPECT_RATIO));
        t.category = cursor.getString(cursor.getColumnIndex(Tables.Collections.CATEGORY));
        if (t.image_a_r == 0)
            t.image_a_r = 1;
        t.productcount = cursor.getInt(cursor.getColumnIndex(Tables.Collections.COUNT_PRODUCTS));
        return t;
    }

    public String getImageUrl() {
        return "http://demo.eremotus-portal.com/collections/3/3.jpg";
//        return ImageFilePath.getImageUrlForCollection(id);
    }
}
