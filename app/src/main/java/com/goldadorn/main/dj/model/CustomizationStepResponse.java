package com.goldadorn.main.dj.model;

import java.util.List;

/**
 * Created by User on 04-08-2016.
 */
public class CustomizationStepResponse {
    List<String> stone;
    List<String> metal;
    List<String> size;
    String price;

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

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
