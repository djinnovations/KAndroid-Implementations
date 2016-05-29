package com.goldadorn.main.dj.uiutils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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


    public AlertDialog displayInfo(Activity activity, String title, String infoMsg, String positiveBtnText,
                                   String negativeBtnText, boolean showTwoOptions, final InfoDisplayListener mInfoListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(infoMsg).setPositiveButton(positiveBtnText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mInfoListener.onPositiveSelection(dialog);
                    }
                }).setTitle(title);

        if(showTwoOptions){
            builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
        }

        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimHelp;
        alert.show();


        Button btnPositive = (alert.getButton(DialogInterface.BUTTON_POSITIVE));
        btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell * 2.2f);
        btnPositive.setTextColor(ResourceReader.getInstance(appContext).getColorFromResource(R.color.colorPrimaryDark));
        btnPositive.setAllCaps(false);
        if(showTwoOptions){

            Button btnNegative = (alert.getButton(DialogInterface.BUTTON_NEGATIVE));
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell*2.2f);
            btnNegative.setTextColor(ResourceReader.getInstance(appContext).getColorFromResource(R.color.colorPrimaryDark));
            btnNegative.setAllCaps(false);
        }

        ((TextView) alert.findViewById(android.R.id.message)).
                setTextSize(TypedValue.COMPLEX_UNIT_PX, pixels_per_Xcell * 2.7f);

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


    public interface DialogButtonClickListener{

        void onPositiveBtnClicked(Dialog dialog, View btn);
        void onNegativeBtnClicked(Dialog dialog, View btn);
    }


    public Dialog displayDialog(Activity activity, int layoutResId, String title, String bodyMsg,String positiveBtnText,
                                String negativeBtnText, final DialogButtonClickListener listener){

        final Dialog dialog = new Dialog(activity);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View tempView = LayoutInflater.from(appContext).inflate(R.layout.dj_custom_views, null);
        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		/*tempParams.width = dialogWidthInPx;
		tempParams.height = dialogHeightInPx;*/
        tempParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = 0; // Dim level. 0.0 - no dim, 1.0 - completely opaque

        dialog.setContentView(layoutResId);
        //dialog.setCancelable(false);
        ((TextView) dialog.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) dialog.findViewById(R.id.tvInfoMsg)).setText(bodyMsg);
        ((TextView) dialog.findViewById(R.id.tvPositive)).setText(positiveBtnText);
        ((TextView) dialog.findViewById(R.id.tvPositive)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPositiveBtnClicked(dialog, v);
            }
        });
        ((TextView) dialog.findViewById(R.id.tvNegative)).setText(negativeBtnText);
        ((TextView) dialog.findViewById(R.id.tvNegative)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNegativeBtnClicked(dialog, v);
            }
        });
        dialog.setCancelable(false);
        dialog.getWindow().setAttributes(tempParams);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.editbox_dropdown_dark_frame);
/*		if(keyPadOnTop)
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);*/

        return dialog;
    }


}
