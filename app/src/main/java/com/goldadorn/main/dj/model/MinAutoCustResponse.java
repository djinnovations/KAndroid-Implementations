package com.goldadorn.main.dj.model;

/**
 * Created by User on 21-09-2016.
 */
public class MinAutoCustResponse {

    int productId;
    double price = -1;
    double discount = -1;
    String sellerSku;
    float weight = -1;
    double size = -1;
    String metalSwatch;
    String stone;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getSellerSku() {
        return sellerSku;
    }

    public void setSellerSku(String sellerSku) {
        this.sellerSku = sellerSku;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getMetalSwatch() {
        return metalSwatch;
    }

    public void setMetalSwatch(String metalSwatch) {
        this.metalSwatch = metalSwatch;
    }

    public String getStone() {
        return stone;
    }

    public void setStone(String stone) {
        this.stone = stone;
    }
}
