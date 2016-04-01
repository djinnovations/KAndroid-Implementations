package com.goldadorn.main.activities.showcase;


import android.annotation.SuppressLint;
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
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class ProductInfoFragment extends Fragment {
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
    private Product mProduct;

    ProductActivity mProductActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mProductActivity = (ProductActivity) getActivity();
        mProduct = mProductActivity.mProduct;
        mUser = mProductActivity.mUser;
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
        bindCollectionUI(mProductActivity.mCollection);
        bindProductInfo(mProductActivity.mProductInfo);
        mdescription.setText(mProduct.description);
    }

    @SuppressLint("StringFormatInvalid")
    public void bindProductInfo(ProductInfo summary) {
        mProductDetail.setText(getString(R.string.product_desc, summary.code,
                summary.getDisplayHeight(), summary.getDisplayWidth(),
                summary.getDisplayWeight()));
    }

    public void bindCollectionUI(Collection collection) {
        if (collection != null) {
            mCollectionName.setText(collection.name);
            mCollectionStyle.setText(collection.category);
        } else {
            mCollectionName.setVisibility(View.GONE);
            mCollectionStyle.setText("");
        }
    }
}
