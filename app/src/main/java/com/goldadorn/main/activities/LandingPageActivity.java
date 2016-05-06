package com.goldadorn.main.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.goldadorn.main.R;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.support.SocialLoginUtil;
import com.goldadorn.main.dj.utils.ConnectionDetector;
import com.goldadorn.main.dj.utils.Constants;
import com.goldadorn.main.utils.TypefaceHelper;
import com.goldadorn.main.views.ColoredSnackbar;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kimeeo.library.actions.Action;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LandingPageActivity extends BaseActivity /*implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/ {

    @Bind(R.id.createAccount)
    Button createAccount;

    @Bind(R.id.loginAccount)
    public Button loginAccount;

    @Bind(R.id.loginWithFacebookButton)
    Button loginWithFacebookButton;

    @Bind(R.id.loginWithGoogleButton)
    Button loginWithGoogleButton;

    @Bind(R.id.loginWithTwitterButton)
    Button loginWithTwitterButton;

    @Bind(R.id.orLabel)
    TextView orLabel;

    /*****************
     * Facebook stuffs
     ***********************/
    private CallbackManager mFbCallbackManager;
    private final String[] permissionArr = new String[]{"user_location", "user_birthday", "email"};
    /*******************************************************/
    private UserSession mUserSession;
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

    private SocialLoginUtil mSocialLoginInstance;

    @OnClick(R.id.createAccount)
    void onClickCreateAccount() {
        new Action(this).launchActivity(RegisterUserActivity.class, true);
    }

    @OnClick(R.id.loginAccount)
    void onClickLogin() {
        new Action(this).launchActivity(LoginPageActivity.class, true);
    }

    @OnClick(R.id.loginWithFacebookButton)
    void onClickLoginWithFacebookButton() {
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(permissionArr));
        if (checkNetwork()){
            mSocialLoginInstance.onFacebookLogin(this);
        }
    }

    @OnClick(R.id.loginWithGoogleButton)
    void onClickLoginWithGoogleButton() {
        //googleLoginClicked();
        if (checkNetwork()){
            mSocialLoginInstance.onGoogleLogin(this);
        }
    }

    @OnClick(R.id.loginWithTwitterButton)
    void onClickLoginWithTwitterButton() {

        if (checkNetwork()){
            mSocialLoginInstance.onTwitterLogin(this);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize fb sdk before performing any other operation.
        //FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.activity_app_landing_page);
        ButterKnife.bind(this);
        TypefaceHelper.setFont(loginAccount, createAccount, orLabel);

        /*initializeFbAndGoogle();
        setFbCallBacks();*/
        mSocialLoginInstance = SocialLoginUtil.getInstance(getApplicationContext());

    }


    private boolean checkNetwork(){

        if (ConnectionDetector.getInstance(getApplicationContext()).isNetworkAvailable()){
            return true;
        }
        else {
            final Snackbar snackbar = Snackbar.make(loginAccount, Constants.ERR_MSG_NETWORK, Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
            return false;
        }
    }

    /*private void initializeFbAndGoogle() {

        *//*************************************Facebook stuffs********************************************//*
        //FacebookSdk.sdkInitialize(this);
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.

        mFbCallbackManager = CallbackManager.Factory.create();
        *//********************************************************************************************//*

        *//**************************************Gmail stuffs**********************************************//*

        mGoogleSignInOpt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Plus.SCOPE_PLUS_LOGIN, new Scope("email"))
                .build();

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOpt)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        *//*************************************************************************************************//*
        mUserSession = UserSession.getUserSession();
    }*/


    /*private void setFbCallBacks() {

        LoginManager.getInstance().registerCallback(mFbCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(Constants.TAG, "Login successs");
                        onSuccessfulLogin(new FbGoogleTweetLoginResult(null, loginResult, Constants.PLATFORM_FACEBOOK));
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LandingPageActivity.this, "Login Cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //probably no network connection at the moment (most of the times)
                        Toast.makeText(LandingPageActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }*/

    /*private void setResultListenerFb(LoginResult loginResult) {

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v(Constants.TAG, "fb loginResult response: "+response.toString());

                        try {
                            String city = "N/A";
                            String birthday = "N/A";
                            String emailId = "N/A";
                            String gender = "N/A";
                            String uniqueId = "N/A";
                            String name = "N/A";

                            uniqueId = object.getString("id");
                            name = object.getString("name");
                            if (!object.isNull("email")){
                                emailId = object.getString("email");
                            }
                            if (!object.isNull("gender")){
                                gender = object.getString("gender");
                            }
                            if (!object.isNull("location")){
                                city = object.getJSONObject("location").getString("name");
                            }
                            if (!object.isNull("birthday")){
                                birthday = object.getString("birthday");
                            }

                            *//*UserProfile mUserProfile = new UserProfile(uniqueId, Constants.PLATFORM_GOOGLE,
                                    name, emailId, gender, city, "10");
                            mUserSession.setUserProfile(mUserProfile);*//*

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

    private void googleLoginClicked() {

        if (isSignedIn) {
            if (mGoogleApiClient.isConnected()) {
                //googleLogout();
                //// TODO: 5/4/2016
            }
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, GMAIL_RC_SIGN_IN);
        }
    }


    private void googleLogout() {

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


    private void processRevokeRequest() {

        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            //Log.d("dj", "on revoke");
                            //Toast.makeText(getBaseContext(), "Sign-out, successful", Toast.LENGTH_SHORT).show();
                            mGoogleApiClient.connect();
                        }

                    });
        }

    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == GMAIL_RC_SIGN_IN && resultCode == RESULT_OK) {

            Log.d(Constants.TAG, "onActivity result - Result_Ok");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);/*

        }

        mFbCallbackManager.onActivityResult(requestCode, resultCode, data);*/
        mSocialLoginInstance.handleActivityResult(requestCode, resultCode, data);
    }


    /*private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully.
            isSignedIn = true;
            //setGooglePlusButtonText(getResources().getString(R.string.google_signout_text));
            onSuccessfulLogin(new FbGoogleTweetLoginResult(result, null, Constants.PLATFORM_GOOGLE));

            GoogleSignInAccount accountInfo = result.getSignInAccount();
            Toast.makeText(getBaseContext(), "Your logged in as: "
                    + accountInfo.getDisplayName(), Toast.LENGTH_LONG).show();
        } else {
            // Signed out.
            isSignedIn = false;
        }
    }*/


    /*private void onSuccessfulLogin(FbGoogleTweetLoginResult loginResults) {
        // TODO Auto-generated method stub
        if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_GOOGLE)) {

            GoogleSignInResult mGoogleResult = loginResults.getmGoogleLoginResult();
            GoogleSignInAccount mGoogleProfile = mGoogleResult.getSignInAccount();

            UserProfile mUserProfie = getGplusProfile( mGoogleProfile.getId(),mGoogleProfile.getEmail());
                    *//*new UserProfile(mGoogleProfile.getId(),  Constants.PLATFORM_GOOGLE,
                    mGoogleProfile.getDisplayName(), mGoogleProfile.getEmail(),
                    mGoogleProfile.,);*//*
            mUserSession.setUserProfile(mUserProfie);
        } else if (loginResults.getLoginPlatform().equals(Constants.PLATFORM_FACEBOOK)) {

            *//** Unable to fetch email-id, user-name through facebook login **//*
            LoginResult mFbLoginResult = loginResults.getFaceBookLoginResult();

            //setResultListenerFb(mFbLoginResult);

            authWithServer(mFbLoginResult);
            *//*
            Log.d(Constants.TAG, "userName: " + user_name);
            Log.d(Constants.TAG, "userId: " + user_id_fb);
            Log.d(Constants.TAG, "email id: " + email_id);

            UserProfile mUserProfie = new UserProfile(user_id_fb, user_name,
                    email_id, Ibek_AppConstants.LOGIN_PLATFORM_FACEBOOK);
            mUserSession.setUserProfile(mUserProfie);

            *//**//************Dont use facebook login******************************************************//*

        }

        //Intent loggedInActivityIntent = new Intent(this, LoggedInScreenActivity.class);
        //startActivity(loggedInActivityIntent);

    }

    private void authWithServer(LoginResult mFbLoginResult) {
        GraphRequest req = new GraphRequest();
        String version = req.getVersion();
        RequestJson reqJson = RequestJson.getInstance();

    }

    public UserProfile getGplusProfile(String uniqueId, String emailId) {

        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String city;
        String gender = null;
        String age = null;
        String name = person.getDisplayName();
        if (person.hasGender()) {
            int type = person.getGender();
            if (type == Person.Gender.MALE)
                gender = "Male";
            else if (type == Person.Gender.FEMALE)
                gender = "Female";
            else if (type == Person.Gender.OTHER)
                gender = "Other";
        }

        if (person.hasBirthday()) {
            String bdayDate = person.getBirthday();
            age = String.valueOf(getAgeFromBdate(bdayDate));
        }

        city = person.getCurrentLocation();

        Log.i(Constants.TAG, "--------------------------------");
        Log.i(Constants.TAG, "Display Name: " + name);
        Log.i(Constants.TAG, "Gender: " + gender);
        Log.i(Constants.TAG, "City: " + city);
        Log.i(Constants.TAG, "age: " + age);
        UserProfile mUserProfile = new UserProfile(uniqueId, Constants.PLATFORM_GOOGLE,
                name, emailId, gender, city, age);
        return mUserProfile;
    }


    private int getAgeFromBdate(String bday) {

        String[] dateArr = bday.split("-");
        LocalDate birthdate = new LocalDate(Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[2]),
                Integer.parseInt(dateArr[2]));
        LocalDate now = new LocalDate();
        Years age = Years.yearsBetween(birthdate, now);
        return age.getYears();
    }*/



    @Override
    public void onStart() {
        super.onStart();
        // make sure to initiate connection
        //mGoogleApiClient.connect();
        mSocialLoginInstance.onActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // disconnect api if it is connected
        /*if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();*/
        mSocialLoginInstance.onActivityStop();
    }


    /*@Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(Constants.TAG, "on connection failed");
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
            Log.d(Constants.TAG, "on connection failed - !mIntentInProgress");
            if (!isSignedIn) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                Log.d(Constants.TAG, "on connection failed - singinflag");
                resolveSignInError();
            }
        }
    }



    private void resolveSignInError() {

        Log.d(Constants.TAG, "on resolve signin error");
        mGoogleApiClient.connect();
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                Log.d(Constants.TAG, "on resolve signin error - has resolution");
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, GMAIL_RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }



    @Override
    public void onConnected(Bundle arg0) {
        Log.d(Constants.TAG, "on connected");
    }



    @Override
    public void onConnectionSuspended(int arg0) {

        Log.d(Constants.TAG, "on connection suspended");
        mGoogleApiClient.connect();
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mGoogleApiClient.isConnected()) {
            googleLogout();
        }*/
        //clearPermissionByFb();
        mSocialLoginInstance.performGoogleLogout();
        //mSocialLoginInstance.clearFbPermission();
    }


    /*private void clearPermissionByFb(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/permissions",
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            *//* handle the result *//*
                        Log.d(Constants.TAG, "on clear permission fb response: "+response.toString());
                    }
                }
        ).executeAsync();
    }*/
}
