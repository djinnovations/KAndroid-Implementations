package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.Product;

import java.util.ArrayList;


/**
 * Created by Kiran BH on 10/03/16.
 */
public class MyCartFragment extends Fragment implements CartProductsViewHolder.IQuantityChangeListener {
    CartProductsViewHolder mCartProductsViewHolder;
    private View mCartEmptyView;
    ArrayList<Product> mCart = new ArrayList<>(5);
    View mShippingContainer, mTaxContainer, mTotalContainer;
    LinearLayout mContainer_header_row;
    float mCostShipping = 200.00f, mCostTax = 420.00f, mCostTotal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mycart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCartEmptyView = view.findViewById(R.id.emptyview_cart);

        mShippingContainer = view.findViewById(R.id.container_shipping);
        mTaxContainer = view.findViewById(R.id.container_tax);
        mTotalContainer = view.findViewById(R.id.container_total);
        mContainer_header_row = (LinearLayout) view.findViewById(R.id.container_header_row);
        ((TextView) mShippingContainer.findViewById(R.id.title)).setText("Shipping");
        ((TextView) mTaxContainer.findViewById(R.id.title)).setText("Tax");
        ((TextView) mTotalContainer.findViewById(R.id.title)).setText("Total");
        ((TextView)mContainer_header_row.getChildAt(0)).setText(R.string.product);
        ((TextView)mContainer_header_row.getChildAt(1)).setText(R.string.quantity);
        SpannableStringBuilder sbr = new SpannableStringBuilder(getString(R.string.price));
        int start = sbr.length();
        sbr.append("\n").append("(quantity * unit price)");
        int end = sbr.length();
        sbr.setSpan(new RelativeSizeSpan(0.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)mContainer_header_row.getChildAt(2)).setText(sbr);


        mCartProductsViewHolder = new CartProductsViewHolder((LinearLayout) view.findViewById(R.id.container_cart), this);
        Product product = new Product(123123);
        product.name = "Gold";
        product.unitPrice = 1400;
        product.quantity = 1;
        mCart.add(product);
        product = new Product(123123);
        product.name = "Gold";
        product.unitPrice = 4900;
        product.quantity = 1;
        mCart.add(product);
        onCartChanged();
    }

    private void bindCostUi() {
        mCostTotal = 0.0f;
        int totalUnits = 0;
        for (Product p : mCart) {
            mCostTotal = mCostTotal + p.getTotalPrice();
            totalUnits = totalUnits + p.quantity;
        }
        mCostTotal = mCostTotal + mCostShipping + mCostTax;
        int t = totalUnits > 0 ? 1 : 0;
        ((TextView) mTaxContainer.findViewById(R.id.cost)).setText(Product.currency + ". " + mCostTax * t);
        ((TextView) mShippingContainer.findViewById(R.id.cost)).setText(Product.currency + ". " + mCostShipping * t);
        ((TextView) mTotalContainer.findViewById(R.id.cost)).setText(Product.currency + ". " + mCostTotal * t);
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
        bindCostUi();
        ((CartManagerActivity) getActivity()).storeCartData(mCart, mCostTotal);
    }

    @Override
    public void onQuantityChanged(Product product) {
        bindCostUi();
    }

    private Product find(int id) {
        Product t = new Product(id);
        int index = mCart.indexOf(t);
        return mCart.get(index);
    }
}
