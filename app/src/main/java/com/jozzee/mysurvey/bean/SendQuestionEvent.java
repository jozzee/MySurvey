package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 30/10/2558.
 */
public class SendQuestionEvent {
    String action;
    String data;

    public SendQuestionEvent() {
    }
    public SendQuestionEvent(String objectAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(objectAsJsonString,JsonObject.class);
        this.action = jsonObject.get("action").getAsString();
        this.data = jsonObject.get("data").getAsString();
    }

    public SendQuestionEvent(String action, String data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
