package com.goldadorn.main.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.goldadorn.main.R;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class CartManagerActivity extends AppCompatActivity {

    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, CartManagerActivity.class);
        return in;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_manager);
    }
}
