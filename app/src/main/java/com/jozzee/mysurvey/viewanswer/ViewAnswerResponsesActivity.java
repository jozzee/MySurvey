package com.jozzee.mysurvey.viewanswer;

import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.adpter.ViewAnswerAdapter;
import com.jozzee.mysurvey.bean.RequestViewAnswerBean;
import com.jozzee.mysurvey.bean.ResponsesBean;
import com.jozzee.mysurvey.bean.ViewAnswerBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewAnswerResponsesActivity extends AppCompatActivity {
    private static String TAG = ViewAnswerResponsesActivity.class.getSimpleName();

    private CoordinatorLayout rootLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String backupData = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private ProgressBar progressBar;
    private ImageView retry;
    private Bundle bundle;
    private Support support;
    private String answerBy;
    private String answerDate;
    private int accountID;
    private List<ViewAnswerBean> answerList;
    private ViewAnswerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answer_responses);

        bundle = getIntent().getExtras();
        if(bundle != null){
            answerBy = (String)bundle.get("answerBy");
            answerDate = (String)bundle.get("answerDate");
            accountID = (Integer)bundle.get("accountID");
        }
        if(savedInstanceState != null){
            backupData = savedInstanceState.getString("backupData","");
        }

        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_viewAnswerResponses);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout_viewAnswerResponses);
        toolbar = (Toolbar)findViewById(R.id.toolbar_viewAnswerResponses);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(answerBy);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_viewAnswerResponses);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        answerList = new ArrayList<ViewAnswerBean>();
        answerList.add(null);
        adapter = new ViewAnswerAdapter(answerList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        retry = (ImageView)findViewById(R.id.imageView_retry_viewAnswerResponses);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_viewAnswerResponses);
        support = new Support();

        if(backupData.equals("") &&!connectionLost &&!onLoad){
            new ViewAnswerTask().execute();
        }
        else if(connectionLost){ // Can't connect to server.
            retry.setVisibility(View.VISIBLE);
        }
        else if(onLoad && backupData.equals("")){ // On restore, AsyncTask on loading and not have  backup data
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(!(backupData.equals(""))){ // On restore, have backup data
            answerList.remove(answerList.size() - 1);//remove  progress bar
            adapter.notifyItemRemoved(answerList.size());
            List<ViewAnswerBean> tempList = getAnswerListFromJsonString(backupData);
            for(ViewAnswerBean bean:tempList){
                answerList.add(bean);
            }
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
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
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("backupData", backupData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_view_answer_responses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == 16908332){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onViewAnswerTaskCallback(String result){ Log.i(TAG, "onViewAnswerTaskCallback");
        if(!(result.equals("connectionLost"))){
            answerList.remove(answerList.size() - 1);//remove  progress bar
            adapter.notifyItemRemoved(answerList.size());
            List<ViewAnswerBean> tempList = getAnswerListFromJsonString(result);
            for(ViewAnswerBean bean:tempList){
                answerList.add(bean);
            }
            adapter.notifyDataSetChanged();
            backupData = getJsonStringFromAnswerList(answerList);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            support.showSnackBarNotConnectToServer(rootLayout);
        }

    }
    public List<ViewAnswerBean> getAnswerListFromJsonString (String jsonString){
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<ViewAnswerBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromAnswerList(List<ViewAnswerBean> answerList){
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(answerList);
        return gson.toJson(element);
    }
    private class ViewAnswerTask extends AsyncTask<String, Void, String>{
        private String TAG  = ViewAnswerTask.class.getSimpleName();

        public ViewAnswerTask() {
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            RequestViewAnswerBean bean = new RequestViewAnswerBean();
            bean.setAnswerBy(answerBy);
            bean.setAccountID(accountID);
            bean.setAnswerDate(answerDate);
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),"ViewAnswer",new Gson().toJson(bean));
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE);
            onViewAnswerTaskCallback(result);
        }
    }
}
