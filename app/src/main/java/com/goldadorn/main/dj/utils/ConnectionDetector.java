package com.goldadorn.main.dj.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    private Context mContext;
    private static ConnectionDetector mConDetector;

    public static ConnectionDetector getInstance(Context mContext){

        if (mConDetector == null){
            mConDetector = new ConnectionDetector(mContext);
        }

        return mConDetector;
    }

    private ConnectionDetector(Context context) {
        this.mContext = context;
    }




    /**
     * Checking for all possible internet providers
     **/
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    public boolean canGetLocation() {

        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(mContext.LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = isGpsTurnedOn();

        if (isGPSEnabled && isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }




    public boolean isGpsTurnedOn(){

        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(mContext.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


}
