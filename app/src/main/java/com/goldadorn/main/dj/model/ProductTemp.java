package com.goldadorn.main.dj.model;

/**
 * Created by User on 23-05-2016.
 */
public class ProductTemp {

    private int productId;
    private int userId;
    private int collectionId;
    private String productName;
    private String productDescription;
    //public static int likeCount;
    private int productDefaultPrice;
    private String costUnits;
    //private int isLiked;
    private float aspectRatio;
    private double discount;
    //private String defMetal;

    public ProductTemp(int productId, int userId, int collectionId, String productName,
                       String productDescription, int productDefaultPrice, String costUnits,
                       float aspectRatio, double discount) {
        this.productId = productId;
        this.userId = userId;
        this.collectionId = collectionId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productDefaultPrice = productDefaultPrice;
        this.costUnits = costUnits;
        this.aspectRatio = aspectRatio;
        this.discount =discount;
    }


    public int getProductId() {
        return productId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getProductDefaultPrice() {
        return productDefaultPrice;
    }

    public String getCostUnits() {
        return costUnits;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "ProductTemp{" +
                "productId=" + productId +
                ", userId=" + userId +
                ", collectionId=" + collectionId +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productDefaultPrice=" + productDefaultPrice +
                ", costUnits='" + costUnits + '\'' +
                ", discount='" + discount + '\'' +
                ", aspectRatio=" + aspectRatio +
                '}';
    }
}

