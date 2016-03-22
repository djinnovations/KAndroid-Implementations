package com.goldadorn.main.activities.cart;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
class CartProductsViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<ProductViewHolder> productsVh = new ArrayList<>(5);
    IQuantityChangeListener quatityChangeListener;

    public CartProductsViewHolder(LinearLayout itemView, IQuantityChangeListener quatityChangeListener) {
        super(itemView);
        container = itemView;
        this.quatityChangeListener = quatityChangeListener;
    }

    private ProductViewHolder createItem(Product product) {
        ProductViewHolder vh = new ProductViewHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_product_in_cart, container, false));
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

    public void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
    }


    public interface IQuantityChangeListener {
        void onQuantityChanged(int id, int quantity);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements TextWatcher {
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
            quantityText.addTextChangedListener(this);
        }

        public void bindUI(Product product) {
            name.setText(product.name);
            quantityText.setText(product.quantity + "");
            price.setText(product.getDisplayTotal());
        }

        public void remove() {
            quantityText.removeTextChangedListener(this);
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            quatityChangeListener.onQuantityChanged(productId, Integer.parseInt(s.toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
