package com.jozzee.mysurvey.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.AccountDataBean;
import com.jozzee.mysurvey.create.CreateSurveyActivity;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.LogoutEvent;
import com.jozzee.mysurvey.event.OnBackPressedFromViewProfileEvent;
import com.jozzee.mysurvey.mysurvey.MySurveyActivity;
import com.jozzee.mysurvey.profile.ProfileActivity;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class SurveyAccountOnLoginFragment extends Fragment {
    private static String TAG = SurveyAccountOnLoginFragment.class.getSimpleName();


    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CoordinatorLayout rootLayout;
    private LinearLayout layoutMainView;
    private RelativeLayout layoutProfile;
    private LinearLayout layoutMySurvey;
    private LinearLayout layoutCreateSurvey;
    private LinearLayout layoutLogout;
    private ImageView profileImage;
    private TextView profileName;
    private ProgressBar progressBar;
    private ImageView retry;
    private int accountID;
    private AccountDataBean accountDataBean;
    private String backupData = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private Support support;
    private NestedScrollView nestedScrollView;
    private Bitmap bitmap;
    private Gson gson;



    // TODO: Rename and change types and number of parameters
    public static SurveyAccountOnLoginFragment newInstance(int accountID) {
        SurveyAccountOnLoginFragment fragment = new SurveyAccountOnLoginFragment();
        Bundle args = new Bundle();
        args.putInt("accountID", accountID);
        fragment.setArguments(args);
        return fragment;
    }
    public SurveyAccountOnLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            this.accountID = getArguments().getInt("accountID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_survey_account_onlogin, container, false); // create rootView
        layoutMainView = (LinearLayout)rootView.findViewById(R.id.linearLayout_mainView_onLogin);
        layoutProfile = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_profile_onLogin);
        layoutMySurvey = (LinearLayout)rootView.findViewById(R.id.linearLayout_mySurvey_onLogin);
        layoutCreateSurvey = (LinearLayout)rootView.findViewById(R.id.linearLayout_createSurvey_onLogin);
        layoutLogout = (LinearLayout)rootView.findViewById(R.id.linearLayout_logOut_onLogin);
        nestedScrollView = (NestedScrollView)rootView.findViewById(R.id.nestedScrollView_onLogin);
        nestedScrollView.setVisibility(View.GONE);
        profileImage = (ImageView)rootView.findViewById(R.id.imageView_profileImage_onLogin);
        profileName = (TextView)rootView.findViewById(R.id.textView_profileName_onLogin);
        retry = (ImageView)rootView.findViewById(R.id.imageView_retry_onLogin);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_onLogin);
        support = new Support();
        gson = new Gson();

        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.e(TAG,"Click view profile");
                Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                profileIntent.putExtra("accountDataBean",new Gson().toJson(accountDataBean));
                startActivity(profileIntent);
            }
        });
        layoutMySurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click My Survey");
                Intent mySurveyIntent = new Intent(v.getContext(), MySurveyActivity.class);
                startActivity(mySurveyIntent);
            }
        });
        layoutCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Log.i(TAG, "Click Create Survey");
                Intent createSurveyIntent = new Intent(v.getContext(), CreateSurveyActivity.class);
                startActivity(createSurveyIntent);
            }
        });
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Log.i(TAG, "Click LogOut");
                new LogoutTask().execute();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetry();
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){Log.i(TAG, "restore from savedInstanceState");
            backupData = savedInstanceState.getString("backupData","");
            connectionLost = savedInstanceState.getBoolean("connectionLost");
            onLoad = savedInstanceState.getBoolean("onLoad");
            if(savedInstanceState.getBoolean("visibleProgressbar",false)){
                Log.i(TAG,"progressbar is View.VISIBLE");
                progressBar.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleRetry",false)){
                Log.i(TAG,"retry is View.VISIBLE");
                retry.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onStart() { Log.i(TAG, "onStart");
        super.onStart();

        if (backupData.equals("") && !(connectionLost) && !(onLoad)) {Log.i(TAG, "Not have backup data, have connect server, and not downloading data from server");
            if(support.checkNetworkConnection(getActivity())) {//check connect internet (wifi or service)
                new RequestAccountDataTask().execute();
            }
            else{
                retry.setVisibility(View.VISIBLE);
                showSnackBarNotConnectInternet(rootView);
            }
        }
        else if(connectionLost){ Log.i(TAG, "Not connect to server");
            retry.setVisibility(View.VISIBLE);
        }
        else if(onLoad){ Log.i(TAG, "Downloading and not have backup data");
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(!backupData.equals("")){ Log.i(TAG, "have backup data ,not downloading, have connect server");
            accountDataBean = new AccountDataBean(backupData);
            if(accountDataBean.getProfileImage() != null){
                Glide.with(this)
                        .load(accountDataBean.getProfileImage())
                        .into(profileImage);
            }
            profileName.setText(accountDataBean.getName());
            nestedScrollView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onResume() {  Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);

    }

    @Override
    public void onPause() { Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStop() {  Log.i(TAG, "onStop");
        super.onStop();

    }
    @Override
    public void onDestroyView() {  Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {  Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {  Log.i(TAG, "onDetach");
        super.onDetach();
        mListener = null;

    }
    @Override
    public void onSaveInstanceState(Bundle outState) { Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("backupData",backupData);
        outState.putBoolean("connectionLost", connectionLost);
        outState.putBoolean("onLoad", onLoad);
        if(progressBar.getVisibility() == View.VISIBLE){
            Log.i(TAG,"progressbar save visibility View.VISIBLE");
            outState.putBoolean("visibleProgressbar",true);
        }
        if(retry.getVisibility() == View.VISIBLE){
            Log.i(TAG, "retry save visibility View.VISIBLE");
            outState.putBoolean("visibleRetry", true);
        }
    }
    @Subscribe
    public void onBackPressedFromViewProfile(OnBackPressedFromViewProfileEvent event){ Log.i(TAG, "onBackPressedFromViewProfile");
        accountDataBean = event.getAccountDataBean();
        backupData = gson.toJson(accountDataBean);
        if(accountDataBean.getProfileImage()!= null){
            Glide.with(this)
                    .load(accountDataBean.getProfileImage())
                    .into(profileImage);
        }
        else{
            profileImage.setImageDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.im_profile));
        }
        profileName.setText(accountDataBean.getName());
    }
    public void onRequestAccountDataTaskCallback(String result){  Log.d(TAG, "onRequestAccountDataTaskCallback");
        if(!(result.equals("connectionLost"))){
            accountDataBean = new AccountDataBean(result);
            if(accountDataBean.getProfileImage() != null){
                Glide.with(this)
                        .load(accountDataBean.getProfileImage())
                        .into(profileImage);
            }
            profileName.setText(accountDataBean.getName());
            nestedScrollView.setVisibility(View.VISIBLE);
            backupData = gson.toJson(accountDataBean);
        }
        else{
            connectionLost = true;
            retry.setVisibility(View.VISIBLE);
            support.showSnackBarNotConnectToServer(rootView);
        }

    }
    public void onRetry(){
        backupData ="";
        connectionLost = false;
        onLoad = false;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }
    //------------------------------------------------------------------------------------
    private class RequestAccountDataTask extends AsyncTask<String, Void, String> {
        private String TAG  = RequestAccountDataTask.class.getSimpleName();

        public RequestAccountDataTask() {

        }

        @Override
        protected void onPreExecute() {   Log.i(TAG, "onPreExecute");
            onLoad = true;
            support.setProgressBarColor(progressBar,support.accentClolr());
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {   Log.i(TAG, "doInBackground");
            return  new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),
                    "requestAccountData",
                    new ManageJson().serializationStringToJson("accountID,"+accountID));
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE);
            onRequestAccountDataTaskCallback(result);
        }
    }
    private class LogoutTask extends AsyncTask<String, Void, String> {
        private  String TAG = LogoutTask.class.getSimpleName();
        ProgressDialog progressDialog;

        public LogoutTask() {

        }
        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Logout...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),
                    "Logout",new ManageJson().serializationStringToJson("accountID,"+accountID));
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if (!(result.equals("connectionLost"))) {
                JsonObject jsonObject = new Gson().fromJson(result,JsonObject.class);
                if (jsonObject.get("resultLogout").getAsString().equals("success")) {
                    BusProvider.getInstance().post(new LogoutEvent(accountID));
                }
            }
            else{
                support.showSnackBarNotConnectToServer(rootView);
            }
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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}
  /* @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("button",testLogin.getText().toString().trim());

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            testLogin.setText(savedInstanceState.getShort("button"));

        }
    }
*/
//----------------------------------------------------------------------------------------------


