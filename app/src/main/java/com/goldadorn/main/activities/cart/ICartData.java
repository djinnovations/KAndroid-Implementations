package com.goldadorn.main.activities.cart;

import com.goldadorn.main.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kiran BH on 27/03/16.
 */
public interface ICartData {
    public void storeCartData(ArrayList<Product> cart, double costTotal);
    public List<Product> getCartProducts();
    public double getBillableAmount();
    public void setPaymentDone(boolean done);
    public boolean isPaymentDone();
}
