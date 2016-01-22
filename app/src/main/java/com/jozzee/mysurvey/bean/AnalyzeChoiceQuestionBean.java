package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 25/11/2558.
 */
public class AnalyzeChoiceQuestionBean {
    int color;
    int choiceNumber;
    String Choice;
    float percent;
    int numberOffSelectThisChoice;

    public AnalyzeChoiceQuestionBean() {
    }
    public AnalyzeChoiceQuestionBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("color") != null){
            this.color = jsonObject.get("color").getAsInt();
        }
        if(jsonObject.get("Choice") != null){
            this.Choice = jsonObject.get("Choice").getAsString();
        }
        if(jsonObject.get("percent") != null){
            this.percent = jsonObject.get("percent").getAsFloat();
        }
        if(jsonObject.get("numberOffSelectThisChoice") != null){
            this.numberOffSelectThisChoice = jsonObject.get("numberOffSelectThisChoice").getAsInt();
        }
        if(jsonObject.get("choiceNumber")!=null){
            this.choiceNumber = jsonObject.get("choiceNumber").getAsInt();
        }
    }

    public AnalyzeChoiceQuestionBean(int color, String choice, Float percent, int numberOffSelectThisChoice) {
        this.color = color;
        Choice = choice;
        this.percent = percent;
        this.numberOffSelectThisChoice = numberOffSelectThisChoice;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getChoice() {
        return Choice;
    }

    public void setChoice(String choice) {
        Choice = choice;
    }

    public float getPercent() {
        return percent;
    }
    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getNumberOffSelectThisChoice() {
        return numberOffSelectThisChoice;
    }
    public void setNumberOffSelectThisChoice(int numberOffSelectThisChoice) {
        this.numberOffSelectThisChoice = numberOffSelectThisChoice;
    }
    public int getChoiceNumber() {
        return choiceNumber;
    }
    public void setChoiceNumber(int choiceNumber) {
        this.choiceNumber = choiceNumber;
    }

}
