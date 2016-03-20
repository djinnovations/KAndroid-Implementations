package com.goldadorn.main.activities.showcase;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class ProductInfoFragment extends Fragment {
    private final static String TAG = ProductInfoFragment.class.getSimpleName();
    private final static boolean DEBUG = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_info,container,false);
    }
}
