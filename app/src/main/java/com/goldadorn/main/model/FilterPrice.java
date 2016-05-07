package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bpa001 on 4/21/16.
 */
public class FilterPrice extends BaseObservable implements IIDInterface, Parcelable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = getType()+id+"";
    }

    private String id;
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    private String type = Filtersparam.TYPE_PRICE;
    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public boolean isSelected() {
        return isSelected;
    }
    private boolean isSelected=false;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }



    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    private String max;
    private String min;

    private String maxVal;
    private String minVal;

    public String getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public String getMinVal() {
        return minVal;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
        dest.writeString(this.max);
        dest.writeString(this.min);
        dest.writeString(this.maxVal);
        dest.writeString(this.minVal);
    }

    public FilterPrice() {
    }

    protected FilterPrice(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.isSelected = in.readByte() != 0;
        this.max = in.readString();
        this.min = in.readString();
        this.maxVal = in.readString();
        this.minVal = in.readString();
    }

    public static final Parcelable.Creator<FilterPrice> CREATOR = new Parcelable.Creator<FilterPrice>() {
        @Override
        public FilterPrice createFromParcel(Parcel source) {
            return new FilterPrice(source);
        }

        @Override
        public FilterPrice[] newArray(int size) {
            return new FilterPrice[size];
        }
    };
}
