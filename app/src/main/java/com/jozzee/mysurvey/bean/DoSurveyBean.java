package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 14/11/2558.
 */
public class DoSurveyBean {
    int surveyID;
    int surveyVersion;
    String answerBy;
    int accountID; //1 is guest, >1 is member
    String answerDate;
    String answerDataAsJsonString;

    public DoSurveyBean() {
    }

    public DoSurveyBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("answerBy")!= null){
            this.answerBy = jsonObject.get("answerBy").getAsString();
        }
        if(jsonObject.get("accountID") != null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
        if(jsonObject.get("answerDate") != null){
           this.answerDate =  jsonObject.get("answerDate").getAsString();
        }
        if(jsonObject.get("answerDataAsJsonString") != null){
            this.answerDataAsJsonString = jsonObject.get("answerDataAsJsonString").getAsString();
        }
        if(jsonObject.get("surveyID")!= null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion")!= null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
    }
    public DoSurveyBean(String answerBy, int accountID, String answerDate, String answerDataAsJsonString, int surveyID, int surveyVersion) {
        this.answerBy = answerBy;
        this.accountID = accountID;
        this.answerDate = answerDate;
        this.answerDataAsJsonString = answerDataAsJsonString;
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
    }

    public String getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(String answerBy) {
        this.answerBy = answerBy;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public String getAnswerDataAsJsonString() {
        return answerDataAsJsonString;
    }

    public void setAnswerDataAsJsonString(String answerDataAsJsonString) {
        this.answerDataAsJsonString = answerDataAsJsonString;
    }
    public int getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(int surveyID) {
        this.surveyID = surveyID;
    }

    public int getSurveyVersion() {
        return surveyVersion;
    }

    public void setSurveyVersion(int surveyVersion) {
        this.surveyVersion = surveyVersion;
    }
}
