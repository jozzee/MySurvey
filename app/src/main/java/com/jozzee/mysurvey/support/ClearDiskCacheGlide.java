package com.jozzee.mysurvey.support;

import android.content.Context;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

/**
 * Created by Jozzee on 1/12/2558.
 */
public class ClearDiskCacheGlide extends AsyncTask<Context,Void,Void>{

    @Override
    protected Void doInBackground(Context... params) {
        Glide.get(params[0]).clearDiskCache();
        Glide.get(params[0]).clearMemory();
        return null;
    }
}
