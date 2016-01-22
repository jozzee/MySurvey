package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;

/**
 * Created by Jozzee on 9/10/2558.
 */
public class LoginBean {
    private String email;
    private String password;

    public LoginBean() {
    }
    public LoginBean(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String serializationJson(LoginBean bean){
        Gson gson = new Gson();
        return gson.toJson(bean);
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
