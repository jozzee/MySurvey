package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 12/11/2558.
 */
public class ChangeStatusBean {
    int surveyID;
    int surveyVersion;
    int surveyStatus;

    public ChangeStatusBean() {
    }
    public ChangeStatusBean(String beanAsJsonString) {
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
    }
    public ChangeStatusBean(int surveyID, int surveyVersion, int surveyStatus) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyStatus = surveyStatus;
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
}
