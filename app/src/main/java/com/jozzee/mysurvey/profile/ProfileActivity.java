package com.jozzee.mysurvey.profile;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.AccountDataBean;
import com.jozzee.mysurvey.bean.ManageProfileBean;
import com.jozzee.mysurvey.bean.UpdateImageBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnBackPressedFromViewProfileEvent;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageImage;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.support.Validate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private static String TAG = ProfileActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA= 0;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CROP_IMAGE_FROM_CAMERA = 2;
    private static final int REQUEST_CROP_IMAGE_FROM_GALLERY = 3;
    private static final int ACTION_EDIT_NAME = 0;
    private static final int ACTION_EDIT_EMAIL = 1;
    private static final int ACTION_EDIT_PASSWORD = 2;
    private CoordinatorLayout rootLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private ImageView profileImage;
    private RelativeLayout layoutEditProfileImage;
    private ProgressBar progressBarProfileImage;
    private AccountDataBean accountDataBean;
    private Bundle bundle;
    private boolean haveEdit = false;
    private TextView name;
    private TextView email;
    private EditText password;
    private ImageView editName;
    private ImageView editEmail;
    private ImageView editPassword;
    private RelativeLayout layoutEditName;
    private RelativeLayout layoutEditEmail;
    private RelativeLayout layoutEditPassword;
    private Bitmap bitmap;
    private Bitmap blurBitmap;
    private File file;
    private ManageImage manageImage;
    private Support support;
    private String imagePath;
    private String imagePathOld;
    private String imageName;
    private int maxSizeImage = 1280;
    private Validate validate;


    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
        setContentView(R.layout.activity_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        bundle = getIntent().getExtras();
        if(bundle != null) { Log.i(TAG, "getBean from Bundle");
            accountDataBean = new AccountDataBean(bundle.getString("accountDataBean"));
        }
        if(savedInstanceState != null){ Log.i(TAG, "getBean from savedInstanceState");
            accountDataBean = new AccountDataBean(savedInstanceState.getString("backupData"));
        }
        //----------------------------------------------------------------------
        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_profile);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout_profile);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout_profile);
        collapsingToolbarLayout.setTitle(accountDataBean.getName());

        toolbar = (Toolbar)findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        profileImage = (ImageView)findViewById(R.id.imageView_profile);
        progressBarProfileImage = (ProgressBar)findViewById(R.id.progressBar_ImageViewProfile);
        layoutEditProfileImage = (RelativeLayout)findViewById(R.id.relativeLayout_editSurveyImage_profile);

        layoutEditName = (RelativeLayout)findViewById(R.id.layoutName_profile);
        layoutEditEmail = (RelativeLayout)findViewById(R.id.layoutEmail_profile);
        layoutEditPassword = (RelativeLayout)findViewById(R.id.layoutPassword_profile);
        editName = (ImageView)findViewById(R.id.imageView_editName_profile);
        editEmail = (ImageView)findViewById(R.id.imageView_editEmail_profile);
        editPassword = (ImageView)findViewById(R.id.imageView_editPassword_profile);
        name = (TextView)findViewById(R.id.textView_name_profile);
        email = (TextView)findViewById(R.id.textView_email_profile);
        password = (EditText)findViewById(R.id.editText_password_profile);

        manageImage = new ManageImage();
        support = new Support();

        if((accountDataBean.getProfileImage() != null)){
            new GetBitMapTask().execute(accountDataBean.getProfileImage());
        }
        else{
            profileImage.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.im_profile));
        }

        name.setText(accountDataBean.getName());
        email.setText(accountDataBean.getEmail());
        //password.setText(accountDataBean.getPassword());

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuSelectImage();
            }
        });
        layoutEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage.callOnClick();
            }
        });

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditName.callOnClick();
            }
        });
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditEmail.callOnClick();
            }
        });
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditPassword.callOnClick();
            }
        });
        layoutEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckPassword(ACTION_EDIT_NAME);
            }
        });
        layoutEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckPassword(ACTION_EDIT_EMAIL);
            }
        });
        layoutEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckPassword(ACTION_EDIT_PASSWORD);
            }
        });

    }
    @Override
    protected void onStart(){ Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume(){  Log.i(TAG, "onResume");
        super.onResume();

    }

    @Override
    protected void onPause() { Log.i(TAG, "onPause");
        super.onPause();

    }

    @Override
    protected void onStop(){  Log.i(TAG, "onStop");
        super.onStop();
        if(haveEdit){ Log.e(TAG,"have edit send bus to fragment on login");
            haveEdit = false;
            new OnBackPressedTask().execute();
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
    public void onSaveInstanceState(Bundle outState) { Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("backupData", new Gson().toJson(accountDataBean));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId(); Log.i(TAG, "id = " + id);

        if (id == R.id.action_settings) {
            return true;
        }
        if(id == 16908332){
            onBackPressed();
            return true;
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
    public void dialogEditEmail(){
        new MaterialDialog.Builder(getContextFromActivity())
                .title("Enter Your Email")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(email.getText().toString().trim(), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        if (validate.validEmail2(charSequence) && !(charSequence.toString().equals(accountDataBean.getEmail()))) {
                            ManageProfileBean bean = new ManageProfileBean();
                            bean.setAction(ACTION_EDIT_EMAIL);
                            bean.setAccountID(accountDataBean.getAccountID());
                            bean.setEmail(charSequence.toString());
                            new ManageProfileTask().execute(new Gson().toJson(bean));
                        } else if (charSequence.toString().equals(accountDataBean.getEmail())) {
                            support.showSnackBarEmailNotChange(rootLayout);
                        } else {
                            onEmailNotValid();
                        }


                    }
                })
                .negativeText("Cancel")
                .show();
    }
    public void dialogEditName(){
         new MaterialDialog.Builder(getContextFromActivity())
                        .title("Enter Your Name")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .inputRange(1, 128)
                        .input(name.getText().toString().trim(), "", false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                                if (!(charSequence.toString().equals(accountDataBean.getName()))) {
                                    ManageProfileBean bean = new ManageProfileBean();
                                    bean.setAction(ACTION_EDIT_NAME);
                                    bean.setAccountID(accountDataBean.getAccountID());
                                    bean.setName(charSequence.toString());
                                    new ManageProfileTask().execute(new Gson().toJson(bean));
                                } else {
                                    support.showSnackBarNameNotChange(rootLayout);
                                }

                            }
                        })
                 .negativeText("Cancel")
                 .show();
    }
    public void dialogEditPassword(){
        new MaterialDialog.Builder(this)
                .title("Enter Your New Password")
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .inputRange(6, 32)
                .input("Password", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        ManageProfileBean bean = new ManageProfileBean();
                        bean.setAction(ACTION_EDIT_PASSWORD);
                        bean.setAccountID(accountDataBean.getAccountID());
                        bean.setPassword(charSequence.toString());
                        new ManageProfileTask().execute(new Gson().toJson(bean));
                    }
                })
                .negativeText("Cancel")
                .show();
    }
    public void dialogCheckPassword(final int action){
        new MaterialDialog.Builder(this)
                .title("Enter Your Password")
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("Password", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        //View view = getActivity().getCurrentFocus();
                        //InputMethodManager imm = (InputMethodManager)getContextFromActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        if (charSequence.toString().length() < 6 || charSequence.toString().length() > 32) {
                            onIncorrectPassword();
                        } else {
                            new CheckAccountPasswordTask(action).execute(
                                    String.valueOf(accountDataBean.getAccountID()), charSequence.toString());
                        }


                    }
                })
                .negativeText("Cancel")
                .show();
    }
    public void onIncorrectPassword(){
        new MaterialDialog.Builder(this)
                .content("Incorrect password")
                .positiveText("OK")
                .show();
    }
    public void onEmailNotValid(){
        new MaterialDialog.Builder(this)
                .content("Email Not Valid.")
                .positiveText("OK")
                .show();
    }
    public void onManageProfileTaskCallback(String result){ Log.i(TAG, "onManageProfileTaskCallback");
        if(!(result.equals("connectionLost"))){
            ManageProfileBean bean = new ManageProfileBean(result);
            if(bean.getAction() == ACTION_EDIT_NAME){
                accountDataBean.setName(bean.getName());
                name.setText(accountDataBean.getName());
                collapsingToolbarLayout.setTitle(accountDataBean.getName());
                haveEdit = true;
            }
            else if(bean.getAction() == ACTION_EDIT_EMAIL){
                accountDataBean.setEmail(bean.getEmail());
                email.setText(accountDataBean.getEmail());
                haveEdit = true;
            }
            else if(bean.getAction() == ACTION_EDIT_PASSWORD){
                haveEdit = true;
            }
        }
        else{
            support.showSnackBarNotConnectToServer(rootLayout);
        }
    }
    public void onUpdateProfileImageTaskCallback(String result){ Log.i(TAG, "onUpdateProfileImageTaskCallback");
        if(!(result.equals("connectionLost"))){
            UpdateImageBean bean = new UpdateImageBean(result);
            if(bean.getFrom().equals("SetDefaultProfileImage") && bean.getResult().equals("success")){
                profileImage.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.im_profile));
                accountDataBean.setProfileImage(null);
                collapsingToolbarLayout.setContentScrimColor(support.primaryColor());
                collapsingToolbarLayout.setStatusBarScrimColor(support.primaryDarkColor());
                haveEdit = true;
                bitmap.recycle();

            }
            else if(bean.getFrom().equals("UpdateImage") && bean.getResult().equals("success")){
                profileImage.setImageBitmap(bitmap);
                accountDataBean.setProfileImage(bean.getUrlImage());
                blurBitmap.recycle();
                file.delete();
                //new ClearDiskCacheGlide().execute(this);
                haveEdit = true;
            }
            else if(bean.getFrom().equals("UploadImage") && bean.getResult().equals("success")){
                profileImage.setImageBitmap(bitmap);
                accountDataBean.setProfileImage(bean.getUrlImage());
                blurBitmap.recycle();
                file.delete();
                //new ClearDiskCacheGlide().execute(this);
                haveEdit = true;
            }
        }
        else{
            support.showSnackBarNotConnectToServer(rootLayout);
        }

    }
    public void onGetBitmapCallback(){ Log.i(TAG, "onGetBitmapCallback");
        manageImage.imageViewAnimatedChange(this, profileImage, bitmap);

    }
    private class OnBackPressedTask extends AsyncTask<String, Void, String> {
        private String TAG = OnBackPressedTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"AfterRegisterTask");
                    BusProvider.getInstance().post(new OnBackPressedFromViewProfileEvent(accountDataBean));
                }
            });
            return null;
        }
    }
    private class ManageProfileTask extends AsyncTask<String,Void,String>{
        private String TAG  = ManageProfileTask.class.getSimpleName();

        private ProgressDialog progressDialog;

        public ManageProfileTask(){
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
            return new ConnectionServiceCore().doOKHttpPostString(
                        support.getURLLink(),"ManageProfile",params[0]);
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onManageProfileTaskCallback(result);
        }
    }
    private class UpdateProfileImageTask extends AsyncTask<String,Void,String>{
        private String TAG  = UpdateProfileImageTask.class.getSimpleName();
        private boolean setDefault = false;
        public UpdateProfileImageTask(boolean setDefault){
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            support.setProgressBarColor(progressBarProfileImage, Color.WHITE);
            if(setDefault){
                bitmap = support.blurImage(getContextFromActivity(),bitmap,25);
                profileImage.setImageBitmap(bitmap);
                progressBarProfileImage.setVisibility(View.VISIBLE);
            }
            else{
                progressBarProfileImage.setVisibility(View.VISIBLE);
            }
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            if(params[0].equals("SetDefaultProfileImage")){ Log.i(TAG, "SetDefaultProfileImage");
                return new ConnectionServiceCore().doOKHttpPostString(
                        support.getURLLink(),params[0],params[1]);
            }
            else{
                if(accountDataBean.getProfileImage() != null){ Log.i(TAG, "UpdateImage");
                    return new ConnectionServiceCore().uploadImage(
                            support.getURLLink(),"UpdateImage","ProfileImage",
                            imagePath,accountDataBean.getAccountID(),0,support.getCodeImage(accountDataBean.getProfileImage()));

                }
                else{ Log.i(TAG, "UploadImage");
                    return new ConnectionServiceCore().uploadImage(
                            support.getURLLink(),"UploadImage","ProfileImage",
                            imagePath,accountDataBean.getAccountID(),0,support.getCodeImage(accountDataBean.getProfileImage()));
                }
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressBarProfileImage.setVisibility(View.GONE);
            onUpdateProfileImageTaskCallback(result);

        }
    }
    private class GetBitMapTask extends AsyncTask<String, Void, String>{
        private  String TAG  = GetBitMapTask.class.getSimpleName();

        public GetBitMapTask() {
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            support.setProgressBarColor(progressBarProfileImage,Color.WHITE);
            progressBarProfileImage.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            getBitmapFromURL(params[0]);
            if(bitmap != null){
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette p) {
                        collapsingToolbarLayout.setContentScrimColor(p.getVibrantColor(support.primaryColor()));
                        collapsingToolbarLayout.setStatusBarScrimColor(p.getDarkVibrantColor(support.primaryDarkColor()));
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressBarProfileImage.setVisibility(View.GONE);
            onGetBitmapCallback();
        }
    }
    private class CheckAccountPasswordTask extends AsyncTask<String,Void,String>{
        private  String TAG  = CheckAccountPasswordTask.class.getSimpleName();

        private int action;
        private  ProgressDialog progressDialog;

        public CheckAccountPasswordTask(int action) {
            this.action = action;
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
        protected String doInBackground(String... params) { Log.i(TAG, "onPreExecute");
            return new ConnectionServiceCore().doOKHttpPostString(
                    support.getURLLink(),"CheckAccountPassword",
                    new ManageJson().serializationStringToJson("accountID,"+params[0],"password,"+params[1]));
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!result.equals("connectionLost")){
                if(result.equals("1")){
                    if(action == ACTION_EDIT_NAME){
                        dialogEditName();
                    }
                    else if(action == ACTION_EDIT_EMAIL){
                        dialogEditEmail();
                    }
                    else if(action == ACTION_EDIT_PASSWORD){
                        dialogEditPassword();
                    }
                }
                else{
                    onIncorrectPassword();
                }
            }
            else{
                support.showSnackBarNotConnectToServer(rootLayout);
            }
        }
    }
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
    public void popupMenuSelectImage(){
        PopupMenu popupMenu = new PopupMenu(this,profileImage);
        if(accountDataBean.getProfileImage()== null)
            popupMenu.getMenuInflater().inflate(R.menu.menu_select_image2, popupMenu.getMenu());
        else
            popupMenu.getMenuInflater().inflate(R.menu.menu_select_image1, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_setDefaultImage) {
                    Log.e(TAG, "SET Default");
                    new UpdateProfileImageTask(true).execute(
                            "SetDefaultProfileImage",String.valueOf(accountDataBean.getAccountID()));
                }
                if (id == R.id.action_takePhoto) {
                    Log.e(TAG, "SET TakePhoto");
                    cameraInTent();
                }
                if (id == R.id.action_choosePhoto) {
                    Log.e(TAG, "SET ChoosePhoto");
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
        cropIntent.putExtra("outputX",maxSizeImage);
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
            Bitmap bitmapReSize = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);
            manageImage.saveBitmapTpFile(bitmapReSize, imagePath);
        }

        blurBitmap = support.blurImage(getContextFromActivity(),bitmap,25);
        profileImage.setImageBitmap(blurBitmap);
        new UpdateProfileImageTask(false).execute("");
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette p) {
                collapsingToolbarLayout.setContentScrimColor(p.getVibrantColor(support.primaryColor()));
                collapsingToolbarLayout.setStatusBarScrimColor(p.getDarkVibrantColor(support.primaryDarkColor()));
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
                Bitmap bitmapReSize = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);

                imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();

                manageImage.saveBitmapTpFile(bitmapReSize, imagePath);
                Log.i(TAG, "create new image");
                Log.e(TAG, "imagePath = " + imagePath);
            }
            else if(width<=R.integer.maxSize && height<=R.integer.maxSize){
                imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();
                manageImage.saveBitmapTpFile(bitmap, imagePath);
                Log.e(TAG, "imagePath = " + imagePath);
            }

            blurBitmap = new Support().blurImage(getContextFromActivity(),bitmap,25);
            profileImage.setImageBitmap(blurBitmap);
            new UpdateProfileImageTask(false).execute("");

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette p) {
                    collapsingToolbarLayout.setContentScrimColor(p.getVibrantColor(support.primaryColor()));
                    collapsingToolbarLayout.setStatusBarScrimColor(p.getDarkVibrantColor(support.primaryDarkColor()));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Context getContextFromActivity(){
        return this;
    }
}
