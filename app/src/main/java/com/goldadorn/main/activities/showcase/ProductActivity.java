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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.activities.cart.IPostCreationCallBacks;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ObjectAsyncLoader;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.dj.fragments.FilterTimelineFragment;
import com.goldadorn.main.dj.model.AddToCartRequestDataObj;
import com.goldadorn.main.dj.model.BookAppointmentDataObj;
import com.goldadorn.main.dj.model.CustomizationDisableList;
import com.goldadorn.main.dj.model.CustomizationStepResponse;
import com.goldadorn.main.dj.model.FilterPostParams;
import com.goldadorn.main.dj.model.MinAutoCustResponse;
import com.goldadorn.main.dj.model.ProductCustomizationTabUpdationDataObj;
import com.goldadorn.main.dj.model.Swatches;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.support.AppTourGuideHelper;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.dj.uiutils.ResourceReader;
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
import com.goldadorn.main.model.OptionValue;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.SocialPost;
import com.goldadorn.main.model.StoneDetail;
import com.goldadorn.main.model.User;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.TypefaceHelper;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsButton;
import com.venmo.view.TooltipView;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductActivity extends BaseDrawerActivity implements IPostCreationCallBacks{
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
    OverlayViewHolder mOverlayVH;
    private int mStartHeight;
    private int mCollapsedHeight;
    public ProductPagerAdapter mProductAdapter;
    private int mVerticalOffset = 0;
    Product mProduct;
    private Handler mHandler = new Handler();
    private TabViewHolder mTabViewHolder;
    private CollectionCallBack mCollectionCallBack = new CollectionCallBack();
    User mUser;
    Collection mCollection;
    public ProductInfo mProductInfo;
    public ProductOptions mProductOptions;

    public static Intent getLaunchIntent(Context context, Product product) {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }

    /*private Dialog overLayDialog;

    public void showOverLay(String text, int colorResId) {
        //if (overLayDialog == null){
        overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlayLogo(this, text, colorResId,
                WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        //}
        overLayDialog.show();
    }*/

    /*private void showOverLay(String text, int colorResId, int gravity) {
        if (overLayDialog == null) {
            //WindowUtils.justPlainOverLay = true;
            overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlay(this, text, colorResId,
                    gravity);
        }
        overLayDialog.show();
    }

    public void dismissOverLay() {
        if (overLayDialog != null) {
            if (overLayDialog.isShowing()) {
                WindowUtils.marginForProgressViewInGrid = 5;
                overLayDialog.dismiss();
            }
        }
    }*/


    boolean isSocialFeed;
    boolean hasCert;

    private void checkHasCert(){
        Cursor cursor = RandomUtils.getUserInfoCursor(mProduct.userId);
        hasCert = User.hasCertification(cursor);
        mProduct.setHasCertificate(hasCert);
    }



    public boolean hasCertificate(){
        return hasCert;
    }


    //private int lastStep;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
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
                    if ((verticalOffset * -1) /*- lastStep*/ > 320) {
                        //lastStep = (verticalOffset * -1);
                        checkOutTour(verticalOffset);
                    }
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
                            isToWait = false;
                            synchronized(holyString) {
                                holyString.notifyAll();
                            }
                            //mProdInfoFragment = (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_PRODUCT + "");
                            /*if (mProdInfoFragment != null)
                                mProdInfoFragment.bindProductInfo(mProductInfo);*/
                            mProduct.discount = mProductInfo.discount;
                        }
                    }
                });
        UIController.getProductOptions(mContext, response, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                if (result.success) {
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {*/
                    query2ndAPIprodInfo();
                     /*   }
                    }, 2000);*/
                    mProductOptions = result.options;
                    mProductOptions.discount = mProduct.discount;
                    mProduct.addDefaultCustomisation(mProductOptions);
                    //mProdCustFrag = (ProductCustomiseFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_CUSTOMIZE + "");
                    if (mProdCustFrag != null)
                        mProdCustFrag.bindProductOptions(mProductOptions);
                }
                //dismissOverLay();
            }
        });
        configureUI(UISTATE_CUSTOMIZE);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                get5NewProductDescFromServer();
            }
        }, 3000);*/

        checkHasCert();
        getSupportLoaderManager().initLoader(mCollectionCallBack.hashCode(), null,
                mCollectionCallBack);

        tourThisScreen();
        setUpGuideListener();
    }

    int limit = 35;
    private boolean isTourInProgress = false;
    private DisplayProperties disProp;

    private void setUpGuideListener() {
        disProp = DisplayProperties.getInstance(getBaseContext(), 1);
        limit = Math.round(limit * disProp.getYPixelsPerCell());
        Log.d(Constants.TAG, "limit allowed: " + limit);
        //mTourHelper = AppTourGuideHelper.getInstance(getApplicationContext());
    }

    private void checkOutTour(int offset) {
        if (isTourInProgress)
            return;
        offset = -1 * offset;
        Log.d(Constants.TAG, "offset: " + offset);
        if (frame_no_scroll_dummy_cust.getVisibility() == View.GONE || frame_no_scroll_dummy_cust.getVisibility() == View.INVISIBLE)
            return;
        if (offset > limit) {
            tourThisScreenNew();
        }
    }

    @Bind(R.id.transViewBottom)
    View transViewBottom;
    private void tourThisScreenNew() {
        //if (true)return;
        isTourInProgress = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(Constants.TAG, "tour customization");
                mTourHelper.displayCustomizationTour(ProductActivity.this,transViewBottom);
            }
        }, 50);
    }


    public void query2ndAPIprodInfo(){
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(PROD_INFO_2nd_API_CALL);
        ajaxCallback.method(AQuery.METHOD_GET);
        getAQueryCustom().ajax(ApiKeys.getProdInfoMetalStoneAPI(mProduct.id), String.class, ajaxCallback);
    }

    /*private void updateProductInfoTab(ProductInfoTabUpdationDataObj dataObj){
        mProductInfo.metalPurity = dataObj.getMetalPurity();
        mProductInfo.metalrate = dataObj.getMetalRate();
        if (mProdInfoFragment != null)
            mProdInfoFragment.bindProductInfo(mProductInfo);
    }*/


    private void updateCustomizationTab(ProductCustomizationTabUpdationDataObj dataObj) {
        mProdCustFrag.updateTitleIconDefValues();
        HashMap<String, String> map = new HashMap<>();
        map.put(ProductCustomiseFragment.PBD_metal, dataObj.getMetalCost());
        map.put(ProductCustomiseFragment.PBD_stone, String.valueOf(dataObj.getStonesTotalCost()));
        cartRequestDataObj.setStonePrice(dataObj.getStonesTotalCost());
        map.put(ProductCustomiseFragment.PBD_making, dataObj.getMakingCharges());
        cartRequestDataObj.setMakingCharges(dataObj.getMakingCharges());
        map.put(ProductCustomiseFragment.PBD_VAT, dataObj.getVAT());
        map.put(ProductCustomiseFragment.PBD_Discount, dataObj.getOffAmount());
        map.put(ProductCustomiseFragment.PBD_discountVal, dataObj.getDiscount());
        //cartRequestDataObj.setDiscount(Double.parseDouble(dataObj.getDiscount()));
        map.put(ProductCustomiseFragment.PBD_FinalPrice, dataObj.getFinalPrice());
        cartRequestDataObj.setTotalAmount(dataObj.getFinalPrice());
        map.put(ProductCustomiseFragment.PBD_total, String.valueOf(dataObj.getTotalCost()));
        cartRequestDataObj.setOfferReplicaPrice(String.valueOf(dataObj.getTotalCost()));
        mProdCustFrag.setPriceBreakDown(map);
    }


    public String getMakingCharges() {
        String temp = mProdCustFrag.getPriceBreakDownParam(ProductCustomiseFragment.PBD_making);
        return temp == null ? "NA" : temp;
    }

    public String getVAT() {
        String temp = mProdCustFrag.getPriceBreakDownParam(ProductCustomiseFragment.PBD_VAT);
        return temp == null ? "NA" : temp;
    }

    public String getTotalPrice() {
        String temp = mProdCustFrag.getPriceBreakDownParam(ProductCustomiseFragment.PBD_total);
        return temp == null ? "NA" : temp;
    }


    public String getDiscount() {
        String temp = mProdCustFrag.getPriceBreakDownParam(ProductCustomiseFragment.PBD_Discount);
        if (temp != null)
            temp = "- " + temp;
        return temp == null ? "0" : temp;
    }


    public String getGrandTotalForProdInfoFrag(){
        /*if (mProduct.discount > 0)*/
        return mOverlayVH.mProductCost.getText().toString();
    }


    //http://demo.eremotus-portal.com/products/3045/3045-1.jpg
    private ArrayList<String> getVariousProductLooks(int lookcount, boolean isNotDefault) {
        if (lookcount == 0)
            return new ArrayList<>();
        ArrayList<String> imageUrlList = new ArrayList<>();
        String defaultUrl = mProduct.getImageUrl(mProduct.userId, mProduct.defMetal, isNotDefault, -1);
        int indexToReplace = defaultUrl.indexOf(/*'-'*/".jpg") /*+*/ - 1;
        char[] charArrOriginal = defaultUrl.toCharArray();
        for (int i = 1; i <= lookcount; i++) {
            char[] toreplace = String.valueOf(i).toCharArray();
            charArrOriginal[indexToReplace] = toreplace[0];
            imageUrlList.add(String.copyValueOf(charArrOriginal));
            Log.d("djprod", "productimageurls: " + imageUrlList.get(i - 1));
        }
        return imageUrlList;
    }


    /*private String getNewUrlS3(){
        String baseUrl =
    }*/


    private void setAdapterForProdImages(int lookcount) {
        mOverlayVH.pager.setAdapter(
                mProductAdapter = new ProductPagerAdapter(getSupportFragmentManager(), getVariousProductLooks(lookcount, false)));
        mOverlayVH.pager.setOffscreenPageLimit(lookcount);
        mOverlayVH.indicator.setViewPager(mOverlayVH.pager);
    }

    public ArrayList<String> getDescriptions() {
        return productInfoList;
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

        //mOverlayVH.like.setSelected(mProduct.isLiked);
        //manupilateLikeBtnStatus(mOverlayVH.like, mProduct.isLiked);

        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
        mOverlayVH.mProductName.setText(mProduct.name);
        mOverlayVH.toastProdName.setText(mProduct.name);
        mOverlayVH.mProductName2.setText(/*mProduct.name*/getEndEllipsizedName(mProduct.name));
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
        /*if (mProduct.discount != 0) {*/
           /* mOverlayVH.mProductCost.setText(RandomUtils
                    .getIndianCurrencyFormat(RandomUtils.getOfferPrice(mProduct.discount, mProduct.getDisplayPrice()), true));
            mOverlayVH.product_price_slash.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
            mOverlayVH.mProductCost2.setText(RandomUtils
                    .getIndianCurrencyFormat(RandomUtils.getOfferPrice(mProduct.discount, mProduct.getDisplayPrice()), true));*/
        updateDiscountUi(mProduct.discount);
        //}
        mTabViewHolder.setCounts(-1, -1);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        //menu.findItem(R.id.nav_my_overflow).setVisible(false);
        menu.findItem(R.id.nav_my_notifications).setIcon(R.drawable.vector_icon_bell_dark);
        menu.findItem(R.id.nav_my_search).setIcon(R.drawable.vector_icon_search_dark);
        return value;
    }*/

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

    private ProductCustomiseFragment mProdCustFrag;
    private ProductInfoFragment mProdInfoFragment;
    private FilterTimelineFragment mFilterFrag;

    @Bind(R.id.frame_no_scroll_dummy_cust)
    FrameLayout frame_no_scroll_dummy_cust;

    private void configureUI(int uiState) {
        Fragment fragment = null;
        int id = R.id.frame_content;
        //mFrame.setVisibility(View.GONE);
        mFrameNoScrollDummy.setVisibility(View.INVISIBLE);
        frame_no_scroll_dummy_cust.setVisibility(View.GONE);
        if (uiState == UISTATE_SOCIAL) {
            id = R.id.frame_no_scroll_dummy;
            //f = new SocialFeedFragment();
            manupilateToggle();
            //mFrame.setVisibility(View.INVISIBLE);
            mFrame.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            if (mProdCustFrag != null)
                fragmentTransaction.hide(mProdCustFrag);
            if (mProdInfoFragment != null)
                fragmentTransaction.hide(mProdInfoFragment);
            fragmentTransaction.commit();

            FilterPostParams fpp = new FilterPostParams(("P" + String.valueOf(mProduct.id)), "0", "0");
            if (mFilterFrag == null) {
                fragment = mFilterFrag = FilterTimelineFragment.newInstance(fpp);
            } else {
                updateFragmentStates(mFilterFrag, true);
                return;
            }
        } else if (uiState == UISTATE_PRODUCT) {
            mFrame.setVisibility(View.VISIBLE);
            if (mProdCustFrag != null)
                updateFragmentStates(mProdCustFrag, false);
            if (mFilterFrag != null)
                updateFragmentStates(mFilterFrag, false);

            if (mProdInfoFragment == null) {
                fragment = mProdInfoFragment = new ProductInfoFragment();
            } else {
                updateFragmentStates(mProdInfoFragment, true);
                return;
            }
        } else {
            id = R.id.frame_no_scroll_dummy_cust;
            mFrame.setVisibility(View.INVISIBLE);
            //mFrame.setVisibility(View.INVISIBLE);
            //mFrameNoScrollDummy.setVisibility(View.INVISIBLE);
            frame_no_scroll_dummy_cust.setVisibility(View.VISIBLE);
            if (mProdInfoFragment != null)
                updateFragmentStates(mProdInfoFragment, false);
            if (mFilterFrag != null)
                updateFragmentStates(mFilterFrag, false);

            if (mProdCustFrag == null) {
                fragment = mProdCustFrag = new ProductCustomiseFragment();
            } else {
                updateFragmentStates(mProdCustFrag, true);
                return;
            }
        }
        //if (f != null) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(id, fragment, "" + uiState);
        fragmentTransaction.commit();
        /*} else {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, mProInfoFragment, "" + uiState);
            fragmentTransaction.commit();
        }*/
    }


    private void updateFragmentStates(Fragment fragment, boolean tobeShown) {
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        if (tobeShown)
            fragmentTransaction.show(fragment);
        else fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }


    private void manupilateToggle() {
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

    @Deprecated
    public void addToCartNew(/*final View cartBtn*/) {
        if (!ConnectionDetector.getInstance(Application.getInstance()).isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_LONG).show();
            return;
        }

        //cartBtn.setEnabled(false);
        //showOverLay("Adding to cart..", R.color.colorPrimary, );
        UIController.addToCartNewProduct(mContext, mProduct, mProductInfo, mProductOptions,
                new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        //logEventsAnalytics(AppEventsConstants.EVENT_NAME_ADDED_TO_CART);
                        //dismissOverLay();
                        if (result == null)
                            return;
                        if (result.success) {
                            logEventsAnalytics(GAAnalyticsEventNames.CART_PRODUCT_ADDED);
                            Log.d(Constants.TAG_APP_EVENT, "AppEventLog: PRODUCT_ADDED_TO_CART");
                        }
                    }
                });
    }


    private void confirmedToCart(/*final View cartBtn*/) {
        /*Log.d("iii","product id that was pushed to cart: "+mProduct.id);
        ViewConstructor.getInstance(getApplicationContext()).displayInfo(this, "Cart", "This item is added to your Cart!\nHow would you like to proceed?",
                "Go to Cart\nCheckout", "Continue\nShopping", true, new ViewConstructor.InfoDisplayListener() {
                    @Override
                    public void onPositiveSelection(DialogInterface alertDialog) {
                        alertDialog.dismiss();
                        menuAction(R.id.nav_cart);
                    }
                });*/
        isLastCallATC = false;
        Dialog dialog = ViewConstructor.getInstance(getApplicationContext()).displayDialog(ProductActivity.this,
                LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_cart_new, null), "Cart", "This item is added to your Cart!\nHow would you like to proceed?",
                "Go to Cart\n& Checkout", "Continue\nShopping", new ViewConstructor.DialogButtonClickListener() {
                    @Override
                    public void onPositiveBtnClicked(Dialog dialog, View btn) {
                        dialog.dismiss();
                        //cartBtn.setEnabled(true);
                        logEventsAnalytics(GAAnalyticsEventNames.CART_GO_TO);
                        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: GO_TO_CART");
                        menuAction(R.id.nav_cart);
                    }

                    @Override
                    public void onNegativeBtnClicked(Dialog dialog, View btn) {
                        dialog.dismiss();
                        //cartBtn.setEnabled(true);
                        logEventsAnalytics(GAAnalyticsEventNames.CART_CONTINUE_SHOPPING);
                        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CART_CONTINUE_SHOPPING");
                        Toast.makeText(getApplicationContext(), "You can access your cart by selecting the cart option from the Slidemenu", Toast.LENGTH_LONG).show();
                    }
                });
        dialog.show();
    }

    public String getEndEllipsizedName(String text) {
        String endEllipsizedName = "";
        if (text.length() > 15)
            endEllipsizedName = text.substring(0, 15) + "...";
        return endEllipsizedName;
    }

    @Override
    public int getPostType() {
        return SocialPost.POST_ATC;
    }

    @Override
    public List<String> getClubbingData() {
        if (mProduct != null){
            mProduct.princeRange = mProductOptions.range;
            List<String> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            list.add(sb.append(mProduct.id).append(":")
                    .append(mProduct.collectionId).append(":")
                    .append(mProduct.userId).append(":")
                    .append(mProduct.getDisplayPrice()).append(":")
                    .append(mProduct.princeRange).append(":")
                    .append(mProduct.discount).toString());
            return list;
        }
        return null;
    }

    @Override
    public List<String> getLinks() {
        if (mProduct != null) {
            List<String> t = new ArrayList<>();
            String url = mProduct.getImageUrl(mProduct.userId, mProduct.defMetal, false, -1);
            String path = /*".." + */url.substring(url.indexOf(/*"/product"*/"defaults/"), url.length());
            t.add(path);
            return t;
        }
        return null;
    }


    @Override
    public String getPostMsg() {
        return "Hey, I just added this "+ mProductOptions.prodType + " to my cart. What do you guys think?! :)";
    }


    public void postATC(){
        String msg = getPostMsg().trim();
        int type = getPostType();
        Intent intent = new Intent();
        List<String> prodColDesIdPrice = getClubbingData();
        if (prodColDesIdPrice != null) {
            if (prodColDesIdPrice.size() != 0) {
                String[] clubbedArr = new String[prodColDesIdPrice.size()];
                int index = 0;
                for (String s : prodColDesIdPrice) {
                    clubbedArr[index] = s;
                    index++;
                }
                intent.putExtra("clubbed", clubbedArr);
            }
        }

        List<String> linksList = getLinks();
        if (linksList != null && linksList.size() != 0) {
            String[] links = new String[linksList.size()];
            for (int i = 0; i < linksList.size(); i++) {
                links[i] = linksList.get(i);
            }
            intent.putExtra("links", links);
        }

        intent.putExtra("type", type);
        intent.putExtra("msg", msg);
        intent.putStringArrayListExtra("hashtags", RandomUtils.getHashTagStrings(msg.trim()));
        //setResult(Activity.RESULT_OK, intent);
        super.onActivityResult(POST_FEED, RESULT_OK, intent);
    }



   /* private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    public Fragment getRegisteredFragment(int fragmentPosition) {

        return registeredFragments.get(fragmentPosition);
    }*/

    public class ProductPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> images;

        public ArrayList<String> getImagesUrlList() {
            return images;
        }

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


        /*@Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.setOnClickListener(itemClick);
            return super.instantiateItem(container, position);
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


        @Override
        public int getItemPosition(Object object) {
            /*if(getSupportFragmentManager().getFragments().contains(object))*/
            return POSITION_NONE;
            /*else
                return POSITION_UNCHANGED;*/
        }

    }


    public void launchProjectorView() {
        Intent intent = new Intent(this, ProjectorViewActivity.class);
        intent.putStringArrayListExtra(IntentKeys.PROJECTOR_VIEW_IMAGES_LIST, mProductAdapter.getImagesUrlList());
        intent.putExtra(IntentKeys.PRODUCT_NAME, getProductName());
        startActivity(intent);
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
                    .setItemImageUrl(mProduct.getImageUrl(mProduct.userId, mProduct.defMetal, false, -1))
                    .setItemName(mProduct.name);
            intent.putExtra(IntentKeys.BOOK_APPOINT_DATA, baaDataObj);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean canProceedToBAA() {
        if (mProduct != null) {
            if (!TextUtils.isEmpty(mProduct.name) && !TextUtils.isEmpty(mProduct.getImageUrl(mProduct.userId, mProduct.defMetal, false, -1))
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

    private void manupilateLikeBtnStatus(IconicsButton likeBtn, boolean isLiked) {
        if (isLiked) {
            likeBtn.setText(getApplicationContext().getResources().getString(R.string.icon_liked_post));
            likeBtn.setSelected(true);
        } else {
            likeBtn.setText(getApplicationContext().getResources().getString(R.string.icon_likes_post));
            likeBtn.setSelected(false);
        }
    }


    public String getProductName() {
        return mProduct.name;
    }


    int defaultDiscountVisibitly;
    private void updateDiscountUi(double discount) {
        mProduct.discount = discount;
        if (cartRequestDataObj != null)
            cartRequestDataObj.setDiscount(discount);
        if (discount <= 0) {
            defaultDiscountVisibitly = View.GONE;
            mOverlayVH.discountHolder.setVisibility(View.GONE);
            mOverlayVH.product_price_slash.setVisibility(View.GONE);
            mOverlayVH.product_price_slash.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
            //mOverlayVH.tvDiscountOnRed.setVisibility(View.GONE);
            mOverlayVH.mProductCost2.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
            mOverlayVH.mProductCost.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
            return;
        }
        defaultDiscountVisibitly = View.VISIBLE;
        mOverlayVH.discountHolder.setVisibility(View.VISIBLE);
        //mOverlayVH.discountHolder.setVisibility(View.GONE);
        mOverlayVH.product_price_slash.setVisibility(View.VISIBLE);
        //mOverlayVH.tvDiscountOnRed.setVisibility(View.VISIBLE);
        mOverlayVH.tvDiscountOnRed.setText(new StringBuilder(String.valueOf(Math.round(discount))).append("%").append("\n").append("off"));
        mOverlayVH.mProductCost.setText(RandomUtils
                .getIndianCurrencyFormat(RandomUtils.getOfferPrice(discount, mProduct.getDisplayPrice()), true));
        mOverlayVH.product_price_slash.setText(RandomUtils.getIndianCurrencyFormat(mProduct.getDisplayPrice(), true));
        mOverlayVH.mProductCost2.setText(RandomUtils
                .getIndianCurrencyFormat(RandomUtils.getOfferPrice(discount, mProduct.getDisplayPrice()), true));
    }


    class OverlayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final AppBarLayout appBarLayout;
        @Bind(R.id.likes_count)
        TextView likesCount;
        @Bind(R.id.likeButton)
        //IconicsButton like;
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
        @Bind(R.id.product_price_slash)
        TextView product_price_slash;
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
        @Bind(R.id.ivGlass)
        ImageView ivGlass;
        @Bind(R.id.toastProdName)
        TooltipView toastProdName;
        @Bind(R.id.discountHolder)
        View discountHolder;
        @Bind(R.id.tvDiscountOnRed)
        TextView tvDiscountOnRed;

        public void setProductActions(boolean productActions) {
            isProductActions = productActions;
        }

        boolean isProductActions = false;

        View.OnTouchListener prodNameTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toastProdName.setVisibility(View.VISIBLE);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    toastProdName.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        };


        private void setFontTypeface(){
            //UiRandomUtils.setTypefaceBold(mNameText);
            TypefaceHelper.setFont(likesCount, product_price_slash, mProductName, mProductOwner
                    , mProductCollection, mProductCost, mProductName2, mProductCost2, tvDiscountOnRed);
        }

        public OverlayViewHolder(View itemView, AppBarLayout appBarLayout) {
            super(itemView);
            this.appBarLayout = appBarLayout;
            ButterKnife.bind(this, itemView);
            setDrawableForMagGlass();
            setFontTypeface();
            ivGlass.setOnClickListener(this);
            mProductName.setOnTouchListener(prodNameTouchListener);
            productActionsToggle.setOnClickListener(this);
            shareButton.setOnClickListener(this);
            buyNoBuyButton.setOnClickListener(this);
            wishlistButton.setOnClickListener(this);
            btnBookApoint.setOnClickListener(this);
            product_price_slash.setVisibility(View.VISIBLE);
            UiRandomUtils.strikeThroughText(product_price_slash);
            //like.setOnClickListener(this);//disabled
            setUpLikeBtn();
            cartButton.setOnClickListener(this);
            followButton.setOnClickListener(this);

            mProductOwner.setOnClickListener(this);
            mProductCollection.setOnClickListener(this);

            mProductOwner.setVisibility(View.GONE);
            mProductCollection.setVisibility(View.GONE);
            followButton.setVisibility(View.GONE);
            //RandomUtils.underLineTv(mProductOwner);
        }

        private void setDrawableForMagGlass() {
            int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.colorPrimary);
            ivGlass.setImageDrawable(new IconicsDrawable(Application.getInstance())
                    .icon(CommunityMaterial.Icon.cmd_magnify)
                    .color(color)
                    .sizeDp(20));
        }

        private void setUpLikeBtn() {
            int color = ResourceReader.getInstance(getApplicationContext()).getColorFromResource(R.color.votedColor);
            like.setImageDrawable(new IconicsDrawable(ProductActivity.this)
                    .icon(FontAwesome.Icon.faw_heart)
                    .color(color)
                    .sizeDp(20));
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
                ivGlass.setVisibility(visibility);
                if (visibility == View.VISIBLE) {
                    if (defaultDiscountVisibitly == View.VISIBLE)
                        discountHolder.setVisibility(visibility);
                } else discountHolder.setVisibility(visibility);
                mProductOwner.setVisibility(View.GONE);
                mProductCollection.setVisibility(View.GONE);
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
                    ivGlass.setVisibility(View.VISIBLE);
                    discountHolder.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    mProductCollection.setVisibility(View.VISIBLE);
                    mProductCost.setVisibility(View.VISIBLE);
                    product_price_slash.setVisibility(View.VISIBLE);
                    mProductOwner.setVisibility(View.GONE);
                    mProductCollection.setVisibility(View.GONE);
                    pager.animate().setDuration(0).scaleY(1f).scaleX(1f);

                } else {
                    isProductActions = true;
                    appBarLayout.setExpanded(false);
                    productActions.setVisibility(View.VISIBLE);
                    productActionsToggle.setImageResource(R.drawable.close);
                    mProductName2.setVisibility(View.VISIBLE);
                    mProductCost2.setVisibility(View.VISIBLE);
                    mProductName.setVisibility(View.GONE);
                    ivGlass.setVisibility(View.GONE);
                    discountHolder.setVisibility(View.GONE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    mProductCollection.setVisibility(View.GONE);
                    mProductCost.setVisibility(View.GONE);
                    product_price_slash.setVisibility(View.GONE);
                    pager.animate().setDuration(0).scaleY(0.8f).scaleX(.8f);
                }
            } else if (v.getId() == R.id.btnBookApoint) {
                displayBookAppointment();
            } else if (v.getId() == mProductOwner.getId()) {
                launchDesignerScreen();
            } else if (v.getId() == mProductCollection.getId()) {
                launchCollectionScreen();
            } else if (v.getId() == ivGlass.getId()) {
                //Toast.makeText(getApplicationContext(), "Click on the Product Image for Magnified view", Toast.LENGTH_SHORT).show();
                launchProjectorView();
            } else if (v == like) {
                final IconicsButton likeBtn = (IconicsButton) v;
                likeBtn.setEnabled(false);
                final boolean isLiked = likeBtn.isSelected();
                Log.d("djprod", "isliked val: " + isLiked);
                manupilateLikeStat(likeBtn, !isLiked);
               /* UIController.like(v.getContext(), mProduct, !isLiked,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                likeBtn.setEnabled(true);
                                //likeBtn.setSelected(result.success != isLiked);
                                if (result.success) {
                                    manupilateLikeBtnStatus(likeBtn, !isLiked);
                                    YoYo.with(Techniques.Landing).duration(300).playOn(likeBtn);
                                    if (isLiked) {
                                        Log.d("djprod", "disliked");
                                        mProduct.likecount = mProduct.likecount - 1;
                                        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                        // Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("djprod", "liked");
                                        mProduct.likecount = mProduct.likecount + 1;
                                        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                        //Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });*/
            } else if (v == shareButton) {
                //todo like click
                Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
            } else if (v == buyNoBuyButton) {
                //Toast.makeText(v.getContext(), "Feature Coming Soon!", Toast.LENGTH_SHORT).show();
                //startActivity(PostPollActivity.getLaunchIntent(mContext, mProduct));
                mProduct.princeRange = mProductOptions.range;
                Intent intent = PostPollActivity.getLaunchIntent(ProductActivity.this, mProduct);
                postABonb(intent);
            } else if (v == wishlistButton) {
                UIController.addToWhishlist(v.getContext(), mProduct, new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(mContext, mProduct.name + " Successfully Added To Wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (v == cartButton) {
                //addToCartNew();
                addToCartV27();
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
                                if (mProdInfoFragment != null) {
                                    mProdInfoFragment.mFollower();
                                }
                            }
                        });
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void postABonb(Intent intent) {
        mProduct.prodType = mProductOptions.prodType;
        startActivityForResult(intent, POST_FEED);
    }


    private void manupilateLikeStat(final IconicsButton likeBtn/*, final Product product*/, final boolean isLikeAction) {

        Log.d("djlike", "product like stat: startPoint: " + mProduct.likeStat);
        Log.d("djlike", "product id: startPoint: " + mProduct.id);
        Log.d("djlike", "product name: startPoint: " + mProduct.name);
        if (mProduct.likeStat == 0) {
            /*if (isLikeAction) {*/
            Log.d("djlike", "product like stat=0; likedAction " + isLikeAction);
            if (isLikeAction)
                mProduct.toWriteLikeCount = 1;
            else mProduct.toWriteLikeCount = -1;
            UIController.like(getApplicationContext(), mProduct, isLikeAction,
                    new IResultListener<LikeResponse>() {
                        @Override
                        public void onResult(LikeResponse result) {
                            //isLikedHover(false);
                            likeBtn.setEnabled(true);
                            //likeBtn.setSelected(result.success != isLiked);
                            if (result.success) {
                                manupilateLikeBtnStatus(likeBtn, isLikeAction);
                                YoYo.with(Techniques.Landing).duration(300).playOn(likeBtn);
                                if (!isLikeAction) {
                                    Log.d("djprod", "disliked");
                                    mProduct.isLiked = false;
                                    mProduct.likeStat = -1;
                                    mProduct.likecount = mProduct.likecount - 1;
                                    mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                    // Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("djprod", "liked");
                                    mProduct.isLiked = true;
                                    mProduct.likeStat = 1;
                                    mProduct.likecount = mProduct.likecount + 1;
                                    mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                    //Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                }
                            }

                                /*if (mToast != null) mToast.cancel();
                                mToast = Toast.makeText(getActivity(),
                                        result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                                mToast.show();*/
                        }
                    });
            /*} else {
                Log.d("djlike", "product like stat=0; likedAction " + isLikeAction);
                UIController.like(getApplicationContext(), product, false, new IResultListener<LikeResponse>() {
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
            }*/
        } else if (mProduct.likeStat == 1) {
            Log.d("djlike", "product like stat=1");
            if (isLikeAction) {
                Log.d("djlike", "product like stat=1; likedAction " + isLikeAction);
                Toast.makeText(getApplicationContext(), "You have already liked this product!", Toast.LENGTH_LONG).show();

            } else {
                Log.d("djlike", "product like stat=1; likedAction " + isLikeAction);
                mProduct.toWriteLikeCount = -2;
                UIController.like(getApplicationContext(), mProduct, isLikeAction,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                //isLikedHover(false);
                                likeBtn.setEnabled(true);
                                //likeBtn.setSelected(result.success != isLiked);
                                if (result.success) {
                                    manupilateLikeBtnStatus(likeBtn, isLikeAction);
                                    YoYo.with(Techniques.Landing).duration(300).playOn(likeBtn);
                                    if (!isLikeAction) {
                                        Log.d("djprod", "disliked");
                                        Toast.makeText(getApplicationContext(), "You have disliked a previously liked product", Toast.LENGTH_LONG).show();
                                        mProduct.isLiked = false;
                                        mProduct.likeStat = -1;
                                        mProduct.likecount = mProduct.likecount - 2;//double dip
                                        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                        // Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("djprod", "liked");
                                        //dont care code
                                        /*product.isLiked = true;
                                        mProduct.likecount = mProduct.likecount + 1;
                                        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));*/
                                        //Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                /*if (mToast != null) mToast.cancel();
                                mToast = Toast.makeText(getActivity(),
                                        result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                                mToast.show();*/
                            }
                        });
            }
        } else if (mProduct.likeStat == -1) {
            Log.d("djlike", "product like stat=-1");
            if (!isLikeAction) {
                Log.d("djlike", "product like stat=-1; likedAction " + isLikeAction);
                Toast.makeText(getApplicationContext(), "You have already liked this product!", Toast.LENGTH_LONG).show();
            } else {
                Log.d("djlike", "product like stat=-1; likedAction " + isLikeAction);
                mProduct.toWriteLikeCount = 2;
                UIController.like(getApplicationContext(), mProduct, true,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                //isLikedHover(false);
                                likeBtn.setEnabled(true);
                                //likeBtn.setSelected(result.success != isLiked);
                                if (result.success) {
                                    manupilateLikeBtnStatus(likeBtn, isLikeAction);
                                    YoYo.with(Techniques.Landing).duration(300).playOn(likeBtn);
                                    if (!isLikeAction) {
                                        Log.d("djprod", "disliked");
                                        //dont care code, wobt happen use case
                                       /* product.isLiked = false;
                                        mProduct.likecount = mProduct.likecount - 2;//double dip
                                        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));*/
                                        // Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("djprod", "liked");
                                        Toast.makeText(getApplicationContext(), "You have liked a previously disliked product", Toast.LENGTH_LONG).show();
                                        mProduct.isLiked = true;
                                        mProduct.likeStat = 1;
                                        mProduct.likecount = mProduct.likecount + 2;//double dip
                                        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
                                        //Toast.makeText(getApplicationContext(),((String.format(Locale.getDefault(), "%d", mProduct.likecount))),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                /*if (mToast != null) mToast.cancel();
                                mToast = Toast.makeText(getActivity(),
                                        result.success ? product.name + " liked" : "failed to update", Toast.LENGTH_LONG);
                                mToast.show();*/
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

    public static final String METAL = "metal";
    public static final String STONE = "stone";
    public static final String SIZE = "size";

    public static final int CUSTOMIZATION_STEP_WISE_CALL = IDUtils.generateViewId();

    Map<String, List<String>> selectedParams;
    //boolean isDoneBtnCall;

    public static final int MIN_AUTO_CUST_CALL = IDUtils.generateViewId();
    public void sendMinAutoCustReq(){
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(MIN_AUTO_CUST_CALL);
        ajaxCallback.method(AQuery.METHOD_GET);
        getAQueryCustom().ajax(ApiKeys.getAutoMinCustomizeAPI(String.valueOf(mProduct.id)), String.class, ajaxCallback);
    }

    public void sendCustomizationToServer(Map<String, List<String>> selectedParams, boolean isDoneBtnCall) {
        //WindowUtils.marginForProgressViewInGrid = 25;
        //this.isDoneBtnCall = isDoneBtnCall;
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        if (isDoneBtnCall && dontCallNextTime) {
            dismissOverLay();
            mProdCustFrag.clearSelection(true);
            return;
        }
        this.selectedParams = selectedParams;
        cartRequestDataObj.setSelectedParams(selectedParams);
        //mProduct.setSelectedParams(selectedParams);
        Map<String, Object> paramsMap = new HashMap<>();
        Set<String> keys = selectedParams.keySet();
        for (String str : keys) {
            /*if (str.equals(STONE)) {
                JSONArray jsonArray = new JSONArray();
                for (String stoneval : selectedParams.get(str))
                    jsonArray.put(stoneval);
                paramsMap.put(str, jsonArray);
            } else*/
            if (str.equals(SIZE)) {
                if (keys.contains(METAL))
                    paramsMap.put("met", getFactor(mProdCustFrag.getSelectedMetalSwatch()));
                else
                    paramsMap.put("met", getFactor(ProductOptions.mCustDefVals.getDefMetalSwatch()));
            }
            paramsMap.put(str, selectedParams.get(str).get(0));
        }
        paramsMap.put("prodId", mProduct.id);
        paramsMap.put("version", "0.02");
        Log.d("djprod", "req Params- sendCustomizationToServer-ProductActivity: " + paramsMap);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(CUSTOMIZATION_STEP_WISE_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQuery().ajax(ApiKeys.getPriceForCustomizedProdAPI(), paramsMap, String.class, ajaxCallback);
    }


    public AddToCartRequestDataObj getCartReqObj(){
        return cartRequestDataObj;
    }


    private ArrayList<String> productInfoList;
    private final int PROD_INFO_2nd_API_CALL = IDUtils.generateViewId();
    private boolean isLastCallATC = false;

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {

        Log.d("djprod", "url queried- ProductActivity: " + url);
        Log.d("djprod", "response- ProductActivity: " + json);
        if (id == ADD_TO_CART_CALL)
            isLastCallATC = true;
        if (/*id != postCallToken &&*/ id != ADD_TO_CART_CALL) {
            dismissOverLay();
        }
        if (id == DESC_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mTabLayout, this);
            if (json == null)
                return;
            if (success) {
                productInfoList = new ArrayList<>();
                try {
                    JSONObject jsonObj = new JSONObject(json.toString());
                    if (!jsonObj.isNull("productWarranty")) {
                        try {
                            productInfoList.add(jsonObj.getString("productWarranty"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            productInfoList.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productMBP")) {
                        try {
                            productInfoList.add(jsonObj.getString("productMBP"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            productInfoList.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productCert")) {
                        try {
                            productInfoList.add(jsonObj.getString("productCert"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            productInfoList.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productEDTInDays")) {
                        try {
                            productInfoList.add(jsonObj.getString("productEDTInDays"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            productInfoList.add(null);
                        }
                    }
                    if (!jsonObj.isNull("productPayModes")) {
                        try {
                            JSONArray jsonArr = jsonObj.getJSONArray("productPayModes");
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < jsonArr.length(); i++) {
                                sb = sb.append(jsonArr.getString(i) + "\n");
                            }
                            productInfoList.add(sb.toString());
                            Log.d("djprod", "payment modes - serverCallEnds: " + sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            productInfoList.add(null);
                        }
                    }
                    /*ProductInfoFragment pif = (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_PRODUCT + "");
                    pif.setAllDescription(productInfoList);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (id == ADD_TO_CART_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mTabLayout, this);
            if (success) {
                postATC();
                logEventsAnalytics(GAAnalyticsEventNames.CART_PRODUCT_ADDED);
                Log.d(Constants.TAG_APP_EVENT, "AppEventLog: PRODUCT_ADDED_TO_CART");
                //confirmedToCart();
            }
        }

        else if (id == MIN_AUTO_CUST_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mTabLayout, this);
            if (success){
                //// TODO: 21-09-2016
                Gson gson = new Gson();
                MinAutoCustResponse mac = gson.fromJson((String) json, MinAutoCustResponse.class);
                if (mac.getPrice() > 0) {
                    int tocompare;
                    try {
                        tocompare = (int) mac.getPrice();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        tocompare = -1;
                    }
                    if (tocompare > 0) {
                        updateTabsMinAutoCust(tocompare, mac);
                        updateNameDescSKU(mac.getSellerSku());
                        updateDiscountUi(mac.getDiscount());
                    }
                }
            }
        }


        else if (id == CUSTOMIZATION_STEP_WISE_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mTabLayout, this);
            if (success) {
                Gson gson = new Gson();
                CustomizationStepResponse csr = gson.fromJson((String) json, CustomizationStepResponse.class);
                List<Integer> metalDisableList = null;
                List<String> sizeList = null;
                List<Integer> stoneDisableList = null;
                if (csr.getMetal() != null) {
                    metalDisableList = mProductOptions.getDisableList(csr.getMetal(), METAL);
                }
                if (csr.getStone() != null) {
                    stoneDisableList = mProductOptions.getDisableList(csr.getStone(), STONE);
                }
                if (csr.getSize() != null) {
                    List<Double> tempList = csr.getSize();
                    Collections.sort(tempList);
                    ProductOptions.rawSizeListVals = tempList;
                    sizeList = mProductOptions.getParsedSize(tempList);
                }
                if (csr.getPrice() != null) {
                    int tocompare;
                    try {
                        tocompare = (int) Double.parseDouble(csr.getPrice());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        tocompare = -1;
                    }
                    if (tocompare > 0) {
                        //mProduct.unitPrice = tocompare;
                        //mProduct.name = csr.getProdName() == null ? mProduct.name : csr.getProdName();
                        //mProductInfo.description = csr.getProdDesc() == null ? mProductInfo.description : csr.getProdDesc();

                        // mOverlayVH.mProductCost.setText(getFormattedPrice(csr.getPrice()));
                        //mOverlayVH.mProductCost2.setText(getFormattedPrice(csr.getPrice()));
                        //mProdCustFrag.setNewTotalPrice(getFormattedPrice(csr.getPrice()));
                        updateTabs(tocompare, csr);
                        updateNameDescSKU(csr.getSellerSku());
                        updateDiscountUi(csr.getDiscount());
                        dontCallNextTime = true;
                    }

                }
                CustomizationDisableList data = new CustomizationDisableList(metalDisableList, stoneDisableList, sizeList);
                mProdCustFrag.updateCustomizationData(data);
            }
        }
        else if (id == PROD_INFO_2nd_API_CALL){
            try {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                        mTabLayout, this);
                if (success) {
                    if (isToWait) {
                        synchronized (holyString) {
                            holyString.wait();
                        }
                    }
                    dismissOverLay();
                    ProductInfo.parseStoneMetal(mProductInfo, new JSONObject(json.toString()));
                    if (mProdInfoFragment != null)
                        mProdInfoFragment.bindProductInfo(mProductInfo);
                    //mProdCustFrag.setPriceRange(mProductInfo.range);
                    setAddToCartReqObj();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        else {
            if (id == postCallToken){
                //dismissOverLay();
                if (isLastCallATC)
                    confirmedToCart();
            }
            super.serverCallEnds(id, url, json, status);
        }
    }

    public final int ADD_TO_CART_CALL = IDUtils.generateViewId();

    public void addToCartV27(){
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(ADD_TO_CART_CALL);
       /* kjbk
                kjnjkn
                jjjknn;*/

        Map<String, Object> map = new HashMap<>();
        map.put("prodId", cartRequestDataObj.getProdId());
        map.put("prodName", cartRequestDataObj.getProdName());
        map.put("prodType", cartRequestDataObj.getProdType());
        map.put("desgnId", cartRequestDataObj.getDesId());
        map.put("primaryMetal", cartRequestDataObj.getPrimaryMetal());
        map.put("primaryMetalPurity", cartRequestDataObj.getMetalPurity());
        map.put("primaryMetalColor", cartRequestDataObj.getMetalColor());
        map.put("size", cartRequestDataObj.getSize());
        map.put("priceUnits", "Rs");
        map.put("productEDTInDays", mProductInfo.estimatedDelTime);
        map.put("totalPrice", Math.round(cartRequestDataObj.getTotalAmount()));
        map.put("offPrice", Math.round(cartRequestDataObj.getOfferReplicaPrice()));
        map.put("discount", cartRequestDataObj.getDiscount());
        map.put("metalPrice", cartRequestDataObj.getMetalCostPerUnit());
        map.put("stonePrice", cartRequestDataObj.getStonePrice());
        map.put("makingCharges", cartRequestDataObj.getMakingCharges());
        map.put("metalWeight", cartRequestDataObj.getMetalWeight());
        map.put("orderQty", 1);
        map.put("VAT", cartRequestDataObj.getVAT());
        map.put("metalSel", cartRequestDataObj.getMetalSelectedString());
        map.put("stoneSel", cartRequestDataObj.getStoneSelectedString());
        map.put("sessionid", Application.getInstance().getCookies().get(0).getValue());
        Log.d("djprod", "reqParams- addToCartv27: " + map.toString());
        getAQueryCustom().ajax(ApiKeys.getAddtoCartV27(), map,String.class, ajaxCallback);
    }

    private AddToCartRequestDataObj cartRequestDataObj;
    private void setAddToCartReqObj(){
        cartRequestDataObj = new AddToCartRequestDataObj();
        cartRequestDataObj.setSelectedParams(new HashMap<String, List<String>>());
        ArrayList<Map.Entry<String, Float>> p = mProductOptions.priceBreakDown;
        for (Map.Entry<String, Float> entry : p) {
            String name = entry.getKey();
            Float price = entry.getValue();
            if (name.equals(ProductCustomiseFragment.PBD_making))
                cartRequestDataObj.setMakingCharges(price);
            else if (name.equals(ProductCustomiseFragment.PBD_stone))
                cartRequestDataObj.setStonePrice(price);
            /*else {
                break;
            }*/
        }
        if (mProductOptions.discount > 0) {
            cartRequestDataObj.setOfferReplicaPrice(String.valueOf(mProduct.unitPrice));
            cartRequestDataObj.setDiscount(mProductOptions.discount);
            cartRequestDataObj.setTotalAmount
                    (String.valueOf(RandomUtils.getOfferPrice(mProductOptions.discount, String.valueOf(mProduct.unitPrice))));
        }
        else {
            cartRequestDataObj.setOfferReplicaPrice(String.valueOf(0));
            cartRequestDataObj.setDiscount(0);
            cartRequestDataObj.setTotalAmount(String.valueOf(mProduct.unitPrice));
        }
        cartRequestDataObj.setStoneSwatch(mProductOptions.mCustDefVals.getDefStoneSwatch());
        cartRequestDataObj.setMetalSwatch(mProductOptions.mCustDefVals.getDefMetalSwatch());
        cartRequestDataObj.setMetalWeight(mProductInfo.metalWeight);
        cartRequestDataObj.setMetalSelectedString(mProductOptions.mCustDefVals.getRawDefMetal());
        cartRequestDataObj.setStoneSelectedString(mProductOptions.mCustDefVals.getRawDefStone());
        cartRequestDataObj.setProdId(mProduct.id);
        cartRequestDataObj.setProdName(mProduct.name);
        cartRequestDataObj.setProdType(mProductOptions.prodType);
        cartRequestDataObj.setSize(String.valueOf(mProductOptions.size));
        cartRequestDataObj.setDesId(mProduct.userId);
    }

    String holyString = "bohr-tesla";

    boolean isToWait = true;

    private void updateNameDescSKU(String SKU) {
        mProduct.name = getNewProdName(SKU);
        cartRequestDataObj.setProdName(mProduct.name);
        mOverlayVH.mProductName.setText(mProduct.name);
        mOverlayVH.mProductName2.setText(mProduct.name);
        mOverlayVH.toastProdName.setText(mProduct.name);
        mProductInfo.code = SKU;
        mProductInfo.description = getProdNewDesc();
    }

    private void updateImages(Swatches.MixedSwatch swatch) {
        /*String factor = new StringBuilder().append(swatch.getType().charAt(0))
                .append(swatch.getPurity()).append(swatch.getColor().charAt(0)).toString();
        mProduct.defMetal = factor;*/
        getFactor(swatch);
        mProductAdapter.changeData(getVariousProductLooks(mProductAdapter.getImagesUrlList().size(), true));
    }


    private String getFactor(Swatches.MixedSwatch swatch) {
        String factor = new StringBuilder().append(swatch.getType().charAt(0))
                .append(swatch.getPurity()).append(swatch.getColor().charAt(0)).toString();
        mProduct.defMetal = factor;
        return factor;
    }


    private void updateTabsMinAutoCust(int totalPrice, MinAutoCustResponse mac){
        mProduct.unitPrice = totalPrice;
        Swatches.MixedSwatch metalSwatch = null;
        Swatches.MixedSwatch stoneSwatch = null;
        if (!TextUtils.isEmpty(mac.getMetalSwatch()))
            metalSwatch = Swatches.getMixedSwatch(mac.getMetalSwatch(), Swatches.TYPE_METAL);
        if (metalSwatch != null)
            updateMetalInProdInfoTab(metalSwatch, mac.getWeight());
        if (!TextUtils.isEmpty(mac.getStone())) {
            stoneSwatch = ProductOptions.getParsedStoneSwatch(mac.getStone());
            if (stoneSwatch != null){
                cartRequestDataObj.setStoneSwatch(stoneSwatch);
                ProductOptions.mCustDefVals.setStoneDescTxt(stoneSwatch.getSwatchDisplayTxt());
                StoneDetail stoneDetail = mProductInfo.stonesDetails.get(0);
                String[] array = stoneSwatch.getSwatchDisplayTxt().split("-");
                stoneDetail.color = array[0];
                stoneDetail.clarity = array[1];
                mProductInfo.stonesDetails.set(0, stoneDetail);
            }

        }

        if (mProdInfoFragment != null) {
            //mProdInfoFragment.bindProductInfo(mProductInfo);
            mProdInfoFragment = null;
        }

        if (metalSwatch == null)
            metalSwatch = ProductOptions.mCustDefVals.getDefMetalSwatch();
        ProductCustomizationTabUpdationDataObj dataObj = new ProductCustomizationTabUpdationDataObj();

        ProductOptions.mCustDefVals.setResIdMetal(metalSwatch.getSwatchDisplayIconResId());
        dataObj.setMetalCostPerUnit(Float.parseFloat(metalSwatch.getCostPerUnit()))
                .setMetalWeight(Float.parseFloat(metalSwatch.getWeight()))
                .setStonesTotalCost((int) ProductOptions.stonePrice)
                .setDiscount(/*mProductOptions.discount*/mac.getDiscount())
                .setTotalCost(totalPrice);
        //}
        if (metalSwatch != null){
            ProductOptions.mCustDefVals.setDefMetalSwatch(metalSwatch);
            ProductOptions.mCustDefVals.setRawDefMetal(mac.getMetalSwatch());
            ProductOptions.mCustDefVals.setResIdMetal(metalSwatch.getSwatchDisplayIconResId());
        }
        if (stoneSwatch != null) {
            ProductOptions.mCustDefVals.setDefStoneSwatch(stoneSwatch);
            ProductOptions.mCustDefVals.setStoneDescTxt(stoneSwatch.getSwatchDisplayTxt());
            ProductOptions.mCustDefVals.setRawDefStone(mac.getStone());
        }
        if (mac.getSize() > 0) {
            ProductOptions.mCustDefVals.setSizeText(ProductOptions.parseSize(mac.getSize(), mProductOptions.prodType));
            cartRequestDataObj.setSize(String.valueOf(mac.getSize()));
        }

        updateCustomizationTab(dataObj);

    }

    private void updateTabs(int totalPrice, CustomizationStepResponse csr) {
        mProduct.unitPrice = totalPrice;
        Swatches.MixedSwatch metalSwatch = null;
        if (selectedParams.containsKey(METAL) || selectedParams.containsKey(SIZE)) {

            /*if (selectedParams.containsKey(SIZE))
                cartRequestDataObj.setSize(String.valueOf(csr.getSize().get(0)));*/
            if (selectedParams.containsKey(METAL))
                metalSwatch = mProdCustFrag.getSelectedMetalSwatch();
            else {
                metalSwatch = Swatches.getMixedSwatch(csr.getMetalSwatch(), Swatches.TYPE_METAL);
            }

            updateMetalInProdInfoTab(metalSwatch, csr.getWeight());
           /* metalSwatch = mProdCustFrag.getSelectedMetalSwatch();
            updateImages(metalSwatch);
            mProductInfo.metalrate = Float.parseFloat(metalSwatch.getCostPerUnit());
            mProductInfo.metalPurity = Integer.parseInt(metalSwatch.getPurity());

            float tempMetalWeight = mProductInfo.metalWeight;
            mProductInfo.metalWeight = Float.parseFloat(metalSwatch.getWeight());
            mProductInfo.weight = mProductInfo.weight - tempMetalWeight + mProductInfo.metalWeight;*/
        }
        if (selectedParams.containsKey(STONE)) {
            Swatches.MixedSwatch stoneSwatch = mProdCustFrag.getSelectedStoneSwatch();
            cartRequestDataObj.setStoneSwatch(stoneSwatch);
            ProductOptions.mCustDefVals.setStoneDescTxt(stoneSwatch.getSwatchDisplayTxt());
            StoneDetail stoneDetail = mProductInfo.stonesDetails.get(0);
            String[] array = stoneSwatch.getSwatchDisplayTxt().split("-");
            stoneDetail.color = array[0];
            stoneDetail.clarity = array[1];
            mProductInfo.stonesDetails.set(0, stoneDetail);
            //mProductInfo.stonesDetails // TODO: 12-08-2016
        }
        if (mProdInfoFragment != null) {
            //mProdInfoFragment.bindProductInfo(mProductInfo);
            mProdInfoFragment = null;
        }

        if (metalSwatch == null)
            metalSwatch = ProductOptions.mCustDefVals.getDefMetalSwatch();
        ProductCustomizationTabUpdationDataObj dataObj = new ProductCustomizationTabUpdationDataObj();
        /*if (metalSwatch != null) {*/

        ProductOptions.mCustDefVals.setResIdMetal(metalSwatch.getSwatchDisplayIconResId());
        dataObj.setMetalCostPerUnit(Float.parseFloat(metalSwatch.getCostPerUnit()))
                .setMetalWeight(Float.parseFloat(metalSwatch.getWeight()))
                .setStonesTotalCost((int) ProductOptions.stonePrice)
                .setDiscount(/*mProductOptions.discount*/csr.getDiscount())
                .setTotalCost(totalPrice);
        //}
        updateCustomizationTab(dataObj);
    }


    private void updateMetalInProdInfoTab(Swatches.MixedSwatch metalSwatch, float weight) {
        updateImages(metalSwatch);
        cartRequestDataObj.setMetalSwatch(metalSwatch);
        mProductInfo.metalrate = Float.parseFloat(metalSwatch.getCostPerUnit());
        mProductInfo.metalPurity = (int) Float.parseFloat(metalSwatch.getPurity());
        mProductInfo.metalColor = metalSwatch.getColor();

        float tempMetalWeight = mProductInfo.metalWeight;
        mProductInfo.metalWeight = weight == -1 ? Float.parseFloat(metalSwatch.getWeight()) : weight;
        mProductInfo.weight = mProductInfo.weight - tempMetalWeight + mProductInfo.metalWeight;
        cartRequestDataObj.setMetalWeight(mProductInfo.weight);
    }


    boolean dontCallNextTime = false;
    //boolean isDoneBtnCall = false;

    private String getFormattedPrice(String unformatedPrice) {
        return RandomUtils.getIndianCurrencyFormat(unformatedPrice, true);
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
                mProdInfoFragment =
                        (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(
                                "" + UISTATE_PRODUCT);
                if (mProdInfoFragment != null) mProdInfoFragment.bindCollectionUI(mCollection);
                if (!TextUtils.isEmpty(mCollection.name)) {
                    mOverlayVH.mProductCollection.setText(mCollection.name.trim());
                    UiRandomUtils.underLineTv(mOverlayVH.mProductCollection, 0, mOverlayVH.mProductCollection.length());
                }
            } else {
                mOverlayVH.mProductCollection.setText("");
            }

        }

        @Override
        public void onLoaderReset(Loader<ObjectAsyncLoader.Result> loader) {

        }
    }

    /*public void mFollower() {
        mUser = UserInfoCache.getInstance(mContext).getUserInfoDB(mProduct.userId, true);
        if (mUser != null) {
            mOverlayVH.followButton.setTag(mUser);
            mOverlayVH.followButton.setSelected(mUser.isFollowed);
        } else {
            mOverlayVH.mProductOwner.setText("");
            mOverlayVH.followButton.setVisibility(View.GONE);
        }
    }*/


    private String getNewProdName(String SKU) {
        /*Product Name - <Brand Name e.g Gili> <Metal/GS Combo eg. Gold & Diamond>
        <Product Type eg. Earring (will need to remove the s from Earrings in our data) >
        <Seller SKU Code eg. ABCD1234>*/
        StringBuilder sb = new StringBuilder(mUser.name).
                append(" ").append(mProductOptions.primaryMetal);
        if (doesContainsDiamond()) {
            sb.append(" & Diamond");
        }
        sb.append(" ").append(mProductOptions.prodType)
                .append("-").append(SKU);

        Log.d("djprod", "getNewProdName - val: " + sb.toString());
        return sb.toString();
    }

    private boolean doesContainsDiamond() {
        if (mProductInfo.stonesDetails.size() <= 0)
            return false;
        for (StoneDetail std : mProductInfo.stonesDetails) {
            if (std.type.equalsIgnoreCase("diamond"))
                return true;
        }
        return false;
    }


    private String getProdNewDesc() {
        /*<Product Type eg. Earring (will need to remove the s from Earrings in our data)> by <Brand Name e.g Gili>
           Metal Part (will always be present): with hallmarked <Metal Weight> <Metal Purity> <Metal Color> <Metal Type>
            Diamond Part (will be present only when a product has diamond): and fully certified real
            <Sum of all diamond weights from GS_1 to GS_11, if GS = "Diamond"> cts. <Diamond color clarity, GS_1 eg. GH-VS> diamonds
            Other Gemstone Part (will be present only when a product has GS other than diamond):
        and <GS Names, comma separated names from GS_1 to GS_11 eg - Ruby, Emerald and Pearl>,

        NEW: Earrings by GoldAdorn Design WITH 1.13 <space> gms 14K White GOLD, AND 0.23 CTS GHI-SI diamonds
        */
        /*String intials = "";
        if (mProductOptions.prodType.equalsIgnoreCase("earrings"))
            intials = mProductOptions.prodType;
        else{
            if(String.valueOf(mProductOptions.prodType.charAt(mProductOptions.prodType.length() - 1)).equals("s")){
                intials = new StringBuilder(mProductOptions.prodType).deleteCharAt(mProductOptions.prodType.length() - 1).toString();
            }else intials = mProductOptions.prodType;
        }*/
        StringBuilder sb = new StringBuilder(/*intials*/mProductOptions.prodType)
                .append(" by ").append(mUser.name).append(" ")
                .append("with ").append(mProductInfo.metalWeight).append(" gms").append(" ")
                .append(mProductInfo.metalPurity).append(mProductInfo.metalPurityInUnits).append(" ")
                .append(mProductInfo.metalColor).append(" ").append(mProductInfo.metalType);
        sb.append(getDiamondDesc());
        sb.append(getOtherGSDesc());

        Log.d("djprod", "getProdNewDesc - val: " + sb.toString());
        return sb.toString();
    }


    private String getDiamondDesc() {
        if (mProductInfo.stonesDetails.size() <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (StoneDetail stds : mProductInfo.stonesDetails) {
            if (stds.type.equalsIgnoreCase("diamond")) {
                sb.append(", and ").append(getWeights(mProductInfo.stonesDetails)).append(" cts ")
                        .append(stds.color).append("-").append(stds.clarity).append(" quality").append(" ").append("diamonds");
                break;
            }
        }
        Log.d("djprod", "getDiamondDesc - val: " + sb.toString());
        return sb.toString();
    }

    private String getWeights(ArrayList<StoneDetail> list) {
        float sum = 0;
        for (StoneDetail std : list) {
            /*if (std.type.equalsIgnoreCase("diamond"))*/
            sum = sum + std.weight;
        }
        return String.valueOf(sum);
    }


    private String getOtherGSDesc(/*StringBuilder sb*/) {
        if (mProductInfo.stonesDetails.size() <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        boolean isSendEmpty = true;
        sb.append(" and ");
        for (StoneDetail std : mProductInfo.stonesDetails) {
            if (!std.type.equalsIgnoreCase("diamond")) {
                isSendEmpty = false;
                sb.append(std.type).append(", ");
            }
        }
        if (isSendEmpty)
            return "";
        else {
            String toSend = sb.substring(0, sb.toString().lastIndexOf(','));
            Log.d("djprod", "getOtherGSDesc - val: " + toSend);
            return toSend;
        }
    }
}