package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.goldadorn.main.BR;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

/**
 * Created by bpa001 on 4/21/16.
 */
public class Designer extends BaseObservable implements IConfigurableObject{
    private int designerId;
    private String designerName;
    private String designerPic;

    @Override
    public void config() {
        setDesignerPic(URLHelper.parseImageURL(designerPic));
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
        return designerPic;
    }

    public void setDesignerPic(String designerPic) {
        this.designerPic = designerPic;
        notifyPropertyChanged(BR.designerPic);
    }
}
