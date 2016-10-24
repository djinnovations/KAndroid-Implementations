package com.goldadorn.main.activities.cart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.dj.model.GetCartResponseObj;
import com.goldadorn.main.dj.model.ShipmentBillingAddress;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.uiutils.ResourceReader;
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
import com.goldadorn.main.utils.TypefaceHelper;
import com.google.gson.Gson;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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


    @Bind(R.id./*continueButton*/btnPlaceOrder)
    TextView mContinueButton;
    @Bind(R.id.payInfo)
    View payInfo;
    @Bind(R.id.tvOrderAmount)
    TextView tvOrderAmount;
    @Bind(R.id.ivShieldCheck)
    ImageView ivShieldCheck;
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
    @Bind(R.id.tvShieldCheck)
    TextView tvShieldCheck;
    @Bind(R.id.tvCartTotal)
    TextView tvCartTotal;

    public ArrayList<GetCartResponseObj.ProductItem> mCartMain = new ArrayList<>();
    public long mCostTotal;
    private boolean mPaymentSuccess;
    private Address mSelectedAddress;


    public static Intent getLaunchIntent(Context context) {
        Intent in = new Intent(context, CartManagerActivity.class);
        return in;
    }

    public void queryPincodeAddressData(String pincode){
        showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        /*ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(PINCDOE_ADD_CALL);
        ajaxCallback.method(AQuery.METHOD_POST);
        //ajaxCallback.params(params);
        Map<String, String> params = new HashMap<>();
        params.put("pin", pincode);
        params.put("project-app-key", Constants.WHIZ_API_PROJECT_KEY);
        getAQueryCustom().ajax(ApiKeys.getPinCodeWhizAPI(pincode), params, String.class, ajaxCallback);*/
        sendwhizquery(pincode);
    }

    public void launchMyOrderScreen(){
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_my_orders);
        if (navigationDataObject != null)
            action(navigationDataObject);
    }

    public void sendwhizquery(final String pincode){
            new Thread() {
                public void run() {
                    Uri.Builder uriBuilder = Uri.parse(ApiKeys.getPinCodeWhizAPI()).buildUpon();
                    uriBuilder.appendQueryParameter("pin", pincode);
                    uriBuilder.appendQueryParameter("project-app-key", Constants.WHIZ_API_PROJECT_KEY);
                    String queryUrl = uriBuilder.build().toString();
                    JSONObject jsonResponse = exceuteGetRequest(queryUrl);
                    if (jsonResponse != null)
                        Log.d("dj", "check user method: " + jsonResponse.toString());
                    if (addressFragment != null){
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse.toString());
                            JSONObject data = jsonObject.getJSONArray("Data").getJSONObject(0);
                            final PayUHelper.PincodeFieldData fieldData = new PayUHelper
                                    .PincodeFieldData(data.getString("Address"), data.getString("City")
                                    , data.getString("State"), data.getString("Country"));
                            Application.getInstance().getUIHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    dismissOverLay();
                                    addressFragment.updateFields(fieldData);
                                }
                            });
                        } catch (JSONException e) {
                            dismissOverLay();
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
    }

    private static final int ADD_TO_CART_CALL = IDUtils.generateViewId();

    public void addToCartV27(GetCartResponseObj.ProductItem product){
        //showOverLay(null, 0, WindowUtils.PROGRESS_FRAME_GRAVITY_CENTER);
        ExtendedAjaxCallback ajaxCallback = getAjaxCallBackCustom(ADD_TO_CART_CALL);
        Map<String, Object> map = new HashMap<>();
        map.put("prodId", product.getProductId());
        map.put("prodName", product.getProductName());
        map.put("prodType", product.getProdType());
        map.put("desgnId", product.getDesgnId());
        map.put("primaryMetal", product.getMetalType());
        map.put("primaryMetalPurity", product.getMetalPurity());
        map.put("primaryMetalColor", product.getMetalColor());
        map.put("size", product.getProdSize());
        map.put("priceUnits", "Rs");
        map.put("productEDTInDays", product.getTimeSLA());
        map.put("totalPrice", Math.round(product.getPricePaid()));
        map.put("offPrice", Math.round(product.getOffPrice()));
        map.put("discount", product.getDiscount());
        map.put("metalPrice", product.getMetalPrice());
        map.put("stonePrice", product.getStonePrice());
        map.put("makingCharges", product.getMakingCharges());
        map.put("metalWeight", product.getMetalWeight());
        int curentQty = product.getOrderQty() - product.getPreviousQty();
        map.put("orderQty", curentQty);
        map.put("VAT", product.getVAT());
        map.put("metalSel", product.getMetalSel());
        map.put("stoneSel", product.getStoneSel());
        map.put("sessionid", Application.getInstance().getCookies().get(0).getValue());
        Log.d("djprod", "reqParams- addToCartv27: " + map.toString());
        getAQueryCustom().ajax(ApiKeys.getAddtoCartV27(), map,String.class, ajaxCallback);
    }


    protected JSONObject exceuteGetRequest(String queryUrl){

        Log.d("dj", "query url - exceuteGetRequest: "+queryUrl);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet genericHttpGetRequest = new HttpGet(queryUrl);

        InputStream is = null;
        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(genericHttpGetRequest);
            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();
            // writing response to log
        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }

        return getJsonFromInputStream(is);
    }


    private JSONObject getJsonFromInputStream(InputStream is){

        JSONObject genericJsonObj = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            String jsonResponse = sb.toString();
            Log.d("dj", "response - getJsonFromInputStream: "+jsonResponse);
            genericJsonObj = new JSONObject(jsonResponse);
            is.close();

        } catch (Exception e) {

            Log.e("Buffer Error", "Error converting result " + e.toString());
            e.printStackTrace();
        }

        return genericJsonObj;

    }


    public void contactUs() {
        NavigationDataObject navigationDataObject = (NavigationDataObject) getApp().getMainMenu().get(R.id.nav_contact_us);
        if (navigationDataObject != null)
            action(navigationDataObject);
    }

    @Override
    public int getItemCount() {
        return getTotalQty(mCartMain);
    }


    public int getTotalQty(List<GetCartResponseObj.ProductItem> prodList){
        int totalQty = 0;
        for (GetCartResponseObj.ProductItem product : prodList) {
            totalQty = totalQty + product.getOrderQty();
        }
        return totalQty;
    }

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
        }
        else if (id == ADD_TO_CART_CALL){
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mContinueButton, this);
            if (success){
                if (myCartFragment != null) {
                    Toast.makeText(Application.getInstance(), "Item updated", Toast.LENGTH_SHORT).show();
                    myCartFragment.onCartChanged();
                }
            }
        }
        else if (id == SET_CART_ADDRESS_CALL) {
            if (mProgressDialog.isShowing()) {
                showLoading(false);
            }
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mContinueButton, this);
        /*}else if (id == SHIP_ADRESS_CALL){*/
            if (success) {
                /*AddressFragment af = (AddressFragment) getSupportFragmentManager().findFragmentByTag(String.valueOf(UISTATE_ADDRESS));
                af.refreshAddr();*/
                configureUI(UISTATE_ADDRESS);
                Toast.makeText(getApplicationContext(), "Address updated", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Address could not be updated", Toast.LENGTH_SHORT).show();

        } else if (id == NOTIFY_PAYMENT_CALL) {
            if (json == null) {
                Toast.makeText(getApplicationContext(), "Unable to process payment", Toast.LENGTH_SHORT).show();
                return;
            }
            //status.
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                    mContinueButton, this);
            if (success) {
                if (mPaymentSuccess) {
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());
                        setOrderId(jsonObject.getString("orderId"));
                        configureUI(UISTATE_FINAL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.colorBlackDimText);
        ivShieldCheck.setImageDrawable(new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_shield_check)
                .color(color)
                .sizeDp(30));

        ((TextView) mContainerProgressImage.getChildAt(UISTATE_CART).findViewById(R.id.text)).setText("My cart");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_ADDRESS).findViewById(R.id.text)).setText("Address");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_PAYMENT).findViewById(R.id.text)).setText("Payment");
        ((TextView) mContainerProgressImage.getChildAt(UISTATE_FINAL).findViewById(R.id.text)).setText("Complete");
        setTypeface();
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

    private void setTypeface() {
        TypefaceHelper.setFont(tvCartTotal, tvOrderAmount, tvShieldCheck, mContinueButton);
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
            displayStaticBar();
            return true;
        }
        return false;
    }

    private AddAddressFragment addressFragment;
    private MyCartFragment myCartFragment;

    public void configureUI(int uistate) {
        mUIState = uistate;
        Fragment f = null;
        int frame = R.id.frame;
        displayStaticBar();
        //paymentModeUI.setVisibility(View.GONE);
        if (uistate == UISTATE_CART) {
            f = myCartFragment = new MyCartFragment();
            //mContinueButton.setText("Select address ->");
            mContinueButton.setVisibility(View.VISIBLE);
        } else if (uistate == UISTATE_ADDRESS) {
            if (addressList.size() > 0 && !isEditAddressCall) {
                mUIState = uistate = /*UISTATE_ADDRESS*/UISTATE_PAYMENT;
                f = new /*AddressFragment()*/PaymentFragment();
                mContinueButton.setVisibility(View.VISIBLE);
            } else {
                frame = R.id.frame_overlay;
                mUIState = uistate = UISTATE_OVERLAY_ADD_ADDRESS;
                f = addressFragment = AddAddressFragment.newInstance(mAddressToEdit);
            }
            //mContinueButton.setText("Proceed to payment");
        } else if (uistate == UISTATE_PAYMENT) {
            f = new PaymentFragment();
            //paymentModeUI.setVisibility(View.VISIBLE);
            //mContinueButton.setVisibility(View.INVISIBLE);
        } else if (uistate == UISTATE_FINAL) {
            f = new SummaryFragment();
            Bundle args = new Bundle();
            args.putBoolean(IntentKeys.COD_CALL, isCOD);

            payInfo.setVisibility(View.GONE);
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) mContinueButton.getLayoutParams();
            param.weight = 2;
            mContinueButton.setLayoutParams(param);
            mContinueButton.setOnClickListener(mClickListener);

            f.setArguments(args);
            mContinueButton.setText("Go To Social Feed");
            mContinueButton.setVisibility(View.VISIBLE);
        } /*else if (uistate == UISTATE_OVERLAY_ADD_ADDRESS) {
            frame = R.id.frame_overlay;
            f = AddAddressFragment.newInstance(mAddressToEdit);
        }*/
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

    @Bind(R.id.shadow)
    View shadow;

    public void removeStaticBottomBar() {
        paymentModeUI.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);
    }

    public void displayStaticBar() {
        paymentModeUI.setVisibility(View.VISIBLE);
        shadow.setVisibility(View.VISIBLE);
    }

    public Button getPlaceOrderBtn() {
        return (Button) paymentModeUI.findViewById(R.id.btnPlaceOrder);
    }

    public View getPayInfoView() {
        return payInfo;
    }

    public TextView getTvAmount() {
        return (TextView) paymentModeUI.findViewById(R.id.tvOrderAmount);
    }

    private void refreshToolBar() {
        if (mUIState == /*UISTATE_OVERLAY_ADD_ADDRESS*/UISTATE_ADDRESS) {
            mToolbar.setTitle("Add Address");
        } else if (mUIState == UISTATE_FINAL) {
            mToolbar.setTitle("Order Summary");
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

    public void showDialogInfo(String msg, boolean isPositive) {
        int color;
        color = isPositive ? R.color.colorPrimary : R.color.Red;
        WindowUtils.getInstance(getApplicationContext()).genericInfoMsgWithOK(this, null, msg, color);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image) {
                if (true)
                    return;
                /*if (summaryFragment != null) {
                    if (summaryFragment.getUserVisibleHint()) {
                        showDialogInfo("Sorry, you cannot go back!", false);
                        return;
                    }
                }*/
                boolean complete = (boolean) v.getTag(TAG_PROGRESS);
                if (complete) {
                    int uistate = (int) v.getTag();
                    if (uistate != UISTATE_FINAL) {
                        configureUI(uistate);
                    }
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
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };

    @Override
    public void storeCartData(ArrayList<GetCartResponseObj.ProductItem> mCart, long costTotal) {
        mCartMain = new ArrayList<>();
        if (mCart != null) {
            mCartMain.addAll(mCart);
            mCostTotal = costTotal;
            Log.d("djcart", "mCostTotal: " + mCostTotal);
        }
    }

    private String orderId = "-1";
    @Override
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public ArrayList<GetCartResponseObj.ProductItem> getCartProducts() {
        return mCartMain;
    }

    @Override
    public long getBillableAmount() {
        return mCostTotal;
    }

    private boolean isCOD;
    String payMode;

    @Override
    public String getPaymentDone() {
        if (payMode.equals("net"))
            return "Net Banking";
        else if (payMode.equals("cre"))
            return "Credit Card";
        else if (payMode.equals("deb"))
            return "Debit Card";
        else if (payMode.equals("emi"))
            return "EMI";
        else if (payMode.equals("cod"))
            return "Cash On Delivery";
        return "";
    }


    public Address getShippingAddress() {
        return mSelectedAddress;
    }

    @Override
    public void setPaymentDone(boolean done, boolean isCOD, String payMode) {
        mPaymentSuccess = done;
        this.isCOD = isCOD;
        this.payMode = payMode;
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: CHECKOUT_SUCCESSFUL");
        logEventsAnalytics(GAAnalyticsEventNames.CHECKOUT_SUCCESSFUL);
        if (done) {
            /*Map<String, Object> params = new HashMap<>();
            params.put("userId", getApp().getUser().id);
            params.put("status", Integer.valueOf(2));*/
            JSONObject params = new JSONObject();
            try {
                params.put("userId", getApp().getUser().id);
                params.put("status", /*Integer.valueOf(2)*/"pass");
                params.put("sessionid", Application.getInstance().getCookies().get(0).getValue());
                JSONArray transIds = new JSONArray();
                int i = 0;
                for (GetCartResponseObj.ProductItem prod : mCartMain) {
                    transIds.put(prod.getTransId());
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
            Log.d("djcart", "req params - paymentMadeAPI" + params);
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put(AQuery.POST_ENTITY, entity);
            //params.put("transId", transIds);
            updatePaymentStatToServer(paramsMap);
        } else return;
        /*if (done) {
            configureUI(UISTATE_FINAL);
        }*/
    }


    public void logAnEvent(String eventName) {
        logEventsAnalytics(eventName);
        Log.d(Constants.TAG_APP_EVENT, "AppEventLog: eventName");
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
    private boolean isEditAddressCall;

    public void showAddAddress(Address address) {
        isEditAddressCall = true;
        mAddressToEdit = address;
        configureUI(/*UISTATE_OVERLAY_ADD_ADDRESS*/UISTATE_ADDRESS);
    }

    public void setAddressResult(ShipmentBillingAddress sba) {
        addressList.clear();
        isEditAddressCall = false;
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