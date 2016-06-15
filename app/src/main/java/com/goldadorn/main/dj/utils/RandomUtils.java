package com.goldadorn.main.dj.utils;

import android.util.Log;

import com.ibm.icu.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import java.text.Format;
import java.util.Locale;

/**
 * Created by User on 13-06-2016.
 */
public class RandomUtils {

    public static String getIndianCurrencyFormat(double amount, boolean canStripTrailingZeros){

        Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        String amountScissored = StringUtils.stripStart(String.valueOf(amount), "0");
        String amtToReturn = format.format(new BigDecimal(amountScissored));

        if (!canStripTrailingZeros)
            return amtToReturn;
        /*if (amtToReturn.contains(".")){*/
            String amtArr = amtToReturn.substring(0, (amtToReturn.length() - 3));
            return amtArr;
        /*}else return amtToReturn;*/
    }
}
