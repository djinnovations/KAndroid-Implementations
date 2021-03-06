package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.LandingPageActivity;
import com.goldadorn.main.activities.LoginPageActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.dj.model.FbGoogleTweetLoginResult;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.server.RequestJson;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.LoginResult;
import com.goldadorn.main.model.User;
import com.goldadorn.main.utils.IDUtils;
import com.goldadorn.main.utils.NetworkResultValidator;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.kimeeo.library.actions.Action;
import com.kimeeo.library.ajax.ExtendedAjaxCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.apache.http.cookie.Cookie;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by COMP on 5/6/2016.
 */
public class SocialLoginUtil implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static SocialLoginUtil ourInstance;
    private static Context mAppContext;

    public static SocialLoginUtil getInstance(Context appContext) {

        mAppContext = appContext;
        if (ourInstance == null) {
            ourInstance = new SocialLoginUtil(appContext);
        }
        return ourInstance;
    }

    protected SocialLoginUtil(Context context) {

        //FacebookSdk.sdkInitialize(mAppContext);
        mAppContext = context;
        Log.d(Constants.TAG, "Social login new Obj creation");
        initializeFbAndGlTw(context);
        setFbCallBacks();
    }


    private Activity mActivity;
    private Dialog dialog;
    /*****************
     * Facebook stuffs
     ***********************/
    protected CallbackManager mFbCallbackManager;
    protected String[] permissionArr = new String[]{"user_location", "user_birthday", "email"};
    /*******************************************************/
    //private UserSession mUserSession;
    /*****************
     * Gmail stuffs
     ***********************/
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOpt;
    private ConnectionResult mConnectionResult;
    private SignInButton btnGmailLogin;
    private final int GMAIL_RC_SIGN_IN = 1991;
    private boolean isSignedIn = false;
    private boolean mIntentInProgress = false;

    /**************************************************************/
    private TwitterCore twitCore;
    private TwitterAuthClient twitAuthClient;


    private void initializeFbAndGlTw(Context context) {

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(Constants.API_KEY_TW,
                        Constants.API_SECRET_TW);

        Fabric.with(context, new Twitter(authConfig));
        twitCore = Twitter.getInstance().core;
        twitAuthClient = new TwitterAuthClient();

        /*************************************Facebook stuffs********************************************/
        mFbCallbackManager = CallbackManager.Factory.create();
        /********************************************************************************************/

        /**************************************Gmail stuffs**********************************************/
        //// TODO: 5/6/2016  
        mGoogleSignInOpt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Plus.SCOPE_PLUS_LOGIN, new Scope("email"))
                .requestIdToken(Constants.OAUTH_WEBCLIENT_ID_GL)
                .build();


        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOpt)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.connect();
            }
        }, 200);


        /*mGoogleSignInOpt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(Constants.OAUTH_WEBCLIENT_ID_GL)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mAppContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOpt)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/


        /*************************************************************************************************/
    }


    public void onGoogleLogin(Activity mActivity) {

        if (isSignedIn) {
            if (mGoogleApiClient.isConnected()) {
                performGoogleLogout();
                //// TODO: 5/4/2016
            }
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mActivity.startActivityForResult(signInIntent, GMAIL_RC_SIGN_IN);
        }
    }


    public void performGoogleLogout() {

        indicateSignedOut();
        Log.d(Constants.TAG, "performGoogleLogout called; signed in stat"+isSignedIn);
        if (!isSignedIn)
            return;
        /*if (mGoogleApiClient.isConnected()) {*/
            Log.d(Constants.TAG, "performGoogleLogout pt 1");
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // // TODO: 5/4/2016
                            Log.d(Constants.TAG, "performGoogleLogout success");
                            processRevokeRequest();
                            isSignedIn = false;
                        }
                    });
        //}
    }


    public boolean isGoogleConnected() {
        if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        return mGoogleApiClient.isConnected();
    }


    public void onFacebookLogin(Activity mActivity) {
        if (isFbSignedIn) {
            performFbLogout();
            return;
        }
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList(permissionArr));
    }

    private boolean isFbSignedIn;
    public void performFbLogout() {
        //// TODO: 5/6/2016
        if (!isFbSignedIn)
            return;
        indicateSignedOut();
        LoginManager.getInstance().logOut();
        Log.d(Constants.TAG, "performFbLogout success: ");
        isFbSignedIn = false;
    }

    public void clearFbPermission() {
        try {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/permissions",
                    null,
                    HttpMethod.DELETE,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                /* handle the result */
                            Log.d(Constants.TAG, "on clear permission fb response: " + response.toString());
                        }
                    }
            ).executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setResultListenerFb(com.facebook.login.LoginResult loginResult) {

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v(Constants.TAG, "fb loginResult response: " + response.toString());
                        try {
                            String name = "N/A";
                            name = object.getString("name");
                            Toast.makeText(mAppContext, "Hi "
                                + name + ", Welcome to Gold Adorn\nAuthorizing...please wait", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,location,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }


    Callback<TwitterSession> mCallBackTwit = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            //onSuccessfulLogin(new FbGoogleTweetLoginResult(null, null, result, Constants.PLATFORM_TWITTER));
        }

        @Override
        public void failure(TwitterException exception) {
            exception.printStackTrace();
            setErrSnackBar(Constants.ERR_MSG_NETWORK);
        }
    };


    public void onTwitterLogin(Activity mActivity) {
        twitAuthClient.authorize(mActivity, mCallBackTwit);
    }

    public void performTwitterLogout() {
        twitCore.logOut();
    }

    public void onActivityStart(Activity mActivity) {
        this.mActivity = mActivity;
        //mGoogleApiClient.connect();
    }


    public void onActivityStop() {
        /*if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();*/
    }


    private Dialog displayOverlay(String infoMsg, int colorResId) {

        Dialog dialog = new Dialog(mActivity);
        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		/*tempParams.width = dialogWidthInPx;
        tempParams.height = dialogHeightInPx;*/
        tempParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = 0.0f;

        View overLay = LayoutInflater.from(mAppContext).inflate(R.layout.dialog_overlay, null);
        TextView tvTemp = (TextView) overLay.findViewById(R.id.tvOverlayInfo);
        if (infoMsg != null) {
            tvTemp.setText(infoMsg);
            tvTemp.setTextColor(ResourceReader.getInstance(mAppContext).getColorFromResource(colorResId));
        } else tvTemp.setVisibility(View.GONE);
        dialog.setContentView(overLay);
        dialog.setCancelable(false);

        dialog.getWindow().setAttributes(tempParams);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }


    private void setFbCallBacks() {

        LoginManager.getInstance().registerCallback(mFbCallbackManager,
                new FacebookCallback<com.facebook.login.LoginResult>() {
                    @Override
                    public void onSuccess(com.facebook.login.LoginResult loginResult) {
                        Log.d(Constants.TAG, "Login fb successs");
                        //// TODO: 5/4/2016
                        setResultListenerFb(loginResult);
                        isFbSignedIn = true;
                        authFromServer(new FbGoogleTweetLoginResult(null, loginResult, null, Constants.PLATFORM_FACEBOOK),
                                Constants.PLATFORM_FACEBOOK);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(mAppContext, "Login Cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //probably no network connection at the moment (most of the times)
                        //Toast.makeText(mAppContext, exception.getMessage(), Toast.LENGTH_LONG).show();
                        setErrSnackBar(Constants.ERR_MSG_NETWORK);
                    }
                });
    }


    private void setErrSnackBar(String errMsg) {

        View viewForSnackBar = null;
        if (mActivity instanceof LandingPageActivity) {
            viewForSnackBar = ((LandingPageActivity) mActivity).loginAccount;
        } else if (mActivity instanceof LoginPageActivity) {
            viewForSnackBar = ((LoginPageActivity) mActivity).layoutParent;
        }
        if (viewForSnackBar != null) {
            final Snackbar snackbar = Snackbar.make(viewForSnackBar, errMsg,
                    Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }


    private View getViewForSnackBar() {

        View viewForSnackBar = null;
        if (mActivity instanceof LandingPageActivity) {
            viewForSnackBar = ((LandingPageActivity) mActivity).loginAccount;
        } else if (mActivity instanceof LoginPageActivity) {
            viewForSnackBar = ((LoginPageActivity) mActivity).layoutParent;
        }
        return viewForSnackBar;
    }


    /*private void onSuccessfulLogin(FbGoogleTweetLoginResult loginResults) {
        // TODO Auto-generated method stub
        if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_GOOGLE)) {

            GoogleSignInResult mGoogleResult = loginResults.getmGoogleLoginResult();
            GoogleSignInAccount mGoogleProfile = mGoogleResult.getSignInAccount();
            genericInfo("Google back-end Auth not yet implement; your signed in successful using Google");

        } else if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_FACEBOOK)) {

            LoginResult mFbLoginResult = loginResults.getFaceBookLoginResult();
            authWithServer(mFbLoginResult, true);

        }else if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_TWITTER)){

            String token = loginResults.getTwitterLoginResult().data.getAuthToken().token;
            String ver = twitCore.getVersion();
            Log.d(Constants.TAG, "token - onSuccessfulLogin: "+token);
            Log.d(Constants.TAG, "version - onSuccessfulLogin: "+ver);
            genericInfo("Twitter back-end Auth not yet implement; your signed in successful using twiiter");
        }

        //Intent loggedInActivityIntent = new Intent(this, LoggedInScreenActivity.class);
        //startActivity(loggedInActivityIntent);

    }*/


    /*private void authWithServer(com.facebook.login.LoginResult mFbLoginResult, boolean isFb) {

        final Dialog dialog = displayOverlay(null, R.color.colorAccent);
        dialog.show();
        GraphRequest req = new GraphRequest();
        String version = req.getVersion();

        RequestJson reqJsonInstance = RequestJson.getInstance();
        JSONObject json = null;
        if (isFb){
            json = reqJsonInstance.getSocialLoginReqMap(mFbLoginResult.getAccessToken().getToken(), version);
        }
        else {
            //// TODO: 5/6/2016  for google
            //json =
        }
        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, Constants.ENDPOINT_SOCIAL_LOGIN,
                json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(Constants.TAG, "response - authWithServer: "+response);
                        String status = "N/A";
                        String message = "N/A";

                        try {
                            status = response.getString(ApiKeys.STATUS);
                            message = response.getString(ApiKeys.MESSAGE);
                            Log.d(Constants.TAG, "status: "+status);
                            Log.d(Constants.TAG, "message: "+message);
                            //dialog.dismiss();
                            evaluateResults(status, message, dialog);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            //genericInfo(Constants.ERR_MSG_1);
                            setErrSnackBar(Constants.ERR_MSG_1);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d(Constants.TAG, "volley error: ");
                error.printStackTrace();
                //genericInfo(Constants.ERR_MSG_1);
                setErrSnackBar(Constants.ERR_MSG_NETWORK);
            }
        });

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.REQUEST_TIMEOUT_SOCIAL_LOGIN,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(mAppContext);
        requestQueue.add(loginRequest);

    }*/


    public static final int socialLoginCall = IDUtils.generateViewId();
    /*public static int glLoginCall = IDUtils.generateViewId();
    public static int twLoginCall = IDUtils.generateViewId();*/

    protected void authFromServer(FbGoogleTweetLoginResult loginResults, String platform) {

        displayOverlayDialog();
        GraphRequest req = new GraphRequest();
        String version = req.getVersion();

        RequestJson reqJsonInstance = RequestJson.getInstance();
        //String url = getUrlHelper().getLoginServiceURL();
        ExtendedAjaxCallback ajaxCallback = null;
        Map<String, String> paramsMap = null;
        if (mActivity instanceof LoginPageActivity) {
            ajaxCallback = ((LoginPageActivity) mActivity).getAjaxCallBackCustom(socialLoginCall);
        } else if (mActivity instanceof LandingPageActivity) {
            ajaxCallback = ((LandingPageActivity) mActivity).getAjaxCallBackCustom(socialLoginCall);
        }
        if (platform.equals(Constants.PLATFORM_FACEBOOK)) {
            com.facebook.login.LoginResult fbResult = loginResults.getFaceBookLoginResult();
            paramsMap = reqJsonInstance.getSocialLoginReqMap(fbResult.getAccessToken().getToken(), Constants.PLATFORM_FACEBOOK);
            ajaxCallback.setParams(paramsMap);
        } else if (platform.equals(Constants.PLATFORM_GOOGLE)) {
            //// TODO: 5/6/2016  for google
            //json =
            GoogleSignInAccount acct = loginResults.getmGoogleLoginResult().getSignInAccount();
            String accessToken = acct.getIdToken();
            paramsMap = reqJsonInstance.getSocialLoginReqMap(accessToken, Constants.PLATFORM_GOOGLE);
            ajaxCallback.setParams(paramsMap);
        } else if (platform.equals(Constants.PLATFORM_TWITTER)) {

        }
        paramsMap = RandomUtils.addPlatformParams(paramsMap);
        getAQuery().ajax(ApiKeys.ENDPOINT_SOCIAL_LOGIN, paramsMap, String.class, ajaxCallback);
    }


    public void serverCallEndsCustom(int id, String url, Object json, AjaxStatus status) {

        Log.d(Constants.TAG, "serverCallEndsCustom - social login response: " + json);
        boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null,
                getViewForSnackBar(), mActivity);
        List<Cookie> cookies = status.getCookies();
        for (Cookie coo : cookies) {
            Log.d(Constants.TAG, "serverCallEndsCustom - social login cookies: " + coo);
        }
        if (json != null && success) {
            Gson gson = new Gson();
            com.goldadorn.main.model.LoginResult loginResult = gson.fromJson((String) json, com.goldadorn.main.model.LoginResult.class);

            if (loginResult.getSuccess()) {

                    /*if (id == fbLoginCall) {
                        doSuccessOperation(loginResult, status.getCookies());
                    }
                    else if (id == glLoginCall){

                    }
                    else if (id == twLoginCall){

                    }*/
                doSuccessOperation(loginResult, cookies);
                //genericInfo("Authorization successful");
            } else {
                setErrSnackBar(loginResult.getMsg());
                dismissOverlayView();
            }
        }
        dismissOverlayView();

    }



    /*public void serverCallEnds(int id, String url, Object json, AjaxStatus status) {
        if (id == loginServiceCall) {
            boolean success = NetworkResultValidator.getInstance().isResultOK(url, (String) json, status, null, layoutParent, this);
            List<Cookie> cookies = status.getCookies();
            if (success) {
                Gson gson = new Gson();
                com.goldadorn.main.model.LoginResult loginResult = gson.fromJson((String) json, com.goldadorn.main.model.LoginResult.class);

                if (loginResult.getSuccess()) {
                    User user = new User(Integer.valueOf(loginResult.getUserid()), User.TYPE_INDIVIDUAL);
                    user.setName(loginResult.getUsername());
                    Log.e("iiii",loginResult.getUserid()+"");
                    user.setImageUrl(loginResult.getUserpic());
                    getApp().setUser(user);

                    getApp().setCookies(cookies);
                    SharedPreferences sharedPreferences = getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, true)
                            .putString(AppSharedPreferences.LoginInfo.USER_NAME, userName.getText().toString())
                            .putInt(AppSharedPreferences.LoginInfo.USER_ID, Integer.valueOf(loginResult.getUserid()))
                            .putString(AppSharedPreferences.LoginInfo.PASSWORD, password.getText().toString()).commit();

                    gotoApp();
                } else {
                    final Snackbar snackbar = Snackbar.make(layoutParent, loginResult.getMsg(), Snackbar.LENGTH_SHORT);
                    ColoredSnackbar.alert(snackbar).show();
                }
                stopProgress(loginResult.getSuccess());
            } else {
                stopProgress(success);
            }
        } else
            super.serverCallEnds(id, url, json, status);
    }*/

    private AQuery getAQuery() {

        if (mActivity instanceof LoginPageActivity){
            return ((LoginPageActivity) mActivity).getAQueryCustom();
        }
        else if (mActivity instanceof LandingPageActivity){
            return ((LandingPageActivity) mActivity).getAQueryCustom();
        }
        else return null;
    }

    /*private void evaluateResults(String status, String message, Dialog dialog) {
        //it would have been better if I had a boolean status field for comparison;
        // since string comparison is not good practice
        if (status.equalsIgnoreCase(ApiKeys.RESPONSE_SUCCESS)){
            doSuccessOperation(dialog);
        }
        else if (status.equalsIgnoreCase(ApiKeys.RESPONSE_FAIL)){
            //genericInfo(message);
            setErrSnackBar(message);
        }
    }*/

    /*private void doSuccessOperation(final Dialog dialog) {
        //// TODO: 5/6/2016

        new Thread() {

            @Override
            public void run() {
                super.run();
                //genericInfo("Auth from server successful");
                SharedPreferences sharedPreferences = mActivity.getSharedPreferences(AppSharedPreferences.LoginInfo.NAME,
                        Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, true).commit();

                dismissOverlayView(dialog);
                mActivity.startActivity(new Intent(mActivity, TestPayment.class));
                mActivity.finish();
            }
        }.start();
    }*/


    private void displayOverlayDialog() {
        dialog = displayOverlay(null, R.color.colorAccent);
        dialog.show();
    }


    private void doSuccessOperation(final LoginResult loginResult, final List<Cookie> cookies) {
        //// TODO: 5/6/2016

        new Thread() {

            @Override
            public void run() {
                super.run();
                //genericInfo("Auth from server successful");

                User user = new User(Integer.valueOf(loginResult.getUserid()), User.TYPE_INDIVIDUAL);
                user.setName(loginResult.getUsername());
                Log.e("iiii", loginResult.getUserid() + "");
                user.setImageUrl(loginResult.getUserpic());
                ((Application) mActivity.getApplication()).setUser(user);

                ((Application) mActivity.getApplication()).setCookies(cookies);
                Application.getInstance().getPrefManager().setSocialLoginStat(true);
                /*SharedPreferences sharedPreferences = mAppContext.getSharedPreferences(AppSharedPreferences.LoginInfo.NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, true)
                        .putString(AppSharedPreferences.LoginInfo.USER_NAME, loginResult.getUsername().trim())
                        .putBoolean(AppSharedPreferences.LoginInfo.IS_SOCIAL_LOGIN, true)
                        .putInt(AppSharedPreferences.LoginInfo.USER_ID, Integer.valueOf(loginResult.getUserid())).commit();*/

                /*mActivity.startActivity(new Intent(mActivity, TestPayment.class));
                mActivity.finish();*/
                //new Action(mActivity).launchActivity(MainActivity.class, true);
                /*Intent intent = new Intent();
                mActivity.startActivity(intent);*/
                new Action(mActivity).launchActivity(MainActivity.class, true);
                Application.getInstance().getUIHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissOverlayView();
                        mActivity.finish();
                    }
                }, 500);
            }
        }.start();
    }


    public void indicateSignedOut(){
        Application.getInstance().getPrefManager().setSocialLoginStat(false);
    }


    private void dismissOverlayView() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    private void genericInfo(String info) {
        Toast.makeText(mAppContext, info, Toast.LENGTH_LONG).show();
    }


    private void processRevokeRequest() {

        /*if (mGoogleApiClient.isConnected()) {*/
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            //Log.d(Constants.TAG, "on revoke");
                            //Toast.makeText(getBaseContext(), "Sign-out, successful", Toast.LENGTH_SHORT).show();
                            mGoogleApiClient.connect();
                        }

                    });
       // }

    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(Constants.TAG, "onActivity result - Social Logins ActResultCallback ");
        Log.d(Constants.TAG, "onActivity result - Social Logins resultCode: " + resultCode);
        if (requestCode == GMAIL_RC_SIGN_IN && resultCode == Activity.RESULT_OK) {

            Log.d(Constants.TAG, "onActivity result GoogleSignIn - Result_Ok");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        mFbCallbackManager.onActivityResult(requestCode, resultCode, data);
        twitAuthClient.onActivityResult(requestCode, resultCode, data);
    }


    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully.
            isSignedIn = true;
            //onSuccessfulLogin(new FbGoogleTweetLoginResult(result, null, null, Constants.PLATFORM_GOOGLE));
            authFromServer(new FbGoogleTweetLoginResult(result, null, null, Constants.PLATFORM_GOOGLE), Constants.PLATFORM_GOOGLE);

            GoogleSignInAccount accountInfo = result.getSignInAccount();
            Toast.makeText(mAppContext, "Hi "
                    + accountInfo.getDisplayName() + ", Welcome to Gold Adorn\nAuthorizing...please wait", Toast.LENGTH_LONG).show();
        } else {
            // Signed out.
            isSignedIn = false;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(Constants.TAG, "on connection failed");
    }


    @Override
    public void onConnected(Bundle arg0) {
        Log.d(Constants.TAG, "on connected");
    }


    @Override
    public void onConnectionSuspended(int arg0) {

        Log.d(Constants.TAG, "on connection suspended");
        mGoogleApiClient.connect();
    }
}
