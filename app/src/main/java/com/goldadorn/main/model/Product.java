package com.goldadorn.main.model;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Product {
    public final int id;
    public String name, description, imageUrl;
    public String price;

    public int quantity;

    public Product(int id) {
        this.id = id;
    }
}
