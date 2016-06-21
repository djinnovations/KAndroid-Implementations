package com.goldadorn.main.activities.showcase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.db.Tables.Products;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
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
import java.util.concurrent.ConcurrentLinkedQueue;

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
    UpdateProductCount mCount;
    private User mUser;
    private Collection mCollection;
    private int mMode = MODE_COLLECTION;
    private ProductCallback mProductCallback = new ProductCallback();
    private View.OnClickListener mBuyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("iiii", "Click2");
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

        Log.d(Constants.TAG, "prod frag");
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

      /*  if (mMode == MODE_USER) {
            ((ShowcaseActivity) getActivity()).registerUserChangeListener(mUserChangeListener);
        } else {
            ((CollectionsActivity) getActivity()).registerCollectionChangeListener(
                    mCollectionChangeListener);
        }*/
        getLoaderManager().initLoader(mProductCallback.hashCode(), null, mProductCallback);
        refreshData(0);

        mBuyButton.setOnClickListener(mBuyClick);
    }


    private void setData() {
        mProduct = mSwipeDeckAdapter.getItem(0);
        mNameText.setText(mProduct.name);
        mPriceText.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
    }

    @Override
    public void onDestroy() {
        /*if (mMode == MODE_USER) {
            ((ShowcaseActivity) getActivity()).unRegisterUserChangeListener(mUserChangeListener);
        } else {
            ((CollectionsActivity) getActivity()).unRegisterCollectionChangeListener(
                    mCollectionChangeListener);
        }*/
        initialTotalProductCount = 0;
        //isFirstTime = true;
        getLoaderManager().destroyLoader(mProductCallback.hashCode());
        super.onDestroy();
    }


    private int initialTotalProductCount;
    //private boolean isFirstTime = true;
    private final int threshold = 2;


    public void displayBookAppointment() {

        Intent intent = new Intent(getActivity(), BookAppointment.class);
        Bundle bundle = new Bundle();
        bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_NAME, mProduct.name);
        bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_URL, mProduct.getImageUrl());
        bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_ID, String.valueOf(mProduct.id));
        /*if (mMode == MODE_COLLECTION) {
            bundle.putString(IntentKeys.COLLECTION_DETAILS_NAME, mCollection.name);
            bundle.putString(IntentKeys.COLLECTION_DETAILS_ID, String.valueOf(mCollection.id));
        }*/
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public class SwipeDeckAdapter extends BaseAdapter implements View.OnClickListener,
            SwipeFlingAdapterView.onFlingListener, SwipeFlingAdapterView.OnItemClickListener {

        private Context context;
        private List<Product> products = new ArrayList<>();
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
                holder.btnBookApoint.setOnClickListener(this);

            } else holder = (ProductViewHolder) convertView.getTag();


            Product product = getItem(position);
            convertView.setTag(PRODUCT, product);

            holder.productActionsToggle.setTag(holder);
            holder.likesCount.setText(String.format(Locale.getDefault(), "%d", product.likecount));
            holder.like.setTag(product);
            holder.like.setSelected(product.isLiked);
            Picasso.with(context).load(product.getImageUrl()).into(holder.image);
            Log.d("djprod", "imageURL: " + product.getImageUrl());
            return convertView;
        }

        @Override
        public void onClick(final View v) {
            int id = v.getId();
            if (id == R.id.btnBookApoint) {
                displayBookAppointment();
            } else if (id == R.id.likeButton) {
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
                Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.buyNoBuyButton) {
                startActivity(PostPollActivity.getLaunchIntent(context, mProduct));
            } else if (id == R.id.wishlistButton) {
                UIController.addToWhishlist(v.getContext(), mProduct, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(getContext(), mProduct.name + " Successfully Added To Wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.i("iiii", "Click1");
                goToProductPage(v.getContext(), mProduct);
            }
        }


        public void changeCursor(Cursor data) {
            Log.d("djprod", "data pulled - cursorCount: " + data.getCount());
           /* if (isFirstTime){
                isFirstTime = false;*/
            //initialTotalProductCount = data.getCount();
            offsetMain = data.getCount();
            Log.d("djprod", "offsetMain: " + offsetMain);
            // }
            products.clear();

            if (data != null && data.moveToFirst()) do {
                Product product = Product.extractFromCursor(data);
                products.add(product);
                Log.d("djprod", "product id: " + product.id);
            } while (data.moveToNext());
            try {
                if (isPaginateCall) {
                    products = getNewListToDisplay(products);
                } else {
                    products = products.subList(0, DbHelper.productCountPerCall);
                    initialTotalProductCount = products.size();
                    Log.d("djprod", "scissored initialTotalProductCount: " + initialTotalProductCount);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            originalProducts = new ArrayList<>(products);
            if (products.size() > 0) {
                showEmptyView(false);
            }
            if (getCount() > 0) setData();
            notifyDataSetChanged();
        }


        private List<Product> getNewListToDisplay(List<Product> prodList) {
            ConcurrentLinkedQueue<Product> tempProdQueue = new ConcurrentLinkedQueue<>();
            List<Product> subList = prodList.subList(0, DbHelper.productCountPerCall);
            //boolean contains = false;
            Log.d("djprod", "lastSeenprodID: " + lastSeenProductId);
            try {
                //tempProdList = prodList.subList(0, DbHelper.productCountPerCall);
                //tempProdList = new ConcurrentLinkedQueue<>(prodList.subList(0, prodList.size()));
                //ConcurrentLinkedQueue<Product> avoid = new ConcurrentLinkedQueue<>(pendingProductQueue.subList(0, pendingProductQueue.size()));
                for (Product prod : pendingProductQueue) {
                    tempProdQueue.add(prod);
                }
                for (Product product : subList) {
                    tempProdQueue.add(product);
                }
                // ArrayList<Product> toComapareList = new ArrayList<>(prodList.subList((prodList.size() -1) - threshold, prodList.size()));
                /*ArrayList<Product> toComapareList = new ArrayList<>(prodList.subList((prodList.size() -1) - threshold, prodList.size()));
                Iterator<Product> iterator = toComapareList.iterator();
                while (iterator.hasNext()){
                    Product prod = iterator.next();
                    Log.d("djprod","prodId - toCompareList: "+prod.productId);
                    if (contains)
                        tempProdList.add(0, prod);
                    if (prod.productId == lastSeenProductId){
                        contains = true;
                    }
                }*/
                //List<Product> avoidConcurrent =
                /*for (Product prod: pendingProductQueue){
                    tempProdList.add(0, prod);
                }*/
                Log.d("djprod", "final size of products to display: " + tempProdQueue.size());
                initialTotalProductCount = tempProdQueue.size();
                Log.d("djprod", "scissored initialTotalProductCount: " + initialTotalProductCount);
                return /*tempProdList*/new ArrayList(tempProdQueue);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private int lastSeenProductId;
        private boolean isPaginateCall = false;
        private ConcurrentLinkedQueue<Product> pendingProductQueue;

        @Override
        public void removeFirstObjectInAdapter() {
            lastSeenProductId = products.get(0).productId;
            products.remove(0);
            notifyDataSetChanged();
            initialTotalProductCount--;
            pendingProductQueue = new ConcurrentLinkedQueue<>(products.subList(0, products.size()));
            Log.d("djprod", "initialTotalProductCount - removeFirstObjectInAdapter: " + initialTotalProductCount);
            if (initialTotalProductCount == threshold) {
                Log.d("djprod", "new offset - threshold reached- paginate: " + (offsetMain));
                isPaginateCall = true;
                refreshData(offsetMain);
            }
        }

        @Override
        public void onLeftCardExit(Object o) {
            Log.d("djlike", "onLeftCardExit");
            final Product p = originalProducts.get(originalProducts.indexOf(o));

            /*Log.d("djlike", "product like stat pt1: " + p.isLiked);
            if (p.likeStat == 0) {

            }
            if (!p.isLiked)
                UIController.like(getActivity(), p, false, new IResultListener<LikeResponse>() {
                    @Override
                    public void onResult(LikeResponse result) {
                        isDislikedHover(false);
                        if (result.success) {
                            p.isLiked = false;
                            p.likecount--;
                        }
                        Log.d("djlike", "product like stat pt2: " + p.isLiked);
                        if (mToast != null) mToast.cancel();
                        mToast = Toast.makeText(getActivity(),
                                result.success ? p.name + " dis-liked" : "failed", Toast.LENGTH_LONG);
                        mToast.show();
                    }
                });
            else
                Toast.makeText(getContext(), "You have already dis-liked the " + p.name, Toast.LENGTH_SHORT).show();*/
            manupilateLikeStat(p, false);
            if (getCount() > 0) setData();
        }

        @Override
        public void onRightCardExit(Object o) {
            Log.d("djlike", "onRightCardExit");
            final Product p = originalProducts.get(originalProducts.indexOf(o));
            manupilateLikeStat(p, true);
            /*Log.d("djlike", "product like stat pt3: " + p.isLiked);
            if (!p.isLiked)
                UIController.like(getActivity(), p, true,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                isLikedHover(false);
                                if (result.success) {
                                    p.isLiked = true;
                                    p.likecount++;
                                }
                                Log.d("djlike", "product like stat pt4: " + p.isLiked);
                                if (mToast != null) mToast.cancel();
                                mToast = Toast.makeText(getActivity(),
                                        result.success ? p.name + " liked" : "failed", Toast.LENGTH_LONG);
                                mToast.show();
                            }
                        });
            else
                Toast.makeText(getContext(), "You have already Liked the " + p.name, Toast.LENGTH_SHORT).show();*/
            if (getCount() > 0) setData();
        }


        private void manupilateLikeStat(final Product product, boolean isLikeAction) {

            Log.d("djlike", "product like stat: startPoint: " + product.likeStat);
            Log.d("djlike", "product id: startPoint: " + product.id);
            Log.d("djlike", "product name: startPoint: " + product.name);
            if (product.likeStat == 0) {
                Log.d("djlike", "product like stat=0");
                if (isLikeAction) {
                    Log.d("djlike", "product like stat=0; likedAction " + isLikeAction);
                    UIController.like(getActivity(), product, true,
                            new IResultListener<LikeResponse>() {
                                @Override
                                public void onResult(LikeResponse result) {
                                    isLikedHover(false);
                                    if (result.success) {
                                        product.isLiked = true;
                                        product.likecount++;
                                    }
                                    if (mToast != null) mToast.cancel();
                                    mToast = Toast.makeText(getActivity(),
                                            result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                                    mToast.show();
                                }
                            });
                } else {
                    Log.d("djlike", "product like stat=0; likedAction " + isLikeAction);
                    UIController.like(getActivity(), product, false, new IResultListener<LikeResponse>() {
                        @Override
                        public void onResult(LikeResponse result) {
                            isDislikedHover(false);
                            if (result.success) {
                                product.isLiked = false;
                                product.likecount--;
                            }
                            if (mToast != null) mToast.cancel();
                            mToast = Toast.makeText(getActivity(),
                                    result.success ? product.name + " dis-liked" : "failed to update", Toast.LENGTH_LONG);
                            mToast.show();
                        }
                    });
                }
            } else if (product.likeStat == 1) {
                Log.d("djlike", "product like stat=1");
                if (isLikeAction) {
                    Log.d("djlike", "product like stat=1; likedAction " + isLikeAction);
                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getActivity(), "You have already liked this product!", Toast.LENGTH_LONG);
                    mToast.show();
                    isLikedHover(false);
                } else {
                    Log.d("djlike", "product like stat=1; likedAction " + isLikeAction);
                    UIController.like(getActivity(), product, false, new IResultListener<LikeResponse>() {
                        @Override
                        public void onResult(LikeResponse result) {
                            isDislikedHover(false);
                            if (result.success) {
                                product.isLiked = false;
                                product.likecount--;
                            }
                            if (mToast != null) mToast.cancel();
                            mToast = Toast.makeText(getActivity(),
                                    result.success ? product.name + " dis-liked" : "failed to update", Toast.LENGTH_LONG);
                            mToast.show();
                        }
                    });
                }
            } else if (product.likeStat == -1) {
                Log.d("djlike", "product like stat=-1");
                if (!isLikeAction) {
                    Log.d("djlike", "product like stat=-1; likedAction " + isLikeAction);
                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getActivity(), "You have already disliked this product!", Toast.LENGTH_LONG);
                    mToast.show();
                    isDislikedHover(false);
                } else {
                    Log.d("djlike", "product like stat=-1; likedAction " + isLikeAction);
                    UIController.like(getActivity(), product, true, new IResultListener<LikeResponse>() {
                        @Override
                        public void onResult(LikeResponse result) {
                            isLikedHover(false);
                            if (result.success) {
                                product.isLiked = false;
                                product.likecount--;
                            }
                            if (mToast != null) mToast.cancel();
                            mToast = Toast.makeText(getActivity(),
                                    result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                            mToast.show();
                        }
                    });
                }
            }
        }

        @Override
        public void onAdapterAboutToEmpty(int i) {
            Log.d("djprod", "swipedeck adapter position: " + i);
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
            Log.e("iiii", "Click4");
            Product p = originalProducts.get(originalProducts.indexOf(o));
            //goToProductPage(context, (Product) o);
            Log.d("djprod", "product name onClickImage: " + p.name);
            goToProductPage(context, p);
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
            @Bind(R.id.btnBookApoint)
            IconicsButton btnBookApoint;

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
            Log.d("djprod", "**empty**");
            mEndView.setVisibility(View.VISIBLE);
            mDataView.setVisibility(View.GONE);
            mTextDataView.setVisibility(View.GONE);
        } else {
            mEndView.setVisibility(View.INVISIBLE);
            mDataView.setVisibility(View.VISIBLE);
            mTextDataView.setVisibility(View.VISIBLE);
        }
    }

    private void goToProductPage(Context context, Product product) {
        startActivity(ProductActivity.getLaunchIntent(context, product));
    }

    /*private UserChangeListener mUserChangeListener = new UserChangeListener() {
        @Override
        public void onUserChange(User user) {
            mUser = user;
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            refreshData(0);
        }
    };
    private CollectionChangeListener mCollectionChangeListener = new CollectionChangeListener() {
        @Override
        public void onCollectionChange(Collection collection) {
            mCollection = collection;
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            refreshData(0);
        }
    };*/

    private int offsetMain;

    private void refreshData(int offset) {
        ProductResponse response = new ProductResponse();
        if (mMode == MODE_USER && mUser != null) {
            response.userId = mUser.id;
            Log.e("iii-product-", mUser.id + "");
            UIController.getProducts(getContext(), response, null);
        } else if (mCollection != null) {
            response.collectionId = mCollection.id;
            response.userId = mCollection.userId;
            response.mPageCount = offset;
            Log.e("iii-colection-", mCollection.id + "");
            UIController.getProducts(getContext(), response, null);
        }

    }

    private class ProductCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection;
            String[] selArgs;

            Log.d("djprod", "onCreateLoader");
            if (mMode == MODE_COLLECTION) {
                Log.d(Constants.TAG, "mode collection; prod frag");
                selection = Products.COLLECTION_ID + " = ?";
                selArgs = new String[]{String.valueOf(mCollection == null ? -1 : mCollection.id)};
            } else {
                Log.d(Constants.TAG, "mode prod/user; prod frag");
                selection = Products.USER_ID + " = ?";
                selArgs = new String[]{String.valueOf(mUser == null ? -1 : mUser.id)};
            }
            return new CursorLoader(getContext(), Products.CONTENT_URI, null, selection, selArgs, null);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            initialTotalProductCount = 0;
            //isFirstTime = true;
            Log.d("djprod", "onLoadFinished");
            if (cursor != null) cursor.close();
            this.cursor = data;

            mSwipeDeckAdapter.changeCursor(data);
            mCount.updateProductCounts(data.getCount());
            //((CollectionsActivity) getActivity()).dismissOverLay();
            //getLoaderManager().destroyLoader(mProductCallback.hashCode());
            //if (mProduct == null && mSwipeDeckAdapter.getCount() > 0) setData();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }

    public interface UpdateProductCount {
        public void updateProductCounts(int count);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCount = (UpdateProductCount) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onViewSelected");
        }

    }
}
