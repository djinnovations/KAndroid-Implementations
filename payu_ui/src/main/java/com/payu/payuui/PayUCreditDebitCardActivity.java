package com.payu.payuui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.Payu.PayuUtils;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.payuui.customutil.GAUtil;

import java.text.DateFormatSymbols;
import java.util.Calendar;


public class PayUCreditDebitCardActivity extends AppCompatActivity implements View.OnClickListener {

    private Button payNowButton;
    private EditText cardNameEditText;
    private EditText cardNumberEditText;
    private EditText cardCvvEditText;
    private EditText cardExpiryMonthEditText;
    private EditText cardExpiryYearEditText;
    private Bundle bundle;
    private CheckBox saveCardCheckBox;

    private String cardName;
    private String cardNumber;
    private String cvv;
    private String expiryMonth;
    private String expiryYear;

    private PayuHashes mPayuHashes;
    private PaymentParams mPaymentParams;
    private PostData postData;
    private Toolbar toolbar;

    private TextView amountTextView;
    private TextView transactionIdTextView;
    private PayuConfig payuConfig;

    private PayuUtils payuUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // todo lets set the toolbar
        /*toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter Card Details");

        (payNowButton = (Button) findViewById(R.id.button_card_make_payment)).setOnClickListener(this);

        cardNameEditText = (EditText) findViewById(R.id.edit_text_name_on_card);
        cardNumberEditText = (EditText) findViewById(R.id.edit_text_card_number);
        cardCvvEditText = (EditText) findViewById(R.id.edit_text_card_cvv);
        cardExpiryMonthEditText = (EditText) findViewById(R.id.edit_text_expiry_month);
        cardExpiryMonthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMMYYYYOption(true);
            }
        });
        cardExpiryYearEditText = (EditText) findViewById(R.id.edit_text_expiry_year);
        cardExpiryYearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMMYYYYOption(false);
            }
        });
        saveCardCheckBox = (CheckBox) findViewById(R.id.check_box_save_card);

        bundle = getIntent().getExtras();


        // lets get payment default params and hashes
        mPayuHashes = bundle.getParcelable(PayuConstants.PAYU_HASHES);
        mPaymentParams = bundle.getParcelable(PayuConstants.PAYMENT_PARAMS);
        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        payuConfig = null != payuConfig ? payuConfig : new PayuConfig();

        (amountTextView = (TextView) findViewById(R.id.text_view_amount))
                .setText(/*PayuConstants.AMOUNT*/"Amount" + ": " + GAUtil.getIndianCurrencyFormat(mPaymentParams.getAmount(), true) + "/-");
        (transactionIdTextView = (TextView) findViewById(R.id.text_view_transaction_id)).setText(PayuConstants.TXNID + ": " + mPaymentParams.getTxnId());
        transactionIdTextView.setVisibility(View.GONE);
        payNowButton.setText("Pay "+ GAUtil.getIndianCurrencyFormat(mPaymentParams.getAmount(), true) + "/-");
        GAUtil.setRelativeFontSize(payNowButton, 4, payNowButton.getText().toString().trim().length(), 1.3f);

        // lets not show the save card check box if user credentials is not found!
        if (null == mPaymentParams.getUserCredentials())
            saveCardCheckBox.setVisibility(View.GONE);
        else
            saveCardCheckBox.setVisibility(View.VISIBLE);

        saveCardCheckBox.setVisibility(View.GONE);
        payuUtils = new PayuUtils();


        cardNumberEditText.addTextChangedListener(new TextWatcher() {
            String issuer;
            Drawable issuerDrawable;

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = ' ';

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String toCheck = charSequence.toString().trim().replaceAll("\\s","");
                if (toCheck.length() > 5) { // to confirm rupay card we need min 6 digit.
                    if (null == issuer) issuer = payuUtils.getIssuer(toCheck);
                    if (issuer != null && issuer.length() > 1 && issuerDrawable == null) {
                        issuerDrawable = getIssuerDrawable(issuer);
                        if (issuer.contentEquals(PayuConstants.SMAE)) { // hide cvv and expiry
                            cardExpiryMonthEditText.setVisibility(View.GONE);
                            cardExpiryYearEditText.setVisibility(View.GONE);
                            cardCvvEditText.setVisibility(View.GONE);
                        } else { //show cvv and expiry
                            cardExpiryMonthEditText.setVisibility(View.VISIBLE);
                            cardExpiryYearEditText.setVisibility(View.VISIBLE);
                            cardCvvEditText.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    issuer = null;
                    issuerDrawable = null;
                }
                cardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, issuerDrawable, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isInputCorrect(editable, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    editable.replace(0, editable.length(), buildCorrecntString(getDigitArray(editable, TOTAL_DIGITS)
                            , DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // chech that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }

        });

    }

    private int selectedMM = 0;
    private int selectedYY = 0;
    private String selectedMMMain = "01";

    private void displayMMYYYYOption(boolean isMM) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        //alt_bld.setIcon(R.drawable.icon);
        if (isMM) {
            alt_bld.setTitle("Select Expiry Month");
            final String[] arr = months;
            alt_bld.setSingleChoiceItems(arr, selectedMM, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    selectedMM = item;
                    Log.d("dj", "Month sel = " + arr[item]);
                    String num = (item+1) < 10 ? "0"+String.valueOf(item+1) : String.valueOf(item+1);
                    selectedMMMain = num;
                    cardExpiryMonthEditText.setText(/*String.valueOf(item + 1)*/num);
                    /*Toast.makeText(getApplicationContext(),
                            "Month = " + arr[item], Toast.LENGTH_SHORT).show();*/
                    dialog.dismiss();// dismiss the alertbox after chose option
                }
            });
        } else {
            alt_bld.setTitle("Select Expiry Year");
            final String[] arr = YYYY;
            alt_bld.setSingleChoiceItems(arr, selectedYY, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    selectedYY = item;
                    Log.d("dj", "YYYY selected: " + arr[item]);
                    cardExpiryYearEditText.setText(arr[item]);
                    /*Toast.makeText(getApplicationContext(),
                            "Year = " + arr[item], Toast.LENGTH_SHORT).show();*/
                    dialog.dismiss();// dismiss the alertbox after chose option

                }
            });
        }
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    String[] months = getMonths();
    String[] YYYY = getYYYYData();

    private String[] getYYYYData() {
        String[] arr = new String[10];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 10; i++) {
            arr[i] = String.valueOf(currentYear);
            currentYear++;
        }
        return arr;
    }

    private String[] getMonths() {
       /* String[] months = new DateFormatSymbols().getMonths();
        for (String month : months) {
            System.out.println("month = " + month);
        }*/

        String[] shortMonths = new DateFormatSymbols().getShortMonths();
        int i = 1;
        for (String shortMon : shortMonths) {
            System.out.println("shortMonth = " + shortMon);
            if (i <= 9)
                shortMonths[i-1] = shortMon + " (0" + String.valueOf(i) + ")";
            else
                shortMonths[i-1] = shortMon + " (" + String.valueOf(i) + ")";
            i++;
        }
        return shortMonths;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // Oh crap! Resource IDs cannot be used in a switch statement in Android library modules less... (Ctrl+F1)
        // Validates using resource IDs in a switch statement in Android library module
        // we cant not use switch and gotta use simple if else
        if (v.getId() == R.id.button_card_make_payment) {

            // do i have to store the card
            if (saveCardCheckBox.isChecked()) {
                mPaymentParams.setStoreCard(1);
            } else {
                mPaymentParams.setStoreCard(0);
            }
            // setup the hash
            mPaymentParams.setHash(mPayuHashes.getPaymentHash());

            // lets try to get the post params

            postData = null;
            // lets get the current card number;
            cardNumber = String.valueOf(cardNumberEditText.getText().toString().trim()).replaceAll("\\s","");
            cardName = cardNameEditText.getText().toString().trim();
            expiryMonth = /*cardExpiryMonthEditText.getText().toString();*/ selectedMMMain;
            expiryYear = cardExpiryYearEditText.getText().toString().trim();
            cvv = cardCvvEditText.getText().toString().trim();

            // lets not worry about ui validations.
            mPaymentParams.setCardNumber(cardNumber);
            mPaymentParams.setCardName(cardName);
            mPaymentParams.setNameOnCard(cardName);
            mPaymentParams.setExpiryMonth(expiryMonth);
            mPaymentParams.setExpiryYear(expiryYear);
            mPaymentParams.setCvv(cvv);
            postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                // okay good to go.. lets make a transaction
                // launch webview
                payuConfig.setData(postData.getResult());
                Intent intent = new Intent(this, PaymentsActivity.class);
                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
            } else {
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            setResult(resultCode, data);
            finish();
        }
    }

    private Drawable getIssuerDrawable(String issuer) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            switch (issuer) {
                case PayuConstants.VISA:
                    return getResources().getDrawable(R.drawable.visa);
                case PayuConstants.LASER:
                    return getResources().getDrawable(R.drawable.laser);
                case PayuConstants.DISCOVER:
                    return getResources().getDrawable(R.drawable.discover);
                case PayuConstants.MAES:
                    return getResources().getDrawable(R.drawable.maestro);
                case PayuConstants.MAST:
                    return getResources().getDrawable(R.drawable.master);
                case PayuConstants.AMEX:
                    return getResources().getDrawable(R.drawable.amex);
                case PayuConstants.DINR:
                    return getResources().getDrawable(R.drawable.diner);
                case PayuConstants.JCB:
                    return getResources().getDrawable(R.drawable.jcb);
                case PayuConstants.SMAE:
                    return getResources().getDrawable(R.drawable.maestro);
                case PayuConstants.RUPAY:
                    return getResources().getDrawable(R.drawable.rupay);
            }
            return null;
        } else {

            switch (issuer) {
                case PayuConstants.VISA:
                    return getResources().getDrawable(R.drawable.visa, null);
                case PayuConstants.LASER:
                    return getResources().getDrawable(R.drawable.laser, null);
                case PayuConstants.DISCOVER:
                    return getResources().getDrawable(R.drawable.discover, null);
                case PayuConstants.MAES:
                    return getResources().getDrawable(R.drawable.maestro, null);
                case PayuConstants.MAST:
                    return getResources().getDrawable(R.drawable.master, null);
                case PayuConstants.AMEX:
                    return getResources().getDrawable(R.drawable.amex, null);
                case PayuConstants.DINR:
                    return getResources().getDrawable(R.drawable.diner, null);
                case PayuConstants.JCB:
                    return getResources().getDrawable(R.drawable.jcb, null);
                case PayuConstants.SMAE:
                    return getResources().getDrawable(R.drawable.maestro, null);
                case PayuConstants.RUPAY:
                    return getResources().getDrawable(R.drawable.rupay, null);
            }
            return null;
        }
    }
}
