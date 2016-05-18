package com.goldadorn.main.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.model.LoginResult;
import com.goldadorn.main.model.ServerError;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

/**
 * Created by bhavinpadhiyar on 11/6/15.
 */
public class NetworkResultValidator {
    // Private constructor prevents instantiation from other classes
    private NetworkResultValidator() {}

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final NetworkResultValidator INSTANCE = new NetworkResultValidator();
    }

    public static NetworkResultValidator getInstance() {
        return SingletonHolder.INSTANCE;
    }




    public static boolean isResultOK(String url, String value,final AjaxStatus status,String msg,View hostPort,Context activityContext)
    {
        return isResultOK( url,  value,   status, msg, hostPort, activityContext,null);
    }
    public static boolean isResultOK(String url, String value,final AjaxStatus status,String msg,View view,Context context,ExtendedAjaxCallback callBack)
    {
        Gson gson = new Gson();

        String message=null;

        if(value!=null && status!=null && status.getCode()==200)
            return true;
        else if(status!=null && status.getError()!=null)
        {
            if(status.getError()!=null && status.getError().equals("")==false) {

                message = status.getError();
                ServerError error = gson.fromJson(message, ServerError.class);
                message = error.getMsg();
            }
            else if(status.getMessage().equals("")==false)
                message = status.getMessage();
            else
                message = "Unknown Error";
        }
        else if(status!=null && status.getMessage()!=null)
            message = status.getMessage();

        if(message!=null)
        {
            if(msg==null)
                msg=message;
            if(msg!=null && msg.equals("")==false)
            {

                final String errorMessage = message;
                final Context classRef = context;
                if(view!=null) {
                    final Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
                    if(!msg.equals(message)) {
                        snackbar.setAction("Read More", new View.OnClickListener() {
                            public void onClick(View var1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(classRef);
                                builder.setTitle("Error Info");
                                builder.setMessage(errorMessage);
                                builder.setCancelable(true);
                                builder.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                    ColoredSnackbar.alert(snackbar).show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(classRef);
                    builder.setTitle(msg);
                    builder.setMessage(errorMessage);
                    builder.setCancelable(true);
                    builder.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            return false;
        }

        return false;
    }


    
}
