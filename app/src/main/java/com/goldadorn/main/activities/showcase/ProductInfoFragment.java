package com.goldadorn.main.activities.showcase;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.collection_style).findViewById(R.id.title)).setText("Collection Style");
        ((TextView)view.findViewById(R.id.description).findViewById(R.id.title)).setText("Description");
        ((TextView)view.findViewById(R.id.product_detail_style).findViewById(R.id.title)).setText("Product Detail");
    }
}
