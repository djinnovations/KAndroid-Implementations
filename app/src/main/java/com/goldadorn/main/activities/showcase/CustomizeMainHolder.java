package com.goldadorn.main.activities.showcase;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.ViewHolder;
import com.rey.material.widget.CircleCheckedTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

class CustomizeMainHolder extends ViewHolder {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.extra)
        TextView extra;
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.addRemoveButton)
        ImageButton addRemoveButton;
        @Bind(R.id.separator)
        View separator;
        @Bind(R.id.extra_layout)
        LinearLayout extraLayout;

        public CustomizeMainHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    extraLayout.setVisibility(extraLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });
        }

        public void populateExtraLayout(ArrayList<String> data) {
            extraLayout.removeAllViews();
            for (String s : data) {
                View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_customize_type, extraLayout);
                TextView tv = (TextView) v.findViewById(R.id.name);
                CircleCheckedTextView image = (CircleCheckedTextView) v.findViewById(R.id.image);
                image.setText(s);
                tv.setText(s);
            }
        }
    }