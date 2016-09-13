package com.goldadorn.main.model;

import android.util.Log;

import com.goldadorn.main.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ProductInfo {
    public final int id;
    public String code, description;
    public String sizeUnit, weightUnits, metalType, metalPurityInUnits, metalWeightUnits, metalColor;
    public int metalPurity = 0;
    public float size, height, width;
    public float weight;
    public int imageCount;
    //public static ArrayList<String> imageUrlList;
    public String productType;
    public Double productDefaultPrice = 0.0;
    public double productmaking_charges = -1;
    public float metalrate = -1;
    public float metalWeight = -1;
    public String chainDetail;
    public final ArrayList<StoneDetail> stonesDetails = new ArrayList<>();
    public String warrantyTxt, moneyBackTxt, certificateTxt, estimatedDelTime, payModesTxt;
    public HashMap<String, String> new5details;


   /* "metalWeight": 1.52,
            "metalRate": 2227.8,  metalPrice":754.0,
            "productMakChargesPerUnit": 500,
            "stonePrice": 30690, stonePrice":897.08,*/


    public ProductInfo(int id) {
        this.id = id;
    }


    private static String getChainDetail(JSONObject jsonObject){
        try {
            if (!jsonObject.isNull("chainDetail")){
                if (jsonObject.getString("chainDetail").equalsIgnoreCase("-1"))
                    return null;
                StringBuilder sb = new StringBuilder();
                String[] array = jsonObject.getString("chainDetail").split(":");
                if (array[0].equalsIgnoreCase("1"))
                    sb.append("Yes,");
                sb.append(" ").append(array[1]).append(" Chain of length ").append(array[2]).append(" inches");
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        //p.weightUnits = obj.getString("widthUnits");
        p.weightUnits = obj.getString("weightUnits");
        p.imageCount = obj.getInt("productNumImages");
        p.productType = obj.getString("productType");
        p.code = obj.getString("productCode");
        p.productDefaultPrice = obj.getDouble("productDefaultPrice");
        p.productmaking_charges = obj.getDouble("productMakChargesPerUnit");
        p.chainDetail = getChainDetail(obj);
        /*p.metalWeight = (float) obj.getDouble("metalWeight");
        p.metalrate = (float) obj.getDouble("metalRate");
        p.metalType = obj.getString("metalType");
        p.metalColor = obj.getString("metalColor");
        p.metalPurity = obj.getInt("metalPurity");
        p.metalPurityInUnits = obj.getString("metalPurityUnits");
        p.metalWeightUnits = obj.getString("metalWeightUnits");
        p.productmaking_charges = (float) obj.getDouble("productMakChargesPerUnit");
        p.productDefaultPrice = obj.getDouble("productDefaultPrice");*/
        p.new5details = new HashMap<>();
        //p.warrantyTxt = obj.getString("productWarranty");
        p.new5details.put("Buyback & Warranty Policy", obj.getString("productWarranty"));
        //p.moneyBackTxt = obj.getString("productMBP");
        p.new5details.put("Money Back Policy", obj.getString("productMBP"));
        //p.certificateTxt = obj.getString("productCert");
        p.new5details.put("Certificate Type", obj.getString("productCert"));
        //p.estimatedDelTime = obj.getString("productEDTInDays");
        p.new5details.put("Estimated Delivery Time", obj.getString("productEDTInDays"));
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
                p.new5details.put("Payment Modes Available", p.payModesTxt);
            } catch (JSONException e) {
                e.printStackTrace();
                p.payModesTxt = null;
                p.new5details.put("Payment Modes Available", p.payModesTxt);
            }
        }

        /*if (obj.has(Constants.JsonConstants.PROSTONEDETAILS)) {
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
        }*/
        return p;
    }


    public String semiPreciousString;
    public static ProductInfo parseStoneMetal(ProductInfo pinfo, JSONObject obj){
        try {
            pinfo.metalWeight = (float) obj.getDouble("metalWeight");
            pinfo.metalrate = (float) obj.getDouble("metalRate");
            pinfo.metalType = obj.getString("metalType");
            pinfo.metalColor = obj.getString("metalColor");
            pinfo.metalPurity = obj.getInt("metalPurity");
            pinfo.metalPurityInUnits = obj.getString("metalPurityUnits");
            pinfo.metalWeightUnits = obj.getString("metalWeightUnits");
            //pinfo.productmaking_charges = (float) obj.getDouble("productMakChargesPerUnit");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (obj.has(Constants.JsonConstants.PROSTONEDETAILS)) {
            ArrayList<String> list = new ArrayList<>();
            JSONArray stonedetailsarray = obj.optJSONArray(Constants.JsonConstants.PROSTONEDETAILS);
            if (stonedetailsarray != null)
                for (int i = 0; i < stonedetailsarray.length(); i++) {
                    try {
                        JSONObject stoneobj = stonedetailsarray.getJSONObject(i);
                        if (stoneobj.getInt("isPrecious") != 1){
                            double weight = stoneobj.optDouble("stoneWeight");
                            String weightString;
                            if (weight < 0)
                                weightString = "";
                            else weightString = " ("+ String.valueOf(weight) + " cts) ";
                            list.add(stoneobj.optString("stoneType")+ weightString);
                        }
                        else {
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
                            pinfo.stonesDetails.add(stoneDetail);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            if (list.size() > 0)
                pinfo.semiPreciousString = buildStoneString(list);
            else pinfo.semiPreciousString = null;

        }
        return pinfo;
    }


    private static String buildStoneString(ArrayList<String> list){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<list.size(); i++){
            sb.append(list.get(i)).append(", ");
        }
        return sb.substring(0, (sb.lastIndexOf(",") - 1));
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