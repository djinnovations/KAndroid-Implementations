package com.goldadorn.main.dj.model;

/**
 * Created by User on 29-07-2016.
 */
public class RecommendedProduct {

    private int productId;
    private int likeCount;
    private int isLiked;
    private int apptmtCnt;
    private int desgnId;
    private String prodName;

    public RecommendedProduct(int productId, int likeCount, int isLiked, int apptmtCnt, int desgnId) {
        this.productId = productId;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.apptmtCnt = apptmtCnt;
        this.desgnId = desgnId;
    }


    public int getDesgnId() {
        return desgnId;
    }

    public void setDesgnId(int desgnId) {
        this.desgnId = desgnId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public int getApptmtCnt() {
        return apptmtCnt;
    }

    public void setApptmtCnt(int apptmtCnt) {
        this.apptmtCnt = apptmtCnt;
    }
}
