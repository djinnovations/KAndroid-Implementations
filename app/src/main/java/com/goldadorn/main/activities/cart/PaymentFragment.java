package com.goldadorn.main.activities.cart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.payu.india.Payu.PayuUtils;
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
    TextView mAddButton;
    PayuResponse mPayuResponse;
    PayuConfig mPayuConfig;
    PaymentParams mPaymentParams;
    PayuHashes mPayUHashes;
    ProgressBar mProgressBar;
    LinearLayout mContainerCard;
    PayUHelper mPayUHelper;
    private Bundle mPayuBundle;
    PayUStoredCardsAdapter mStoredCardsAdapter;
    ICartData mCartData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCartData = (ICartData) getActivity();
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStoredCardsAdapter = new PayUStoredCardsAdapter(view.getContext(), mPaymentModes);
        mAddButton = (TextView) view.findViewById(R.id.action_add);
        mContainerCard = (LinearLayout) view.findViewById(R.id.recyclerView);
//        mContainerCard.setAdapter(mStoredCardsAdapter);
        mAddButton.setText("Add new payment method");
        mAddButton.setOnClickListener(mAddClick);
        ((TextView) view.findViewById(R.id.cart_desc)).setText("Pay with");
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mPayUHelper = new PayUHelper(mCartData.getBillableAmount(), new IResultListener<Bundle>() {
            @Override
            public void onResult(Bundle bundle) {
                mPayuBundle = bundle;
                Parcelable p = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
                mPayuConfig = p != null && p instanceof PayuConfig ? (PayuConfig) p : new PayuConfig();
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
                        mPayuConfig.setData(postData.getResult());

                        // lets set the visibility of progress bar
                        mProgressBar.setVisibility(View.VISIBLE);
                        GetPaymentRelatedDetailsTask paymentRelatedDetailsForMobileSdkTask = new GetPaymentRelatedDetailsTask(PaymentFragment.this);
                        paymentRelatedDetailsForMobileSdkTask.execute(mPayuConfig);
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
        if (mPaymentModes.size() == 0) {
            StoredCard card = new StoredCard();
            card.setCardName("Master");
            card.setCardBin("visa");
            card.setMaskedCardNumber("783");
            mPaymentModes.add(card);
            card = new StoredCard();
            card.setCardName("Kiran");
            card.setCardBin("visa");
            card.setMaskedCardNumber("783");
            mPaymentModes.add(card);
            new StoredCard();
            card.setCardName("vijith");
            card.setMaskedCardNumber("783");
            card.setCardBin("visa");
            mPaymentModes.add(card);
        }
        if (mPaymentModes.size() > 0) {
            mContainerCard.setVisibility(View.VISIBLE);
        } else {
            mContainerCard.setVisibility(View.GONE);
        }
        mStoredCardsAdapter.changeData(mPaymentModes);
    }

    private View.OnClickListener mAddClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), PayUCreditDebitCardActivity.class);
            intent.putParcelableArrayListExtra(PayuConstants.CREDITCARD, mPayuResponse.getCreditCard());
            intent.putParcelableArrayListExtra(PayuConstants.DEBITCARD, mPayuResponse.getDebitCard());
            launchActivity(intent);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setMessage(data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();
            } else {
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

                mPayuConfig.setData(postData.getResult());
                mPayuConfig.setEnvironment(mPayuConfig.getEnvironment());

                GetStoredCardTask getStoredCardTask = new GetStoredCardTask(this);
                getStoredCardTask.execute(mPayuConfig);
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
        mPayuConfig.setData(null);
        intent.putExtra(PayuConstants.PAYU_CONFIG, mPayuConfig);
        // salt
        if (mPayuBundle.getString(PayuConstants.SALT) != null)
            intent.putExtra(PayuConstants.SALT, mPayuBundle.getString(PayuConstants.SALT));

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
            mPayuConfig.setData(postData.getResult());
            mPayuConfig.setEnvironment(mPayuConfig.getEnvironment());

            DeleteCardTask deleteCardTask = new DeleteCardTask(this);
            deleteCardTask.execute(mPayuConfig);
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
            mPayuConfig.setData(postData.getResult());
            Intent intent = new Intent(getContext(), PaymentsActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, mPayuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), postData.getResult(), Toast.LENGTH_SHORT).show();
        }

    }

    public class PayUStoredCardsAdapter extends BaseAdapter { // todo rename to storedcardAdapter

        private ArrayList<StoredCard> storedCards;
        private Context context;

        private PayuUtils payuUtils;

        public PayUStoredCardsAdapter(Context context, ArrayList<StoredCard> StoredCards) {
            this.context = context;
            storedCards = StoredCards;
            payuUtils = new PayuUtils();
        }

        private void bindUI(ViewHolder holder, int position) {
//            holder.setPosition(position);
            holder.cvvPayNowLinearLayout.setVisibility(View.GONE);
            String issuer = payuUtils.getIssuer(storedCards.get(position).getCardBin());
            switch (issuer) {
                case PayuConstants.VISA:
                    holder.cardIconImageView.setImageResource(R.mipmap.visa);
                    break;
                case PayuConstants.LASER:
                    holder.cardIconImageView.setImageResource(R.mipmap.laser);
                    break;
                case PayuConstants.DISCOVER:
                    holder.cardIconImageView.setImageResource(R.mipmap.discover);
                    break;
                case PayuConstants.MAES:
                    holder.cardIconImageView.setImageResource(R.mipmap.maestro);
                    break;
                case PayuConstants.MAST:
                    holder.cardIconImageView.setImageResource(R.mipmap.master);
                    break;
                case PayuConstants.AMEX:
                    holder.cardIconImageView.setImageResource(R.mipmap.amex);
                    break;
                case PayuConstants.DINR:
                    holder.cardIconImageView.setImageResource(R.mipmap.diner);
                    break;
                case PayuConstants.JCB:
                    holder.cardIconImageView.setImageResource(R.mipmap.jcb);
                    break;
                case PayuConstants.SMAE:
                    holder.cardIconImageView.setImageResource(R.mipmap.maestro);
                    break;
                default:
                    holder.cardIconImageView.setImageResource(R.mipmap.card);
                    break;

            }
            holder.cardNumberTextView.setText(storedCards.get(position).getMaskedCardNumber());
            holder.cardNameTextView.setText(storedCards.get(position).getCardName());
        }

        @Override
        public int getCount() {
            if (storedCards != null)
                return storedCards.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int index) {
            if (null != storedCards) return storedCards.get(index);
            else return 0;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.item_payment_mode, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.setPosition(position);
            bindUI(holder, position);

            return convertView;
        }

        public void changeData(ArrayList<StoredCard> mPaymentModes) {
            storedCards = mPaymentModes;
            for (int i = 0; i < storedCards.size(); i++) {
                View child = mContainerCard.getChildAt(i);
                View v = getView(i, child, mContainerCard);
                v.setVisibility(View.VISIBLE);
                if (child == null) {
                    mContainerCard.addView(v);
                }
            }
            if (mContainerCard.getChildCount() > storedCards.size()) {
                for (int i = storedCards.size(); i < mContainerCard.getChildCount(); i++) {
                    mContainerCard.getChildAt(i).setVisibility(View.GONE);
                }
            }
//            notifyDataSetChanged();
        }


        class ViewHolder implements View.OnClickListener {

            int position; //for index

            ImageView cardIconImageView;
            ImageView cardTrashImageView;
            TextView cardNumberTextView;
            TextView cardNameTextView;
            LinearLayout cvvPayNowLinearLayout;
            LinearLayout rowLinearLayout;
            Button paynNowButton;
            EditText cvvEditText;

            public void setPosition(int position) {
                this.position = position;
            }

            public ViewHolder(View itemView) {

                cardIconImageView = (ImageView) itemView.findViewById(R.id.image_view_card_icon);
                cardNumberTextView = (TextView) itemView.findViewById(R.id.text_view_card_number);
                cardTrashImageView = (ImageView) itemView.findViewById(R.id.image_view_card_trash);
                cardNameTextView = (TextView) itemView.findViewById(R.id.text_view_card_name);
                rowLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_row);
                cvvPayNowLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_cvv_paynow);
                paynNowButton = (Button) itemView.findViewById(R.id.button_pay_now);
                cvvEditText = (EditText) itemView.findViewById(R.id.edit_text_cvv);

                // lets restrict the user not from typing alpha characters.

                cardTrashImageView.setOnClickListener(this);
                cvvPayNowLinearLayout.setOnClickListener(this);
                rowLinearLayout.setOnClickListener(this);
                paynNowButton.setOnClickListener(this);

                // we need to set the length of cvv field according to the card number

                cvvEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        /// lets enable or disable the pay now button according to the cvv and card number
                        cvvEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(payuUtils.getIssuer(storedCards.get(position).getCardBin()).contentEquals(PayuConstants.AMEX) ? 4 : 3)});
                        if (payuUtils.validateCvv(storedCards.get(position).getCardBin(), s.toString())) {
                            paynNowButton.setEnabled(true);
                        } else {
                            paynNowButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            @Override
            public void onClick(View view) {
                for (int i = 0; i < mContainerCard.getChildCount(); i++) {
                    View cvvContainer = mContainerCard.getChildAt(i).findViewById(cvvPayNowLinearLayout.getId());
                    if (cvvContainer.equals(cvvPayNowLinearLayout)) {
                        if (cvvPayNowLinearLayout.getVisibility() == View.VISIBLE) {
                            cvvPayNowLinearLayout.setVisibility(View.GONE);
                        } else {
                            cvvPayNowLinearLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        cvvContainer.setVisibility(View.GONE);
                    }
                }

                if (view.getId() == R.id.image_view_card_trash) {
                    deleteCard(storedCards.get(position));
                } else if (view.getId() == R.id.button_pay_now) {
                    makePayment(storedCards.get(position), cvvEditText.getText().toString());
                }
            }
        }
    }

}
