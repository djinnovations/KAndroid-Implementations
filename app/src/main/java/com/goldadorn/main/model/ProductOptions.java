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
    public final ArrayList<Map.Entry<String, Float>> priceBreakDown = new ArrayList<>();
    public final ArrayList<Map.Entry<OptionKey, ArrayList<OptionValue>>> customisationOptions = new ArrayList<>();

    public ProductOptions(int id) {
        this.id = id;
    }

    private static void extract(JSONObject productInfo, OptionKey key, List<Map.Entry<OptionKey, ArrayList<OptionValue>>> map) throws JSONException {
        if (productInfo.has(key.keyName)) {
            JSONArray ja = productInfo.getJSONArray(key.keyName);
            ArrayList<OptionValue> options = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                options.add(new OptionValue(ja.getString(i)));
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
