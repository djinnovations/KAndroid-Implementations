package com.goldadorn.main.dj.model;

public class UserProfile {

	private String uniqueUserIdByPlatform;
	private String platform;

    private String userName;
    private String email_id;
    private String gender;
    private String city;
    private String age;

	/*public UserProfile(String uniqueUserIdByPlatform, String userName, String email_id,
			String platform) {
		super();
		this.uniqueUserIdByPlatform = uniqueUserIdByPlatform;
		this.userName = userName;
		this.email_id = email_id;
		this.platform = platform;
	}*/

    public UserProfile(String uniqueUserIdByPlatform, String platform,
                       String userName, String email_id, String gender,
                       String city, String age) {
        this.uniqueUserIdByPlatform = uniqueUserIdByPlatform;
        this.platform = platform;
        this.userName = userName;
        this.email_id = email_id;
        this.gender = gender;
        this.city = city;
        this.age = age;
    }

    public String getUserIdForIbec() {
		return uniqueUserIdByPlatform;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail_id() {
		return email_id;
	}

	public String getPlatform() {
		return platform;
	}
	
	
	
	
}
