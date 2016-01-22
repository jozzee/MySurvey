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
public class ResultRefreshSurveyForm {
    boolean haveNewUpdate;
    boolean notAnySurvey;
    boolean allSurvey;
    List<SurveyBean> surveyList;


    public ResultRefreshSurveyForm() {
        // TODO Auto-generated constructor stub
    }
    public ResultRefreshSurveyForm(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("haveNewUpdate")!=null){
            this.haveNewUpdate = jsonObject.get("haveNewUpdate").getAsBoolean();
        }
        if(jsonObject.get("notAnySurvey")!=null){
            this.notAnySurvey = jsonObject.get("notAnySurvey").getAsBoolean();
        }
        if(jsonObject.get("allSurvey")!=null){
            this.allSurvey = jsonObject.get("allSurvey").getAsBoolean();
        }
        if(jsonObject.get("surveyList")!=null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("surveyList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<SurveyBean>>(){}.getType();
            this.surveyList =  gson.fromJson(jsonArray, listType);
        }
    }
    public ResultRefreshSurveyForm(boolean haveNewUpdate, boolean notAnySurvey, boolean allSurvey,
                                   List<SurveyBean> surveyList) {
        super();
        this.haveNewUpdate = haveNewUpdate;
        this.notAnySurvey = notAnySurvey;
        this.allSurvey = allSurvey;
        this.surveyList = surveyList;
    }
    public boolean isHaveNewUpdate() {
        return haveNewUpdate;
    }
    public void setHaveNewUpdate(boolean haveNewUpdate) {
        this.haveNewUpdate = haveNewUpdate;
    }
    public boolean isNotAnySurvey() {
        return notAnySurvey;
    }
    public void setNotAnySurvey(boolean notAnySurvey) {
        this.notAnySurvey = notAnySurvey;
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