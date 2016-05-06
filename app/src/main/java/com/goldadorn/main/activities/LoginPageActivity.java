package com.goldadorn.main.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.utils.ConnectionDetector;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.icons.IconsUtils;
import com.goldadorn.main.model.LoginResult;
import com.goldadorn.main.model.User;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.gson.Gson;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.rey.material.widget.ProgressView;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginPageActivity extends BaseActivity {


    @Bind(R.id.createAccount)
    Button createAccount;

    @Bind(R.id.forgotPasswordButton)
    Button forgotPasswordButton;

    @Bind(R.id.loginAccount)
    Button loginAccount;

    @Bind(R.id.loginWithFacebookButton)
    Button loginWithFacebookButton;

    @Bind(R.id.loginWithGoogleButton)
    Button loginWithGoogleButton;

    @Bind(R.id.loginWithTwitterButton)
    Button loginWithTwitterButton;

    @Bind(R.id.input_layout_userName)
    TextInputLayout inputLayoutUserName;

    @Bind(R.id.input_layout_password)
    TextInputLayout inputLayoutPassword;

    @Bind(R.id.userName)
    EditText userName;

    @Bind(R.id.orLabel)
    TextView orLabel;


    @Bind(R.id.password)
    EditText password;


    @Bind(R.id.layoutParent)
    public ViewGroup layoutParent;

    @Bind(R.id.progressBar)
    ProgressView progressBar;


    //Author DJphy
    private SocialLoginUtil mSocialLoginInstance;

    @OnClick(R.id.createAccount)
    void onClickCreateAccount() {
        new Action(this).launchActivity(RegisterUserActivity.class, true);
    }

    @OnClick(R.id.loginAccount)
    void onClickLogin() {

        if (!validateEmail())
            return;
        if (!validatePassword())
            return;

        login(userName.getText().toString().trim(), password.getText().toString().trim());
    }

    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        if (id == loginServiceCall) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            List<Cookie> cookies = status.getCookies();
            if (success) {
                Gson gson = new Gson();
                LoginResult loginResult = gson.fromJson((String) json, LoginResult.class);

                if (loginResult.getSuccess()) {
                    User user = new User(loginResult.getUserid(), User.TYPE_INDIVIDUAL);
                    user.setName(loginResult.getUsername());
                    user.setImageUrl(loginResult.getUserpic());
                    getApp().setUser(user);

                    getApp().setCookies(cookies);
                    SharedPreferences sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, true)
                            .putString(AppSharedPreferences.LoginInfo.USER_NAME, userName.getText().toString())
                            .putString(AppSharedPreferences.LoginInfo.PASSWORD, password.getText().toString()).commit();

                    gotoApp();
                } else {
                    final Snackbar snackbar = Snackbar.make(layoutParent, loginResult.getMsg(), Snackbar.LENGTH_SHORT);
                    ColoredSnackbar.alert(snackbar).show();
                }
                stopProgress(loginResult.getSuccess());
            } else {
                stopProgress(success);
            }
        } else
            super.serverCallEnds(id, url, json, status);
    }

    protected void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0f);
        progressBar.start();

        password.setEnabled(false);
        userName.setEnabled(false);
        loginAccount.setEnabled(false);
        loginWithFacebookButton.setEnabled(false);
        loginWithGoogleButton.setEnabled(false);
        loginWithTwitterButton.setEnabled(false);
        createAccount.setEnabled(false);
        forgotPasswordButton.setEnabled(false);
    }

    protected void stopProgress(Boolean success) {
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(0f);
        progressBar.stop();
        password.setEnabled(true);
        userName.setEnabled(true);
        loginAccount.setEnabled(true);
        loginWithFacebookButton.setEnabled(true);
        loginWithGoogleButton.setEnabled(true);
        loginWithTwitterButton.setEnabled(true);
        createAccount.setEnabled(true);
        forgotPasswordButton.setEnabled(true);
    }


    final private int loginServiceCall = IDUtils.generateViewId();

    protected void login(String userName, String password) {
        startProgress();
        String url = getUrlHelper().getLoginServiceURL();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(loginServiceCall);
        Map<String, String> params = new HashMap<>();
        params.put(URLHelper.LOGIN_PARAM.USER_NAME, userName);
        params.put(URLHelper.LOGIN_PARAM.PASSWORD, password);
        ajaxCallback.setClazz(String.class);

        ajaxCallback.setParams(params);
        ajaxCallback.setClazz(String.class);
        getAQuery().ajax(url, params, String.class, ajaxCallback);
    }

    private boolean validateEmail() {
        String email = userName.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {

            inputLayoutUserName.setError(getString(R.string.err_msg_email));
            requestFocus(userName);
            return false;
        } else {
            inputLayoutUserName.setErrorEnabled(false);
        }
        return true;

    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @OnClick(R.id.loginWithFacebookButton)
    void onClickLoginWithFacebookButton() {
        //// TODO: 5/6/2016
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
        setContentView(R.layout.activity_login_page);
        ButterKnife.bind(this);

        userName.addTextChangedListener(new LoginTextWatcher(userName));
        password.addTextChangedListener(new LoginTextWatcher(password));

        int iconSize = 15;
        Drawable icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_user, R.color.colorPrimary, iconSize);
        userName.setCompoundDrawables(icon, null, null, null);
        userName.setCompoundDrawablePadding(iconSize);


        icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_lock, R.color.colorPrimary, iconSize);
        password.setCompoundDrawables(icon, null, null, null);
        password.setCompoundDrawablePadding(iconSize);

        TypefaceHelper.setFont(inputLayoutUserName, inputLayoutPassword, userName, password, loginAccount, createAccount, forgotPasswordButton, orLabel);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ForgotPasswordActivity.getLaunchIntent(LoginPageActivity.this,userName.getText().toString()));
            }
        });
//        Intent in = new Intent(this, CartManagerActivity.class);
//        startActivity(in);

        //Author DJphy
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


    private class LoginTextWatcher implements TextWatcher {

        private View view;

        private LoginTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.userName:
                    if (!userName.getText().toString().trim().equals(""))
                        inputLayoutUserName.setErrorEnabled(false);
                    break;
                case R.id.password:
                    if (!password.getText().toString().trim().equals(""))
                        inputLayoutPassword.setErrorEnabled(false);
                    break;
            }
        }
    }
}
