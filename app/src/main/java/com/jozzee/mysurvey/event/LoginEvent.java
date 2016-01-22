package com.jozzee.mysurvey.event;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jozzee on 7/10/2558.
 */
public class LoginEvent {
    private int accountID;

    public LoginEvent() {
    }
    public LoginEvent(int accountID) {
        this.accountID = accountID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
