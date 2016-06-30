package com.payu.payuui.customutil;

/**
 * Created by User on 01-07-2016.
 */
public class GACurrencyUtil {


    private static String INDIAN_CURRENCY_SYMBOL = "₹";

    public static String getIndianCurrencyFormat(double amount, boolean canStripTrailingZeros) {

        //Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        //String amountScissored = StringUtils.stripStart(String.valueOf(amount), "0");
        //String amtToReturn = format.format(new BigDecimal(/*amountScissored*/String.valueOf(amount)));
        try {
            String amtToReturn = rupeeFormat(String.valueOf(Math.round(amount)));
            return amtToReturn;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        /*if (!canStripTrailingZeros)
            return amtToReturn;
        if (amtToReturn.contains(".")){
            String amtArr = amtToReturn.substring(0, (amtToReturn.length() - 3));
            return amtArr;
        }else return amtToReturn;*/
    }

    public static String getIndianCurrencyFormat(String amount, boolean canStripTrailingZeros) {

        try {
            return getIndianCurrencyFormat(Double.parseDouble(amount), true);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        } catch (Exception ex){
            return "";
        }
    }


    private static String rupeeFormat(String value) {
        value = value.replace(",", "");
        char lastDigit = value.charAt(value.length() - 1);
        String result = "";
        int len = value.length() - 1;
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--) {
            result = value.charAt(i) + result;
            nDigits++;
            if (((nDigits % 2) == 0) && (i > 0)) {
                result = "," + result;
            }
        }
        String indianFormat = result + lastDigit;
        return INDIAN_CURRENCY_SYMBOL + " " + indianFormat;
    }
}
