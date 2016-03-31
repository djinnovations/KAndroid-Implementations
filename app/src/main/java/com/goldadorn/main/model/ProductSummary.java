package com.goldadorn.main.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductSummary implements Serializable {
    public final int id;
    public String sizeUnit,weightUnits;
    public float size, height, width;
    public float weight;
    ArrayList<String> images = new ArrayList<>();
    public String productType;

    public ProductSummary(int id) {
        this.id = id;
    }


//    public static ProductSummary extractFromJson(JSONObject obj) {
//        ProductSummary p= new ProductSummary()
//    }
}