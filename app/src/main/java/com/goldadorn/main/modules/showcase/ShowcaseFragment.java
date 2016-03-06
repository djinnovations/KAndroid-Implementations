package com.goldadorn.main.modules.showcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.kimeeo.library.fragments.BaseFragment;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class ShowcaseFragment extends BaseFragment {
    private final static String TAG = ShowcaseFragment.class.getSimpleName();
    private final static boolean DEBUG = true;
    public static final String EXTRA_CATEGORY_POSITION = "position";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_showcase,viewGroup,false);
    }

    @Override
    protected void garbageCollectorCall() {

    }
}
