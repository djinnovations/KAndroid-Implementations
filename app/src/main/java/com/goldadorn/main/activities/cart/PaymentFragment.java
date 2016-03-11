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
import com.goldadorn.main.model.PaymentMode;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class PaymentFragment extends Fragment {

    ArrayList<PaymentMode> mPayments = new ArrayList<>(5);
    PaymentModesViewHolder mPaymentsHolder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addresses, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPaymentsHolder = new PaymentModesViewHolder((LinearLayout) view.findViewById(R.id.container_addresses_payment), mPaymentSelectedListener);
        view.findViewById(R.id.action_add).setOnClickListener(mClick);

        PaymentMode product = new PaymentMode(123123,2);
        product.name = "Mobikwik";
        product.details = "Jabong flipped on\n amazon";
        mPayments.add(product);
        product = new PaymentMode(123123,3);
        product.name = "Office";
        product.details = "Snapdeal flipped on\n amazon";
        mPayments.add(product);
        onPaymentOptionsChanged();
    }

    private void onPaymentOptionsChanged() {
        if (mPayments.size() > 0) {
            mPaymentsHolder.setVisibility(View.VISIBLE);
            mPaymentsHolder.bindUI(mPayments);
        } else {
            mPaymentsHolder.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    IResultListener<Integer> mPaymentSelectedListener = new IResultListener<Integer>() {
        @Override
        public void onResult(Integer addressId) {

        }
    };
}
