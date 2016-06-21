package com.goldadorn.main.activities.showcase;

import android.app.Dialog;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.rey.material.widget.ProgressView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

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

    private String name;
    private String id;
    private String url;
   /* private String collectionId;
    private String productId;
    private String collectionName;
    private String productName;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        ButterKnife.bind(this);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            name = args.getString(IntentKeys.BOOK_APPOINT_DETAILS_NAME);
            id = args.getString(IntentKeys.BOOK_APPOINT_DETAILS_ID);
            url = args.getString(IntentKeys.BOOK_APPOINT_DETAILS_URL);
            /*collectionId = args.getString(IntentKeys.COLLECTION_DETAILS_ID);
            collectionName = args.getString(IntentKeys.COLLECTION_DETAILS_NAME);
            productId = args.getString(IntentKeys.PRODUCT_DETAILS_ID);
            productName = args.getString(IntentKeys.PRODUCT_DETAILS_NAME);*/
            Log.d("djappoint", "name: " + name);
            Log.d("djappoint", "id: " + id);
            Log.d("djappoint", "url: " + url);
           /* Log.d("djappoint", "collectionId: " + collectionId);
            Log.d("djappoint", "collectionName: " + collectionName);
            Log.d("djappoint", "productId: " + productId);
            Log.d("djappoint", "productName: " + productName);*/
        }
        dismissOverLay();
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Book Appoin");*/
        setCustomTitle();
        if (!TextUtils.isEmpty(url))
            Picasso.with(getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.img_timeline)
                    .into(ivHeader);
        //tvNegative.setOnClickListener(finishClickListener);
        tvOkay.setOnClickListener(finishClickListener);
        btnTvBookNow.setOnClickListener(positiveClick);
    }

    private void setCustomTitle() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedText);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedText);

        collapsingToolbar.setTitle(name == null ? "Designer Name" : name);
    }


    View.OnClickListener positiveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (etPhNum.getText().toString().trim().length() < 10) {
                Toast.makeText(getApplicationContext(), "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "Please provide your name", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(etMsgAppoint.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "Please leave a message", Toast.LENGTH_SHORT).show();
                return;
            }
            doTransition();
        }
    };

    interface ServerResponseListener {
        void onPositiveResponse();

        void onNegativeResponse();
    }

    private ServerResponseListener srspListener = new ServerResponseListener() {
        @Override
        public void onPositiveResponse() {
            bringUpSuccessBookingScreen();
        }

        @Override
        public void onNegativeResponse() {
            Toast.makeText(getApplicationContext(), "Unable to make an Appointment, Please try again ", Toast.LENGTH_LONG).show();
        }
    };

    private final int BOOK_APPOINTMENT_CALL = IDUtils.generateViewId();

    private void doTransition() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", etPhNum.getText().toString().trim());

        if (!TextUtils.isEmpty(id))
            params.put("designer", String.valueOf(id));
        /*if (!TextUtils.isEmpty(collectionId))
            params.put("collection", collectionId);
        if (!TextUtils.isEmpty(productId))
            params.put("product", productId);
        if (!TextUtils.isEmpty(collectionName))
            params.put("collName", collectionName);
        if (!TextUtils.isEmpty(productName))
            params.put("prodName", productName);*/

        params.put("message", etMsgAppoint.getText().toString().trim());

        Log.d("djappoint", "req params- book appointment: " + params);
        sendAppointmentRequest(params);
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
        showOverLay(null, R.color.colorPrimaryDark);
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
            if (success) {
                if (json == null)
                    srspListener.onNegativeResponse();
                else {
                    srspListener.onPositiveResponse();
                }
            } else srspListener.onNegativeResponse();
        } else super.serverCallEnds(id, url, json, status);
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

    private void showOverLay(String text, int colorResId) {
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
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static final int DESIGNER = 197;
    public static final int COLLECTION = 198;
    public static final int PRODUCTS = 199;
    private void setScreenData(int wicMode){

        switch (wicMode){
            case DESIGNER:
                break;
            case COLLECTION:
                break;
            case PRODUCTS:
                break;
            default: break;
        }
    }
}
