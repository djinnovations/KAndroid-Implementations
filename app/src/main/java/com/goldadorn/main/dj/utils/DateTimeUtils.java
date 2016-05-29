package com.goldadorn.main.dj.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by COMP on 3/4/2016.
 */
public class DateTimeUtils {


    public static long getDateInMillis(String dateFromServer) {

        int[] dateInInt = serverToUsableDate(dateFromServer);
        if (dateInInt != null) {
            long dateInMillis = getTimeInMillisForDate(dateInInt[2], dateInInt[0], dateInInt[1]);
            return dateInMillis;
        }
        return 0;
    }


    public static String getTime12Hour(String dateFromServer) {

        String[] arraydateTime = dateFromServer.split(" ");
        /*Time t = Time.valueOf(arraydateTime[1].trim());
        long l = t.getTime();*/
        return _24To12Hour(arraydateTime[1].trim());
    }


    private static String _24To12Hour(String _24HrTime) {

        try {
            String _24HourTime = _24HrTime;
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            System.out.println(_24HourDt);
            String time12Hr = _12HourSDF.format(_24HourDt);
            System.out.println("time in 12 hours: " + time12Hr);
            return time12Hr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static int[] serverToUsableDate(String dateFromServer) {

        try {
            String[] dateArr = dateFromServer.split("/");
            int[] dateToInt = new int[]{Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2])};
            return dateToInt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static long getTimeInMillisForDate(int year, int month, int date) {

        Log.d("dj", "year: " + year);
        Log.d("dj", "month: " + month);
        Log.d("dj", "date: " + date);
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.YEAR, year);
        tempCalendar.set(Calendar.MONTH, (month - 1));
        tempCalendar.set(Calendar.DAY_OF_MONTH, date);
        long dateTime = tempCalendar.getTimeInMillis();
        return dateTime;
    }


    public static String getNewDate(String inputDate, String inputDateFormat, String outputDateFormat) {

        try {
            DateFormat dateFormat = new SimpleDateFormat(inputDateFormat);
            Date inputDateObj = dateFormat.parse(inputDate);
            String newDateFormat = new SimpleDateFormat(outputDateFormat).format(inputDateObj);
            Log.d("dj", "new date format string: " + newDateFormat);
            return newDateFormat;
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate;
        }

    }


    public static String[] getFromAndToDate() {

        String fromDate;
        String toDate;
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        String month = String.valueOf(now.get(Calendar.MONTH));
        String thisday = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        String last7thDay = String.valueOf(getWeekLessFromCurrent());

        fromDate = year + "-" + month + "-" + thisday;
        toDate = year + "-" + month + "-" + last7thDay;

        return new String[]{fromDate, toDate};
    }


    private static int getWeekLessFromCurrent() {

        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_MONTH) - 7;
    }
}
