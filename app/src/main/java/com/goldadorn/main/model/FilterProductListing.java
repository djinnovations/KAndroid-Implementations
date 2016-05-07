package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.goldadorn.main.BR;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

import java.io.Serializable;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class FilterProductListing extends BaseObservable implements Parcelable,IConfigurableObject {

    private int prodId;
    private int collId;
    private int desgnId;
    private String prodName;

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    @Bindable
    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
        notifyPropertyChanged(BR.likeCount);
    }

    private String likeCount;


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;

    }

    private String price;
    private int productPrice;
    private int aspectRatio;
    private String productPriceUnits;

    public void config()
    {
        price = productPriceUnits+" "+productPrice;
        image = URLHelper.getInstance().productImageEndPoint+prodId+"/"+prodId+"-1.jpg";
        System.out.println(image);
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public int getCollId() {
        return collId;
    }

    public void setCollId(int collId) {
        this.collId = collId;
    }

    public int getDesgnId() {
        return desgnId;
    }

    public void setDesgnId(int desgnId) {
        this.desgnId = desgnId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(int aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getProductPriceUnits() {
        return productPriceUnits;
    }

    public void setProductPriceUnits(String productPriceUnits) {
        this.productPriceUnits = productPriceUnits;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.prodId);
        dest.writeInt(this.collId);
        dest.writeInt(this.desgnId);
        dest.writeString(this.prodName);
        dest.writeInt(this.productPrice);
        dest.writeInt(this.aspectRatio);
        dest.writeString(this.productPriceUnits);
    }

    public FilterProductListing() {
    }

    protected FilterProductListing(Parcel in) {
        this.prodId = in.readInt();
        this.collId = in.readInt();
        this.desgnId = in.readInt();
        this.prodName = in.readString();
        this.productPrice = in.readInt();
        this.aspectRatio = in.readInt();
        this.productPriceUnits = in.readString();
    }

    public static final Parcelable.Creator<FilterProductListing> CREATOR = new Parcelable.Creator<FilterProductListing>() {
        @Override
        public FilterProductListing createFromParcel(Parcel source) {
            return new FilterProductListing(source);
        }

        @Override
        public FilterProductListing[] newArray(int size) {
            return new FilterProductListing[size];
        }
    };
}
