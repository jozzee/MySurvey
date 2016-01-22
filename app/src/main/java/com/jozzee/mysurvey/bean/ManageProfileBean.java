package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 3/12/2558.
 */
public class ManageProfileBean {
    int action; // 0 is edit name, 1 is edit email, 2 is edit password
    int accountID;
    String name;
    String email;
    String password;

    public ManageProfileBean() {
    }
    public ManageProfileBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("action") != null){
            this.action = jsonObject.get("action").getAsInt();
        }
        if(jsonObject.get("accountID") != null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
        if(jsonObject.get("name") != null){
            this.name = jsonObject.get("name").getAsString();
        }
        if(jsonObject.get("email") != null){
            this.email = jsonObject.get("email").getAsString();
        }
        if(jsonObject.get("password") != null){
            this.password = jsonObject.get("password").getAsString();
        }
    }

    public ManageProfileBean(int action, int accountID, String name, String email, String password) {
        this.action = action;
        this.accountID = accountID;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

