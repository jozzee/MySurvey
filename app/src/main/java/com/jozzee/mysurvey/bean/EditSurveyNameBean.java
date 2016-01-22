package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 11/11/2558.
 */
public class EditSurveyNameBean {
    int surveyID;
    int surveyVersion;
    String surveyName;
    String lastModifyDate;

    public EditSurveyNameBean() {
    }
    public EditSurveyNameBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID")!= null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
           this.surveyVersion = (jsonObject.get("surveyVersion").getAsInt());
        }
        if(jsonObject.get("surveyName")!= null){
            this.surveyName = jsonObject.get("surveyName").getAsString();
        }
        if(jsonObject.get("lastModifyDate") != null){
            this.lastModifyDate = jsonObject.get("lastModifyDate").getAsString();
        }
    }
    public EditSurveyNameBean(int surveyID, int surveyVersion, String surveyName, String lastModifyDate) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyName = surveyName;
        this.lastModifyDate = lastModifyDate;
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

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

}
