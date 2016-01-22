package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by Jozzee on 27/10/2558.
 */
public class SaveSurveyStatusBean {
    int surveyID;
    int surveyVersion;
    int surveyStatus;
    String lastModifyDate;
    int numberOffQuestions;

    public SaveSurveyStatusBean() {
    }
    public SaveSurveyStatusBean(String saveSurveyStatusBeanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(saveSurveyStatusBeanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion")!= null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("surveyStatus")!= null){
            this.surveyStatus = jsonObject.get("surveyStatus").getAsInt();
        }
        if(jsonObject.get("lastModifyDate") != null){
            this.lastModifyDate = jsonObject.get("lastModifyDate").getAsString();
        }
        if(jsonObject.get("numberOffQuestions") != null){
            this.numberOffQuestions = jsonObject.get("numberOffQuestions").getAsInt();
        }
    }

    public SaveSurveyStatusBean(int surveyID, int surveyVersion, int surveyStatus) {
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

    public String getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public int getNumberOffQuestions() {
        return numberOffQuestions;
    }

    public void setNumberOffQuestions(int numberOffQuestions) {
        this.numberOffQuestions = numberOffQuestions;
    }
}
