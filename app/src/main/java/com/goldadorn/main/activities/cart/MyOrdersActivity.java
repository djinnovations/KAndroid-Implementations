package com.goldadorn.main.activities.cart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.dj.model.GetCartResponseObj;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyOrdersActivity extends BaseActivity implements ILoadingProgress {

    @Bind(R.id.frame)
    FrameLayout fmLayout;
    MyOrdersFragment myOrdersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        ButterKnife.bind(this);

        setUpTitle();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fmLayout.getId(), myOrdersFragment = new MyOrdersFragment()/*, String.valueOf(uistate)*/);
        fragmentTransaction.commit();
    }

    private void setUpTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitleTextColor(Color.WHITE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Orders");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });
    }


    public final int REQ_CANCEL_CALL = IDUtils.generateViewId();
    public void sendCancelReqToServer(GetCartResponseObj.ProductItem product){
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallback(REQ_CANCEL_CALL);
        JSONObject params = new JSONObject();
        //Map<String, Object> params = new HashMap<>();
        try {
            params.put("sessionid", Application.getInstance().getCookies().get(0).getValue());
            JSONArray array = new JSONArray();
            String cancel = product.getOrderId()+":"+product.getProductId();
            array.put(cancel);
            params.put("cancel", array);
            params.put("userId", String.valueOf(Application.getInstance().getUser().id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(AQuery.POST_ENTITY, entity);
        getAQuery().ajax(ApiKeys.getCancelReqAPI(), paramsMap, String.class, ajaxCallback);
    }

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        Log.d("djcart", "url queried- MyOrdersActivity: " + url);
        Log.d("djcart", "response- MyOrdersActivity: " + json);
        dismissOverLay();
        if (id == REQ_CANCEL_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    viewForSnackBar, this);
            if (success)
                myOrdersFragment.onCartChanged();
        }
        else
        super.serverCallEnds(id, url, json, status);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //finish();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }*/


    private Dialog overLayDialog;

    public void showOverLay(String text, int colorResId, int gravity) {
        //if (overLayDialog == null) {
        overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlayLogo(this, text, colorResId,
                gravity);
        //}
        Log.d("djcart", "showOverLay");
        overLayDialog.show();
    }

    public void dismissOverLay() {
        if (overLayDialog != null) {
            if (overLayDialog.isShowing()) {
                WindowUtils.marginForProgressViewInGrid = 5;
                overLayDialog.dismiss();
            }
        }
    }

    ProgressDialog mProgressDialog;

    @Override
    public void showLoading(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }
}
