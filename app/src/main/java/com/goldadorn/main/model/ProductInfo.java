package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class ProductInfo implements Serializable {
    public final int id;
    public String code;
    public String sizeUnit, weightUnits;
    public float size, height, width;
    public float weight;
    public int imageCount;
    public ArrayList<String> images = new ArrayList<>();
    public String productType;

    public ProductInfo(int id) {
        this.id = id;
    }


    public static ProductInfo extractFromJson(JSONObject obj) throws JSONException {
        ProductInfo p = new ProductInfo(obj.getInt(Constants.JsonConstants.PRODUCT_ID));
        p.height = (float) obj.getDouble("productHeight");
        p.width = (float) obj.getDouble("productWidth");
        p.size = (float) obj.getDouble("productSize");
        p.weight = (float) obj.getDouble("productWeight");
        p.sizeUnit = obj.getString("sizeUnits");
        p.weightUnits = obj.getString("widthUnits");
        p.imageCount = obj.getInt("productNumImages");
        p.productType = obj.getString("productType");
        p.code = obj.getString("productCode");
        return p;
    }

    public CharSequence getDisplayHeight(){
        return String.format(Locale.getDefault(),"%.2f %s",height,sizeUnit);
    }
    public CharSequence getDisplayWidth(){
        return String.format(Locale.getDefault(),"%.2f %s",width,sizeUnit);
    }
    public CharSequence getDisplayWeight(){
        return String.format(Locale.getDefault(),"%.2f %s",weight,weightUnits);
    }
}