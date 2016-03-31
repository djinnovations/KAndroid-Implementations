package com.goldadorn.main.activities.cart;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Address;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
class AddressesViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<AddressHolder> productsVh = new ArrayList<>(5);
    IResultListener<Address> resultListener;

    public AddressesViewHolder(LinearLayout itemView, IResultListener<Address> resultListener) {
        super(itemView);
        container = itemView;
        this.resultListener = resultListener;
    }

    private AddressHolder createItem(Address address) {
        AddressHolder vh = new AddressHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_cart_address, container, false));
        vh.address = address;
        container.addView(vh.itemView);
        vh.checkBox.setOnCheckedChangeListener(mCheckListener);
        productsVh.add(vh);
        return vh;
    }

    public AddressHolder getItem(Address product) {
        for (AddressHolder vh : productsVh)
            if (product.equals(vh.address)) return vh;
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

    public void setChecked(Address address) {
        for (AddressHolder vh : productsVh) {
            vh.checkBox.setOnCheckedChangeListener(null);
            vh.checkBox.setChecked(vh.address.equals(address));
            vh.checkBox.setOnCheckedChangeListener(mCheckListener);
        }

    }

    private final CheckBox.OnCheckedChangeListener mCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            for (AddressHolder vh : productsVh) {
                vh.checkBox.setOnCheckedChangeListener(null);
                vh.checkBox.setChecked(buttonView.equals(vh.checkBox) && isChecked);
                if (vh.checkBox.isChecked())
                    resultListener.onResult(vh.address);
                vh.checkBox.setOnCheckedChangeListener(mCheckListener);
            }
        }
    };

    public void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
    }

    static class AddressHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView name, detailedAddress;
        public final CheckBox checkBox;
        public final ImageView actionEdit;
        private Address address;


        public AddressHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.address_name);
            detailedAddress = (TextView) itemView.findViewById(R.id.address_detail);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            actionEdit = (ImageView) itemView.findViewById(R.id.action_edit);
            actionEdit.setOnClickListener(this);
        }

        public void bindUI(Address address) {
            name.setText(address.name);
            StringBuilder sbr = new StringBuilder();
            sbr.append(address.street).append(", ").append(address.city).append(", ").append(address.state)
                    .append(". Pin:").append(address.pincode).append("\n").append("Ph: ").append(address.phoneNumber);
            detailedAddress.setText(sbr);

        }

        public void remove() {
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }

        @Override
        public void onClick(View v) {
            ((CartManagerActivity) v.getContext()).showAddAddress(address);
        }
    }
}
