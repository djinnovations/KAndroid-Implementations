package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.goldadorn.main.BR;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

/**
 * Created by bpa001 on 4/21/16.
 */
public class FilterCollection extends BaseObservable implements IConfigurableObject,IIDInterface, Parcelable {
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

    private String type = Filtersparam.TYPE_COLLECTION;
    private int collId;
    private String collName;

    public boolean getSelected() {
        return isSelected;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected=false;

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
//        notifyPropertyChanged(BR.image);
    }

    private String image;

    public int getCollId() {
        return collId;
    }

    public void setCollId(int collId) {
        this.collId = collId;
    }

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    @Override
    public void config() {
        String url =URLHelper.getInstance().collectionImageEndPoint+collId+"/"+collId+".jpg";
        setImage(url);
        id= getType()+collId+"";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeInt(this.collId);
        dest.writeString(this.collName);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
        dest.writeString(this.image);
    }

    public FilterCollection() {
    }

    protected FilterCollection(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.collId = in.readInt();
        this.collName = in.readString();
        this.isSelected = in.readByte() != 0;
        this.image = in.readString();
    }

    public static final Parcelable.Creator<FilterCollection> CREATOR = new Parcelable.Creator<FilterCollection>() {
        @Override
        public FilterCollection createFromParcel(Parcel source) {
            return new FilterCollection(source);
        }

        @Override
        public FilterCollection[] newArray(int size) {
            return new FilterCollection[size];
        }
    };
}
