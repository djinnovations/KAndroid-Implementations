package com.goldadorn.main.activities.showcase;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductSummary;
import com.goldadorn.main.modules.showcase.ShowcaseFragment;
import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.server.response.ProductResponse;
import com.mikepenz.iconics.view.IconicsButton;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductActivity extends BaseDrawerActivity {
//    private final static int UISTATE_CUSTOMIZE = 0;
    private final static int UISTATE_PRODUCT = 0;
    private final static int UISTATE_SOCIAL = 1;
    private static final String TAG = ProductActivity.class.getName();
    public static final String EXTRA_PRODUCT = "product";

    private int mUIState = UISTATE_PRODUCT;

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
    private Product mProduct;
    private Handler mHandler = new Handler();
    private TabViewHolder mTabViewHolder;
    private ViewPager.OnPageChangeListener mPageChangeListener =
            new ViewPager.OnPageChangeListener() {


                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //todo pager page changes
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };

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
        }

        mContext = this;
        mOverlayVH = new OverlayViewHolder(mBrandButtonsLayout, mAppBarLayout);
        initTabs();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mStartHeight = (int) (.8f * dm.heightPixels);
        mCollapsedHeight = (int) (.4f * dm.heightPixels);

        mPagerDummy.getLayoutParams().height = mStartHeight;
        mBrandButtonsLayout.getLayoutParams().height = mStartHeight;
        mToolBar.getLayoutParams().height = mCollapsedHeight;

        final int tabStart = mStartHeight - getResources().getDimensionPixelSize(
                R.dimen.tab_height) + getResources().getDimensionPixelSize(R.dimen.shadow_height);

        mOverlayVH.pager.setAdapter(
                mProductAdapter = new ProductPagerAdapter(getSupportFragmentManager()));
        mOverlayVH.indicator.setViewPager(mOverlayVH.pager);

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


        configureUI(UISTATE_PRODUCT);
        ProductResponse response= new ProductResponse();
        response.productId=mProduct.id;
        UIController.getProductBasicInfo(mContext, response, new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                if (result.success) {
                    ProductSummary summary = result.summary;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.nav_my_overflow).setVisible(false);
        menu.findItem(R.id.nav_my_notifications).setIcon(R.drawable.vector_icon_bell_dark);
        menu.findItem(R.id.nav_my_search).setIcon(R.drawable.vector_icon_search_dark);
        return value;
    }

    private void initTabs() {
        mTabViewHolder = new TabViewHolder(mContext, mTabLayout);
        mTabViewHolder.initTabs( getString(R.string.products),
                getString(R.string.social),null, new TabViewHolder.TabClickListener() {
                    @Override
                    public void onTabClick(int position) {
                        configureUI(position);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mOverlayVH.indicator.setOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mOverlayVH.indicator.setOnPageChangeListener(null);
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
            f = new CustomizeFragment();
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(id, f);
            fragmentTransaction.commit();
        }
    }

    private class ProductPagerAdapter extends FragmentStatePagerAdapter {
        Cursor cursor = null;

        public ProductPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public Fragment getItem(int position) {
            ShowcaseFragment f = new ShowcaseFragment();
            Bundle b = new Bundle(1);
            b.putInt(ShowcaseFragment.EXTRA_CATEGORY_POSITION, position);
            f.setArguments(b);
            return f;
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
        @Bind(R.id.product_collection_name_2)
        TextView mProductCollection2;
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
            like.setOnClickListener(this);
            cartButton.setOnClickListener(this);
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
                    mProductCollection2.setVisibility(View.GONE);
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
                    mProductCollection2.setVisibility(View.VISIBLE);
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
                UIController.like(v.getContext(), mProduct, !v.isSelected(),
                        new IResultListener<LikeResponse>() {
                            @Override
                            public void onResult(LikeResponse result) {
                                v.setEnabled(true);
                                v.setSelected(result.success);
                            }
                        });
            } else if (v == shareButton) {
                //todo like click
                Toast.makeText(v.getContext(), "Share click!", Toast.LENGTH_SHORT).show();
            } else if (v == buyNoBuyButton) {
                //todo buy no buy click
                Toast.makeText(v.getContext(), "Buy No buy click!", Toast.LENGTH_SHORT).show();
            } else if (v == wishlistButton) {
                //todo wishlist click
                Toast.makeText(v.getContext(), "wishlist click!", Toast.LENGTH_SHORT).show();
            } else if (v == cartButton) {
                UIController.addToCart(v.getContext(), mProduct,
                        new IResultListener<ProductResponse>() {

                            @Override
                            public void onResult(ProductResponse result) {
                                Toast.makeText(v.getContext(),
                                        result.success ? "Added to cart successfully!" :
                                                "Adding to cart failed.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }
}
