package com.goldadorn.main.activities.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.goldadorn.main.R;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class CartManagerActivity extends AppCompatActivity {
    public static final int UISTATE_CART = 0;
    public static final int UISTATE_ADDRESS = 1;
    public static final int UISTATE_PAYMENT = 2;
    public static final int UISTATE_FINAL = 3;
    private int mUIState = 0;

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, CartManagerActivity.class);
        return in;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_manager);
        loadFragment(UISTATE_CART);
    }

    public void loadFragment(int uistate) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new MyCartFragment());
        fragmentTransaction.commit();
    }
}
