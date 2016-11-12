package com.goldadorn.main.dj.model;

/**
 * Created by User on 12-11-2016.
 */
public class TitlesData {

    private String couponTxt;
    private String couponDesc;
    private int viewType;
    public final static int COUPON_VIEW = 5678;
    public final static int COUPON_DESC_VIEW = 5688;

    public TitlesData(String couponTxt, String couponDesc/*, int viewType*/) {
        this.couponTxt = couponTxt;
        this.couponDesc = couponDesc;
        //this.viewType = viewType;
    }


    public String getCouponTxt() {
        return couponTxt;
    }

    public void setCouponTxt(String couponTxt) {
        this.couponTxt = couponTxt;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
