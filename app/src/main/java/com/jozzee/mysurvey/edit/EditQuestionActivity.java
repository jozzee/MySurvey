package com.jozzee.mysurvey.edit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.QuestionsBean;
import com.jozzee.mysurvey.bean.SaveSomethingBean;
import com.jozzee.mysurvey.bean.SaveSurveyStatusBean;
import com.jozzee.mysurvey.bean.SaveWithNewVersionBean;
import com.jozzee.mysurvey.create.ManageQuestionActivity;
import com.jozzee.mysurvey.adpter.QuestionAdapter;
import com.jozzee.mysurvey.adpter.RecyclerItemClickListener;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnAfterEditQuestionEvent;
import com.jozzee.mysurvey.event.OnSendQuestionEvent;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EditQuestionActivity extends AppCompatActivity {
    private static String TAG = EditQuestionActivity.class.getSimpleName();


    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private Bundle bundle;
    private int surveyID;
    private int surveyVersion;
    private String surveyName;
    private boolean editWithNew = false;
    private int surveyStatus;
    private ImageView retry;
    private ProgressBar progressBar;
    private TextView notHaveQuestions;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private QuestionAdapter adapter;
    private List<QuestionsBean> questionList;
    private FloatingActionButton floatingActionButton;
    private String questionListAsJsonString = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private boolean visibleRecyclerView = false;
    private boolean visibleRetry = false;
    private boolean visibleProgressBar = false;
    private boolean onBackPressedFromAction = false;
    private int surveyVersionAfterEditQuestion;
    private int numberOffQuestionAfterEditQuestion;
    private String lastUpdateAfterEditQuestion;
    private boolean newVersion = false;
    private String link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        if(savedInstanceState != null){
            questionListAsJsonString = savedInstanceState.getString("backupData","");
            visibleRecyclerView = savedInstanceState.getBoolean("visibleRecyclerView",false);
            visibleRetry = savedInstanceState.getBoolean("visibleRetry", false);
            visibleProgressBar = savedInstanceState.getBoolean("visibleProgressBar",false);
        }
        bundle = getIntent().getExtras();
        if(bundle != null) {
            surveyID = (Integer)bundle.get("surveyID"); Log.i(TAG,"surveyID = " +surveyID);
            surveyVersion = (Integer)bundle.get("surveyVersion"); Log.i(TAG,"surveyVersion = " +surveyVersion);
            surveyName = (String)bundle.get("surveyName"); Log.i(TAG,"surveyName = " +surveyName);
            surveyStatus = bundle.getInt("surveyStatus",1); Log.i(TAG,"surveyStatus = " +surveyStatus);
            if(bundle.get("editWithNew") != null){
                editWithNew = (boolean)bundle.get("editWithNew"); Log.i(TAG,"editWithNew = " +String.valueOf(editWithNew));
            }

        }

        toolbar = (Toolbar)findViewById(R.id.toolbar_editQuestion);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(surveyName);
        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_editQuestion);
        retry = (ImageView)findViewById(R.id.imageView_retry_editQuestion);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_editQuestion);
        notHaveQuestions = (TextView)findViewById(R.id.textView_notHaveQuestion_editQuestion);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fabBtn_editQuestion);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_editQuestion);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        questionList = new ArrayList<QuestionsBean>();
        questionList.add(null);
        adapter = new QuestionAdapter(questionList,recyclerView,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onClick item in RecycleView, " + "position = " + String.valueOf(position));
                if(!(questionList.get(position) == null)){
                    Intent manageQuestion = new Intent(view.getContext(), ManageQuestionActivity.class);
                    manageQuestion.putExtra("action", "update");
                    manageQuestion.putExtra("questionsBean", new Gson().toJson(questionList.get(position)));
                    if (editWithNew) {
                        manageQuestion.putExtra("editWithNew", true);
                    }
                    startActivity(manageQuestion);
                }
            }
        }));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick floatingActionButton");
                Intent manageQuestion = new Intent(v.getContext(), ManageQuestionActivity.class);
                manageQuestion.putExtra("action", "add");
                manageQuestion.putExtra("surveyID", surveyID);
                manageQuestion.putExtra("surveyVersion", surveyVersion);
                if (editWithNew) {
                    manageQuestion.putExtra("editWithNew", true);
                }
                startActivity(manageQuestion);
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionLost = false;
                questionListAsJsonString = "";
                if(checkNetworkConnection()){//check connect internet (wifi or service)
                    new RequestQuestion().execute();
                }
                else{
                    retry.setVisibility(View.VISIBLE);
                    Snackbar.make(rootLayout, "Not connect internet", Snackbar.LENGTH_LONG)
                            .setAction("Setting", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                }
                            }).show();
                }
            }
        });
        //-----------------------------------------------
        //retire view
        if(visibleRecyclerView){
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else if(visibleRetry){
            retry.setVisibility(View.VISIBLE);
        }
        else if(visibleProgressBar){
            progressBar.setVisibility(View.VISIBLE);
        }
        //------------------------------------------------------

        if (questionListAsJsonString.equals("") && !(connectionLost) &&!(onLoad)){Log.i(TAG, "Not have backup data, have connect server, and not downloading data from server");

            if(checkNetworkConnection()){//check connect internet (wifi or service)
                new RequestQuestion().execute();
            }
            else{
                retry.setVisibility(View.VISIBLE);
                Snackbar.make(rootLayout, "Not connect internet", Snackbar.LENGTH_LONG)
                        .setAction("Setting", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                            }
                        }).show();
            }
        }
        else if(connectionLost){ Log.i(TAG, "Not connect to server");
            retry.setVisibility(View.VISIBLE);
        }
        else if(onLoad && questionListAsJsonString.equals("")){ Log.i(TAG,"Downloading and not have backup data");
           progressBar.setVisibility(View.VISIBLE);
        }
        else if(onLoad && !questionListAsJsonString.equals("")){  Log.i(TAG,"Downloading and  have backup data");
            //but this activity not load more
            questionList = getQuestionListFromJsonString(questionListAsJsonString);
            adapter.setQuestionList(questionList);
            adapter.notifyDataSetChanged();
        }
        else if(!questionListAsJsonString.equals("")){ Log.i(TAG,"have backup data ,not downloading, have connect server");
            questionList = getQuestionListFromJsonString(questionListAsJsonString);
            adapter.setQuestionList(questionList);
            adapter.notifyDataSetChanged();
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
        if(onBackPressedFromAction){
            onBackPressedFromAction = false;
            new AfterEditQuestionTask().execute();
        }
        //new SendNumberQuestionTask().execute(questionList.size());
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
        super.onSaveInstanceState(outState);
        outState.putString("backupData", questionListAsJsonString);
        if(recyclerView.getVisibility() == View.VISIBLE){
            outState.getBoolean("visibleRecyclerView",true);
        }
        else {
            if(retry.getVisibility() == View.VISIBLE){
                outState.putBoolean("visibleRetry",true);
            }
            if(progressBar.getVisibility() == View.VISIBLE){
                outState.putBoolean("visibleProgressBar",true);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.e(TAG,"ID = "+id); Log.e(TAG,"action_save_editQuestion = "+R.id.action_save_editQuestion);


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
                            if(surveyStatus == 2){
                                SaveSurveyStatusBean bean = new SaveSurveyStatusBean();
                                bean.setSurveyID(surveyID);
                                bean.setSurveyVersion(surveyVersion);
                                bean.setSurveyStatus(surveyStatus);
                                new SaveSurveyStatus().execute(new Gson().toJson(bean));
                            }
                            onBackPressed();
                        }
                    }).show();
            return true;
        }
        if(id == R.id.action_save_editQuestion){
            Log.e(TAG,"Save Edit Questions"); Log.e(TAG,"surveyStatus = " +surveyStatus);
            if(editWithNew){
                String questionListAsJsonString = getJsonStringFromQuestionList(questionList);
                SaveWithNewVersionBean bean = new SaveWithNewVersionBean(surveyID,surveyVersion,surveyStatus,questionListAsJsonString);
                new SaveWithNewVersionTask().execute(new Gson().toJson(bean));

            }
            else{
                SaveSomethingBean bean = new  SaveSomethingBean(surveyID, surveyVersion, surveyStatus);
                String requestData = new Gson().toJson(bean);
                new SaveEditQuestionTask().execute(requestData);
            }


            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Subscribe
    public void onReceivedQuestionEvent(OnSendQuestionEvent event){
        Log.i(TAG, "onReceivedQuestionEvent");
        QuestionsBean bean = event.getQuestionsBean();
        String action = bean.getAction();
        Log.i(TAG, "action = " + action);

        if(action.equals("add")){
            if(questionList.get(0) == null){
                questionList.remove((questionList.size() - 1));
                adapter.notifyDataSetChanged();
                questionList.add(bean);
                adapter.notifyDataSetChanged();
            }
            else{
                questionList.add(bean);
                adapter.notifyDataSetChanged();

            }
            notHaveQuestions.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else if(action.equals("update")){
            questionList.set((bean.getQuestionNumber() - 1), bean);
            adapter.notifyDataSetChanged();

        }
        else if(action.equals("delete")){
            int sizeQuestionList = questionList.size(); Log.i(TAG,"sizeQuestionList = " +sizeQuestionList);
            int questionNumberToDelete = bean.getQuestionNumber();  Log.i(TAG,"questionNumberToDelete = " +questionNumberToDelete);
            for(int i = 0 ;i<sizeQuestionList;i++){
                QuestionsBean tempBean = new QuestionsBean();
                tempBean = questionList.get(i);
                int questionNumberOffTempBean = tempBean.getQuestionNumber(); Log.i(TAG,"questionNumberOffTempBean = " +questionNumberOffTempBean);
                if(questionNumberOffTempBean > questionNumberToDelete){
                    Log.i(TAG,"updateQuestionNumber from no. " +tempBean.getQuestionNumber() +" to no. " +i);
                    tempBean.setQuestionNumber(i);
                    questionList.set(i,tempBean);
                }
            }
            questionList.remove((bean.getQuestionNumber()-1));
            adapter.notifyDataSetChanged();
            if(questionList.isEmpty()){
                questionList.add(null);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                notHaveQuestions.setVisibility(View.VISIBLE);
            }
        }

    }
    public void onRequestQuestionCallback(String result){ Log.i(TAG, "onRequestQuestionCallback");
        if(!(result.equals("connectionLost"))){
            if(!(result.equals("0"))){
                questionList = getQuestionListFromJsonString(result);
                //---------------------------------
                QuestionsBean test = questionList.get(0);
                Log.e(TAG,"questionID = "+test.getQuestionID());
                Log.e(TAG,"question = "+test.getQuestion());
                Log.e(TAG,"answerType = "+test.getAnswerType());
                //Log.e(TAG, "choiceAsJsonString = " + test.getChoiceAsJsonString());
                //==================================
                adapter.setQuestionList(questionList);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            }
            else{
                notHaveQuestions.setVisibility(View.VISIBLE);
            }

        }
        else{
            recyclerView.setVisibility(View.GONE);
            retry.setVisibility(View.VISIBLE);
            Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
        }

    }
    public void onSaveWithNewVersionTaskCallback(String result){ Log.e(TAG, "onSaveWithNewVersionTaskCallback");
        if(!(result.equals("connectionLost"))){
            JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
            surveyVersionAfterEditQuestion = jsonObject.get("surveyVersion").getAsInt(); Log.e(TAG,"surveyVersionAfterEditQuestion = " +surveyVersionAfterEditQuestion);
            lastUpdateAfterEditQuestion = jsonObject.get("lastModifyDate").getAsString(); Log.e(TAG,"lastUpdateAfterEditQuestion = " +lastUpdateAfterEditQuestion);
            numberOffQuestionAfterEditQuestion = jsonObject.get("numberOffQuestion").getAsInt(); Log.e(TAG,"numberOffQuestionAfterEditQuestion = " +numberOffQuestionAfterEditQuestion);
            link = jsonObject.get("link").getAsString(); Log.e(TAG,"link = " +link);
            onBackPressedFromAction = true;
            newVersion =true;
            onBackPressed();
        }
        else{
            Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
        }
    }
    public void onSaveEditQuestionTaskCallback(String result){ Log.e(TAG,"onSaveEditQuestionTaskCallback");
        if(!(result.equals("connectionLost"))){
            SaveSomethingBean bean = new SaveSomethingBean(result);
            lastUpdateAfterEditQuestion= bean.getLastUpdate(); Log.e(TAG,"lastUpdateAfterEditQuestion = " +lastUpdateAfterEditQuestion);
            numberOffQuestionAfterEditQuestion = bean.getNumberOffQuestions(); Log.e(TAG,"numberOffQuestionAfterEditQuestion = " +numberOffQuestionAfterEditQuestion);
            surveyVersionAfterEditQuestion = surveyVersion;
            onBackPressedFromAction = true;
            newVersion = false;
            onBackPressed();

        }
        else{
            Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
        }

    }
    public void onSaveSurveyStatusCallback(String result){
        if(retry.equals("success")){
            onBackPressed();
        }
        else{
            Snackbar.make(rootLayout, "Not connect to Server", Snackbar.LENGTH_LONG).show();
            onBackPressed();
        }

    }
    private class AfterEditQuestionTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "AfterEditQuestionTask");
                    BusProvider.getInstance().post(
                            new OnAfterEditQuestionEvent(lastUpdateAfterEditQuestion
                                    , numberOffQuestionAfterEditQuestion
                                    , surveyVersionAfterEditQuestion
                                    , newVersion
                                    , link));
                }
            });
            return null;
        }
    }
    private class RequestQuestion extends AsyncTask<String,Void,String>{
        private String TAG  = RequestQuestion.class.getSimpleName();

        public RequestQuestion() {
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            retry.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            String url = new Support().getURLLink()
                    +"?command=RequestQuestions&surveyID=" +surveyID +"&surveyVersion=" +surveyVersion;
            if(editWithNew){
                url = url+"&editWithNew="+String.valueOf(editWithNew);
            }
            Log.i(TAG, "URL = " + url);
            return new ConnectionServiceCore().doOKHttpGetString(url);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE);
            onRequestQuestionCallback(result);
        }
    }
    private class SaveEditQuestionTask  extends AsyncTask<String, Void, String> { // save surveyStatus to database
        private String TAG = SaveEditQuestionTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public SaveEditQuestionTask() {
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("Saving...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            String result = "";
            String url = new Support().getURLLink();
            String command = "SaveSomething";
            String requestData = params[0];
            result = new ConnectionServiceCore().doOKHttpPostString(url, command, requestData);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onSaveEditQuestionTaskCallback(result);


        }
    }
    private class SaveWithNewVersionTask extends AsyncTask<String,Void,String>{
        private String TAG  = SaveWithNewVersionTask.class.getSimpleName();

        private ProgressDialog progressDialog;

        public SaveWithNewVersionTask() {
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("Saving...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            String url = new Support().getURLLink();
            String command = "SaveWithNewVersion";
            String requestData = params[0];
            return new ConnectionServiceCore().doOKHttpPostString(url,command,requestData);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
           progressDialog.dismiss();
            onSaveWithNewVersionTaskCallback(result);
        }
    }
    public class SaveSurveyStatus  extends AsyncTask<String,Void,String>{
        private String TAG  = SaveSurveyStatus.class.getSimpleName();

        private ProgressDialog progressDialog;

        public SaveSurveyStatus() {
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("Saving...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            String url = new Support().getURLLink();
            String command = "SaveSurveyStatus";
            String requestData = params[0];
            return new ConnectionServiceCore().doOKHttpPostString(url,command,requestData);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressDialog.dismiss();
            onSaveSurveyStatusCallback(result);
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
    public List<QuestionsBean> getQuestionListFromJsonString (String questionListAsJsonString){
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(questionListAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<QuestionsBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromQuestionList(List<QuestionsBean> questionList){
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(questionList);
        return gson.toJson(element);
    }
    public Context getContextFromActivity(){
        return this;
    }
}
