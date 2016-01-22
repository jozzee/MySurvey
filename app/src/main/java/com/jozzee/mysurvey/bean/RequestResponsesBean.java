package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 18/11/2558.
 */
public class RequestResponsesBean {
    int surveyID;
    int surveyVersion;
    int rowStart;
    int reoEnd;

    public RequestResponsesBean() {
    }
    public RequestResponsesBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("rowStart") != null){
            this.rowStart = jsonObject.get("rowStart").getAsInt();
        }
        if(jsonObject.get("reoEnd") != null){
            this.reoEnd = jsonObject.get("reoEnd").getAsInt();
        }
    }

    public RequestResponsesBean(int surveyID, int surveyVersion, int rowStart, int reoEnd) {
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.rowStart = rowStart;
        this.reoEnd = reoEnd;
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

    public int getRowStart() {
        return rowStart;
    }

    public void setRowStart(int rowStart) {
        this.rowStart = rowStart;
    }

    public int getReoEnd() {
        return reoEnd;
    }

    public void setReoEnd(int reoEnd) {
        this.reoEnd = reoEnd;
    }

}
