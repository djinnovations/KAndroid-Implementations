package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

/**
 * Created by bpa001 on 4/21/16.
 */
public class FilterType extends BaseObservable implements IConfigurableObject,IIDInterface, Parcelable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    private String type = Filtersparam.TYPE_PRODUCT_TYPE;
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

    private String prodType;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public String getProdType() {
        return prodType;
    }

    public void setProdType(String prodType) {
        this.prodType = prodType;
    }

    @Override
    public void config() {
        String url = URLHelper.getInstance().productTypesImageEndPoint+prodType+".jpg";
        setImage(url);
        id = getType()+prodType+"";
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
        dest.writeString(this.prodType);
        dest.writeString(this.image);
    }

    public FilterType() {
    }

    protected FilterType(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.isSelected = in.readByte() != 0;
        this.prodType = in.readString();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<FilterType> CREATOR = new Parcelable.Creator<FilterType>() {
        @Override
        public FilterType createFromParcel(Parcel source) {
            return new FilterType(source);
        }

        @Override
        public FilterType[] newArray(int size) {
            return new FilterType[size];
        }
    };
}
