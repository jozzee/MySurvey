package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 11/11/2558.
 */
public class SaveSomethingBean {
    int surveyID;
    int surveyVersion;
    int surveyStatus;
    int numberOffQuestions;
    String lastUpdate;

    public SaveSomethingBean() {
    }
    public SaveSomethingBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("surveyStatus") != null){
            this.surveyStatus = jsonObject.get("surveyStatus").getAsInt();
        }
        if(jsonObject.get("numberOffQuestions") != null){
            this.numberOffQuestions = jsonObject.get("numberOffQuestions").getAsInt();
        }
        if(jsonObject.get("lastUpdate") != null){
            this.lastUpdate = jsonObject.get("lastUpdate").getAsString();
        }
    }

    public SaveSomethingBean(int surveyID, int surveyVersion, int surveyStatus, int numberOffQuestions, String lastUpdate) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyStatus = surveyStatus;
        this.numberOffQuestions = numberOffQuestions;
        this.lastUpdate = lastUpdate;
    }
    public SaveSomethingBean(int surveyID, int surveyVersion, int surveyStatus) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyStatus = surveyStatus;
        this.numberOffQuestions = numberOffQuestions;
        this.lastUpdate = lastUpdate;
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

    public int getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public int getNumberOffQuestions() {
        return numberOffQuestions;
    }

    public void setNumberOffQuestions(int numberOffQuestions) {
        this.numberOffQuestions = numberOffQuestions;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


}
