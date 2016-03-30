package com.goldadorn.main.server;

import android.content.Context;

import com.goldadorn.main.model.ProductDetail;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.ProductResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    protected static ProductDetail extractProductBasicInfo(String jsonstring) {
        if (jsonstring != null) {
            try {
                JSONObject productObj = new JSONObject(jsonstring);
                return  ProductDetail.extractBasicInfo(productObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected static ProductDetail extractProductCustomizationDetail(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject productObj = new JSONObject(jsonString);
                return ProductDetail.extractCustomization(productObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected static void extractGetCart(ProductResponse response) throws JSONException {
        if (response.responseContent != null) {
            JSONArray productArray = new JSONArray(response.responseContent);
            for (int i = 0; i < productArray.length(); i++) {
                response.productArray.add(ProductDetail.extractGetCartProductList(productArray.getJSONObject(i)));

            }

        }
    }
}
