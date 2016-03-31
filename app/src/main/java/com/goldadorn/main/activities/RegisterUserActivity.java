package com.goldadorn.main.activities;

    import android.graphics.drawable.Drawable;
    import android.os.Bundle;
    import android.os.Handler;
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
    import android.widget.Toast;

    import com.androidquery.callback.AjaxStatus;
    import com.goldadorn.main.R;
    import com.goldadorn.main.icons.IconsUtils;
    import com.goldadorn.main.model.LoginResult;
    import com.goldadorn.main.utils.TypefaceHelper;
    import com.goldadorn.main.utils.URLHelper;
    import com.goldadorn.main.utils.IDUtils;
    import com.goldadorn.main.utils.NetworkResultValidator;
    import com.goldadorn.main.views.ColoredSnackbar;
    import com.google.gson.Gson;
    import com.kimeeo.library.actions.Action;
    import com.kimeeo.library.ajax.ExtendedAjaxCallback;
    import com.mikepenz.fontawesome_typeface_library.FontAwesome;
    import com.rey.material.widget.ProgressView;

    import java.util.HashMap;
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

        @Bind(R.id.input_layout_password)
        TextInputLayout inputLayoutPassword;

        @Bind(R.id.userName)
        EditText userName;

        @Bind(R.id.password)
        EditText password;

        @Bind(R.id.policyText)
        TextView policyText;



        @Bind(R.id.layoutParent)
        ViewGroup layoutParent;

        @Bind(R.id.input_layout_name)
        TextInputLayout inputLayoutName;

        @Bind(R.id.name)
        EditText name;

        @Bind(R.id.genderRadioGroup)
        RadioGroup genderRadioGroup;



        @Bind(R.id.progressBar)
        ProgressView progressBar;


        @Bind(R.id.maleRadioButton)
        RadioButton maleRadioButton;

        @Bind(R.id.femaleRadioButton)
        RadioButton femaleRadioButton;




        @OnClick(R.id.createAccount) void onClickCreateAccount()
        {
            if(!validateName())
                return;
            if(!validateEmail())
                return;
            if(!validatePassword())
                return;
            register(name.getText().toString().trim(), userName.getText().toString().trim(), password.getText().toString().trim());
        }

        @OnClick(R.id.loginAccount) void onClickLogin()
        {
            new Action(this).launchActivity(LoginPageActivity.class, true);
        }

        public void serverCallEnds(int id,String url, Object json, AjaxStatus status) {
            if(id==loginServiceCall)
            {
                boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String)json, status,null, layoutParent, this);

                if(success)
                {
                    Gson gson = new Gson();
                    LoginResult loginResult = gson.fromJson((String)json, LoginResult.class);

                    if(loginResult.getSuccess())
                    {
                        onClickLogin();
                        Toast.makeText(this, R.string.registerSuccssMsg, Toast.LENGTH_SHORT).show();
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
        }

        protected void startProgress()
        {
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
        }
        protected void stopProgress(Boolean success)
        {
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
        }

        final private int loginServiceCall= IDUtils.generateViewId();
        protected void register(String name,String userName,String password)
        {
            startProgress();
            String url = getUrlHelper().getRegisterServiceURL();
            ExtendedAjaxCallback ajaxCallback =getAjaxCallback(loginServiceCall);
            Map<String,String> params= new HashMap<>();
            params.put(URLHelper.REGISTER_PARAM.REGISTER_FIRST_NAME, name);
            params.put(URLHelper.REGISTER_PARAM.REGISTER_LAST_NAME, " ");
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
        private boolean validateName() {
            if (name.getText().toString().trim().isEmpty()) {
                inputLayoutName.setError(getString(R.string.err_msg_name));
                requestFocus(name);
                return false;
            } else {
                inputLayoutName.setErrorEnabled(false);
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
            name.addTextChangedListener(new LoginTextWatcher(name));


            int iconSize = 15;
            Drawable icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_envelope, R.color.colorPrimary, iconSize);
            userName.setCompoundDrawables(icon, null, null, null);
            userName.setCompoundDrawablePadding(iconSize);


            icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_lock,R.color.colorPrimary,iconSize);
            password.setCompoundDrawables(icon, null, null, null);
            password.setCompoundDrawablePadding(iconSize);

            icon = IconsUtils.getFontIconDrawable(this, FontAwesome.Icon.faw_user,R.color.colorPrimary,iconSize);
            name.setCompoundDrawables(icon, null, null, null);
            name.setCompoundDrawablePadding(iconSize);

            policyText.setMovementMethod(LinkMovementMethod.getInstance());
            policyText.setText(Html.fromHtml(getResources().getString(R.string.creatingAnAccount)));

            genderRadioGroup.check(R.id.femaleRadioButton);

            TypefaceHelper.setFont(userName, password,name, loginAccount, createAccount);
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
                    case R.id.name:
                        if(!name.getText().toString().trim().equals(""))
                            inputLayoutName.setErrorEnabled(false);
                        break;

                    case R.id.password:
                        if(!password.getText().toString().trim().equals(""))
                            inputLayoutPassword.setErrorEnabled(false);
                        break;
                }
            }
        }
    }
