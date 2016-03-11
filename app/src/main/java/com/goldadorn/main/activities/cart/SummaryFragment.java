package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.model.Address;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class SummaryFragment extends Fragment {

    ArrayList<Address> mAddresses = new ArrayList<>(5);
    AddressesViewHolder mAddressesHolder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addresses, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddressesHolder = new AddressesViewHolder((LinearLayout) view.findViewById(R.id.container_addresses_payment), mAddressSelectedListener);
        view.findViewById(R.id.action_add).setOnClickListener(mClick);

        Address product = new Address(123123);
        product.name = "Home";
        product.detailed = "Jabong flipped on\n amazon";
        mAddresses.add(product);
        product = new Address(123123);
        product.name = "Office";
        product.detailed = "Snapdeal flipped on\n amazon";
        mAddresses.add(product);
        onAddressesChanged();
    }

    private void onAddressesChanged() {
        if (mAddresses.size() > 0) {
            mAddressesHolder.setVisibility(View.VISIBLE);
            mAddressesHolder.bindUI(mAddresses);
        } else {
            mAddressesHolder.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    IResultListener<Integer> mAddressSelectedListener = new IResultListener<Integer>() {
        @Override
        public void onResult(Integer addressId) {

        }
    };
}
