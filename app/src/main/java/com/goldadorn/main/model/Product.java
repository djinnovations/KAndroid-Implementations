package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.db.Tables;
import com.goldadorn.main.utils.ImageFilePath;

import java.io.Serializable;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Product implements Serializable {
    public final int id;
    public int userId, collectionId;
    public String name, description;
    public long unitPrice;
    public String priceUnit = " RS";
    public float image_a_r = 1;

    public int quantity, maxQuantity = 10;
    public int likecount;

    public Product(int id) {
        this.id = id;
    }

    public long getTotalPrice() {
        return unitPrice * quantity;
    }

    public String getDisplayPrice() {
        return priceUnit + ". " + unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Product)
            return ((Product) o).id == id;
        else return false;
    }

    public String getImageUrl() {
        return ImageFilePath.getImageUrlForProduct(id);
    }

    public static Product extractFromCursor(Cursor cursor) {
        Product t = new Product(cursor.getInt(cursor.getColumnIndex(Tables.Products._ID)));
        t.userId = cursor.getInt(cursor.getColumnIndex(Tables.Products.USER_ID));
        t.collectionId = cursor.getInt(cursor.getColumnIndex(Tables.Products.COLLECTION_ID));
        t.name = cursor.getString(cursor.getColumnIndex(Tables.Products.NAME));
        t.description = cursor.getString(cursor.getColumnIndex(Tables.Products.DESCRIPTION));
        t.likecount = cursor.getInt(cursor.getColumnIndex(Tables.Products.COUNT_LIKES));
        t.unitPrice = cursor.getLong(cursor.getColumnIndex(Tables.Products.PRICE));
        t.priceUnit = cursor.getString(cursor.getColumnIndex(Tables.Products.PRICEUNIT));
        t.image_a_r = cursor.getFloat(cursor.getColumnIndex(Tables.Products.IMAGE_ASPECT_RATIO));
        if (t.image_a_r == 0)
            t.image_a_r = 1;
        return t;
    }
}
