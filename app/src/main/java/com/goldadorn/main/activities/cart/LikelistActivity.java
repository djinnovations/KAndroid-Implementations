package com.goldadorn.main.activities.cart;

import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.adapter.LikelistAdapter;
import com.goldadorn.main.dj.adapter.WishlistAdapter;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 20-10-2016.
 */
public class LikelistActivity extends WishListManagerActivity{

    public final String TAG = "LikelistActivity";
    private int offset = 0;

    @Override
    protected WishlistAdapter getAdapter() {
        //return super.getAdapter();
        return new LikelistAdapter();
    }

    private final int LIKE_LIST_CALL = IDUtils.generateViewId();
    @Override
    protected void queryServer() {
        //showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
//        super.queryServer();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(LIKE_LIST_CALL);
        ajaxCallback.method(AQuery.METHOD_GET);
        getAQuery().ajax((ApiKeys.getLikesAPI(String.valueOf(offset))), String.class, ajaxCallback);
    }

    @Override
    protected String getTitleTxt() {
        //return super.getTitleTxt();
        return "My Likes";
    }

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djprod", "url queried- LikelistActivity: " + url);
        Log.d("djprod", "response- LikelistActivity: " + json);
        showLoading(false);
       // dismissOverLay();
        if (id == LIKE_LIST_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mRecyclerView, this);
            if (success){
                try {
                    ArrayList temp;
                    JSONObject jsonObject = new JSONObject(json.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("likes");
                    mAdapter.changeData(temp = extractData(jsonArray));
                    if (isFirstTime){
                        isFirstTime = false;
                        updateEmptyStatus(temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else super.serverCallEnds(id, url, json, status);
    }

    private ArrayList<Product> extractData(JSONArray jsonArray){
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 0; i< jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Product product = new Product(Integer.parseInt(jsonObject.getString("productId")));
                product.likecount = jsonObject.getInt("likeCount");
                String[] dataArr = jsonObject.getString("data").trim().split(":");
                product.name = dataArr[0];
                product.desName = dataArr[1];
                product.userId = Integer.parseInt(dataArr[2]);
                product.displayPrice = dataArr[3];
                products.add(product);
            } catch (Exception e) {
                e.printStackTrace();
                return products;
            }
        }

        return products;
    }
}
