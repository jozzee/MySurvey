package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 30/11/2558.
 */
public class OnBackPressedAfterRegisterEvent {
    boolean refresh;

    public OnBackPressedAfterRegisterEvent(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }
}
