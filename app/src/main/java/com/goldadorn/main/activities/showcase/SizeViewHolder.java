package com.goldadorn.main.activities.showcase;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.ViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 30-07-2016.
 */
public class SizeViewHolder extends ViewHolder {

    @Bind(R.id.image)
    ImageView ivImageExtra;
    @Bind(R.id.extra)
    TextView tvSelectExtra;
    @Bind(R.id.addRemoveButton)
    ImageView ivAddRemove;
    @Bind(R.id.name)
    TextView tvTitle;
    @Bind(R.id.extra_layout)
    View extraLayout;
    @Bind(R.id.ivDecrease)
    ImageView ivDecrease;
    @Bind(R.id.ivIncrease)
    ImageView ivIncrease;
    @Bind(R.id.tvAdapter)
    TextView tvAdapter;

    public SizeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}