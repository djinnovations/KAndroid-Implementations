package com.goldadorn.main.dj.model;

import com.goldadorn.main.dj.utils.RandomUtils;

/**
 * Created by User on 12-08-2016.
 */
public class ProductCustomizationTabUpdationDataObj {

    private float metalCostPerUnit;
    //private int stoneCostPerUnit;
    private float metalWeight;
    //private int diamondNos;
    private int totalCost;
    private int stonesTotalCost;

    public int getStonesTotalCost() {
        return stonesTotalCost;
    }

    public ProductCustomizationTabUpdationDataObj setStonesTotalCost(int stonesTotalCost) {
        this.stonesTotalCost = stonesTotalCost;
        return this;
    }

    public String getMetalCost(){
        return String.valueOf( (int) (metalCostPerUnit * metalWeight));
    }

   /* public String getStoneCost(){
        return String.valueOf(stoneCostPerUnit * diamondNos);
    }*/

    public int getTotalCost(){
        return totalCost;
    }

    public String getVAT(){
        return String.valueOf( (int) RandomUtils.getVat(String.valueOf(totalCost)));
    }

    public ProductCustomizationTabUpdationDataObj setMetalCostPerUnit(float metalCostPerUnit) {
        this.metalCostPerUnit = metalCostPerUnit;
        return this;
    }

    /*public ProductCustomizationTabUpdationDataObj setStoneCostPerUnit(int stoneCostPerUnit) {
        this.stoneCostPerUnit = stoneCostPerUnit;
        return this;
    }*/

    public ProductCustomizationTabUpdationDataObj setMetalWeight(float metalWeight) {
        this.metalWeight = metalWeight;
        return this;
    }

    /*public void setDiamondNos(int diamondNos) {
        this.diamondNos = diamondNos;
    }*/

    public ProductCustomizationTabUpdationDataObj setTotalCost(int totalCost) {
        this.totalCost = totalCost;
        return this;
    }

    /*public void setMakingCharges(String makingCharges) {
        this.makingCharges = makingCharges;
    }*/

    public String getMakingCharges(){
        return String.valueOf(totalCost - (stonesTotalCost + ((int) (metalCostPerUnit * metalWeight)) + Integer.parseInt(getVAT())));
    }
}
