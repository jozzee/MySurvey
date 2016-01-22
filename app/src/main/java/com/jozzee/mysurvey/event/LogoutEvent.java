package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 8/10/2558.
 */
public class LogoutEvent {
    private int accountID;

    public LogoutEvent() {
    }
    public LogoutEvent(int accountID) {
        this.accountID = accountID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
