package com.goldadorn.main.modules.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.kimeeo.library.fragments.BaseFragment;

/**
 * Created by bhavinpadhiyar on 2/19/16.
 */
public class UnderDevelopment extends BaseFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.under_development, container, false);
        return view;
    }
    protected void garbageCollectorCall()
    {

    }
}
