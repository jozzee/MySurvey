package com.jozzee.mysurvey.bean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
/**
 * Created by Jozzee on 3/12/2558.
 */
public class ResultRequestMySurveyBean {
    boolean allSurvey;
    boolean mySurveyListIsEmpty;
    List<SurveyBeanForMySurvey> mySurveyList;

    public ResultRequestMySurveyBean() {
        // TODO Auto-generated constructor stub
    }
    public ResultRequestMySurveyBean(String jsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("allSurvey") != null){
            this. allSurvey = jsonObject.get("allSurvey").getAsBoolean();
        }
        if(jsonObject.get("mySurveyListIsEmpty") != null){
            this. mySurveyListIsEmpty = jsonObject.get("mySurveyListIsEmpty").getAsBoolean();
        }
        if(jsonObject.get("mySurveyList") != null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("mySurveyList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<SurveyBeanForMySurvey>>(){}.getType();
            this.mySurveyList = gson.fromJson(jsonArray, listType);
        }
    }
    public ResultRequestMySurveyBean(boolean allSurvey, boolean mySurveyListIsEmpty,
                                     List<SurveyBeanForMySurvey> mySurveyList) {
        super();
        this.allSurvey = allSurvey;
        this.mySurveyListIsEmpty = mySurveyListIsEmpty;
        this.mySurveyList = mySurveyList;
    }
    public boolean isAllSurvey() {
        return allSurvey;
    }
    public void setAllSurvey(boolean allSurvey) {
        this.allSurvey = allSurvey;
    }
    public boolean isMySurveyListIsEmpty() {
        return mySurveyListIsEmpty;
    }
    public void setMySurveyListIsEmpty(boolean mySurveyListIsEmpty) {
        this.mySurveyListIsEmpty = mySurveyListIsEmpty;
    }
    public List<SurveyBeanForMySurvey> getMySurveyList() {
        return mySurveyList;
    }
    public void setMySurveyList(List<SurveyBeanForMySurvey> mySurveyList) {
        this.mySurveyList = mySurveyList;
    }

}