package com.goldadorn.main.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.LoginResult;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.model.ServerError;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ObjectResponse;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.utils.URLHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.gson.Gson;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.rey.material.widget.ProgressView;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends BaseActivity {

    @Bind(R.id.email_input_layout)
    TextInputLayout mEmailLayout;

    @Bind(R.id.send)
    Button send;

    @Bind(R.id.email)
    EditText email;

    @Bind(R.id.password_desc)
    TextView password_desc;

    @Bind(R.id.layoutParent)
    ViewGroup layoutParent;
    @Bind(R.id.progressBar)
    ProgressView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        ButterKnife.bind(this);

        mEmailLayout.setHint(getString(R.string.hint_email));
        mEmailLayout.setHintAnimationEnabled(true);

        if (b != null) {
            String email = b.getString("email");
            mEmailLayout.getEditText().setText(email);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailLayout.getEditText().getText().toString();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    mEmailLayout.setErrorEnabled(false);
                    resetPassword(mEmailLayout.getEditText().getText().toString());
                } else {
                    mEmailLayout.setErrorEnabled(true);
                    mEmailLayout.setError("Please provide a valid email");
                }
            }});

        TypefaceHelper.setFont(password_desc,mEmailLayout,email,send);
    }

    final private int loginServiceCall = IDUtils.generateViewId();
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        if (id == loginServiceCall) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            if (success) {
                Gson gson = new Gson();
                ServerError loginResult = gson.fromJson((String) json, ServerError.class);
                if (loginResult.getSuccess()) {
                    stopProgress(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link has been sent to your email ID.", Toast.LENGTH_SHORT).show();
                    new Action(this).launchActivity(LoginPageActivity.class,true);
                    finish();

                } else {
                    final Snackbar snackbar = Snackbar.make(layoutParent, loginResult.getMsg(), Snackbar.LENGTH_SHORT);
                    ColoredSnackbar.alert(snackbar).show();
                    stopProgress(false);
                }

            } else {
                stopProgress(false);
            }
        } else
            super.serverCallEnds(id, url, json, status);
    }
    private void resetPassword(String userName)
    {
        startProgress();
        String url = getUrlHelper().getForgotPasswordServiceURL();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(loginServiceCall);
        Map<String, String> params = new HashMap<>();
        params.put(URLHelper.LOGIN_PARAM.USERNAME, userName);
        ajaxCallback.setClazz(String.class);

        ajaxCallback.setParams(params);
        ajaxCallback.setClazz(String.class);
        getAQuery().ajax(url, params, String.class, ajaxCallback);
    }

    protected void startProgress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0f);
        progressBar.start();

        email.setEnabled(false);
        send.setEnabled(false);
    }

    protected void stopProgress(Boolean success) {
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(0f);
        progressBar.stop();
        email.setEnabled(true);
        send.setEnabled(true);
    }

    public static Intent getLaunchIntent(Context context, String email) {
        Intent in = new Intent(context, ForgotPasswordActivity.class);
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            in.putExtra("email", email);
        return in;
    }
}
