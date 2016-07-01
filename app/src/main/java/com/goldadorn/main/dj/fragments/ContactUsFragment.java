package com.goldadorn.main.dj.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.WebActivity;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 14-06-2016.
 */
public class ContactUsFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us_new, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Bind(R.id.etName)
    EditText etName;
    @Bind(R.id.etEmail)
    EditText etEmail;
    @Bind(R.id.etMsgBody)
    EditText etMsgBody;
    @Bind(R.id.btnSend)
    Button btnSend;
    @Bind(R.id.cbEmail)
    CheckBox cbEmail;
    @Bind(R.id.progressBar)
    ProgressView progressBar;

    List<EditText> myInputs;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife.bind(view);
        progressBar.setVisibility(View.INVISIBLE);
        btnSend.setOnClickListener(btnSendClick);
        myInputs = Arrays.asList(new EditText[]{etName, etEmail, etMsgBody});
    }


    View.OnClickListener btnSendClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getParams() != null)
                ((WebActivity) getActivity()).sendContactUsInfoToServer(getParams(), progressBar);
        }
    };

    private Map<String, String > getParams(){
        Map<String, String > params = new HashMap<>();
        if (canProceed()){
            params.put("contactus_name", etName.getText().toString().trim());
            params.put("contactus_email", etEmail.getText().toString().trim());
            params.put("contactus_message", etMsgBody.getText().toString().trim());
            String cbstat = cbEmail.isChecked() ? "on" : "off";
            params.put("contactus_copy", cbstat);
            return params;
        }else return null;
    }

    private boolean canProceed() {
        for (EditText et: myInputs){
            if (TextUtils.isEmpty(et.getText().toString().trim())){
                Toast.makeText(getContext(), "fields are empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public void clearForm(){
        for (EditText et: myInputs){
            et.setText("");
        }
    }

}
