package com.goldadorn.main.activities.showcase;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.dj.model.BookAppointmentDataObj;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.views.ColoredSnackbar;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookAppointment extends BaseActivity {

    @Bind(R.id.llScreen1)
    View llScreen1;
    @Bind(R.id.etPhNum)
    EditText etPhNum;
    @Bind(R.id.etMsgAppoint)
    EditText etMsgAppoint;
    @Bind(R.id.etName)
    EditText etName;
    /*@Bind(R.id.tvNegative)
    TextView tvNegative;*/
    @Bind(R.id.tvPositive)
    TextView btnTvBookNow;
    @Bind(R.id.llScreen2)
    View llScreen2;
    @Bind(R.id.tvOkay)
    TextView tvOkay;
    @Bind(R.id.header)
    ImageView ivHeader;
    @Bind(R.id.tvBookDate)
    TextView tvBookDate;

    int currentMode;
    BookAppointmentDataObj baaDataObj;
    /*private String designerName;
    private String id;
    private String url;
    private String collectionId;
    private String productId;
    private String collectionName;
    private String productName;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        ButterKnife.bind(this);
        setUpCalendar();
        baaDataObj = getIntent().getParcelableExtra(IntentKeys.BOOK_APPOINT_DATA);
        try {
            if (baaDataObj != null) {
                currentMode = baaDataObj.getModeOfAppt();
                /*designerName = args.getString(IntentKeys.BOOK_APPOINT_DETAILS_NAME);
                id = args.getString(IntentKeys.BOOK_APPOINT_DETAILS_ID);
                url = args.getString(IntentKeys.BOOK_APPOINT_DETAILS_URL);
                *//*collectionId = args.getString(IntentKeys.COLLECTION_DETAILS_ID);
                collectionName = args.getString(IntentKeys.COLLECTION_DETAILS_NAME);
                productId = args.getString(IntentKeys.PRODUCT_DETAILS_ID);
                productName = args.getString(IntentKeys.PRODUCT_DETAILS_NAME);*//*
                Log.d("djappoint", "designerName: " + designerName);
                Log.d("djappoint", "id: " + id);*/
                Log.d("djappoint", "BAA data: " + baaDataObj.toString());
               /* Log.d("djappoint", "collectionId: " + collectionId);
                Log.d("djappoint", "collectionName: " + collectionName);
                Log.d("djappoint", "productId: " + productId);
                Log.d("djappoint", "productName: " + productName);*/
            }
            //dismissOverLay();
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Book Appoin");*/
            setCustomTitle();
            if (!TextUtils.isEmpty(baaDataObj.getItemImageUrl()))
                Picasso.with(getApplicationContext())
                        .load(baaDataObj.getItemImageUrl())
                        //.placeholder(R.drawable.img_timeline)
                        .into(ivHeader);
            //tvNegative.setOnClickListener(finishClickListener);
            tvOkay.setOnClickListener(finishClickListener);
            btnTvBookNow.setOnClickListener(positiveClick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mCalendar.set(year, monthOfYear, dayOfMonth);
                    tvBookDate.setText(android.text.format.DateFormat.format("MM/dd/yyyy", mCalendar));
                }
            };
    private DatePickerDialog mDialog;
    private Calendar mCalendar;

    private void setUpCalendar() {
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getDefault());
        //mCalendar.setTimeInMillis(System.currentTimeMillis());
        tvBookDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog == null)
                    mDialog = new DatePickerDialog(BookAppointment.this,
                            mDateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH));
                else
                    mDialog.updateDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH));
                mDialog.show();
            }
        });
    }

    private void setCustomTitle() {
        try {
            Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(tb);
            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedText);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedText);

            collapsingToolbar.setTitle(baaDataObj.getItemName() == null ? "Name" : baaDataObj.getItemName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    View.OnClickListener positiveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Please provide your designerName", Toast.LENGTH_SHORT).show();
                    return;
                } else if (etPhNum.getText().toString().trim().length() < 10) {
                    Toast.makeText(getApplicationContext(), "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (tvBookDate.getText().toString().trim().equalsIgnoreCase("appointment date")) {
                    Toast.makeText(getApplicationContext(), "Please provide Appointment date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(etMsgAppoint.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Please leave a message", Toast.LENGTH_SHORT).show();
                    return;
                }
                doTransition();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    interface ServerResponseListener {
        void onPositiveResponse();

        void onNegativeResponse(String errMsg);
    }

    private ServerResponseListener srspListener = new ServerResponseListener() {
        @Override
        public void onPositiveResponse() {
            bringUpSuccessBookingScreen();
        }

        @Override
        public void onNegativeResponse(String errMsg) {
            failedInfo(errMsg);
            //Toast.makeText(getApplicationContext(), "Unable to make an Appointment, Please try again ", Toast.LENGTH_LONG).show();
        }
    };

    private final int BOOK_APPOINTMENT_CALL = IDUtils.generateViewId();

    private void doTransition() throws Exception {
        /*Map<String, String> params = new HashMap<>();
        params.put("phone", etPhNum.getText().toString().trim());

        if (!TextUtils.isEmpty(id))
            params.put("designer", String.valueOf(id));
        *//*if (!TextUtils.isEmpty(collectionId))
            params.put("collection", collectionId);
        if (!TextUtils.isEmpty(productId))
            params.put("product", productId);
        if (!TextUtils.isEmpty(collectionName))
            params.put("collName", collectionName);
        if (!TextUtils.isEmpty(productName))
            params.put("prodName", productName);*//*

        params.put("message", etMsgAppoint.getText().toString().trim());*/
        Map<String, String> params = getParamMap();
        Log.d("djappoint", "req params- book appointment: " + params);
        sendAppointmentRequest(params);
    }


    private Map<String, String> getParamMap() {
        Map<String, String> params = new HashMap<>();
        if (currentMode == COLLECTION) {
            params.put("collection", baaDataObj.getCollectionId());
            //params.put("collection", baaDataObj.getCollectionId());
        } else if (currentMode == PRODUCTS) {
            params.put("collection", baaDataObj.getCollectionId());
            params.put("product", baaDataObj.getProductId());
        }
        params.put("itemname", baaDataObj.getItemName());
        params.put("designer", baaDataObj.getDesignerId());
        params.put("phone", etPhNum.getText().toString().trim());
        params.put("message", etMsgAppoint.getText().toString().trim());
        params.put("name", etName.getText().toString().trim());
        params.put("bookdate", tvBookDate.getText().toString().trim());
        return params;
    }

    private void bringUpSuccessBookingScreen() {
        try {
            startAnim(llScreen1, R.anim.slide_out_into_left);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                llScreen1.setAlpha(0);
                llScreen1.setVisibility(View.GONE);
                llScreen2.setVisibility(View.VISIBLE);
                try {
                    startAnim(llScreen2, R.anim.slide_in_from_right);
                    llScreen2.setAlpha(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 400);
    }


    private void startAnim(View view, int animResID) throws Exception {

        Animation anim = AnimationUtils.loadAnimation(this, animResID);
        view.startAnimation(anim);
    }

    private void sendAppointmentRequest(Map<String, String> params) {
        showOverLay();
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(BOOK_APPOINTMENT_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQuery().ajax(ApiKeys.getAppointmentAPI(), params, String.class, ajaxCallback);
    }


    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djweb", "url queried- BookAppointment: " + url);
        Log.d("djweb", "response- BookAppointment: " + json);
        dismissOverLay();
        if (id == BOOK_APPOINTMENT_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    etPhNum, this);
            /*if (json == null) {
                srspListener.onNegativeResponse(Constants.ERR_MSG_ISE);
                return;
            }*/
            /*String errMsg = "";
            try {
                errMsg = new JSONObject((String) json).getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
                errMsg = Constants.ERR_MSG_1;
            }*/
            if (success) {
                /*if (json == null) {
                    srspListener.onNegativeResponse(errMsg);
                } else {*/
                    srspListener.onPositiveResponse();
                //}
            } //else srspListener.onNegativeResponse(errMsg);
        } else super.serverCallEnds(id, url, json, status);
    }


    private void failedInfo(String msg) {
        final Snackbar snackbar = Snackbar.make(etName, msg, Snackbar.LENGTH_SHORT);
        ColoredSnackbar.alert(snackbar).show();
    }

    /*public ExtendedAjaxCallback getAjaxCallBackCustom(int requestId) {
        return getAjaxCallback(requestId);
    }

    public AQuery getAQueryCustom() {
        return getAQuery();
    }*/

    private Dialog overLayDialog;
    @Bind(R.id.progressBar)
    ProgressView pgView;

    private void showOverLay() {
        /*if (overLayDialog == null) {
            overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlay(this, text, colorResId,
                    WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        }
        overLayDialog.show();*/
        btnTvBookNow.setEnabled(false);
        btnTvBookNow.setClickable(false);
        pgView.setVisibility(View.VISIBLE);
    }

    private void dismissOverLay() {
        /*if (overLayDialog != null) {
            if (overLayDialog.isShowing()) {
                overLayDialog.dismiss();
            }
        }*/
        btnTvBookNow.setEnabled(true);
        btnTvBookNow.setClickable(true);
        pgView.setVisibility(View.GONE);
    }


    View.OnClickListener finishClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        if (llScreen2.getVisibility() == View.VISIBLE) {
            setResult(Activity.RESULT_OK);
        } else setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    public static final int DESIGNER = 197;
    public static final int COLLECTION = 198;
    public static final int PRODUCTS = 199;

    private void setScreenData(int wicMode) {

        switch (wicMode) {
            case DESIGNER:
                break;
            case COLLECTION:
                break;
            case PRODUCTS:
                break;
            default:
                break;
        }
    }
}
