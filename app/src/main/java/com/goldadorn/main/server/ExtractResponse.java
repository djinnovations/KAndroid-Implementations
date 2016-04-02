package com.goldadorn.main.server;

import android.content.Context;

import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.ProductResponse;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by nithinjohn on 12/03/16.
 */
public class ExtractResponse {

    protected static void extractBasicResponse(Context context, BasicResponse response) throws JSONException {
        if (response.responseCode == BasicResponse.OKAY) {
            response.success = true;
//            if (response.responseContent != null) {
//                JSONObject content = new JSONObject(response.responseContent);
//                if (content.has(Constants.JsonConstants.ERROR)) {
//                    response.success = false;
//                    response.customErrorCode = content.getInt(Constants.JsonConstants.CODE);
//                    response.customErrorMessage = content.getString(Constants.JsonConstants.MESSAGE);
//                } else {
//                    response.success = true;
//                }
//            } else {
//                response.success = false;
//            }
        } else
            response.success = false;
    }


    protected static void extractGetCart(ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray productArray = new JSONArray(response.responseContent);
            for (int i = 0; i < productArray.length(); i++) {
                response.productArray.add(Product.extractGetCartProductList(productArray.getJSONObject(i)));

            }

        }
    }
}
