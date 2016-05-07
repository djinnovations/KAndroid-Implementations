package com.goldadorn.main.activities;

    import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
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

    public class RegisterUserActivity extends BaseActivity {


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

        @Bind(R.id.input_layout_userName)
        TextInputLayout inputLayoutUserName;



        @Bind(R.id.userName)
        EditText userName;

        @Bind(R.id.labelGender)
        TextView labelGender;


        @Bind(R.id.input_layout_password)
        TextInputLayout inputLayoutPassword;

        @Bind(R.id.password)
        EditText password;

        @Bind(R.id.input_layout_password_confirm)
        TextInputLayout inputLayoutPasswordConfirm;

        @Bind(R.id.password_confirm)
        EditText passwordConfirm;

        @Bind(R.id.policyText)
        TextView policyText;



        @Bind(R.id.layoutParent)
        ViewGroup layoutParent;

        @Bind(R.id.input_layout_first_name)
        TextInputLayout inputLayoutFirstName;

        @Bind(R.id.firstName)
        EditText firstName;

        @Bind(R.id.input_layout_last_name)
        TextInputLayout inputLayoutLastName;

        @Bind(R.id.lastName)
        EditText lastName;



        @Bind(R.id.genderRadioGroup)
        RadioGroup genderRadioGroup;



        @Bind(R.id.progressBar)
        ProgressView progressBar;


        @Bind(R.id.maleRadioButton)
        RadioButton maleRadioButton;

        @Bind(R.id.femaleRadioButton)
        RadioButton femaleRadioButton;

        @Bind(R.id.orLabel)
        TextView orLabel;



        @OnClick(R.id.createAccount) void onClickCreateAccount()
        {
            if(!validateFirstName())
                return;
            if(!validateLastName())
                return;
            if(!validateEmail())
                return;
            if(!validatePassword())
                return;
            register(firstName.getText().toString().trim(),lastName.getText().toString().trim(), userName.getText().toString().trim(), password.getText().toString().trim());
        }

        @OnClick(R.id.loginAccount) void onClickLogin()
        {
            new Action(this).launchActivity(LoginPageActivity.class, true);
        }

        public void serverCallEnds(int id,String url, Object json, AjaxStatus status) {
            if(id==regServiceCall)
            {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String)json, status,null, layoutParent, this);

                if(success)
                {
                    Gson gson = new Gson();
                    LoginResult loginResult = gson.fromJson((String)json, LoginResult.class);

                    if(loginResult.getSuccess())
                    {
                        login(userName.getText().toString().trim(), password.getText().toString().trim());
                        //onClickLogin();
                        //Toast.makeText(this, R.string.registerSuccssMsg, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(layoutParent, loginResult.getMsg(), Snackbar.LENGTH_SHORT);
                        ColoredSnackbar.alert(snackbar).show();
                    }
                    stopProgress(loginResult.getSuccess());
                }
                else {
                    stopProgress(success);
                }
            }
            else if (id == loginServiceCall) {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
                List<Cookie> cookies = status.getCookies();
                if (success) {
                    Gson gson = new Gson();
                    LoginResult loginResult = gson.fromJson((String) json, LoginResult.class);

                    if (loginResult.getSuccess()) {
                        User user = new User(Integer.valueOf(loginResult.getUserid()), User.TYPE_INDIVIDUAL);
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
            }else
                super.serverCallEnds(id, url, json, status);

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

        protected void startProgress()
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0f);
            progressBar.start();

            password.setEnabled(false);
            passwordConfirm.setEnabled(false);
            userName.setEnabled(false);
            loginAccount.setEnabled(false);
            loginWithFacebookButton.setEnabled(false);
            loginWithGoogleButton.setEnabled(false);
            loginWithTwitterButton.setEnabled(false);
            createAccount.setEnabled(false);
        }
        protected void stopProgress(Boolean success)
        {
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(0f);
            progressBar.stop();

            password.setEnabled(true);
            passwordConfirm.setEnabled(true);
            userName.setEnabled(true);
            loginAccount.setEnabled(true);
            loginWithFacebookButton.setEnabled(true);
            loginWithGoogleButton.setEnabled(true);
            loginWithTwitterButton.setEnabled(true);
            createAccount.setEnabled(true);
        }

        final private int regServiceCall= IDUtils.generateViewId();
        protected void register(String fName,String lName,String userName,String password)
        {
            startProgress();
            String url = getUrlHelper().getRegisterServiceURL();
            ExtendedAjaxCallback ajaxCallback =getAjaxCallback(regServiceCall);
            Map<String,String> params= new HashMap<>();
            params.put(URLHelper.REGISTER_PARAM.REGISTER_FIRST_NAME, fName);
            params.put(URLHelper.REGISTER_PARAM.REGISTER_LAST_NAME,lName);
            params.put(URLHelper.REGISTER_PARAM.REGISTER_EMAIL, userName);
            params.put(URLHelper.REGISTER_PARAM.REGISTER_PASSWORD, password);
            params.put(URLHelper.REGISTER_PARAM.REGISTER_CONFIRM_PASSWORD, password);

            if(femaleRadioButton.isSelected())
                params.put(URLHelper.REGISTER_PARAM.REGISTER_GENDER, URLHelper.REGISTER_PARAM.REGISTER_GENDER_FEMALE);
            else
                params.put(URLHelper.REGISTER_PARAM.REGISTER_GENDER, URLHelper.REGISTER_PARAM.REGISTER_GENDER_MALE);




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
        private boolean validateFirstName() {
            if (firstName.getText().toString().trim().isEmpty()) {
                inputLayoutFirstName.setError(getString(R.string.err_msg_f_name));
                requestFocus(firstName);
                return false;
            } else {
                inputLayoutFirstName.setErrorEnabled(false);
            }

            return true;
        }

        private boolean validateLastName() {
            if (lastName.getText().toString().trim().isEmpty()) {
                inputLayoutLastName.setError(getString(R.string.err_msg_l_name));
                requestFocus(lastName);
                return false;
            } else {
                inputLayoutLastName.setErrorEnabled(false);
            }

            return true;
        }

        private boolean validatePassword() {
            if (password.getText().toString().trim().isEmpty()) {
                inputLayoutPassword.setError(getString(R.string.err_msg_password));
                requestFocus(password);
                return false;
            }
            else if (passwordConfirm.getText().toString().trim().isEmpty()) {
                inputLayoutPasswordConfirm.setError(getString(R.string.err_msg_password));
                requestFocus(passwordConfirm);
                return false;
            }
            else if (!passwordConfirm.getText().toString().equals(password.getText().toString())) {
                inputLayoutPasswordConfirm.setError(getString(R.string.err_msg_password_not_match));
                requestFocus(passwordConfirm);
                return false;
            }

            else {
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
            setContentView(R.layout.activity_register_user);
            ButterKnife.bind(this);

            userName.addTextChangedListener(new LoginTextWatcher(userName));
            password.addTextChangedListener(new LoginTextWatcher(password));
            passwordConfirm.addTextChangedListener(new LoginTextWatcher(passwordConfirm));
            firstName.addTextChangedListener(new LoginTextWatcher(firstName));
            lastName.addTextChangedListener(new LoginTextWatcher(lastName));


            int iconSize = 15;
            Drawable icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_envelope, R.color.colorPrimary, iconSize);
            userName.setCompoundDrawables(icon, null, null, null);
            userName.setCompoundDrawablePadding(iconSize);


            icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_lock,R.color.colorPrimary,iconSize);
            password.setCompoundDrawables(icon, null, null, null);
            password.setCompoundDrawablePadding(iconSize);


            icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_lock,R.color.colorPrimary,iconSize);
            passwordConfirm.setCompoundDrawables(icon, null, null, null);
            passwordConfirm.setCompoundDrawablePadding(iconSize);


            icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_user,R.color.colorPrimary,iconSize);
            firstName.setCompoundDrawables(icon, null, null, null);
            firstName.setCompoundDrawablePadding(iconSize);

            icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_user,R.color.colorPrimary,iconSize);
            lastName.setCompoundDrawables(icon, null, null, null);
            lastName.setCompoundDrawablePadding(iconSize);



            policyText.setMovementMethod(LinkMovementMethod.getInstance());
            policyText.setText(Html.fromHtml(getResources().getString(R.string.creatingAnAccount)));

            genderRadioGroup.check(R.id.femaleRadioButton);

            TypefaceHelper.setFont(orLabel,femaleRadioButton,maleRadioButton,labelGender,inputLayoutFirstName,inputLayoutLastName,inputLayoutPassword,inputLayoutUserName,userName, password,firstName,lastName, loginAccount, createAccount);
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
                        if(!userName.getText().toString().trim().equals(""))
                            inputLayoutUserName.setErrorEnabled(false);
                        break;
                    case R.id.firstName:
                        if(!firstName.getText().toString().trim().equals(""))
                            inputLayoutFirstName.setErrorEnabled(false);
                        break;
                    case R.id.lastName:
                        if(!lastName.getText().toString().trim().equals(""))
                            inputLayoutLastName.setErrorEnabled(false);
                        break;
                    case R.id.password:
                        if(!password.getText().toString().trim().equals(""))
                            inputLayoutPassword.setErrorEnabled(false);
                        break;
                    case R.id.password_confirm:
                        if(!passwordConfirm.getText().toString().trim().equals(""))
                            inputLayoutPasswordConfirm.setErrorEnabled(false);
                        break;
                }
            }
        }
    }
