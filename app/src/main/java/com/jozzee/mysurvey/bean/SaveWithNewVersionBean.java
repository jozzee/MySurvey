package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 10/11/2558.
 */
public class SaveWithNewVersionBean {
    int surveyID;
    int surveyVersion;
    int surveyStatus;
    String questionListAsJsonString;

    public SaveWithNewVersionBean() {
    }
    public SaveWithNewVersionBean(String objectAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(objectAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("surveyStatus") != null){
            this.surveyStatus = jsonObject.get("surveyStatus").getAsInt();
        }
        if(jsonObject.get("questionListAsJsonString") != null){
            this.questionListAsJsonString = jsonObject.get("questionListAsJsonString").getAsString();
        }
    }

    public SaveWithNewVersionBean(int surveyID, int surveyVersion, int surveyStatus, String questionListAsJsonString) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyStatus = surveyStatus;
        this.questionListAsJsonString = questionListAsJsonString;
    }

    public int getSurveyID3() {
        return surveyID;
    }

    public void setSurveyID3(int surveyID) {
        this.surveyID = surveyID;
    }

    public int getSurveyVersion3() {
        return surveyVersion;
    }

    public void setSurveyVersion3(int surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public int getSurveyStatus3() {
        return surveyStatus;
    }

    public void setSurveyStatus3(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public String getQuestionListAsJsonString3() {
        return questionListAsJsonString;
    }

    public void setQuestionListAsJsonString3(String questionListAsJsonString) {
        this.questionListAsJsonString = questionListAsJsonString;
    }


}
