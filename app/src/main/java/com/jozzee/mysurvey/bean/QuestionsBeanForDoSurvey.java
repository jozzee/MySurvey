package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 16/11/2558.
 */
public class QuestionsBeanForDoSurvey {
    String action;
    int surveyID;
    int surveyVersion;
    int questionID;
    String question;
    int answerType;
    int questionNumber;
    List<ChoiceBean> choiceList;
    String answerData;
    int choiceIDAsAnswer;

    public QuestionsBeanForDoSurvey() {
        // TODO Auto-generated constructor stub
    }
    public QuestionsBeanForDoSurvey(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("action") != null){
            this.action = jsonObject.get("action").getAsString();
        }
        if(jsonObject.get("surveyID") != null){
            this.surveyID = jsonObject.get("surveyID").getAsInt();
        }
        if(jsonObject.get("surveyVersion") != null){
            this.surveyVersion = jsonObject.get("surveyVersion").getAsInt();
        }
        if(jsonObject.get("questionID") != null ) {
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
        if(jsonObject.get("choiceList") != null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("choiceList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<ChoiceBean>>(){}.getType();
            this.choiceList = gson.fromJson(jsonArray, listType);
        }
        if(jsonObject.get("answerData") != null){
            this.answerData = jsonObject.get("answerData").getAsString();
        }
        if(jsonObject.get("choiceIDAsAnswer") != null){
            this.choiceIDAsAnswer = jsonObject.get("choiceIDAsAnswer").getAsInt();
        }
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

    public List<ChoiceBean> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(List<ChoiceBean> choiceList) {
        this.choiceList = choiceList;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getAnswerData() {

        return answerData;
    }

    public void setAnswerData(String answerData) {
        this.answerData = answerData;
    }

    public int getChoiceIDAsAnswer() {
        return choiceIDAsAnswer;
    }

    public void setChoiceIDAsAnswer(int choiceIDAsAnswer) {
        this.choiceIDAsAnswer = choiceIDAsAnswer;
    }

}
