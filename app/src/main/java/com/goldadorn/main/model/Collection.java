package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.db.Tables;

import java.io.Serializable;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Collection implements Serializable {
    public final int id;
    public String name, description, imageUrl;

    public Collection(int id) {
        this.id = id;
    }

    public static Collection extractFromCursor(Cursor cursor) {
        Collection t = new Collection(cursor.getInt(cursor.getColumnIndex(Tables.Collections._ID)));
        t.name = cursor.getString(cursor.getColumnIndex(Tables.Collections.NAME));
        t.description = cursor.getString(cursor.getColumnIndex(Tables.Collections.DESCRIPTION));
        t.imageUrl = cursor.getString(cursor.getColumnIndex(Tables.Collections.IMAGEURL));
        return t;
    }
}