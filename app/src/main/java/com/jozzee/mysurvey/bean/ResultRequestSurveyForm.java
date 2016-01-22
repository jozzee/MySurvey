package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 20/11/2558.
 */
public class ResultRequestSurveyForm {
    boolean surveyListIsEmpty;
    boolean allSurvey;
    List<SurveyBean> surveyList;



    public ResultRequestSurveyForm() {
        // TODO Auto-generated constructor stub
    }
    public ResultRequestSurveyForm(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);

        if(jsonObject.get("surveyListIsEmpty") != null){
            this.surveyListIsEmpty = jsonObject.get("surveyListIsEmpty").getAsBoolean();
        }
        if(jsonObject.get("allSurvey") != null){
            this.allSurvey = jsonObject.get("allSurvey").getAsBoolean();
        }
        if(jsonObject.get("surveyList")!=null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("surveyList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<SurveyBean>>(){}.getType();
            this.surveyList =  gson.fromJson(jsonArray, listType);
        }
    }
    public ResultRequestSurveyForm(boolean surveyListIsEmpty, boolean allSurvey, List<SurveyBean> surveyList) {
        super();
        this.surveyListIsEmpty = surveyListIsEmpty;
        this.allSurvey = allSurvey;
        this.surveyList = surveyList;
    }
    public boolean isSurveyListIsEmpty() {
        return surveyListIsEmpty;
    }
    public void setSurveyListIsEmpty(boolean surveyListIsEmpty) {
        this.surveyListIsEmpty = surveyListIsEmpty;
    }
    public boolean isAllSurvey() {
        return allSurvey;
    }
    public void setAllSurvey(boolean allSurvey) {
        this.allSurvey = allSurvey;
    }
    public List<SurveyBean> getSurveyList() {
        return surveyList;
    }
    public void setSurveyList(List<SurveyBean> surveyList) {
        this.surveyList = surveyList;
    }

}