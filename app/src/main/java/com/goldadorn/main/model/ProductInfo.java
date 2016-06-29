package com.goldadorn.main.model;

import android.util.Log;

import com.goldadorn.main.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ProductInfo {
    public final int id;
    public String code, description;
    public String sizeUnit, weightUnits, metalType, metalPurityInUnits, metalWeightUnits, metalColor;
    public int metalPurity = 0;
    public float size, height, width;
    public float weight;
    public int imageCount;
    public ArrayList<String> images = new ArrayList<>();
    public String productType;
    public Double productDefaultPrice = 0.0;
    public float productmaking_charges = 0, metalrate = 0, metalWeight = 0;
    public final ArrayList<StoneDetail> stonesDetails = new ArrayList<>();
    public String warrantyTxt, moneyBackTxt, certificateTxt, estimatedDelTime, payModesTxt;
    public ArrayList<String> new5details;


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
        if (obj.has("productDescription")) {
            p.description = obj.getString("productDescription");
        }
        p.size = (float) obj.getDouble("productSize");
        p.weight = (float) obj.getDouble("productWeight");
        p.sizeUnit = obj.getString("sizeUnits");
        p.weightUnits = obj.getString("widthUnits");
        p.imageCount = obj.getInt("productNumImages");
        p.productType = obj.getString("productType");
        p.code = obj.getString("productCode");
        p.metalWeight = (float) obj.getDouble("metalWeight");
        p.metalrate = (float) obj.getDouble("metalRate");
        p.metalType = obj.getString("metalType");
        p.metalColor = obj.getString("metalColor");
        p.metalPurity = obj.getInt("metalPurity");
        p.metalPurityInUnits = obj.getString("metalPurityUnits");
        p.metalWeightUnits = obj.getString("metalWeightUnits");
        p.productmaking_charges = (float) obj.getDouble("productMakChargesPerUnit");
        p.productDefaultPrice = obj.getDouble("productDefaultPrice");
        p.new5details = new ArrayList<>();
        //p.warrantyTxt = obj.getString("productWarranty");
        p.new5details.add(obj.getString("productWarranty"));
        //p.moneyBackTxt = obj.getString("productMBP");
        p.new5details.add(obj.getString("productMBP"));
        //p.certificateTxt = obj.getString("productCert");
        p.new5details.add(obj.getString("productCert"));
        //p.estimatedDelTime = obj.getString("productEDTInDays");
        p.new5details.add(obj.getString("productEDTInDays"));
        //ArrayList<String> list = new ArrayList<>();
        if (!obj.isNull("productPayModes")) {
            try {
                JSONArray jsonArr = obj.getJSONArray("productPayModes");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonArr.length(); i++) {
                    sb = sb.append(getFullNamePay(jsonArr.getString(i)) + ", ");
                }
                p.payModesTxt = sb.toString().substring(0, (sb.toString().length() - 2));
                Log.d("djprod", "payment modes - productInfo: " + p.payModesTxt);
                p.new5details.add(p.payModesTxt);
            } catch (JSONException e) {
                e.printStackTrace();
                p.payModesTxt = null;
                p.new5details.add(p.payModesTxt);
            }
        }

        if (obj.has(Constants.JsonConstants.PROSTONEDETAILS)) {
            JSONArray stonedetailsarray = obj.optJSONArray(Constants.JsonConstants.PROSTONEDETAILS);
            if (stonedetailsarray != null)
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


    private static String getFullNamePay(String payModeFromServer) {
        switch (payModeFromServer) {
            case "net":
                return "Net Banking";
            case "cre":
                return "Credit Card";
            case "deb":
                return "Debit Card";
            case "cod":
                return "Cash On Delivery";
            case "emi":
                return "EMI";
            default:
                return "";
        }
    }

    public CharSequence getDisplayHeight() {
        if (height < 0)
            return "NA";
        return String.format(Locale.getDefault(), "%.2f %s", height, sizeUnit);
    }

    public CharSequence getDisplayWidth() {
        if (width < 0)
            return "NA";
        return String.format(Locale.getDefault(), "%.2f %s", width, sizeUnit);
    }

    public CharSequence getDisplayWeight() {
        return String.format(Locale.getDefault(), "%.2f %s", weight, weightUnits);
    }
}