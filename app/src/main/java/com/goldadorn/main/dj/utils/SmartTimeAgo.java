package com.goldadorn.main.dj.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 08-06-2016.
 */

public class SmartTimeAgo {

    public static final String SMART_TIME_EOF = "EOF";
    static DateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault());

    public static String getSmartTime(/*Context appContext, */long inputTime/*, boolean isPost*/){
        long thisInstant = System.currentTimeMillis();
        long diffInSeconds = Math.round((thisInstant - inputTime)/1000);
        if (diffInSeconds > 60){
            long diffInMins = Math.round(diffInSeconds/60);
            if (diffInMins > 60){
                int diffInhrs = Math.round(diffInMins/60);
                if (diffInhrs > 6){
                    /*if (isPost)
                        return SMART_TIME_EOF;
                    return DateUtils.getRelativeDateTimeString(appContext,inputTime, DateUtils.
                            SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL).toString();*/
                    return getPostFormattedTime(inputTime);
                }else return String.valueOf(diffInhrs)+" hours ago";
            }else return String.valueOf(diffInMins)+" mins ago";
        }else return "1 min ago";
    }


    private static String getPostFormattedTime(Long timestamp){
        Date date = (new Date(Long.parseLong(timestamp.toString())));
        String time = sdf.format(date);

        time = time.replace(",",", at ");
        return time;
    }
}
