package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 17/11/2558.
 */
public class AnswerBean {
    int questionID;
    int answerType;
    String answer;
    int choiceID;

    public AnswerBean() {
    }

    public AnswerBean(int questionID, int answerType, String answer, int choiceID) {
        this.questionID = questionID;
        this.answerType = answerType;
        this.answer = answer;
        this.choiceID = choiceID;
    }

    public AnswerBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString,JsonObject.class);
        if(jsonObject.get("questionID")!= null){
            this.questionID = jsonObject.get("questionID").getAsInt();
        }
        if(jsonObject.get("answerType")!= null){
            this.answerType = jsonObject.get("answerType").getAsInt();
        }
        if(jsonObject.get("answer")!= null){
            this.answer = jsonObject.get("answer").getAsString();
        }
        if(jsonObject.get("choiceID")!= null){
            this.choiceID = jsonObject.get("choiceID").getAsInt();
        }

    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(int choiceID) {
        this.choiceID = choiceID;
    }

}
