package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.db.Tables;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Address {
    public final int id;
    public String name, street, city, state, country,phoneNumber;
    public int pincode;

    public Address(int id) {
        this.id = id;
    }

    public static Address extractFromCursor(Cursor cursor) {
        Address t = new Address(cursor.getInt(cursor.getColumnIndex(Tables.Addresses._ID)));
        t.name = cursor.getString(cursor.getColumnIndex(Tables.Addresses.NAME));
        t.street = cursor.getString(cursor.getColumnIndex(Tables.Addresses.STREET));
        t.city = cursor.getString(cursor.getColumnIndex(Tables.Addresses.CITY));
        t.state = cursor.getString(cursor.getColumnIndex(Tables.Addresses.STATE));
        t.country = cursor.getString(cursor.getColumnIndex(Tables.Addresses.COUNTRY));
        t.pincode = cursor.getInt(cursor.getColumnIndex(Tables.Addresses.PINCODE));
        t.phoneNumber = cursor.getString(cursor.getColumnIndex(Tables.Addresses.PHONENUMBER));
        return t;
    }
}
