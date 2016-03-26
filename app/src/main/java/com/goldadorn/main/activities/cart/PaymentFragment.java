package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class PaymentFragment extends Fragment implements PaymentRelatedDetailsListener {

    ArrayList<StoredCard> mPaymentModes = new ArrayList<>(5);
    PaymentModesViewHolder mPaymentsHolder;
    TextView mAddButton;
    PayuResponse mPayuResponse;
    PayuConfig payuConfig;
    PaymentParams mPaymentParams;
    PayuHashes mPayUHashes;
    ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Parcelable p = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = p != null && p instanceof PayuConfig ? (PayuConfig) p : new PayuConfig();
        mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS); // Todo change the name to PAYMENT_PARAMS
        mPayUHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
        return inflater.inflate(R.layout.fragment_addresses, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPaymentsHolder = new PaymentModesViewHolder((LinearLayout) view.findViewById(R.id.container_addresses_payment), mPaymentSelectedListener);
        mAddButton = (TextView) view.findViewById(R.id.action_add);
        mAddButton.setText("Add new payment method");
        mAddButton.setOnClickListener(mClick);

        ((TextView) view.findViewById(R.id.cart_desc)).setText("Pay with");

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        if (null == savedInstanceState) { // dont fetch the data if its been called from payment activity.
            MerchantWebService merchantWebService = new MerchantWebService();
            merchantWebService.setKey(mPaymentParams.getKey());
            merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
            merchantWebService.setVar1(mPaymentParams.getUserCredentials() == null ? "default" : mPaymentParams.getUserCredentials());
            // hash we have to generate
            merchantWebService.setHash(mPayUHashes.getPaymentRelatedDetailsForMobileSdkHash());
            PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                // ok we got the post params, let make an api call to payu to fetch the payment related details
                payuConfig.setData(postData.getResult());

                // lets set the visibility of progress bar
                mProgressBar.setVisibility(View.VISIBLE);
                GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(this);
                paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
            } else {
                Toast.makeText(getContext(), postData.getResult(), Toast.LENGTH_LONG).show();
                // close the progress bar
                mProgressBar.setVisibility(View.GONE);
            }
        }


//        PaymentMode pm = new PaymentMode(123123, 2);
//        pm.name = "Mobikwik";
//        pm.details = "Jabong flipped on\n amazon";
//        mPaymentModes.add(pm);
//        pm = new PaymentMode(123123, 3);
//        pm.name = "Office";
//        pm.details = "Snapdeal flipped on\n amazon";
//        mPaymentModes.add(pm);
//        onPaymentOptionsChanged();
    }

    private void onPaymentOptionsChanged() {
        if (mPaymentModes.size() > 0) {
            mPaymentsHolder.setVisibility(View.VISIBLE);
            mPaymentsHolder.bindUI(mPaymentModes);
        } else {
            mPaymentsHolder.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((CartManagerActivity) getActivity()).configureUI(CartManagerActivity.UISTATE_OVERLAY_ADD_PAYEMNT);
        }
    };

    IResultListener<StoredCard> mPaymentSelectedListener = new IResultListener<StoredCard>() {
        @Override
        public void onResult(StoredCard selectedCard) {

        }
    };

    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse payuResponse) {
        mPayuResponse = payuResponse;
        mProgressBar.setVisibility(View.GONE);
        mPaymentModes = payuResponse.getStoredCards();
        onPaymentOptionsChanged();
    }
}
