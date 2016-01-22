package com.jozzee.mysurvey.event;

import com.jozzee.mysurvey.bean.AccountDataBean;

/**
 * Created by Jozzee on 30/11/2558.
 */
public class OnBackPressedFromViewProfileEvent {
    AccountDataBean accountDataBean;

    public OnBackPressedFromViewProfileEvent(AccountDataBean accountDataBean) {
        this.accountDataBean = accountDataBean;
    }

    public AccountDataBean getAccountDataBean() {
        return accountDataBean;
    }

    public void setAccountDataBean(AccountDataBean accountDataBean) {
        this.accountDataBean = accountDataBean;
    }
}
