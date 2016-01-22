package com.jozzee.mysurvey.event;

import com.jozzee.mysurvey.bean.QuestionsBean;

/**
 * Created by Jozzee on 29/10/2558.
 */
public class OnSendQuestionEvent {
    QuestionsBean questionsBean;

    public OnSendQuestionEvent() {
    }

    public OnSendQuestionEvent(QuestionsBean questionsBean) {
        this.questionsBean = questionsBean;
    }

    public QuestionsBean getQuestionsBean() {
        return questionsBean;
    }

    public void setQuestionsBean(QuestionsBean questionsBean) {
        this.questionsBean = questionsBean;
    }
}
