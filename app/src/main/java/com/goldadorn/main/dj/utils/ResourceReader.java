package com.goldadorn.main.dj.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * Created by COMP on 2/9/2016.
 */
public class ResourceReader {

    private static ResourceReader ourInstance;
    private static Context mContext;

    private ResourceReader(Context mContext) {

        ResourceReader.mContext = mContext;
    }

    public static ResourceReader getInstance(Context appContext) {

        if (ourInstance == null) {
            ourInstance = new ResourceReader(appContext);
        }
        return ourInstance;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public int getColorFromResource(int colorResId){

        if(Constants.CURRENT_API_LEVEL == Build.VERSION_CODES.M)
            return mContext.getResources().getColor(colorResId, mContext.getTheme());
        else
            return mContext.getResources().getColor(colorResId);
    }


    public String getStringFromResource(int stringResId){

        return mContext.getResources().getString(stringResId);
    }
}
