package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.goldadorn.main.BR;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

/**
 * Created by bpa001 on 4/21/16.
 */
public class Designer extends BaseObservable implements IConfigurableObject,IIDInterface, Parcelable {
    private int designerId;

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

    private String type = Filtersparam.TYPE_DESIGNER;
    private String designerName;
    private String designerPic;

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

    @Override
    public void config() {
        Log.d("dj", "config - url before parsing: "+designerPic);
        setDesignerPic(URLHelper./*parseImageURLDesignersTemp(designerPic)*/parseImageURL(designerPic));
        id =getType()+designerId+"";
    }
    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }
    @Bindable
    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
        notifyPropertyChanged(BR.designerName);
    }
    @Bindable
    public String getDesignerPic() {
        Log.d("dj", "getDesignerPic - url: "+designerPic);
        return designerPic;
    }

    public void setDesignerPic(String designerPic) {
        this.designerPic = designerPic;
        Log.d("dj", "setDesignerPic - url: "+designerPic);
        notifyPropertyChanged(BR.designerPic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.designerId);
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.designerName);
        dest.writeString(this.designerPic);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    public Designer() {
    }

    protected Designer(Parcel in) {
        this.designerId = in.readInt();
        this.id = in.readString();
        this.type = in.readString();
        this.designerName = in.readString();
        this.designerPic = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Designer> CREATOR = new Parcelable.Creator<Designer>() {
        @Override
        public Designer createFromParcel(Parcel source) {
            return new Designer(source);
        }

        @Override
        public Designer[] newArray(int size) {
            return new Designer[size];
        }
    };
}
