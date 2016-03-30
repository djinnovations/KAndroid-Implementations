package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class AddAddressFragment extends Fragment {
    View mDoneButton;
    TextInputLayout mNameInput, mStreetInput, mCityInput, mStateInput, mPinCodeInput, mPhoneNumberInput;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_address, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDoneButton = view.findViewById(R.id.doneButton);
        mNameInput = (TextInputLayout) view.findViewById(R.id.input_name);
        mStreetInput = (TextInputLayout) view.findViewById(R.id.input_address_street);
        mCityInput = (TextInputLayout) view.findViewById(R.id.input_city);
        mStateInput = (TextInputLayout) view.findViewById(R.id.input_state);
        mPinCodeInput = (TextInputLayout) view.findViewById(R.id.input_pincode);
        mPhoneNumberInput = (TextInputLayout) view.findViewById(R.id.input_phone_number);

        mDoneButton.setOnClickListener(mClick);
    }


    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


        }
    };
}
