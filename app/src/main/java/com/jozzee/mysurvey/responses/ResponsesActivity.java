package com.jozzee.mysurvey.responses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.adpter.OnLoadMoreListener;
import com.jozzee.mysurvey.adpter.RecyclerItemClickListener;
import com.jozzee.mysurvey.adpter.ResponsesAdapter;
import com.jozzee.mysurvey.bean.RefreshResponsesBean;
import com.jozzee.mysurvey.bean.RequestResponsesBean;
import com.jozzee.mysurvey.bean.ResponsesBean;
import com.jozzee.mysurvey.bean.ResultRequestResponsesBean;
import com.jozzee.mysurvey.bean.SurveyBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.viewanswer.ViewAnswerResponsesActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResponsesActivity extends AppCompatActivity {
    private static String TAG = ResponsesActivity.class.getSimpleName();

    private CoordinatorLayout rootLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ResponsesAdapter adapter;
    private List<ResponsesBean> responsesList;
    private SwipeRefreshLayout refreshLayout;
    private String backupData = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private boolean loadedAllData = false;
    private ProgressBar progressBar;
    private ImageView retry;
    private RelativeLayout layoutNotAnyResponses;
    private int surveyID;
    private int surveyVersion;
    private Bundle bundle;
    private Support support;
    private ManageJson manageJson;
    private TextView question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {  Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

        bundle = getIntent().getExtras();
        if(bundle != null){
            surveyID = (Integer)bundle.get("surveyID");
            surveyVersion = (Integer)bundle.get("surveyVersion");
        }
        if(savedInstanceState != null){

        }

        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_responses);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout_responses);
        toolbar = (Toolbar)findViewById(R.id.toolbar_responses);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.titleActivityResponses);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_responses);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        responsesList = new ArrayList<ResponsesBean>();
        responsesList.add(null);
        adapter = new ResponsesAdapter(recyclerView,responsesList,this);
        recyclerView.setAdapter(adapter);
        //recyclerView.setVisibility(View.GONE);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout_responses);
        refreshLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar)findViewById(R.id.progressBar_responses);
        retry = (ImageView)findViewById(R.id.imageView_retry_responses);
        layoutNotAnyResponses = (RelativeLayout)findViewById(R.id.relativeLayout_notHaveResponses);


        support = new Support();
        manageJson = new ManageJson();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {   Log.i(TAG, "Click Item on RecycleView");
                ResponsesBean bean = responsesList.get(position);
                Intent viewAnswerIntent = new Intent(view.getContext(), ViewAnswerResponsesActivity.class);
                viewAnswerIntent.putExtra("answerBy",bean.getAnswerBy()); Log.e(TAG, "answerBy = " + bean.getAnswerBy());
                viewAnswerIntent.putExtra("answerDate",bean.getResponsesDate());
                viewAnswerIntent.putExtra("accountID",bean.getAccountID()); Log.e(TAG,"accountID = "+bean.getAccountID());
                startActivity(viewAnswerIntent);
            }
        }));

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() { Log.i(TAG,"onLoadMore responses");
                if (!loadedAllData) {
                    if (support.checkNetworkConnection(getContextFromActivity())) {
                        int rowStart = (responsesList.size() + 1);
                        int rowEnd = (responsesList.size() + 50);
                        responsesList.add(null);
                        adapter.notifyItemInserted(responsesList.size() - 1);
                        RequestResponsesBean requestResponsesBean = new RequestResponsesBean(surveyID, surveyVersion, rowStart, rowEnd);
                        new RequestResponsesTask(true).execute(new Gson().toJson(requestResponsesBean));
                    } else {
                        showSnackBarNotConnectInternet(rootLayout);
                        adapter.setLoaded();
                    }
                } else {
                    Log.i(TAG, "is load all responses off this survey");
                    adapter.setLoaded();
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { Log.i(TAG, "onRefresh responses");
                ResponsesBean bean = responsesList.get(0);
                String responsesDate = bean.getResponsesDate();
                if (support.checkNetworkConnection(getContextFromActivity())) {
                    RefreshResponsesBean refreshBean = new RefreshResponsesBean(surveyID, surveyVersion, responsesDate);
                    new RefreshResponsesTask().execute(new Gson().toJson(refreshBean));
                } else {
                    refreshLayout.setRefreshing(false);
                    showSnackBarNotConnectInternet(rootLayout);
                }
            }
        });
        layoutNotAnyResponses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG,"on click layoutNotAnyResponses");
                requestResponses();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Log.i(TAG,"retry");
                requestResponses();
            }
        });
        //----------------------------------------------------------------------------------------------------------------------
        if(backupData.equals("") &&!connectionLost &&!onLoad){
            // Start activity not have backup data, can connect server, and  AsyncTask not on loading...
            requestResponses();
        }
        else if(connectionLost){ // Can't connect to server.
            retry.setVisibility(View.VISIBLE);
        }
        else if(onLoad && backupData.equals("")){ // On restore, AsyncTask on loading and not have  backup data
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(onLoad && !(backupData.equals(""))){ //On restore, AsyncTask on loading and have backup data
            responsesList.remove(responsesList.size() - 1);//remove  progress bar
            adapter.notifyItemRemoved(responsesList.size());
            List<ResponsesBean> tempList = manageJson.getResponsesListFromJsonString(backupData);
            for(ResponsesBean bean:tempList){
                responsesList.add(bean);
            }
            responsesList.add(null);
            adapter.notifyDataSetChanged();
            refreshLayout.setVisibility(View.VISIBLE);
        }
        else if(!(backupData.equals(""))){ // On restore, have backup data
            responsesList.remove(responsesList.size() - 1);//remove  progress bar
            adapter.notifyItemRemoved(responsesList.size());
            List<ResponsesBean> tempList = manageJson.getResponsesListFromJsonString(backupData);
            for(ResponsesBean bean:tempList){
                responsesList.add(bean);
            }
            adapter.notifyDataSetChanged();
            refreshLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart(){
        Log.i(TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onResume(){
        Log.i(TAG, "onResume");

        super.onResume();
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");

        super.onPause();
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.
    }

    @Override
    protected void onStop(){
        Log.i(TAG, "onStop");

        super.onStop();
    }

    @Override
    protected void onRestart(){
        Log.i(TAG, "onRestart");

        super.onRestart();
    }
    @Override
    protected void onDestroy(){
        Log.i(TAG, "onDestroy");

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_responses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.i(TAG, "id = " + id);

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == 16908332){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestResponses(){ Log.i(TAG,"requestResponses");
        if(support.checkNetworkConnection(this)){
            RequestResponsesBean bean = new RequestResponsesBean(surveyID,surveyVersion,1,50); // request responses between row 1 to row 50
            new RequestResponsesTask(false).execute(new Gson().toJson(bean));
        }
        else{
            showSnackBarNotConnectInternet(rootLayout);
        }
    }
    public void onRequestResponsesCallback(String result){ Log.i(TAG, "onRequestResponsesCallback");
        if(!(result.equals("connectionLost"))){

            ResultRequestResponsesBean resultBean = new ResultRequestResponsesBean(result);
            if(!(resultBean.isResponsesListIsEmpty())){
                if(resultBean.isAllResponses()){
                    loadedAllData = true; Log.i(TAG,"Loaded all response, set loadAllData = true.");
                }
                responsesList.remove(responsesList.size() - 1);//remove  progress bar
                adapter.notifyItemRemoved(responsesList.size());
                List<ResponsesBean> tempList = resultBean.getResponsesList();
                for(ResponsesBean bean:tempList){
                    responsesList.add(bean);
                }
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
                backupData = manageJson.getJsonStringFromResponsesList(responsesList);
                refreshLayout.setVisibility(View.VISIBLE);
                layoutNotAnyResponses.setVisibility(View.GONE);
                adapter.setLoaded();
            }
            else{
                if(responsesList.get(0) == null){
                    Log.i(TAG,"Not any responses off this survey");
                    layoutNotAnyResponses.setVisibility(View.VISIBLE);
                }
                else{
                    Log.i(TAG,"Loaded all response, set loadAllData = true.");
                    loadedAllData = true;
                    responsesList.remove(responsesList.size() - 1);//remove  progress bar
                    adapter.notifyItemRemoved(responsesList.size());
                    Snackbar.make(rootLayout, "All Responses.", Snackbar.LENGTH_LONG).show();
                }
                adapter.setLoaded();
            }
        }
        else{ Log.i(TAG,"Can't connect to server.");
            connectionLost = true;
            onLoad = false;
            adapter.setLoaded();
            backupData = "";
            refreshLayout.setVisibility(View.GONE);
            layoutNotAnyResponses.setVerticalGravity(View.GONE);
            retry.setVisibility(View.VISIBLE);
            support.showSnackBarNotConnectToServer(rootLayout);
        }

    }
    public void onRefreshResponsesCallback(String result){ Log.i(TAG, "onRefreshResponsesCallback");
        if(!(result.equals("connectionLost")) && !(result.equals("0"))){
            ResultRequestResponsesBean resultBean = new ResultRequestResponsesBean(result);
            if(resultBean.isAllResponses()){
                Log.i(TAG, "Loaded all response, set loadAllData = true.");
                loadedAllData = true;
            }
            responsesList = resultBean.getResponsesList();
            adapter.setListItem(responsesList);
            adapter.notifyDataSetChanged();
        }
        else if(!(result.equals("connectionLost")) && (result.equals("0"))){
            Snackbar.make(rootLayout, "Not New Responses.", Snackbar.LENGTH_LONG).show();
        }
        else if(result.equals("connectionLost")){
            support.showSnackBarNotConnectToServer(rootLayout);
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    private class RequestResponsesTask extends AsyncTask<String,Void,String>{
        private String TAG  = RequestResponsesTask.class.getSimpleName();
        private boolean onLoadMore = false;

        public RequestResponsesTask(boolean onLoadMore) {
            this.onLoadMore = onLoadMore;
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            if(!onLoadMore){
                Log.e(TAG,"onLoadMore = false");
                layoutNotAnyResponses.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),"RequestResponses",params[0]);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE);
            onRequestResponsesCallback(result);
        }
    }
    private class RefreshResponsesTask extends AsyncTask<String,Void,String>{
        private String TAG  = RefreshResponsesTask.class.getSimpleName();

        public RefreshResponsesTask() {
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
        }
        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),"RefreshResponses",params[0]);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            refreshLayout.setRefreshing(false);
            onRefreshResponsesCallback(result);
        }
    }
    private Context getContextFromActivity(){
        return this;
    }
    public void showSnackBarNotConnectInternet(View viewAnyWhere){
        Snackbar.make(viewAnyWhere, "Not connect internet", Snackbar.LENGTH_LONG)
                .setAction("Setting", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                }).show();

    }
}
