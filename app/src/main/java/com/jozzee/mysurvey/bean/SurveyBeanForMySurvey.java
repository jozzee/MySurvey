package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 5/11/2558.
 */
public class SurveyBeanForMySurvey {
    private int surveyID;
    private int surveyVersion;
    private String surveyName;
    private int surveyType;
    private int surveyStatus;
    private int numberOfQuestions;
    private String creteDate;
    private String lastUpdate;
    private int numberOfTested;
    private String coverImage;
    private String surveyPassword;
    private String action;
    private String lastResponses;
    private String link;

    public SurveyBeanForMySurvey() {
    }
    public SurveyBeanForMySurvey(String objectAsJsonStrong) {
        JsonObject jsonObject = new Gson().fromJson(objectAsJsonStrong,JsonObject.class);
        if(jsonObject.get("surveyID")!=null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion")!=null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("surveyName")!=null){
            this.surveyName = jsonObject.get("surveyName").getAsString();
        }
        if(jsonObject.get("surveyType")!=null){
            this.surveyType = jsonObject.get("surveyType").getAsInt();
        }
        if(jsonObject.get("surveyStatus")!=null){
            this.surveyStatus = jsonObject.get("surveyStatus").getAsInt();
        }
        if(jsonObject.get("numberOfQuestions")!=null){
            this.numberOfQuestions = jsonObject.get("numberOfQuestions").getAsInt();
        }
        if(jsonObject.get("creteDate")!=null){
            this.creteDate = jsonObject.get("creteDate").getAsString();
        }
        if(jsonObject.get("lastUpdate")!=null){
            this.lastUpdate = jsonObject.get("lastUpdate").getAsString();
        }
        if(jsonObject.get("numberOfTested")!=null){
            this.numberOfTested = jsonObject.get("numberOfTested").getAsInt();
        }
        if(jsonObject.get("coverImage")!=null){
            this.coverImage = jsonObject.get("coverImage").getAsString();
        }
        if(jsonObject.get("surveyPassword")!=null){
            this.surveyPassword = jsonObject.get("surveyPassword").getAsString();
        }
        if(jsonObject.get("action")!=null){
            this.action = jsonObject.get("action").getAsString();
        }
        if(jsonObject.get("lastResponses")!= null){
            this.lastResponses = jsonObject.get("lastResponses").getAsString();
        }
        if(jsonObject.get("link") != null){
            this.link = jsonObject.get("link").getAsString();
        }
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
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

    public Integer getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(Integer surveyType) {
        this.surveyType = surveyType;
    }

    public Integer getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(Integer surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getCreteDate() {
        return creteDate;
    }

    public void setCreteDate(String creteDate) {
        this.creteDate = creteDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getNumberOfTested() {
        return numberOfTested;
    }

    public void setNumberOfTested(int numberOfTested) {
        this.numberOfTested = numberOfTested;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSurveyPassword() {
        return surveyPassword;
    }

    public void setSurveyPassword(String surveyPassword) {
        this.surveyPassword = surveyPassword;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getLastResponses() {
        return lastResponses;
    }
    public void setLastResponses(String lastResponses) {
        this.lastResponses = lastResponses;
    }
    public void setSurveyType(int surveyType) {
        this.surveyType = surveyType;
    }
    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

}

