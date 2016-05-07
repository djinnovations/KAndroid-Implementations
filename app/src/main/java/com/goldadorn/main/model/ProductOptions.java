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
    public String priceUnit;
    public Double size=0.0;
    public int primaryMetalPurity=-1;
    public String primaryMetal="";
    public String primaryMetalColor="";
    public String priceUnits="";
    public Double stonePrice=0.0;
    public final ArrayList<Map.Entry<String, Float>> priceBreakDown = new ArrayList<>();
    public final ArrayList<Map.Entry<OptionKey, ArrayList<OptionValue>>> customisationOptions = new ArrayList<>();

    public ProductOptions(int id) {
        this.id = id;
    }

    private static void extract(JSONObject productInfo, OptionKey key, List<Map.Entry<OptionKey, ArrayList<OptionValue>>> map) throws JSONException {
        if (productInfo.has(key.keyID)) {
            JSONArray ja = productInfo.getJSONArray(key.keyID);
            ArrayList<OptionValue> options = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                options.add(new OptionValue(ja.getString(i)));
            }
            map.add(new AbstractMap.SimpleEntry<>(key, options));
        }
    }

    public static ProductOptions extractCustomization(JSONObject productInfo) throws JSONException {
        ProductOptions p = new ProductOptions(productInfo.optInt(Constants.JsonConstants.PRODUCTID));

        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Metal", (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPRICE)));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Stone", (float) productInfo.optDouble(Constants.JsonConstants.STONEPRICE)));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Making Charges", (float) productInfo.optDouble(Constants.JsonConstants.MAKINGCHARGES)));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("VAT (Tax)", (float) 00.00));

        p.priceUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
        p.primaryMetalPurity=productInfo.optInt(Constants.JsonConstants.METALPURITY);
        p.primaryMetal=productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        p.primaryMetalColor=productInfo.optString(Constants.JsonConstants.METALCOLOUR);
        p.priceUnits=productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
        p.size=productInfo.optDouble(Constants.JsonConstants.SIZE);
        p.stonePrice=productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
        extract(productInfo, new OptionKey(Constants.JsonConstants.METALLIST, false), p.customisationOptions);
        extract(productInfo, new OptionKey(Constants.JsonConstants.METALPURITYLIST, true), p.customisationOptions);
        extract(productInfo, new OptionKey(Constants.JsonConstants.METALCOLORLIST, true), p.customisationOptions);
        extract(productInfo, new OptionKey(Constants.JsonConstants.CENTERSTONE, false), p.customisationOptions);

        for (int i = 0; i < 11; i++) {
            OptionKey key = new OptionKey(Constants.JsonConstants.ACCENTSTONE + i, true);
            extract(productInfo, key, p.customisationOptions);
        }
        for (int i = 0; i < 11; i++) {
            OptionKey key = new OptionKey(Constants.JsonConstants.GEMSTONE + i, true);
            extract(productInfo, key, p.customisationOptions);
        }
        return p;
    }

}
