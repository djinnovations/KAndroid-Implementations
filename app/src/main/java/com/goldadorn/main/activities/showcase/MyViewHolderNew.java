package com.goldadorn.main.activities.showcase;

import android.view.View;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.ViewHolder;

/**
 * Created by User on 17-06-2016.
 */
 public class MyViewHolderNew extends /*RecyclerView.*/ViewHolder {

    public TextView tvTitle, tvValue;
    public View lineView;
    public MyViewHolderNew(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvValue = (TextView) itemView.findViewById(R.id.tvValue);
        lineView = itemView.findViewById(R.id.lineView);
    }
}
