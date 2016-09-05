package com.goldadorn.main.dj.model;

import java.util.List;

/**
 * Created by User on 04-08-2016.
 */
public class CustomizationStepResponse {
    List<String> stone;
    List<String> metal;
    List<Double> size;
    double price;
    String prodName;
    String prodDesc;
    String metalSwatch;
    String sellerSku;
    double discount;
    float weight = -1;

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscount() {
        return Math.round((float) discount);
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }

    public String getMetalSwatch() {
        return metalSwatch;
    }

    public void setMetalSwatch(String metalSwatch) {
        this.metalSwatch = metalSwatch;
    }

    public String getSellerSku() {
        return sellerSku;
    }

    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
    }

    public List<String> getStone() {
        return stone;
    }

    public void setStone(List<String> stone) {
        this.stone = stone;
    }

    public List<String> getMetal() {
        return metal;
    }

    public void setMetal(List<String> metal) {
        this.metal = metal;
    }

    public List<Double> getSize() {
        return size;
    }

    public void setSize(List<Double> size) {
        this.size = size;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    public void setPrice(String price) {
        this.price = Double.parseDouble(price);
    }
}
