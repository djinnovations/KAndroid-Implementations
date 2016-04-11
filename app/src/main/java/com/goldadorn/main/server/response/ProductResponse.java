package com.goldadorn.main.server.response;

import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by nithinjohn on 14/03/16.
 */
public class ProductResponse extends BasicResponse {

    public int userId = -1;
    public int collectionId = -1;
    public int productId = -1;
    public Product product;
    public ProductInfo info;
    public ProductOptions options;

    public JSONArray idsForProducts = new JSONArray();
    //for get cart api and wishlist
    public final ArrayList<Product> productArray;
    public final boolean writeToDb;

    private ProductResponse(boolean writeToDb, boolean fillList) {
        this.writeToDb = writeToDb;
        productArray = fillList ? new ArrayList<Product>() : null;
    }

    public ProductResponse() {
        this(true, false);
    }

    public static ProductResponse getAddToWishlistResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.productId = product.id;
        return response;
    }

    public static ProductResponse getListResponse() {
        return new ProductResponse(false, true);
    }
}
