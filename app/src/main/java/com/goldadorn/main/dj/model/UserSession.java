package com.goldadorn.main.dj.model;


public class UserSession {

	
	private static UserSession mUserSession;
	
	private UserProfile currentlyLoggedInUser;
	
	private UserSession(){
		
	}
	
	
	public static UserSession getUserSession(){
		
		if(mUserSession == null){
			
			mUserSession = new UserSession();
		}
		
		return mUserSession;
	}
	
	
	public void setUserProfile(UserProfile loggedInUserProfile){
		
		currentlyLoggedInUser = loggedInUserProfile;
	}
	
	
	public UserProfile getCurrentUserProfile(){
		
		return currentlyLoggedInUser;
	}
}
