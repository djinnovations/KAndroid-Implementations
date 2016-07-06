package com.goldadorn.main.modules.showcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class ShowcaseFragment extends Fragment {

    private final static String TAG = ShowcaseFragment.class.getSimpleName();
    private final static boolean DEBUG = true;
    public static final String EXTRA_CATEGORY_POSITION = "position";
    public static final String EXTRA_IMAGE_URL = "extra_image";
    String mImageUrl;
    private int mPosition = -1;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Bundle b = bundle == null ? getArguments() : bundle;
        if(b!=null) {
            mImageUrl = b.getString(EXTRA_IMAGE_URL);
            mPosition = b.getInt(EXTRA_CATEGORY_POSITION);
        }
        return layoutInflater.inflate(R.layout.fragment_showcase, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (!TextUtils.isEmpty(mImageUrl))
                Picasso.with(getActivity()).load(mImageUrl).into(
                    ((ImageView) view.findViewById(R.id.image)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* @Override
    protected void garbageCollectorCall() {

    }*/

}
