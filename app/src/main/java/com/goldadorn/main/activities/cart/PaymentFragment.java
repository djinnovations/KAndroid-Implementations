package com.goldadorn.main.activities.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.payu.india.Interfaces.DeleteCardApiListener;
import com.payu.india.Interfaces.GetStoredCardApiListener;
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
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.Tasks.DeleteCardTask;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;
import com.payu.india.Tasks.GetStoredCardTask;
import com.payu.payuui.PayUCreditDebitCardActivity;
import com.payu.payuui.PaymentsActivity;

import java.util.ArrayList;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class PaymentFragment extends Fragment implements PaymentRelatedDetailsListener, DeleteCardApiListener, GetStoredCardApiListener {

    ArrayList<StoredCard> mPaymentModes = new ArrayList<>(5);
    PaymentModesViewHolder mPaymentsHolder;
    TextView mAddButton;
    PayuResponse mPayuResponse;
    PayuConfig payuConfig;
    PaymentParams mPaymentParams;
    PayuHashes mPayUHashes;
    ProgressBar mProgressBar;
    PayUHelper mPayUHelper;
    private Bundle mBundle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPaymentsHolder = new PaymentModesViewHolder((LinearLayout) view.findViewById(R.id.container_addresses_payment), mPaymentSelectedListener);
        mAddButton = (TextView) view.findViewById(R.id.action_add);
        mAddButton.setText("Add new payment method");
        mAddButton.setOnClickListener(mAddClick);
        ((TextView) view.findViewById(R.id.cart_desc)).setText("Pay with");
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mPayUHelper = new PayUHelper(new IResultListener<Bundle>() {
            @Override
            public void onResult(Bundle bundle) {
                mBundle=bundle;
                Parcelable p = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
                payuConfig = p != null && p instanceof PayuConfig ? (PayuConfig) p : new PayuConfig();
                mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS); // Todo change the name to PAYMENT_PARAMS
                mPayUHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
                if (savedInstanceState == null) { // dont fetch the data if its been called from payment activity.
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
                        GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(PaymentFragment.this);
                        paymentRelatedDetailsForMobileSdkTask.execute(payuConfig);
                    } else {
                        Toast.makeText(getContext(), postData.getResult(), Toast.LENGTH_LONG).show();
                        // close the progress bar
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });


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

    private View.OnClickListener mAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent  intent= new Intent(getContext(), PayUCreditDebitCardActivity.class);
            intent.putParcelableArrayListExtra(PayuConstants.CREDITCARD, mPayuResponse.getCreditCard());
            intent.putParcelableArrayListExtra(PayuConstants.DEBITCARD, mPayuResponse.getDebitCard());
            launchActivity(intent);
        }
    };

    IResultListener<StoredCard> mPaymentSelectedListener = new IResultListener<StoredCard>() {
        @Override
        public void onResult(StoredCard selectedCard) {

        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if(data != null ) {
                new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setMessage(data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();
            }else{
                Toast.makeText(getContext(), "Could not receive data", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse payuResponse) {
        mPayuResponse = payuResponse;
        mProgressBar.setVisibility(View.GONE);
        onGetStoredCardApiResponse(payuResponse);
    }

    @Override
    public void onDeleteCardApiResponse(PayuResponse payuResponse) {
        if (payuResponse.isResponseAvailable()) {
            Toast.makeText(getContext(), payuResponse.getResponseStatus().getResult(), Toast.LENGTH_LONG).show();
        }
        if (payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) {
            // there is no error, lets fetch te cards list.

            MerchantWebService merchantWebService = new MerchantWebService();
            merchantWebService.setKey(mPaymentParams.getKey());
            merchantWebService.setCommand(PayuConstants.GET_USER_CARDS);
            merchantWebService.setVar1(mPaymentParams.getUserCredentials());
            merchantWebService.setHash(mPayUHashes.getStoredCardsHash());

            PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

            if (postData.getCode() == PayuErrors.NO_ERROR) {
                // ok we got the post params, let make an api call to payu to fetch the payment related details

                payuConfig.setData(postData.getResult());
                payuConfig.setEnvironment(payuConfig.getEnvironment());

                GetStoredCardTask getStoredCardTask = new GetStoredCardTask(this);
                getStoredCardTask.execute(payuConfig);
            } else {
                Toast.makeText(getContext(), postData.getResult(), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onGetStoredCardApiResponse(PayuResponse payuResponse) {
        mPaymentModes.clear();
        if (payuResponse.getStoredCards() != null)
            mPaymentModes.addAll(payuResponse.getStoredCards());
        onPaymentOptionsChanged();
    }
    private void launchActivity(Intent intent) {
        intent.putExtra(PayuConstants.PAYU_HASHES, mPayUHashes);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        payuConfig.setData(null);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        // salt
        if(mBundle.getString(PayuConstants.SALT) != null)
            intent.putExtra(PayuConstants.SALT, mBundle.getString(PayuConstants.SALT));

        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }
    private void deleteCard(StoredCard storedCard) {
        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.DELETE_USER_CARD);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials());
        merchantWebService.setVar2(storedCard.getCardToken());
        merchantWebService.setHash(mPayUHashes.getDeleteCardHash());

        PostData postData = null;
        postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            // ok we got the post params, let make an api call to payu to fetch
            // the payment related details
            payuConfig.setData(postData.getResult());
            payuConfig.setEnvironment(payuConfig.getEnvironment());

            DeleteCardTask deleteCardTask = new DeleteCardTask(this);
            deleteCardTask.execute(payuConfig);
        } else {
            Toast.makeText(getContext(), postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }


    private void makePayment(StoredCard storedCard, String cvv) {
        PostData postData = new PostData();
        // lets try to get the post params
        postData = null;
        storedCard.setCvv(cvv); // make sure that you set the cvv also
        mPaymentParams.setHash(mPayUHashes.getPaymentHash()); // make sure that you set payment hash
        mPaymentParams.setCardToken(storedCard.getCardToken());
        mPaymentParams.setCvv(cvv);
        mPaymentParams.setNameOnCard(storedCard.getNameOnCard());
        mPaymentParams.setCardName(storedCard.getCardName());
        mPaymentParams.setExpiryMonth(storedCard.getExpiryMonth());
        mPaymentParams.setExpiryYear(storedCard.getExpiryYear());

        postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();

        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(getContext(), PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), postData.getResult(), Toast.LENGTH_SHORT).show();
        }

    }
}
