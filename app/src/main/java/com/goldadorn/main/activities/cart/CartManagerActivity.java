package com.goldadorn.main.activities.cart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.dj.model.ShipmentBillingAddress;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.Address;
import com.goldadorn.main.model.NavigationDataObject;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.response.SearchResponse;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 08/03/16.
 */
public class CartManagerActivity extends BaseActivity implements ICartData, ILoadingProgress {
    public static final int UISTATE_CART = 0;
    public static final int UISTATE_ADDRESS = 1;
    public static final int UISTATE_PAYMENT = 2;
    public static final int UISTATE_FINAL = 3;
    public static final int UISTATE_OVERLAY_ADD_ADDRESS = 10;
    private static final int TAG_PROGRESS = R.id.tag_progress;
    private int mUIState = UISTATE_CART;
    private int mMainUiState = UISTATE_CART;
    Context mContext;


    @Bind(R.id.continueButton)
    TextView mContinueButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.frame_overlay)
    View mOverlayFrame;
    @Bind(R.id.container_progress_image)
    LinearLayout mContainerProgressImage;
    @Bind(R.id.scrollview)
    ScrollView mScollView;
    @Bind(R.id.paymentModeUI)
    View paymentModeUI;
    ProgressDialog mProgressDialog;

    public List<Product> mCartItems = new ArrayList<>();
    public long mCostTotal;
    private boolean mPaymentSuccess;
    private Address mSelectedAddress;


    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, CartManagerActivity.class);
        return in;
    }

    public void contactUs() {
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_contact_us);
        if (navigationDataObject != null)
            action(navigationDataObject);
    }


    private Dialog overLayDialog;

    public void showOverLay(String text, int colorResId, int gravity) {
        //if (overLayDialog == null) {
        overLayDialog = WindowUtils.getInstance(getApplicationContext()).displayOverlay(this, text, colorResId,
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


    public AQuery getAQueryCustom() {
        return getAQuery();
    }

    //private String mShippingAddress;
    private ArrayList<Address> addressList;

    public ArrayList<Address> getShippingAdress() {
        return addressList;
    }

    public static final String TYPE_ADDRESS_SHIPPING = "s";
    public static final String TYPE_ADDRESS_BILLING = "b";

    private static final int /*BILL*/ GET_CART_ADRESS_CALL = IDUtils.generateViewId();
    //private static final int SHIP_ADRESS_CALL = IDUtils.generateViewId();

    private void fetchAddressFromServer(String type/*, int reqType*/) {
        if (!mProgressDialog.isShowing()) {
            showLoading(true);
        }
        ExtendedAjaxCallback ajaxCallback = /*null;
        if (reqType == BILL_ADRESS_CALL)
            ajaxCallback = getAjaxCallBackCustom(BILL_ADRESS_CALL);
        else if (reqType == SHIP_ADRESS_CALL) ajaxCallback*/ getAjaxCallBackCustom(GET_CART_ADRESS_CALL);

        ajaxCallback.method(AQuery.METHOD_GET);
        getAQueryCustom().ajax(ApiKeys.getCartAddressAPI(type), String.class, ajaxCallback);
    }

    private final int SET_CART_ADDRESS_CALL = IDUtils.generateViewId();

    public void passCartAddressToServer(Map<String, String> params, String type) {

        if (!mProgressDialog.isShowing()) {
            showLoading(true);
        }
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(SET_CART_ADDRESS_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        getAQueryCustom().ajax(ApiKeys.getSetCartAddressAPI(), params, String.class, ajaxCallback);
    }

    private final int NOTIFY_PAYMENT_CALL = IDUtils.generateViewId();

    private void updatePaymentStatToServer(Map<String, Object> params) {
        Log.d("djcart", "req param - updatePaymentStatToServer: " + params);
        WindowUtils.marginForProgressViewInGrid = 20;
        showOverLay("Processing...", R.color.colorPrimary, WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(NOTIFY_PAYMENT_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        //ajaxCallback.params(params);
        getAQueryCustom().ajax(ApiKeys.getUpdatePaymentAPI(), params, String.class, ajaxCallback);
    }

    @Override
    public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {

        Log.d("djcart", "url queried- CartManagerActivity: " + url);
        Log.d("djcart", "response- CartManagerActivity: " + json);
        dismissOverLay();
        if (id == GET_CART_ADRESS_CALL) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mContinueButton, this);
        /*}else if (id == SHIP_ADRESS_CALL){*/
            addressList = new ArrayList<>();
            if (success) {
                Gson gson = new Gson();
                ShipmentBillingAddress sba = gson.fromJson((String) json, ShipmentBillingAddress.class);
                Log.d("djcart", sba.toString());
                if (sba.getFname() != null) {
                    addressList.add(getAddressObj(sba));
                }
            }
        } else if (id == SET_CART_ADDRESS_CALL) {
            if (mProgressDialog.isShowing()) {
                showLoading(false);
            }
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mContinueButton, this);
        /*}else if (id == SHIP_ADRESS_CALL){*/
            if (success) {
                AddressFragment af = (AddressFragment) getSupportFragmentManager().findFragmentByTag(String.valueOf(UISTATE_ADDRESS));
                af.refreshAddr();
                Toast.makeText(getApplicationContext(), "Address updated", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Address could not be updated", Toast.LENGTH_SHORT).show();

        } else if (id == NOTIFY_PAYMENT_CALL) {
            if (json == null) {
                Toast.makeText(getApplicationContext(), "Unable to process payment", Toast.LENGTH_SHORT).show();
                return;
            }
            //status.
            boolean success = NetworkResultValidator.getInstance().isResultOK(url,(String) json, status, null,
                    mContinueButton, this);
            if (success) {
                if (mPaymentSuccess) {
                    configureUI(UISTATE_FINAL);
                }
            } else
                Toast.makeText(getApplicationContext(), "Unable to process", Toast.LENGTH_SHORT).show();
        } else super.serverCallEnds(id, url, json, status);
    }

    private Address getAddressObj(ShipmentBillingAddress sba) {
        String lastName = "";
        if (!TextUtils.isEmpty(sba.getLname())) {
            lastName = sba.getLname();
        }
        String addr2 = "";
        if (!TextUtils.isEmpty(sba.getAddress2())) {
            addr2 = sba.getAddress2();
        }
        String street = sba.getAddress1() + (addr2.equals("") ? "" : ", " + addr2);
        String fullName = sba.getFname() + (lastName.equals("") ? "" : " " + lastName);
        return new Address(fullName, street, sba.getCity(), sba.getState(), sba.getCountry(), sba.getPhone(), sba.getPincode());
    }


    public ExtendedAjaxCallback getAjaxCallBackCustom(int requestId) {
        return getAjaxCallback(requestId);
    }
    /*public long getTotalAmount(){
        return mCostTotal;
    }*/

    /*private void logEventsAnalytics(String eventName) {
        ((Application) getApplication()).getFbAnalyticsInstance().logCustomEvent(this, eventName);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_cart_manager);
        ButterKnife.bind(this);

        logEventsAnalytics(GAAnalyticsEventNames.CART_VIEWED);
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: Cart viewed");
        //logEventsAnalytics(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);

        fetchAddressFromServer(TYPE_ADDRESS_SHIPPING);

        ((TextView) mContainerProgressImage.getChildAt(UISTATE_CART).findViewById(R.id.text)).setText("My cart");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_ADDRESS).findViewById(R.id.text)).setText("Address");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_PAYMENT).findViewById(R.id.text)).setText("Payment");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_FINAL).findViewById(R.id.text)).setText("Complete");
        for (int i = 0; i < mContainerProgressImage.getChildCount(); i++) {
            ImageView iv = (ImageView) mContainerProgressImage.getChildAt(i).findViewById(R.id.image);
            iv.setTag(i);
            iv.setOnClickListener(mClickListener);
        }
        mContinueButton.setOnClickListener(mClickListener);
        configureUI(UISTATE_CART);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!closeOverlay())
                    finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (closeOverlay())
            return;
        super.onBackPressed();
    }

    private boolean closeOverlay() {
        if (mUIState != mMainUiState) {
            mOverlayFrame.setVisibility(View.GONE);
            mUIState = mMainUiState;
            refreshToolBar();
            return true;
        }
        return false;
    }

    public void configureUI(int uistate) {
        mUIState = uistate;
        Fragment f = null;
        int frame = R.id.frame;
        paymentModeUI.setVisibility(View.GONE);
        if (uistate == UISTATE_CART) {
            f = new MyCartFragment();
            mContinueButton.setText("Select address ->");
            mContinueButton.setVisibility(View.VISIBLE);
        } else if (uistate == UISTATE_ADDRESS) {
            f = new AddressFragment();
            mContinueButton.setVisibility(View.VISIBLE);
            mContinueButton.setText("Proceed to payment");
        } else if (uistate == UISTATE_PAYMENT) {
            f = new PaymentFragment();
            paymentModeUI.setVisibility(View.VISIBLE);
            mContinueButton.setVisibility(View.INVISIBLE);
        } else if (uistate == UISTATE_FINAL) {
            f = new SummaryFragment();
            Bundle args = new Bundle();
            args.putBoolean(IntentKeys.COD_CALL, isCOD);
            f.setArguments(args);
            mContinueButton.setText("Go To Social Feed");
            mContinueButton.setVisibility(View.VISIBLE);
        } else if (uistate == UISTATE_OVERLAY_ADD_ADDRESS) {
            frame = R.id.frame_overlay;
            f = AddAddressFragment.newInstance(mAddressToEdit);
        }
        if (frame == R.id.frame_overlay) {
            mOverlayFrame.setVisibility(View.VISIBLE);
        } else {
            mMainUiState = uistate;
            bindProgress(uistate);
            mOverlayFrame.setVisibility(View.GONE);
        }
        if (f != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(frame, f, String.valueOf(uistate));
            fragmentTransaction.commit();
        }
        mScollView.scrollTo(0, 0);
        refreshToolBar();
    }

    public Button getPlaceOrderBtn() {
        return (Button) paymentModeUI.findViewById(R.id.btnPlaceOrder);
    }

    public TextView getTvAmount() {
        return (TextView) paymentModeUI.findViewById(R.id.tvOrderAmount);
    }

    private void refreshToolBar() {
        if (mUIState == UISTATE_OVERLAY_ADD_ADDRESS) {
            mToolbar.setTitle("Add Address");
        } else {
            mToolbar.setTitle("My Cart");
        }

    }

    @Override
    public void showLoading(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.dismiss();
    }

    private void bindProgress(int index) {
        for (int i = 0; i < mContainerProgressImage.getChildCount(); i++) {
            ImageView iv = ((ImageView) mContainerProgressImage.getChildAt(i).findViewById(R.id.image));
            if (index >= i) {
                iv.setImageResource(R.drawable.bg_thumb);
                iv.setTag(TAG_PROGRESS, true);
            } else {
                iv.setImageResource(R.drawable.bg_thumb_n);
                iv.setTag(TAG_PROGRESS, false);
            }

        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                boolean complete = (boolean) v.getTag(TAG_PROGRESS);
                if (complete) {
                    int uistate = (int) v.getTag();
                    if (uistate != UISTATE_FINAL)
                        configureUI(uistate);
                }
            } else {
                if (mUIState == UISTATE_CART) {
                    if (getBillableAmount() > 0)
                        configureUI(UISTATE_ADDRESS);
                    else {
                        Toast.makeText(mContext, "No items in Cart", Toast.LENGTH_SHORT).show();
                    }
                } else if (mUIState == UISTATE_ADDRESS) {
                    if (mSelectedAddress != null)
                        configureUI(UISTATE_PAYMENT);
                    else {
                        Toast.makeText(mContext, "Please Select or Add an address", Toast.LENGTH_SHORT).show();
                    }
                } else if (mUIState == UISTATE_FINAL) {
                    Intent in = new Intent(mContext, MainActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }
            }
        }
    };

    @Override
    public void storeCartData(ArrayList<Product> cart, long costTotal) {
        mCartItems.clear();
        if (cart != null) {
            mCartItems.addAll(cart);
            mCostTotal = costTotal;
            Log.d("djcart", "mCostTotal: " + mCostTotal);
        }
    }

    @Override
    public List<Product> getCartProducts() {
        return mCartItems;
    }

    @Override
    public long getBillableAmount() {
        return mCostTotal;
    }

    private boolean isCOD;

    @Override
    public void setPaymentDone(boolean done, boolean isCOD, String payMode) {
        mPaymentSuccess = done;
        this.isCOD = isCOD;
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CHECKOUT_SUCCESSFUL");
        logEventsAnalytics(GAAnalyticsEventNames.CHECKOUT_SUCCESSFUL);
        if (done) {
            /*Map<String, Object> params = new HashMap<>();
            params.put("userId", getApp().getUser().id);
            params.put("status", Integer.valueOf(2));*/
            JSONObject params = new JSONObject();
            try {
                params.put("userID", getApp().getUser().id);
                params.put("status", /*Integer.valueOf(2)*/"pass");
                JSONArray transIds = new JSONArray();
                int i = 0;
                for (Product prod : mCartItems) {
                    transIds.put(prod.transid);
                }
                params.put("transId", transIds);
                params.put("modePay", payMode);
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
            //params.put("transId", transIds);
            updatePaymentStatToServer(paramsMap);
        } else return;
        /*if (done) {
            configureUI(UISTATE_FINAL);
        }*/
    }

    @Override
    public boolean isPaymentDone() {
        return mPaymentSuccess;
    }

    @Override
    public void storeAddressData(Address address) {
        mSelectedAddress = address;
    }

    private Address mAddressToEdit;

    public void showAddAddress(Address address) {
        mAddressToEdit = address;
        configureUI(UISTATE_OVERLAY_ADD_ADDRESS);
    }

    public void setAddressResult(ShipmentBillingAddress sba) {
        addressList.add(sba.getAddressDataObj());
        Map<String, String> params = new HashMap<>();
        params.put("fname", sba.getFname());
        params.put("lname", sba.getLname());
        params.put("phone", sba.getPhone());
        params.put("address1", sba.getAddress1());
        params.put("address2", sba.getAddress2());
        params.put("country", /*sba.getCountry()*/ "INDIA");
        params.put("state", sba.getState());
        params.put("city", sba.getCity());
        params.put("pincode", sba.getPincode());
        params.put("type", sba.getType());
        Log.d("djcart", "req params - setAddressResult: " + params);
        passCartAddressToServer(params, sba.getType());
    }

}
