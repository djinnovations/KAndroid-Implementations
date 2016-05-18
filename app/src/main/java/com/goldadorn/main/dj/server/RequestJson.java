package com.goldadorn.main.dj.server;

import android.util.Log;

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
}
