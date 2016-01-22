package com.jozzee.mysurvey.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.LoginEvent;
import com.jozzee.mysurvey.forgot.ForGotPasswordActivity;
import com.jozzee.mysurvey.register.RegisterActivity;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.support.Validate;



public class SurveyAccountUnLoginFragment extends Fragment {
    private static String TAG = SurveyAccountUnLoginFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CoordinatorLayout rootLayout;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button login;
    private TextView forgot;
    private TextView register;
    private TextView error;
    private String email;
    private String password;
    private Support support;
    private Validate validate;


    public static SurveyAccountUnLoginFragment newInstance() { //use fragment from this for sent data to fragment
        SurveyAccountUnLoginFragment fragment = new SurveyAccountUnLoginFragment();
        return fragment;
    }

    public SurveyAccountUnLoginFragment() {
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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_survey_account_unlogin, container, false); // create rootView
        layoutEmail = (TextInputLayout)rootView.findViewById(R.id.TextInputLayout_email_unLogin);
        layoutPassword = (TextInputLayout)rootView.findViewById(R.id.TextInputLayout_password_unLogin);
        editTextEmail = (EditText)rootView.findViewById(R.id.editText_email_unLogin);
        editTextPassword = (EditText)rootView.findViewById(R.id.editText_password_unLogin);
        login = (Button)rootView.findViewById(R.id.button_login_unLogin);
        forgot = (TextView)rootView.findViewById(R.id.textView_forgot_unLogin);
        register = (TextView)rootView.findViewById(R.id.textView_register_unLogin);
        error = (TextView)rootView.findViewById(R.id.textView_error_unLogin);

        editTextEmail.addTextChangedListener(new MyTextWatcher(editTextEmail));
        editTextPassword.addTextChangedListener(new MyTextWatcher(editTextPassword));

        support = new Support();
        validate = new Validate();

        editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    validate.validEmail(editTextEmail,layoutEmail);
                }
                return false;
            }
        });

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    login.callOnClick();
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate.validEmail(editTextEmail, layoutEmail) && validate.validPassword(editTextPassword,layoutPassword,6,32)) {
                    if (support.checkNetworkConnection(getActivity())) {
                        String requestData = new ManageJson().serializationStringToJson(
                                "email," + editTextEmail.getText().toString().trim(),
                                "password," + editTextPassword.getText().toString().trim());
                        new LoginTask().execute(requestData);
                    } else {
                        showSnackBarNotConnectInternet(rootView);
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(v.getContext(), ForGotPasswordActivity.class);
                startActivity(forgotIntent);
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
        mListener = null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    //-----------------------------------------------------------------------------------
    private class LoginTask extends AsyncTask<String, Void, String> {
        private String TAG = LoginTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        public LoginTask() {
            //constructor
        }
        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Login....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpPostString(support.getURLLink(), "Login", params[0]);
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost"))){
                JsonObject jsonObject = new Gson().fromJson(result,JsonObject.class);
                if (jsonObject.get("resultLogin").getAsBoolean()) {
                    int accountID = jsonObject.get("accountID").getAsInt();
                    BusProvider.getInstance().post(new LoginEvent(accountID)); //sent event to MainActivity for refresh aap
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
            else {
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
    private class MyTextWatcher implements TextWatcher{
        private View view;
        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            error.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public final static boolean isEmailValid(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
    //-----------------------------------------------------------------------------------------------------------------------
    /* public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }*/
    //--------------------------------------------------------------------------------------------------------------------
    /*public static SurveyAccountUnLoginFragment newInstance(String param1, String param2) {
        SurveyAccountUnLoginFragment fragment = new SurveyAccountUnLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    //---------------------------------------------------------------------------------------------------------------------
    /* TextView textView = (TextView)rootView.findViewById(R.id.textViewUnLogin);
        textView.setText(textView.getText().toString().trim() + String.valueOf(sharedPreferences.getBoolean("loginOnApps", false)));
        Button login = (Button)rootView.findViewById(R.id.button_login_SurveyAccountUnLoginFragment);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new LoginEvent("jozzeezaadee@gmail.com","1234"));
            }
        });*/
    //---------------------------------------------------------------------------------------------------------------------------
