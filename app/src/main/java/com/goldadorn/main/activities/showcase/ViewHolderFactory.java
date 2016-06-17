package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.MergeRecycleAdapter;
import com.goldadorn.main.assist.ViewHolder;

/**
 * Created by Kiran BH on 15/04/15.
 */
class ViewHolderFactory implements MergeRecycleAdapter.IViewHolderFactory {
    private final LayoutInflater inflater;

    public ViewHolderFactory(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewTypeconfig) {
        int viewType = viewTypeconfig / 10;
        viewType = viewType * 10;
        int config = viewTypeconfig % 10;
        ViewHolder vh;
        switch (viewType) {
            case TYPE.VHT_PB:
                vh = new PBViewHolder(inflater.inflate(R.layout.layout_price_breakdown, parent, false));
                break;
            case TYPE.VHT_C_BUTTON:
                vh = new ViewHolder(inflater.inflate(R.layout.item_customize_button, parent, false));
                break;
            case TYPE.VHT_C_EXTRA:
                vh = new ViewHolder(inflater.inflate(R.layout.item_customize_type, parent, false));
                break;
            case TYPE.VHT_TITLE:
                vh = new ViewHolder(inflater.inflate(R.layout.layout_cutomize_title, parent, false));
                break;
            case TYPE.VHT_C_MAIN:
                vh = new CustomizeMainHolder(inflater.inflate(R.layout.item_cutomize_main, parent, false));
                break;
            case TYPE.VHT_C_SEPARATOR:
                vh = new ViewHolder(inflater.inflate(R.layout.layout_cutomize_separator, parent, false));
                break;
            case TYPE.VHT_C_SPINNER:
                vh = new ViewHolder(inflater.inflate(R.layout.item_customize_spinner, parent, false));
                break;
            case TYPE.VHT_PBCA:
                vh = new MyViewHolderNew(inflater.inflate(R.layout.adapter_price_breakdown, parent, false));
                break;
            default:
                throw new IllegalStateException("undefined state " + viewType);
        }


        return vh;
    }

    public interface TYPE {
        int VHT_PB = 5010;
        int VHT_TITLE = 5020;
        int VHT_C_MAIN = 5030;
        int VHT_C_EXTRA = 5040;
        int VHT_C_SEPARATOR = 5050;
        int VHT_C_BUTTON = 5060;
        int VHT_C_SPINNER = 5070;
        int VHT_PBCA = 5080;
    }

}