package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 25/10/2558.
 */
public class ResultCreateSurveyForm {
    String result;
    int surveyID;
    int surveyVersion;
    String surveyName;

    public ResultCreateSurveyForm() {

    }
    public ResultCreateSurveyForm(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        this.result = jsonObject.get("result").getAsString();
        this.surveyID = jsonObject.get("surveyID").getAsInt();
        this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        if(jsonObject.get("surveyName") != null){
            this.surveyName = jsonObject.get("surveyName").getAsString();
        }
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
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
