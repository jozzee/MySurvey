package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 23/11/2558.
 */
public class RequestViewAnswerBean {
    int accountID;
    String answerBy;
    String answerDate;

    public RequestViewAnswerBean() {
    }
    public RequestViewAnswerBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("accountID") != null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
        if(jsonObject.get("answerBy") != null){
            this.answerBy = jsonObject.get("answerBy").getAsString();
        }
        if(jsonObject.get("answerDate") != null){
            this.answerDate = jsonObject.get("answerDate").getAsString();
        }
    }

    public RequestViewAnswerBean(int accountID, String answerBy, String answerDate) {
        this.accountID = accountID;
        this.answerBy = answerBy;
        this.answerDate = answerDate;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(String answerBy) {
        this.answerBy = answerBy;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }



}
