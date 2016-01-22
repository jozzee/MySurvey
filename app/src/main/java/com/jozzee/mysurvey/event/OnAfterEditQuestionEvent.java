package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 12/11/2558.
 */
public class OnAfterEditQuestionEvent {
    String lastUpdate;
    int numberOffQuestions;
    int surveyVersion;
    boolean newVersion;
    String link;

    public OnAfterEditQuestionEvent() {
    }

    public OnAfterEditQuestionEvent(String lastUpdate, int numberOffQuestions, int surveyVersion, boolean newVersion, String link) {
        this.lastUpdate = lastUpdate;
        this.numberOffQuestions = numberOffQuestions;
        this.surveyVersion = surveyVersion;
        this.newVersion = newVersion;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getNumberOffQuestions() {
        return numberOffQuestions;
    }

    public void setNumberOffQuestions(int numberOffQuestions) {
        this.numberOffQuestions = numberOffQuestions;
    }

    public int getSurveyVersion() {
        return surveyVersion;
    }

    public void setSurveyVersion(int surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }
}
