package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 25/11/2558.
 */
public class ResultAnalyzeChoiceQuestionBean  {
    boolean notAnyResponses;
    int numberOffAnswer;
    List<AnalyzeChoiceQuestionBean> analyzeList;

    public ResultAnalyzeChoiceQuestionBean() {
        // TODO Auto-generated constructor stub
    }
    public ResultAnalyzeChoiceQuestionBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("notAnyResponses") != null){
            this.notAnyResponses = jsonObject.get("notAnyResponses").getAsBoolean();
        }
        if(jsonObject.get("numberOffAnswer") != null){
            this.numberOffAnswer = jsonObject.get("numberOffAnswer").getAsInt();
        }
        if(jsonObject.get("analyzeList") != null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("analyzeList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<AnalyzeChoiceQuestionBean>>(){}.getType();
            this.analyzeList =  gson.fromJson(jsonArray, listType);
        }
    }
    public ResultAnalyzeChoiceQuestionBean(boolean notAnyResponses, int numberOffAnswer,
                                           List<AnalyzeChoiceQuestionBean> analyzeList) {
        super();
        this.notAnyResponses = notAnyResponses;
        this.numberOffAnswer = numberOffAnswer;
        this.analyzeList = analyzeList;
    }
    public boolean isNotAnyResponses() {
        return notAnyResponses;
    }
    public void setNotAnyResponses(boolean notAnyResponses) {
        this.notAnyResponses = notAnyResponses;
    }
    public int getNumberOffAnswer() {
        return numberOffAnswer;
    }
    public void setNumberOffAnswer(int numberOffAnswer) {
        this.numberOffAnswer = numberOffAnswer;
    }
    public List<AnalyzeChoiceQuestionBean> getAnalyzeList() {
        return analyzeList;
    }
    public void setAnalyzeList(List<AnalyzeChoiceQuestionBean> analyzeList) {
        this.analyzeList = analyzeList;
    }
}
