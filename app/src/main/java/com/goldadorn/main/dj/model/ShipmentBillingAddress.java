package com.goldadorn.main.dj.model;

import com.goldadorn.main.model.Address;

/**
 * Created by User on 13-06-2016.
 */
public class ShipmentBillingAddress {

    private String fname;
    private String lname;
    private String phone;
    private String address1;
    private String address2;
    private String country;
    private String state;
    private String city;
    private String pincode;
    private String type;

    public ShipmentBillingAddress(String fname, String lname, String phone, String address1,
                                  String address2, String country, String state, String city,
                                  String pincode, String type) {
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.address1 = address1;
        this.address2 = address2;
        this.country = country;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
        this.type = type;
    }


    public Address getAddressDataObj(){
        return new Address(fname, address1, city, state, country, phone, pincode);
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getPincode() {
        return pincode;
    }

    public String getType() {
        return type;
    }


    @Override
    public String toString() {
        return "ShipmentBillingAddress{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", pincode='" + pincode + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
