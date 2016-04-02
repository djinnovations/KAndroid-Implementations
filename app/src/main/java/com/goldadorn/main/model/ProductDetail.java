package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONArray;
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

    public ArrayList<StoneDetail> stonesDetails = new ArrayList<>();

    public ProductDetail(int id) {
        super(id);
    }


    public ProductDetail extractBasicInfo(JSONObject productInfo) throws JSONException {
        ProductDetail p = new ProductDetail(productInfo.optInt(Constants.JsonConstants.PRODUCTID));
        p.code = productInfo.optString(Constants.JsonConstants.PRODUCTCODE);
        p.name = productInfo.optString(Constants.JsonConstants.PRODUCTNAME);
        p.description = productInfo.optString(Constants.JsonConstants.DESCRIPTION);
        p.type = productInfo.optDouble(Constants.JsonConstants.TYPE);
        p.image_a_r = (float) productInfo.optDouble(Constants.JsonConstants.ASPECTRATIO);
        p.currency = productInfo.optString(Constants.JsonConstants.COSTUNITS);
        p.unitPrice = productInfo.optLong(Constants.JsonConstants.PRODUCTPRICE);
        p.widthUnit = productInfo.optString(Constants.JsonConstants.WIDTHUNITS);
        p.weight = (float) productInfo.optDouble(Constants.JsonConstants.WEIGHT);
        p.height = (float) productInfo.optDouble(Constants.JsonConstants.HEIGHT);
        p.width = (float) productInfo.optDouble(Constants.JsonConstants.WIDTH);
        p.size = (float) productInfo.optDouble(Constants.JsonConstants.SIZE);
        p.sizeUnit = productInfo.optString(Constants.JsonConstants.SIZEUNITS);
        if (productInfo.has(Constants.JsonConstants.PROSTONEDETAILS)) {
            JSONArray stonedetailsarray = productInfo.getJSONArray(Constants.JsonConstants.PROSTONEDETAILS);
            for (int i = 0; i < stonedetailsarray.length(); i++) {
                JSONObject stoneobj = stonedetailsarray.getJSONObject(i);
                StoneDetail stoneDetail = new StoneDetail();
                stoneDetail.stoneFactor = stoneobj.optString("stoneFactor");
                stoneDetail.type = stoneobj.optString("stoneType");
                stoneDetail.number = stoneobj.optInt("stoneNum");
                stoneDetail.price = (float) stoneobj.optDouble("stoneRate");
                stoneDetail.weight = (float) stoneobj.optDouble("stoneWeight");
                stoneDetail.color = stoneobj.optString("stoneColor");
                stoneDetail.clarity = stoneobj.optString("stoneClarity");
                stoneDetail.shape = stoneobj.optString("stoneShape");
                stoneDetail.size = stoneobj.optInt("stoneSize");
                stoneDetail.seting = stoneobj.optString("stoneSeting");
                stoneDetail.weightunit = stoneobj.optString("stoneWeightUnit");
                stoneDetail.rateunit = stoneobj.optString("stoneRateUnit");
                stoneDetail.sizeunit = stoneobj.optString("stoneSizeUnit");
                stoneDetail.stonecut = stoneobj.optString("stoneCut");
                p.stonesDetails.add(stoneDetail);
            }
        }
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
        p.currency = productInfo.optString(Constants.JsonConstants.PRODUCTPRICEUNITS);
        p.unitPrice = productInfo.optLong(Constants.JsonConstants.PRODUCTPRICE);
        p.size = productInfo.optInt(Constants.JsonConstants.PRODUCTSIZE);

        p.quantity = 1;
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

    class StoneDetail {
        public String stoneFactor;
        public String type;
        public int number;
        public float price;
        public float weight;
        public String color;
        public String clarity;
        public String shape;
        public int size;
        public String seting;
        public String weightunit;
        public String rateunit;
        public String sizeunit;
        public String stonecut;
    }
}
