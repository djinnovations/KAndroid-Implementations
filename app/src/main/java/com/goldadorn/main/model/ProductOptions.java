package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class ProductOptions {

    private final int id;
    ///customization variables
    public float makingCharge;
    public String makingChargeUnit;
    public float primaryMetalPrice;
    public String primaryMetalPriceUnits;
    public float stonePrice;
    public String stonePriceUnit;

    private ArrayList<String> metalList = new ArrayList<>();
    private ArrayList<String> metalPurityList = new ArrayList<>();
    private ArrayList<String> metalColorList = new ArrayList<>();
    private ArrayList<String> centerStone = new ArrayList<>();
    private HashMap<String, ArrayList<String>> accentStones = new HashMap<>();
    private HashMap<String, ArrayList<String>> gemStones = new HashMap<>();

    public ProductOptions(int id) {
        this.id = id;
    }


    public static ProductOptions extractCustomization(JSONObject productInfo) throws JSONException {
        ProductOptions p = new ProductOptions(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.makingCharge = (float) productInfo.optDouble(Constants.JsonConstants.MAKINGCHARGES);
        p.makingChargeUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
        p.primaryMetalPrice = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPRICE);
        p.primaryMetalPriceUnits = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
        p.stonePrice = (float) productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
        p.stonePriceUnit = productInfo.optString(Constants.JsonConstants.STONEPRICEUNITS);
        if (productInfo.has(Constants.JsonConstants.METALLIST)) {
            JSONArray ja = productInfo.getJSONArray(Constants.JsonConstants.METALLIST);
            for (int i = 0; i < ja.length(); i++) {
                p.metalList.add(ja.getString(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.METALPURITYLIST)) {
            JSONArray ja = productInfo.getJSONArray(Constants.JsonConstants.METALLIST);
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALPURITYLIST).length(); i++) {
                p.metalPurityList.add(productInfo.getJSONArray(Constants.JsonConstants.METALPURITYLIST).getString(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.METALCOLORLIST)) {
            JSONArray ja = productInfo.getJSONArray(Constants.JsonConstants.METALCOLORLIST);
            for (int i = 0; i < ja.length(); i++) {
                p.metalColorList.add(ja.getString(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.CENTERSTONE)) {
            JSONArray ja = productInfo.getJSONArray(Constants.JsonConstants.CENTERSTONE);
            for (int i = 0; i < ja.length(); i++) {
                p.centerStone.add(ja.getString(i));
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                JSONArray ja = productInfo.getJSONArray(Constants.JsonConstants.ACCENTSTONE);
                ArrayList<String> accentStonelist = new ArrayList<>();
                for (int j = 0; i < ja.length(); j++) {
                    accentStonelist.add(ja.getString(j));
                }
                p.accentStones.put(Constants.JsonConstants.ACCENTSTONE + i, accentStonelist);
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                JSONArray ja = productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE);
                ArrayList<String> gemstonelist = new ArrayList<>();
                for (int j = 0; i < ja.length(); j++) {
                    gemstonelist.add(ja.getString(j));
                }
                p.gemStones.put(Constants.JsonConstants.GEMSTONE + i, gemstonelist);
            }
        }
        return p;
    }

    public List<Map.Entry<String, ArrayList<String>>> getOptionsList() {
        ArrayList<Map.Entry<String, ArrayList<String>>> list = new ArrayList<>();
        if (metalList != null && metalList.size() > 0)
            list.add(new AbstractMap.SimpleEntry<>("metalList", metalList));
        if (metalPurityList != null && metalPurityList.size() > 0)
            list.add(new AbstractMap.SimpleEntry<>("metalPurityList", metalPurityList));
        if (metalColorList != null && metalColorList.size() > 0)
            list.add(new AbstractMap.SimpleEntry<>("metalColorList", metalColorList));
        if (centerStone != null && centerStone.size() > 0)
            list.add(new AbstractMap.SimpleEntry<>("centerStone", centerStone));
        list.addAll(accentStones.entrySet());
        list.addAll(gemStones.entrySet());
        return list;

    }

}
