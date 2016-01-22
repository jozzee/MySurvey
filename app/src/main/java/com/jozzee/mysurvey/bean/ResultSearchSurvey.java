package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 4/12/2558.
 */
public class ResultSearchSurvey {
    boolean notMathAnySurvey;
    boolean allSurvey;
    List<SurveyBean> surveyList;

    public ResultSearchSurvey() {
        // TODO Auto-generated constructor stub
    }
    public ResultSearchSurvey(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("notMathAnySurvey")!=null){
            this.notMathAnySurvey = jsonObject.get("notMathAnySurvey").getAsBoolean();
        }
        if(jsonObject.get("allSurvey")!=null){
            this.allSurvey = jsonObject.get("allSurvey").getAsBoolean();
        }
        if(jsonObject.get("surveyList")!= null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("surveyList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<SurveyBean>>(){}.getType();
            this.surveyList =  gson.fromJson(jsonArray, listType);
        }

    }
    public ResultSearchSurvey(boolean notMathAnySurvey, boolean allSurvey, List<SurveyBean> surveyList) {
        super();
        this.notMathAnySurvey = notMathAnySurvey;
        this.allSurvey = allSurvey;
        this.surveyList = surveyList;
    }
    public boolean isNotMathAnySurvey() {
        return notMathAnySurvey;
    }
    public void setNotMathAnySurvey(boolean notMathAnySurvey) {
        this.notMathAnySurvey = notMathAnySurvey;
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
