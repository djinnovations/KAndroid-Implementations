package com.goldadorn.main.activities.cart;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldadorn.main.R;
import com.goldadorn.main.db.Tables;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class AddAddressFragment extends Fragment {
    View mDoneButton;
    TextInputLayout mNameInput, mStreetInput, mCityInput, mStateInput, mPinCodeInput, mPhoneNumberInput;
    private ArrayList<TextInputLayout> mInputs = new ArrayList<>();
    private int TAG_ERROR = R.id.tag_error;
    private int TAG_COLUMN = R.id.tag_collom;
    private boolean mInErrorUi;

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

        mNameInput.setTag(TAG_ERROR, "Required");
        mStreetInput.setTag(TAG_ERROR, "Required");
        mCityInput.setTag(TAG_ERROR, "Required");
        mStateInput.setTag(TAG_ERROR, "Required");
        mPinCodeInput.setTag(TAG_ERROR, "Required");
        mPhoneNumberInput.setTag(TAG_ERROR, "Required");

        mNameInput.setTag(TAG_COLUMN, Tables.Addresses.NAME);
        mStreetInput.setTag(TAG_COLUMN, Tables.Addresses.STREET);
        mCityInput.setTag(TAG_COLUMN, Tables.Addresses.CITY);
        mStateInput.setTag(TAG_COLUMN, Tables.Addresses.STATE);
        mPinCodeInput.setTag(TAG_COLUMN, Tables.Addresses.PINCODE);
        mPhoneNumberInput.setTag(TAG_COLUMN, Tables.Addresses.PHONENUMBER);


        mInputs.add(mNameInput);
        mInputs.add(mStreetInput);
        mInputs.add(mCityInput);
        mInputs.add(mStateInput);
        mInputs.add(mPinCodeInput);
        mInputs.add(mPhoneNumberInput);

        for (TextInputLayout t : mInputs) {
            t.setErrorEnabled(false);
            t.getEditText().addTextChangedListener(mTextWatcher);
        }

        mDoneButton.setOnClickListener(mClick);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (TextInputLayout t : mInputs) {
            t.getEditText().removeTextChangedListener(mTextWatcher);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mInErrorUi) {
                mInErrorUi = false;
                for (TextInputLayout t : mInputs) {
                    t.setErrorEnabled(false);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.Addresses._ID, System.currentTimeMillis());
            for (TextInputLayout t : mInputs) {
                String text = t.getEditText().getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    cv.put((String) t.getTag(TAG_COLUMN), text);
                } else {
                    mInErrorUi = true;
                    t.setErrorEnabled(true);
                    t.setError((CharSequence) t.getTag(TAG_ERROR));
                    return;
                }
            }
            getContext().getContentResolver().insert(Tables.Addresses.CONTENT_URI, cv);
            getActivity().onBackPressed();
        }
    };
}
