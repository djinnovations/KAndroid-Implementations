package com.goldadorn.main.model;

import com.goldadorn.main.utils.ImageFilePath;

/**
 * Created by Kiran BH on 06/03/16.
 */
public class Product {
    public final int id;
    public String name, description;
    public double unitPrice;
    public static final String currency = " RS";
    public float image_a_r = 1;

    public int quantity, maxQuantity = 10;

    public Product(int id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }

    public String getDisplayPrice() {
        return currency + ". " + unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Product)
            return ((Product) o).id == id;
        else return false;
    }

    public String getImageUrl() {
        return ImageFilePath.getImageUrlForProduct(id);
    }
}
