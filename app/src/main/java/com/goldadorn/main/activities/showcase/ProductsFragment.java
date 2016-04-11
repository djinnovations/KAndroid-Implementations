package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.db.Tables.Products;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class ProductsFragment extends Fragment {
    private final static String TAG = ProductsFragment.class.getSimpleName();
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_DATA = "data";
    public static final int MODE_COLLECTION = 0;
    public static final int MODE_USER = 1;
    private final static boolean DEBUG = true;
    private static final int PRODUCT = 999999999;

    @Bind(R.id.swipe_deck)
    SwipeFlingAdapterView mCardStack;


    @Bind(R.id.product_price)
    TextView mPriceText;
    @Bind(R.id.product_name)
    TextView mNameText;
    @Bind(R.id.cards_data)
    View mDataView;
    @Bind(R.id.cards_end)
    View mEndView;
    @Bind(R.id.text_data)
    View mTextDataView;

    @Bind(R.id.dislike_bg)
    View mDisLikeBg;
    @Bind(R.id.like_bg)
    View mLikeBg;

    @Bind(R.id.buyButton)
    Button mBuyButton;

    Toast mToast;

    private User mUser;
    private Collection mCollection;
    private int mMode = MODE_COLLECTION;
    private ProductCallback mProductCallback = new ProductCallback();
    private View.OnClickListener mBuyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToProductPage(v.getContext(), mProduct);
        }
    };
    private SwipeDeckAdapter mSwipeDeckAdapter;
    private Product mProduct;

    public static ProductsFragment newInstance(int mode, User user, Collection collection) {
        ProductsFragment f = new ProductsFragment();
        Bundle b = new Bundle();
        b.putInt(EXTRA_MODE, mode);
        b.putSerializable(EXTRA_DATA, mode == MODE_COLLECTION ? collection : user);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            mMode = b.getInt(EXTRA_MODE);
            if (mMode == MODE_COLLECTION) mCollection = (Collection) b.getSerializable(EXTRA_DATA);
            else mUser = (User) b.getSerializable(EXTRA_DATA);
        }
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mEndView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmptyView(false);
                mSwipeDeckAdapter.refresh();
            }
        });

        mSwipeDeckAdapter = new SwipeDeckAdapter(getActivity());
        mCardStack.setAdapter(mSwipeDeckAdapter);
        mCardStack.setFlingListener(mSwipeDeckAdapter);

        mCardStack.setOnItemClickListener(mSwipeDeckAdapter);

        if (mMode == MODE_USER) {
            ((ShowcaseActivity) getActivity()).registerUserChangeListener(mUserChangeListener);
        } else {
            ((CollectionsActivity) getActivity()).registerCollectionChangeListener(
                    mCollectionChangeListener);
        }
        getLoaderManager().initLoader(mProductCallback.hashCode(), null, mProductCallback);
        refreshData();

        mBuyButton.setOnClickListener(mBuyClick);
    }


    private void setData() {
        mProduct = mSwipeDeckAdapter.getItem(0);
        mNameText.setText(mProduct.name);
        mPriceText.setText(mProduct.getDisplayPrice());
    }

    @Override
    public void onDestroy() {
        if (mMode == MODE_USER) {
            ((ShowcaseActivity) getActivity()).unRegisterUserChangeListener(mUserChangeListener);
        } else {
            ((CollectionsActivity) getActivity()).unRegisterCollectionChangeListener(
                    mCollectionChangeListener);
        }
        getLoaderManager().destroyLoader(mProductCallback.hashCode());
        super.onDestroy();
    }


    public class SwipeDeckAdapter extends BaseAdapter implements View.OnClickListener,
            SwipeFlingAdapterView.onFlingListener, SwipeFlingAdapterView.OnItemClickListener {

        private Context context;
        private final List<Product> products = new ArrayList<>();
        private ArrayList<Product> originalProducts;

        public SwipeDeckAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Product getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ProductViewHolder holder;
            if (convertView == null) {
                holder = new ProductViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.layout_product_card,
                                parent, false));
                convertView = holder.itemview;
                convertView.setTag(holder);
                convertView.setOnClickListener(this);

                holder.like.setOnClickListener(this);
                holder.productActionsToggle.setOnClickListener(this);
                holder.shareButton.setOnClickListener(this);
                holder.buyNoBuyButton.setOnClickListener(this);
                holder.wishlistButton.setOnClickListener(this);

            } else holder = (ProductViewHolder) convertView.getTag();


            Product product = getItem(position);
            convertView.setTag(PRODUCT, product);

            holder.productActionsToggle.setTag(holder);
            holder.likesCount.setText(String.format(Locale.getDefault(), "%d", product.likecount));
            holder.like.setTag(product);
            holder.like.setSelected(product.isLiked);
            Picasso.with(context).load(product.getImageUrl()).into(holder.image);
            return convertView;
        }

        @Override
        public void onClick(final View v) {
            int id = v.getId();
            if (id == R.id.likeButton) {
                v.setEnabled(false);
                Product product = (Product) v.getTag();
                final boolean isLiked = v.isSelected();
                UIController.like(v.getContext(), product, !isLiked,
                        new IResultListener<LikeResponse>() {

                            @Override
                            public void onResult(LikeResponse result) {
                                v.setEnabled(true);
                                v.setSelected(result.success != isLiked);
                            }
                        });
            } else if (id == R.id.product_actions_open) {
                ProductViewHolder holder = (ProductViewHolder) v.getTag();
                boolean isVisible = holder.productActions.getVisibility() == View.VISIBLE;
                if (isVisible) {
                    holder.productActionsToggle.setImageResource(R.drawable.add);
                    holder.productActions.setVisibility(View.GONE);
                } else {
                    holder.productActionsToggle.setImageResource(R.drawable.close);
                    holder.productActions.setVisibility(View.VISIBLE);
                }
            } else if (id == R.id.shareButton) {
                //todo like click
                Toast.makeText(v.getContext(), "Share click!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.buyNoBuyButton) {
                startActivity(PostPollActivity.getLaunchIntent(context, mProduct));
            } else if (id == R.id.wishlistButton) {
                UIController.addToWhishlist(v.getContext(), ProductResponse.getAddToWishlistResponse(mProduct), new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(getContext(), "Added to wishlist" + result.success, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                goToProductPage(v.getContext(), mProduct);
            }
        }

        public void changeCursor(Cursor data) {
            products.clear();
            if (data != null && data.moveToFirst()) do {
                products.add(Product.extractFromCursor(data));
            } while (data.moveToNext());
            originalProducts = new ArrayList<>(products);
            if (products.size() > 0) {
                showEmptyView(false);
            }
            notifyDataSetChanged();
        }

        @Override
        public void removeFirstObjectInAdapter() {
            Product p = products.remove(0);
            notifyDataSetChanged();
        }

        @Override
        public void onLeftCardExit(Object o) {
            final Product p = (Product) o;
            UIController.like(getActivity(), p, false, new IResultListener<LikeResponse>() {
                @Override
                public void onResult(LikeResponse result) {
                    isDislikedHover(false);
                    if (result.success) {
                        p.isLiked = false;
                    }

                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getActivity(),
                            result.success ? p.name + " dis-liked" : "failed", Toast.LENGTH_LONG);
                    mToast.show();
                }
            });
            if (getCount() > 0) setData();
        }

        @Override
        public void onRightCardExit(Object o) {
            final Product p = (Product) o;
            UIController.like(getActivity(), p, true, new IResultListener<LikeResponse>() {
                @Override
                public void onResult(LikeResponse result) {
                    isLikedHover(false);
                    if (result.success) {
                        p.isLiked = true;
                    }
                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getActivity(),
                            result.success ? p.name + " liked" : "failed", Toast.LENGTH_LONG);
                    mToast.show();
                }
            });
            if (getCount() > 0) setData();
        }

        @Override
        public void onAdapterAboutToEmpty(int i) {
            showEmptyView(i == 0);
        }

        @Override
        public void onScroll(float v) {
            isDislikedHover(false);
            isLikedHover(false);
            if (v == -1.0f) {
                isDislikedHover(true);
            } else if (v == 1.0f) {
                isLikedHover(true);
            }
        }

        public void refresh() {
            products.clear();
            products.addAll(originalProducts);
            notifyDataSetChanged();
        }

        @Override
        public void onItemClicked(int i, Object o) {
            goToProductPage(context, (Product) o);
        }

        class ProductViewHolder {
            View itemview;

            @Bind(R.id.likes_count)
            TextView likesCount;
            @Bind(R.id.likeButton)
            IconicsButton like;
            @Bind(R.id.product_image)
            ImageView image;
            @Bind(R.id.product_actions_open)
            ImageButton productActionsToggle;

            @Bind(R.id.layout_product_actions)
            View productActions;
            @Bind(R.id.shareButton)
            ImageView shareButton;
            @Bind(R.id.buyNoBuyButton)
            ImageView buyNoBuyButton;
            @Bind(R.id.wishlistButton)
            ImageView wishlistButton;


            public ProductViewHolder(View itemview) {
                this.itemview = itemview;
                ButterKnife.bind(this, itemview);
            }
        }
    }

    private void isLikedHover(boolean value) {
        mLikeBg.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private void isDislikedHover(boolean value) {
        mDisLikeBg.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            mEndView.setVisibility(View.VISIBLE);
            mDataView.setVisibility(View.GONE);
            mTextDataView.setVisibility(View.GONE);
        } else {
            mEndView.setVisibility(View.GONE);
            mDataView.setVisibility(View.VISIBLE);
            mTextDataView.setVisibility(View.VISIBLE);
        }
    }

    private void goToProductPage(Context context, Product product) {
        startActivity(ProductActivity.getLaunchIntent(context, product));
    }

    private UserChangeListener mUserChangeListener = new UserChangeListener() {
        @Override
        public void onUserChange(User user) {
            mUser = user;
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            refreshData();
        }
    };
    private CollectionChangeListener mCollectionChangeListener = new CollectionChangeListener() {
        @Override
        public void onCollectionChange(Collection user) {
            mCollection = user;
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            refreshData();
        }
    };

    private void refreshData() {
        ProductResponse response = new ProductResponse();
        if (mMode == MODE_USER) {
            response.userId = mUser.id;
        } else {
            response.collectionId = mCollection.id;
            response.userId = mCollection.userId;
        }
        UIController.getProducts(getContext(), response, null);
    }

    private class ProductCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection;
            String[] selArgs;
            if (mMode == MODE_COLLECTION) {
                selection = Products.COLLECTION_ID + " = ?";
                selArgs = new String[]{String.valueOf(mCollection == null ? -1 : mCollection.id)};
            } else {
                selection = Products.USER_ID + " = ?";
                selArgs = new String[]{String.valueOf(mUser == null ? -1 : mUser.id)};
            }
            return new CursorLoader(getContext(), Products.CONTENT_URI, null, selection, selArgs,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
            mSwipeDeckAdapter.changeCursor(data);
            if (mProduct == null && mSwipeDeckAdapter.getCount() > 0) setData();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }
}
