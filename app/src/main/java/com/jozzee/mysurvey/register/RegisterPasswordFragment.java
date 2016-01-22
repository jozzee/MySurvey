package com.jozzee.mysurvey.register;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class RegisterPasswordFragment extends Fragment {
    public static  final String TAG = RegisterPasswordFragment.class.getSimpleName();

    private RegisterBean registerBean;
    private View rootView;
    private TextInputLayout layoutPassword;
    private EditText editTextPassword;
    private Button register;
    private Validate validate;
    private Support support;

    public static RegisterPasswordFragment newInstance(String registerBeanAsJsonString){
        RegisterPasswordFragment fragment = new RegisterPasswordFragment();
        Bundle args = new Bundle();
        args.putString("registerBeanAsJsonString",registerBeanAsJsonString);
        fragment.setArguments(args);
        return fragment;
    }
    public RegisterPasswordFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_register_password, container, false);

        layoutPassword = (TextInputLayout)rootView.findViewById(R.id.TextInputLayout_password_register);
        editTextPassword = (EditText)rootView.findViewById(R.id.editText_password_register);
        register = (Button)rootView.findViewById(R.id.button_register_registerEmail);
        validate = new Validate();
        support = new Support();

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register.callOnClick();
                }
                return false;
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(validate.validPassword(editTextPassword, layoutPassword,6,32)){
                    registerBean.setPassword(editTextPassword.getText().toString().trim());
                    new RegisterTask().execute(new Gson().toJson(registerBean));
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
    public void onRegisterTaskCallback(String result){
        if(!(result.equals("connectionLost"))){
            registerBean.setAccountID(Integer.parseInt(result));
            //go to fragment upload image
            RegisterProfileImageFragment profileImageFragment = new RegisterProfileImageFragment()
                    .newInstance(new Gson().toJson(registerBean));
            getFragmentManager().beginTransaction().replace(R.id.fragment_container_register, profileImageFragment).commit();
        }
        else{
            support.showSnackBarNotConnectToServer(rootView);
        }

    }
    private class RegisterTask extends AsyncTask<String,Void,String>{
        private String TAG = RegisterTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public RegisterTask() {
        }

        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Register...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(),"Register",params[0]);
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onRegisterTaskCallback(result);
        }
    }
}
