package com.goldadorn.main.dj.model;


import com.goldadorn.main.modules.socialFeeds.SocialFeedFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class UserSession {

	
	private static UserSession mUserSession;
	
	//private UserProfile currentlyLoggedInUser;

	private SocialFeedFragment socialFeedFragment;
    private String fcmToken;

	private UserSession(){
		
	}
	
	public static UserSession getInstance(){
		if(mUserSession == null){
			mUserSession = new UserSession();
		}
		return mUserSession;
	}


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
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

    private String goldCoinCount = "0";
    private String diamondCnt = "0";
    private List<TitlesData> voucherData = new ArrayList<>();

    private boolean isVoucherDataAvail = false;

    public boolean isVoucherDataAvail() {
        return isVoucherDataAvail;
    }

    public void setVoucherDataAvail(boolean voucherDataAvail) {
        isVoucherDataAvail = voucherDataAvail;
    }

    public void setVoucherData(JSONArray jsonArray){
        //List<TitlesData> datas = new ArrayList<>();
        voucherData.clear();
        setVoucherDataAvail(true);
        for (int i = 0; i< jsonArray.length() ; i++){
            try {
                String string = jsonArray.getString(i);
                String[] split = string.split("-\\$\\$-");
                TitlesData data = new TitlesData(split[0], split[1]);
                voucherData.add(data);
            } catch (Exception e) {
                e.printStackTrace();
                setVoucherDataAvail(false);
            }
        }
    }

    public void setCounts(int coincnt, int diamondcnt){
        goldCoinCount = String.valueOf(coincnt);
        diamondCnt = String.valueOf(diamondcnt);
    }

    public List<TitlesData> getVoucherData(){
        return voucherData;
    }

    private boolean isAvailVoucherPresent;
    private List<VoucherData> availVoucher = new ArrayList<>();
    public List<VoucherData> getAvailVoucher(){
        return availVoucher;
    }

    public boolean isAvailVoucherPresent() {
        return isAvailVoucherPresent;
    }

    public void setAvailVoucherPresent(boolean availVoucherPresent) {
        isAvailVoucherPresent = availVoucherPresent;
    }

    public void setAvailVoucher(JSONArray jsonArray){
        availVoucher.clear();
        setAvailVoucherPresent(true);
        for (int i = 0; i< jsonArray.length(); i++){
            try {
                String string = jsonArray.getString(i);
                String[] stringArr = string.split(":");
                VoucherData voucherData = new VoucherData(stringArr[0], stringArr[1], stringArr[2]);
                availVoucher.add(voucherData);
            } catch (Exception e) {
                e.printStackTrace();
                setAvailVoucherPresent(false);
            }
        }
    }

    public String getGoldCoinCount(){
        return goldCoinCount;
    }

    public String getDiamondCnt(){
        return diamondCnt;
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
