package com.jozzee.mysurvey.create;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ResultCreateSurveyForm;
import com.jozzee.mysurvey.bean.CreateSurveyFormBean;
import com.jozzee.mysurvey.bean.UpdateImageBean;
import com.jozzee.mysurvey.event.ActivityResultBus;
import com.jozzee.mysurvey.event.AddQuestionsEvent;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnActivityResultEvent;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateFormFragment extends Fragment {
    private static String TAG = CreateFormFragment.class.getSimpleName();
    private static final int REQUEST_CAMERA= 0;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CROP_IMAGE_FROM_CAMERA = 2;
    private static final int REQUEST_CROP_IMAGE_FROM_GALLERY = 3;
    private static final int ORIENTATION_0 = 0; // Portrait
    private static final int ORIENTATION_90 = 3; // Landscape right
    private static final int ORIENTATION_180 = 2;
    private static final int ORIENTATION_270 = 1; // Landscape left
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CoordinatorLayout rootLayout;
    private File file;
    private EditText editTextSurveyName;
    private EditText editTextSurveyPassword;
    private RadioGroup radioGroupSurveyType;
    private Switch switchSurveyStatus;
    private TextView textViewSurveyStatus;
    private ImageView surveyImage;
    private LinearLayout layoutImage;
    private RelativeLayout layoutButtonAddImage;
    private Button buttonAddSurveyImage;
    private Button buttonCreateSurveyForm;
    private String surveyName;
    private int surveyType = 1; // 1 = Public, 2 = Private, 3 = Password
    private String surveyPassword;
    private int surveyStatus = 1; // 1 = Offline, 2 = Online
    private int accountID; // use set create by....
    private String dateTime;
    private String imagePath = "noImage"; //part of file image to upload
    private Bitmap bitmap;
    private int maxSizeImage = 1280;
    private LinearLayout layoutPassword;
    private String saveData; //as json string to save and restore OnSavedInstanceState
    private CreateSurveyFormBean bean;
    private boolean visibilitySurveyImage;
    private boolean visibilityButtonAddSurveyImage;
    private TextInputLayout inputLayoutSurveyName;
    private TextInputLayout inputLayoutSurveyPassword;
    private RelativeLayout layoutSurveyStatus;

    ///private ProgressDialog progressDialog;

    //==================================================


    public static CreateFormFragment newInstance(int accountID) {
        CreateFormFragment fragment = new CreateFormFragment();
        Bundle args = new Bundle();
        args.putInt("accountID",accountID);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateFormFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach");
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            accountID = getArguments().getInt("accountID",0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_from, container, false);
        rootLayout = (CoordinatorLayout)rootView.findViewById(R.id.rootLayout_createForm);

        inputLayoutSurveyName = (TextInputLayout) rootView.findViewById(R.id.TextInputLayout_surveyName_createFrom);
        editTextSurveyName = (EditText)rootView.findViewById(R.id.editText_surveyName_createFrom);
        radioGroupSurveyType = (RadioGroup)rootView.findViewById(R.id.radioGroup_surveyType_crateForm);
        inputLayoutSurveyPassword =(TextInputLayout)rootLayout.findViewById(R.id.TextInputLayout_password_crateForm);
        editTextSurveyPassword = (EditText)rootView.findViewById(R.id.editText_password_crateForm);
        switchSurveyStatus = (Switch)rootView.findViewById(R.id.switch_surveyStatus_createForm);
        textViewSurveyStatus = (TextView)rootView.findViewById(R.id.textView_surveyStatus_createForm);
        surveyImage = (ImageView)rootView.findViewById(R.id.imageView_surveyImage_createForm);
        buttonAddSurveyImage = (Button)rootView.findViewById(R.id.button_addSurveyImage_createForm);
        buttonCreateSurveyForm = (Button)rootView.findViewById(R.id.button_create_createForm);
        layoutImage = (LinearLayout)rootView.findViewById(R.id.linear_imageView_createForm);
        layoutButtonAddImage = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_buttonAddImage_createFrom);
        layoutPassword = (LinearLayout)rootView.findViewById(R.id.linearLayout__password_crateForm);
        layoutSurveyStatus = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_surveyStatus_createForm);

        editTextSurveyName.addTextChangedListener(new MyTextWatcher(editTextSurveyName));
        editTextSurveyPassword.addTextChangedListener(new MyTextWatcher(editTextSurveyPassword));

        Display display = ((WindowManager)
                getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenOrientation = display.getRotation();
        Configuration configuration = getActivity().getResources().getConfiguration();
        int screenWidthDp = 0;
        if(screenOrientation == ORIENTATION_0 || screenOrientation == ORIENTATION_180){ // if Portrait
            screenWidthDp = configuration.screenWidthDp;
        }
        else{
            screenWidthDp = configuration.screenHeightDp;
        }

        float sizeWidth = (screenWidthDp -32)*getActivity().getResources().getDisplayMetrics().density; Log.i(TAG, "sizeWidth = " + sizeWidth);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((int)sizeWidth, (int)sizeWidth);
        param.weight = 1.0f;
        surveyImage.setLayoutParams(param);

        radioGroupSurveyType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_public) {
                    surveyType = 1;
                    layoutPassword.setVisibility(View.GONE);
                } else if (checkedId == R.id.radio_private) {
                    surveyType = 2;
                    layoutPassword.setVisibility(View.GONE);
                } else if (checkedId == R.id.radio_password) {
                    surveyType = 3;
                    layoutPassword.setVisibility(View.VISIBLE);
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
        switchSurveyStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    surveyStatus = 2;
                    textViewSurveyStatus.setText(R.string.online);
                    textViewSurveyStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                } else {
                    surveyStatus = 1;
                    textViewSurveyStatus.setText(R.string.offline);
                    textViewSurveyStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.red_700));
                }
            }
        });
        layoutButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPickImage(0);
            }
        });
        buttonAddSurveyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPickImage(0);
            }
        });
        surveyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPickImage(1);
            }
        });
        buttonCreateSurveyForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestData = null;
                surveyName = editTextSurveyName.getText().toString().trim();
                surveyPassword = editTextSurveyPassword.getText().toString().trim();
                if(validSurveyName()){
                    bean = new CreateSurveyFormBean();
                    bean.setSurveyName(surveyName);
                    bean.setSurveyStatus(surveyStatus);
                    bean.setSurveyType(surveyType);
                    bean.setSurveyImage(imagePath);
                    bean.setAccountID(accountID);
                    bean.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                    if(surveyType == 3 && validSurveyPassword()){
                        bean.setSurveyPassword(surveyPassword);
                        requestData = new Gson().toJson(bean);
                        Log.d(TAG, requestData);
                    }
                    else if(!(surveyType == 3)){
                        requestData = new Gson().toJson(bean);
                        Log.d(TAG, requestData);
                    }
                }
                if(requestData != null){
                    new CreateSurveyFormTask().execute(requestData);
                }
            }
        });

        editTextSurveyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextSurveyName.setHintTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){ Log.i(TAG,"Restore from savedInstanceState.");

            accountID = savedInstanceState.getInt("accountID"); //Log.i(TAG,"accountID = " +accountID);
            if(savedInstanceState.getBoolean("visibleLayoutImage",false)){
                imagePath = savedInstanceState.getString("imagePath"); Log.i(TAG,"imagePath = " +imagePath);
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
                surveyImage.setImageBitmap(bitmap);
                layoutButtonAddImage.setVisibility(View.GONE);
                layoutImage.setVisibility(View.VISIBLE);
            }
            else if(savedInstanceState.getBoolean("visibleLayoutButtonAddImage")){
                layoutImage.setVisibility(View.GONE);
                layoutButtonAddImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);

    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt("accountID", accountID);
        if(layoutImage.getVisibility() == View.VISIBLE){
            Log.i(TAG,"layoutImage is VISIBLE");
            outState.putBoolean("visibleLayoutImage",true);
            outState.putString("imagePath",imagePath);
        }
        else if(layoutButtonAddImage.getVisibility() == View.VISIBLE){
            Log.i(TAG,"buttonAddSurveyImage is VISIBLE");
            outState.putBoolean("visibleLayoutButtonAddImage",true);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult from fragment");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
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
    }
    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(OnActivityResultEvent event) {
            Log.i(TAG, "onActivityResultEvent new");
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);

        }
    };

    public void dialogPickImage(int fromWhere){ // 0 is from button addSurveyImage, 1 is from onClick imageView surveyImage
        final CharSequence[] choicePickImage;
        if(fromWhere == 0){
            choicePickImage = new CharSequence[]{"Take photo", "Choose photo"};
        }
        else{
            choicePickImage = new CharSequence[]{"Use default", "Take photo", "Choose photo"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_image)
                .setItems(choicePickImage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (choicePickImage[which].equals("Use default")) {
                            layoutImage.setVisibility(View.GONE);
                            layoutButtonAddImage.setVisibility(View.VISIBLE);
                            imagePath = "noImage";
                        }
                        if (choicePickImage[which].equals("Take photo")) {Log.i(TAG, "select image from take a photo");
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                            imagePath = file.getAbsolutePath();
                            Log.i(TAG, "image name: " + file.getName());Log.i(TAG, "filePath: " + imageName);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            getActivity().startActivityForResult(cameraIntent, REQUEST_CAMERA);

                        }
                        if (choicePickImage[which].equals("Choose photo")) {Log.i(TAG, "select image from Gallery");
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            getActivity().startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_GALLERY);
                        }
                    }
                });
        Log.i(TAG, "show dialog select image");
        builder.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    //-----------------------------------------------------------------------------------------------------------
    private class CreateSurveyFormTask  extends AsyncTask<String, Void, String> {
        private String TAG  = CreateSurveyFormTask.class.getSimpleName();
        private ProgressDialog progressDialog;
        ResultCreateSurveyForm resultCreateForm;

        public CreateSurveyFormTask() {

        }
        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Create Survey...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            String resultFromServer = "";
            String url = new Support().getURLLink();
            String command = "CreateSurveyForm";
            String requestData = params[0];
            String resultOne = new ConnectionServiceCore().doOKHttpPostString(url, command, requestData);
            if(!resultOne.equals("connectionLost")){
                resultCreateForm = new ResultCreateSurveyForm(resultOne);
                if(resultCreateForm.getResult().equals("success")) { //create from compleate in to add question
                    Log.i(TAG,"Create form success not upload image");
                    resultFromServer = resultOne;
                    //send bus to go addQuestionActivity
                }
                else if(resultCreateForm.getResult().equals("wantImage")){
                    Log.i(TAG,"Upload image");
                    //upLoadImage to sever--------------------------------------------------------
                    String resultTwo = new ConnectionServiceCore().uploadImage(
                            new Support().getURLLink(), "UploadImage", "SurveyImage",
                            imagePath, resultCreateForm.getSurveyID(), 1,"");
                    if(!(resultTwo.equals("connectionLost"))){
                        UpdateImageBean resultBean = new UpdateImageBean(resultTwo);
                        if(resultBean.getResult().equals("success")){
                            resultFromServer = resultOne;
                        }
                    }
                    else{
                        Log.i(TAG,"connectionLostUploadImage");
                        resultFromServer = "connectionLostUploadImage";
                    }
                }
            }
            else{
                Log.i(TAG,"connectionLost");
                resultFromServer = "connectionLost";
            }
            return resultFromServer;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(!(result.equals("connectionLost")) && !(result.equals("connectionLostUploadImage"))){
                if(!(imagePath.equals("noImage"))){
                    file.delete();
                }
                BusProvider.getInstance().post(new AddQuestionsEvent(resultCreateForm.getSurveyName()
                        , resultCreateForm.getSurveyID(), resultCreateForm.getSurveyVersion(),surveyStatus));
            }
            else if(result.equals("connectionLost")){

                Snackbar.make(rootView, "Not connect to Server", Snackbar.LENGTH_LONG).show();

            }
            else if(result.equals("connectionLostUploadImage")){

                Snackbar.make(rootView, "Not upload image to Server", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private class MyTextWatcher implements TextWatcher{
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           /* int id = view.getId();
            if(id == R.id.editText_surveyName_createFrom){
                validSurveyName();
            }
            else if(id == R.id.editText_password_crateForm){
                validSurveyPassword();
            }*/
        }

        @Override
        public void afterTextChanged(Editable s) {
            int id = view.getId();
            if(id == R.id.editText_surveyName_createFrom){
                validSurveyName();
            }
            else if(id == R.id.editText_password_crateForm){
                validSurveyPassword();
            }

        }
    }
    public boolean validSurveyName(){
        if(editTextSurveyName.getText().toString().trim().isEmpty()){
            inputLayoutSurveyName.setError("Enter Your Survey Name");
            requestFocus(editTextSurveyName);
            return false;
        }
        else{
            inputLayoutSurveyName.setErrorEnabled(false);
            return true;
        }
    }
    public boolean validSurveyPassword(){
        if(editTextSurveyPassword.getText().toString().trim().isEmpty()){
            inputLayoutSurveyPassword.setError("Enter Your Password");
            requestFocus(editTextSurveyPassword);
            return false;
        }
        else{
            inputLayoutSurveyPassword.setErrorEnabled(false);
            return true;
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        }
    }
    //-----------------------------------------------------------------------------------------------------------
    private void onCameraResult(){ Log.i(TAG, "onCameraResult");
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
        Log.i(TAG, "create bitmap");
        int width = bitmapOptions.outWidth;
        int height = bitmapOptions.outHeight;

        if(width>maxSizeImage || height>maxSizeImage){
            Log.i(TAG,"Bitmap has large size, " +"size = " +width +" x " +height);
            int reSize[] = reSizeImage(width, height, maxSizeImage); //reSize[0] = width, reSize[1] = height
            Log.i(TAG, "resize width: " + reSize[0]); Log.i(TAG, "resize height: " + reSize[1]);
            Bitmap bitmapReSize = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);
            reduceSizeImage(bitmapReSize, imagePath);
            layoutButtonAddImage.setVisibility(View.GONE);
            layoutImage.setVisibility(View.VISIBLE);
            surveyImage.setImageBitmap(bitmapReSize);
            Log.i(TAG,"Set Bitmap to ImageView");
        }
        else{
            layoutButtonAddImage.setVisibility(View.GONE);
            layoutImage.setVisibility(View.VISIBLE);
            surveyImage.setImageBitmap(bitmap);
            Log.i(TAG, "Set Bitmap to ImageView");
        }

    }
    private void onGalleryResult(Intent data){Log.i(TAG, "onGalleryResult");
        Uri path = data.getData();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if(width>maxSizeImage || height>maxSizeImage){
                Log.i(TAG,"Bitmap has large size, " +"size = " +width +" x " +height);
                int reSize[] = reSizeImage(width, height, maxSizeImage);
                Log.i(TAG, "resize width: " + reSize[0]); Log.i(TAG, "resize height: " + reSize[1]);
                Bitmap bitmapReSize = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);

                String imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();

                reduceSizeImage(bitmapReSize, imagePath);
                Log.i(TAG, "create new image");
                Log.e(TAG, "imagePath = " + imagePath);
                layoutButtonAddImage.setVisibility(View.GONE);
                layoutImage.setVisibility(View.VISIBLE);
                surveyImage.setImageBitmap(bitmapReSize);
                Log.i(TAG, "set new size of bitmap");
            }
            else if(width<=maxSizeImage && height<=maxSizeImage){
                String imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();
                saveBitmapTpFile(bitmap,imagePath);
                Log.e(TAG, "imagePath = " + imagePath);
                layoutButtonAddImage.setVisibility(View.GONE);
                layoutImage.setVisibility(View.VISIBLE);
                surveyImage.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int[] reSizeImage(int width,int height,int max){
        int[] reSize = new int[]{width,height};
        float cal = 0;
        if(width > max || height >max){
            if(width>height || width == height){
                cal = (float)width/max;
            }
            else if(height>width){
                cal = (float)height/max;
            }
            reSize[0] = (int)(width/cal);
            reSize[1] = (int)(height/cal);
        }
        return reSize;
    }
    public void reduceSizeImage(Bitmap inBitmap, String outImagePath){Log.i(TAG, "reduceSizeImage");
        //save replace old file image

        OutputStream outputStream = null;
        File file = new File(outImagePath);//imagePath = file.getAbsolutePath();
        try {
            outputStream = new FileOutputStream(file);
            inBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveBitmapTpFile(Bitmap inBitmap, String outImagePath){
        Log.i(TAG, "saveBitmapTpFile");
        //save replace old file image

        OutputStream outputStream = null;
        File file = new File(outImagePath);//imagePath = file.getAbsolutePath();
        try {
            outputStream = new FileOutputStream(file);
            inBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public Context getContextActivity(){
        return getActivity();
    }
}
//----------------------------------------------------------------------------------------------------------
/*
public void onActivityResult(int requestCode, int resultCode, Intent data) {Log.i(TAG, "onActivityResult from fragment");
    super.onActivityResult(requestCode, resultCode, data);
    Uri outputURI;
    if(resultCode == -1){
        if(requestCode == REQUEST_CAMERA){
            Uri inputURI = Uri.fromFile(new File(filePath));
            String fileName = "tempIMG.jpg";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
            outputURI = Uri.fromFile(file);
            filePath = file.getAbsolutePath();
            Crop.of(inputURI,outputURI).asSquare()
                    .withMaxSize(1280,1280)
                    .start(getActivity(), REQUEST_CROP_IMAGE);


        }
        if(requestCode == REQUEST_GALLERY){
            Uri inputURI = data.getData();
            String fileName = "tempIMG.jpg";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
            outputURI = Uri.fromFile(file);
            filePath = file.getAbsolutePath();
            Crop.of(inputURI,outputURI).asSquare()
                    .withMaxSize(1280,1280)
                    .start(getActivity(), REQUEST_CROP_IMAGE);


        }
        if(requestCode == REQUEST_CROP_IMAGE){
            Log.e(TAG,"CropCamera");
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(filePath, bitmapOptions);
            File file = saveBitmapToFile(bitmap);
            filePath = file.getAbsolutePath();
            surveyImage.setImageBitmap(bitmap);



        }

    }
}
*/

