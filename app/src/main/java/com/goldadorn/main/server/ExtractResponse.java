package com.goldadorn.main.server;

import android.content.Context;

import com.goldadorn.main.model.ProductDetail;
import com.goldadorn.main.server.response.BasicResponse;

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
                ProductDetail productDetail = new ProductDetail();
                productDetail.extractBasicInfo(productObj);
                return productDetail;
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
                ProductDetail productDetail = new ProductDetail();
                productDetail.extractCustomization(productObj);
                return productDetail;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
