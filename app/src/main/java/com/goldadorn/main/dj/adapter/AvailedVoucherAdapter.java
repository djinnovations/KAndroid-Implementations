package com.goldadorn.main.dj.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.TitlesData;
import com.goldadorn.main.dj.model.VoucherData;
import com.goldadorn.main.utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 14-11-2016.
 */
public class AvailedVoucherAdapter extends RecyclerView.Adapter<AvailedVoucherAdapter.TitlesViewHolder>{


    private List<VoucherData> titles = new ArrayList<>();

    public void changeData(List<VoucherData> titles){
        this.titles = new ArrayList<>(titles);
        notifyDataSetChanged();
    }

    @Override
    public TitlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_availed_voucher_display, parent, false);
        return new TitlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TitlesViewHolder holder, int position) {
        holder.labelDiamond.setText(titles.get(position).getDiamondCnt());
        holder.labelGoldCoin.setText(titles.get(position).getGoldCnt());
        holder.labelDiscount.setText(titles.get(position).getAvailedVoucher());
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    class TitlesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.labelGoldCoin)
        TextView labelGoldCoin;
        @Bind(R.id.labelDiamond)
        TextView labelDiamond;
        @Bind(R.id.labelDiscount)
        TextView labelDiscount;

        public TitlesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            TypefaceHelper.setFont(labelDiamond, labelGoldCoin, labelDiscount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
