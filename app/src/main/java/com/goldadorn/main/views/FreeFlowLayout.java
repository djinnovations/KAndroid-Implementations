package com.goldadorn.main.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.kimeeo.library.listDataView.recyclerView.adapterLayout.IAdapterLayoutView;
import com.liangfeizc.flowlayout.FlowLayout;

/**
 * Created by bhavinpadhiyar on 2/28/16.
 */
public class FreeFlowLayout extends FlowLayout implements IAdapterLayoutView {
    private AdapterLayoutDelegate mAdapterLayoutDelegate;

    public FreeFlowLayout(Context context) {
        super(context);
    }

    public FreeFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mAdapterLayoutDelegate == null) {
            mAdapterLayoutDelegate = new AdapterLayoutDelegate(this);
        }
        mAdapterLayoutDelegate.setAdapter(adapter);
    }

    @Nullable
    public RecyclerView.Adapter getAdapter() {
        if (mAdapterLayoutDelegate != null) {
            return mAdapterLayoutDelegate.getAdapter();
        }
        return null;
    }

    @Nullable
    public RecyclerView.ViewHolder getViewHolderAt(int index) {
        return mAdapterLayoutDelegate.getViewHolderAt(index);
    }
}
