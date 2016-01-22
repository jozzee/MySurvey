package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 26/11/2558.
 */
public class RegisterBean {
    String email;
    String name;
    String password;
    String profileImage;
    String phoneNumber;
    int accountID;

    public RegisterBean() {
    }
    public RegisterBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("email")!= null){
            this.email = jsonObject.get("email").getAsString();
        }
        if(jsonObject.get("name")!= null){
            this.name = jsonObject.get("name").getAsString();
        }
        if(jsonObject.get("password")!= null){
            this.password = jsonObject.get("password").getAsString();
        }
        if(jsonObject.get("profileImage")!= null){
            this.profileImage = jsonObject.get("profileImage").getAsString();
        }
        if(jsonObject.get("phoneNumber")!= null){
            this.phoneNumber = jsonObject.get("phoneNumber").getAsString();
        }
        if(jsonObject.get("accountID") != null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
    }

    public RegisterBean(String email, String name, String password, String profileImage, String phoneNumber, int accountID) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.accountID = accountID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
