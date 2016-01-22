package com.jozzee.mysurvey.support;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.bean.AnalyzeTextQuestionBean;
import com.jozzee.mysurvey.bean.QuestionsBeanForDoSurvey;
import com.jozzee.mysurvey.bean.ResponsesBean;
import com.jozzee.mysurvey.bean.SurveyBean;
import com.jozzee.mysurvey.bean.SurveyBeanForMySurvey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 26/11/2558.
 */
public class ManageJson {
    private Gson gson = new Gson();
    private JsonObject jsonObject;
    private JsonArray jsonArray;
    private Type listType;
    private JsonElement element;

    public ManageJson() {
    }

    public String serializationStringToJson(String... data){
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for(int i=0;i<data.length;i++){
            String[] temp = data[i].split(",");
            String jsonData = "\"" +temp[0] +"\":\"" +temp[1] + "\"";
            if(i < (data.length-1)){
                jsonData = jsonData+",";
            }
            buffer.append(jsonData);
        }
        buffer.append("}");
        return buffer.toString();
    }
    public List<QuestionsBeanForDoSurvey> getQuestionListForDoSurvey(String jsonString){
        jsonArray = gson.fromJson(jsonString, JsonArray.class);
        listType = new TypeToken<ArrayList<QuestionsBeanForDoSurvey>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromQuestionListForDoSurvey(List<QuestionsBeanForDoSurvey> questionList){
        element = gson.toJsonTree(questionList);
        return gson.toJson(element);
    }
    public List<SurveyBeanForMySurvey> getMySurveyListFromJsonString (String jsonString){
        jsonArray = gson.fromJson(jsonString, JsonArray.class);
        listType = new TypeToken<ArrayList<SurveyBeanForMySurvey>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromMySurveyList(List<SurveyBeanForMySurvey> mySurveyList){
        element = gson.toJsonTree(mySurveyList);
        return gson.toJson(element);
    }
    public List<SurveyBean> getSurveyListFromJsonString (String jsonString){
        jsonArray = gson.fromJson(jsonString, JsonArray.class);
        listType = new TypeToken<ArrayList<SurveyBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }

    public String getJsonStringFromSurveyList(List<SurveyBean> surveyList){
        element = gson.toJsonTree(surveyList);
        return gson.toJson(element);
    }
    public List<AnalyzeTextQuestionBean> getAnalyzeTextListFromJsonString (String jsonString){
        jsonArray = gson.fromJson(jsonString, JsonArray.class);
        listType = new TypeToken<ArrayList<AnalyzeTextQuestionBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromAnalyzeTextList(List<AnalyzeTextQuestionBean> analyzeList){
        element = gson.toJsonTree(analyzeList);
        return gson.toJson(element);
    }
    public List<ResponsesBean> getResponsesListFromJsonString (String jsonString){
        jsonArray = gson.fromJson(jsonString, JsonArray.class);
        listType = new TypeToken<ArrayList<ResponsesBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromResponsesList(List<ResponsesBean> responsesList){
        element = gson.toJsonTree(responsesList);
        return gson.toJson(element);
    }
}
