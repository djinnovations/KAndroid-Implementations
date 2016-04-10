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

    //for get cart api
    public final ArrayList<Product> productArray = new ArrayList<>();

    public static ProductResponse getWishlistResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.productId = product.id;
        return response;
    }
}
