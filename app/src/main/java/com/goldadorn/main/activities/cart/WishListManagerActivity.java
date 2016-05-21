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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class WishListManagerActivity extends FragmentActivity implements ILoadingProgress {
    ProgressDialog mProgressDialog;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private View.OnClickListener mAddToCartClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof Product) {
                Product p = (Product) v.getTag();
                UIController.addToCart(mContext, p, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(mContext, result.success ? "Added to Cart" : "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    private View.OnClickListener mDeleteClick = new View.OnClickListener() {
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
    };

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, WishListManagerActivity.class);
        return in;
    }


    private void logEventsAnalytics(String eventName) {
        ((Application) getApplication()).getFbAnalyticsInstance().logCustomEvent(this, eventName);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        logEventsAnalytics(GAAnalyticsEventNames.WISHLIST);

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
                    if (!result.success) {
                        Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                showLoading(false);
            }
        });
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

    private class Adapter extends RecyclerView.Adapter<WishlistViewHolder> {

        private final ArrayList<Product> products = new ArrayList<>();

        @Override
        public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WishlistViewHolder vh = new WishlistViewHolder(getLayoutInflater().inflate(R.layout.layout_wishlist_item, parent, false)) {
            };
            vh.addToCart.setVisibility(View.INVISIBLE);
            //vh.addToCart.setOnClickListener(mAddToCartClick);
            vh.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (v.getTag() != null && v.getTag() instanceof Product) {
                        Product p = (Product) v.getTag();
                        //TODO kiran delete click
                        UIController.deleteFromWhishlist(mContext, p, new IResultListener<ProductResponse>() {
                            @Override
                            public void onResult(ProductResponse result) {
                                if (result.success) {
                                    products.remove(v.getTag());
                                    notifyDataSetChanged();
                                    Toast.makeText(mContext, "Product successfully deleted from Wishlist.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(WishlistViewHolder holder, int position) {
            Product p = products.get(position);

            holder.name.setText(p.name);
            holder.description.setText(p.description);
            Picasso.with(WishListManagerActivity.this).load(p.getImageUrl()).into(holder.image);

            holder.addToCart.setTag(p);
            holder.delete.setTag(p);
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public void changeData(ArrayList<Product> products) {
            this.products.clear();
            this.products.addAll(products);
            notifyDataSetChanged();
        }
    }

    class WishlistViewHolder extends ViewHolder {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.description)
        TextView description;
        @Bind(R.id.image)
        ImageView image;

        @Bind(R.id.addToCart)
        ImageButton addToCart;
        @Bind(R.id.delete)
        ImageButton delete;

        public WishlistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
