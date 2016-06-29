package com.goldadorn.main.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.goldadorn.main.dj.utils.DateTimeUtils;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.response.BasicResponse;
import com.goldadorn.main.server.response.ObjectResponse;
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


    protected static void extractGetCart(ProductResponse response) throws JSONException {
        if (response.responseContent != null && response.productArray != null) {
            JSONObject cartJson = new JSONObject(response.responseContent);
            JSONArray productArray = cartJson.getJSONArray("items");
            int offset = cartJson.optInt("offset");
            //JSONArray productArray = new JSONArray(response.responseContent);
            if (productArray != null) {
                for (int i = 0; i < productArray.length(); i++) {
                    response.productArray.add(Product.extractGetCartProductList(productArray.getJSONObject(i)));
                }
            }
        }
    }

    protected static void extractGetWishlist(ProductResponse response) throws JSONException {
        if (response.responseContent != null && response.productArray != null) {
            JSONObject json = new JSONObject(response.responseContent);
            JSONArray productArray = json.getJSONArray("wishes");
            for (int i = 0; i < productArray.length(); i++) {
                response.productArray.add(new Product(productArray.getJSONObject(i).optInt("productId")));
                response.idsForProducts.put(productArray.getJSONObject(i).optInt("productId"));
            }
        }
    }

    protected static void extractBasicProfile(ObjectResponse<ProfileData> response) throws JSONException {
        if (response.responseContent != null) {
            JSONObject jsonObject = new JSONObject(response.responseContent);
            ProfileData profileData = response.object = new ProfileData();
            if (!jsonObject.isNull("fname"))
                profileData.firstName = jsonObject.getString("fname");
            if (!jsonObject.isNull("lname"))
                profileData.lastName = jsonObject.getString("lname");
            if (!jsonObject.isNull("username"))
                profileData.email = jsonObject.getString("username");
            if (!jsonObject.isNull("phone"))
                profileData.phone = jsonObject.getString("phone");
            if (jsonObject.has("profilePic")) {
                String url = jsonObject.optString("profilePic", null);
                if (url != null && !TextUtils.isEmpty(url)) {
                    url = url.replace("../", ApiFactory.IMAGE_URL_HOST);
                }
                profileData.imageUrl = url;
            }
            if (!jsonObject.isNull("address1"))
                profileData.address1 = jsonObject.getString("address1");
            if (!jsonObject.isNull("address2"))
                profileData.address2 = jsonObject.getString("address2");
            if (!jsonObject.isNull("country"))
                profileData.country = jsonObject.getString("country");
            if (!jsonObject.isNull("state"))
                profileData.state = jsonObject.getString("state");
            if (!jsonObject.isNull("city"))
                profileData.city = jsonObject.getString("city");
            if (!jsonObject.isNull("pincode"))
                profileData.pincode = jsonObject.getString("pincode");
            profileData.dob = getBdayInLong(jsonObject);
            profileData.genderType = jsonObject.isNull("gender") ? 0 : getGenderTypeFromText(jsonObject.getString("gender"));
            Log.d("dj", "genderType: " + profileData.genderType);

        }

    }


    private static int getGenderTypeFromText(String code) {

        Log.d("dj", "gender: " + code);
        if (code.equals("Female")) {
            return 1;
        } else if (code.equals("Male")) return 2;

        return 0;
    }


    private static long getBdayInLong(JSONObject jsonObject) {

        try {
            String dateFromServer = jsonObject.isNull("birthday") ? "0" : jsonObject.getString("birthday");
            return DateTimeUtils.getDateInMillis(dateFromServer);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
