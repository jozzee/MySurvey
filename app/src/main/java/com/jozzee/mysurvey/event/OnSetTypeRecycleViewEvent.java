package com.jozzee.mysurvey.event;

/**
 * Created by Jozzee on 24/10/2558.
 */
public class OnSetTypeRecycleViewEvent {
    int typeOffRecycleView;

    public OnSetTypeRecycleViewEvent(int typeOffRecycleView) {
        this.typeOffRecycleView = typeOffRecycleView;
    }

    public int getTypeOffRecycleView() {
        return typeOffRecycleView;
    }

    public void setTypeOffRecycleView(int typeOffRecycleView) {
        this.typeOffRecycleView = typeOffRecycleView;
    }
}
