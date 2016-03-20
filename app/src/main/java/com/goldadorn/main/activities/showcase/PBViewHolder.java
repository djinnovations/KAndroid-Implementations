package com.goldadorn.main.activities.showcase;

import android.view.View;
import android.widget.TextView;

import com.goldadorn.main.R;

import butterknife.Bind;
import butterknife.ButterKnife;

class PBViewHolder extends ViewHolder {

        @Bind(R.id.goldCost)
        TextView gold;
        @Bind(R.id.gemCost)
        TextView gem;
        @Bind(R.id.makingCost)
        TextView making;
        @Bind(R.id.vatCost)
        TextView vat;
        @Bind(R.id.totalCost)
        TextView total;

        public PBViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }