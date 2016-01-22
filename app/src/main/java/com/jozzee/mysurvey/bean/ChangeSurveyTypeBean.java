package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 12/11/2558.
 */
public class ChangeSurveyTypeBean {
    int surveyID;
    int surveyVersion;
    int surveyType;
    String password;

    public ChangeSurveyTypeBean() {
    }

    public ChangeSurveyTypeBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID")!= null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion")!= null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("surveyType")!=null){
            this.surveyType = jsonObject.get("surveyType").getAsInt();
        }
        if(jsonObject.get("password") != null){
            this.password = jsonObject.get("password").getAsString();
        }
    }
    public ChangeSurveyTypeBean(int surveyID, int surveyVersion, int surveyType) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyType = surveyType;
    }

    public ChangeSurveyTypeBean(int surveyID, int surveyVersion, int surveyType, String password) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyType = surveyType;
        this.password = password;
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

    public int getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(int surveyType) {
        this.surveyType = surveyType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
