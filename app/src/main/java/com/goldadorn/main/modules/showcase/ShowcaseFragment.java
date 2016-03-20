package com.goldadorn.main.modules.showcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.kimeeo.library.fragments.BaseFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class ShowcaseFragment extends BaseFragment {

    private final static String TAG = ShowcaseFragment.class.getSimpleName();
    private final static boolean DEBUG = true;
    public static final String EXTRA_CATEGORY_POSITION = "position";
    public static final String EXTRA_IMAGE_URL = "extra_image";
    private static final int[] IMAGES =
            {R.drawable.rajat, R.drawable.designer, R.drawable.oxana, R.drawable.eric,
                    R.drawable.rajat, R.drawable.eric, R.drawable.oxana, R.drawable.designer};
    private static final int[] P_IMAGES =
            {R.drawable.im1, R.drawable.im2, R.drawable.im3, R.drawable.im4,
                    R.drawable.im5, R.drawable.im6, R.drawable.im7, R.drawable.im8};
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
        if (mImageUrl == null) {
            if(mPosition!=-1){
                ((ImageView) view.findViewById(R.id.image)).setImageResource(
                        P_IMAGES[mPosition%P_IMAGES.length]);
            }else
            ((ImageView) view.findViewById(R.id.image)).setImageResource(
                    IMAGES[(int) (Math.random() * 10 % IMAGES.length)]);
        }
        else Picasso.with(getActivity()).load(mImageUrl).into(
                ((ImageView) view.findViewById(R.id.image)));
    }

    @Override
    protected void garbageCollectorCall() {

    }

}
