package com.goldadorn.main.utils;

import java.util.Date;

/**
 * Created by bhavinpadhiyar on 2/20/16.
 */
public class TimeUtils {

    public static String getTimeRedable(Date timeMs) {
        if(timeMs ==null)
            return "0d 0h 0m";
        Long dif=new Date().getTime()-timeMs.getTime();
        return getTimeRedable(dif);
    }
    public static String getTimeRedable(Long val) {
        if(val <= 0){
            return "0d 0h 0m";
        }
        double d = val.doubleValue();
        Double timeMs = Math.floor(d / 1000);
        Double secs_diff = timeMs % 60;
        timeMs = Math.floor(timeMs / 60);
        Double minsDiff = timeMs % 60;
        timeMs = Math.floor(timeMs / 60);
        Double hoursDiff = timeMs % 24;
        Double daysDiff = Math.floor(timeMs / 24);

        if(daysDiff==null || hoursDiff==null || minsDiff ==null){
            return "NA";
        }
        else{
            return Math.round(daysDiff) + "d " + Math.round(hoursDiff) + "h " + Math.round(minsDiff) + "m";
        }
    }
    public static String getTimeRedable(Double timeMs) {
        if(timeMs <= 0){
            return "0d 0h 0m";
        }

        timeMs = Math.floor(timeMs / 1000);
        Double secs_diff = timeMs % 60;
        timeMs = Math.floor(timeMs / 60);
        Double minsDiff = timeMs % 60;
        timeMs = Math.floor(timeMs / 60);
        Double hoursDiff = timeMs % 24;
        Double daysDiff = Math.floor(timeMs / 24);

        if(daysDiff==null || hoursDiff==null || minsDiff ==null){
            return "NA";
        }
        else{
            return Math.round(daysDiff) + "d " + Math.round(hoursDiff) + "h " + Math.round(minsDiff) + "m";
        }
    }
}
