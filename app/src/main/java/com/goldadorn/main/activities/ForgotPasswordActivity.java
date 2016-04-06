package com.goldadorn.main.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.ProfileData;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ObjectResponse;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Bind(R.id.email_input_layout)
    TextInputLayout mEmailLayout;

    @Bind(R.id.send)
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        mEmailLayout.setHint(getString(R.string.hint_email));
        mEmailLayout.setHintAnimationEnabled(true);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailLayout.getEditText().getText().toString();
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmailLayout.setErrorEnabled(false);
                    UIController.forgotPassword(ForgotPasswordActivity.this, email, new
                            IResultListener<ObjectResponse<ProfileData>>() {
                                @Override
                                public void onResult(ObjectResponse<ProfileData> result) {
                                    if(result.success){
                                        Toast.makeText(ForgotPasswordActivity.this,"Mail sent!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                    mEmailLayout.setErrorEnabled(true);
                    mEmailLayout.setError("Please provide a valid email");
                }
                //// TODO: 6/4/16 call reset password
            }
        });
    }
}
