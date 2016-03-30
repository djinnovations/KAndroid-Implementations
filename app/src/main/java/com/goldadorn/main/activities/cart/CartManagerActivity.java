package com.goldadorn.main.activities.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.model.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class CartManagerActivity extends FragmentActivity implements ICartData, ILoadingProgress {
    public static final int UISTATE_CART = 0;
    public static final int UISTATE_ADDRESS = 1;
    public static final int UISTATE_PAYMENT = 2;
    public static final int UISTATE_FINAL = 3;
    public static final int UISTATE_OVERLAY_ADD_ADDRESS = 10;
    private int mUIState = UISTATE_CART;
    private int mMainUiState = UISTATE_CART;
    Context mContext;


    @Bind(R.id.continueButton)
    TextView mContinueButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.frame_overlay)
    View mOverlayFrame;
    @Bind(R.id.container_progress_image)
    LinearLayout mContainerProgressImage;
    @Bind(R.id.scrollview)
    ScrollView mScollView;

    ProgressDialog mProgressDialog;

    public List<Product> mCartItems = new ArrayList<>();
    public double mCostTotal;
    private boolean mPaymentSuccess;

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, CartManagerActivity.class);
        return in;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_cart_manager);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);

        ((TextView) mContainerProgressImage.getChildAt(UISTATE_CART).findViewById(R.id.text)).setText("My cart");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_ADDRESS).findViewById(R.id.text)).setText("Address");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_PAYMENT).findViewById(R.id.text)).setText("Payment");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_FINAL).findViewById(R.id.text)).setText("Complete");
        for (int i = 0; i < mContainerProgressImage.getChildCount(); i++) {
            ImageView iv = (ImageView) mContainerProgressImage.getChildAt(i).findViewById(R.id.image);
            iv.setTag(i);
            iv.setOnClickListener(mClickListener);
        }
        mContinueButton.setOnClickListener(mClickListener);
        configureUI(UISTATE_CART);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!closeOverlay())
                    finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (closeOverlay())
            return;
        super.onBackPressed();
    }

    private boolean closeOverlay() {
        if (mUIState != mMainUiState) {
            mOverlayFrame.setVisibility(View.GONE);
            mUIState = mMainUiState;
            refreshToolBar();
            return true;
        }
        return false;
    }

    public void configureUI(int uistate) {
        mUIState = uistate;
        Fragment f = null;
        int frame = R.id.frame;
        if (uistate == UISTATE_CART) {
            f = new MyCartFragment();
            mContinueButton.setText("Select address ->");
            mContinueButton.setVisibility(View.VISIBLE);
        } else if (uistate == UISTATE_ADDRESS) {
            f = new AddressFragment();
            mContinueButton.setVisibility(View.VISIBLE);
            mContinueButton.setText("Proceed to payment");
        } else if (uistate == UISTATE_PAYMENT) {
            f = new PaymentFragment();
            mContinueButton.setVisibility(View.INVISIBLE);
        } else if (uistate == UISTATE_FINAL) {
            f = new SummaryFragment();
            mContinueButton.setText("Go to home");
            mContinueButton.setVisibility(View.VISIBLE);
        } else if (uistate == UISTATE_OVERLAY_ADD_ADDRESS) {
            frame = R.id.frame_overlay;
            f = new AddAddressFragment();
        }
        if (frame == R.id.frame_overlay) {
            mOverlayFrame.setVisibility(View.VISIBLE);
        } else {
            mMainUiState = uistate;
            bindProgress(uistate);
            mOverlayFrame.setVisibility(View.GONE);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(frame, f, String.valueOf(uistate));
            fragmentTransaction.commit();
        }
        mScollView.scrollTo(0, 0);
        refreshToolBar();
    }

    private void refreshToolBar() {
        if (mUIState == UISTATE_OVERLAY_ADD_ADDRESS) {
            mToolbar.setTitle("Add Address");
        } else {
            mToolbar.setTitle("My Cart");
        }

    }

    @Override
    public void showLoading(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }

    private void bindProgress(int index) {
        for (int i = 0; i < mContainerProgressImage.getChildCount(); i++)
            ((ImageView) mContainerProgressImage.getChildAt(i).findViewById(R.id.image)).setImageResource(index >= i ? R.drawable.bg_thumb : R.drawable.bg_thumb_n);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                int uistate = (int) v.getTag();
                configureUI(uistate);
//                Intent in = new Intent(mContext, com.goldadorn.main.activities.showcase.TestActivity.class);
//                startActivity(in);
            } else {
                if (mUIState == UISTATE_CART)
                    configureUI(UISTATE_ADDRESS);
                else if (mUIState == UISTATE_ADDRESS) configureUI(UISTATE_PAYMENT);
                else if (mUIState == UISTATE_PAYMENT) configureUI(UISTATE_FINAL);
                else {
                    bindProgress(-1);
                    configureUI(UISTATE_CART);
                }
            }
        }
    };

    @Override
    public void storeCartData(ArrayList<Product> cart, double costTotal) {
        mCartItems.clear();
        if (cart != null) mCartItems.addAll(cart);
        mCostTotal = costTotal;
    }

    @Override
    public List<Product> getCartProducts() {
        return mCartItems;
    }

    @Override
    public double getBillableAmount() {
        return mCostTotal;
    }

    @Override
    public void setPaymentDone(boolean done) {
        mPaymentSuccess = done;
    }

    @Override
    public boolean isPaymentDone() {
        return mPaymentSuccess;
    }
}
