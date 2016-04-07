package com.goldadorn.main.model;

import java.io.File;

/**
 * Created by Kiran BH on 06/04/16.
 */
public class ProfileData {
    public static final int GENDER_FEMALE = 1;
    public static final int GENDER_MALE = 2;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String address1;
    public String address2;
    public String country;
    public String state;
    public String city;
    public String pincode;
    public long dob;
    public int genderType;
    public String imageUrl;
    public File imageToUpload;

    public String getGenderDisplayText() {
        if (genderType == GENDER_FEMALE) {
            return "FEMALE";
        } else return "Male";
    }
}
