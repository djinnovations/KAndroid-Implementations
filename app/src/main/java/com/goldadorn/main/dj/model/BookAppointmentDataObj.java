package com.goldadorn.main.dj.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 02-07-2016.
 */
public class BookAppointmentDataObj implements Parcelable {

    //private String designerName;
    private String itemName;
    private String designerId;
    private String itemImageUrl;
    private String collectionId;
    private String productId;
    private int modeOfAppt;
    //private String collectionName;
    //private String productName;

    public BookAppointmentDataObj(int mode){
        modeOfAppt = mode;
    }

    public int getModeOfAppt() {
        return modeOfAppt;
    }

    public String getItemName() {
        return itemName;
    }

    public BookAppointmentDataObj setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public String getDesignerId() {
        return designerId;
    }

    public BookAppointmentDataObj setDesignerId(String designerId) {
        this.designerId = designerId;
        return this;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public BookAppointmentDataObj setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
        return this;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public BookAppointmentDataObj setCollectionId(String collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public BookAppointmentDataObj setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    protected BookAppointmentDataObj(Parcel in) {
        itemName = in.readString();
        designerId = in.readString();
        itemImageUrl = in.readString();
        collectionId = in.readString();
        productId = in.readString();
        modeOfAppt = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(designerId);
        dest.writeString(itemImageUrl);
        dest.writeString(collectionId);
        dest.writeString(productId);
        dest.writeInt(modeOfAppt);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BookAppointmentDataObj> CREATOR = new Parcelable.Creator<BookAppointmentDataObj>() {
        @Override
        public BookAppointmentDataObj createFromParcel(Parcel in) {
            return new BookAppointmentDataObj(in);
        }

        @Override
        public BookAppointmentDataObj[] newArray(int size) {
            return new BookAppointmentDataObj[size];
        }
    };


    @Override
    public String toString() {
        return "BookAppointmentDataObj{" +
                "itemName='" + itemName + '\'' +
                ", designerId='" + designerId + '\'' +
                ", itemImageUrl='" + itemImageUrl + '\'' +
                ", collectionId='" + collectionId + '\'' +
                ", productId='" + productId + '\'' +
                ", modeOfAppt=" + modeOfAppt +
                '}';
    }
}
