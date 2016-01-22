package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 11/11/2558.
 */
public class UpdateFormBean {
    String form;
    String result;
    String data;
    String lastModifyDate;

    public UpdateFormBean() {
        // TODO Auto-generated constructor stub
    }

    public UpdateFormBean(String form, String result, String data, String lastModifyDate) {
        super();
        this.form = form;
        this.result = result;
        this.data = data;
        this.lastModifyDate = lastModifyDate;
    }

    public UpdateFormBean(String beanAsJsonString) {
        JsonObject jsonObject = new Gson().fromJson(beanAsJsonString, JsonObject.class);
        if (jsonObject.get("form") != null) {
            this.form = jsonObject.get("form").getAsString();
        }
        if (jsonObject.get("result") != null) {
            this.result = jsonObject.get("result").getAsString();
        }
        if (jsonObject.get("data") != null) {
            this.data = jsonObject.get("data").getAsString();
        }
        if (jsonObject.get("lastModifyDate") != null) {
            this.lastModifyDate = jsonObject.get("lastModifyDate").getAsString();
        }
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(String lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }
}
