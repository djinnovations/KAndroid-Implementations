package com.goldadorn.main.activities.cart;

import com.goldadorn.main.model.Address;
import com.goldadorn.main.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kiran BH on 27/03/16.
 */
public interface ICartData {
    void storeAddressData(Address address);
     void storeCartData(ArrayList<Product> cart, float costTotal);
    public List<Product> getCartProducts();
    public float getBillableAmount();
    public void setPaymentDone(boolean done);
    public boolean isPaymentDone();
}
