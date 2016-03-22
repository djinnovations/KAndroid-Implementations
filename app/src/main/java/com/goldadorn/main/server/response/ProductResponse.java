package com.goldadorn.main.server.response;

import com.goldadorn.main.model.ProductDetail;

/**
 * Created by nithinjohn on 14/03/16.
 */
public class ProductResponse extends BasicResponse {

    public int userId = -1;
    public int collectionId = -1;
    public int productId = -1;
    public ProductDetail productDetail;
}
