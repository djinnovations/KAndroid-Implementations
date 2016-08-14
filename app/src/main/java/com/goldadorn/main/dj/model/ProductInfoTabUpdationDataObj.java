package com.goldadorn.main.dj.model;

import com.goldadorn.main.dj.utils.RandomUtils;

/**
 * Created by User on 12-08-2016.
 */
public class ProductInfoTabUpdationDataObj {

    private int metalRate;
    private int metalPurity;
    private int makingCharges;
    //private int grandTotal;

    public int getMetalRate() {
        return metalRate;
    }

    public void setMetalRate(int metalRate) {
        this.metalRate = metalRate;
    }

    public int getMetalPurity() {
        return metalPurity;
    }

    public void setMetalPurity(int metalPurity) {
        this.metalPurity = metalPurity;
    }

    public int getMakingCharges() {
        return makingCharges;
    }

    public void setMakingCharges(int makingCharges) {
        this.makingCharges = makingCharges;
    }

    /*public int getVAT() {
        return *//*String.valueOf( *//*(int) RandomUtils.getVat(String.valueOf(grandTotal));
    }
*/
    /*public void setVAT(int VAT) {
        this.VAT = VAT;
    }*/

    /*public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }*/
}
