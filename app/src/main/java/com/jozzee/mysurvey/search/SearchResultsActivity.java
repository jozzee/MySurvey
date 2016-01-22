package com.jozzee.mysurvey.search;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.adpter.OnLoadMoreListener;
import com.jozzee.mysurvey.adpter.RecyclerItemClickListener;
import com.jozzee.mysurvey.adpter.SurveyAdapter;
import com.jozzee.mysurvey.bean.ResultSearchSurvey;
import com.jozzee.mysurvey.bean.SearchBean;
import com.jozzee.mysurvey.bean.SurveyBean;
import com.jozzee.mysurvey.dosurvey.DoSurveyActivity;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private static String TAG = SearchResultsActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private String query;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SurveyAdapter adapter;
    private List<SurveyBean> surveyList;
    private ImageView retry;
    private RelativeLayout layoutNotMatchAnySurvey;
    private ProgressBar progressBar;
    private String backupData = "";
    private boolean allSurvey = false;
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private boolean notMatchAnySurvey = false;
    private int typeOffRecycleView = 0;
    private SurveyBean surveyBean;
    private Support support;
    private Gson gson;
    private ManageJson manageJson;
    private int accountID;
    private String nameGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("LOG", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        accountID = sharedPreferences.getInt("accountID", 1);
        setContentView(R.layout.activity_search_results);
        handleIntent(getIntent());

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_searchResult);  //set RootLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar_searchResult); //Set Toolbar replace Actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(query);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_searchResult);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        surveyList = new ArrayList<SurveyBean>();
        surveyList.add(null);
        adapter = new SurveyAdapter(surveyList, this, recyclerView, typeOffRecycleView,true);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        retry = (ImageView)findViewById(R.id.imageView_retry_searchResult);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_searchResult);
        layoutNotMatchAnySurvey = (RelativeLayout)findViewById(R.id.relativeLayout_notHaveSurvey_searchResult);

        support = new Support();
        gson = new Gson();
        manageJson = new ManageJson();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {
                Log.i(TAG, "on click item RecycleView, " + "position = " + String.valueOf(position));

                surveyBean = surveyList.get(position);
                if(surveyBean.getNumberOfQuestions()>= 1){
                    if(accountID == 1){ //this guest
                        dialogDoSurveyForGuest();
                    }
                    else { //this member
                        new CheckDoRepeatSurvey().execute(
                                String.valueOf(surveyBean.getSurveyID()),String.valueOf(surveyBean.getSurveyVersion()));
                    }
                }
                else{
                    support.showSnackBarNoQuestion(rootLayout);
                }
            }
        }));

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore");

                if (!allSurvey) {
                    Log.i(TAG, "allSurvey false");
                    String rowStart = String.valueOf(surveyList.size() + 1);
                    String rowEnd = String.valueOf(surveyList.size() + 20);
                    Log.i(TAG, "rowStart = " + rowStart + ", rowEnd" + rowEnd);

                    if (support.checkNetworkConnection(getContextFromActivity())) {
                        surveyList.add(null);
                        adapter.notifyItemInserted(surveyList.size() - 1);
                        //new RequestSurveyFormTask(true).execute("1",rowStart, rowEnd);
                        new SearchTask(true).execute(rowStart, rowEnd);
                    } else {
                        showSnackBarNotConnectInternet(rootLayout);
                        adapter.setLoaded();
                    }

                } else {
                    support.showSnackBarAllSurvey(rootLayout);
                    adapter.setLoaded();
                }
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchTask(false).execute("1","20");
            }
        });
        if(backupData.equals("") &&!(connectionLost) &&!(onLoad) &&!(notMatchAnySurvey)){
            new SearchTask(false).execute("1","20");
        }
        else if(connectionLost){
            if(backupData.equals("")){
                retry.setVisibility(View.VISIBLE);
            }
            else{
                surveyList = manageJson.getSurveyListFromJsonString(backupData);
                adapter.setSurveyList(surveyList);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
        else if(onLoad && backupData.equals("")){ //Log.i(TAG, "Downloading and not have backup data");
            progressBar.setVisibility(View.VISIBLE); //Log.e(TAG,"set Visibility VISIBLE on progressBar");
        }
        else if(onLoad && !backupData.equals("")){  //Log.i(TAG,"Downloading and  have backup data");
            surveyList = manageJson.getSurveyListFromJsonString(backupData);
            surveyList.add(null);
            adapter.setSurveyList(surveyList);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        }
        else if(notMatchAnySurvey){
            recyclerView.setVisibility(View.GONE);
            layoutNotMatchAnySurvey.setVisibility(View.VISIBLE);
        }
        else if(!backupData.equals("")){ //Log.i(TAG,"have backup data ,not downloading, have connect server");
            surveyList = manageJson.getSurveyListFromJsonString(backupData);
            adapter.setSurveyList(surveyList);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        }

    }
    @Override
    protected void onStart(){ Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume(){ Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
    }

    @Override
    protected void onPause() {  Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.
    }

    @Override
    protected void onStop(){ Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart(){ Log.i(TAG, "onRestart");
        super.onRestart();
    }
    @Override
    protected void onDestroy(){ Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {  Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();  Log.i(TAG,"id = " +id);
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == 16908332){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
        }
    }
    public void onSearchTaskCallback(String result){
        if(!(result.equals("connectionLost"))){
            ResultSearchSurvey bean = new ResultSearchSurvey(result);

            if(bean.isNotMathAnySurvey()){
                allSurvey = true;
                notMatchAnySurvey = true;
                layoutNotMatchAnySurvey.setVisibility(View.VISIBLE);
            }
            else{
                if(bean.isAllSurvey()){
                    allSurvey = true;
                }
                List<SurveyBean> tempList = bean.getSurveyList();
                surveyList.remove(surveyList.size()-1);//remove  progress bar
                adapter.notifyItemRemoved(surveyList.size());
                for(SurveyBean tempBean:tempList){
                    surveyList.add(tempBean);
                }
                adapter.notifyDataSetChanged(); //set new render adapter
                adapter.setLoaded();
                layoutNotMatchAnySurvey.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                backupData = manageJson.getJsonStringFromSurveyList(surveyList); // backup data
                notMatchAnySurvey = false;

            }
        }
        else{
            retry.setVisibility(View.VISIBLE);
            support.showSnackBarNotConnectToServer(rootLayout);
        }
    }
    public void dialogDoSurveyForGuest(){
        new MaterialDialog.Builder(this)
                .title("Enter Your Name.")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Your Name", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        nameGuest = charSequence.toString();
                        if (surveyBean.getSurveyType().equals("password")) {
                            dialogPassword();
                        } else {
                            startDoSurvey();
                        }
                    }
                })
                .negativeText("Cancel")
                .show();
    }
    public void dialogPassword(){
        new MaterialDialog.Builder(this)
                .title("Enter Password to Test Survey")
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("Password", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        new CheckPasswordTask().execute(
                                new ManageJson().serializationStringToJson("password," + charSequence.toString()
                                        , "surveyID," + surveyBean.getSurveyID(), "surveyVersion," + surveyBean.getSurveyVersion()));
                        materialDialog.dismiss();
                    }
                })
                .negativeText("Cancel")
                .show();
    }
    private class SearchTask extends AsyncTask <String,Void,String>{
        private String TAG  = SearchTask.class.getSimpleName();

        private boolean onLoadMore = false;

        public SearchTask(boolean onLoadMore) {
            this.onLoadMore = onLoadMore;
        }
        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            if(onLoadMore){
                progressBar.setVisibility(View.GONE);
            }
            else{
                support.setProgressBarColor(progressBar,support.accentClolr());
                progressBar.setVisibility(View.VISIBLE);
            }
            retry.setVisibility(View.GONE);
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");

            SearchBean bean = new SearchBean(accountID,Integer.parseInt(params[0]),Integer.parseInt(params[1]),query);
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),
                    "SearchSurvey",new Gson().toJson(bean));
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressBar.setVisibility(View.GONE);
            onSearchTaskCallback(result);

        }
    }
    private class CheckPasswordTask extends AsyncTask<String,Void,String> {
        private String TAG  = CheckPasswordTask.class.getSimpleName();

        private ProgressDialog progressDialog;

        public CheckPasswordTask() {

        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("In process...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),"CheckSurveyPassword",params[0]);
        }

        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost"))){
                if(result.equals("1")){
                    startDoSurvey();
                }
                else{
                    new MaterialDialog.Builder(getContextFromActivity())
                            .content("Incorrect password")
                            .positiveText("OK")
                            .show();
                }
            }
            else{
                support.showSnackBarNotConnectToServer(rootLayout);
            }
        }
    }
    private class CheckDoRepeatSurvey extends AsyncTask<String, Void, String> {
        private String TAG  = CheckDoRepeatSurvey.class.getSimpleName();

        private ProgressDialog progressDialog;


        public  CheckDoRepeatSurvey() {

        }
        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");

            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("in process...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            String url = new Support().getURLLink() +"?command=CheckDoRepeatSurvey&accountID=" +accountID
                    +"&surveyID="+params[0] +"&surveyVersion="+params[1];
            return new ConnectionServiceCore().doOKHttpGetString(url);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost")) && (result.equals("1"))){
                if(surveyBean.getSurveyType().equals("password")){
                    dialogPassword();
                }
                else {
                    startDoSurvey();
                    }
                }
            else if(!(result.equals("connectionLost")) && (result.equals("0"))){
                support.showSnackBarMadeSurveyView(rootLayout);
            }
            else if((result.equals("connectionLost"))){
                support.showSnackBarNotConnectToServer(rootLayout);
            }


        }
    }
    public void startDoSurvey(){
        Intent doSurveyIntent = new Intent(this, DoSurveyActivity.class);
        if(nameGuest != null){
            doSurveyIntent.putExtra("nameGuest", nameGuest);
        }
        doSurveyIntent.putExtra("surveyBean", new Gson().toJson(surveyBean));
        startActivity(doSurveyIntent);
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
    public Context getContextFromActivity(){
        return this;
    }
}
