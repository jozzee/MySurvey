package com.jozzee.mysurvey.create;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.SaveSurveyStatusBean;
import com.jozzee.mysurvey.edit.EditQuestionActivity;
import com.jozzee.mysurvey.event.ActivityResultBus;
import com.jozzee.mysurvey.event.AddQuestionsEvent;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnActivityResultEvent;
import com.jozzee.mysurvey.mysurvey.MySurveyActivity;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;

public class CreateSurveyActivity extends AppCompatActivity {

    private static String TAG = CreateSurveyActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private int accountID;
    private int surveyStatus;
    private int surveyID;
    private int surveyVersion;
    private String surveyName;
    private boolean createQuestionIsCurrentFragment = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("LOG", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        accountID = sharedPreferences.getInt("accountID", 0);
        Log.i(TAG, String.valueOf(accountID));
        setContentView(R.layout.activity_create_survey);
        if(savedInstanceState != null){
            Log.e(TAG,"savedInstanceState != null");
            accountID = savedInstanceState.getInt("accountID",0);  Log.i(TAG,"accountID = " +String.valueOf(accountID));
            surveyStatus = savedInstanceState.getInt("surveyStatus"); Log.i(TAG,"surveyStatus = " +String.valueOf(surveyStatus));
            surveyID = savedInstanceState.getInt("surveyID"); Log.i(TAG,"surveyID = " +String.valueOf(surveyID));
            surveyVersion = savedInstanceState.getInt("surveyVersion"); Log.i(TAG,"surveyVersion = " +String.valueOf(surveyVersion));
            surveyName = savedInstanceState.getString("surveyName");  Log.i(TAG,"surveyName = " +surveyName);
            createQuestionIsCurrentFragment = savedInstanceState.getBoolean("createQuestionIsCurrentFragment",false); Log.i(TAG,"createQuestionIsCurrentFragment = " +String.valueOf(createQuestionIsCurrentFragment));

        }
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_createSurvey);  //set RootLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar_createSurvey); //Set Toolbar replace Actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create your Survey");
        if(createQuestionIsCurrentFragment){
            Log.e(TAG, "createQuestionIsCurrentFragment = true");
            getSupportActionBar().setTitle(surveyName);
            //CreateQuestionsFragment createQuestionsFragment = new CreateQuestionsFragment().newInstance(surveyID, surveyVersion);
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.replace(R.id.fragment_container_createSurvey, createQuestionsFragment);
            //transaction.commit();
        }
        else{
            createQuestionIsCurrentFragment = false;
            CreateFormFragment createFormFragment = new CreateFormFragment().newInstance(accountID);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container_createSurvey, createFormFragment);
            transaction.commit();
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_save_survey) {
                    //save questions, status to server
                    Log.i(TAG,"surveyStatus = " +surveyStatus);
                    if (surveyStatus == 2) {
                        SaveSurveyStatusBean bean = new SaveSurveyStatusBean(surveyID, surveyVersion, surveyStatus);
                        String requestData = new Gson().toJson(bean);
                        new SaveSurveyTask(getContextActivity()).execute(requestData);
                    } else {
                        Log.e(TAG, "Go to Activity MySurvey");
                        onBackPressed();
                    }
                }
                return false;
            }
        });
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
        outState.putInt("accountID",accountID);
        outState.putInt("surveyStatus",surveyStatus);
        outState.putInt("surveyID",surveyID);
        outState.putInt("surveyVersion", surveyVersion);
        outState.putString("surveyName", surveyName);
        outState.putBoolean("createQuestionIsCurrentFragment", createQuestionIsCurrentFragment);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        if(createQuestionIsCurrentFragment){
            getMenuInflater().inflate(R.menu.menu_create_survey_save, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.menu_create_survey, menu);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");

        int id = item.getItemId(); Log.i(TAG,"id = " +id);
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == 16908332){
            new AlertDialogWrapper.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.messageDetailExitCreateSurvey)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult from activity");
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(new OnActivityResultEvent(requestCode, resultCode, data));
    }
    @Subscribe
    public void onAddQuestions(AddQuestionsEvent event){
        Log.i(TAG, "onAddQuestions");
        surveyStatus = event.getSurveyStatus();
        surveyID = event.getSurveyID();
        surveyVersion = event.getSurveyVersion();
        surveyName = event.getSurveyName();
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_create_survey_save);
        getSupportActionBar().setTitle(surveyName);
        createQuestionIsCurrentFragment = true; Log.e(TAG,"createQuestionIsCurrentFragment = " +String.valueOf(createQuestionIsCurrentFragment));
        CreateQuestionsFragment createQuestionsFragment = new CreateQuestionsFragment().newInstance(surveyID, surveyVersion);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_createSurvey, createQuestionsFragment);
        transaction.commit();
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    public void onSaveSurveyTaskCallback(String result){
        if(!(result.equals("connectionLost"))){
            SaveSurveyStatusBean bean = new SaveSurveyStatusBean(result);
            String lastModifyDate = bean.getLastModifyDate();
            int numberOffQuestion = bean.getNumberOffQuestions();
            onBackPressed();
        }
        else{
            Snackbar.make(rootLayout, "Not connect to Server", Snackbar.LENGTH_LONG).show();
        }
       /* if(result.equals("success")){
            onBackPressed();
        }
        else if(result.equals("connectionLost")){
            Snackbar.make(rootLayout, "Not connect to Server", Snackbar.LENGTH_LONG).show();
        }*/
    }
    private class SaveSurveyTask  extends AsyncTask<String, Void, String> { // save surveyStatus to database
        private String TAG = SaveSurveyTask.class.getSimpleName();
        private ProgressDialog progressDialog;
        Context context;

        public SaveSurveyTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Save Survey");
            progressDialog.setMessage("Saving....");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            String result = "";
            String url = new Support().getURLLink();
            String command = "SaveStatusSurvey";
            String requestData = params[0];
            result = new ConnectionServiceCore().doOKHttpPostString(url, command, requestData);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onSaveSurveyTaskCallback(result);


        }
    }
   public Context getContextActivity(){
       return this;
   }

}
