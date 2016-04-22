package com.goldadorn.main.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.goldadorn.main.BR;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.IConfigurableObject;

/**
 * Created by bpa001 on 4/21/16.
 */
public class FilterCollection extends BaseObservable implements IConfigurableObject
{

    private int collId;
    private String collName;

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

    }
}
