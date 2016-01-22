package com.jozzee.mysurvey.bean;

/**
 * Created by Jozzee on 24/10/2558.
 */
public class CreateSurveyFormBean {
    String surveyName;
    int surveyStatus;
    int surveyType;
    int accountID;
    String surveyPassword;
    String surveyImage;
    String createDate;

    public CreateSurveyFormBean() {
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public int getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public int getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(int surveyType) {
        this.surveyType = surveyType;
    }

    public String getSurveyPassword() {
        return surveyPassword;
    }

    public void setSurveyPassword(String surveyPassword) {
        this.surveyPassword = surveyPassword;
    }

    public String getSurveyImage() {
        return surveyImage;
    }

    public void setSurveyImage(String surveyImage) {
        this.surveyImage = surveyImage;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
