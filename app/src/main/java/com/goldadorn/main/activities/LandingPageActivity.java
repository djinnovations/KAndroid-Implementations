package com.goldadorn.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.utils.ConnectionDetector;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.actions.Action;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LandingPageActivity extends BaseActivity /*implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/ {

    @Bind(R.id.createAccount)
    Button createAccount;

    @Bind(R.id.loginAccount)
    public Button loginAccount;

    @Bind(R.id.loginWithFacebookButton)
    Button loginWithFacebookButton;

    @Bind(R.id.loginWithGoogleButton)
    Button loginWithGoogleButton;

    @Bind(R.id.loginWithTwitterButton)
    Button loginWithTwitterButton;

    @Bind(R.id.orLabel)
    TextView orLabel;

    //Author DJphy
    private SocialLoginUtil mSocialLoginInstance;



    @OnClick(R.id.createAccount)
    void onClickCreateAccount() {
        new Action(this).launchActivity(RegisterUserActivity.class, true);
    }

    @OnClick(R.id.loginAccount)
    void onClickLogin() {
        new Action(this).launchActivity(LoginPageActivity.class, true);
    }

    @OnClick(R.id.loginWithFacebookButton)
    void onClickLoginWithFacebookButton() {
        if (checkNetwork()){
            mSocialLoginInstance.onFacebookLogin(this);
        }
    }

    @OnClick(R.id.loginWithGoogleButton)
    void onClickLoginWithGoogleButton() {
        if (checkNetwork()){
            mSocialLoginInstance.onGoogleLogin(this);
        }
    }

    @OnClick(R.id.loginWithTwitterButton)
    void onClickLoginWithTwitterButton() {
        if (checkNetwork()){
            mSocialLoginInstance.onTwitterLogin(this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_landing_page);
        ButterKnife.bind(this);
        TypefaceHelper.setFont(loginAccount, createAccount, orLabel);

        mSocialLoginInstance = SocialLoginUtil.getInstance(getApplicationContext());

    }


    private boolean checkNetwork(){

        if (ConnectionDetector.getInstance(getApplicationContext()).isNetworkAvailable()){
            return true;
        }
        else {
            final Snackbar snackbar = Snackbar.make(loginAccount, Constants.ERR_MSG_NETWORK, Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
            return false;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mSocialLoginInstance.handleActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onStart() {
        super.onStart();
        mSocialLoginInstance.onActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSocialLoginInstance.onActivityStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocialLoginInstance.performGoogleLogout();
    }

}
