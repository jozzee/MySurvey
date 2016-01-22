package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 11/11/2558.
 */
public class UpdateImageBean {
    String from;
    String result;
    String urlImage;
    String lastUpdate;
    public UpdateImageBean() {
        // TODO Auto-generated constructor stub
    }
    public UpdateImageBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString, JsonObject.class);
        if(jsonObject.get("from")!= null){
            this.from = jsonObject.get("from").getAsString();
        }
        if(jsonObject.get("result")!= null){
            this.result = jsonObject.get("result").getAsString();
        }
        if(jsonObject.get("urlImage")!= null){
            this.urlImage = jsonObject.get("urlImage").getAsString();
        }
        if(jsonObject.get("lastUpdate")!= null){
            this.lastUpdate = jsonObject.get("lastUpdate").getAsString();
        }
    }
    public UpdateImageBean(String from, String result, String urlImage, String lastUpdate) {
        super();
        this.from = from;
        this.result = result;
        this.urlImage = urlImage;
        this.lastUpdate = lastUpdate;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
