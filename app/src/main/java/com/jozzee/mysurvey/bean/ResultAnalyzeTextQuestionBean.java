package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 24/11/2558.
 */
public class ResultAnalyzeTextQuestionBean {
    boolean analyzeListIsEmpty;
    boolean allData;
    List<AnalyzeTextQuestionBean> analyzeList;
    int numberOffAnswer;

    public ResultAnalyzeTextQuestionBean() {
        // TODO Auto-generated constructor stub
    }

    public ResultAnalyzeTextQuestionBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("analyzeList") != null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("analyzeList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<AnalyzeTextQuestionBean>>(){}.getType();
            this.analyzeList =  gson.fromJson(jsonArray, listType);
        }
        if((jsonObject.get("analyzeListIsEmpty") != null)){
            this.analyzeListIsEmpty = jsonObject.get("analyzeListIsEmpty").getAsBoolean();
        }
        if((jsonObject.get("allData") != null)){
            this.allData = jsonObject.get("allData").getAsBoolean();
        }
        if((jsonObject.get("numberOffAnswer") != null)){
            this.numberOffAnswer = jsonObject.get("numberOffAnswer").getAsInt();
        }
    }

    public ResultAnalyzeTextQuestionBean(boolean analyzeListIsEmpty, boolean allData,
                                         List<AnalyzeTextQuestionBean> analyzeList, int numberOffAnswer) {
        super();
        this.analyzeListIsEmpty = analyzeListIsEmpty;
        this.allData = allData;
        this.analyzeList = analyzeList;
        this.numberOffAnswer = numberOffAnswer;
    }

    public boolean isAnalyzeListIsEmpty() {
        return analyzeListIsEmpty;
    }

    public void setAnalyzeListIsEmpty(boolean analyzeListIsEmpty) {
        this.analyzeListIsEmpty = analyzeListIsEmpty;
    }

    public boolean isAllData() {
        return allData;
    }

    public void setAllData(boolean allData) {
        this.allData = allData;
    }

    public List<AnalyzeTextQuestionBean> getAnalyzeList() {
        return analyzeList;
    }

    public void setAnalyzeList(List<AnalyzeTextQuestionBean> analyzeList) {
        this.analyzeList = analyzeList;
    }

    public int getNumberOffAnswer() {
        return numberOffAnswer;
    }

    public void setNumberOffAnswer(int numberOffAnswer) {
        this.numberOffAnswer = numberOffAnswer;
    }


}
