package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 16/11/2558.
 */
public class OnLoadedQuestionDoSurveyEvent {
    boolean reult;

    public OnLoadedQuestionDoSurveyEvent(boolean reult) {
        this.reult = reult;
    }

    public boolean isReult() {
        return reult;
    }

    public void setReult(boolean reult) {
        this.reult = reult;
    }
}
