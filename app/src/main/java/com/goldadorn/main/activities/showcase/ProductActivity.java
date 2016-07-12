package com.goldadorn.main.activities.showcase;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.dj.fragments.FilterTimelineFragment;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ObjectAsyncLoader;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.dj.model.BookAppointmentDataObj;
import com.goldadorn.main.dj.model.FilterPostParams;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.uiutils.ViewConstructor;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.ConnectionDetector;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.OptionKey;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.User;
import com.goldadorn.main.model.OptionValue;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mikepenz.iconics.view.IconicsButton;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductActivity extends BaseDrawerActivity {
    private final static int UISTATE_CUSTOMIZE = 0;
    private final static int UISTATE_PRODUCT = 1;
    private final static int UISTATE_SOCIAL = 2;
    private static final String TAG = ProductActivity.class.getName();
    public static final String EXTRA_PRODUCT = "product";
    private int mUIState = UISTATE_CUSTOMIZE;

    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabs)
    View mTabLayout;

    @Bind(R.id.frame_no_scroll_dummy)
    View mFrameNoScrollDummy;

    @Bind(R.id.container_designer_overlay)
    FrameLayout mBrandButtonsLayout;

    @Bind(R.id.frame_content)
    FrameLayout mFrame;

    @Bind(R.id.view_pager_dummy)
    View mPagerDummy;

    @Bind(R.id.progress_frame)
    View mProgressFrame;

    private Context mContext;
    private OverlayViewHolder mOverlayVH;
    private int mStartHeight;
    private int mCollapsedHeight;
    private ProductPagerAdapter mProductAdapter;
    private int mVerticalOffset = 0;
    Product mProduct;
    private Handler mHandler = new Handler();
    private TabViewHolder mTabViewHolder;
    private CollectionCallBack mCollectionCallBack = new CollectionCallBack();
    User mUser;
    Collection mCollection;
    public ProductInfo mProductInfo;
    public ProductOptions mProductOptions;
    private ProductInfoFragment mProInfoFragment = null;

    public static Intent getLaunchIntent(Context context, Product product) {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }

    private Dialog overLayDialog;

    public void showOverLay(String text, int colorResId) {
        //if (overLayDialog == null){
        overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlayLogo(this, text, colorResId,
                WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        //}
        overLayDialog.show();
    }

    public void dismissOverLay() {
        if (overLayDialog != null) {
            if (overLayDialog.isShowing()) {
                overLayDialog.dismiss();
            }
        }
    }

    boolean isSocialFeed;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        showOverLay(null, R.color.colorPrimary);
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: PRODUCT_SCREEN");
        logEventsAnalytics(GAAnalyticsEventNames.PRODUCT_SCREEN);

        drawerLayout.setBackgroundColor(Color.WHITE);
        BitmapDrawable d = (BitmapDrawable) (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                getDrawable(R.drawable.ic_menu_black_24dp) : getResources().getDrawable(
                R.drawable.ic_menu_black_24dp));
        if (d != null) {
            d.setColorFilter(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                            getColor(R.color.colorPrimary) : getResources().getColor(R.color
                            .colorPrimary),
                    PorterDuff.Mode.SRC_IN);
            mToolBar.setNavigationIcon(d);
        }

        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (b != null) {
            mProduct = (Product) b.getSerializable(EXTRA_PRODUCT);
            if (mProduct == null) {
                finish();
                return;
            }
        }

        isSocialFeed = getIntent().getBooleanExtra(IntentKeys.CALLER_SOCIAL_FEED, false);

        /*ArrayList<String> data = new ArrayList<>(1);
        data.add(mProduct.getImageUrl());
        Log.d("djprod", "imageurlList: " + data);*/


        mContext = this;
        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout, mAppBarLayout);
        initTabs();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.8f * dm.heightPixels);
        mCollapsedHeight = (int) (.4f * dm.heightPixels);

        /*mOverlayVH.pager.setAdapter(
                mProductAdapter = new ProductPagerAdapter(getSupportFragmentManager(), data));
        mOverlayVH.indicator.setViewPager(mOverlayVH.pager);*/

        mPagerDummy.getLayoutParams().height = mStartHeight;
        mBrandButtonsLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        final int tabStart = mStartHeight - getResources().getDimensionPixelSize(
                R.dimen.tab_height) + getResources().getDimensionPixelSize(R.dimen.shadow_height);

        mTabLayout.animate().setDuration(0).y(tabStart);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mVerticalOffset != verticalOffset) {
                    Log.d(TAG, "offset : " + verticalOffset);
                    boolean change = Math.abs(verticalOffset) <= .1f * mStartHeight;
                    final int visibility = change ? View.VISIBLE : View.GONE;
                    FrameLayout.LayoutParams lp =
                            (FrameLayout.LayoutParams) mBrandButtonsLayout.getLayoutParams();
                    lp.height = change ? mStartHeight : mCollapsedHeight;
                    mBrandButtonsLayout.setLayoutParams(lp);
                    mOverlayVH.setVisisbility(visibility);
                    mTabLayout.animate().setDuration(0).yBy(verticalOffset - mVerticalOffset);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mVerticalOffset == 0) {
                                mTabLayout.animate().setDuration(0).y(tabStart);
                                mOverlayVH.setProductActions(false);
                                mOverlayVH.setVisisbility(visibility);
                            }
                        }
                    }, 180);
                }
                mVerticalOffset = verticalOffset;
            }
        });

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        bindOverlay();

        //get5NewProductDescFromServer();

        ProductResponse response = new ProductResponse();
        response.productId = mProduct.id;
        response.product = mProduct;
        UIController.getProductBasicInfo(mContext, response,
                new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        if (result.success) {
                            mProductInfo = result.info;
                            //mProductAdapter.changeData(/*mProductInfo.images*/getVariousProductLooks(mProductInfo.imageCount));
                            setAdapterForProdImages(mProductInfo.imageCount);
                            mProInfoFragment = (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_PRODUCT + "");
                            if (mProInfoFragment != null)
                                mProInfoFragment.bindProductInfo(mProductInfo);

                        }
                    }
                });
        UIController.getProductOptions(mContext, response, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                if (result.success) {
                    mProductOptions = result.options;
                    mProduct.addDefaultCustomisation(mProductOptions);
                    ProductCustomiseFragment f = (ProductCustomiseFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_CUSTOMIZE + "");
                    if (f != null)
                        f.bindProductOptions(mProductOptions);
                }
                dismissOverLay();
            }
        });
        configureUI(UISTATE_CUSTOMIZE);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                get5NewProductDescFromServer();
            }
        }, 3000);*/

        getSupportLoaderManager().initLoader(mCollectionCallBack.hashCode(), null,
                mCollectionCallBack);

        tourThisScreen();
    }


    private ArrayList<String> getVariousProductLooks(int lookcount) {
        if (lookcount == 0)
            return new ArrayList<>();
        ArrayList<String> imageUrlList = new ArrayList<>();
        String defaultUrl = mProduct.getImageUrl();
        int indexToReplace = defaultUrl.indexOf('-') + 1;
        char[] charArrOriginal = defaultUrl.toCharArray();
        for (int i = 1; i <= lookcount; i++) {
            char[] toreplace = String.valueOf(i).toCharArray();
            charArrOriginal[indexToReplace] = toreplace[0];
            imageUrlList.add(String.copyValueOf(charArrOriginal));
            Log.d("djprod", "productimageurls: " + imageUrlList.get(i - 1));
        }
        return imageUrlList;
    }

    private void setAdapterForProdImages(int lookcount) {
        mOverlayVH.pager.setAdapter(
                mProductAdapter = new ProductPagerAdapter(getSupportFragmentManager(), getVariousProductLooks(lookcount)));
        mOverlayVH.pager.setOffscreenPageLimit(lookcount);
        mOverlayVH.indicator.setViewPager(mOverlayVH.pager);
    }

    public ArrayList<String> getDescriptions() {
        return list;
    }

    @Bind(R.id.transViewProducts)
    View transViewProducts;

    private AppTourGuideHelper mTourHelper;

    private void tourThisScreen() {

        mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d(Constants.TAG, "tour product activity");
                mTourHelper.displayProductsTour(ProductActivity.this, transViewProducts);
            }
        }, 800);
    }


    public void addCustomisation(OptionKey key, OptionValue value) {
        mProduct.addCustomisation(key, value);
        // call api for updated price
        UIController.getPriceForCustomization(mContext, mProduct, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {

            }
        });
    }


    private void bindOverlay() {
        mOverlayVH.like.setSelected(mProduct.isLiked);
        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
        mOverlayVH.mProductName.setText(mProduct.name);
        mOverlayVH.mProductName2.setText(mProduct.name);
        Log.d(Constants.TAG, "user ID: " + mProduct.userId);
        mUser = UserInfoCache.getInstance(mContext).getUserInfoDB(mProduct.userId, true);
        Log.e("iiii--", mUser.id + "--" + mUser.isFollowed + "---" + mUser.followers_cnt);
        if (mUser != null) {
            String temp = "By " + mUser.getName();
            temp = temp.trim();
            mOverlayVH.mProductOwner.setText(temp);
            UiRandomUtils.underLineTv(mOverlayVH.mProductOwner, 3, mOverlayVH.mProductOwner.length());
            mOverlayVH.followButton.setTag(mUser);
            mOverlayVH.followButton.setSelected(mUser.isFollowed);
        } else {
            mOverlayVH.mProductOwner.setText("");
            mOverlayVH.followButton.setVisibility(View.GONE);
        }
        mOverlayVH.mProductCost.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
        mOverlayVH.mProductCost2.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
        mTabViewHolder.setCounts(-1, -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        //menu.findItem(R.id.nav_my_overflow).setVisible(false);
        menu.findItem(R.id.nav_my_notifications).setIcon(R.drawable.vector_icon_bell_dark);
        menu.findItem(R.id.nav_my_search).setIcon(R.drawable.vector_icon_search_dark);
        return value;
    }

    private void initTabs() {
        mTabViewHolder = new TabViewHolder(mContext, mTabLayout);
        String social = getString(R.string.social).toLowerCase();
        if (mProduct != null && !TextUtils.isEmpty(mProduct.name)) {
            social += "@";
            social += mProduct.name.toLowerCase().replace(" ", "");
        }
        mTabViewHolder.initTabs(getString(R.string.customize), getString(R.string.product_information), social,
                new TabViewHolder.TabClickListener() {
                    @Override
                    public void onTabClick(int position) {
                        configureUI(position);
                    }
                });
    }


    public String getProductDisplayPrice() {
        return mProduct.getDisplayPrice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(mCollectionCallBack.hashCode());
    }

    private void configureUI(int uiState) {
        Fragment f = null;
        int id = R.id.frame_content;
        mFrame.setVisibility(View.VISIBLE);
        mFrameNoScrollDummy.setVisibility(View.INVISIBLE);
        if (uiState == UISTATE_SOCIAL) {
            id = R.id.frame_no_scroll_dummy;
            //f = new SocialFeedFragment();
            manupilateToggle();
            FilterPostParams fpp = new FilterPostParams(("P" + String.valueOf(mProduct.id)), "0", "0");
            f = FilterTimelineFragment.newInstance(fpp);
            mFrame.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);
        } else if (uiState == UISTATE_PRODUCT) {
            mProInfoFragment = new ProductInfoFragment();
        } else {
            id = R.id.frame_no_scroll_dummy;
            f = new ProductCustomiseFragment();
            mFrame.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, f, "" + uiState);
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, mProInfoFragment, "" + uiState);
            fragmentTransaction.commit();
        }
    }


    private void manupilateToggle(){
        mAppBarLayout.setExpanded(false);
        //mOverlayVH.brandName.setVisibility(View.GONE);
        mOverlayVH.setVisisbility(View.GONE);
        /*layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.GONE);
        mProductCollection.setVisibility(View.GONE);
        mProductCost.setVisibility(View.GONE);*/
        //mTabLayout.animate().setDuration(0).scaleY(0.8f).scaleX(.8f);
    }

    /*public void addToCart() {
        Log.e("iii",mProduct.id+"");
        UIController.addToCart(mContext, mProduct,
                new IResultListener<ProductResponse>() {

                    @Override
                    public void onResult(ProductResponse result) {

                        Toast.makeText(mContext,
                                result.success ? "Added to cart successfully!" :
                                        "Adding to cart failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }*/

    public void addToCartNew(final View cartBtn) {

        if (!ConnectionDetector.getInstance(Application.getInstance()).isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
            return;
        }

        //cartBtn.setEnabled(false);
        showOverLay("Adding to cart..", R.color.colorPrimary);
        UIController.addToCartNewProduct(mContext, mProduct, mProductInfo, mProductOptions,
                new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        //logEventsAnalytics(AppEventsConstants.EVENT_NAME_ADDED_TO_CART);
                        dismissOverLay();
                        if (result == null)
                            return;
                        if (result.success) {
                            logEventsAnalytics(GAAnalyticsEventNames.CART_PRODUCT_ADDED);
                            Log.d(Constants.TAG_APP_EVENT, "AppEventLog: PRODUCT_ADDED_TO_CART");
                            confirmedToCart(cartBtn);
                        }
                    }
                });
    }


    private void confirmedToCart(final View cartBtn) {
        /*Log.d("iii","product id that was pushed to cart: "+mProduct.id);
        ViewConstructor.getInstance(getApplicationContext()).displayInfo(this, "Cart", "This item is added to your Cart!\nHow would you like to proceed?",
                "Go to Cart\nCheckout", "Continue\nShopping", true, new ViewConstructor.InfoDisplayListener() {
                    @Override
                    public void onPositiveSelection(DialogInterface alertDialog) {
                        alertDialog.dismiss();
                        menuAction(R.id.nav_cart);
                    }
                });*/

        Dialog dialog = ViewConstructor.getInstance(getApplicationContext()).displayDialog(ProductActivity.this,
                R.layout.dialog_cart_new, "Cart", "This item is added to your Cart!\nHow would you like to proceed?",
                "Go to Cart\n& Checkout", "Continue\nShopping", new ViewConstructor.DialogButtonClickListener() {
                    @Override
                    public void onPositiveBtnClicked(Dialog dialog, View btn) {
                        dialog.dismiss();
                        //cartBtn.setEnabled(true);
                        menuAction(R.id.nav_cart);
                    }

                    @Override
                    public void onNegativeBtnClicked(Dialog dialog, View btn) {
                        dialog.dismiss();
                        //cartBtn.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "You can access your cart by selecting the cart option from the Slidemenu", Toast.LENGTH_LONG).show();
                    }
                });
        dialog.show();
    }

   /* private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    public Fragment getRegisteredFragment(int fragmentPosition) {

        return registeredFragments.get(fragmentPosition);
    }*/

    private class ProductPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> images;

       /* @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d("djpager", "destroyed items FragPageAdapter");
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d("djpager", "destroyed items FragPageAdapter");
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }*/


        public ProductPagerAdapter(FragmentManager fm, ArrayList<String> data) {
            super(fm);
            images = data;
        }


        @Override
        public int getCount() {
            return images != null ? images.size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("djpager", "position- getItem() " + position);
            ShowcaseFragment f = new ShowcaseFragment();
            Bundle b = new Bundle();
            b.putString(ShowcaseFragment.EXTRA_IMAGE_URL,
                    images != null ? images.get(position) : "");
            f.setArguments(b);
            return f;
        }

        public void changeData(ArrayList<String> data) {
            images = data;
            notifyDataSetChanged();
        }

    }

    public void displayBookAppointment() {

        try {

            if (!canProceedToBAA()) {
                Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, BookAppointment.class);
           /* Bundle bundle = new Bundle();
            bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_NAME, mProduct.name);
            bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_URL, mProduct.getImageUrl());
            bundle.putString(IntentKeys.BOOK_APPOINT_DETAILS_ID, String.valueOf(mProduct.id));*/
        /*if (mMode == MODE_COLLECTION) {
            bundle.putString(IntentKeys.COLLECTION_DETAILS_NAME, mCollection.name);
            bundle.putString(IntentKeys.COLLECTION_DETAILS_ID, String.valueOf(mCollection.id));
        }*/
            BookAppointmentDataObj baaDataObj = new BookAppointmentDataObj(BookAppointment.PRODUCTS);
            baaDataObj.setCollectionId(String.valueOf(mProduct.collectionId))
                    .setDesignerId(String.valueOf(mProduct.userId))
                    .setProductId(String.valueOf(mProduct.id))
                    .setItemImageUrl(mProduct.getImageUrl())
                    .setItemName(mProduct.name);
            intent.putExtra(IntentKeys.BOOK_APPOINT_DATA, baaDataObj);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean canProceedToBAA() {
        if (mProduct != null) {
            if (!TextUtils.isEmpty(mProduct.name) && !TextUtils.isEmpty(mProduct.getImageUrl())
                    && mProduct.id != -1 && mProduct.collectionId != -1 && mProduct.userId != -1) {
                return true;
            }
            return false;
        }
        return false;
    }


    public void launchDesignerScreen() {
        /*menuAction(R.id.nav_showcase);
        finish();*/
        RandomUtils.launchDesignerScreen(this, mProduct.userId);
    }

    public void launchCollectionScreen() {
        if (isSocialFeed) {
            // showDialogInfo("Link couldn't be Established! Please visit our Showcase section", false);
            RandomUtils.launchCollectionScreen(this, mProduct.userId, mProduct.collectionId);
        } else finish();
    }

    public void showDialogInfo(String msg, boolean isPositive) {
        int color;
        color = isPositive ? R.color.colorPrimary : R.color.Red;
        WindowUtils.getInstance(getApplicationContext()).genericInfoMsgWithOK(this, null, msg, color);
    }

    class OverlayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppBarLayout appBarLayout;
        @Bind(R.id.likes_count)
        TextView likesCount;
        @Bind(R.id.likeButton)
        ImageView like;

        @Bind(R.id.product_actions_open)
        ImageButton productActionsToggle;
        @Bind(R.id.layout_product_actions)
        View productActions;
        @Bind(R.id.shareButton)
        ImageView shareButton;
        @Bind(R.id.buyNoBuyButton)
        ImageView buyNoBuyButton;
        @Bind(R.id.btnBookApoint)
        IconicsButton btnBookApoint;

        @Bind(R.id.wishlistButton)
        ImageView wishlistButton;

        @Bind(R.id.view_pager)
        ViewPager pager;
        @Bind(R.id.indicator)
        CirclePageIndicator indicator;

        @Bind(R.id.product_name)
        TextView mProductName;
        @Bind(R.id.product_owner_name)
        TextView mProductOwner;
        @Bind(R.id.followButton)
        ImageView followButton;

        @Bind(R.id.product_collection_name)
        TextView mProductCollection;
        @Bind(R.id.product_cost)
        TextView mProductCost;
        @Bind(R.id.product_name_2)
        TextView mProductName2;
        @Bind(R.id.product_cost_2)
        TextView mProductCost2;

        @Bind(R.id.layout_1)
        View layout1;
        @Bind(R.id.layout_2)
        View layout2;
        @Bind(R.id.layout_3)
        View layout3;

        @Bind(R.id.cartButton)
        IconicsButton cartButton;

        public void setProductActions(boolean productActions) {
            isProductActions = productActions;
        }

        boolean isProductActions = false;

        public OverlayViewHolder(View itemView, AppBarLayout appBarLayout) {
            super(itemView);
            this.appBarLayout = appBarLayout;
            ButterKnife.bind(this, itemView);
            productActionsToggle.setOnClickListener(this);
            shareButton.setOnClickListener(this);
            buyNoBuyButton.setOnClickListener(this);
            wishlistButton.setOnClickListener(this);
            btnBookApoint.setOnClickListener(this);
            like.setOnClickListener(this);
            cartButton.setOnClickListener(this);
            followButton.setOnClickListener(this);

            mProductOwner.setOnClickListener(this);
            mProductCollection.setOnClickListener(this);
            //RandomUtils.underLineTv(mProductOwner);
        }

        public void setVisisbility(int visibility) {
            if (!isProductActions) {
                int oppositeVisibility = View.VISIBLE == visibility ? View.GONE : View.VISIBLE;
                layout1.setVisibility(visibility);
                layout2.setVisibility(visibility);
                layout3.setVisibility(visibility);
                mProductCollection.setVisibility(visibility);
                mProductOwner.setVisibility(visibility);
                pager.setVisibility(visibility);
                indicator.setVisibility(visibility);
                productActionsToggle.setVisibility(visibility);
            }
        }

        @Override
        public void onClick(final View v) {
            if (v == productActionsToggle) {
                boolean visible = productActions.getVisibility() == View.VISIBLE;
                if (visible) {
                    appBarLayout.setExpanded(true);
                    productActions.setVisibility(View.GONE);
                    productActionsToggle.setImageResource(R.drawable.add);
                    mProductName2.setVisibility(View.GONE);
                    mProductCost2.setVisibility(View.GONE);
                    mProductName.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    mProductCollection.setVisibility(View.VISIBLE);
                    mProductCost.setVisibility(View.VISIBLE);
                    pager.animate().setDuration(0).scaleY(1f).scaleX(1f);

                } else {
                    isProductActions = true;
                    appBarLayout.setExpanded(false);
                    productActions.setVisibility(View.VISIBLE);
                    productActionsToggle.setImageResource(R.drawable.close);
                    mProductName2.setVisibility(View.VISIBLE);
                    mProductCost2.setVisibility(View.VISIBLE);
                    mProductName.setVisibility(View.GONE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    mProductCollection.setVisibility(View.GONE);
                    mProductCost.setVisibility(View.GONE);
                    pager.animate().setDuration(0).scaleY(0.8f).scaleX(.8f);
                }
            } else if (v.getId() == R.id.btnBookApoint) {
                displayBookAppointment();
            } else if (v.getId() == mProductOwner.getId()) {
                launchDesignerScreen();
            } else if (v.getId() == mProductCollection.getId()) {
                launchCollectionScreen();
            } else if (v == like) {
                v.setEnabled(false);
                final boolean isLiked = v.isSelected();
                Log.d("djprod", "isliked val: " + isLiked);
                UIController.like(v.getContext(), mProduct, !isLiked,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                v.setEnabled(true);
                                Log.d("djprod", "isliked val: " + v.isSelected());
                                v.setSelected(result.success != isLiked);
                                if (isLiked) {
                                    Log.d("djprod", "isliked - true");
                                    mProduct.likecount = mProduct.likecount - 1;
                                    mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                    // Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("djprod", "isliked - false");
                                    mProduct.likecount = mProduct.likecount + 1;
                                    mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                    //Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else if (v == shareButton) {
                //todo like click
                Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
            } else if (v == buyNoBuyButton) {
                startActivity(PostPollActivity.getLaunchIntent(mContext, mProduct));
            } else if (v == wishlistButton) {
                UIController.addToWhishlist(v.getContext(), mProduct, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(mContext, mProduct.name + " Successfully Added To Wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (v == cartButton) {
                addToCartNew(cartButton);
            } else if (v == followButton) {
                v.setEnabled(false);
                final boolean isFollowing = v.isSelected();
                UIController.follow(mContext, mUser, !isFollowing,
                        new IResultListener<LikeResponse>() {

                            @Override
                            public void onResult(LikeResponse result) {
                                v.setEnabled(true);
                                v.setSelected(result.success != isFollowing);
                                if (isFollowing) {
                                    mUser.followers_cnt = mUser.followers_cnt - 1;
                                    mUser.isFollowed = false;
                                    //Toast.makeText(getApplicationContext(), ((String.format(Locale.getDefault(), "%d", user.followers_cnt))), Toast.LENGTH_SHORT).show();
                                } else {
                                    mUser.followers_cnt = mUser.followers_cnt + 1;
                                    mUser.isFollowed = true;
                                    //Toast.makeText(getApplicationContext(), ((String.format(Locale.getDefault(), "%d", user.followers_cnt))), Toast.LENGTH_SHORT).show();
                                }
                                if (mProInfoFragment != null) {
                                    mProInfoFragment.mFollower();
                                }
                            }
                        });
            }
        }
    }


    public ExtendedAjaxCallback getAjaxCallBackCustom(int requestId) {
        return getAjaxCallback(requestId);
    }

    public AQuery getAQueryCustom() {
        return getAQuery();
    }

    private final int DESC_CALL = IDUtils.generateViewId();

    private void get5NewProductDescFromServer() {
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(DESC_CALL);
        ajaxCallback.method(AQuery.METHOD_GET);
        getAQueryCustom().ajax((ApiKeys.getDesc5NewAPI() + mProduct.id), String.class, ajaxCallback);
    }


    ArrayList<String> list;

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {

        Log.d("djprod", "url queried- ProductActivity: " + url);
        Log.d("djprod", "response- ProductActivity: " + json);
        if (id == DESC_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mTabLayout, this);
            if (json == null)
                return;
            if (success) {
                list = new ArrayList<>();
                try {
                    JSONObject jsonObj = new JSONObject(json.toString());
                    if (!jsonObj.isNull("productWarranty")) {
                        try {
                            list.add(jsonObj.getString("productWarranty"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productMBP")) {
                        try {
                            list.add(jsonObj.getString("productMBP"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productCert")) {
                        try {
                            list.add(jsonObj.getString("productCert"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productEDTInDays")) {
                        try {
                            list.add(jsonObj.getString("productEDTInDays"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productPayModes")) {
                        try {
                            JSONArray jsonArr = jsonObj.getJSONArray("productPayModes");
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < jsonArr.length(); i++) {
                                sb = sb.append(jsonArr.getString(i) + "\n");
                            }
                            list.add(sb.toString());
                            Log.d("djprod", "payment modes - serverCallEnds: " + sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            list.add(null);
                        }
                    }
                    /*ProductInfoFragment pif = (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_PRODUCT + "");
                    pif.setAllDescription(list);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else super.serverCallEnds(id, url, json, status);
    }

    private class CollectionCallBack implements
            LoaderManager.LoaderCallbacks<ObjectAsyncLoader.Result> {
        @Override
        public Loader<ObjectAsyncLoader.Result> onCreateLoader(int id, Bundle args) {
            return new ObjectAsyncLoader(mContext) {
                @Override
                protected void registerContentObserver(ContentObserver observer) {
                    getContentResolver().registerContentObserver(Tables.Collections.CONTENT_URI,
                            true, observer);
                }

                @Override
                protected void unRegisterContentObserver(ContentObserver observer) {
                    getContentResolver().unregisterContentObserver(observer);
                }

                @Override
                public Result loadInBackground() {
                    Result result = new Result();
                    Cursor c = getContentResolver().query(Tables.Collections.CONTENT_URI, null,
                            Tables.Collections._ID + " = ?",
                            new String[]{String.valueOf(mProduct.collectionId)}, null);
                    if (c != null) {
                        if (c.moveToFirst()) {
                            result.object = Collection.extractFromCursor(c);
                        }
                        c.close();
                    }
                    return result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ObjectAsyncLoader.Result> loader,
                                   ObjectAsyncLoader.Result data) {
            mCollection = (Collection) data.object;
            if (mCollection != null) {
                mProInfoFragment =
                        (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(
                                "" + UISTATE_PRODUCT);
                if (mProInfoFragment != null) mProInfoFragment.bindCollectionUI(mCollection);
                mOverlayVH.mProductCollection.setText(mCollection.name.trim());
                UiRandomUtils.underLineTv(mOverlayVH.mProductCollection, 0, mOverlayVH.mProductCollection.length());
            } else {
                mOverlayVH.mProductCollection.setText("");
            }

        }

        @Override
        public void onLoaderReset(Loader<ObjectAsyncLoader.Result> loader) {

        }
    }

    public void mFollower() {
        mUser = UserInfoCache.getInstance(mContext).getUserInfoDB(mProduct.userId, true);
        if (mUser != null) {
            mOverlayVH.followButton.setTag(mUser);
            mOverlayVH.followButton.setSelected(mUser.isFollowed);
        } else {
            mOverlayVH.mProductOwner.setText("");
            mOverlayVH.followButton.setVisibility(View.GONE);
        }
    }
}
