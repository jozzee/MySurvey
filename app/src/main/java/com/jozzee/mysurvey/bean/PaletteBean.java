package com.jozzee.mysurvey.bean;

import android.support.v7.graphics.Palette;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jozzee.mysurvey.support.Support;

/**
 * Created by Jozzee on 11/12/2558.
 */
public class PaletteBean {
    int lightVibrant;
    int vibrant;
    int darkVibrant;
    int lightMuted;
    int muted;
    int darkMuted;
    public PaletteBean() {
    }
    public PaletteBean(Palette palette) {
        Support support = new Support();
        this.lightVibrant = palette.getLightVibrantColor(support.primaryColor());
        this.vibrant = palette.getVibrantColor(support.primaryColor());
        this.darkVibrant = palette.getDarkVibrantColor(support.primaryColor());
        this.lightMuted = palette.getLightMutedColor(support.primaryColor());
        this.muted = palette.getMutedColor(support.primaryColor());
        this.darkMuted = palette.getDarkMutedColor(support.primaryColor());
    }
    public PaletteBean(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString,JsonObject.class);
        if(jsonObject.get("lightVibrant") != null){
            this.lightVibrant = jsonObject.get("lightVibrant").getAsInt();
        }
        if(jsonObject.get("vibrant") != null){
            this.vibrant = jsonObject.get("vibrant").getAsInt();
        }
        if(jsonObject.get("darkVibrant") != null){
            this.darkVibrant = jsonObject.get("darkVibrant").getAsInt();
        }
        if(jsonObject.get("lightMuted") != null){
            this.lightMuted = jsonObject.get("lightMuted").getAsInt();
        }
        if(jsonObject.get("muted") != null){
            this.muted = jsonObject.get("muted").getAsInt();
        }
        if(jsonObject.get("darkMuted") != null){
            this.darkMuted = jsonObject.get("darkMuted").getAsInt();
        }
    }

    public int getLightVibrant() {
        return lightVibrant;
    }

    public void setLightVibrant(int lightVibrant) {
        this.lightVibrant = lightVibrant;
    }

    public int getVibrant() {
        return vibrant;
    }

    public void setVibrant(int vibrant) {
        this.vibrant = vibrant;
    }

    public int getDarkVibrant() {
        return darkVibrant;
    }

    public void setDarkVibrant(int darkVibrant) {
        this.darkVibrant = darkVibrant;
    }

    public int getLightMuted() {
        return lightMuted;
    }

    public void setLightMuted(int lightMuted) {
        this.lightMuted = lightMuted;
    }

    public int getMuted() {
        return muted;
    }

    public void setMuted(int muted) {
        this.muted = muted;
    }

    public int getDarkMuted() {
        return darkMuted;
    }

    public void setDarkMuted(int darkMuted) {
        this.darkMuted = darkMuted;
    }

}
