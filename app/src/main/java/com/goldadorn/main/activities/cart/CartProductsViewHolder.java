package com.goldadorn.main.activities.cart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.Product;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class CartProductsViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<ProductViewHolder> productsVh = new ArrayList<>(5);

    public CartProductsViewHolder(LinearLayout itemView) {
        super(itemView);
        container = itemView;
    }

    private ProductViewHolder createItem(Product product) {
        ProductViewHolder vh = new ProductViewHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_product_in_cart, null, false));
        vh.productId = product.id;
        container.addView(vh.itemView);
        productsVh.add(vh);
        return vh;
    }

    public ProductViewHolder getItem(Product product) {
        for (ProductViewHolder vh : productsVh)
            if (product.id == vh.productId) return vh;
        return null;
    }

    public void bindUI(ArrayList<Product> cart) {
        for (ProductViewHolder vh : productsVh) {
            vh.remove();
        }
        productsVh.clear();
        for (Product product : cart) {
            ProductViewHolder pvh = createItem(product);
            pvh.bindUI(product);
        }

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final TextView name, price;
        public final EditText quantityText;
        public final View quantityChange;
        private int productId;


        public ProductViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.product_image);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);
            quantityText = (EditText) itemView.findViewById(R.id.product_quantity);
            quantityChange = itemView.findViewById(R.id.product_quantity_change);
        }

        public void bindUI(Product product) {
            name.setText(product.name);
            price.setText(product.price + " " + product.quantity * product.price);
        }

        public void remove() {
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }
    }
}
