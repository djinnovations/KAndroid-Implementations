package com.goldadorn.main.dj.uiutils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Created by COMP on 1/28/2016.
 */
public class AlertWindowUtils {

    private Context appContext;
    ViewConstructor mViewConstructor;
    DisplayProperties mDispProp;
    private static AlertWindowUtils thisInstance;
    //private final String networkInfoMsg = "Please turn on your mobile DATA or WIFI";

    private AlertWindowUtils(Context appContext) {

        this.appContext = appContext;
        mViewConstructor = ViewConstructor.getInstance(appContext);
    }


    public static AlertWindowUtils getInstance(Context appContext){
        if (thisInstance == null){
            thisInstance = new AlertWindowUtils(appContext);
        }
        return thisInstance;
    }

    public void genericPermissionInfoDialog(Activity activity, String message) {

        mViewConstructor.displayInfo(activity, "Permission info", message, "OKAY",

                false, new ViewConstructor.InfoDisplayListener() {
                    @Override
                    public void onPositiveSelection(DialogInterface alertDialog) {

                        alertDialog.dismiss();
                    }
                });

    }

}
