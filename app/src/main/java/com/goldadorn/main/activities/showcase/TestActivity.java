package com.goldadorn.main.activities.showcase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.goldadorn.main.R;
import com.payu.custombrowser.Bank;
import com.payu.custombrowser.PayUWebChromeClient;
import com.payu.custombrowser.PayUWebViewClient;

/**
 * Created by Vijith Menon on 6/3/16.
 */
public class TestActivity extends Activity {
//    private WebView mWebView;
//    private BroadcastReceiver mReceiver = null;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test_activity);
//
//        webView = (WebView) findViewById(R.id.webview);
//        try {
//            Class.forName("com.payu.custombrowser.Bank");
//            final Bank bank = new Bank() {
//                @Override
//                public void registerBroadcast(BroadcastReceiver broadcastReceiver, IntentFilter filter) {
//                    mReceiver = broadcastReceiver;
//                    registerReceiver(broadcastReceiver, filter);
//                }
//
//                @Override
//                public void unregisterBroadcast(BroadcastReceiver broadcastReceiver) {
//                    if(mReceiver != null){
//                        unregisterReceiver(mReceiver);
//                        mReceiver = null;
//                    }
//                }
//
//                @Override
//                public void onHelpUnavailable() {
//                    findViewById(R.id.parent).setVisibility(View.GONE);
//                    findViewById(R.id.trans_overlay).setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onBankError() {
//                    findViewById(R.id.parent).setVisibility(View.GONE);
//                    findViewById(R.id.trans_overlay).setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onHelpAvailable() {
//                    findViewById(R.id.parent).setVisibility(View.VISIBLE);
//                }
//            };
//            Bundle args = new Bundle();
//            args.putInt("webView", R.id.webview);
//            args.putInt("tranLayout",R.id.trans_overlay);
//            args.putInt("mainLayout",R.id.r_layout);
//
//            String [] list =  payuConfig.getData().split("&");
//            String txnId = null;
//            String merchantKey = null;
//            for (String item : list) {
//                if(item.contains("txnid")){
//                    txnId = item.split("=")[1];
//                }else if (item.contains("key")){
//                    merchantKey = item.split("=")[1];
//                }
//                if (null != txnId && null != merchantKey) break;
//            }
//            args.putString(Bank.TXN_ID, txnId == null ? String.valueOf(System.currentTimeMillis()) : txnId);
//            args.putString("merchantid", null != merchantKey ? merchantKey : "could not find");
//            PayUSdkDetails payUSdkDetails = new PayUSdkDetails();
//            args.putString("sdkcode", "VersionCode: " + payUSdkDetails.getSdkVersionCode() + " VersionName: " + payUSdkDetails.getSdkVersionName());
//            if(getIntent().getExtras().containsKey("showCustom")) {
//                args.putBoolean("showCustom", getIntent().getBooleanExtra("showCustom", false));
//            }
//            args.putBoolean("showCustom", true);
//            bank.setArguments(args);
//            findViewById(R.id.parent).bringToFront();
//            try {
//                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.cb_fade_in, R.anim.cb_face_out).add(R.id.parent, bank).commit();
//            }catch(Exception e)
//            {
//                e.printStackTrace();
//                finish();
//            }
//            mWebView.setWebChromeClient(new PayUWebChromeClient(bank));
//            mWebView.setWebViewClient(new PayUWebViewClient(bank));
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.postUrl(PAYMENT_URL, POSTDATA, "base64"));
//        // In case of using PayU PG
//        //url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV?  PayuConstants.PRODUCTION_PAYMENT_URL : //PayuConstants.MOBILE_TEST_PAYMENT_URL ;
//        //webView.postUrl(url, EncodingUtils.getBytes(payuConfig.getData(), "base64"));
//    }
}
