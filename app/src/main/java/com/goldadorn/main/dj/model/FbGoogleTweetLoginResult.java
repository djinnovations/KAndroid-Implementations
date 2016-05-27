package com.goldadorn.main.dj.model;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

public class FbGoogleTweetLoginResult {

	private GoogleSignInResult mGoogleLoginResult;
	private LoginResult faceBookLoginResult;
	private Result<TwitterSession> twitterLoginResult;
	
	private String loginPlatform;

	public FbGoogleTweetLoginResult(GoogleSignInResult mGoogleLoginResult, LoginResult faceBookLoginResult,
									Result<TwitterSession> twitterLoginResult, String loginPlatform) {
		this.mGoogleLoginResult = mGoogleLoginResult;
		this.faceBookLoginResult = faceBookLoginResult;
		this.twitterLoginResult = twitterLoginResult;
		this.loginPlatform = loginPlatform;
	}

	/*public FbGoogleTweetLoginResult(GoogleSignInResult mGoogleLoginResult, LoginResult faceBookLoginResult,
									String loginPlatform){
		
		this.mGoogleLoginResult = mGoogleLoginResult;
		this.faceBookLoginResult = faceBookLoginResult;
		this.loginPlatform = loginPlatform;
	}*/


	public GoogleSignInResult getmGoogleLoginResult() {
		return mGoogleLoginResult;
	}

	public LoginResult getFaceBookLoginResult() {
		return faceBookLoginResult;
	}

	public Result<TwitterSession> getTwitterLoginResult() {
		return twitterLoginResult;
	}

	public String getLoginPlatform() {
		return loginPlatform;
	}
	
	
}
