package com.goldadorn.main.dj.support;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.LandingPageActivity;
import com.goldadorn.main.activities.LoginPageActivity;
import com.goldadorn.main.activities.MainActivity;
import com.goldadorn.main.dj.model.FbGoogleTweetLoginResult;
import com.goldadorn.main.dj.server.ApiKeys;
import com.goldadorn.main.dj.server.RequestJson;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.dj.utils.ResourceReader;
import com.goldadorn.main.sharedPreferences.AppSharedPreferences;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

/**
 * Created by COMP on 5/6/2016.
 */
public class SocialLoginUtil implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static SocialLoginUtil ourInstance;
    private static Context mContext;

    public static SocialLoginUtil getInstance(Context appContext) {

        if (ourInstance == null) {
            mContext = appContext;
            ourInstance = new SocialLoginUtil();
        }
        return ourInstance;
    }

    private SocialLoginUtil() {

        //FacebookSdk.sdkInitialize(mContext);
        initializeFbAndGlTw();
        setFbCallBacks();
    }


    private Activity mActivity;
    /*****************
     * Facebook stuffs
     ***********************/
    private CallbackManager mFbCallbackManager;
    private final String[] permissionArr = new String[]{"user_location", "user_birthday", "email"};
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


    private void initializeFbAndGlTw() {

        /*TwitterAuthConfig authConfig =
                new TwitterAuthConfig("consumerKey",
                        "consumerSecret");*/
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(Constants.API_KEY_TW,
                        Constants.API_SECRET_TW);

        Fabric.with(mContext, new Twitter(authConfig));
        twitCore = Twitter.getInstance().core;
        twitAuthClient = new TwitterAuthClient();
        /*************************************Facebook stuffs********************************************/
        //FacebookSdk.sdkInitialize(this);
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.

        mFbCallbackManager = CallbackManager.Factory.create();
        /********************************************************************************************/

        /**************************************Gmail stuffs**********************************************/
        //// TODO: 5/6/2016  
        mGoogleSignInOpt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Plus.SCOPE_PLUS_LOGIN, new Scope("email"))
                .build();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOpt)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        /*************************************************************************************************/
        //mUserSession = UserSession.getUserSession();
    }


    public void onGoogleLogin(Activity mActivity) {

        if (isSignedIn) {
            if (mGoogleApiClient.isConnected()) {
                //googleLogout();
                //// TODO: 5/4/2016
            }
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mActivity.startActivityForResult(signInIntent, GMAIL_RC_SIGN_IN);
        }
    }


    public void performGoogleLogout() {

        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // // TODO: 5/4/2016
                            processRevokeRequest();
                            isSignedIn = false;
                        }
                    });
        }
    }


    public boolean isGoogleConnected() {
        return mGoogleApiClient.isConnected();
    }


    public void onFacebookLogin(Activity mActivity) {
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList(permissionArr));
    }


    public void performFbLogout(){
        //// TODO: 5/6/2016
    }
    
    public void clearFbPermission(){
        try {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/permissions",
                    null,
                    HttpMethod.DELETE,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                /* handle the result */
                            Log.d(Constants.TAG, "on clear permission fb response: "+response.toString());
                        }
                    }
            ).executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Callback<TwitterSession> mCallBackTwit = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            onSuccessfulLogin(new FbGoogleTweetLoginResult(null, null, result, Constants.PLATFORM_TWITTER));
        }

        @Override
        public void failure(TwitterException exception) {
            exception.printStackTrace();
            setErrSnackBar(Constants.ERR_MSG_NETWORK);
        }
    };


    public void onTwitterLogin(Activity mActivity){
        twitAuthClient.authorize(mActivity, mCallBackTwit);
    }

    public void performTwitterLogout(){
        twitCore.logOut();
    }

    public void onActivityStart(Activity mActivity) {
        this.mActivity = mActivity;
        mGoogleApiClient.connect();
    }


    public void onActivityStop() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    private Dialog displayOverlay(String infoMsg, int colorResId){

        Dialog dialog = new Dialog(mActivity);
        WindowManager.LayoutParams tempParams = new WindowManager.LayoutParams();
        tempParams.copyFrom(dialog.getWindow().getAttributes());

		/*tempParams.width = dialogWidthInPx;
        tempParams.height = dialogHeightInPx;*/
        tempParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tempParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        tempParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tempParams.dimAmount = 0.0f;

        View overLay = LayoutInflater.from(mContext).inflate(R.layout.dialog_overlay, null);
        TextView tvTemp = (TextView) overLay.findViewById(R.id.tvOverlayInfo);
        if (infoMsg != null) {
            tvTemp.setText(infoMsg);
            tvTemp.setTextColor(ResourceReader.getInstance(mContext).getColorFromResource(colorResId));
        }
        else tvTemp.setVisibility(View.GONE);
        dialog.setContentView(overLay);
        dialog.setCancelable(false);

        dialog.getWindow().setAttributes(tempParams);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }




    private void setFbCallBacks() {

        LoginManager.getInstance().registerCallback(mFbCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(Constants.TAG, "Login successs");
                        //// TODO: 5/4/2016
                        onSuccessfulLogin(new FbGoogleTweetLoginResult(null, loginResult, null, Constants.PLATFORM_FACEBOOK));
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(mContext, "Login Cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //probably no network connection at the moment (most of the times)
                        //Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
                        setErrSnackBar(Constants.ERR_MSG_NETWORK);
                    }
                });
    }


    private void setErrSnackBar(String errMsg){

        View viewForSnackBar = null;
        if (mActivity instanceof LandingPageActivity){
            viewForSnackBar = ((LandingPageActivity) mActivity).loginAccount;
        }else if (mActivity instanceof LoginPageActivity){
            viewForSnackBar = ((LoginPageActivity) mActivity).layoutParent;
        }
        if (viewForSnackBar != null){
            final Snackbar snackbar = Snackbar.make(viewForSnackBar, errMsg,
                    Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }


    private void onSuccessfulLogin(FbGoogleTweetLoginResult loginResults) {
        // TODO Auto-generated method stub
        if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_GOOGLE)) {

            GoogleSignInResult mGoogleResult = loginResults.getmGoogleLoginResult();
            GoogleSignInAccount mGoogleProfile = mGoogleResult.getSignInAccount();

            /*UserProfile mUserProfie = getGplusProfile( mGoogleProfile.getId(),mGoogleProfile.getEmail());
                    new UserProfile(mGoogleProfile.getId(),  Constants.PLATFORM_GOOGLE,
                    mGoogleProfile.getDisplayName(), mGoogleProfile.getEmail(),
                    mGoogleProfile.,);
            mUserSession.setUserProfile(mUserProfie);*/
        } else if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_FACEBOOK)) {

            /** Unable to fetch email-id, user-name through facebook login **/
            LoginResult mFbLoginResult = loginResults.getFaceBookLoginResult();

            //setResultListenerFb(mFbLoginResult);

            authWithServer(mFbLoginResult, true);
            /*
            Log.d(Constants.TAG, "userName: " + user_name);
            Log.d(Constants.TAG, "userId: " + user_id_fb);
            Log.d(Constants.TAG, "email id: " + email_id);

            UserProfile mUserProfie = new UserProfile(user_id_fb, user_name,
                    email_id, Ibek_AppConstants.LOGIN_PLATFORM_FACEBOOK);
            mUserSession.setUserProfile(mUserProfie);

            *//************Dont use facebook login******************************************************/

        }else if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_TWITTER)){

            String token = loginResults.getTwitterLoginResult().data.getAuthToken().token;
            String ver = twitCore.getVersion();
            Log.d(Constants.TAG, "token - onSuccessfulLogin: "+token);
            Log.d(Constants.TAG, "version - onSuccessfulLogin: "+ver);
        }

        //Intent loggedInActivityIntent = new Intent(this, LoggedInScreenActivity.class);
        //startActivity(loggedInActivityIntent);

    }


    private void authWithServer(LoginResult mFbLoginResult, boolean isFb) {

        final Dialog dialog = displayOverlay(null, R.color.colorAccent);
        dialog.show();
        GraphRequest req = new GraphRequest();
        String version = req.getVersion();

        RequestJson reqJsonInstance = RequestJson.getInstance();
        JSONObject json = null;
        if (isFb){
            json = reqJsonInstance.getFbLoginReqJson(mFbLoginResult.getAccessToken().getToken(), version);
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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(loginRequest);

    }

    private void evaluateResults(String status, String message, Dialog dialog) {
        //it would have been better if I had a boolean status field for comparison;
        // since string comparison is not good practice
        if (status.equalsIgnoreCase(ApiKeys.RESPONSE_SUCCESS)){
            doSuccessOperation(dialog);
        }
        else if (status.equalsIgnoreCase(ApiKeys.RESPONSE_FAIL)){
            //genericInfo(message);
            setErrSnackBar(message);
        }
    }

    private void doSuccessOperation(final Dialog dialog) {
        //// TODO: 5/6/2016
        genericInfo("Auth from server successful");

        new Thread() {

            @Override
            public void run() {
                super.run();
                //genericInfo("Auth from server successful");
                SharedPreferences sharedPreferences = mActivity.getSharedPreferences(AppSharedPreferences.LoginInfo.NAME,
                        Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(AppSharedPreferences.LoginInfo.IS_LOGIN_DONE, true).commit();

                dismissOverlayView(dialog);
                mContext.startActivity(new Intent(mActivity, MainActivity.class));
            }
        };
    }

    private void dismissOverlayView(final Dialog dialog) {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    private void genericInfo(String info) {
        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
    }


    private void processRevokeRequest() {

        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            //Log.d(Constants.TAG, "on revoke");
                            //Toast.makeText(getBaseContext(), "Sign-out, successful", Toast.LENGTH_SHORT).show();
                            mGoogleApiClient.connect();
                        }

                    });
        }

    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(Constants.TAG, "onActivity result - Social Logins ActResultCallback ");
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
            onSuccessfulLogin(new FbGoogleTweetLoginResult(result, null, null, Constants.PLATFORM_GOOGLE));

            GoogleSignInAccount accountInfo = result.getSignInAccount();
            Toast.makeText(mContext, "Your logged in as: "
                    + accountInfo.getDisplayName(), Toast.LENGTH_LONG).show();
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
