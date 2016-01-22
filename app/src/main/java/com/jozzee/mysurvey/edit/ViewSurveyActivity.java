package com.jozzee.mysurvey.edit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.analyze.AnalyzeActivity;
import com.jozzee.mysurvey.bean.ChangeStatusBean;
import com.jozzee.mysurvey.bean.ChangeSurveyTypeBean;
import com.jozzee.mysurvey.bean.EditSurveyNameBean;
import com.jozzee.mysurvey.bean.PaletteBean;
import com.jozzee.mysurvey.bean.ResultCreateAnalyzeBean;
import com.jozzee.mysurvey.event.OnAfterEditQuestionEvent;
import com.jozzee.mysurvey.event.OnBackPressedFromViewSurveyEvent;
import com.jozzee.mysurvey.bean.SurveyBeanForMySurvey;
import com.jozzee.mysurvey.bean.UpdateFormBean;
import com.jozzee.mysurvey.bean.UpdateImageBean;
import com.jozzee.mysurvey.event.BusProvider;

import com.jozzee.mysurvey.responses.ResponsesActivity;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageImage;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.support.Validate;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewSurveyActivity extends AppCompatActivity {
    private static String TAG = ViewSurveyActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA= 0;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CROP_IMAGE_FROM_CAMERA = 2;
    private static final int REQUEST_CROP_IMAGE_FROM_GALLERY = 3;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private RelativeLayout editSurveyImageLayout;
    private RelativeLayout responsesLayout;
    private LinearLayout menuEdit;
    private LinearLayout menuSend;
    private LinearLayout menuAnalyze;
    private Bundle bundle;
    private int position;
    private SurveyBeanForMySurvey bean;
    private ImageView surveyImage;
    private Bitmap bitmap;
    private TextView responses;
    private TextView surveyName;
    private ProgressBar progressBar;
    private Button buttonEditSurveyImage;
    private File file;
    private String imagePath = "noImage";
    private int maxSizeImage = 1280;
    private Palette palette;
    private Bitmap blurBitmap;
    private RelativeLayout layoutSurveyStatus;
    private TextView textViewSurveyStatus;
    private Switch switchSurveyStatus;
    private RadioGroup radioGroupSurveyType;
    private LinearLayout layoutSurveyPassword;
    private TextInputLayout inputLayoutSurveyPassword;
    private EditText editTextSurveyPassword;
    private TextView version;
    private TextView questions;
    private TextView lastResponses;
    private TextView createDate;
    private TextView lastUpdate;
    private String action = "update";
    private Support support;
    private ManageImage manageImage;
    private Validate validate;
    private Gson gson;
    private boolean haveEdit = false;
    private String imageName;
    private int statusBarColor = 0;
    private int toolBarColor = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
        setContentView(R.layout.activity_view_survey);

        bundle = getIntent().getExtras();
        if(bundle != null) {
            Log.i(TAG, "get action from bundle");
            bean = new SurveyBeanForMySurvey((String)bundle.get("survey"));
            position = (Integer)bundle.get("position");
        }
        if(savedInstanceState != null){  Log.i(TAG, "get action from savedInstanceState");
            bean = new SurveyBeanForMySurvey(savedInstanceState.getString("bean"));
            statusBarColor = savedInstanceState.getInt("statusBarColor");
            toolBarColor = savedInstanceState.getInt("toolBarColor");
        }

        toolbar = (Toolbar)findViewById(R.id.toolbar_viewSurvey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_viewSurvey);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout_viewSurvey);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout_viewSurvey);
        collapsingToolbarLayout.setTitle("");


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setAppBarLayout();
        progressBar = (ProgressBar)findViewById(R.id.progressBar_ImageViewSurvey);
        surveyImage = (ImageView)findViewById(R.id.imageView_viewSurvey);
        surveyName = (TextView)findViewById(R.id.surveyName_viewSurvey);
        editSurveyImageLayout = (RelativeLayout)findViewById(R.id.relativeLayout_editSurveyImage_viewSurvey);
        buttonEditSurveyImage = (Button)findViewById(R.id.buttonEditSurveyImage_viewSurvey);
        responses = (TextView)findViewById(R.id.textView_show_numberResponse_viewSurvey);
        responsesLayout = (RelativeLayout)findViewById(R.id.relativeLayout_show_responses_viewSurvey);
        menuEdit = (LinearLayout)findViewById(R.id.linearLayout_menuEdit_viewSurvey);
        menuSend = (LinearLayout)findViewById(R.id.linearLayout_menuSend_viewSurvey);
        menuAnalyze = (LinearLayout)findViewById(R.id.linearLayout_menuAnalyze_viewSurvey);

        layoutSurveyStatus = (RelativeLayout)findViewById(R.id.relativeLayout_surveyStatus_viewSurvey);
        textViewSurveyStatus = (TextView)findViewById(R.id.textView_surveyStatus_viewSurvey);
        switchSurveyStatus = (Switch)findViewById(R.id.switch_surveyStatus_viewSurvey);

        radioGroupSurveyType = (RadioGroup)findViewById(R.id.radioGroup_surveyType_viewSurvey);
        layoutSurveyPassword = (LinearLayout)findViewById(R.id.linearLayout_password_viewSurvey);
        inputLayoutSurveyPassword = (TextInputLayout)findViewById(R.id.TextInputLayout_password_viewSurvey);
        editTextSurveyPassword = (EditText)findViewById(R.id.editText_password_viewSurvey);


        version = (TextView)findViewById(R.id.textView_show_detail_version_viewSurvey);
        questions = (TextView)findViewById(R.id.textView_show_detail_questions_viewSurvey);
        lastResponses = (TextView)findViewById(R.id.textView_show_detail_lastResponses_viewSurvey);
        createDate = (TextView)findViewById(R.id.textView_show_detail_createDate_viewSurvey);
        lastUpdate = (TextView)findViewById(R.id.textView_show_detail_modified_viewSurvey);

        support = new Support();
        manageImage = new ManageImage();
        validate = new Validate();
        gson = new Gson();


        if(bean.getCoverImage().equals("noImage")){ Log.e(TAG,"noImage");
            surveyImage.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.im_survey_form));
        }
        else{
            if(statusBarColor == 0 && toolBarColor == 0){
                new GetBitMapTask().execute(bean.getCoverImage());
            }
            else{
                Log.e(TAG, "have Palette");
                Glide.with(this)
                        .load(bean.getCoverImage())
                        .into(surveyImage);

                Log.e(TAG,"statusBarColor = "+statusBarColor);
                Log.e(TAG,"toolBarColor = "+toolBarColor);
                collapsingToolbarLayout.setContentScrimColor(toolBarColor);
                collapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
            }

        }
        //---------------------------------------------------------------------------------------------------------------

        surveyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click Edit SurveyName");
                dialogEditSurveyName();
            }
        });

        surveyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click Edit Survey Image");
                popupMenuSelectImage();
            }
        });
        editSurveyImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click  Edit layout Survey Image");
                popupMenuSelectImage();
            }
        });
        responsesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click View Responses");
                Intent responsesIntent = new Intent(v.getContext(), ResponsesActivity.class);
                responsesIntent.putExtra("surveyID",bean.getSurveyID());
                responsesIntent.putExtra("surveyVersion", bean.getSurveyVersion());
                startActivity(responsesIntent);
            }
        });
        menuEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click Edit Questions");
                dialogEditQuestions();
            }
        });
        menuSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(rootLayout, bean.getLink(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, bean.getSurveyName());
                share.putExtra(Intent.EXTRA_TEXT, bean.getLink());

                startActivity(Intent.createChooser(share, "Share Survey"));
            }
        });
        menuAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click Analyze QResponses");
                if(bean.getNumberOfQuestions()>0){
                    if(support.checkNetworkConnection(getContextFromActivity())){
                        new CreateAnalyzeTask(v).execute(String.valueOf(bean.getSurveyID()),String.valueOf(bean.getSurveyVersion()));
                    }
                    else {
                        showSnackBarNotConnectInternet(rootLayout);
                    }
                }
                else{
                    support.showSnackBarNoQuestion(rootLayout);
                }

            }
        });
        if(bean.getSurveyStatus() == 2){
            switchSurveyStatus.setChecked(true);
            textViewSurveyStatus.setText(R.string.online);
            textViewSurveyStatus.setTextColor(ContextCompat.getColor(getContextFromActivity(), R.color.green));
        }
        switchSurveyStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new UpdateSurveyFormTask().execute("ChangeSurveyStatus"
                            ,new Gson().toJson(new ChangeStatusBean(bean.getSurveyID(),bean.getSurveyVersion(),2)));
                }
                else{
                    new UpdateSurveyFormTask().execute("ChangeSurveyStatus"
                            , new Gson().toJson(new ChangeStatusBean(bean.getSurveyID(), bean.getSurveyVersion(), 1)));
                }
            }
        });
        layoutSurveyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewSurveyStatus.getText().toString().trim().equals("Online")) {
                    switchSurveyStatus.setChecked(false);
                } else if (textViewSurveyStatus.getText().toString().trim().equals("Offline")) {
                    switchSurveyStatus.setChecked(true);
                }
            }
        });
        if(bean.getSurveyType() == 3){
            radioGroupSurveyType.check(R.id.radio_password);
            layoutSurveyPassword.setVisibility(View.VISIBLE);
            editTextSurveyPassword.setVisibility(View.VISIBLE);
            editTextSurveyPassword.setEnabled(true);
        }
        else if(bean.getSurveyType() == 1){
            radioGroupSurveyType.check(R.id.radio_public);
            editTextSurveyPassword.setEnabled(false);
        }
        else if(bean.getSurveyType() == 2){
            radioGroupSurveyType.check(R.id.radio_private);
            editTextSurveyPassword.setEnabled(false);
        }
        editTextSurveyPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (validate.validPassword(editTextSurveyPassword,inputLayoutSurveyPassword,1,32)) {
                        ChangeSurveyTypeBean changeSurveyTypeBean = new ChangeSurveyTypeBean(bean.getSurveyID()
                                , bean.getSurveyVersion(), 3, editTextSurveyPassword.getText().toString().trim());
                        new UpdateSurveyFormTask().execute("ChangeSurveyType", new Gson().toJson(changeSurveyTypeBean));
                    }
                }
                return handled;
            }
        });
        radioGroupSurveyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            //save to server then....
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int surveyType = 1;

                if (checkedId == R.id.radio_public) {
                    layoutSurveyPassword.setVisibility(View.GONE);
                    editTextSurveyPassword.setVisibility(View.GONE);
                    editTextSurveyPassword.setEnabled(false);
                    surveyType = 1;
                    ChangeSurveyTypeBean changeSurveyTypeBean = new ChangeSurveyTypeBean(bean.getSurveyID(), bean.getSurveyVersion(), surveyType);
                    new UpdateSurveyFormTask().execute("ChangeSurveyType", new Gson().toJson(changeSurveyTypeBean));
                } else if (checkedId == R.id.radio_private) {
                    layoutSurveyPassword.setVisibility(View.GONE);
                    editTextSurveyPassword.setVisibility(View.GONE);
                    editTextSurveyPassword.setEnabled(false);

                    surveyType = 2;
                    ChangeSurveyTypeBean changeSurveyTypeBean = new ChangeSurveyTypeBean(bean.getSurveyID(), bean.getSurveyVersion(), surveyType);
                    new UpdateSurveyFormTask().execute("ChangeSurveyType", new Gson().toJson(changeSurveyTypeBean));
                } else if (checkedId == R.id.radio_password) {
                    surveyType = 3;
                    layoutSurveyPassword.setVisibility(View.VISIBLE);
                    editTextSurveyPassword.setVisibility(View.VISIBLE);
                    editTextSurveyPassword.setEnabled(true);

                }


            }
        });
        //-----------------------------------------------------------------------------------------
        surveyName.setText(bean.getSurveyName());
        responses.setText(String.valueOf(bean.getNumberOfTested())); //Log.e(TAG, bean.getCoverImage());
        version.setText(String.valueOf(bean.getSurveyVersion()));
        questions.setText(String.valueOf(bean.getNumberOfQuestions()));
        if(bean.getLastResponses()!= null){
            lastResponses.setText(conventDateTimeToShow(bean.getLastResponses()));
        }
        else{
            lastResponses.setText("Not Responses");
        }

        createDate.setText(conventDateTimeToShow(bean.getCreteDate()));
        lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));




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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");

        super.onPause();

    }

    @Override
    protected void onStop(){  Log.i(TAG, "onStop");
        super.onStop();
        if(haveEdit){
            haveEdit = false;
            new BackPressedTask().execute(action);
        }


    }

    @Override
    protected void onRestart(){ Log.i(TAG, "onRestart");
        super.onRestart();
    }
    @Override
    protected void onDestroy(){ Log.i(TAG, "onDestroy");
        super.onDestroy();
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.

    }
    @Override
    public void onBackPressed() {  Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("bean", new Gson().toJson(bean));
        outState.putInt("statusBarColor", statusBarColor);
        outState.putInt("toolBarColor", toolBarColor);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_survvey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.i(TAG,"id menu = " +id);
        if(id == 16908332){
            onBackPressed();
           return true;
        }
        if(id == R.id.action_deleteSurvey){
            new AlertDialogWrapper.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.messageDetailDeleteSurvey)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteSurveyTask().execute(String.valueOf(bean.getSurveyID()));
                        }
                    }).show();
        }
        if(id == R.id.action_clearResponses){
            new AlertDialogWrapper.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.messageDetailClearResponses)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ClearResponsesTask().execute(new ManageJson()
                                    .serializationStringToJson("surveyID," + bean.getSurveyID()
                                            , "surveyVersion," + bean.getSurveyVersion()));
                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { Log.i(TAG, "onActivityResult");
        if(resultCode == RESULT_OK){
            if(resultCode == -1){ Log.i(TAG, "resultCode OK");
                if(requestCode == REQUEST_CAMERA){
                    CropImageFromCamera(Uri.fromFile(file),imagePath);
                }
                else if(requestCode == REQUEST_GALLERY){
                    onGalleryResult(data);
                }
                else if(requestCode == REQUEST_CROP_IMAGE_FROM_CAMERA){
                    onCameraResult();
                }
                else if(requestCode == REQUEST_CROP_IMAGE_FROM_GALLERY){

                }
            }
            else{Log.i(TAG, "resultCode not OK");
                if(file!= null){
                    file.delete(); Log.i(TAG, "deleteFile");
                }
            }
        }
    }
    @Subscribe
    public void onAfterEditQuestionEvent(OnAfterEditQuestionEvent event){ Log.i(TAG, "onAfterEditQuestionEvent");
        bean.setLastUpdate(event.getLastUpdate());
        bean.setNumberOfQuestions(event.getNumberOffQuestions());
        bean.setSurveyVersion(event.getSurveyVersion());

        lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));
        questions.setText(String.valueOf(bean.getNumberOfQuestions()));
        version.setText(String.valueOf(bean.getSurveyVersion()));

        if(event.isNewVersion()){
            bean.setNumberOfTested(0);
            bean.setLastResponses(null);
            responses.setText("0");
            lastResponses.setText("Not Responses");
            bean.setLink(event.getLink());

        }
        haveEdit = true;
    }
    public void onGetBitmapCallback(){
        manageImage.imageViewAnimatedChange(getContextFromActivity(), surveyImage, bitmap);
        surveyName.setVisibility(View.VISIBLE);
    }
    public void onUpdateSurveyFormCallback(String result){ Log.i(TAG, "onUpdateSurveyFormCallback");

        if(!(result.equals("connectionLost"))){
            UpdateFormBean updateFormBean = new UpdateFormBean(result);
            if(updateFormBean.getForm().equals("UpdateSurveyName") && updateFormBean.getResult().equals("success")){
                bean.setLastUpdate(updateFormBean.getLastModifyDate());
                lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));
                bean.setSurveyName(updateFormBean.getData());
                surveyName.setText(bean.getSurveyName());
                haveEdit = true;
            }
            else if(updateFormBean.getForm().equals("ChangeSurveyStatus")&& updateFormBean.getResult().equals("success")){
                if(updateFormBean.getData().equals("2")){
                    textViewSurveyStatus.setText(R.string.online);
                    textViewSurveyStatus.setTextColor(ContextCompat.getColor(getContextFromActivity(), R.color.green));
                    bean.setSurveyStatus(2);
                } else{
                    textViewSurveyStatus.setText(R.string.offline);
                    textViewSurveyStatus.setTextColor(ContextCompat.getColor(getContextFromActivity(), R.color.red_700));
                    bean.setSurveyStatus(1);
                }
            }
            else if(updateFormBean.getForm().equals("ChangeSurveyType") && updateFormBean.getResult().equals("success")){

                int surveyType = Integer.parseInt(updateFormBean.getData());
                bean.setSurveyType(surveyType);
                if(surveyType == 3){
                    bean.setSurveyPassword(editTextSurveyPassword.getText().toString().trim());
                }
                bean.setLastUpdate(updateFormBean.getLastModifyDate());
                lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));
                haveEdit = true;

            }
        }
        else{
            Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
        }
    }
    public void onUpdateSurveyImageCallback(String result){ Log.i(TAG,"onUpdateSurveyImageCallback");
        if(!(result.equals("connectionLost"))){
            UpdateImageBean updateImageBean = new UpdateImageBean(result);
            if(updateImageBean.getFrom().equals("SetDefaultSurveyImage") && updateImageBean.getResult().equals("success")){
                Log.i(TAG, "SetDefaultSurveyImage");
                surveyImage.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.im_survey_form));
                bean.setCoverImage("noImage");
                bitmap.recycle();
                bean.setLastUpdate(updateImageBean.getLastUpdate());
                lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));
                haveEdit = true;
            }
            else if(updateImageBean.getFrom().equals("UploadImage") && updateImageBean.getResult().equals("success")){
                Log.i(TAG, "UploadImage");
                surveyImage.setImageBitmap(bitmap);
                bean.setCoverImage(updateImageBean.getUrlImage());
                bean.setLastUpdate(updateImageBean.getLastUpdate());
                lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));
                blurBitmap.recycle();
                file.delete();
                haveEdit = true;
                //new ClearDiskCacheGlide().execute(this);
            }
            else if(updateImageBean.getFrom().equals("UpdateImage") && updateImageBean.getResult().equals("success")){
                Log.i(TAG, "UpdateImage");
                surveyImage.setImageBitmap(bitmap);
                bean.setCoverImage(updateImageBean.getUrlImage());
                bean.setLastUpdate(updateImageBean.getLastUpdate());
                lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));

                blurBitmap.recycle();
                file.delete();
                haveEdit = true;
                //new ClearDiskCacheGlide().execute(this);
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------
    private class ClearResponsesTask extends AsyncTask<String,Void,String>{
        private  String TAG  = ClearResponsesTask.class.getSimpleName();

        private ProgressDialog progressDialog;

        public ClearResponsesTask() {}

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("Clearing...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "onPreExecute");

            return new ConnectionServiceCore().doOKHttpPostString(new Support().getURLLink(),"ClearResponses",params[0]);
        }
        @Override
        protected void onPostExecute(String result){ Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost"))){
                bean.setLastUpdate(result);
                bean.setLastResponses(null);
                bean.setNumberOfTested(0);
                responses.setText(String.valueOf(bean.getNumberOfQuestions()));
                lastResponses.setText("Not Responses");
                lastUpdate.setText(bean.getLastUpdate());
                haveEdit = true;
            }
            else{
                Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    private class DeleteSurveyTask extends AsyncTask<String,Void,String>{
        private  String TAG  = DeleteSurveyTask.class.getSimpleName();

        private ProgressDialog progressDialog;

        public DeleteSurveyTask() {}

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("Deleting...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "onPreExecute");

            return new ConnectionServiceCore().doOKHttpPostString(new Support().getURLLink(),"DeleteSurvey",params[0]);
        }
        @Override
        protected void onPostExecute(String result){ Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(result.equals("success")){
                action = "delete";
                haveEdit = true;
                onBackPressed();
            }
            else{
                Snackbar.make(rootLayout, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    private class BackPressedTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(final String... params) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "BackPressedTask");
                    BusProvider.getInstance().post(new OnBackPressedFromViewSurveyEvent(bean,position,params[0]));
                }
            });
            return null;
        }
    }
    private class GetBitMapTask extends AsyncTask<String, Void, String>{
        private  String TAG  = GetBitMapTask.class.getSimpleName();

        public GetBitMapTask() {
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            support.setProgressBarColor(progressBar,Color.WHITE);
            progressBar.setVisibility(View.VISIBLE);
            surveyName.setVisibility(View.GONE);
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            getBitmapFromURL(params[0]);
            if(bitmap != null){
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette p) {
                        palette = p;
                        statusBarColor = palette.getDarkVibrantColor(support.primaryDarkColor());
                        toolBarColor = palette.getVibrantColor(support.primaryColor());
                        Log.e(TAG,"statusBarColor = "+statusBarColor);
                        Log.e(TAG, "toolBarColor = " + toolBarColor);
                        collapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
                        collapsingToolbarLayout.setContentScrimColor(toolBarColor);

                        //collapsingToolbarLayout.setContentScrimColor(palette.getVibrantColor(support.primaryColor()));
                        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(support.primaryDarkColor()));
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
    //-----------------------------------------------------------------------------------------------------------------------------------
    private class UpdateSurveyFormTask extends AsyncTask<String, Void, String>{
        private  String TAG  = UpdateSurveyFormTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public UpdateSurveyFormTask() {}

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("In Process...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),params[0],params[1]);
        }
        @Override
        protected void onPostExecute(String result){ Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onUpdateSurveyFormCallback(result);
        }
    }
    //--------------------------------------------------------------------------------------------
    private class UpdateSurveyImage extends AsyncTask<String, Void, String>{
        private  String TAG  = UpdateSurveyImage.class.getSimpleName();

        public UpdateSurveyImage() {

        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            support.setProgressBarColor(progressBar,Color.WHITE);
            progressBar.setVisibility(View.VISIBLE);
            surveyName.setVisibility(View.GONE);
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            if(params[0].equals("SetDefaultSurveyImage")){  Log.i(TAG, "SetDefaultSurveyImage");
                return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),params[0],params[1]);
            }
            else {
                if(bean.getCoverImage().equals("noImage")) { Log.i(TAG, "UploadImage");
                    return new ConnectionServiceCore().uploadImage(
                            support.getURLLink(),"UploadImage","SurveyImage"
                            , imagePath,bean.getSurveyID(),bean.getSurveyVersion(),"");
                }
                else { Log.i(TAG, "UpdateImage");
                    return new ConnectionServiceCore().uploadImage(
                            support.getURLLink(),"UpdateImage","SurveyImage",
                            imagePath,bean.getSurveyID(),bean.getSurveyVersion(),support.getCodeImage(bean.getCoverImage()));
                }
            }
        }
        @Override
        protected void onPostExecute(String result){ Log.i(TAG, "onPostExecute");
            progressBar.setVisibility(View.GONE);
            surveyName.setVisibility(View.VISIBLE);
            onUpdateSurveyImageCallback(result);

        }
    }
    private class CreateAnalyzeTask extends AsyncTask<String, Void, String>{
        private  String TAG  = CreateAnalyzeTask.class.getSimpleName();

        private ProgressDialog progressDialog;
        private View view;

        public CreateAnalyzeTask(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getContextFromActivity());
            progressDialog.setMessage("In process...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground");

            String url = new Support().getURLLink()+"?command=CreateAnalyze&surveyID="+params[0] +"&surveyVersion="+params[1];
            Log.i(TAG,"url = "+url);
            return new ConnectionServiceCore().doOKHttpGetString(url);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(!(result.equals("connectionLost"))){
                ResultCreateAnalyzeBean bean = new ResultCreateAnalyzeBean(result);
                if(!(bean.isQuestionIsEmpty())){  Log.i(TAG, "startActivity AnalyzeActivity.class");
                    Intent analyzeIntent = new Intent(view.getContext(), AnalyzeActivity.class);
                    analyzeIntent.putExtra("createAnalyzeList",bean.getCreateAnalyzeListAsJsonString());
                    startActivity(analyzeIntent);
                }
                else{
                    support.showSnackBarNoQuestion(rootLayout);
                }
            }
            else{
                support.showSnackBarNotConnectToServer(rootLayout);
            }

        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------
    public void getBitmapFromURL(String url) { Log.i(TAG, "getBitmapFromURL");
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL urlLink = new URL(url);
            connection = (HttpURLConnection) urlLink.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input); Log.i(TAG, "get Bitmap success");
            input.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Context getContextFromActivity(){
        return this;
    }
    public void setAppBarLayout(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(bean.getSurveyName());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }
    public void dialogEditQuestions(){
        new MaterialDialog.Builder(getContextFromActivity())
                .title("Select Edit Type")
                .items(R.array.selectEditType)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Intent editQuestionIntent = new Intent(view.getContext(), EditQuestionActivity.class);
                        editQuestionIntent.putExtra("surveyID", bean.getSurveyID());
                        editQuestionIntent.putExtra("surveyVersion", bean.getSurveyVersion());
                        editQuestionIntent.putExtra("surveyName", bean.getSurveyName());
                        editQuestionIntent.putExtra("surveyStatus", bean.getSurveyStatus());
                        if (text.toString().equals("Edit with new version")) { Log.i(TAG,"Edit with new version");
                            editQuestionIntent.putExtra("editWithNew", true);
                        } else if (text.toString().equals("Edit something")) { Log.i(TAG,"Edit something");

                        }
                        startActivity(editQuestionIntent);
                        return true;
                    }
                })
                .negativeText("Cancel")
                .positiveText("OK")
                .show();
    }
    public void dialogEditSurveyName(){
        new MaterialDialog.Builder(getContextFromActivity())
                .title("Edit Survey Name")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRangeRes(2, 128, R.color.red_700)
                .input(bean.getSurveyName(), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        if (charSequence.toString().equals(bean.getSurveyName()) || TextUtils.isEmpty(charSequence)) {
                            Snackbar.make(rootLayout, "Survey name not changed.", Snackbar.LENGTH_LONG).show();
                        } else {
                            EditSurveyNameBean editSurveyNamebean = new EditSurveyNameBean();
                            editSurveyNamebean.setSurveyID(bean.getSurveyID());
                            editSurveyNamebean.setSurveyVersion(bean.getSurveyVersion());
                            editSurveyNamebean.setSurveyName(charSequence.toString());
                            new UpdateSurveyFormTask().execute("UpdateSurveyName", new Gson().toJson(editSurveyNamebean));
                            materialDialog.dismiss();
                        }

                    }
                })
                .negativeText("Cancel")
                .show();
    }
    public void popupMenuSelectImage(){
        PopupMenu popupMenu = new PopupMenu(getContextFromActivity(),buttonEditSurveyImage);
        if(bean.getCoverImage().equals("noImage"))
            popupMenu.getMenuInflater().inflate(R.menu.menu_select_image2, popupMenu.getMenu());
        else
            popupMenu.getMenuInflater().inflate(R.menu.menu_select_image1, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_setDefaultImage) { Log.i(TAG, "SET Default Survey image");
                    new UpdateSurveyImage().execute("SetDefaultSurveyImage", new ManageJson().serializationStringToJson(
                                        "surveyID," + bean.getSurveyID(), "surveyVersion," + bean.getSurveyVersion()));
                }
                if (id == R.id.action_takePhoto) { Log.i(TAG, "SET TakePhoto");
                    cameraInTent();
                }
                if (id == R.id.action_choosePhoto) { Log.i(TAG, "SET ChoosePhoto");
                    galleryIntent();
                }
                return false;
            }
        });
        popupMenu.show();
    }
    public void cameraInTent(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
        imagePath = file.getAbsolutePath(); Log.i(TAG, "image name: " + file.getName()); Log.i(TAG, "filePath: " + imagePath);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }
    public void galleryIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_GALLERY);
    }

    private void CropImageFromCamera(Uri inUriPath,String outImagePath){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outImagePath)));
        // indicate image type and Uri
        cropIntent.setDataAndType(inUriPath, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", maxSizeImage);
        cropIntent.putExtra("outputY", maxSizeImage);
        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, REQUEST_CROP_IMAGE_FROM_CAMERA);
    }
    private void onCameraResult(){ Log.i(TAG, "onCameraResult");
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
        Log.i(TAG, "create bitmap");
        int width = bitmapOptions.outWidth;
        int height = bitmapOptions.outHeight;

        if(width>maxSizeImage || height>maxSizeImage){
            Log.i(TAG,"Bitmap has large size, " +"size = " +width +" x " +height);
            int reSize[] = manageImage.reduceWidthHeight(width, height, maxSizeImage);
            Log.i(TAG, "resize width: " + reSize[0]); Log.i(TAG, "resize height: " + reSize[1]);
            bitmap = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);
            manageImage.saveBitmapTpFile(bitmap, imagePath);
        }

        blurBitmap = new Support().blurImage(getContextFromActivity(),bitmap,25);
        surveyImage.setImageBitmap(blurBitmap);
        new UpdateSurveyImage().execute("", "");
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette p) {
                palette = p;
                statusBarColor = palette.getDarkVibrantColor(support.primaryDarkColor());
                toolBarColor = palette.getVibrantColor(support.primaryColor());
                Log.e(TAG,"statusBarColor = "+statusBarColor);
                Log.e(TAG, "toolBarColor = " + toolBarColor);
                collapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
                collapsingToolbarLayout.setContentScrimColor(toolBarColor);
            }
        });


    }
    private void onGalleryResult(Intent data){Log.i(TAG, "onGalleryResult");
        Uri path = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if(width>maxSizeImage || height>maxSizeImage){
                Log.i(TAG,"Bitmap has large size, " +"size = " +width +" x " +height);
                int reSize[] = manageImage.reduceWidthHeight(width, height, maxSizeImage);
                Log.i(TAG, "resize width: " + reSize[0]); Log.i(TAG, "resize height: " + reSize[1]);
                bitmap = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);

                imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();

                manageImage.saveBitmapTpFile(bitmap, imagePath);
                Log.i(TAG, "create new image");
                Log.e(TAG, "imagePath = " + imagePath);
            }
            else if(width<=maxSizeImage && height<=maxSizeImage){
                imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();
                manageImage.saveBitmapTpFile(bitmap, imagePath);
                Log.e(TAG, "imagePath = " + imagePath);
            }

            blurBitmap = new Support().blurImage(getContextFromActivity(),bitmap,25);
            surveyImage.setImageBitmap(blurBitmap);
            new UpdateSurveyImage().execute("","");

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette p) {
                    palette = p;
                    statusBarColor = palette.getDarkVibrantColor(support.primaryDarkColor());
                    toolBarColor = palette.getVibrantColor(support.primaryColor());
                    Log.e(TAG,"statusBarColor = "+statusBarColor);
                    Log.e(TAG, "toolBarColor = " + toolBarColor);
                    collapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
                    collapsingToolbarLayout.setContentScrimColor(toolBarColor);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public String conventDateTimeToShow(String oldDateTime){
        String[] dateTime= new String[5];
        dateTime[0] = oldDateTime.substring(0, 4); //years
        dateTime[1] = oldDateTime.substring(5, 7); //mont
        dateTime[2] = oldDateTime.substring(8, 10); //day
        dateTime[3] = oldDateTime.substring(11, 13); //hour
        dateTime[4] = oldDateTime.substring(14, 16); //min
        return dateTime[2] +"/"+dateTime[1] +"/"+dateTime[0] +" "+dateTime[3]+":"+dateTime[4];
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
