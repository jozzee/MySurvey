package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 27/10/2558.
 */
public class ChoiceBean {
    int questionID;
    int choiceID;
    int choiceNumber;
    String choiceData;

    public ChoiceBean() {
        // TODO Auto-generated constructor stub
    }

    public ChoiceBean(String choiceAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(choiceAsJsonString,JsonObject.class);
        if(jsonObject.get("choiceID") != null){
            this.choiceID = jsonObject.get("choiceID").getAsInt();
        }
        if(jsonObject.get("choiceData") != null){
            this.choiceData = jsonObject.get("choiceData").getAsString();
        }
        if(jsonObject.get("questionID") != null){
            this.questionID = jsonObject.get("questionID").getAsInt();
        }
    }
    public int getChoiceID() {
        return choiceID;
    }
    public void setChoiceID(int choiceID) {
        this.choiceID = choiceID;
    }
    public String getChoiceData() {
        return choiceData;
    }
    public void setChoiceData(String choiceData) {
        this.choiceData = choiceData;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getChoiceNumber() {
        return choiceNumber;
    }
    public void setChoiceNumber(int choiceNumber) {
        this.choiceNumber = choiceNumber;
    }


}
