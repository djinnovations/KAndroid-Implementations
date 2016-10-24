package com.goldadorn.main.activities.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.dj.adapter.WishlistAdapter;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.utils.TypefaceHelper;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class WishListManagerActivity extends BaseActivity implements ILoadingProgress {
    ProgressDialog mProgressDialog;
    private Context mContext;
    protected RecyclerView mRecyclerView;
    protected WishlistAdapter mAdapter;
   /* private View.OnClickListener mAddToCartClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Product) {//DJphy // TODO: 12-07-2016  
                Product p = (Product) v.getTag();
                UIController.addToCart(mContext, p, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(mContext, result.success ? "Added to Cart" : "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };*/
    /*private View.OnClickListener mDeleteClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v.getTag() != null && v.getTag() instanceof Product) {
                Product p = (Product) v.getTag();
                //TODO kiran delete click
                UIController.deleteFromWhishlist(mContext, p, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        if (result.success) {
                            Toast.makeText(mContext, "Product successfully deleted from Wishlist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };*/

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, WishListManagerActivity.class);
        return in;
    }


    /*private void logEventsAnalytics(String eventName) {
        ((Application) getApplication()).getFbAnalyticsInstance().logCustomEvent(this, eventName);
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: WISHLIST_VIEWED");
        logEventsAnalytics(GAAnalyticsEventNames.WISHLIST);

        TypefaceHelper.setFont(tvNone);
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = getAdapter());
        queryServer();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitleTxt());
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showLoading(true);
    }

    protected String getTitleTxt(){
        return "Wish List";
    }

    @Bind(R.id.tvNone)
    TextView tvNone;

    protected boolean isFirstTime = true;

    protected void queryServer(){
        UIController.getWishlist(mContext, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                if (result.success && result.productArray != null) {
                    if (isFirstTime){
                        isFirstTime = false;
                        updateEmptyStatus(result.productArray);
                    }
                    mAdapter.changeData(result.productArray);
                    if (!result.success) {
                        Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                showLoading(false);
            }
        });
    }

    protected void updateEmptyStatus(ArrayList jsonArray){
        if (jsonArray == null){
            mRecyclerView.setVisibility(View.GONE);
            tvNone.setVisibility(View.VISIBLE);
            return;
        }
        if (jsonArray.size() <= 0) {
            tvNone.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        else {
            tvNone.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    protected WishlistAdapter getAdapter(){
        return new WishlistAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }

    /*private class Adapter extends RecyclerView.Adapter<Adapter.WishlistViewHolder> {


    }*/

}
