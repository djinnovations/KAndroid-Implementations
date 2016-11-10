package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 08-09-2016.
 */
public class MyCartFragmentReplica extends MyCartFragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected boolean displayBottomBtns() {
        //return super.displayBottomBtns();
        return false;
    }

    @Override
    protected boolean showCouponCode() {
        return false;
    }

    @Override
    protected boolean displayQtyHolder() {
        //return super.displayQtyHolder();
        return false;
    }

    @Override
    protected boolean getIsUseCart() {
        //return super.getIsUseCart();
        return false;
    }

    @Override
    protected String getInitialTxt() {
        return "Summary";
    }
}
