package com.goldadorn.main.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by bhavinpadhiyar on 4/13/15.
 */
public class NetworkUtilities
{


    public static boolean haveConnectedWifi(Context c)
    {
        boolean connectedWifi = false;
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ( ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI && ni.isConnectedOrConnecting())
            connectedWifi = true;
        return connectedWifi;
    }

    public static boolean haveConnectedMobile(Context c)
    {
        boolean connectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ( ni != null  && ni.getType() == ConnectivityManager.TYPE_MOBILE && ni.isConnectedOrConnecting())
            connectedMobile = true;

        return connectedMobile;
    }

    public static boolean haveConnected2GMobile(Context c)
    {
        boolean connectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ( ni != null  && ni.getType() == ConnectivityManager.TYPE_MOBILE && ni.isConnectedOrConnecting())
            connectedMobile = true;

        return connectedMobile;
    }

    public static boolean isConnected(Context c)
    {

        ConnectivityManager connManager = ((ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.isFailover()) {
                    return false;
                }
                return networkInfo.isConnected();
            } else {
                return false;
            }
        }
        return false;
       /* boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ( ni != null )
        {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI)
                if (ni.isConnectedOrConnecting())
                    haveConnectedWifi = true;
            if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
                if (ni.isConnectedOrConnecting())
                    haveConnectedMobile = true;
        }

        if(haveConnectedWifi || haveConnectedMobile)
            return true;
        else
            return false;*/
    }


}
