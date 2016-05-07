package com.goldadorn.main.activities.cart;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
        vh.product = product;
        container.addView(vh.itemView);
        productsVh.add(vh);
        return vh;
    }

    public ProductViewHolder getItem(Product product) {
        for (ProductViewHolder vh : productsVh)
            if (product.equals(vh.product)) return vh;
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
        void onQuantityChanged(Product product);
    }


    class ProductViewHolder extends RecyclerView.ViewHolder implements TextWatcher, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        public final ImageView image;
        public final TextView name, price;
        public final EditText quantityText;
        public final View quantityChange;
        private Product product;


        public ProductViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.product_image);
            name = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.product_price);
            quantityText = (EditText) itemView.findViewById(R.id.product_quantity);
            quantityChange = itemView.findViewById(R.id.product_quantity_change);
            quantityText.addTextChangedListener(this);
            quantityChange.setOnClickListener(this);
        }

        public void bindUI(Product product) {
            name.setText(product.name);
            quantityText.removeTextChangedListener(this);
            Log.e("iiii",product.orderQty + "");
            quantityText.setText(product.orderQty + "");
            quantityText.addTextChangedListener(this);

            Picasso.with(image.getContext()).load(product.getImageUrl()).placeholder(R.mipmap.ic_launcher).fit().into(image);

            //remove 0 at end the end
            DecimalFormat format = new DecimalFormat("0.#");
            System.out.println(format.format(product.pricePaid));
            int pricee= Integer.parseInt(format.format(product.pricePaid));
            SpannableStringBuilder sbr = new SpannableStringBuilder("Rs " + (pricee * product.orderQty));
            sbr.append("/-");
            int start = sbr.length();
            sbr.append("\n").append("(").append(product.orderQty + " * " + product.pricePaid).append(")");
            int end = sbr.length();
            sbr.setSpan(new RelativeSizeSpan(0.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            price.setText(sbr);
        }
        public void remove() {
            if (popupMenu != null) {
                popupMenu.dismiss();
                popupMenu = null;
            }
            quantityChange.setOnClickListener(null);
            quantityText.removeTextChangedListener(this);
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }

        PopupMenu popupMenu;

        @Override
        public void onClick(View v) {
            if (popupMenu != null) popupMenu.dismiss();
            popupMenu = new PopupMenu(quantityChange.getContext(), quantityChange);
            Menu menu = popupMenu.getMenu();
            for (int i = 0; i < product.maxQuantity + 1; i++)
                menu.add(0, i, i, "" + i);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            changeQuantity(item.getItemId());
            return true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            changeQuantity(TextUtils.isEmpty(s) ? 0 : Integer.parseInt(s.toString()));
        }

        private void changeQuantity(int newQuantity) {
            product.orderQty = newQuantity;
            bindUI(product);
            quatityChangeListener.onQuantityChanged(product);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
