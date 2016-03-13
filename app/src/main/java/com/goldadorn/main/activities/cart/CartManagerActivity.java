package com.goldadorn.main.activities.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class CartManagerActivity extends AppCompatActivity {
    public static final int UISTATE_CART = 0;
    public static final int UISTATE_ADDRESS = 1;
    public static final int UISTATE_PAYMENT = 2;
    public static final int UISTATE_FINAL = 3;
    private int mUIState = 0;
    Context mContext;


    @Bind(R.id.continueButton)
    View mContinueButton;

    @Bind(R.id.container_progress_image)
    LinearLayout mContainerProgressImage;

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
    }

    public void configureUI(int uistate) {
        mUIState = uistate;
        Fragment fragment = null;
        if (uistate == UISTATE_CART) {
            fragment = new MyCartFragment();
        } else if (uistate == UISTATE_ADDRESS) {
            fragment = new AddressFragment();
        } else if (uistate == UISTATE_PAYMENT) {
            fragment = new PaymentFragment();
        } else if (uistate == UISTATE_FINAL) {
            fragment = new SummaryFragment();
        }
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }
        bindProgress(uistate);
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
}
