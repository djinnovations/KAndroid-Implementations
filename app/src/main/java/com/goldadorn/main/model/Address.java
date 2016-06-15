package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.db.Tables;

import java.io.Serializable;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Address implements Serializable{
    public int id;
    public String name, street, city, state, country,phoneNumber;
    public String pincode;

    public Address(int id) {
        this.id = id;
    }

    public Address(String name, String street, String city, String state, String country, String phoneNumber, String pincode) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.pincode = pincode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Address extractFromCursor(Cursor cursor) {
        Address t = new Address(cursor.getInt(cursor.getColumnIndex(Tables.Addresses._ID)));
        t.name = cursor.getString(cursor.getColumnIndex(Tables.Addresses.NAME));
        t.street = cursor.getString(cursor.getColumnIndex(Tables.Addresses.STREET));
        t.city = cursor.getString(cursor.getColumnIndex(Tables.Addresses.CITY));
        t.state = cursor.getString(cursor.getColumnIndex(Tables.Addresses.STATE));
        t.country = cursor.getString(cursor.getColumnIndex(Tables.Addresses.COUNTRY));
        t.pincode = String.valueOf(cursor.getInt(cursor.getColumnIndex(Tables.Addresses.PINCODE)));
        t.phoneNumber = cursor.getString(cursor.getColumnIndex(Tables.Addresses.PHONENUMBER));
        return t;
    }
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Address)
            return ((Address) o).id == id;
        else return false;
    }
}
