package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;

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
    long mCostShipping = 0, mCostTax = 0, mCostTotal;

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
        mTaxContainer.setVisibility(View.GONE);
        mTotalContainer = view.findViewById(R.id.container_total);
        mContainer_header_row = (LinearLayout) view.findViewById(R.id.container_header_row);
        ((TextView) mShippingContainer.findViewById(R.id.title)).setText("Shipping");
        ((TextView) mTaxContainer.findViewById(R.id.title)).setText("Tax");
        ((TextView) mTotalContainer.findViewById(R.id.title)).setText("Total");
        ((TextView) mContainer_header_row.getChildAt(0)).setText(R.string.Product);
        ((TextView) mContainer_header_row.getChildAt(1)).setText(R.string.Quantity);
        SpannableStringBuilder sbr = new SpannableStringBuilder(getString(R.string.Price));
        int start = sbr.length();
        sbr.append("\n").append("(Quantity * Unit price)");
        int end = sbr.length();
//        sbr.setSpan(new RelativeSizeSpan(0.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) mContainer_header_row.getChildAt(2)).setText(sbr);


        mCartProductsViewHolder = new CartProductsViewHolder((LinearLayout) view.findViewById(R.id.container_cart), this);

        ((ILoadingProgress) getActivity()).showLoading(true);
        UIController.getCartDetails(getContext(),  new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                try {
                    if (result.success && result.productArray != null) {
                        for (int i=0;i<result.productArray.size();i++){
                            Product p=result.productArray.get(i);

                        }
                        mCart.addAll(result.productArray);
                        onCartChanged();
                    }
                    ((ILoadingProgress) getActivity()).showLoading(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindCostUi() {
        mCostTotal = 0;
        int totalUnits = 0;
        String currency = null;
        for (Product p : mCart) {
            mCostTotal = mCostTotal + p.getTotalPriceNew();
            totalUnits = totalUnits + p.orderQty;
            currency = p.priceUnit;
            Log.e("iii---",p.transid+"--"+mCostTotal+"--"+p.orderQty);
        }
        mCostTotal = mCostTotal + mCostShipping + mCostTax;
        int t = totalUnits > 0 ? 1 : 0;
        Log.e("iii---",t+"--"+mCostTotal);
        /*Madhu*/
        ((TextView) mTaxContainer.findViewById(R.id.cost)).setText("Rs "+ (mCostTax * t) + "/-");
        ((TextView) mShippingContainer.findViewById(R.id.cost)).setText("Rs " + (mCostShipping * t) + "/-");
        ((TextView) mTotalContainer.findViewById(R.id.cost)).setText("Rs " + (mCostTotal * t) + "/-");
      /*  ((TextView) mTaxContainer.findViewById(R.id.cost)).setText(currency + ". " + (mCostTax * t) + "/-");
        ((TextView) mShippingContainer.findViewById(R.id.cost)).setText(currency + ". " + (mCostShipping * t) + "/-");
        ((TextView) mTotalContainer.findViewById(R.id.cost)).setText(currency + ". " + (mCostTotal * t) + "/-");*/
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
