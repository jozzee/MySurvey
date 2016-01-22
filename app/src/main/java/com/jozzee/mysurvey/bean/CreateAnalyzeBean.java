package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 23/11/2558.
 */
public class CreateAnalyzeBean {
    int questionID;
    String question;
    int answerType;
    int questionNumber;

    public CreateAnalyzeBean() {
    }
    public CreateAnalyzeBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("questionID") != null){
            this.questionID = jsonObject.get("questionID").getAsInt();
        }
        if(jsonObject.get("question") != null){
            this.question = jsonObject.get("question").getAsString();
        }
        if(jsonObject.get("answerType") != null){
            this.answerType = jsonObject.get("answerType").getAsInt();
        }
        if(jsonObject.get("questionNumber") != null){
            this.questionNumber = jsonObject.get("questionNumber").getAsInt();
        }
    }

    public CreateAnalyzeBean(int questionID, String question, int answerType, int questionNumber) {
        this.questionID = questionID;
        this.question = question;
        this.answerType = answerType;
        this.questionNumber = questionNumber;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getAnswerType() {
        return answerType;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }
}
