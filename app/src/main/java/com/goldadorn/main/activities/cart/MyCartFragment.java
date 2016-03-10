package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.goldadorn.main.R;
import com.goldadorn.main.model.Product;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class MyCartFragment extends Fragment {
    CartProductsViewHolder mCartProductsViewHolder;
    private View mCartEmptyView;
    ArrayList<Product> mCart = new ArrayList<>(5);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mycart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCartEmptyView=view.findViewById(R.id.emptyview_cart);
        mCartProductsViewHolder = new CartProductsViewHolder((LinearLayout) view.findViewById(R.id.container_cart));
        Product product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        mCart.add(product);
        onCartChanged();
    }

    private void onCartChanged() {
        if (mCart.size() > 0) {
            mCartEmptyView.setVisibility(View.GONE);
            mCartProductsViewHolder.setVisibility(View.VISIBLE);
            mCartProductsViewHolder.bindUI(mCart);
        } else {
            mCartEmptyView.setVisibility(View.VISIBLE);
            mCartProductsViewHolder.setVisibility(View.GONE);
        }
    }
}
