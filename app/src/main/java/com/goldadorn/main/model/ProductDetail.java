package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class ProductDetail {

    public long mId;
    public String mCode;
    public String mName;
    public String mDescription;
    public double mType;
    public String mCostUnit;
    public float mAspectratio;
    public String mWidthUnit;
    public float mWeight;
    public String mSizeUnit;
    public float mHeight;
    public float mWidth;
    public float mSize;

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
    ArrayList<ArrayList<String>> mAccentStones;
    ArrayList<ArrayList<String>> mGemStones;
    public String mCenterStoneSelected;
    public String mAccentStoneSelected;


    public void extractBasicInfo(JSONObject productInfo) {
        mId = productInfo.optInt(Constants.JsonConstants.PRODUCTID);
        mCode = productInfo.optString(Constants.JsonConstants.PRODUCTCODE);
        mName = productInfo.optString(Constants.JsonConstants.PRODUCTNAME);
        mDescription = productInfo.optString(Constants.JsonConstants.DESCRIPTION);
        mType = productInfo.optDouble(Constants.JsonConstants.TYPE);
        mCostUnit = productInfo.optString(Constants.JsonConstants.COSTUNITS);
        mAspectratio = (float) productInfo.optDouble(Constants.JsonConstants.ASPECTRATIO);
        mWidthUnit = productInfo.optString(Constants.JsonConstants.WIDTHUNITS);
        mWeight = (float) productInfo.optDouble(Constants.JsonConstants.WEIGHT);
        mSizeUnit = productInfo.optString(Constants.JsonConstants.SIZEUNITS);
        mHeight = (float) productInfo.optDouble(Constants.JsonConstants.HEIGHT);
        mWidth = (float) productInfo.optDouble(Constants.JsonConstants.WIDTH);
        mSize = (float) productInfo.optDouble(Constants.JsonConstants.SIZE);
    }

    public void extractCustomization(JSONObject productInfo) throws JSONException {
        mId = productInfo.optInt(Constants.JsonConstants.PRODUCTID);
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
                mAccentStones.add(accentStonelist);
            }
        }
        for (int i = 0; i < 11; i++) {
            if (productInfo.has(Constants.JsonConstants.GEMSTONE + i)) {
                ArrayList<String> gemstonelist = new ArrayList<>();
                for (int j = 0; i < productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE + i).length(); j++) {
                    gemstonelist.add(productInfo.getJSONArray(Constants.JsonConstants.GEMSTONE + i).getString(j));
                }
                mGemStones.add(gemstonelist);
            }
        }

    }
}
