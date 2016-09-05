package com.goldadorn.main.dj.model;

import com.goldadorn.main.activities.showcase.ProductActivity;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.ProductOptions;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 29-08-2016.
 */
public class AddToCartRequestDataObj {


    /**
     * {
     * "prodId":295,
     * "prodName":"The Catherine Ring",
     * "userId":2,
     * "primaryMetal":"Gold",
     * "primaryMetalPurity":18,
     * "primaryMetalColor":"Yellow",
     * "size":12,
     * "priceUnits":"Rs",
     * "totalPrice":8184,
     * "metalPrice":2227.800048828125,
     * "stonePrice":0,
     * "makingCharges":500,
     * "metalWeight":3,
     * "orderQty":1,
     * "VAT":0,
     * "sessionid":"bbca2ab771260d7d267c1b6d737961bc",
     * "metalSel" : "bla",
     * "stoneSel" : "ppp"
     * }
     */

    public String getPrimaryMetal() {
        if (metalSwatch != null)
            return metalSwatch.getType();
        return null;
    }

    public String getMetalPurity() {
        if (metalSwatch != null)
            return metalSwatch.getPurity();
        return null;
    }

    public String getMetalColor(){
        if (metalSwatch != null)
            return metalSwatch.getColor();
        return null;
    }

    public String getMetalCostPerUnit() {
        if (metalSwatch != null)
            return metalSwatch.getCostPerUnit();
        return null;
    }

    private Swatches.MixedSwatch metalSwatch;
    private Swatches.MixedSwatch stoneSwatch;
    private String size;
    private int prodId;
    private String prodName;
    private String prodType;
    private int desId;
    private double totalAmount;
    private double stonePrice;
    private double makingCharges;
    private float metalWeight;
    private float VAT;
    private String metalSelectedString;
    private String stoneSelectedString;
    private double offerReplicaPrice;
    private double discount;
    //private Map<String, List<String>> selectedParams;


    public double getOfferReplicaPrice() {
        return offerReplicaPrice;
    }

    public void setOfferReplicaPrice(String offerReplicaPrice) {
        this.offerReplicaPrice = Double.parseDouble(offerReplicaPrice);
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    public String getStoneSelectedString() {
        return stoneSelectedString;
    }

    public void setStoneSelectedString(String stoneSelectedString) {
        this.stoneSelectedString = stoneSelectedString;
    }

    public void setSelectedParams(Map<String, List<String>> selectedParams) {
        //this.selectedParams = selectedParams;
        setSelectedStrings(selectedParams);
    }


    private void setSelectedStrings(Map<String, List<String>> selectedParams) {
        if (selectedParams == null)
            return;
        for (String key : selectedParams.keySet()) {
            if (key.equals(ProductActivity.METAL))
                setMetalSelectedString(selectedParams.get(ProductActivity.METAL).get(0));
            else if (key.equals(ProductActivity.STONE))
                setStoneSelectedString(selectedParams.get(ProductActivity.STONE).get(0));
            else if (key.equals(ProductActivity.SIZE))
                setSize(selectedParams.get(ProductActivity.SIZE).get(0));
        }
    }


    public Swatches.MixedSwatch getMetalSwatch() {
        return metalSwatch;
    }

    public void setMetalSwatch(Swatches.MixedSwatch metalSwatch) {
        this.metalSwatch = metalSwatch;
    }

    public Swatches.MixedSwatch getStoneSwatch() {
        return stoneSwatch;
    }

    public void setStoneSwatch(Swatches.MixedSwatch stoneSwatch) {
        this.stoneSwatch = stoneSwatch;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getDesId() {
        return desId;
    }

    public void setDesId(int desId) {
        this.desId = desId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        /*if (totalAmount instanceof String)
            this.totalAmount = Double.parseDouble((String) totalAmount);
        else if (totalAmount instanceof Double || totalAmount instanceof Float ||
                totalAmount instanceof Integer)
            this.totalAmount = (double) totalAmount;
        else if (totalAmount instanceof Long)*/
            this.totalAmount = Double.parseDouble(totalAmount);
        setVAT();
    }

    public double getStonePrice() {
        return stonePrice;
    }

    public void setStonePrice(double stonePrice) {
        this.stonePrice = stonePrice;
    }

    public double getMakingCharges() {
        return makingCharges;
    }

    public void setMakingCharges(Object makingCharges) {
        if (makingCharges instanceof String)
            this.makingCharges = Double.parseDouble((String) makingCharges);
        else if (makingCharges instanceof Double)
            this.makingCharges = (double) makingCharges;
    }

    public float getMetalWeight() {
        return metalWeight;
    }

    public void setMetalWeight(float metalWeight) {
        this.metalWeight = metalWeight;
    }

    public float getVAT() {
        return VAT;
    }

    private void setVAT() {
        this.VAT = (float) RandomUtils.getVat(String.valueOf(totalAmount));
    }

    public String getMetalSelectedString() {
        return metalSelectedString;
    }

    public void setMetalSelectedString(String metalSelectedString) {
        this.metalSelectedString = metalSelectedString;
    }

}
