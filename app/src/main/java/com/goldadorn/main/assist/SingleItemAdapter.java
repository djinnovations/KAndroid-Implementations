package com.goldadorn.main.assist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public class SingleItemAdapter extends RecyclerAdapter<ViewHolder> {
    private final int mViewType, mId;
    private IViewBinder mBinder;
    private ViewHolder mVH;

    public SingleItemAdapter(Context context, boolean enabled, int id, int viewType) {
        super(context, enabled);
        mId = id;
        mViewType = viewType;
    }

    public SingleItemAdapter setViewBinder(IViewBinder binder) {
        mBinder = binder;
        return this;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mVH = holder;
        if (mBinder != null) {
            if (!holder.isInitialized) {
                mBinder.onNewView(mId, holder);
                holder.isInitialized = true;
            }
            mBinder.onBindView(mId, holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public int getItemCount() {
        return mEnabled ? 1 : 0;
    }


    public ViewHolder getViewHolder() {
        return mVH;
    }

    public interface IViewBinder<V extends RecyclerView.ViewHolder> {
        public void onNewView(int id, V holder);

        public void onBindView(int id, V holder);
    }
}