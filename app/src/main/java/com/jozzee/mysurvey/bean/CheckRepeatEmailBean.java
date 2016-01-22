package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;

/**
 * Created by Jozzee on 24/9/2558.
 */
public class CheckRepeatEmailBean {
    private String email;

    public CheckRepeatEmailBean() {
    }
    public CheckRepeatEmailBean(String email) {
        this.email = email;
    }
    public String serializationJson(CheckRepeatEmailBean bean){
        Gson gson = new Gson();
        return gson.toJson(bean);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
