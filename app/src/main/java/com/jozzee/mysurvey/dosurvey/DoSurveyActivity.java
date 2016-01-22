package com.jozzee.mysurvey.dosurvey;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.AnswerBean;
import com.jozzee.mysurvey.bean.DoSurveyBean;
import com.jozzee.mysurvey.bean.QuestionsBeanForDoSurvey;
import com.jozzee.mysurvey.bean.SurveyBean;
import com.jozzee.mysurvey.adpter.DoSurveyAdapter;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.support.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoSurveyActivity extends AppCompatActivity {
    private static String TAG = DoSurveyActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private int accountID = 1;
    private SurveyBean surveyBean;
    private String nameGuest;
    private Bundle bundle;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressBar progressBar;
    private Bitmap bitmap;
    private Palette palette;
    private ImageView surveyImage;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<QuestionsBeanForDoSurvey> questionList;
    private DoSurveyAdapter adapter;
    private String questionListAsJsonString = "";
    private String surveyBeanAsJsonString = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private ManageJson manageJson;
    private Support support;

    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("LOG", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        accountID = sharedPreferences.getInt("accountID", 1);
        setContentView(R.layout.activity_do_survey);

        bundle = getIntent().getExtras();
        if(bundle != null){
            surveyBean = new SurveyBean((String)bundle.get("surveyBean"));
            if(accountID == 1){ //this member
                nameGuest = (String)bundle.get("nameGuest");
            }
        }
        if(savedInstanceState != null){
            surveyBean = new SurveyBean(savedInstanceState.getString("surveyBeanAsJsonString"));
            questionListAsJsonString = savedInstanceState.getString("questionListAsJsonString");
        }
        //----------------------------------------------------------------------------------------------------
        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_doSurvey);  //set RootLayout
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout_doSurvey);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbarLayout_doSurvey);
        collapsingToolbarLayout.setTitle(surveyBean.getSurveyName());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        toolbar = (Toolbar) findViewById(R.id.toolbar_doSurvey); //Set Toolbar replace Actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        support = new Support();
        manageJson = new ManageJson();


        progressBar = (ProgressBar)findViewById(R.id.progressBar_coverImage_doSurvey);
        surveyImage = (ImageView)findViewById(R.id.imageView_coverImage_doSurvey);

        if(!(surveyBean.getCoverImage().equals("noImage"))){
            new GetBitMapTask().execute(surveyBean.getCoverImage());
        }
        else{
            Glide.with(this)
                    .load(R.drawable.im_survey_form)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(surveyImage);
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_doSurvey);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        questionList = new ArrayList<QuestionsBeanForDoSurvey>();
        questionList.add(null);
        adapter = new DoSurveyAdapter(questionList,this);
        recyclerView.setAdapter(adapter);

        //------------------------------------------------------------------------------------------------------------------------
        if (questionListAsJsonString.equals("") && !(connectionLost) &&!(onLoad)){Log.i(TAG, "Not have backup data, have connect server, and not downloading data from server");

            if(checkNetworkConnection()){//check connect internet (wifi or service)
                new DoSurveyTask().execute();
            }
            else{
                showSnackBarNotConnectInternet(rootLayout);
            }
        }
        else if(connectionLost){ Log.i(TAG, "Can't connect to server");

        }
        else if(onLoad && questionListAsJsonString.equals("")){ Log.i(TAG,"Downloading and not have backup data");

        }
        else if(!questionListAsJsonString.equals("")){ Log.i(TAG,"have backup data ,not downloading, have connect server");
            questionList = manageJson.getQuestionListForDoSurvey(questionListAsJsonString);
            adapter.setQuestionList(questionList);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onStart() { Log.i(TAG, "onStart");
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
    protected void onStop(){  Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart(){  Log.i(TAG, "onRestart");
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
        getMenuInflater().inflate(R.menu.menu_do_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.i(TAG, "id = " + id);

        if(id == 16908332){
            new AlertDialogWrapper.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.messageDetailExitDoSurvey)
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
        }
        else if(id == R.id.action_submit_doSurvey){ Log.i(TAG, "Send Answer to Server");
            boolean checkCompleteAnswer = true;
            List<QuestionsBeanForDoSurvey> tempQuestionList = adapter.getQuestionList();
            if(tempQuestionList.get(0)!= null){
                final List<AnswerBean> answerBeans = new ArrayList<AnswerBean>();
                for(QuestionsBeanForDoSurvey bean:tempQuestionList){
                    if(!(bean.getAnswerData() == null)){
                        AnswerBean answerBean = new AnswerBean(
                                bean.getQuestionID(),bean.getAnswerType(),bean.getAnswerData(),bean.getChoiceIDAsAnswer());
                        answerBeans.add(answerBean);
                    }
                    else{
                        checkCompleteAnswer = false;
                    }
                }
                if(checkCompleteAnswer){
                    new MaterialDialog.Builder(this)
                            .title(R.string.thanksForAnswer)
                            .content(R.string.messageDetailSendAnswer)
                            .positiveText("OK")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    DoSurveyBean doSurveyBean = new DoSurveyBean();
                                    doSurveyBean.setSurveyID(surveyBean.getSurveyID());
                                    doSurveyBean.setSurveyVersion(surveyBean.getSurveyVersion());
                                    if(accountID == 1){
                                        doSurveyBean.setAnswerBy(nameGuest);
                                        Log.e(TAG,"nameGuest = "+nameGuest);
                                    }
                                    doSurveyBean.setAccountID(accountID);
                                    doSurveyBean.setAnswerDate(new Support().getDateTime());
                                    doSurveyBean.setAnswerDataAsJsonString(new Gson().toJson(answerBeans));

                                    String requestData = new Gson().toJson(doSurveyBean); Log.i(TAG,requestData);
                                    new SendAnswerTask().execute(requestData);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                }
                            })
                            .show();
                }
                else{
                    Snackbar.make(rootLayout, "You must complete all answer.", Snackbar.LENGTH_LONG).show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("questionListAsJsonString",
                manageJson.getJsonStringFromQuestionListForDoSurvey(questionList));
        outState.putString("surveyBeanAsJsonString", new Gson().toJson(surveyBean));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");

    }
    public void onGetBitmapCallback(){
        ImageViewAnimatedChange(this, surveyImage, bitmap);
        //bitmap.recycle();


    }
    public void onDoSurveyTaskCallback(String result){
        if(!(result.equals("connectionLost"))){
            questionList.remove(questionList.size() - 1);//remove  progress bar
            adapter.notifyItemRemoved(questionList.size());
           /* List<QuestionsBeanForDoSurvey> tempQuestionList = new QuestionsBeanForDoSurvey().getQuestionList(result);
            for(QuestionsBeanForDoSurvey tempQuestionsBean:tempQuestionList){
                questionList.add(tempQuestionsBean);
            }*/
            questionList = manageJson.getQuestionListForDoSurvey(result);
            adapter.setQuestionList(questionList);
            adapter.notifyDataSetChanged();
        }
        else if(result.equals("connectionLost")){

        }
    }

    public Context getContextFromActivity(){
        return this;
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
    ///---------------------------------------------------------------------------
    private class SendAnswerTask extends  AsyncTask<String, Void, String>{
        private String TAG  = SendAnswerTask.class.getSimpleName();
        private ProgressDialog progressDialog;
        public SendAnswerTask() {
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            onLoad = true;
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("in process...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            String url = new Support().getURLLink();
            String command = "SendAnswer";
            String requestData = params[0];
            return new ConnectionServiceCore().doOKHttpPostString(url,command,requestData);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressDialog.dismiss();
            if(result.equals("success")){
                onBackPressed();
            }
            else{
                Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    private class DoSurveyTask extends  AsyncTask<String, Void, String>{
        private String TAG  = DoSurveyTask.class.getSimpleName();

        public DoSurveyTask() {
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            onLoad = true;
        }

        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpGetString(
                            support.getURLLink()+"?command=DoSurvey&surveyID="
                            +surveyBean.getSurveyID() +"&surveyVersion="
                            +surveyBean.getSurveyVersion());
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            onDoSurveyTaskCallback(result);
        }
    }
    private class GetBitMapTask extends AsyncTask<String, Void, String> {
        private  String TAG  = GetBitMapTask.class.getSimpleName();
        public static final int colorDriver = 0xFFFFFFFF;

        public GetBitMapTask() {
        }
        public void setColorOfProgressBar(ProgressBar mProgressBar, int mColor) {
            mProgressBar.getIndeterminateDrawable().setColorFilter(mColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            setColorOfProgressBar(progressBar, colorDriver);
            progressBar.setVisibility(View.VISIBLE);

        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            getBitmapFromURL(params[0]);
            if(bitmap != null){
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette p) {
                        palette = p;
                        int defaultColor = Color.WHITE;
                        int colorPrimary = 0xFF2196F3;
                        int colorPrimaryDark = 0xFF1976D2;
                        collapsingToolbarLayout.setContentScrimColor(palette.getVibrantColor(colorPrimary));
                        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(colorPrimaryDark));
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){ Log.i(TAG, "onPostExecute");
            progressBar.setVisibility(View.GONE);
            onGetBitmapCallback();
        }
    }
    public void getBitmapFromURL(String url) {
        Log.i(TAG,"getBitmapFromURL");
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL urlLink = new URL(url);
            connection = (HttpURLConnection) urlLink.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            Log.i(TAG, "get Bitmap success");

            input.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {

        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
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
