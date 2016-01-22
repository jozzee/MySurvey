package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 30/11/2558.
 */
public class OnAfterRegisterEvent {
    int accountID;

    public OnAfterRegisterEvent(int accountID) {
        this.accountID = accountID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
