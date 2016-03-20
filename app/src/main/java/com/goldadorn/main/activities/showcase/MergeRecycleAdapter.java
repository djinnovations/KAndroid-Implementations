package com.goldadorn.main.activities.showcase;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class MergeRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<RecyclerView.Adapter> mPieces = new ArrayList<>();
    protected IViewHolderFactory mViewHolderFactory;

    public MergeRecycleAdapter(IViewHolderFactory factory) {
        mViewHolderFactory = factory;
    }

    /**
     * Adds a new adapter to the roster of things to appear in the aggregate list.
     *
     * @param adapter Source for row views for this section
     */
    public void addAdapter(RecyclerView.Adapter adapter) {
        mPieces.add(adapter);
        adapter.registerAdapterDataObserver(new CascadeDataSetObserver(adapter));
    }

    public void addAdapter(RecyclerView.Adapter adapter, boolean atStart) {
        mPieces.add(0, adapter);
        adapter.registerAdapterDataObserver(new CascadeDataSetObserver(adapter));
    }

    public IViewHolderFactory getViewHolderFactory() {
        return mViewHolderFactory;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mViewHolderFactory.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int i) {
        for (RecyclerView.Adapter piece : mPieces) {
            int size = piece.getItemCount();
            if (i < size) {
                if (vh instanceof ViewHolder && piece instanceof RecyclerAdapter) {
                    ViewHolder holder = (ViewHolder) vh;
                    if (!holder.isInitialized) {
                        ((RecyclerAdapter) piece).onNewViewHolder(holder);
                        holder.isInitialized = true;
                    }
                }
                piece.onBindViewHolder(vh, i);
                return;
            }

            i -= size;
        }
    }

    @Override
    public int getItemCount() {
        try {
            int total = 0;
            for (RecyclerView.Adapter piece : mPieces) {
                total += piece.getItemCount();
            }
            return total;
        } catch (ConcurrentModificationException e) {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        for (RecyclerView.Adapter piece : mPieces) {
            int size = piece.getItemCount();

            if (position < size) {
                return piece.getItemId(position);
            }

            position -= size;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int i) {
        for (RecyclerView.Adapter piece : mPieces) {
            int size = piece.getItemCount();

            if (i < size) {

                return piece.getItemViewType(i);
            }

            i -= size;
        }
        return -1;
    }

    public List<RecyclerView.Adapter> getAdapters() {
        return mPieces;
    }

    public final void notifyItemRangeChanged(RecyclerView.Adapter adapter, int positionStart, int itemCount) {
        int index = mPieces.indexOf(adapter);
        if (index != -1) {
            int offset = 0;
            for (int i = 0; i < index; i++)
                offset = offset + mPieces.get(i).getItemCount();
            notifyItemRangeChanged(positionStart + offset, itemCount);
        } else notifyDataSetChanged();
    }

    public final void notifyItemRangeInserted(RecyclerView.Adapter adapter, int positionStart, int itemCount) {
        int index = mPieces.indexOf(adapter);
        if (index != -1) {
            int offset = 0;
            for (int i = 0; i < index; i++)
                offset = offset + mPieces.get(i).getItemCount();
            notifyItemRangeInserted(positionStart + offset, itemCount);
        } else notifyDataSetChanged();
    }

    public final void notifyItemRangeRemoved(RecyclerView.Adapter adapter, int positionStart, int itemCount) {
        int index = mPieces.indexOf(adapter);
        if (index != -1) {
            int offset = 0;
            for (int i = 0; i < index; i++)
                offset = offset + mPieces.get(i).getItemCount();
            notifyItemRangeRemoved(positionStart + offset, itemCount);
        } else notifyDataSetChanged();
    }

    private class CascadeDataSetObserver extends RecyclerView.AdapterDataObserver {
        RecyclerView.Adapter adapter;

        public CascadeDataSetObserver(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onChanged() {
            notifyDataSetChanged();
//            notifyItemRangeChanged(adapter,0,adapter.getItemCount());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(adapter, positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            throw new IllegalAccessError("Not implemented  onItemRangeMoved");
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(adapter, positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(adapter, positionStart, itemCount);
        }


    }

    public interface IViewHolderFactory {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewTypeconfig);
    }
}