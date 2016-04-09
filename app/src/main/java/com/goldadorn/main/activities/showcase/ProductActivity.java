package com.goldadorn.main.activities.showcase;

import android.annotation.TargetApi;
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
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseDrawerActivity;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ObjectAsyncLoader;
import com.goldadorn.main.assist.UserInfoCache;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.ProductOptions;
import com.goldadorn.main.model.User;
import com.goldadorn.main.model.Value;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.mikepenz.iconics.view.IconicsButton;
import com.viewpagerindicator.CirclePageIndicator;

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

    public static Intent getLaunchIntent(Context context, Product product) {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
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
        ArrayList<String> data = new ArrayList<>(1);
        data.add(mProduct.getImageUrl());


        mContext = this;
        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout, mAppBarLayout);
        initTabs();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.8f * dm.heightPixels);
        mCollapsedHeight = (int) (.4f * dm.heightPixels);


        mOverlayVH.pager.setAdapter(
                mProductAdapter = new ProductPagerAdapter(getSupportFragmentManager(), data));
        mOverlayVH.indicator.setViewPager(mOverlayVH.pager);

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

        ProductResponse response = new ProductResponse();
        response.productId = 68;
        response.product = mProduct;
        UIController.getProductBasicInfo(mContext, response,
                new IResultListener<ProductResponse>() {
                    @Override
                    public void onResult(ProductResponse result) {
                        if (result.success) {
                            mProductInfo = result.info;
                            mProductAdapter.changeData(mProductInfo.images);
                            ProductInfoFragment f = (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_PRODUCT + "");
                            if (f != null)
                                f.bindProductInfo(mProductInfo);

                        }
                    }
                });
        UIController.getProductOptions(mContext, response, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                if (result.success) {
                    mProductOptions = result.options;
                    ProductCustomiseFragment f = (ProductCustomiseFragment) getSupportFragmentManager().findFragmentByTag(UISTATE_CUSTOMIZE + "");
                    if (f != null)
                        f.bindProductOptions(mProductOptions);
                }
            }
        });
        configureUI(UISTATE_CUSTOMIZE);
        getSupportLoaderManager().initLoader(mCollectionCallBack.hashCode(), null,
                mCollectionCallBack);
    }

    public void addCustomisation(String key, Value value) {
        Toast.makeText(mContext, key + " : " + value, Toast.LENGTH_SHORT).show();
        mProduct.addCustomisation(key, value);
        // call api for updated price
        UIController.getPriceForCustomization(mContext, mProduct, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {

            }
        });
    }

    private void bindOverlay() {
        mOverlayVH.likesCount.setText(String.format(Locale.getDefault(), "%d", mProduct.likecount));
        mOverlayVH.mProductName.setText(mProduct.name);
        mOverlayVH.mProductName2.setText(mProduct.name);
        mUser = UserInfoCache.getInstance(mContext).getUserInfo(mProduct.userId, true);
        if (mUser != null) {
            mOverlayVH.mProductOwner.setText("By " + mUser.getName());
        } else {
            mOverlayVH.mProductOwner.setText("");
            mOverlayVH.followButton.setVisibility(View.GONE);
        }
        mOverlayVH.mProductCost.setText(mProduct.getDisplayPrice());
        mOverlayVH.mProductCost2.setText(mProduct.getDisplayPrice());
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
            f = new SocialFeedFragment();
            mFrame.setVisibility(View.INVISIBLE);
            mFrameNoScrollDummy.setVisibility(View.VISIBLE);
        } else if (uiState == UISTATE_PRODUCT) {
            f = new ProductInfoFragment();
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
        }
    }

    public void addToCart() {
        UIController.addToCart(mContext, mProduct,
                new IResultListener<ProductResponse>() {

                    @Override
                    public void onResult(ProductResponse result) {
                        Toast.makeText(mContext,
                                result.success ? "Added to cart successfully!" :
                                        "Adding to cart failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private class ProductPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> images;

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
            ShowcaseFragment f = new ShowcaseFragment();
            Bundle b = new Bundle(1);
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

            like.setOnClickListener(this);
            cartButton.setOnClickListener(this);
            followButton.setOnClickListener(this);
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
            } else if (v == like) {
                v.setEnabled(false);
                final boolean isLiked = v.isSelected();
                UIController.like(v.getContext(), mProduct, !isLiked,
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                v.setEnabled(true);
                                v.setSelected(result.success != isLiked);
                            }
                        });
            } else if (v == shareButton) {
                //todo like click
                Toast.makeText(v.getContext(), "Share click!", Toast.LENGTH_SHORT).show();
            } else if (v == buyNoBuyButton) {
                startActivity(PostPollActivity.getLaunchIntent(mContext, mProduct));
            } else if (v == wishlistButton) {
                //todo wishlist click
                Toast.makeText(v.getContext(), "wishlist click!", Toast.LENGTH_SHORT).show();
            } else if (v == cartButton) {
                addToCart();
            } else if (v == followButton) {
                v.setEnabled(false);
                final boolean isFollowing = v.isSelected();
                UIController.follow(mContext, mUser, !isFollowing,
                        new IResultListener<LikeResponse>() {

                            @Override
                            public void onResult(LikeResponse result) {
                                v.setEnabled(true);
                                v.setSelected(result.success != isFollowing);
                            }
                        });
            }
        }
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
                ProductInfoFragment f =
                        (ProductInfoFragment) getSupportFragmentManager().findFragmentByTag(
                                "" + UISTATE_PRODUCT);
                if (f != null) f.bindCollectionUI(mCollection);
                mOverlayVH.mProductCollection.setText(mCollection.name);
            } else {
                mOverlayVH.mProductCollection.setText("");
            }

        }

        @Override
        public void onLoaderReset(Loader<ObjectAsyncLoader.Result> loader) {

        }
    }
}
