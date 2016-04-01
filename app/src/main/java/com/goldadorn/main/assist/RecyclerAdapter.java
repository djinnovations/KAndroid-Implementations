package com.goldadorn.main.assist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.goldadorn.main.activities.showcase.IEnabler;

/**
 * Created by Kiran BH on 24/04/15.
 */
public abstract class RecyclerAdapter<T extends ViewHolder> extends RecyclerView.Adapter<T> implements IEnabler{
    protected boolean mEnabled = false;
    protected Context mContext;

    public RecyclerAdapter(Context context, boolean enabled) {
        mContext = context;
        mEnabled = enabled;
    }

    private MergeRecycleAdapter.IViewHolderFactory mFactory;

    public void setFactory(MergeRecycleAdapter.IViewHolderFactory factory) {
        mFactory = factory;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return (T) mFactory.onCreateViewHolder(parent, viewType);
    }

    public  void onNewViewHolder(T holder){};

    @Override
    public void setEnabled(boolean enable) {
        if (enable != mEnabled) {
            int effectiveCnt = -1;
            if (!enable)
                effectiveCnt = getItemCount();
            mEnabled = enable;
            if (enable)
                effectiveCnt = getItemCount();
            if (effectiveCnt != -1) {
                if (enable) {
                    notifyItemRangeInserted(0, effectiveCnt);
                } else {
                    notifyItemRangeRemoved(0, effectiveCnt);
                }
            } else notifyDataSetChanged();
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}