package com.jozzee.mysurvey.bean;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by Jozzee on 4/12/2558.
 */
public class SearchBean {
    int accountID;
    int rowStart;
    int rowEnd;
    String query;

    public SearchBean() {
    }
    public SearchBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("accountID")!=null){
            this.accountID = jsonObject.get("accountID").getAsInt();
        }
        if(jsonObject.get("rowStart")!=null){
            this.rowStart = jsonObject.get("rowStart").getAsInt();
        }
        if(jsonObject.get("rowEnd")!=null){
            this.rowEnd = jsonObject.get("rowEnd").getAsInt();
        }
        if(jsonObject.get("query")!=null){
            this.query = jsonObject.get("query").getAsString();
        }
    }

    public SearchBean(int accountID, int rowStart, int rowEnd, String query) {
        this.accountID = accountID;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.query = query;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getRowStart() {
        return rowStart;
    }

    public void setRowStart(int rowStart) {
        this.rowStart = rowStart;
    }

    public int getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(int rowEnd) {
        this.rowEnd = rowEnd;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
