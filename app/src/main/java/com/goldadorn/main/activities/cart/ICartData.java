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
     void storeCartData(/*ArrayList<Product> cart,*/ long costTotal);
    public List<Product> getCartProducts();
    public long getBillableAmount();
    public void setPaymentDone(boolean done, boolean isCOD, String payMode);
    public boolean isPaymentDone();

    String getPaymentDone();
    Address getShippingAddress();
}
