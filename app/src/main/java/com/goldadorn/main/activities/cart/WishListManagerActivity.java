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
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class WishListManagerActivity extends FragmentActivity implements ILoadingProgress {
    ProgressDialog mProgressDialog;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, WishListManagerActivity.class);
        return in;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = new Adapter());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Wish list");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showLoading(true);
        UIController.getWishlist(mContext, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                if (result.success && result.productArray != null) {
                    mAdapter.changeData(result.productArray);
                }
                showLoading(false);
            }
        });
    }

    @Override
    public void showLoading(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<Product> products;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // todo vijith
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Product p = products.get(position);
            // todo vijith
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public void changeData(ArrayList<Product> products) {
            this.products = products;
        }
    }
}
