package com.jozzee.mysurvey.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.RegisterBean;
import com.jozzee.mysurvey.bean.UpdateImageBean;
import com.jozzee.mysurvey.event.ActivityResultBus;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.LoginEvent;
import com.jozzee.mysurvey.event.OnActivityResultEvent;
import com.jozzee.mysurvey.event.OnAfterRegisterEvent;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageImage;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.security.Guard;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class RegisterProfileImageFragment extends Fragment {
    public static  final String TAG = RegisterPasswordFragment.class.getSimpleName();

    private static final int REQUEST_CAMERA= 0;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CROP_IMAGE_FROM_CAMERA = 2;
    private static final int REQUEST_CROP_IMAGE_FROM_GALLERY = 3;
    private static final int ORIENTATION_0 = 0; // Portrait
    private static final int ORIENTATION_90 = 3; // Landscape right
    private static final int ORIENTATION_180 = 2;
    private static final int ORIENTATION_270 = 1; // Landscape left
    private int maxSizeImage = 1280;
    private Bitmap bitmap;
    private Bitmap bitmapReSize;
    private OnFragmentInteractionListener mListener;
    private RegisterBean registerBean;
    private View rootView;
    private LinearLayout linearLayoutProfileImage;
    private ImageView profileImage;
    private Button skip;
    private Button takePhoto;
    private Button choosePhoto;
    private Button upload;
    private File file;
    private String imagePath = "";
    private String imagePathOld = "";
    private String imageName = "surveyTempIMG.jpg";
    private ManageImage manageImage;
    private Support support;


    public static RegisterProfileImageFragment newInstance(String registerBeanAsJsonString) {
        RegisterProfileImageFragment fragment = new RegisterProfileImageFragment();
        Bundle args = new Bundle();
        args.putString("registerBeanAsJsonString", registerBeanAsJsonString);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterProfileImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) { Log.i(TAG, "onAttach");
        super.onAttach(context);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) { Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); Log.i(TAG, "setRetainInstance");
        if (getArguments() != null) {Log.i(TAG, "getArguments");
            registerBean = new RegisterBean(getArguments().getString("registerBeanAsJsonString"));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { Log.i(TAG, "onCreateView");
        rootView =  inflater.inflate(R.layout.fragment_register_profile_image, container, false);

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

        float sizeWidth = (screenWidthDp -128)*getActivity().getResources().getDisplayMetrics().density; Log.i(TAG, "sizeWidth = " + sizeWidth);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((int)sizeWidth, (int)sizeWidth);
        param.weight = 1.0f;

        linearLayoutProfileImage = (LinearLayout)rootView.findViewById(R.id.layoutProfileImage);
        profileImage = (ImageView)rootView.findViewById(R.id.imageView_uploadProfileImage);
        skip = (Button)rootView.findViewById(R.id.button_skip_uploadProfileImage);
        takePhoto = (Button)rootView.findViewById(R.id.button_takePhoto_uploadImageProfile);
        choosePhoto = (Button)rootView.findViewById(R.id.button_choosePhoto_uploadImageProfile);
        upload = (Button)rootView.findViewById(R.id.button_upload_profileImage);
        support = new Support();
        manageImage = new ManageImage();
        profileImage.setLayoutParams(param);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraInTent();
            }
        });
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(imagePath.equals(""))){
                    new UploadProfileImageTask().execute();
                }
                else{
                    support.showSnackBarNotSelectImage(rootView);
                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap != null){
                    bitmap.recycle();
                }
                if(bitmapReSize != null){
                    bitmapReSize.recycle();
                }
                if(file != null){
                    file.delete();
                }
                BusProvider.getInstance().post(new OnAfterRegisterEvent(registerBean.getAccountID()));
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) { Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            imagePath = savedInstanceState.getString("imagePath"); Log.e(TAG,"get image path from savedInstanceState");
            imagePathOld = savedInstanceState.getString("imagePathOld"); Log.e(TAG,"get image old path from savedInstanceState");
        }
    }
    @Override
    public void onStart() { Log.i(TAG, "onStart");
        super.onStart();
        if(!imagePath.equals("")){
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
            profileImage.setImageBitmap(bitmap);
        }
        else{
            Glide.with(this)
                    .load(R.drawable.im_profile)
                    .into(profileImage);
        }
    }

    @Override
    public void onResume() { Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() { Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() { Log.i(TAG, "onDestroyView");
        super.onDestroyView();
        mListener = null;
        if(bitmap != null){
            bitmap.recycle();
        }
        if(bitmapReSize != null){
            bitmapReSize.recycle();
        }
        /*if(file != null){
            file.delete();
        }*/
    }

    @Override
    public void onDestroy() { Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        /*if(file != null){
            file.delete();
        }*/


    }
    @Override
    public void onSaveInstanceState(Bundle outState) { Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", imagePath);
        outState.putString("imagePathOld", imagePathOld);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode = " + requestCode);
        Log.e(TAG, "resultCode = " + resultCode);
        if(data != null){
            Log.e(TAG,"data = "+data.getAction());
        }

        else{
            Log.e(TAG,"data = null");
        }
        //------------------------------------------------------------------------
        if(resultCode == -1){
            if(requestCode == REQUEST_CAMERA){
                CropImageFromCamera(Uri.fromFile(file), imagePath);
            }
            else if(requestCode == REQUEST_GALLERY && data != null){
                onGalleryResult(data);
            }
            else if(requestCode == REQUEST_CROP_IMAGE_FROM_CAMERA){
                onCameraResult();
            }
            else if(requestCode == REQUEST_CROP_IMAGE_FROM_GALLERY){

            }
        }
        else if(resultCode != -1 && (requestCode == REQUEST_CAMERA || requestCode == REQUEST_CROP_IMAGE_FROM_CAMERA))
            {Log.e(TAG,"Not success off activity result from camera");// may be onBackPressed;
            if(imagePathOld.equals("")){
                Glide.with(this)
                        .load(R.drawable.im_profile)
                        .into(profileImage);
                imagePath = "";
                if(file != null){
                    file.delete();
                }
            }
            else{
                Log.e(TAG,"old path  = "+imagePathOld);
                Log.e(TAG,"imagePath = " +imagePath);
                imagePath = imagePathOld;
                Log.e(TAG,"imagePath = " +imagePath);
                imagePathOld = "";
                file.delete();
                file = new File(imagePath);
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
                profileImage.setImageBitmap(bitmap);

            }
        }
    }
    private class UploadProfileImageTask extends AsyncTask<String,Void,String>{
        private String TAG  = UploadProfileImageTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public UploadProfileImageTask() {
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Upload Image...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return  new ConnectionServiceCore().uploadImage(
                    support.getURLLink(),"UploadImage","ProfileImage",
                    imagePath,registerBean.getAccountID(),0,"");
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost"))){
                UpdateImageBean bean = new UpdateImageBean(result);
                if( bean.getResult().equals("success")){
                    if(bitmap != null){
                        bitmap.recycle();
                    }
                    if(bitmapReSize != null){
                        bitmapReSize.recycle();
                    }
                    if(file != null){
                        file.delete();
                    }
                    BusProvider.getInstance().post(new OnAfterRegisterEvent(registerBean.getAccountID()));
                }

            }
            else{
                support.showSnackBarNotConnectToServer(rootView);
            }
        }
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
    public void cameraInTent(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(!(imagePath.equals(""))){
            imagePathOld = imagePath; Log.e(TAG,"save path old image");
            Log.e(TAG,"imagePathOld = "+imagePathOld);
        }
        imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".jpg";
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
        imagePath = file.getAbsolutePath(); Log.i(TAG, "image name = " + file.getName());  Log.i(TAG, "filePath = " + imagePath);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }
    public void galleryIntent(){
        if(!(imagePath.equals(""))){
            imagePathOld = imagePath; Log.e(TAG,"save path old image");
            Log.e(TAG,"imagePathOld = "+imagePathOld);
        }
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_GALLERY);
    }
    private void CropImageFromCamera(Uri inputPath,String outputPath){
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outputPath)));
            // indicate image type and Uri
            cropIntent.setDataAndType(inputPath, "image/*");
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
            bitmapReSize = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);
            bitmap.recycle();
            manageImage.saveBitmapTpFile(bitmapReSize, imagePath);
            profileImage.setImageBitmap(bitmapReSize);
        }
        else{
            if(bitmapReSize != null){
                bitmapReSize.recycle();
            }
            profileImage.setImageBitmap(bitmap); Log.i(TAG, "set bitmap to imageView");
        }
        if(!imagePathOld.equals("")){ Log.e(TAG, "delete old image path");
            File file = new File(imagePathOld);
            file.delete();
            imagePathOld = "";
        }


    }

    private void onGalleryResult(Intent data){Log.i(TAG, "onGalleryResult");
        Uri path = data.getData();
        //String realPath = getImagePath(path); Log.e(TAG, "realPath = " + realPath);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if(width>maxSizeImage || height>maxSizeImage){
                Log.i(TAG, "Bitmap has large size, " + "size = " + width + " x " + height);
                int reSize[] = manageImage.reduceWidthHeight(width, height, maxSizeImage);
                Log.i(TAG, "resize width: " + reSize[0]); Log.i(TAG, "resize height: " + reSize[1]);
                bitmapReSize = Bitmap.createScaledBitmap(bitmap, reSize[0], reSize[1], false);
                bitmap.recycle();
                imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();
                manageImage.saveBitmapTpFile(bitmapReSize, imagePath);
                Log.i(TAG, "create new image");
                Log.e(TAG, "imagePath = " + imagePath);
                profileImage.setImageBitmap(bitmapReSize);
            }
            else if(width<=maxSizeImage && height<=maxSizeImage){
                imageName = "surveyTempIMG" +new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +".jpg";
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imageName);
                imagePath = file.getAbsolutePath();
                if(bitmapReSize != null){
                    bitmapReSize.recycle();
                }
                manageImage.saveBitmapTpFile(bitmap, imagePath);
                Log.e(TAG, "imagePath = " + imagePath);
                profileImage.setImageBitmap(bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!imagePathOld.equals("")){ Log.e(TAG, "delete old image path");
            File file = new File(imagePathOld);
            file.delete();
            imagePathOld = "";
        }
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

}
