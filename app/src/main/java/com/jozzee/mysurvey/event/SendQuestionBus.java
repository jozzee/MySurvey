package com.jozzee.mysurvey.event;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;

/**
 * Created by Jozzee on 3/11/2558.
 */
public class SendQuestionBus extends Bus {

    private static SendQuestionBus instance;

    public static SendQuestionBus getInstance(){
        if(instance != null){
            instance = new SendQuestionBus();
        }
        return instance;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    public void postQueue(final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("SendQuestionBus","555555555555555555555555555555555555555555555555555555555555555555555555555");
                SendQuestionBus.getInstance().post(obj);
            }
        });
    }
}
