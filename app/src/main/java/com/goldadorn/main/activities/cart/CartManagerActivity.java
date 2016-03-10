package com.goldadorn.main.activities.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    @Bind(R.id.continueButton)
    View mContinueButton;

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, CartManagerActivity.class);
        return in;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_manager);
        ButterKnife.bind(this);
        mContinueButton.setOnClickListener(mClickListener);
        configureUI(UISTATE_CART);
    }

    public void configureUI(int uistate) {
        Fragment fragment = null;
        if (uistate == UISTATE_CART) {
            fragment = new MyCartFragment();
        } else if (uistate == UISTATE_ADDRESS) {
            fragment = new AddressFragment();
        } else if (uistate == UISTATE_PAYMENT) {
            fragment = new AddressFragment();
        }
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mUIState == UISTATE_CART)
                configureUI(UISTATE_ADDRESS);
            else if (mUIState == UISTATE_ADDRESS) configureUI(UISTATE_PAYMENT);
            else if (mUIState == UISTATE_FINAL) configureUI(UISTATE_FINAL);
            else configureUI(UISTATE_CART);
        }
    };
}
