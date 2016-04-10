package com.goldadorn.main.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class OptionKey implements Serializable {

    public final String keyID;
    public String keyName;
    public final boolean isOptional;

    public OptionKey(String keyID, boolean isOptional) {
        this.keyID = keyID;
        this.isOptional = isOptional;
    }
    public OptionKey(String keyID) {
        this.keyID = keyID;
        isOptional=true;
    }
    public String getDisplayString() {
        if (!TextUtils.isEmpty(keyName))
            return keyName;
        else
            return keyID;
    }
}
