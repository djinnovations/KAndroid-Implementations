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
import com.goldadorn.main.model.PaymentMode;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
class PaymentModesViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<PaymentHolder> paymentsVh = new ArrayList<>(5);
    IResultListener<Integer> resultListener;

    public PaymentModesViewHolder(LinearLayout itemView, IResultListener<Integer> resultListener) {
        super(itemView);
        container = itemView;
        this.resultListener = resultListener;
    }

    private PaymentHolder createItem(PaymentMode paymentmode) {
        PaymentHolder vh = new PaymentHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_payment_mode, null, false));
        vh.paymentId = paymentmode.id;
        container.addView(vh.itemView);
        vh.checkBox.setOnCheckedChangeListener(mCheckListener);
        paymentsVh.add(vh);
        return vh;
    }

    public PaymentHolder getItem(PaymentMode payment) {
        for (PaymentHolder vh : paymentsVh)
            if (payment.id == vh.paymentId) return vh;
        return null;
    }

    public void bindUI(ArrayList<PaymentMode> addresses) {
        for (PaymentHolder vh : paymentsVh) {
            vh.remove();
        }
        paymentsVh.clear();
        for (PaymentMode product : addresses) {
            PaymentHolder pvh = createItem(product);
            pvh.bindUI(product);
        }
    }

    public void setChecked(int addressID) {
        for (PaymentHolder vh : paymentsVh) {
            vh.checkBox.setOnCheckedChangeListener(null);
            vh.checkBox.setChecked(vh.paymentId == addressID);
            vh.checkBox.setOnCheckedChangeListener(mCheckListener);
        }

    }

    private final CheckBox.OnCheckedChangeListener mCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            for (PaymentHolder vh : paymentsVh) {
                vh.checkBox.setOnCheckedChangeListener(null);
                vh.checkBox.setChecked(buttonView.equals(vh.checkBox) && isChecked);
                if (vh.checkBox.isChecked())
                    resultListener.onResult(vh.paymentId);
                vh.checkBox.setOnCheckedChangeListener(mCheckListener);
            }
        }
    };

    public void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
    }

    static class PaymentHolder extends RecyclerView.ViewHolder {
        public final TextView name, details;
        public final CheckBox checkBox;
        public final ImageView actionEdit,icon;
        private int paymentId;


        public PaymentHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.payment_title);
            details = (TextView) itemView.findViewById(R.id.payment_description);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            actionEdit = (ImageView) itemView.findViewById(R.id.action_edit);
            icon = (ImageView) itemView.findViewById(R.id.payment_icon);
        }

        public void bindUI(PaymentMode address) {
            name.setText(address.name);
        }

        public void remove() {
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }
    }
}
