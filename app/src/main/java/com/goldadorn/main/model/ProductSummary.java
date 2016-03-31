package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductSummary implements Serializable {
    public final int id;
    public String sizeUnit, weightUnits;
    public float size, height, width;
    public float weight;
    public int imageCount
    ArrayList<String> images = new ArrayList<>();
    public String productType;

    public ProductSummary(int id) {
        this.id = id;
    }


    public static ProductSummary extractFromJson(JSONObject obj) throws JSONException {
        ProductSummary p = new ProductSummary(obj.getInt(Constants.JsonConstants.PRODUCT_ID));
        p.height = (float) obj.getDouble("productHeight");
        p.width = (float) obj.getDouble("productWidth");
        p.size = (float) obj.getDouble("productSize");
        p.weight = (float) obj.getDouble("productWeight");
        p.sizeUnit = obj.getString("costUnits");
        p.weightUnits = obj.getString("widthUnits");
        p.imageCount = obj.getInt("productNumImages");
        p.productType = obj.getString("productType");
        return p
    }
}