package com.goldadorn.main.activities.cart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.model.Address;
import com.goldadorn.main.model.Product;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
class AddressesViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<AddressHolder> productsVh = new ArrayList<>(5);

    public AddressesViewHolder(LinearLayout itemView) {
        super(itemView);
        container = itemView;
    }

    private AddressHolder createItem(Address address) {
        AddressHolder vh = new AddressHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_cart_address, null, false));
        vh.addressId = address.id;
        container.addView(vh.itemView);
        productsVh.add(vh);
        return vh;
    }

    public AddressHolder getItem(Product product) {
        for (AddressHolder vh : productsVh)
            if (product.id == vh.addressId) return vh;
        return null;
    }

    public void bindUI(ArrayList<Address> addresses) {
        for (AddressHolder vh : productsVh) {
            vh.remove();
        }
        productsVh.clear();
        for (Address product : addresses) {
            AddressHolder pvh = createItem(product);
            pvh.bindUI(product);
        }

    }

    public void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
    }

    static class AddressHolder extends RecyclerView.ViewHolder {
        public final TextView name, detailedAddress;
        public final CheckBox checkBox;
        public final ImageView actionEdit;
        private int addressId;


        public AddressHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.address_name);
            detailedAddress = (TextView) itemView.findViewById(R.id.address_detail);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            actionEdit = (ImageView) itemView.findViewById(R.id.action_edit);
        }

        public void bindUI(Address address) {
            name.setText(address.name);
        }

        public void remove() {
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }
    }
}
