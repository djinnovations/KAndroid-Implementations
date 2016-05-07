package com.goldadorn.main.model;

import com.goldadorn.main.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ProductInfo  {
    public final int id;
    public String code,description;
    public String sizeUnit, weightUnits,metalType,metalPurityInUnits,metalWeightUnits,metalColor;
    public int metalPurity=0;
    public float size, height, width;
    public float weight;
    public int imageCount;
    public ArrayList<String> images = new ArrayList<>();
    public String productType;
    public Double productDefaultPrice=0.0;
    public float productmaking_charges=0,metalrate=0,metalWeight=0;
    public final ArrayList<StoneDetail> stonesDetails= new ArrayList<>();


   /* "metalWeight": 1.52,
            "metalRate": 2227.8,  metalPrice":754.0,
            "productMakChargesPerUnit": 500,
            "stonePrice": 30690, stonePrice":897.08,*/


    public ProductInfo(int id) {
        this.id = id;
    }


    public static ProductInfo extractFromJson(JSONObject obj) throws JSONException {
        ProductInfo p = new ProductInfo(obj.getInt(Constants.JsonConstants.PRODUCT_ID));
        p.height = (float) obj.getDouble("productHeight");
        p.width = (float) obj.getDouble("productWidth");
        if(obj.has("productDescription")){
            p.description=obj.getString("productDescription");
        }
        p.size = (float) obj.getDouble("productSize");
        p.weight = (float) obj.getDouble("productWeight");
        p.sizeUnit = obj.getString("sizeUnits");
        p.weightUnits = obj.getString("widthUnits");
        p.imageCount = obj.getInt("productNumImages");
        p.productType = obj.getString("productType");
        p.code = obj.getString("productCode");
        p.metalWeight=(float) obj.getDouble("metalWeight");
        p.metalrate=(float) obj.getDouble("metalRate");
        p.metalType=obj.getString("metalType");
        p.metalColor=obj.getString("metalColor");
        p.metalPurity=obj.getInt("metalPurity");
        p.metalPurityInUnits=obj.getString("metalPurityUnits");
        p.metalWeightUnits=obj.getString("metalWeightUnits");
        p.productmaking_charges=(float) obj.getDouble("productMakChargesPerUnit");
        p.productDefaultPrice=obj.getDouble("productDefaultPrice");
        if (obj.has(Constants.JsonConstants.PROSTONEDETAILS)) {
            JSONArray stonedetailsarray = obj.optJSONArray(Constants.JsonConstants.PROSTONEDETAILS);
            if(stonedetailsarray!=null)
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

    public CharSequence getDisplayHeight(){
        return String.format(Locale.getDefault(),"%.2f %s",height,sizeUnit);
    }
    public CharSequence getDisplayWidth(){
        return String.format(Locale.getDefault(),"%.2f %s",width,sizeUnit);
    }
    public CharSequence getDisplayWeight(){
        return String.format(Locale.getDefault(),"%.2f %s",weight,weightUnits);
    }
}