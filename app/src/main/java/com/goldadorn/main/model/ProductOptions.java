package com.goldadorn.main.model;

import android.util.Log;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.showcase.ProductActivity;
import com.goldadorn.main.constants.Constants;
import com.goldadorn.main.dj.model.Swatches;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class ProductOptions {

    private final int id;
    public String priceUnit;
    //public Double size = 0.0;
    public int primaryMetalPurity = -1;
    public String primaryMetal = "";
    public String primaryMetalColor = "";
    public String priceUnits = "";
    //public Double stonePrice = 0.0;
    public double size = -1;
    public String prodType;
    public int discount;
    public final ArrayList<Map.Entry<String, Float>> priceBreakDown = new ArrayList<>();
    //public final ArrayList<Map.Entry<OptionKey, ArrayList<OptionValue>>> customisationOptions = new ArrayList<>();
    public ArrayList<Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>>> customisationOptions;

    public static List<String> parsedSizeList = new ArrayList<>();
    public static CustomizationDefaultValues mCustDefVals;

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


    public boolean isCustomizationAvail(){
        return (rawSizeListVals.size() > 0) || (mapOfStoneCust.size() > 0) || (mapOfMetalCust.size() > 0);
    }


    public static float stonePrice;

    public static ProductOptions extractCustomization(JSONObject productInfo) throws JSONException {
        ProductOptions p = new ProductOptions(productInfo.optInt(Constants.JsonConstants.PRODUCTID));

        Log.d("djcustom", "response Obj - ProductOptions: " + productInfo.toString());
        float primaryMetalPrice;
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

        mCustDefVals = new CustomizationDefaultValues();
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Metal", primaryMetalPrice));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Stone", stonePrice));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("Making Charges", makingcharges));
        p.priceBreakDown.add(new AbstractMap.SimpleEntry<>("VAT (Tax)", (float) 00.00));

        p.priceUnit = productInfo.optString(Constants.JsonConstants.MAKINGCHARGESUNITS);
        p.primaryMetalPurity = productInfo.optInt(Constants.JsonConstants.METALPURITY);
        p.primaryMetal = productInfo.optString(Constants.JsonConstants.PRIMARYMETAL);
        p.primaryMetalColor = productInfo.optString(Constants.JsonConstants.METALCOLOUR);
        p.priceUnits = productInfo.optString(Constants.JsonConstants.PRIMARYMETALPRICEUNITS);
        p.size = productInfo.getDouble(Constants.JsonConstants.SIZE);
        try {
            p.prodType = productInfo.getString("prodType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCustDefVals.setSizeText(parseSize(p.size, p.prodType));
        mapOfMetalCust = new HashMap<>();
        mapOfStoneCust = new HashMap<>();
        metalListForParam = new ArrayList<>();
        stoneListForParam = new ArrayList<>();
        //p.stonePrice = productInfo.optDouble(Constants.JsonConstants.STONEPRICE);
        //DJphy

        p.customisationOptions = new ArrayList<>();
        ArrayList<Swatches.MixedSwatch> metalSwatch = extractSwatch(productInfo, Swatches.TYPE_METAL);
        if (metalSwatch.size() != 0)
            p.customisationOptions.add(new AbstractMap.SimpleEntry<>(new OptionKey("Metal", false)
                    , metalSwatch));
        ArrayList<Swatches.MixedSwatch> gemStoneSwatch = extractSwatch(productInfo, Swatches.TYPE_GEMSTONE);
        if (gemStoneSwatch.size() != 0)
            p.customisationOptions.add(new AbstractMap.SimpleEntry<>(new OptionKey("Diamond Quality", false)
                    , gemStoneSwatch));
        parseDefStone(productInfo);
        parseDefMetal(productInfo);
        rawSizeListVals = new ArrayList<>();
        parsedSizeList = new ArrayList<>();
        extractSizeCust(productInfo, p.prodType);
        /*if (!productInfo.isNull("discount"))
            p.discount = productInfo.getInt("discount");*/
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

    public static Map<String, Integer> mapOfMetalCust;
    public static Map<String, Integer> mapOfStoneCust ;
    //public static Map<String, Integer> mapOfSize = new HashMap<>();
    public static List<Double> rawSizeListVals ;
    public static ArrayList<String> metalListForParam ;
    public static ArrayList<String> stoneListForParam ;

    private static void parseDefStone(JSONObject jsonObject) {
        if (jsonObject.isNull("defaultStone")){
            mCustDefVals.setStoneDescTxt("NA");
            mCustDefVals.setUrlStoneImg(UiRandomUtils.DIAMOND_URL);
            return;
        }
        try {
            Swatches.MixedSwatch stoneSwatch = Swatches.getMixedSwatch(addDummyVarToGemStoneCust(jsonObject.getString("defaultStone")),
                    Swatches.TYPE_GEMSTONE);
            mCustDefVals.setDefStoneSwatch(stoneSwatch);
            mCustDefVals.setStoneDescTxt(stoneSwatch.getSwatchDisplayTxt());
            mCustDefVals.setUrlStoneImg(UiRandomUtils.DIAMOND_URL);
        } catch (Exception e) {
            e.printStackTrace();
            mCustDefVals.setStoneDescTxt("NA");
            mCustDefVals.setUrlStoneImg(UiRandomUtils.DIAMOND_URL);
        }
    }


    private static void parseDefMetal(JSONObject jsonObject){
        if (jsonObject.isNull("defaultMetal")){
            mCustDefVals.setResIdMetal(R.drawable.vector_icon_cross_brown);
            return;
        }
        try {
            Swatches.MixedSwatch metalSwatch = Swatches.getMixedSwatch(jsonObject.getString("defaultMetal"),
                    Swatches.TYPE_METAL);
            mCustDefVals.setResIdMetal(metalSwatch.getSwatchDisplayIconResId());
            mCustDefVals.setDefMetalSwatch(metalSwatch);
        } catch (JSONException e) {
            e.printStackTrace();
            mCustDefVals.setResIdMetal(R.drawable.vector_icon_cross_brown);
        }
    }


    private static String parseSize(double size, String prodType){
        int sizeInInt = (int) size;
        if (size == -1.000)
            return "-1";
        if (prodType.equals("Rings")) {
            //mCustDefVals.setSizeText(String.valueOf(sizeInInt));
            return String.valueOf(sizeInInt);
        }else if (prodType.equals("Necklaces") || prodType.equals("Chains")){
            //mCustDefVals.setSizeText(String.valueOf(sizeInInt) + "\"");
            return String.valueOf(sizeInInt) + "\"";
        }
        else {
            String temp = String.valueOf(size);
            String[] tempArr = temp.split("\\.");
            String from = tempArr[0];
            String to  = tempArr[1].replace("0","");
            //mCustDefVals.setSizeText(from+"-"+to+"\"");
            return from+"-"+to+"\"";
        }
    }

    public static class CustomizationDefaultValues {
        private int resIdMetal;
        private int resIdStone;
        private String sizeText;
        private String urlStoneImg;
        private String stoneDescTxt;
        private Swatches.MixedSwatch defStoneSwatch;
        private Swatches.MixedSwatch defMetalSwatch;

        public Swatches.MixedSwatch getDefMetalSwatch() {
            return defMetalSwatch;
        }

        public void setDefMetalSwatch(Swatches.MixedSwatch defMetalSwatch) {
            this.defMetalSwatch = defMetalSwatch;
        }

        public Swatches.MixedSwatch getDefStoneSwatch() {
            return defStoneSwatch;
        }

        public void setDefStoneSwatch(Swatches.MixedSwatch defStoneSwatch) {
            this.defStoneSwatch = defStoneSwatch;
        }

        public String getStoneDescTxt() {
            return stoneDescTxt;
        }

        public void setStoneDescTxt(String stoneDescTxt) {
            this.stoneDescTxt = stoneDescTxt;
        }

        public void setUrlStoneImg(String urlStoneImg){
            this.urlStoneImg = urlStoneImg;
        }

        public String getUrlStoneImg(){
            return urlStoneImg;
        }

        public int getResIdMetal() {
            return resIdMetal;
        }

        public void setResIdMetal(int resIdMetal) {
            this.resIdMetal = resIdMetal;
        }

        public int getResIdStone() {
            return resIdStone;
        }

        public void setResIdStone(int resIdStone) {
            this.resIdStone = resIdStone;
        }

        public String getSizeText() {
            return sizeText;
        }

        public void setSizeText(String sizeText) {
            this.sizeText = sizeText;
        }
    }

    private static void extractSizeCust(JSONObject productInfo, String prodType) {
        if (!productInfo.isNull("SizeCust")){
            try {
                JSONArray jsonArray = productInfo.getJSONArray("SizeCust");
                for (int i = 0; i < jsonArray.length(); i++) {
                    rawSizeListVals.add(i, jsonArray.getDouble(i));
                    double sizeval = jsonArray.getDouble(i);
                    parsedSizeList.add(parseSize(sizeval, prodType));
                    //// TODO: 02-08-2016  default value for size
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static List<String> getSizeList(){
        return parsedSizeList;
    }*/

    /*public void clearOptions(){
        mapOfMetalCust = new HashMap<>();
        mapOfStoneCust = new HashMap<>();
        metalListForParam = new ArrayList<>();
        stoneListForParam = new ArrayList<>();
    }*/

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
            Swatches.MixedSwatch mixSwatch = null;
            try {
                if (type == Swatches.TYPE_METAL) {
                    mapOfMetalCust.put(jsonArrayCust.getString(i), i);
                    metalListForParam.add(i, jsonArrayCust.getString(i));
                    mixSwatch = Swatches.getMixedSwatch(jsonArrayCust.getString(i), type);
                    if (mixSwatch.getDefStat() == 1)
                        mCustDefVals.setResIdMetal(mixSwatch.getSwatchDisplayIconResId());
                }
                else if (type == Swatches.TYPE_GEMSTONE) {
                    mapOfStoneCust.put(jsonArrayCust.getString(i), i);
                    stoneListForParam.add(i, jsonArrayCust.getString(i));
                    mixSwatch = Swatches.getMixedSwatch(addDummyVarToGemStoneCust(jsonArrayCust.getString(i)), type);
                    /*if (mixSwatch.getDefStat() == 1){
                        mCustDefVals.setStoneDescTxt(mixSwatch.getSwatchDisplayTxt());
                        mCustDefVals.setUrlStoneImg(UiRandomUtils.DIAMOND_URL);
                    }*/
                    //// TODO: 02-08-2016  default resId for stone
                }
                swatchList.add(mixSwatch);
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


    public List<Integer> getDisableList(List<String> enableList, String key){
        if (key == ProductActivity.METAL){
            Map<String, Integer> tempMetalCust = new HashMap(mapOfMetalCust);
            return listMakeUP(tempMetalCust, enableList);
        }
        else if (key == ProductActivity.STONE){
            Map<String, Integer> tempStoneCust = new HashMap(mapOfStoneCust);
            return listMakeUP(tempStoneCust, enableList);
        }
        return null;
    }


    public List<String> getParsedSize(List<Double> listOfSizeFromServer){
        List<String > modifiedlist = new ArrayList<>();
        for (Double sizeval: listOfSizeFromServer) {
            modifiedlist.add(parseSize(sizeval, this.prodType));
        }
        return modifiedlist;
    }

    private List<Integer> listMakeUP(Map<String, Integer> originalList, List<String> enableList){
        for (String val: enableList){
            originalList.remove(val);
        }
        List<Integer> positionList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : originalList.entrySet()) {
            positionList.add(entry.getValue());
        }
        return positionList;
    }

}
