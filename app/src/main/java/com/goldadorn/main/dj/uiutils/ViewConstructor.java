package com.goldadorn.main.dj.uiutils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import com.goldadorn.main.R;


/**
 * Created by COMP on 1/22/2016.
 */
public class ViewConstructor {


    private static ViewConstructor thisInstance;

    public static interface InfoDisplayListener{

        public void onPositiveSelection(DialogInterface alertDialog);
    }

    private DisplayProperties mDispProp;
    private float pixels_per_Ycell;
    private float pixels_per_Xcell;
    private Context appContext;


    private ViewConstructor(Context appContext) {

        this.appContext = appContext;
        DisplayProperties mDispProp = DisplayProperties.getInstance(appContext, 1);
        this.pixels_per_Xcell = mDispProp.getXPixelsPerCell();
        this.pixels_per_Ycell = mDispProp.getYPixelsPerCell();
    }


    public static ViewConstructor getInstance(Context appContext){

        if (thisInstance == null){
            thisInstance = new ViewConstructor(appContext);
        }
        return thisInstance;
    }


    public AlertDialog displayInfo(Activity activity, String title, String infoMsg, String positiveBtnText, boolean showTwoOptions,
                                   final InfoDisplayListener mInfoListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(infoMsg).setPositiveButton(positiveBtnText,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mInfoListener.onPositiveSelection(dialog);
                    }
                }).setTitle(title);

        if(showTwoOptions){

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimHelp;
        alert.show();


        Button btnPositive = (alert.getButton(DialogInterface.BUTTON_POSITIVE));
        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell * 2);
        btnPositive.setTextColor(ResourceReader.getInstance(appContext).getColorFromResource(R.color.colorPrimaryDark));

        if(showTwoOptions){

            Button btnNegative = (alert.getButton(DialogInterface.BUTTON_NEGATIVE));
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell*2);
            btnNegative.setTextColor(ResourceReader.getInstance(appContext).getColorFromResource(R.color.colorPrimaryDark));
            builder.setCancelable(false);
        }

        ((TextView) alert.findViewById(android.R.id.message)).
                setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell * 3);

        return alert;
    }


    public AlertDialog displayViewInfo(Activity activity, String title, int viewResId, String positiveBtnText, boolean showTwoOptions,
                                                          final InfoDisplayListener mInfoListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(viewResId).setPositiveButton(positiveBtnText,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mInfoListener.onPositiveSelection(dialog);
                    }
                }).setCancelable(false).setTitle(title);

        if(showTwoOptions){

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimHelp;
        alert.show();


        Button btnPositive = (alert.getButton(DialogInterface.BUTTON_POSITIVE));
        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell * 2);
        btnPositive.setTextColor(ResourceReader.getInstance(appContext).getColorFromResource(R.color.colorPrimaryDark));

        if(showTwoOptions){

            Button btnNegative = (alert.getButton(DialogInterface.BUTTON_NEGATIVE));
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell*2);
            btnNegative.setTextColor(ResourceReader.getInstance(appContext).getColorFromResource(R.color.colorPrimaryDark));
        }

        ((TextView) alert.findViewById(android.R.id.message)).
                setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell * 3);

        return alert;
    }



}
