package com.goldadorn.main.utils;

/**
 * Created by BhavinPadhiyar on 21/04/16.
 */
public class ResultFormating {
    static public String formating(String value) {
        if(value!=null)
        {
            String stringValue = value.toString();
            stringValue = stringValue.trim();
            if(stringValue.indexOf("[")==0) {
                stringValue = "{"+'"'+"data"+'"'+":"+stringValue+"}";
                return stringValue;
            }
            else if(stringValue.indexOf("[[{")!=-1) {
                stringValue = stringValue.replace("[[{","[{");
                stringValue = stringValue.replace("}]]","}]");
                return stringValue;
            }
            else
                return  value;
        }
        else
            return  value;
    }
}
