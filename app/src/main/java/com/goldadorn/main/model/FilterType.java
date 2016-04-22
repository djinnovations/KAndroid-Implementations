package com.goldadorn.main.model;

import android.databinding.BaseObservable;

import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

/**
 * Created by bpa001 on 4/21/16.
 */
public class FilterType extends BaseObservable implements IConfigurableObject{

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
    }
}
