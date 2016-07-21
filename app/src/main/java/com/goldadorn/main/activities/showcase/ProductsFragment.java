package com.goldadorn.main.activities.showcase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
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

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.db.DbHelper;
import com.goldadorn.main.db.Tables.Products;
import com.goldadorn.main.dj.model.BookAppointmentDataObj;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.utils.IDUtils;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mikepenz.iconics.view.IconicsButton;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
            goToProductPage(mProduct);
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


    private Dialog overLayDialog;
    private Dialog overLayDialogLogo;

    private void showOverLay(String text, int colorResId, int gravity) {
        if (overLayDialog == null) {
            //WindowUtils.justPlainOverLay = true;
            overLayDialog = WindowUtils.getInstance(getContext().getApplicationContext()).displayOverlay(getActivity(), text, colorResId,
                    gravity);
        }
        overLayDialog.show();
    }

    private void dismissOverLay() {
        if (overLayDialog != null) {
            if (overLayDialog.isShowing()) {
                WindowUtils.marginForProgressViewInGrid = 5;
                // WindowUtils.justPlainOverLay = false;
                overLayDialog.dismiss();
            }
        }
    }

    private void showLogoOverLay(String text, int colorResId, int gravity) {
        if (overLayDialogLogo == null) {
            Log.d("djprod", "showLogoOverLay");
            //WindowUtils.justPlainOverLay = true;
            overLayDialogLogo = WindowUtils.getInstance(getContext().getApplicationContext()).displayOverlayLogo(getActivity(), text, colorResId,
                    gravity);
        }
        overLayDialogLogo.show();
    }

    private void dismissLogoOverLay() {
        if (overLayDialogLogo != null) {
            if (overLayDialogLogo.isShowing()) {
                WindowUtils.marginForProgressViewInGrid = 5;
                // WindowUtils.justPlainOverLay = false;
                overLayDialogLogo.dismiss();
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //showOverLay(null, R.color.Black, WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        WindowUtils.marginForProgressViewInGrid = 15;
        showLogoOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        Log.d(Constants.TAG, "prod frag");
        firstTime = true;
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, view);
        Bundle b = getArguments();
        if (b != null) {
            mMode = b.getInt(EXTRA_MODE);
            if (mMode == MODE_COLLECTION) mCollection = (Collection) b.getSerializable(EXTRA_DATA);
            else mUser = (User) b.getSerializable(EXTRA_DATA);
        }
        recyclerDrawable = ResourceReader.getInstance(Application.getInstance()).getDrawableFromResId(R.drawable.ic_action_autorenew);
        defaultBounds = tvEndView.getCompoundDrawables()[1].getBounds();
        defaultPadding = tvEndView.getCompoundDrawablePadding();
        return view;
    }


    View.OnClickListener mRefreshClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showEmptyView(false);
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            mSwipeDeckAdapter.refresh();
        }
    };

    View.OnClickListener doNothingClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        //setUserIdAndCollectionId();
        mEndView.setOnClickListener(/*new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmptyView(false);
                mSwipeDeckAdapter.refresh();
            }
        }*/      doNothingClick);

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
        canRequestServer = false;
        ProductResponse response = new ProductResponse();
        if (mMode == MODE_COLLECTION) {
            response.collectionId = mCollection.id;
            response.userId = mCollection.userId;
            response.mPageCount = 0;
            Log.d("djprod", "collectionid: " + mCollection.id + "");

        } else {
            response.userId = mUser.id;
            response.mPageCount = 0;
            Log.d("djprod", "userid: " + mUser.id + "");
        }
        UIController.getProducts(getContext(), response, resultCallBackListener);

        getLoaderManager().initLoader(mProductCallback.hashCode(), null, mProductCallback);
        //refreshData(0);
        mBuyButton.setOnClickListener(mBuyClick);
    }


    public static final int DES_COLL_ID_CALL = IDUtils.generateViewId();

    private void setNewTry() {
        WindowUtils.marginForProgressViewInGrid = 25;
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        Activity activity = getActivity();
        ExtendedAjaxCallback ajaxCallback = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("prodIds", new JSONArray().put(mProduct.id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activity instanceof ShowcaseActivity) {
            ajaxCallback = ((ShowcaseActivity) activity).getAjaxCallBackCustom(DES_COLL_ID_CALL);
        } else if (activity instanceof CollectionsActivity) {
            ajaxCallback = ((CollectionsActivity) activity).getAjaxCallBackCustom(DES_COLL_ID_CALL);
        }
        HttpEntity entity = null;
        try {
            entity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(AQuery.POST_ENTITY, entity);
        ajaxCallback.setParams(paramsMap);
        if (activity instanceof ShowcaseActivity) {
            ((ShowcaseActivity) activity).getAQueryCustom().ajax(ApiKeys.getProductsAPI(), paramsMap, String.class, ajaxCallback);
        } else if (activity instanceof CollectionsActivity) {
            ((CollectionsActivity) activity).getAQueryCustom().ajax(ApiKeys.getProductsAPI(), paramsMap, String.class, ajaxCallback);
        }
    }


    private boolean notBaaReq;

    public void continueTry(String response) {
        //Log.d("djprod", "url queried: " + ApiKeys.getProductsAPI());
        //Log.d("djprod", "response: " + response);
        int collectionid = -1;
        int desId = -1;
        try {
            JSONObject jsonObj = new JSONObject(response.toString().replace("[", "").replace("]", ""));
            Log.d("djprod", "jsonObj- after removing braces: " + response);
            try {
                if (!jsonObj.isNull("collectionId"))
                    collectionid = jsonObj.getInt("collectionId");
                if (!jsonObj.isNull("userId"))
                    desId = jsonObj.getInt("userId");
            } catch (JSONException e) {
                e.printStackTrace();
                collectionid = -1;
                desId = -1;
            }
            if (desId == -1 && collectionid == -1) {
                Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (notBaaReq)
                continueToProdScreen(collectionid, desId);
            else continueOnBAA(collectionid, desId);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }
    }

    private void continueToProdScreen(int colId, int desId) {
        dismissOverLay();
        clickedProduct.userId = desId;
        clickedProduct.collectionId = colId;
        goToProductPage(clickedProduct);
    }

    /*private void setUserIdAndCollectionId() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("prodIds", new JSONArray().put(mProduct.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, ApiKeys.getProductsAPI(),
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                JSONObject loginResponse = response;
                Log.d("djprod", "url queried: " + ApiKeys.getProductsAPI());
                //Log.d("djprod", "response: " + response);
                int collectionid = -1;
                int desId = -1;
                try {
                    JSONObject jsonObj = new JSONObject(response.toString().replace("[", "").replace("]", ""));
                    try {
                        if (!jsonObj.isNull("collectionId"))
                            collectionid = jsonObj.getInt("collectionId");
                        if (!jsonObj.isNull("userId"))
                            desId = jsonObj.getInt("userId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        collectionid = -1;
                        desId = -1;
                    }
                    if (desId == -1 && collectionid == -1) {
                        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    continueOnBAA(collectionid, desId);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("dj", "volley error: ");
                error.printStackTrace();
                Toast.makeText(getContext(), "No Network", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(loginRequest);
    }*/


    /*private static HashMap<String, String> getHeaders(Context context, ParamsBuilder paramsBuilder) {
        HashMap<String, String> headers = new HashMap<>();
        if (paramsBuilder.mResponse.mCookies != null)
            headers.put("Cookie", paramsBuilder.mResponse.mCookies);
        return headers;
    }*/

    /*protected static String getProducts(Context context, int id) throws IOException, JSONException {
        *//*if (response.mCookies == null || response.mCookies.isEmpty()) {
            response.responseCode = BasicResponse.FORBIDDEN;
            response.success = false;
            return null;
        }*//*
        if (NetworkUtilities.isConnected(context)) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.mUrlType = 2;

            urlBuilder.mResponse = response;
            ParamsBuilder paramsBuilder = new ParamsBuilder().build(response);
            paramsBuilder.mContext = context;
            paramsBuilder.mApiType = 2;

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prodIds", new JSONArray().put(id));

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            L.d("getProducts post body content " + jsonObject.toString());


            com.squareup.okhttp.Response httpResponse = ServerRequest.doPostRequest(context, ApiKeys.getProductsAPI(),
                    getHeaders(context, paramsBuilder), body);
            response.responseCode = httpResponse.code();
            response.responseContent = httpResponse.body().string();
            L.d("getProducts " + "Code :" + response.responseCode + " content", response.responseContent.toString());
            return response.responseContent.toString();
        } else {
            response.success = false;
            response.responseCode = BasicResponse.IO_EXE;
            return null;
        }
    }*/


    private void continueOnBAA(int collectionId, int desId) {
        Intent intent = new Intent(getActivity(), BookAppointment.class);
       /* Bundle bundle = new Bundle();
        bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_NAME, mProduct.name);
        bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_URL, mProduct.getImageUrl());
        bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_ID, String.valueOf(mProduct.id));
        *//*if (mMode == MODE_COLLECTION) {
            bundle.putString(IntentKeys.COLLECTION_DETAILS_NAME, mCollection.name);
            bundle.putString(IntentKeys.COLLECTION_DETAILS_ID, String.valueOf(mCollection.id));
        }*//*
        intent.putExtras(bundle);*/
        dismissOverLay();
        BookAppointmentDataObj baaDataObj = new BookAppointmentDataObj(BookAppointment.PRODUCTS);
        baaDataObj.setCollectionId(String.valueOf(collectionId))
                .setDesignerId(String.valueOf(desId))
                .setProductId(String.valueOf(mProduct.id))
                .setItemImageUrl(mProduct.getImageUrl())
                .setItemName(mProduct.name);
        intent.putExtra(IntentKeys.BOOK_APPOINT_DATA, baaDataObj);
        startActivity(intent);
    }


    private void setData() {
        Log.d("djprod", "setData");
        mProduct = mSwipeDeckAdapter.getItem(0);
        mNameText.setText(mProduct.name);
        mPriceText.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
        Log.d("djprod", "setData - price val: " + mProduct.getDisplayPrice().trim().length());
        /*if (mProduct.getDisplayPrice().trim().length() > 0)
            dismissOverLay();*/
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
    private boolean isPaginationFinishedMode;
    //private boolean isFirstTime = true;
    //private final int threshold = 2;
    private boolean firstTime;

    public void displayBookAppointment() {
        if (!canProceedToBAA()) {
            Toast.makeText(getContext(), "This is screen is not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMode == MODE_COLLECTION) {
            Log.d("djprod", "desId: " + mCollection.userId);
            Log.d("djprod", "collId: " + mCollection.id);
            continueOnBAA(mCollection.id, mCollection.userId);
        } else {
            notBaaReq = false;
            setNewTry();
        }
    }

    private boolean canProceedToBAA() {
        if (mProduct != null) {
            if (!TextUtils.isEmpty(mProduct.name) && !TextUtils.isEmpty(mProduct.getImageUrl())
                    && mProduct.id != -1) {
                return true;
            }
            return false;
        }
        return false;
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
            Glide.with(context).load(product.getImageUrl()).into(holder.image);
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
                Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
                //startActivity(PostPollActivity.getLaunchIntent(context, mProduct));
            } else if (id == R.id.wishlistButton) {
                UIController.addToWhishlist(v.getContext(), mProduct, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(getContext(), mProduct.name + " Successfully Added To Wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.i("iiii", "Click1");
                goToProductPage(mProduct);
            }
        }


        public void changeCursor(Cursor data) {
            if (canRequestServer) {
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
                    Log.d("djprod", "user id-changeCursor: " + product.userId);
                } while (data.moveToNext());
                try {
                /*if (isPaginateCall) {
                    products = getNewListToDisplay(products);
                }*/// else {
                    if (!isPaginationFinishedMode) {
                        if (DbHelper.productCountPerCall == -1) {
                            products = new ArrayList<>();
                        } else {
                            products = products.subList(0, DbHelper.productCountPerCall);
                        }
                    }
                    initialTotalProductCount = products.size();
                    Log.d("djprod", "scissored initialTotalProductCount: " + initialTotalProductCount);
                    // }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                originalProducts = new ArrayList<>(products);
                if (products.size() > 0) {
                    showEmptyView(false);
                }
                if (getCount() > 0) setData();
                Log.d("djtime", "changeCursor - loading finished-: " + System.currentTimeMillis());
                notifyDataSetChanged();
                //dismissLogoOverLay();
            }
        }


        private List<Product> getNewListToDisplay(List<Product> prodList) {
            ConcurrentLinkedQueue<Product> tempProdQueue = new ConcurrentLinkedQueue<>();
            List<Product> subList = prodList.subList(0, DbHelper.productCountPerCall);
            //boolean contains = false;
            //Log.d("djprod", "lastSeenprodID: " + lastSeenProductId);
            try {
                //tempProdList = prodList.subList(0, DbHelper.productCountPerCall);
                //tempProdList = new ConcurrentLinkedQueue<>(prodList.subList(0, prodList.size()));
                //ConcurrentLinkedQueue<Product> avoid = new ConcurrentLinkedQueue<>(pendingProductQueue.subList(0, pendingProductQueue.size()));

                /*for (Product prod : pendingProductQueue) {
                    tempProdQueue.add(prod);
                }
                for (Product product : subList) {//// TODO: 18-07-2016 need to uncomment
                    tempProdQueue.add(product);
                }*/

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

        //private int lastSeenProductId;
        private boolean isPaginateCall = false;
        private final int threshold = 0;
        //private ConcurrentLinkedQueue<Product> pendingProductQueue;

        @Override
        public void removeFirstObjectInAdapter() {
            //lastSeenProductId = products.get(0).productId;
            products.remove(0);
            notifyDataSetChanged();
            initialTotalProductCount--;
            //pendingProductQueue = new ConcurrentLinkedQueue<>(products.subList(0, products.size()));
            Log.d("djprod", "initialTotalProductCount - removeFirstObjectInAdapter: " + initialTotalProductCount);
            if (initialTotalProductCount == threshold) {
                Log.d("djprod", "new offset - threshold reached- paginate: " + (offsetMain));
                isPaginateCall = true;
                //refreshData(offsetMain);
            }
        }

        @Override
        public void onLeftCardExit(Object o) {
            if (!canProceedToBAA()) {
                Toast.makeText(getContext(), "This is screen is not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
                return;
            }

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
            if (!canProceedToBAA()) {
                Toast.makeText(getContext(), "This is screen is not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
                return;
            }
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
            if (!firstTime) {
                //showEmptyView(i == 0);
                if (i == 0) {
                    refreshData(offsetMain);
                    showEmptyView(true);
                }
                count = -1;
                //ref
            }
            if (count > 1)
                firstTime = false;
            count++;
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
            //products.addAll(originalProducts);// TODO: 18-07-2016
            //notifyDataSetChanged();
        }

        @Override
        public void onItemClicked(int i, Object o) {
            try {
                Product p = clickedProduct = originalProducts.get(originalProducts.indexOf(o));
                if (!canProceedToBAA() || p == null) {
                    Toast.makeText(getContext(), "This is screen is not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("djprod", "product name onClickImage: " + p.name);
                if (mMode == MODE_USER) {
                    notBaaReq = true;
                    setNewTry();
                } else {
                    notBaaReq = false;
                    goToProductPage(p);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "This is screen is not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
            }
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

    private Product clickedProduct;

    private int count = 0;

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
            mEndView.setVisibility(View./*INVISIBLE*/GONE);
            mDataView.setVisibility(View.VISIBLE);
            mTextDataView.setVisibility(View.VISIBLE);
        }
    }

    private void goToProductPage(Product product) {
        startActivity(ProductActivity.getLaunchIntent(getActivity(), product));
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
    @Bind(R.id.tvEndView)
    TextView tvEndView;

    Drawable recyclerDrawable;
    Rect defaultBounds;
    int defaultPadding;
    private boolean canRequestServer;

    private void refreshData(int offset) {
        ProductResponse response = new ProductResponse();
        int toCompareCount;
        try {
            toCompareCount = mMode == MODE_COLLECTION ? mCollection.productcount : mUser.products_cnt;
        } catch (Exception e) {
            e.printStackTrace();
            toCompareCount = offset;
        }
        if (toCompareCount == offset) {
            tvEndView.setCompoundDrawablePadding(defaultPadding);
            recyclerDrawable.setBounds(defaultBounds);
            tvEndView.setCompoundDrawables(null, recyclerDrawable, null, null);
            tvEndView.setText("That's all folks!");
            isPaginationFinishedMode = true;
            mEndView.setOnClickListener(mRefreshClick);
            showEmptyView(true);
        } else {
            if (canRequestServer)
                tvEndView.setCompoundDrawables(null, null, null, null);
            tvEndView.setText("Loading More...");
            mEndView.setOnClickListener(doNothingClick);
            isPaginationFinishedMode = false;
            if (mMode == MODE_USER && mUser != null) {
                response.userId = mUser.id;
                response.mPageCount = offset;
                Log.d("djprod", "userid: " + mUser.id + "");
                UIController.getProducts(getContext(), response, null);
            } else if (mCollection != null) {
                response.collectionId = mCollection.id;
                response.userId = mCollection.userId;
                response.mPageCount = offset;
                Log.d("djprod", "collectionid: " + mCollection.id + "");
                UIController.getProducts(getContext(), response, null);
            }
        }

    }


    IResultListener<ProductResponse> resultCallBackListener = new IResultListener<ProductResponse>() {
        @Override
        public void onResult(ProductResponse result) {
            dismissLogoOverLay();
            if (isAdded()) {
                Log.d("djprod", "onResult: isAdded-true");
                canRequestServer = true;
                getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            }
        }
    };


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
