package com.goldadorn.main.model;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class PaymentMode {
    public static final int TYPE_CARD=1;
    public static final int TYPE_WALLET=2;
    public final int id;
    public final int type;
    public String name;
    public String details;

    public PaymentMode(int id, int type) {
        this.id = id;
        this.type = type;
    }
}
