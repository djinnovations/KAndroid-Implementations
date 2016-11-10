package com.goldadorn.main.activities.cart;

import com.goldadorn.main.dj.model.GetCartResponseObj;

/**
 * Created by User on 23-09-2016.
 */
public class MyOrdersFragment extends MyCartFragment{

    CartProductsViewHolder.CancellationListener listener = new CartProductsViewHolder.CancellationListener() {
        @Override
        public void onCancelRequest(GetCartResponseObj.ProductItem product) {
            ((MyOrdersActivity) getActivity()).sendCancelReqToServer(product);
        }
    };

    @Override
    protected boolean showCouponCode() {
        return false;
    }

    @Override
    protected boolean displayBottomBtns() {
        //return super.displayBottomBtns();
        return false;
    }

    @Override
    protected boolean getIsUseCart() {
        //return super.getIsUseCart();
        return false;
    }

    @Override
    protected boolean displayQtyHolder() {
        //return super.displayQtyHolder();
        return false;
    }

    @Override
    protected boolean getIsMyOrderScreen() {
        //return super.getIsMyOrderScreen();
        return true;
    }

    @Override
    protected boolean isDontShowInitialUi() {
        //return super.isDontShowInitialUi();
        return true;
    }

    @Override
    protected CartProductsViewHolder.CancellationListener getCancelListener() {
        return listener;
    }

    @Override
    protected String getInitialTxt() {
        return "No Order history";
    }
}
