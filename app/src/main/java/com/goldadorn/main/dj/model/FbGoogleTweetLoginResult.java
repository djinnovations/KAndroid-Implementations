package com.goldadorn.main.dj.model;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class FbGoogleTweetLoginResult {

	private GoogleSignInResult mGoogleLoginResult;
	private LoginResult faceBookLoginResult;
	
	private String loginPlatform;
	
	
	public FbGoogleTweetLoginResult(GoogleSignInResult mGoogleLoginResult, LoginResult faceBookLoginResult, String loginPlatform){
		
		this.mGoogleLoginResult = mGoogleLoginResult;
		this.faceBookLoginResult = faceBookLoginResult;
		this.loginPlatform = loginPlatform;
	}


	public GoogleSignInResult getmGoogleLoginResult() {
		return mGoogleLoginResult;
	}


	public LoginResult getFaceBookLoginResult() {
		return faceBookLoginResult;
	}


	public String getLoginPlatform() {
		return loginPlatform;
	}
	
	
}
