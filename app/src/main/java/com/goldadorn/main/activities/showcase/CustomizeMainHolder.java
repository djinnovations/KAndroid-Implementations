package com.goldadorn.main.activities.showcase;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ViewHolder;
import com.rey.material.widget.CircleCheckedTextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CustomizeMainHolder extends ViewHolder {

    private final TextView name;
    private final TextView extra;
    private final ImageView image;
    private ImageButton addRemoveButton;
    private View separator;
    private LinearLayout extraLayout;

    private IResultListener<Map.Entry<String, String>> listener;
    private Map.Entry<String, ArrayList<String>> data;

    public CustomizeMainHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        extra = (TextView) itemView.findViewById(R.id.extra);
        image = (ImageView) itemView.findViewById(R.id.image);
        addRemoveButton = (ImageButton) itemView.findViewById(R.id.addRemoveButton);
        separator = itemView.findViewById(R.id.separator);
        extraLayout = (LinearLayout) itemView.findViewById(R.id.extra_layout);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extraLayout.getVisibility() == View.VISIBLE) {
                    extraLayout.setVisibility(View.GONE);
                } else {
                    extraLayout.setVisibility(View.VISIBLE);
                    populateExtraLayout();
                }
            }
        });
    }

    public void bindUI(Map.Entry<String, ArrayList<String>> option) {
        data = option;
        name.setText(option.getKey());
    }

    public void setOptionSelectedListener(IResultListener<Map.Entry<String, String>> listener) {
        this.listener = listener;
    }

    private View.OnClickListener mItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onResult(new AbstractMap.SimpleEntry<>(data.getKey(), (String) v.getTag()));
        }
    };


    private void populateExtraLayout() {
        List<String> list = data.getValue();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            View child = extraLayout.getChildAt(i);
            View v = LayoutInflater.from(extraLayout.getContext()).inflate(R.layout.item_customize_type, extraLayout, false);
            v.setTag(s);
            v.setOnClickListener(mItemClick);
            TextView tv = (TextView) v.findViewById(R.id.name);
            CircleCheckedTextView image = (CircleCheckedTextView) v.findViewById(R.id.image);
            image.setText(s);
            tv.setText(s);
            v.setVisibility(View.VISIBLE);
            if (child == null) {
                extraLayout.addView(v);
            }
        }
        if (extraLayout.getChildCount() > list.size()) {
            for (int i = list.size(); i < extraLayout.getChildCount(); i++) {
                extraLayout.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }


}