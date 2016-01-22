package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 13/10/2558.
 */
public class SurveyBean {
    private static String TAG = SurveyBean.class.getCanonicalName();
    private int surveyID;
    private int surveyVersion;
    private String surveyName;
    private String surveyType;
    private String creator;
    private String lastUpdate;
    private int numberOfQuestions;
    private int numberOfTested;
    private String coverImage;
    private String passwordSurvey;
    private String link;

    public SurveyBean() {
    }

    public SurveyBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("surveyName") != null){
            this.surveyName = jsonObject.get("surveyName").getAsString();
        }
        if(jsonObject.get("surveyType") != null){
            this.surveyType = jsonObject.get("surveyType").getAsString();
        }
        if(jsonObject.get("creator") != null){
            this.creator = jsonObject.get("creator").getAsString();
        }
        if(jsonObject.get("lastUpdate") != null){
            this.lastUpdate = jsonObject.get("lastUpdate").getAsString();
        }
        if(jsonObject.get("numberOfQuestions") != null){
            this.numberOfQuestions = jsonObject.get("numberOfQuestions").getAsInt();
        }
        if(jsonObject.get("numberOfTested") != null){
            this.numberOfTested = jsonObject.get("numberOfTested").getAsInt();
        }
        if(jsonObject.get("coverImage") != null){
            this.coverImage = jsonObject.get("coverImage").getAsString();
        }
        if(jsonObject.get("passwordSurvey") != null){
            this.passwordSurvey = jsonObject.get("passwordSurvey").getAsString();
        }
        if(jsonObject.get("link") != null){
            this.link = jsonObject.get("link").getAsString();
        }
    }

    public String getLink() {
        return link;
    }

    public static void setTAG(String TAG) {
        SurveyBean.TAG = TAG;
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

    public String getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(String surveyType) {
        this.surveyType = surveyType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
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

    public String getPasswordSurvey() {
        return passwordSurvey;
    }

    public void setPasswordSurvey(String passwordSurvey) {
        this.passwordSurvey = passwordSurvey;
    }

}
