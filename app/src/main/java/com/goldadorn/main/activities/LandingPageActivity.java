package com.goldadorn.main.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.actions.Action;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LandingPageActivity extends BaseActivity {


    @Bind(R.id.createAccount)
    Button createAccount;

    @Bind(R.id.loginAccount)
    Button loginAccount;

    @Bind(R.id.loginWithFacebookButton)
    Button loginWithFacebookButton;

    @Bind(R.id.loginWithGoogleButton)
    Button loginWithGoogleButton;

    @Bind(R.id.loginWithTwitterButton)
    Button loginWithTwitterButton;

    @Bind(R.id.orLabel)
    TextView orLabel;



    @OnClick(R.id.createAccount) void onClickCreateAccount()
    {
        new Action(this).launchActivity(RegisterUserActivity.class,true);
    }

    @OnClick(R.id.loginAccount) void onClickLogin()
    {
        new Action(this).launchActivity(LoginPageActivity.class,true);
    }

    @OnClick(R.id.loginWithFacebookButton) void onClickLoginWithFacebookButton()
    {

    }

    @OnClick(R.id.loginWithGoogleButton) void onClickLoginWithGoogleButton()
    {

    }

    @OnClick(R.id.loginWithTwitterButton) void onClickLoginWithTwitterButton()
    {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_landing_page);
        ButterKnife.bind(this);
        TypefaceHelper.setFont(loginAccount,createAccount,orLabel);
    }
}
