package com.goldadorn.main.dj.model;

import java.util.List;

/**
 * Created by User on 04-08-2016.
 */
public class CustomizationDisableList {

    private List<Integer> metalDisableList;
    private List<Integer> stoneDisableList;
    private List<String> sizeDataList;

    public CustomizationDisableList(List<Integer> metalDisableList, List<Integer> stoneDisableList, List<String> sizeDataList) {
        this.metalDisableList = metalDisableList;
        this.stoneDisableList = stoneDisableList;
        this.sizeDataList = sizeDataList;
    }

    public List<Integer> getMetalDisableList() {
        return metalDisableList;
    }

    public void setMetalDisableList(List<Integer> metalDisableList) {
        this.metalDisableList = metalDisableList;
    }

    public List<Integer> getStoneDisableList() {
        return stoneDisableList;
    }

    public void setStoneDisableList(List<Integer> stoneDisableList) {
        this.stoneDisableList = stoneDisableList;
    }

    public List<String> getSizeDataList() {
        return sizeDataList;
    }

    public void setSizeDataList(List<String> sizeDataList) {
        this.sizeDataList = sizeDataList;
    }
}
