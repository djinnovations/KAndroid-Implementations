package com.goldadorn.main.modules.likes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.kimeeo.library.listDataView.recyclerView.adapterLayout.DefaultAdapterLayoutView;

/**
 * Created by bhavinpadhiyar on 2/28/16.
 */
abstract public class FreeFlowLayout extends DefaultAdapterLayoutView
{
    protected View createRootView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_free_flow_layout_adapter_view, container, false);
    }

    protected ViewGroup createViewGroup(View rootView)
    {
        ViewGroup view = (ViewGroup) rootView.findViewById(com.kimeeo.library.R.id.viewGroup);
        return view;
    }
}
