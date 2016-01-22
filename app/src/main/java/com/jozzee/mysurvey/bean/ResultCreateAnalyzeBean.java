package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 23/11/2558.
 */
public class ResultCreateAnalyzeBean  {
    boolean questionIsEmpty;
    String createAnalyzeListAsJsonString;

    public ResultCreateAnalyzeBean() {
        // TODO Auto-generated constructor stub
    }
    public ResultCreateAnalyzeBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        if(jsonObject.get("questionIsEmpty")!= null){
            this.questionIsEmpty = jsonObject.get("questionIsEmpty").getAsBoolean();
        }
        if(jsonObject.get("createAnalyzeListAsJsonString") != null){
            this.createAnalyzeListAsJsonString = jsonObject.get("createAnalyzeListAsJsonString").getAsString();
        }
    }
    public ResultCreateAnalyzeBean(boolean questionIsEmpty, String createAnalyzeListAsJsonString) {
        super();
        this.questionIsEmpty = questionIsEmpty;
        this.createAnalyzeListAsJsonString = createAnalyzeListAsJsonString;
    }
    public boolean isQuestionIsEmpty() {
        return questionIsEmpty;
    }
    public void setQuestionIsEmpty(boolean questionIsEmpty) {
        this.questionIsEmpty = questionIsEmpty;
    }
    public String getCreateAnalyzeListAsJsonString() {
        return createAnalyzeListAsJsonString;
    }
    public void setCreateAnalyzeListAsJsonString(String createAnalyzeListAsJsonString) {
        this.createAnalyzeListAsJsonString = createAnalyzeListAsJsonString;
    }


}