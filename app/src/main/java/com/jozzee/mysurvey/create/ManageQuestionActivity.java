package com.jozzee.mysurvey.create;

import android.app.Activity;
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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ChoiceBean;
import com.jozzee.mysurvey.bean.QuestionsBean;
import com.jozzee.mysurvey.bean.SendQuestionEvent;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnSendQuestionEvent;
import com.jozzee.mysurvey.event.SendQuestionBus;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Produce;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManageQuestionActivity extends AppCompatActivity {
    private static String TAG = ManageQuestionActivity.class.getSimpleName();
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private RadioGroup radioGroupSelectAnswerType;
    private LinearLayout choiceView;
    private List<EditText> choice;
    private String question;
    private EditText editTextQuestion;
    private int answerType = 1; //1 is text type, 2 is choice type.
    private QuestionsBean questionsBean;
    private SendQuestionEvent sendQuestionEvent;
    private String action;
    private LinearLayout deleteAndSaveLayout;
    private int surveyID;
    private int surveyVersion;
    private boolean onBackPressFromAction = false;
    private String oldQuestion;
    private Bundle bundle;
    private QuestionsBean oldQuestionBean;
    private boolean editWithNew = false;
    private TextInputLayout questionInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_question);
        bundle = getIntent().getExtras();
        if(bundle != null) {
            Log.i(TAG, "get action from bundle");
            action = (String)bundle.get("action");
            if(bundle.get("editWithNew") != null){
                editWithNew = bundle.getBoolean("editWithNew");
            }
        }

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_manageQuestion);  //set RootLayout
        toolbar = (Toolbar) findViewById(R.id.toolbar_manageQuestion); //Set Toolbar replace Actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(new Support().getNameManageQuestionTitle(action));

        choiceView = (LinearLayout)findViewById(R.id.choiceView);
        radioGroupSelectAnswerType = (RadioGroup)findViewById(R.id.selectTypeQuestion);
        editTextQuestion = (EditText)findViewById(R.id.editText_question_manageQuestion);
        questionInputLayout = (TextInputLayout)findViewById(R.id.TextInputLayout_question_manageQuestion);
        initEditTextQuestion();
        //---------------------------------------------------------------------------------------------------------------------
        if(action.equals("add")){
            Log.i(TAG, "Action Add");
            if(bundle != null){
                surveyID = (Integer)bundle.get("surveyID");
                surveyVersion = (Integer)bundle.get("surveyVersion");
                Log.i(TAG,"surveyID = " +String.valueOf(surveyID) +", surveyVersion = " +String.valueOf(surveyVersion));
            }
        }
        else if(action.equals("update")) {
            Log.i(TAG, "Action Update");
            if(bundle != null){
                questionsBean = new QuestionsBean((String)bundle.get("questionsBean")); //set values to questionBean;
                questionsBean.setAction("update");
                //create old question
                oldQuestionBean = new QuestionsBean();
                oldQuestionBean.setQuestion(questionsBean.getQuestion());
                oldQuestionBean.setAnswerType(questionsBean.getAnswerType());
                oldQuestionBean.setChoiceList(questionsBean.getChoiceList());
            }
            answerType = questionsBean.getAnswerType();
            setViewQuestion(answerType);

        }
        if(radioGroupSelectAnswerType.getCheckedRadioButtonId() == R.id.radio_textType){
            choiceView.setVisibility(View.GONE);
        }
        radioGroupSelectAnswerType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_textType) {
                    answerType = 1;
                    choiceView.setVisibility(View.GONE);
                    choiceView.removeAllViews();
                    //questionsBean.setChoiceAsJsonString(null);
                } else if (checkedId == R.id.radio_choiceType) {
                    answerType = 2;
                    choiceView.setVisibility(View.VISIBLE);
                    addChoice();
                }
            }
        });
    }

    @Override
    protected void onStart(){
        Log.i(TAG, "onStart");
        super.onStart();
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.

    }
    @Override
    protected void onResume(){
        Log.i(TAG, "onResume");
        super.onResume();
    }
    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    protected void onStop(){
        Log.i(TAG, "onStop");
        super.onStop();
        if(onBackPressFromAction){
            onBackPressFromAction = false;
            new SendQuestionTask().execute();
        }
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
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.        //SendQuestionBus.getInstance().unregister(this);
    }
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        if(action.equals("add")){
            getMenuInflater().inflate(R.menu.menu_manage_question, menu);

        }
        else if(action.equals("update")){
            getMenuInflater().inflate(R.menu.menu_manage_question_save, menu);
            toolbar.setNavigationIcon(R.drawable.ic_done_white_24dp);

        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.e(TAG, "id = " + id);

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == 16908332){
            if(action.equals("add")){
                onBackPressed();
            }
            else if(action.equals("update")){
                updateQuestion();
            }
        }
        else if(id == R.id.action_add_manageQuestion){
            addQuestion();
        }
        else if(id == R.id.action_delete_manageQuestion){
            deleteQuestion();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onManageQuestionTaskCallback(String result){
        Log.i(TAG, "onManageQuestionTaskCallback");
        if(!(result.equals("connectionLost"))){
            //send data to view Question
            questionsBean = new QuestionsBean(result);
            Log.i(TAG, "questionID = " + String.valueOf(questionsBean.getQuestionID()));
            if(questionsBean.getAnswerType() == 2){
                Log.i(TAG, "AnswerType = " + String.valueOf(questionsBean.getAnswerType()));
                //Log.i(TAG, "choiceAsJsonString = " + questionsBean.getChoiceAsJsonString());
            }
            else{
                Log.i(TAG, "AnswerType = " + String.valueOf(questionsBean.getAnswerType()));
            }

            //-------------------------------------------------------------------------------------
            onBackPressFromAction = true;
            Log.i(TAG, "onBackPressFromAction = true");
            onBackPressed();
        }
        else{
            Snackbar.make(rootLayout, "Not connect to Server", Snackbar.LENGTH_LONG).show();
        }
    }
    public void addQuestion(){ Log.i(TAG,"addQuestion");
        List<ChoiceBean> choiceList = new ArrayList<ChoiceBean>();
        String question = editTextQuestion.getText().toString().trim();
        if (!(TextUtils.isEmpty(question))) {
            if (answerType == 2) {
                if (choiceView.getChildCount() >= 1) {
                    for (int i = 0; i < choiceView.getChildCount(); i++) {
                        View view = choiceView.getChildAt(i);
                        EditText answerChoice = (EditText) view.findViewById(R.id.editText_choice);
                        String tempChoice = answerChoice.getText().toString().trim();
                        if (!(TextUtils.isEmpty(tempChoice))) {
                            ChoiceBean bean = new ChoiceBean();
                            bean.setChoiceData(tempChoice);
                            if (choiceList.isEmpty()) {
                                bean.setChoiceNumber(1);
                            } else {
                                bean.setChoiceNumber((choiceList.size()) + 1);
                            }
                            choiceList.add(bean);
                        }
                    }
                    if (!(choiceList.isEmpty())) {
                        Gson gson = new Gson();
                        JsonElement element = gson.toJsonTree(choiceList);
                        String choiceAsJsonString = gson.toJson(element);
                        questionsBean = new QuestionsBean();
                        questionsBean.setAction("add");
                        questionsBean.setSurveyID(surveyID);
                        questionsBean.setSurveyVersion(surveyVersion);
                        questionsBean.setQuestion(question);
                        questionsBean.setAnswerType(answerType);
                        questionsBean.setChoiceList(choiceList);
                    } else {// choice is empty
                        Snackbar.make(rootLayout, "Empty Answer Choice", Snackbar.LENGTH_LONG).show();
                    }
                }
            } else if (answerType == 1) {
                questionsBean = new QuestionsBean();
                questionsBean.setAction("add");
                questionsBean.setSurveyID(surveyID);
                questionsBean.setSurveyVersion(surveyVersion);
                questionsBean.setQuestion(question);
                questionsBean.setAnswerType(answerType);
            }
            if (questionsBean != null) { //send data to server
                if (checkNetworkConnection()) {
                    //Log.e(TAG, "choiceAsJsonString = " + questionsBean.getChoiceAsJsonString());
                    String requestData = new Gson().toJson(questionsBean);
                    if(!editWithNew){
                        Log.w(TAG,"Edit Something send Question to Server");
                        new ManageQuestionTask().execute(requestData);
                    }
                    else{
                        Log.w(TAG,"Edit with new version sen question to EditQuestionActivity");
                        //sen requestData to EditQuestionActivity
                        onBackPressFromAction = true;
                        onBackPressed();
                    }

                } else {
                    Snackbar.make(rootLayout, "Not connect internet", Snackbar.LENGTH_LONG)
                            .setAction("Setting", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                    //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                }
                            }).show();
                }
            }
        } else {
            //Empty question
            Snackbar.make(rootLayout, "Empty Question Text", Snackbar.LENGTH_LONG).show();
        }
    }
    public void updateQuestion(){ Log.i(TAG,"updateQuestion");

        String question = editTextQuestion.getText().toString().trim();
        List<ChoiceBean> choiceList = new ArrayList<ChoiceBean>();
        if (!(TextUtils.isEmpty(question))) {
            if (answerType == 2) {
                Log.e(TAG,"choiceView.getChildCount() = " +choiceView.getChildCount());
                if (choiceView.getChildCount() >= 1) {
                    for (int i = 0; i < choiceView.getChildCount(); i++) {
                        View view = choiceView.getChildAt(i);
                        EditText answerChoice = (EditText) view.findViewById(R.id.editText_choice);
                        String tempChoice = answerChoice.getText().toString().trim();
                        if (!(TextUtils.isEmpty(tempChoice))) {
                            ChoiceBean bean = new ChoiceBean();
                            bean.setChoiceData(tempChoice);
                            if (choiceList.isEmpty()) {
                                bean.setChoiceNumber(1);
                            } else {
                                bean.setChoiceNumber((choiceList.size()) + 1);
                            }
                            choiceList.add(bean);
                        }
                    }
                    if (!(choiceList.isEmpty())) {
                        Gson gson = new Gson();
                        JsonElement element = gson.toJsonTree(choiceList);
                        String choiceAsJsonString = gson.toJson(element);
                        questionsBean.setQuestion(question);
                        questionsBean.setAnswerType(answerType);
                        questionsBean.setChoiceList(choiceList);
                        if(compareDifferentQuestion(oldQuestionBean,questionsBean)){//Compare question
                            if(!editWithNew){
                                Log.w(TAG,"Edit Something send Question to Server");
                                sendDataToServer(questionsBean);
                            }
                            else{
                                Log.w(TAG,"Edit with new version sen question to EditQuestionActivity");
                                //sen questionsBean to EditQuestionActivity
                                onBackPressFromAction = true;
                                onBackPressed();
                            }

                        }else {
                            //Snackbar.make(rootLayout, "Not change a Question", Snackbar.LENGTH_LONG).show();
                            onBackPressed();
                        }
                    } else {// choice is empty
                        Snackbar.make(rootLayout, "Empty Answer Choice", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            else if (answerType == 1) {
                questionsBean.setQuestion(question);
                questionsBean.setAnswerType(answerType);
                if(compareDifferentQuestion(oldQuestionBean,questionsBean)){//Compare question
                    if(!editWithNew){
                        Log.w(TAG,"Edit Something send Question to Server");
                        sendDataToServer(questionsBean);
                    }
                    else{
                        Log.w(TAG,"Edit with new version sen question to EditQuestionActivity");
                        //sen questionsBean to EditQuestionActivity
                        onBackPressFromAction = true;
                        onBackPressed();
                    }

                }else {
                    //Snackbar.make(rootLayout, "Not change a Question", Snackbar.LENGTH_LONG).show();
                    onBackPressed();
                }
            }
        }
    }
    public void deleteQuestion(){ Log.i(TAG,"deleteQuestion");
        new AlertDialogWrapper.Builder(getContextActivity())
                .setTitle(R.string.askSureToDelete)
                .setMessage(R.string.messageDetailDeleteQuestion)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        questionsBean.setAction("delete");
                        if (!editWithNew) {
                            Log.w(TAG,"Edit Something send Question to Server");
                            Log.e(TAG, "surveyID " + questionsBean.getSurveyID());
                            Log.e(TAG, "surveyVersion " + questionsBean.getSurveyVersion());
                            Log.e(TAG,"questionID "+questionsBean.getQuestionID());
                            sendDataToServer(questionsBean);
                        }
                        else {
                            Log.w(TAG,"Edit with new version sen question to EditQuestionActivity");
                            //sen questionsBean to EditQuestionActivity
                            onBackPressFromAction = true;
                            onBackPressed();
                        }

                    }
                }).show();
    }
    public void initEditTextQuestion(){
        editTextQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                questionInputLayout.setHint("Question");

            }

            @Override
            public void afterTextChanged(Editable s) {
                validQuestion();
            }
        });
        editTextQuestion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextQuestion.setCursorVisible(true);
                return false;
            }
        });
        editTextQuestion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validQuestion()) {
                        editTextQuestion.setCursorVisible(false);
                    }
                }
                return false;
            }
        });
    }
    public boolean validQuestion(){
        if(editTextQuestion.getText().toString().trim().isEmpty()){
            questionInputLayout.setError("Enter Your Question");
            return false;
        }
        else{

            questionInputLayout.setErrorEnabled(false);
            return true;
        }
    }
    public boolean checkNetworkConnection(){
        boolean networkConnection = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            networkConnection = true;
        }
        else{
            networkConnection = false;
        }
        return networkConnection;
    }
    public List<ChoiceBean> getChoiceListFromJsonString (String choiceAsJsonString){
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(choiceAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<ChoiceBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    //-----------------------------------------------------------------------------------------------------------------
    private class SendQuestionTask extends AsyncTask<String, Void, String>{
        private String TAG = SendQuestionTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"SendQuestion");
                    BusProvider.getInstance().post(new OnSendQuestionEvent(questionsBean));
                }
            });
            return null;
        }
    }
    //------------------------------------------------------------------------------------------------------------------

    private class ManageQuestionTask extends AsyncTask<String, Void, String> {
        private String TAG = ManageQuestionTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public ManageQuestionTask() {
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextActivity());
            if(questionsBean.getAction().equals("add")){
                progressDialog.setMessage("Adding...");
            }
            else if(questionsBean.getAction().equals("update")){
                progressDialog.setMessage("updating...");
            }
            else if(questionsBean.getAction().equals("delete")){
                progressDialog.setMessage("Deleting...");
            }
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String url = new Support().getURLLink();
            String requestData = params[0];
            result = new ConnectionServiceCore().doOKHttpPostString(url,"ManageQuestion",requestData);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onManageQuestionTaskCallback(result);
        }
    }


    public Context getContextActivity(){
        return this;
    }
    public boolean compareDifferentQuestion(QuestionsBean oldQuestionBean, QuestionsBean questionsBean){
        Log.i(TAG, "compareDifferentQuestion");
        boolean resultCompare = false;
        if(oldQuestionBean.getQuestion().equals(questionsBean.getQuestion())){
            Log.i(TAG, "Not change text question");
            if(oldQuestionBean.getAnswerType() == questionsBean.getAnswerType()){
                Log.i(TAG, "Not change answer type");
                if(oldQuestionBean.getAnswerType() == 2 && questionsBean.getAnswerType() == 2){
                    Log.i(TAG, "Not answer type is Choice answer");
                    List<ChoiceBean> oldChoiceBeanList = oldQuestionBean.getChoiceList();
                    List<ChoiceBean> choiceBeanList = questionsBean.getChoiceList();
                    int sizeOld = oldChoiceBeanList.size();
                    int size = choiceBeanList.size();
                    if(sizeOld == size){
                        Log.i(TAG, "oldChoiceBeanList == choiceBeanList at " + size);
                        for(int i=0;i<size;i++){
                            ChoiceBean oldChoiceBean = oldChoiceBeanList.get(i);
                            ChoiceBean choiceBean = choiceBeanList.get(i);
                            String oldChoiceData = oldChoiceBean.getChoiceData();
                            String choiceData = choiceBean.getChoiceData();
                            if(!(oldChoiceData.equals(choiceData))){
                                Log.i(TAG, "has Change set true 555");
                                Log.i(TAG, "for " + i + " oldChoiceData = " + oldChoiceData + ", choiceData = " + choiceData);
                                resultCompare = true;
                            }
                        }
                    }
                    else{
                        resultCompare = true;
                    }
                }
                else{
                    resultCompare = false;
                }
            }
            else{
                resultCompare = true;
            }
        }
        else{
            resultCompare = true;
        }
        return resultCompare;
    }
    public void addChoice(){
        Log.i(TAG,"addChoice");
        final View choiceItem = LayoutInflater.from(getContextActivity()).inflate(R.layout.choice_item, choiceView, false);
        final EditText answerChoice = (EditText)choiceItem.findViewById(R.id.editText_choice);
        final ImageView deleteChoice = (ImageView)choiceItem.findViewById(R.id.imageView_deleteChoice);

        choiceView.addView(choiceItem);
        if(choiceView.getChildCount() == 1 ){
            deleteChoice.setVisibility(View.GONE);
        }
        else if(choiceView.getChildCount() > 1){
            deleteChoice.setVisibility(View.VISIBLE);
        }

        if((TextUtils.isEmpty(answerChoice.getText().toString().trim()))){
            deleteChoice.setVisibility(View.INVISIBLE);
        }
        answerChoice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && choiceView.getChildCount() > 1) {
                    choiceView.removeView(choiceItem);
                } else if (s.length() > 0 && ((before + start) == 0)) {
                    deleteChoice.setVisibility(View.VISIBLE);
                    addChoice();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        deleteChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((choiceView.getChildCount() > 1)) {
                    choiceView.removeView(choiceItem);
                }

            }
        });
    }
    public void setChoice(List<ChoiceBean> choiceList){
        Log.i(TAG,"setChoice");
        int i=0,last = choiceList.size();
        for(ChoiceBean choiceBean:choiceList){
            final View choiceItem = LayoutInflater.from(getContextActivity()).inflate(R.layout.choice_item, choiceView, false);
            final EditText answerChoice = (EditText)choiceItem.findViewById(R.id.editText_choice);
            final ImageView deleteChoice = (ImageView)choiceItem.findViewById(R.id.imageView_deleteChoice);
            answerChoice.setText(choiceBean.getChoiceData());

            choiceView.addView(choiceItem);
            i++;
            if(i == last){
                addChoice();
            }
            if(choiceView.getChildCount() == 1 && (TextUtils.isEmpty(answerChoice.getText().toString().trim()))){
                deleteChoice.setVisibility(View.GONE);
            }
            else if(choiceView.getChildCount() > 1){
                deleteChoice.setVisibility(View.VISIBLE);
            }

            if((TextUtils.isEmpty(answerChoice.getText().toString().trim()))){
                deleteChoice.setVisibility(View.INVISIBLE);
            }
            answerChoice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0 && choiceView.getChildCount() > 1) {
                        choiceView.removeView(choiceItem);
                    } else if (s.length() > 0 && ((before + start) == 0)) {
                        deleteChoice.setVisibility(View.VISIBLE);
                        addChoice();
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            deleteChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((choiceView.getChildCount() > 1)) {
                        choiceView.removeView(choiceItem);
                    }

                }
            });
        }
    }

    public void setViewQuestion(int answerType){
        Log.i(TAG,"setViewQuestion");
        editTextQuestion.setText(questionsBean.getQuestion());
        if(answerType == 1){
            radioGroupSelectAnswerType.check(R.id.radio_textType);
        }
        else if(answerType == 2){
            radioGroupSelectAnswerType.check(R.id.radio_choiceType);
            setChoice(questionsBean.getChoiceList());
        }
    }

    public void sendDataToServer(Object object){
        Log.i(TAG, "sendDataToServer");
        if (checkNetworkConnection()) {
            String requestData = new Gson().toJson(object);
            new ManageQuestionTask().execute(requestData);
        } else {
            Snackbar.make(rootLayout, "Not connect internet", Snackbar.LENGTH_LONG)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                            //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).show();
        }
    }
}
