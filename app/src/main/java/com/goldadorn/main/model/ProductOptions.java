package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class ProductOptions {

    private final int id;
    ///customization variables
//    public float makingCharge;
//    public String makingChargeUnit;
//    public float primaryMetalPrice;
//    public String primaryMetalPriceUnits;
//    public float stonePrice;
//    public String stonePriceUnit;

    public String priceUnit;
    public final ArrayList<Map.Entry<String, Float>> priceBreakDown = new ArrayList<>();
    public final ArrayList<Map.Entry<String, ArrayList<Value>>> customisationOptions = new ArrayList<>();

    public ProductOptions(int id) {
        this.id = id;
    }

    private static void extract(JSONObject productInfo, String key, List<Map.Entry<String, ArrayList<Value>>> map) throws JSONException {
        if (productInfo.has(key)) {
            JSONArray ja = productInfo.getJSONArray(key);
            ArrayList<Value> options = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                options.add(new Value(key,ja.getString(i)));
            }
            map.add(new AbstractMap.SimpleEntry<>(key, options));
        }
    }

    public static ProductOptions extractCustomization(JSONObject productInfo) throws JSONException {
        ProductOptions p = new ProductOptions(productInfo.optInt(Constants.JsonConstants.PRODUCTID));

        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Making Charges", (float) productInfo.optDouble(Constants.JsonConstants.MAKINGCHARGES)));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Metal", (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPRICE)));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Stone", (float) productInfo.optDouble(Constants.JsonConstants.STONEPRICE)));

        p.priceUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);


//        p.makingCharge = (float) productInfo.optDouble(Constants.JsonConstants.MAKINGCHARGES);
//        p.makingChargeUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
//        p.primaryMetalPrice = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPRICE);
//        p.primaryMetalPriceUnits = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
//        p.stonePrice = (float) productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
//        p.stonePriceUnit = productInfo.optString(Constants.JsonConstants.STONEPRICEUNITS);

        extract(productInfo, Constants.JsonConstants.METALLIST, p.customisationOptions);
        extract(productInfo, Constants.JsonConstants.METALPURITYLIST, p.customisationOptions);
        extract(productInfo, Constants.JsonConstants.METALCOLORLIST, p.customisationOptions);
        extract(productInfo, Constants.JsonConstants.CENTERSTONE, p.customisationOptions);

        for (int i = 0; i < 11; i++) {
            String key = Constants.JsonConstants.ACCENTSTONE + i;
            extract(productInfo, key, p.customisationOptions);
        }
        for (int i = 0; i < 11; i++) {
            String key = Constants.JsonConstants.GEMSTONE + i;
            extract(productInfo, key, p.customisationOptions);
        }
        return p;
    }

}
