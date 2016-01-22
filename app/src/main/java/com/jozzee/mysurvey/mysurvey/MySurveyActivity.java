package com.jozzee.mysurvey.mysurvey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ResultRequestMySurveyBean;
import com.jozzee.mysurvey.bean.SurveyBeanForMySurvey;
import com.jozzee.mysurvey.adpter.RecyclerItemClickListener;
import com.jozzee.mysurvey.edit.ViewSurveyActivity;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnBackPressedFromViewSurveyEvent;
import com.jozzee.mysurvey.adpter.OnLoadMoreListener;
import com.jozzee.mysurvey.adpter.MySurveyAdapter;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MySurveyActivity extends AppCompatActivity {
    private static String TAG = MySurveyActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MySurveyAdapter adapter;
    private ImageView retry;
    private RelativeLayout layoutNotSurvey;
    private ProgressBar progressBar;
    private List<SurveyBeanForMySurvey> mySurveyList;
    private String backupData = "";
    private boolean allSurvey = false;
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private boolean notHaveSurvey = false;
    private int accountID;
    private Support support;
    private Gson gson;
    private ManageJson manageJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {  Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("LOG", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        accountID = sharedPreferences.getInt("accountID", 0); Log.i(TAG, "accountID = " + accountID);

        setContentView(R.layout.activity_mysurvey);
        if(savedInstanceState != null){
            backupData = savedInstanceState.getString("backupData", "");
            connectionLost = savedInstanceState.getBoolean("connectionLost", false);
            allSurvey = savedInstanceState.getBoolean("allSurvey",false);
            onLoad = savedInstanceState.getBoolean("onLoad",false);
            notHaveSurvey = savedInstanceState.getBoolean("notHaveSurvey",false);
        }

        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_mySurvey);

        toolbar = (Toolbar)findViewById(R.id.toolbar_mySurvey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_mysurvey);

        retry = (ImageView)findViewById(R.id.imageView_retry_mySurvey);
        layoutNotSurvey = (RelativeLayout)findViewById(R.id.relativeLayout_notHaveSurvey_mySurvey);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_mySurvey);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_mySurvey);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mySurveyList = new ArrayList<SurveyBeanForMySurvey>();
        mySurveyList.add(null);
        adapter = new MySurveyAdapter(mySurveyList, recyclerView, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        gson = new Gson();
        support = new Support();
        manageJson = new ManageJson();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick RecycleView, " + "position = " + String.valueOf(position));
                SurveyBeanForMySurvey bean = mySurveyList.get(position);
                Intent viewSurveyIntent = new Intent(view.getContext(), ViewSurveyActivity.class);
                viewSurveyIntent.putExtra("survey", new Gson().toJson(bean));
                viewSurveyIntent.putExtra("position", position);
                startActivity(viewSurveyIntent);

            }
        }));
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!allSurvey) {
                    String rowStart = String.valueOf(mySurveyList.size() + 1);
                    String rowEnd = String.valueOf(mySurveyList.size() + 20);
                    Log.i(TAG, "rowStart = " + rowStart + ", rowEnd" + rowEnd);
                    mySurveyList.add(null);
                    adapter.notifyItemInserted(mySurveyList.size() - 1);
                    new RequestMySurveyTask(true).execute(rowStart, rowEnd);
                    adapter.setLoaded();
                } else {
                    support.showSnackBarAllSurvey(rootLayout);
                }
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "onClick retry");
                onRetry();
            }
        });

        //--------------------------------------------------------------------------
        if(backupData.equals("") && !(connectionLost) && !(onLoad)) {
            Log.i(TAG, "Not have backup data, have connect server, and not downloading data from server");
            if(support.checkNetworkConnection(this)){//check connect internet (wifi or service)
                new RequestMySurveyTask(false).execute("1", "20");
            }
            else{
                retry.setVisibility(View.VISIBLE);
                showSnackBarNotConnectInternet(rootLayout);
            }
        }
        else if(connectionLost){ Log.i(TAG, "Not connect to server");
            retry.setVisibility(View.VISIBLE);
        }
        else if(onLoad && backupData.equals("")){ Log.i(TAG,"Downloading and not have backup data");
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(onLoad && !backupData.equals("")){  Log.i(TAG,"Downloading and  have backup data");
            mySurveyList = manageJson.getMySurveyListFromJsonString(backupData);
            adapter = new MySurveyAdapter(mySurveyList, recyclerView, this);
            recyclerView.setAdapter(adapter);
            mySurveyList.add(null);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
        else if(!backupData.equals("")){ Log.i(TAG,"have backup data ,not downloading, have connect server");
            mySurveyList = manageJson.getMySurveyListFromJsonString(backupData);
            adapter = new MySurveyAdapter(mySurveyList, recyclerView, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else if(notHaveSurvey){
            layoutNotSurvey.setVisibility(View.VISIBLE);
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
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        outState.putString("backupData", backupData);
        outState.putBoolean("connectionLost", connectionLost);
        outState.putBoolean("onLoad", onLoad);
        outState.putBoolean("allSurvey", allSurvey);
        outState.putBoolean("notHaveSurvey", notHaveSurvey);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_mysurvey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.i(TAG, "id menu = " + id);
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == 16908332){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Subscribe
    public void onBackPressedFromViewSurvey(OnBackPressedFromViewSurveyEvent event){ Log.i(TAG, "onBackPressedFromViewSurvey");
        if(event.getAction().equals("update")){
            mySurveyList.set(event.getPosition(), event.getSurveyBeanForMySurvey());
            //adapter.setMySurveyList(mySurveyList);
            adapter.notifyDataSetChanged();
        }
        else if(event.getAction().equals("delete")){
            mySurveyList.remove(event.getPosition());
            adapter.notifyDataSetChanged();
        }


    }
    public void onRetry(){
        Log.i(TAG, "onRetry");
        connectionLost = false;
        onLoad = false;
        allSurvey = false;
        mySurveyList = new ArrayList<>();
        mySurveyList.add(null);
        adapter = new MySurveyAdapter(mySurveyList, recyclerView, this);
        recyclerView.setAdapter(adapter);
        new RequestMySurveyTask(false).execute("1", "20");
    }
    public void onRequestMySurveyTaskCallback(String result){ Log.i(TAG, "onRequestMySurveyTaskCallback");
        if(!(result.equals("connectionLost"))){

            ResultRequestMySurveyBean bean = new ResultRequestMySurveyBean(result);
            if(bean.isMySurveyListIsEmpty()){
                allSurvey = true;
                layoutNotSurvey.setVisibility(View.VISIBLE);

            }
            else{
                if(bean.isAllSurvey()){
                    allSurvey = true;
                }
                List<SurveyBeanForMySurvey> tempMySurveyList = bean.getMySurveyList();
                mySurveyList.remove(mySurveyList.size()-1);
                adapter.notifyItemRemoved(mySurveyList.size());
                for(SurveyBeanForMySurvey surveyBean:tempMySurveyList){
                    mySurveyList.add(surveyBean);
                }
                adapter.setLoaded();
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                backupData = manageJson.getJsonStringFromMySurveyList(mySurveyList);
                tempMySurveyList.clear();
            }
        }
        else {
            connectionLost = true;
            if(backupData.equals("")){
                retry.setVisibility(View.VISIBLE);
                support.showSnackBarNotConnectToServer(rootLayout);
            }
            else{
                mySurveyList.remove(mySurveyList.size() - 1);//remove  progress bar
                adapter.notifyItemRemoved(mySurveyList.size());
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
                support.showSnackBarNotConnectToServer(rootLayout);
            }
        }
    }
    public boolean checkNetworkConnection(){
        boolean networkConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            networkConnection = true;
        }
        else{
            networkConnection = false;
        }
        return networkConnection;
    }
    private class RequestMySurveyTask extends AsyncTask<String, Void, String>{
        private String TAG  = RequestMySurveyTask.class.getSimpleName();

        private boolean onLoadMore = false;

        public RequestMySurveyTask(boolean onLoadMore) {
            this.onLoadMore = onLoadMore;
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            retry.setVisibility(View.GONE);
            if(onLoadMore){
                progressBar.setVisibility(View.GONE);
            }
            else{
                progressBar.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpGetString(
                            support.getURLLink() +"?command=RequestMySurvey&accountID="
                            +accountID +"&rowStart=" +params[0] +"&rowEnd=" +params[1]);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE);
            onRequestMySurveyTaskCallback(result);
        }
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
