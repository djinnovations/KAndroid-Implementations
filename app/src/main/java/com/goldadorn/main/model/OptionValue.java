package com.goldadorn.main.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class OptionValue implements Serializable {

    public final String  valueId;
    public String  valueName;

    public OptionValue(String valueId) {
        this.valueId = valueId;
    }

    public String getDisplayString() {
        if (!TextUtils.isEmpty(valueName))
            return valueName;
        else
            return valueId;
    }
}
