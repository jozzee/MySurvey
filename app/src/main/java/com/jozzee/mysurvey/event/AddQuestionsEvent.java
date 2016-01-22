package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 27/10/2558.
 */
public class AddQuestionsEvent {
    String surveyName;
    int surveyID;
    int surveyVersion;
    int surveyStatus;

    public AddQuestionsEvent() {
    }
    public AddQuestionsEvent(String surveyName, int surveyID, int surveyVersion, int surveyStatus) {
        this.surveyName = surveyName;
        this.surveyID = surveyID;
        this.surveyVersion = surveyVersion;
        this.surveyStatus = surveyStatus;
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

    public int getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }
}
