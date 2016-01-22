package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 18/11/2558.
 */
public class ResponsesBean {
    String answerBy;
    int accountID;
    String Status;
    String responsesDate;

    public ResponsesBean() {
    }

    public ResponsesBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("answerBy") != null){
            this.answerBy = jsonObject.get("answerBy").getAsString();
        }
        if(jsonObject.get("Status") != null){
            this.Status = jsonObject.get("Status").getAsString();
        }
        if(jsonObject.get("responsesDate") != null){
            this.responsesDate = jsonObject.get("responsesDate").getAsString();
        }
        if(jsonObject.get("accountID") != null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
    }

    public ResponsesBean(String answerBy, int accountID, String status, String responsesDate) {
        this.answerBy = answerBy;
        this.Status = status;
        this.responsesDate = responsesDate;
        this.accountID = accountID;
    }

    public String getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(String answerBy) {
        this.answerBy = answerBy;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getResponsesDate() {
        return responsesDate;
    }

    public void setResponsesDate(String responsesDate) {
        this.responsesDate = responsesDate;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }


}
