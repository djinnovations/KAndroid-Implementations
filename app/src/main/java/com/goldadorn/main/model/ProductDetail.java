package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class ProductDetail extends Product{

    public String code;
    public double type;
    public float price;
    public String costunit;
    public float aspectratio;
    public String widthUnit;
    public float weight;
    public String sizeUnit;
    public float height;
    public float width;
    public float size;

    ///customization variables
    public float mMakeingCharge;
    public String mMakeingChargeUnit;
    public String mPrimaryMetal;
    public String mPrimaryMetalColor;
    public float mPrimaryMetalPurity;
    public float mPrimaryMetalPrice;
    public String mPrimaryMetalPriceUnits;
    public float mStonePrice;
    public String mStonePriceUnit;
    ArrayList<String> mMetalList;
    ArrayList<Integer> mMetalPurityList;
    ArrayList<Integer> mMetalColorList;
    ArrayList<String> mCenterStone;

    HashMap<String, ArrayList<String>> mAccentStones;
    HashMap<String, ArrayList<String>> mGemStones;


    public String mCenterStoneSelected;
    public HashMap<String, String> mAccentStoneSelected;
    public HashMap<String, String> mGemStoneSelected;

    public ProductDetail(int id) {
        super(id);
    }


    public void extractBasicInfo(JSONObject productInfo) {
        id = productInfo.optInt(Constants.JsonConstants.PRODUCTID);
        code = productInfo.optString(Constants.JsonConstants.PRODUCTCODE);
        name = productInfo.optString(Constants.JsonConstants.PRODUCTNAME);
        description = productInfo.optString(Constants.JsonConstants.DESCRIPTION);
        type = productInfo.optDouble(Constants.JsonConstants.TYPE);
        costunit = productInfo.optString(Constants.JsonConstants.COSTUNITS);
        aspectratio = (float) productInfo.optDouble(Constants.JsonConstants.ASPECTRATIO);
        widthUnit = productInfo.optString(Constants.JsonConstants.WIDTHUNITS);
        weight = (float) productInfo.optDouble(Constants.JsonConstants.WEIGHT);
        sizeUnit = productInfo.optString(Constants.JsonConstants.SIZEUNITS);
        height = (float) productInfo.optDouble(Constants.JsonConstants.HEIGHT);
        width = (float) productInfo.optDouble(Constants.JsonConstants.WIDTH);
        size = (float) productInfo.optDouble(Constants.JsonConstants.SIZE);
    }

    public void extractGetCartProductList(JSONObject productInfo) {
        id = productInfo.optInt(Constants.JsonConstants.PRODUCTID);
        name = productInfo.optString(Constants.JsonConstants.PRODUCTLABEL);
        mPrimaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        mPrimaryMetalColor = productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR);
        mPrimaryMetalPurity = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPURITY);
        mCenterStoneSelected = productInfo.optString(Constants.JsonConstants.CENTERSTONE);
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                mAccentStoneSelected.put(Constants.JsonConstants.ACCENTSTONE + i, productInfo.optString(Constants.JsonConstants.ACCENTSTONE + i));
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                mGemStoneSelected.put(Constants.JsonConstants.GEMSTONE + i, productInfo.optString(Constants.JsonConstants.GEMSTONE + i));
            }
        }
        price = (float) productInfo.optDouble(Constants.JsonConstants.PRODUCTPRICE);
        size = productInfo.optInt(Constants.JsonConstants.PRODUCTSIZE);
    }

    public void extractCustomization(JSONObject productInfo) throws JSONException {
        id = productInfo.optInt(Constants.JsonConstants.PRODUCTID);
        mMakeingCharge = (float) productInfo.optDouble(Constants.JsonConstants.MAKINGCHARGES);
        mMakeingChargeUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
        mPrimaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        mPrimaryMetalColor = productInfo.optString(Constants.JsonConstants.PRIMARYMETALCOLOR);
        mPrimaryMetalPurity = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPURITY);
        mPrimaryMetalPrice = (float) productInfo.optDouble(Constants.JsonConstants.PRIMARYMETALPRICE);
        mPrimaryMetalPriceUnits = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
        mStonePrice = (float) productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
        mStonePriceUnit = productInfo.optString(Constants.JsonConstants.STONEPRICEUNITS);
        if (productInfo.has(Constants.JsonConstants.METALLIST)) {
            mMetalList = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALLIST).length(); i++) {
                mMetalList.add(productInfo.getJSONArray(Constants.JsonConstants.METALLIST).getString(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.METALPURITYLIST)) {
            mMetalPurityList = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALPURITYLIST).length(); i++) {
                mMetalPurityList.add(productInfo.getJSONArray(Constants.JsonConstants.METALPURITYLIST).getInt(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.METALCOLORLIST)) {
            mMetalColorList = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.METALCOLORLIST).length(); i++) {
                mMetalColorList.add(productInfo.getJSONArray(Constants.JsonConstants.METALCOLORLIST).getInt(i));
            }
        }
        if (productInfo.has(Constants.JsonConstants.CENTERSTONE)) {
            mCenterStone = new ArrayList<>();
            for (int i = 0; i < productInfo.getJSONArray(Constants.JsonConstants.CENTERSTONE).length(); i++) {
                mCenterStone.add(productInfo.getJSONArray(Constants.JsonConstants.CENTERSTONE).getString(i));
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.ACCENTSTONE + i)) {
                ArrayList<String> accentStonelist = new ArrayList<>();
                for (int j = 0; i < productInfo.getJSONArray(Constants.JsonConstants.ACCENTSTONE + i).length(); j++) {
                    accentStonelist.add(productInfo.getJSONArray(Constants.JsonConstants.ACCENTSTONE + i).getString(j));
                }
                mAccentStones.put(Constants.JsonConstants.ACCENTSTONE + i, accentStonelist);
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                ArrayList<String> gemstonelist = new ArrayList<>();
                for (int j = 0; i < productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE + i).length(); j++) {
                    gemstonelist.add(productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE + i).getString(j));
                }
                mGemStones.put(Constants.JsonConstants.GEMSTONE + i, gemstonelist);
            }
        }

    }
}
