package com.goldadorn.main.model;

import java.io.Serializable;

/**
 * Created by nithinjohn on 22/03/16.
 */
public class Value implements Serializable{

    public final String valueName, keyID,keyName, valueId;


    public Value(String keyID,String keyName, String valueId, String valueName) {
        this.keyID = keyID;
        this.keyName = keyName;
        this.valueId = valueId;
        this.valueName = valueName;
    }
    public Value(String keyID, String valueId) {
        this.keyID = keyID;
        this.keyName=keyID;
        this.valueId = valueId;
        this.valueName = valueId;
    }
}
