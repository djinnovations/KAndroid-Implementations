package com.goldadorn.main.activities.showcase;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.StoneDetail;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;

import java.util.List;

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

    @Bind(R.id.container_table)
    LinearLayout mTableContainer;


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
        if (summary != null) {
            mProductDetail.setText(getString(R.string.product_desc, summary.code,
                    summary.getDisplayHeight(), summary.getDisplayWidth(),
                    summary.getDisplayWeight()));
            List<StoneDetail> rows = summary.stonesDetails;
//            rows.clear();
//            StoneDetail det = new StoneDetail();
//            det.type = "a";
//            det.stoneFactor = "1";
//            det.rateunit = "Kiran";
//            rows.add(det);
//            det = new StoneDetail();
//            det.type = "a";
//            det.stoneFactor = "2";
//            rows.add(det);
//            det = new StoneDetail();
//            det.type = "a";
//            det.stoneFactor = "3";
//            rows.add(det);
//            det = new StoneDetail();
//            det.type = "b";
//            det.stoneFactor = "1";
//            rows.add(det);
//            det = new StoneDetail();
//            det.type = "b";
//            det.stoneFactor = "2";
//            rows.add(det);
            if (rows.size() == 0) {
                mTableContainer.setVisibility(View.GONE);
            } else {
                mTableContainer.setVisibility(View.VISIBLE);
                TableRowHolder head = new TableRowHolder(mTableContainer.findViewById(R.id.heading));
                head.itemView.setBackgroundColor(Color.RED);
                head.component.setText("Component");
                head.rate.setText("Rate");
                head.weight.setText("Weight");
                head.price.setText("Value");
                head.offer_price.setText("Offer Price");

                int childPos = 1;
                int pos = 0;
                String type = "";
                do {
                    StoneDetail detail = rows.get(pos);
                    View child = mTableContainer.getChildAt(childPos);
                    View v;
                    if (detail.type.equals(type)) {
                        v = getRowView(detail, child, mTableContainer);
                        pos++;
                    } else {
                        v = getRowView(type = detail.type, child, mTableContainer);
                    }
                    childPos++;
                    v.setVisibility(View.VISIBLE);
                    if (child == null) {
                        mTableContainer.addView(v);
                    }
                } while (pos < rows.size());


                if (mTableContainer.getChildCount() > childPos) {
                    for (int i = childPos; i < mTableContainer.getChildCount(); i++) {
                        mTableContainer.getChildAt(i).setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private View getRowView(Object obj, View convertView, LinearLayout parent) {
        TableRowHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            convertView = mInflater.inflate(R.layout.table_row, parent, false);
            holder = new TableRowHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TableRowHolder) convertView.getTag();
        }
        if (obj instanceof StoneDetail) {
            StoneDetail detail = (StoneDetail) obj;
            holder.component.setText(detail.stoneFactor);
            holder.rate.setText(detail.rateunit + " " + detail.price);
            holder.weight.setText(detail.weightunit + " " + detail.weight);
            holder.price.setText(detail.rateunit + " " + detail.price);
            holder.offer_price.setText(detail.rateunit + " " + detail.price);
        } else {
            holder.component.setText((String) obj);
            holder.rate.setText("");
            holder.weight.setText("");
            holder.price.setText("");
            holder.offer_price.setText("");

        }
        return holder.itemView;
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

    public static class TableRowHolder {
        final View itemView;
        final TextView component, rate, weight, price, offer_price;

        public TableRowHolder(View container) {
            itemView = container;
            component = (TextView) container.findViewById(R.id.colom_component);
            rate = (TextView) container.findViewById(R.id.colom_rate);
            weight = (TextView) container.findViewById(R.id.colom_weight);
            price = (TextView) container.findViewById(R.id.colom_price);
            offer_price = (TextView) container.findViewById(R.id.colom_offer_price);
        }
    }
}
