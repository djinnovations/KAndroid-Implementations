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
import com.payu.india.Model.StoredCard;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
class PaymentModesViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<CardHolder> paymentsVh = new ArrayList<>(5);
    IResultListener<StoredCard> resultListener;

    public PaymentModesViewHolder(LinearLayout itemView, IResultListener<StoredCard> resultListener) {
        super(itemView);
        container = itemView;
        this.resultListener = resultListener;
    }

    private CardHolder createItem(StoredCard card) {
        CardHolder vh = new CardHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_payment_mode, null, false));
        vh.card = card;
        container.addView(vh.itemView);
        vh.checkBox.setOnCheckedChangeListener(mCheckListener);
        paymentsVh.add(vh);
        return vh;
    }

    public CardHolder getItem(StoredCard card) {
        for (CardHolder vh : paymentsVh)
            if (card.equals(vh.card)) return vh;
        return null;
    }

    public void bindUI(ArrayList<StoredCard> cards) {
        for (CardHolder vh : paymentsVh) {
            vh.remove();
        }
        paymentsVh.clear();
        for (StoredCard product : cards) {
            CardHolder pvh = createItem(product);
            pvh.bindUI();
        }
    }

    public void setChecked(StoredCard card) {
        for (CardHolder vh : paymentsVh) {
            vh.checkBox.setOnCheckedChangeListener(null);
            vh.checkBox.setChecked(vh.card.equals(card));
            vh.checkBox.setOnCheckedChangeListener(mCheckListener);
        }

    }

    private final CheckBox.OnCheckedChangeListener mCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            for (CardHolder vh : paymentsVh) {
                vh.checkBox.setOnCheckedChangeListener(null);
                vh.checkBox.setChecked(buttonView.equals(vh.checkBox) && isChecked);
                if (vh.checkBox.isChecked())
                    resultListener.onResult(vh.card);
                vh.checkBox.setOnCheckedChangeListener(mCheckListener);
            }
        }
    };

    public void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
    }

    static class CardHolder extends RecyclerView.ViewHolder {
        public final TextView name, details;
        public final CheckBox checkBox;
        public final ImageView actionEdit, icon;
        private StoredCard card;


        public CardHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.payment_title);
            details = (TextView) itemView.findViewById(R.id.payment_description);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            actionEdit = (ImageView) itemView.findViewById(R.id.action_edit);
            icon = (ImageView) itemView.findViewById(R.id.payment_icon);
        }

        public void bindUI() {
            name.setText(card.getCardName());
        }

        public void remove() {
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }
    }
}
