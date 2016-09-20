package com.goldadorn.main.activities.cart;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.dj.model.ShipmentBillingAddress;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.model.Address;
import com.goldadorn.main.utils.TypefaceHelper;

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
    private boolean mIsEditMode;
    private Address mAddressToEdit;

    public static AddAddressFragment newInstance(Address address) {
        AddAddressFragment f = new AddAddressFragment();
        Bundle b = new Bundle();
        if (address != null)
            b.putSerializable("address", address);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle b = savedInstanceState == null ? getArguments() : savedInstanceState;
        mAddressToEdit = (Address) b.getSerializable("address");
        mIsEditMode = mAddressToEdit != null;
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

        if (mAddressToEdit != null) {
            mNameInput.getEditText().setText(mAddressToEdit.name);
            mStreetInput.getEditText().setText(mAddressToEdit.street);
            mCityInput.getEditText().setText(mAddressToEdit.city);
            mStateInput.getEditText().setText(mAddressToEdit.state);
            mPinCodeInput.getEditText().setText(mAddressToEdit.pincode + "");
            mPhoneNumberInput.getEditText().setText(mAddressToEdit.phoneNumber);
        }
        /*else {*/
            tryautofill();
        //}
        ((CartManagerActivity) getActivity()).removeStaticBottomBar();

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
        mPinCodeInput.getEditText().addTextChangedListener(mPinCodeWatcher);

        mDoneButton.setOnClickListener(mClick);
    }

    private void tryautofill() {
        displayDialog();
    }

    private Dialog pincodeDialog;
    View.OnClickListener positiveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pincode  = editText.getText().toString().trim();
            if (pincode.length() < 6){
                Toast.makeText( Application.getInstance(),"Invalid Area Pin Code", Toast.LENGTH_SHORT).show();
                return;
            }
            mPinCodeInput.getEditText().setText(pincode);
            ((CartManagerActivity) getActivity()).queryPincodeAddressData(pincode);
        }
    };


    public void updateFields(PayUHelper.PincodeFieldData data){
        pincodeDialog.dismiss();
        mCityInput.getEditText().setText(data.getCity());
        mStateInput.getEditText().setText(data.getState());
        mStreetInput.getEditText().setText(data.getStreet());
    }

    EditText editText;
    private void displayDialog(){
        if (pincodeDialog == null){
            View layout = LayoutInflater.from(Application.getInstance()).inflate(R.layout.dialog_pincode_input, null);
            pincodeDialog = WindowUtils.getInstance(Application.getInstance())
                    .displayViewDialog(getActivity(), layout);
            TextView title = (TextView) layout.findViewById(R.id.tvTitle);
            title.setText("Pincode");
            editText = (EditText) layout.findViewById(R.id.etPinCode);
            TextView positive = (TextView) layout.findViewById(R.id.tvPositive);
            TextView negative = (TextView) layout.findViewById(R.id.tvNegative);
            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pincodeDialog.dismiss();
                }
            });
            positive.setOnClickListener(positiveClick);
            positive.setText("Done");
            negative.setText("I don't know");
            TypefaceHelper.setFont(editText, positive, negative);
            UiRandomUtils.setTypefaceBold(title);
        }
        pincodeDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (TextInputLayout t : mInputs) {
            t.getEditText().removeTextChangedListener(mTextWatcher);
        }
        mPinCodeInput.getEditText().removeTextChangedListener(mPinCodeWatcher);
    }

    private TextWatcher mPinCodeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPinCodeInput.setErrorEnabled(false);
            if (s.length() > 0) {
                try {
                    double d = Double.parseDouble(s.toString());
                } catch (NumberFormatException nfe) {
                    mPinCodeInput.setError("Not a valid pincode");
                    mPinCodeInput.setErrorEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
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
           /* ContentValues cv = new ContentValues();
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
            int id = -1;
            if (mAddressToEdit != null)
                id = mAddressToEdit.id;
            int cnt = 0;
            if (id != -1)
                cnt = getContext().getContentResolver().update(Tables.Addresses.CONTENT_URI, cv, Tables.Addresses._ID + " = ?", new String[]{String.valueOf(id)});
            if (cnt == 0) {
                int newId = 1;
                Cursor c = getContext().getContentResolver().query(Tables.Addresses.CONTENT_URI, new String[]{Tables.Addresses._ID}, null, null, Tables.Addresses._ID + " desc");
                if (c != null) {
                    if (c.moveToFirst())
                        id = c.getInt(0) + 1;
                    c.close();
                }
                cv.put(Tables.Addresses._ID, newId);
                getContext().getContentResolver().insert(Tables.Addresses.CONTENT_URI, cv);
            }
            getActivity().onBackPressed();*/
            setAddress();
        }
    };

    private void setAddress(){
        ShipmentBillingAddress address ;
        String fname = "", lname = "", ph = "", addr1 = "", addr2 = "", city = "", pincode = "", state = "", country = "";

        for (TextInputLayout t : mInputs) {
            String text = t.getEditText().getText().toString();
            if (!TextUtils.isEmpty(text)) {
                switch (t.getId()){
                    case R.id.input_name: fname = text;
                        break;
                    case R.id.input_address_street: addr1 = text;
                        break;
                    case R.id.input_city: city = text;
                        break;
                    case R.id.input_state: state = text;
                        break;
                    case R.id.input_pincode: pincode = text;
                        break;
                    case R.id.input_phone_number: ph = text;
                    default: break;
                }
            } else {
                mInErrorUi = true;
                t.setErrorEnabled(true);
                t.setError((CharSequence) t.getTag(TAG_ERROR));
                return;
            }
        }

        address = new ShipmentBillingAddress(fname, lname, ph, addr1, addr2, country,
                state, city, pincode, CartManagerActivity.TYPE_ADDRESS_SHIPPING);
        ((CartManagerActivity) getActivity()).setAddressResult(address);
        getActivity().onBackPressed();
    }
}
