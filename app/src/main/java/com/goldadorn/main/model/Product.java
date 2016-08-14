package com.goldadorn.main.model;

import android.database.Cursor;

import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.dj.model.ProductTemp;
import com.goldadorn.main.utils.ImageFilePath;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Product implements Serializable {
    public int id = -1;
    public int transid;
    public int productId = -1;
    public int userId = -1, collectionId = -1;
    public String name, description;
    public long unitPrice;
    public String priceUnit = " RS";
    public float image_a_r = 1;
    public String defMetal = "";

    public int quantity;
    public int maxQuantity = 10;
    public int likecount;

    public int toWriteLikeCount;

    @SerializedName("orderQty")
    public int orderQty;

    @SerializedName("pricePaid")
    public Double pricePaid;

    /*"primaryMetal":"Gold",
            "primaryMetalPurity":18,
            "primaryMetalColor":"Rose",*/


    public final HashMap<OptionKey, OptionValue> customisations = new HashMap<>();
    public boolean isLiked;
    public int likeStat;

    public Product(int id) {
        this.id = id;
    }

    public void addCustomisation(OptionKey key, OptionValue value) {
        if (value == null)
            customisations.remove(key);
        else
            customisations.put(key, value);
    }

    public long getTotalPrice() {
        return unitPrice * quantity;
    }

    public long getTotalPriceNew() {
        return (long) (pricePaid * orderQty);
    }

    public String getDisplayPrice() {
        if (/*priceUnit!=null &&*/ unitPrice > 0)
            return /*priceUnit + ". " +*/ String.valueOf(unitPrice);
        else
            return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Product)
            return ((Product) o).id == id;
        else return false;
    }

    public String getImageUrl(int desId, String defMetal, boolean isNotDefault) {
        return ImageFilePath.getImageUrlForProduct(desId, id, defMetal, isNotDefault);
    }


    public static Product extractFromProductTemp(ProductTemp productTemp, boolean isLiked, int likecount) {
        Product thisProduct = new Product(productTemp.getProductId());
        thisProduct.userId = productTemp.getUserId();
        thisProduct.productId = productTemp.getProductId();
        thisProduct.collectionId = productTemp.getCollectionId();
        thisProduct.name = productTemp.getProductName();
        thisProduct.description = productTemp.getProductDescription();
        thisProduct.likecount = likecount;
        thisProduct.unitPrice = productTemp.getProductDefaultPrice();
        thisProduct.priceUnit = productTemp.getCostUnits();
        thisProduct.isLiked = isLiked;
        thisProduct.image_a_r = productTemp.getAspectRatio();
        //thisProduct.defMetal = // TODO: 14-08-2016
        if (thisProduct.image_a_r == 0)
            thisProduct.image_a_r = 1;
        return thisProduct;
    }


    public static Product extractFromCursor(Cursor cursor) {
        Product t = new Product(cursor.getInt(cursor.getColumnIndex(Tables.Products._ID)));
        t.userId = cursor.getInt(cursor.getColumnIndex(Tables.Products.USER_ID));
        t.productId = cursor.getInt(cursor.getColumnIndex(Tables.Products._ID));
        t.collectionId = cursor.getInt(cursor.getColumnIndex(Tables.Products.COLLECTION_ID));
        t.name = cursor.getString(cursor.getColumnIndex(Tables.Products.NAME));
        t.description = cursor.getString(cursor.getColumnIndex(Tables.Products.DESCRIPTION));
        t.likecount = cursor.getInt(cursor.getColumnIndex(Tables.Products.COUNT_LIKES));
        t.unitPrice = cursor.getLong(cursor.getColumnIndex(Tables.Products.PRICE));
        t.priceUnit = cursor.getString(cursor.getColumnIndex(Tables.Products.PRICEUNIT));
        t.isLiked = cursor.getInt(cursor.getColumnIndex(Tables.Products.IS_LIKED)) == 1;
        t.likeStat = cursor.getInt(cursor.getColumnIndex(Tables.Products.IS_LIKED));
        //t.defMetal = cursor.getString(cursor.getColumnIndex(Tables.Products.DEF_METAl));
        t.image_a_r = cursor.getFloat(cursor.getColumnIndex(Tables.Products.IMAGE_ASPECT_RATIO));
        if (t.image_a_r == 0)
            t.image_a_r = 1;
        return t;
    }

    public static Product extractGetCartProductList(JSONObject productInfo) {
        Product p = new Product(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.name = productInfo.optString(Constants.JsonConstants.PRODUCTLABEL);
        p.transid = productInfo.optInt("transId");
        p.priceUnit = productInfo.optString(Constants.JsonConstants.PRODUCTPRICEUNITS);
        p.unitPrice = productInfo.optLong(Constants.JsonConstants.PRODUCTPRICE);
        p.quantity = 1;
        p.orderQty = productInfo.optInt("orderQty");
        p.pricePaid = productInfo.optDouble("pricePaid");

        // alternate customisation
        OptionValue v;
        v = new OptionValue(productInfo.optString(Constants.JsonConstants.PRIMARYMETAL));
        p.addCustomisation(new OptionKey(Constants.JsonConstants.PRIMARYMETAL), v);
        v = new OptionValue(productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR));
        p.addCustomisation(new OptionKey(Constants.JsonConstants.PRIMARYMETALCOLOR), v);
        v = new OptionValue(productInfo.optString(Constants.JsonConstants.PRIMARYMETALPURITY));
        p.addCustomisation(new OptionKey(Constants.JsonConstants.PRIMARYMETALPURITY), v);
        v = new OptionValue(productInfo.optString(Constants.JsonConstants.CENTERSTONE));
        p.addCustomisation(new OptionKey(Constants.JsonConstants.CENTERSTONE), v);
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                String key = Constants.JsonConstants.ACCENTSTONE + i;
                v = new OptionValue(productInfo.optString(key));
                p.addCustomisation(new OptionKey(key), v);
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                String key = Constants.JsonConstants.GEMSTONE + i;
                v = new OptionValue(productInfo.optString(key));
                p.addCustomisation(new OptionKey(key), v);
            }
        }
        return p;
    }

    public void addDefaultCustomisation(ProductOptions productOptions) {
        /*for (Map.Entry<OptionKey, ArrayList<OptionValue>> entry : productOptions.customisationOptions) {
            if (!entry.getKey().isOptional)
                addCustomisation(entry.getKey(), entry.getValue().get(0));
        }*/
    }
}
