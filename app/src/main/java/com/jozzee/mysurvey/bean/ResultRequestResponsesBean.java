package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 18/11/2558.
 */
public class ResultRequestResponsesBean{
    List<ResponsesBean> responsesList;
    boolean allResponses;
    boolean responsesListIsEmpty = false;

    public ResultRequestResponsesBean() {
    }

    public ResultRequestResponsesBean(String beanAsJsonString) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(beanAsJsonString, JsonObject.class);
        if(jsonObject.get("responsesList") != null){
            JsonArray jsonArray = gson.fromJson(jsonObject.get("responsesList"), JsonArray.class);
            Type listType = new TypeToken<ArrayList<ResponsesBean>>(){}.getType();
            this.responsesList =  gson.fromJson(jsonArray, listType);
        }
        if(jsonObject.get("allResponses") != null){
            this.allResponses = jsonObject.get("allResponses").getAsBoolean();
        }
        if(jsonObject.get("responsesListIsEmpty") != null){
            this.responsesListIsEmpty = jsonObject.get("responsesListIsEmpty").getAsBoolean();
        }
    }

    public ResultRequestResponsesBean(List<ResponsesBean> responsesList, boolean allResponses,
                                      boolean responsesListIsEmpty) {
        super();
        this.responsesList = responsesList;
        this.allResponses = allResponses;
        this.responsesListIsEmpty = responsesListIsEmpty;
    }

    public List<ResponsesBean> getResponsesList() {
        return responsesList;
    }

    public void setResponsesList(List<ResponsesBean> responsesList) {
        this.responsesList = responsesList;
    }

    public boolean isAllResponses() {
        return allResponses;
    }

    public void setAllResponses(boolean allResponses) {
        this.allResponses = allResponses;
    }

    public boolean isResponsesListIsEmpty() {
        return responsesListIsEmpty;
    }

    public void setResponsesListIsEmpty(boolean responsesListIsEmpty) {
        this.responsesListIsEmpty = responsesListIsEmpty;
    }


}

