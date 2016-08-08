package com.goldadorn.main.modules.showcase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.showcase.ProductActivity;
import com.goldadorn.main.activities.showcase.ProjectorViewActivity;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.Image;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.utils.ImageFilePath;
import com.goldadorn.main.utils.ImageLoaderUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
                /*Glide.with(getActivity()).load(mImageUrl).into(
                    ((ImageView) view.findViewById(R.id.image)));*/
                ImageLoaderUtils.loadImageNew(Application.getInstance()
                        , mImageUrl, (ImageView) view.findViewById(R.id.image)
                        , R.drawable.img_404_place_holder, -1);
            view.setOnClickListener(itemClick);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ProjectorViewActivity.class);
            intent.putStringArrayListExtra(IntentKeys.PROJECTOR_VIEW_IMAGES_LIST, getUrlList());
            intent.putExtra(IntentKeys.PRODUCT_NAME, ((ProductActivity) getActivity()).getProductName());
            startActivity(intent);
        }
    };

   /* @Override
    protected void garbageCollectorCall() {

    }*/

    private ArrayList<String > getUrlList(){
        return ((ProductActivity)getActivity()).mProductAdapter.getImagesUrlList();
    }

}
