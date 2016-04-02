package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.utils.ImageFilePath;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

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


    //customise variable
    public final HashMap<String, String> customisations = new HashMap<>();
//    public String primaryMetal;
//    public String primaryMetalColor;
//    public String primaryMetalPurity;
//    public String centerStoneSelected;
//    public final HashMap<String, String> accentStoneSelected = new HashMap<>();
//    public final HashMap<String, String> gemStoneSelected = new HashMap<>();

    public Product(int id) {
        this.id = id;
    }

    public void addCustomisation(String key, String value) {
        if (value == null)
            customisations.remove(key);
        else
            customisations.put(key, value);
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

    public static Product extractGetCartProductList(JSONObject productInfo) {
        Product p = new Product(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.name = productInfo.optString(Constants.JsonConstants.PRODUCTLABEL);
//        p.primaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
//        p.primaryMetalColor = productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR);
//        p.primaryMetalPurity = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPURITY);
//        p.centerStoneSelected = productInfo.optString(Constants.JsonConstants.CENTERSTONE);
//        for (int i = 0; i < 11; i++) {
//            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
//                p.accentStoneSelected.put(Constants.JsonConstants.ACCENTSTONE + i, productInfo.optString(Constants.JsonConstants.ACCENTSTONE + i));
//            }
//        }
//        for (int i = 0; i < 11; i++) {
//            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
//                p.gemStoneSelected.put(Constants.JsonConstants.GEMSTONE + i, productInfo.optString(Constants.JsonConstants.GEMSTONE + i));
//            }
//        }
        p.priceUnit = productInfo.optString(Constants.JsonConstants.PRODUCTPRICEUNITS);
        p.unitPrice = productInfo.optLong(Constants.JsonConstants.PRODUCTPRICE);
        p.quantity = 1;

        // alternate customisation
        p.addCustomisation(Constants.JsonConstants.PRIMARYMETAL, productInfo.optString(Constants.JsonConstants.PRIMARYMETAL));
        p.addCustomisation(Constants.JsonConstants.PRIMARYMETALCOLOR, productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR));
        p.addCustomisation(Constants.JsonConstants.PRIMARYMETALPURITY, productInfo.optString(Constants.JsonConstants.PRIMARYMETALPURITY));
        p.addCustomisation(Constants.JsonConstants.CENTERSTONE, productInfo.optString(Constants.JsonConstants.CENTERSTONE));
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                String key = Constants.JsonConstants.ACCENTSTONE + i;
                p.addCustomisation(key, productInfo.optString(key));
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                String key = Constants.JsonConstants.GEMSTONE + i;
                p.addCustomisation(key, productInfo.optString(key));
            }
        }
        return p;
    }
}
