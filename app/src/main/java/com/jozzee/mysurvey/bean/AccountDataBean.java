package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 11/10/2558.
 */
public class AccountDataBean {
    private int accountID;
    private String name;
    private String email;
    private String profileImage;
    private String password;

    public AccountDataBean() {
        // TODO Auto-generated constructor stub
    }
    public AccountDataBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("accountID") != null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
        if(jsonObject.get("name") != null){
            this.name = jsonObject.get("name").getAsString();
        }
        if(jsonObject.get("email") != null){
            this.email = jsonObject.get("email").getAsString();
        }
        if(jsonObject.get("profileImage") != null){
            this.profileImage = jsonObject.get("profileImage").getAsString();
        }
        if(jsonObject.get("password") != null){
            this.password = jsonObject.get("password").getAsString();
        }

    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
