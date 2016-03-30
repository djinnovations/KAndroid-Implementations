package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class ProductDetail extends Product {

    public String code;
    public double type;
    public float price;
    public float aspectratio;
    public String widthUnit;
    public float weight;
    public String sizeUnit;
    public float height;
    public float width;
    public float size;

    ///customization variables
    public float makeingCharge;
    public String makeingChargeUnit;
    public String primaryMetal;
    public String primaryMetalColor;
    public float primaryMetalPurity;
    public float primaryMetalPrice;
    public String primaryMetalPriceUnits;
    public float stonePrice;
    public String stonePriceUnit;
    ArrayList<String> metalList;
    ArrayList<Integer> metalPurityList;
    ArrayList<Integer> metalColorList;
    ArrayList<String> centerStone;

    HashMap<String, ArrayList<String>> accentStones;
    HashMap<String, ArrayList<String>> gemStones;


    public String centerStoneSelected;
    public final HashMap<String, String> accentStoneSelected = new HashMap<>();
    public final HashMap<String, String> gemStoneSelected = new HashMap<>();

    public ProductDetail(int id) {
        super(id);
    }


    public static ProductDetail extractBasicInfo(JSONObject productInfo) {
        ProductDetail p = new ProductDetail(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.code = productInfo.optString(Constants.JsonConstants.PRODUCTCODE);
        p.name = productInfo.optString(Constants.JsonConstants.PRODUCTNAME);
        p.description = productInfo.optString(Constants.JsonConstants.DESCRIPTION);
        p.type = productInfo.optDouble(Constants.JsonConstants.TYPE);
        p.unitPrice = productInfo.optDouble(Constants.JsonConstants.COSTUNITS, -1f);
        p.aspectratio = (float) productInfo.optDouble(Constants.JsonConstants.ASPECTRATIO);
        p.widthUnit = productInfo.optString(Constants.JsonConstants.WIDTHUNITS);
        p.weight = (float) productInfo.optDouble(Constants.JsonConstants.WEIGHT);
        p.sizeUnit = productInfo.optString(Constants.JsonConstants.SIZEUNITS);
        p.height = (float) productInfo.optDouble(Constants.JsonConstants.HEIGHT);
        p.width = (float) productInfo.optDouble(Constants.JsonConstants.WIDTH);
        p.size = (float) productInfo.optDouble(Constants.JsonConstants.SIZE);
        return p;
    }

    public static ProductDetail extractGetCartProductList(JSONObject productInfo) {
        ProductDetail p = new ProductDetail(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.name = productInfo.optString(Constants.JsonConstants.PRODUCTLABEL);
        p.primaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        p.primaryMetalColor = productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR);
        p.primaryMetalPurity = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPURITY);
        p.centerStoneSelected = productInfo.optString(Constants.JsonConstants.CENTERSTONE);
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                p.accentStoneSelected.put(Constants.JsonConstants.ACCENTSTONE + i, productInfo.optString(Constants.JsonConstants.ACCENTSTONE + i));
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                p.gemStoneSelected.put(Constants.JsonConstants.GEMSTONE + i, productInfo.optString(Constants.JsonConstants.GEMSTONE + i));
            }
        }
        p.price = (float) productInfo.optDouble(Constants.JsonConstants.PRODUCTPRICE);
        p.size = productInfo.optInt(Constants.JsonConstants.PRODUCTSIZE);
        return p;
    }

    public static ProductDetail extractCustomization(JSONObject productInfo) throws JSONException {
        ProductDetail p = new ProductDetail(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.makeingCharge = (float) productInfo.optDouble(Constants.JsonConstants.MAKINGCHARGES);
        p.makeingChargeUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
        p.primaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        p.primaryMetalColor = productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR);
        p.primaryMetalPurity = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPURITY);
        p.primaryMetalPrice = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPRICE);
        p.primaryMetalPriceUnits = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
        p.stonePrice = (float) productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
        p.stonePriceUnit = productInfo.optString(Constants.JsonConstants.STONEPRICEUNITS);
        if (productInfo.has(Constants.JsonConstants.METALLIST)) {
            p.metalList = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALLIST).length(); i++) {
                p.metalList.add(productInfo.getJSONArray(Constants.JsonConstants.METALLIST).getString(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.METALPURITYLIST)) {
            p.metalPurityList = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALPURITYLIST).length(); i++) {
                p.metalPurityList.add(productInfo.getJSONArray(Constants.JsonConstants.METALPURITYLIST).getInt(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.METALCOLORLIST)) {
            p.metalColorList = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALCOLORLIST).length(); i++) {
                p.metalColorList.add(productInfo.getJSONArray(Constants.JsonConstants.METALCOLORLIST).getInt(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.CENTERSTONE)) {
            p.centerStone = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.CENTERSTONE).length(); i++) {
                p.centerStone.add(productInfo.getJSONArray(Constants.JsonConstants.CENTERSTONE).getString(i));
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                ArrayList<String> accentStonelist = new ArrayList<>();
                for (int j = 0; i < productInfo.getJSONArray(Constants.JsonConstants.ACCENTSTONE + i).length(); j++) {
                    accentStonelist.add(productInfo.getJSONArray(Constants.JsonConstants.ACCENTSTONE + i).getString(j));
                }
                p.accentStones.put(Constants.JsonConstants.ACCENTSTONE + i, accentStonelist);
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                ArrayList<String> gemstonelist = new ArrayList<>();
                for (int j = 0; i < productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE + i).length(); j++) {
                    gemstonelist.add(productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE + i).getString(j));
                }
                p.gemStones.put(Constants.JsonConstants.GEMSTONE + i, gemstonelist);
            }
        }
        return p;

    }
}
