package com.jozzee.mysurvey.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;

/**
 * Created by Jozzee on 22/11/2558.
 */
public class RequestSurveyForm extends AsyncTask<String, Void, String> {
    private static String TAG = RequestSurveyForm.class.getSimpleName();

    private OnRequestCallbackListener mListener;
    private boolean onLoadMore = false;
    private boolean onLoad = false;
    private ImageView retry;
    private RelativeLayout layoutNotHaveSurvey;
    private ProgressBar progressBar;
    private Support support;

    public interface OnRequestCallbackListener {
        public void onRequestSurveyFormCallback (String result);
    }
    public RequestSurveyForm(boolean onLoad, boolean onLoadMore, ImageView retry,
                             ProgressBar progressBar, RelativeLayout layoutNotHaveSurvey, Support  support,
                             OnRequestCallbackListener mListener) {
        this.onLoad = onLoad;
        this.onLoadMore = onLoadMore;
        this.retry = retry;
        this.layoutNotHaveSurvey = layoutNotHaveSurvey;
        this.progressBar = progressBar;
        this.support = support;
    }



    @Override
    protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
        onLoad = true;
        if(onLoadMore){
            retry.setVisibility(View.GONE);
            layoutNotHaveSurvey.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
        else{
            retry.setVisibility(View.GONE);
            layoutNotHaveSurvey.setVisibility(View.GONE);
            support.setProgressBarColor(progressBar, 0xFFFF4081);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected String doInBackground(String... params) {
        String url = support.getURLLink()+"?command=RequestSurveyForm&surveyType=" +params[0] +"&rowStart="+params[1] +"&rowEnd=" +params[2];
        Log.i(TAG,"URL = "+url);
        return new ConnectionServiceCore().doOKHttpGetString(url);

    }
    @Override
    protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
        onLoad = false;
        progressBar.setVisibility(View.GONE); //Log.e(TAG, "setVisibility GONE on progressBar");
        mListener.onRequestSurveyFormCallback(result);
    }
}
