package com.jozzee.mysurvey.register;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.RegisterBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.support.Validate;


public class RegisterEmailFragment extends Fragment {
    private static  final String TAG = RegisterEmailFragment.class.getSimpleName();

    private View rootView;
    private String responseData = null;
    private TextInputLayout layoutEmail;
    private EditText editTextEmail;
    private Button next;
    private Support support;
    private Validate validate;

    public static RegisterEmailFragment newInstance(){
        RegisterEmailFragment fragment = new RegisterEmailFragment();
        return fragment;
    }
    public RegisterEmailFragment() {

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

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){ Log.i(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_register_email, container, false);
        layoutEmail = (TextInputLayout)rootView.findViewById(R.id.TextInputLayout_email_forgot);
        editTextEmail = (EditText)rootView.findViewById(R.id.editText_email_register);
        next = (Button)rootView.findViewById(R.id.button_next_registerEmail);
        support = new Support();
        validate = new Validate();

        editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    next.callOnClick();
                }
                return false;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if ( validate.validEmail(editTextEmail,layoutEmail)) {
                    if (support.checkNetworkConnection(getActivity())) {
                        new CheckRepeatEmailTask().execute(editTextEmail.getText().toString().trim());
                    } else {
                        showSnackBarNotConnectInternet(rootView);
                    }
                }
            }
        });
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){

        }
    }
    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
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

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class CheckRepeatEmailTask extends AsyncTask<String,Void,String>{
        private String TAG = CheckRepeatEmailTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public CheckRepeatEmailTask() {
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("In process...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpGetString(support.getURLLink() +"?command=CheckRepeatEmail&email="+params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost"))){
                if(result.equals("1")){
                    Log.e(TAG, "Email can use");
                    RegisterBean registerBean = new RegisterBean();
                    registerBean.setEmail(editTextEmail.getText().toString().trim());
                    RegisterNameFragment registerNameFragment = new RegisterNameFragment().newInstance(new Gson().toJson(registerBean));
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_register, registerNameFragment).commit();
                }
                else{
                    Log.e(TAG,"Email repeat");
                    layoutEmail.setError("Email Repeat");
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
}
