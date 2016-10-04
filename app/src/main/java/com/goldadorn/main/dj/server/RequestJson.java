package com.goldadorn.main.dj.server;

import android.util.Log;

import com.goldadorn.main.dj.model.ProductTemp;
import com.goldadorn.main.dj.utils.Constants;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by COMP on 5/6/2016.
 */
public class RequestJson {
    private static RequestJson ourInstance = new RequestJson();

    public static RequestJson getInstance() {
        return ourInstance;
    }

    private RequestJson() {
    }


    public Map<String, String> getSocialLoginReqMap(String accessToken, String socialMedia){

        try {
            Map<String, String> req = new HashMap<>();
            /*req.put(ApiKeys.CLIENT_SECRET, Constants.CLIENT_SECRET_FB);
            req.put(ApiKeys.CLIENT_ID, Constants.CLIENT_ID_FB);
            req.put(ApiKeys.GRAPH_VERSION, graphVersion);*/
            req.put(ApiKeys.ACCESS_TOKEN, accessToken);
            req.put(ApiKeys.SOCIAL_MEDIUM, socialMedia);
            Log.d("dj", "requestMap - getSocialLoginReqMap: "+req);
            return req;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public Map<String, String> getAdressCartReqMap(){

    }*/



    public static ProductTemp parseProduct(JSONObject json) {

        int productId;
        int userId;
        int collectionId;
        String productName;
        String productDescription;
        int productDefaultPrice;
        String costUnits;
        float aspectRatio;
        ProductTemp productTemp;
        double discount;

        try {
            productId = json.getInt("productId");
            userId = json.getInt("userId");
            collectionId = json.getInt("collectionId");
            productName = json.getString("productName");
            productDescription = json.getString("productName");
            productDefaultPrice = json.getInt("productDefaultPrice");
            costUnits = json.getString("costUnits");
            aspectRatio = json.getInt("aspectRatio");
            discount = json.optDouble("discount");
            productTemp = new ProductTemp(productId, userId, collectionId, productName,
                    productDescription, productDefaultPrice, costUnits,aspectRatio, discount);
            return productTemp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
