package com.goldadorn.main.dj.model;

/**
 * Created by User on 14-11-2016.
 */
public class VoucherData {

    private String goldCnt;
    private String diamondCnt;
    private String availedVoucher;

    public VoucherData(String goldCnt, String diamondCnt, String availedVoucher) {
        this.goldCnt = goldCnt;
        this.diamondCnt = diamondCnt;
        this.availedVoucher = availedVoucher;
    }


    public String getAvailedVoucher() {
        return availedVoucher;
    }

    public void setAvailedVoucher(String availedVoucher) {
        this.availedVoucher = availedVoucher;
    }

    public String getGoldCnt() {
        return goldCnt;
    }

    public void setGoldCnt(String goldCnt) {
        this.goldCnt = goldCnt;
    }

    public String getDiamondCnt() {
        return diamondCnt;
    }

    public void setDiamondCnt(String diamondCnt) {
        this.diamondCnt = diamondCnt;
    }
}
