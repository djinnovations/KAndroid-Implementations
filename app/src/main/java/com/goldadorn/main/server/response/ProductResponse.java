package com.goldadorn.main.server.response;

import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;

import java.util.ArrayList;

/**
 * Created by nithinjohn on 14/03/16.
 */
public class ProductResponse extends BasicResponse {

    public int userId = -1;
    public int collectionId = -1;
    public int productId = -1;
    public Product productToAdd;
    public ProductInfo summary;

    //for get cart api
    public final ArrayList<Product> productArray= new ArrayList<>();
}
