package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 18/11/2558.
 */
public class RefreshResponsesBean {
    int surveyID;
    int surveyVersion;
    String responsesDate;

    public RefreshResponsesBean() {
    }

    public RefreshResponsesBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("responsesDate") != null){
            this.responsesDate = jsonObject.get("responsesDate").getAsString();
        }
    }
    public RefreshResponsesBean(int surveyID, int surveyVersion, String responsesDate) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.responsesDate = responsesDate;
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

    public String getResponsesDate() {
        return responsesDate;
    }

    public void setResponsesDate(String responsesDate) {
        this.responsesDate = responsesDate;
    }


}
