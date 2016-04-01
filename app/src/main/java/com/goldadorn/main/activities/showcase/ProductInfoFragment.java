package com.goldadorn.main.activities.showcase;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductSummary;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class ProductInfoFragment extends Fragment {
    private final static String TAG = ProductInfoFragment.class.getSimpleName();
    private final static boolean DEBUG = true;
    public static final String EXTRA_PRODUCT_SUMMARY = "product_summary";
    private static final String EXTRA_PRODUCT_OWNER = "product_owner";
    private static final String EXTRA_PRODUCT_COLLECTION = "product_collection";
    private static final String EXTRA_PRODUCT = "product";
    private ProductSummary mProductSummary;

    @Bind(R.id.product_owner_name)
    TextView mProductOwnerName;
    @Bind(R.id.followButton)
    ImageView followButton;
    @Bind(R.id.collection_name)
    TextView mCollectionName;
    @Bind(R.id.collection_style_desc)
    TextView mCollectionStyle;
    @Bind(R.id.description_desc)
    TextView mdescription;
    @Bind(R.id.product_detail_desc)
    TextView mProductDetail;


    private User mUser;
    private Collection mCollection;
    private Product mProduct;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        ProductActivity activity = (ProductActivity) getActivity();
        mProductSummary =  activity.mProductSummary;
        mProduct = activity.mProduct;
        mUser = activity.mUser;
        mCollection = activity.mCollection;
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        ((TextView) view.findViewById(R.id.collection_style).findViewById(R.id.title)).setText(
                "Collection Style");
        ((TextView) view.findViewById(R.id.description).findViewById(R.id.title)).setText(
                "Description");
        ((TextView) view.findViewById(R.id.product_detail_style).findViewById(R.id.title)).setText(
                "Product Detail");

        if (mUser != null) {
            mProductOwnerName.setText(mUser.name);
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.setEnabled(false);
                    final boolean isFollowing = v.isSelected();
                    UIController.follow(getActivity(), mUser, !isFollowing,
                            new IResultListener<LikeResponse>() {
                                @Override
                                public void onResult(LikeResponse result) {
                                    v.setEnabled(false);
                                    v.setSelected(result.success != isFollowing);
                                }
                            });
                }
            });
        } else {
            view.findViewById(R.id.owner_layout).setVisibility(View.GONE);
        }
        bindCollectionUI(mCollection);
        mdescription.setText(mProduct.description);

        mProductDetail.setText(getString(R.string.product_desc, mProductSummary.code,
                mProductSummary.getDisplayHeight(), mProductSummary.getDisplayWidth(),
                mProductSummary.getDisplayWeight()));

    }

    public void bindCollectionUI(Collection collection){
        if (collection != null) {
            mCollectionName.setText(collection.name);
            mCollectionStyle.setText(mCollection.category);
        } else {
            mCollectionName.setVisibility(View.GONE);
            mCollectionStyle.setText("");
        }
    }
}
