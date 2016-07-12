package com.goldadorn.main.model;

import android.util.Log;

import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.dj.model.Swatches;

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
    public Double size = 0.0;
    public int primaryMetalPurity = -1;
    public String primaryMetal = "";
    public String primaryMetalColor = "";
    public String priceUnits = "";
    public Double stonePrice = 0.0;
    public final ArrayList<Map.Entry<String, Float>> priceBreakDown = new ArrayList<>();
    //public final ArrayList<Map.Entry<OptionKey, ArrayList<OptionValue>>> customisationOptions = new ArrayList<>();
    public final ArrayList<Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>>> customisationOptions = new ArrayList<>();

    public List<String> sizeList;

    public ProductOptions(int id) {
        this.id = id;
    }

    private static void extract(JSONObject productInfo, OptionKey key, List<Map.Entry<OptionKey,
            ArrayList<OptionValue>>> map) throws JSONException {
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

        Log.d("djcustom", "response Obj - ProductOptions: " + productInfo.toString());
        float primaryMetalPrice;
        float stonePrice;
        float makingcharges;
        try {
            primaryMetalPrice = (float) productInfo.getDouble(Constants.JsonConstants.PRIMARYMETALPRICE);
            stonePrice = (float) productInfo.getDouble(Constants.JsonConstants.STONEPRICE);
            makingcharges = (float) productInfo.getDouble(Constants.JsonConstants.MAKINGCHARGES);
        } catch (JSONException e) {
            e.printStackTrace();
            primaryMetalPrice = -1;
            stonePrice = -1;
            makingcharges = -1;
        }

        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Metal", primaryMetalPrice));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Stone", stonePrice));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Making Charges", makingcharges));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("VAT (Tax)", (float) 00.00));

        p.priceUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
        p.primaryMetalPurity = productInfo.optInt(Constants.JsonConstants.METALPURITY);
        p.primaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        p.primaryMetalColor = productInfo.optString(Constants.JsonConstants.METALCOLOUR);
        p.priceUnits = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
        p.size = productInfo.optDouble(Constants.JsonConstants.SIZE);
        p.stonePrice = productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
        //DJphy
        ArrayList<Swatches.MixedSwatch> metalSwatch = extractSwatch(productInfo, Swatches.TYPE_METAL);
        if (metalSwatch.size() != 0)
            p.customisationOptions.add(new AbstractMap.SimpleEntry<>(new OptionKey("Metal", false)
                    , metalSwatch));
        ArrayList<Swatches.MixedSwatch> gemStoneSwatch = extractSwatch(productInfo, Swatches.TYPE_GEMSTONE);
        if (gemStoneSwatch.size() != 0)
            p.customisationOptions.add(new AbstractMap.SimpleEntry<>(new OptionKey("Diamond Quality", false)
                    , gemStoneSwatch));

        // p.allSwatches = new AllSwatches(extractMetalSwatch(productInfo), extractGemStoneSwatch(productInfo));
       /* extract(productInfo, new OptionKey(Constants.JsonConstants.METALLIST, false), p.customisationOptions);
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
        }*/
        return p;
    }

    private void addToSwatchMap() {

    }

    private static List<Swatches.GemStoneSwatch> extractGemStoneSwatch(JSONObject productInfo) {

        return null; //TODO
    }

    private static ArrayList<Swatches.MixedSwatch> extractSwatch(JSONObject productInfo, int type) {
        JSONArray jsonArrayCust = new JSONArray();
        ArrayList<Swatches.MixedSwatch> swatchList = new ArrayList<>();
        try {
            if (type == Swatches.TYPE_METAL) {
                if (!productInfo.isNull("MetalCust"))
                    jsonArrayCust = productInfo.getJSONArray("MetalCust");
            } else if (type == Swatches.TYPE_GEMSTONE) {
                if (!productInfo.isNull("StoneCust"))
                    jsonArrayCust = productInfo.getJSONArray("StoneCust");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArrayCust.length(); i++) {
            Swatches.MixedSwatch metalSwatch = null;
            try {
                if (type == Swatches.TYPE_METAL)
                    metalSwatch = Swatches.getMixedSwatch(jsonArrayCust.getString(i), type);
                else if (type == Swatches.TYPE_GEMSTONE)
                    metalSwatch = Swatches.getMixedSwatch(addDummyVarToGemStoneCust(jsonArrayCust.getString(i)), type);
                swatchList.add(metalSwatch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return swatchList;
    }


    private static String addDummyVarToGemStoneCust(String fromServer) {

        String[] brokenStuff = fromServer.split(":");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String string : brokenStuff) {
            if (i == 3)
                sb.append(-1).append(":").append(string).append(":");
            else sb.append(string).append(":");
            i++;
        }
        String finalString = sb.deleteCharAt(sb.length() - 1).toString();
        Log.d("djprod", "after adding dummy val for clarity units - Gemstone: " + finalString);
        return finalString;
    }

}
