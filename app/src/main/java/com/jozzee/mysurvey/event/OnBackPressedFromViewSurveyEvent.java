package com.jozzee.mysurvey.event;

import com.jozzee.mysurvey.bean.SurveyBeanForMySurvey;

/**
 * Created by Jozzee on 13/11/2558.
 */
public class OnBackPressedFromViewSurveyEvent {
    SurveyBeanForMySurvey surveyBeanForMySurvey;
    int position;
    String action;

    public OnBackPressedFromViewSurveyEvent(SurveyBeanForMySurvey surveyBeanForMySurvey, int position, String action) {
        this.surveyBeanForMySurvey = surveyBeanForMySurvey;
        this.position = position;
        this.action = action;
    }

    public SurveyBeanForMySurvey getSurveyBeanForMySurvey() {
        return surveyBeanForMySurvey;
    }

    public void setSurveyBeanForMySurvey(SurveyBeanForMySurvey surveyBeanForMySurvey) {
        this.surveyBeanForMySurvey = surveyBeanForMySurvey;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
