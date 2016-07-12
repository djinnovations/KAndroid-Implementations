package com.goldadorn.main.server.response;

import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.StoneDetail;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by nithinjohn on 14/03/16.
 */
public class ProductResponse extends BasicResponse {

    public int userId = -1;
    public int collectionId = -1;
    public int productId = -1;
    public Double size = 0.0;
    public Product product;
    public ProductInfo info;
    public ProductOptions options;

    public JSONArray idsForProducts = new JSONArray();
    //for get cart api and wishlist
    public final ArrayList<Product> productArray;
    public final boolean writeToDb;

    public float metalWeight=0,metalrate=0;
    public float productmaking_charges=0;
    public int primaryMetalPurity=-1;
    public String primaryMetal="";
    public String primaryMetalColor="";
    public String priceUnits="";
    public Double stonePrice=0.0,productDefaultPrice=0.0;
    public  ArrayList<StoneDetail> stonesDetails= new ArrayList<>();
    private ProductResponse(boolean writeToDb, boolean fillList) {
        this.writeToDb = writeToDb;
        productArray = fillList ? new ArrayList<Product>() : null;
    }

    public ProductResponse() {
        this(true, false);
    }

    public static ProductResponse getAddToListResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.userId=product.userId;
        response.collectionId=product.collectionId;
        response.productId=product.productId;
        return response;
    }
    public static ProductResponse getAddToListResponseNew(Product product,ProductInfo mProductInfo,ProductOptions mProductOptions) {
        try {
            ProductResponse response = new ProductResponse();
            response.product = product;
            response.productId = product.id;
            response.metalWeight=mProductInfo.metalWeight;
            response.metalrate=mProductInfo.metalrate;
            response.productmaking_charges=mProductInfo.productmaking_charges;
            response.stonesDetails=mProductInfo.stonesDetails;
            response.productDefaultPrice=mProductInfo.productDefaultPrice;
            response.primaryMetalPurity=mProductOptions.primaryMetalPurity;
            response.primaryMetal=mProductOptions.primaryMetal;
            response.primaryMetalColor=mProductOptions.primaryMetalColor;
            response.priceUnits=mProductOptions.priceUnits;
            response.size=mProductOptions.size;
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ProductResponse getListResponse() {
        return new ProductResponse(false, true);
    }

    public static ProductResponse deleteWishlistReponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.productId=product.id;
        return response;
    }
}
