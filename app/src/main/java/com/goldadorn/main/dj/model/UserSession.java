package com.goldadorn.main.dj.model;


import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;

public class UserSession {

	
	private static UserSession mUserSession;
	
	//private UserProfile currentlyLoggedInUser;

	private SocialFeedFragment socialFeedFragment;
	private UserSession(){
		
	}
	
	
	public static UserSession getInstance(){
		if(mUserSession == null){
			mUserSession = new UserSession();
		}
		return mUserSession;
	}

    public SocialFeedFragment getSocialFeedFragment() {
        return socialFeedFragment;
    }

    public void setSocialFeedFragment(SocialFeedFragment socialFeedFragment) {
        this.socialFeedFragment = socialFeedFragment;
    }

    boolean isBonbRefreshPending = false;
    public void setIsBonbRefreshPending(boolean isPendingRefreshDone){
        isBonbRefreshPending = isPendingRefreshDone;
    }

    public boolean getIsBonbRefreshPending(){
        return isBonbRefreshPending;
    }

    /*public void setUserProfile(UserProfile loggedInUserProfile){
		
		currentlyLoggedInUser = loggedInUserProfile;
	}
	
	
	public UserProfile getCurrentUserProfile(){
		
		return currentlyLoggedInUser;
	}*/



}
