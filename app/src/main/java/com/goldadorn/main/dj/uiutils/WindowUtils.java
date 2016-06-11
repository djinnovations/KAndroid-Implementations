package com.goldadorn.main.dj.uiutils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.goldadorn.main.R;

/**
 * Created by COMP on 1/28/2016.
 */
public class WindowUtils {

    private Context appContext;
    ViewConstructor mViewConstructor;
    DisplayProperties mDispProp;
    private static WindowUtils thisInstance;
    //private final String networkInfoMsg = "Please turn on your mobile DATA or WIFI";

    private WindowUtils(Context appContext) {

        this.appContext = appContext;
        mViewConstructor = ViewConstructor.getInstance(appContext);
    }


    public static WindowUtils getInstance(Context appContext){
        if (thisInstance == null){
            thisInstance = new WindowUtils(appContext);
        }
        return thisInstance;
    }

    public void genericPermissionInfoDialog(Activity activity, String message) {

        mViewConstructor.displayInfo(activity, "Permission info", message, "OKAY", "",

                false, new ViewConstructor.InfoDisplayListener() {
                    @Override
                    public void onPositiveSelection(DialogInterface alertDialog) {

                        alertDialog.dismiss();
                    }
                });

    }


    public static float dialogDimAmount = 0.3f;
    public Dialog displayDialogNoTitle(Context activityContext, View layout/*, String title*/){

        Dialog dialog = new Dialog(activityContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*TextView alertTitle=(TextView) dialog.getWindow().getDecorView().findViewById(android.R.id.title);
        alertTitle.setBackgroundColor(new ResourceReader(activityContext).getColorFromResource(R.color.colorBlackDimText));
        alertTitle.setTextColor(Color.WHITE);
        alertTitle.setGravity(Gravity.CENTER);
        dialog.setTitle(title);*/

        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		/*tempParams.width = dialogWidthInPx;
		tempParams.height = dialogHeightInPx;*/
        tempParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        tempParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = dialogDimAmount;

        View tempView =  layout;
        dialog.setContentView(tempView);
        //dialog.setCancelable(false);

        dialog.getWindow().setAttributes(tempParams);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.editbox_dropdown_dark_frame);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
/*		if(keyPadOnTop)
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);*/

        return dialog;
    }



    public Dialog displayOverlay(Activity activityToDisplayOverlay, String infoMsg, int colorResId) {

        Dialog dialog = new Dialog(activityToDisplayOverlay);
        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		/*tempParams.width = dialogWidthInPx;
        tempParams.height = dialogHeightInPx;*/
        tempParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = 0.0f;

        View overLay = LayoutInflater.from(appContext).inflate(R.layout.window_util_overlay/*dialog_overlay*/, null);
        TextView tvTemp = (TextView) overLay.findViewById(R.id.tvOverlayInfo);
        if (infoMsg != null) {
            tvTemp.setText(infoMsg);
            tvTemp.setTextColor(ResourceReader.getInstance(appContext)
                    .getColorFromResource(colorResId));
        } else tvTemp.setVisibility(View.GONE);
        dialog.setContentView(overLay);
        dialog.setCancelable(false);

        dialog.getWindow().setAttributes(tempParams);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}
