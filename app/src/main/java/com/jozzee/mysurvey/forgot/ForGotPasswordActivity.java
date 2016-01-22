package com.jozzee.mysurvey.forgot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.jozzee.mysurvey.support.Validate;

public class ForGotPasswordActivity extends AppCompatActivity {
    private static String TAG = ForGotPasswordActivity.class.getSimpleName();

    private RelativeLayout rootlayout;
    private TextInputLayout layoutEmail;
    private EditText editTextEmail;
    private Button sendEmail;
    private Validate validate;
    private Support support;

    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_got_password);

        rootlayout = (RelativeLayout)findViewById(R.id.rootLayout_forgot);
        layoutEmail = (TextInputLayout)findViewById(R.id.TextInputLayout_email_forgot);
        editTextEmail = (EditText)findViewById(R.id.editText_email_forgot);
        sendEmail = (Button)findViewById(R.id.button_sendEmail_forgot);
        validate = new Validate();
        support = new Support();

        editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    sendEmail.callOnClick();
                }
                return false;
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getContextFromActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if ( validate.validEmail(editTextEmail,layoutEmail)) {
                    if (support.checkNetworkConnection(getContextFromActivity())) {
                        new ForgotPasswordTask().execute(editTextEmail.getText().toString().trim());
                    } else {
                        showSnackBarNotConnectInternet(rootlayout);
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume(){ Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
    }

    @Override
    protected void onPause() { Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart(){ Log.i(TAG, "onRestart");
        super.onRestart();
    }
    @Override
    protected void onDestroy(){ Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
    @Override
    public void onBackPressed() { Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_for_got_password, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();
        Log.i(TAG, "id = " + id);
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onForgotPasswordTaskCallback(String result){ Log.i(TAG, "onForgotPasswordTaskCallback");
        if(result.equals("1")){
            new MaterialDialog.Builder(getContextFromActivity())
                    .title("Check Your Email!")
                    .content("A reset password has been sent to your email.")
                    .positiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            finish();
                        }
                    })
                    .show();
        }
        else if(result.equals("0")){
            new MaterialDialog.Builder(getContextFromActivity())
                    .content("Incorrect Email")
                    .positiveText("OK")
                    .show();
        }
        else {
            support.showSnackBarNotConnectToServer(rootlayout);
        }

    }
    private class ForgotPasswordTask extends AsyncTask<String, Void, String>{
        private String TAG = ForgotPasswordTask.class.getSimpleName();
        private ProgressDialog progressDialog;

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
            return new ConnectionServiceCore().doOKHttpGetString(
                    support.getURLLink()+"?command=ForgotPassword&email="+params[0]);
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onForgotPasswordTaskCallback(result);
        }
    }
    public Context getContextFromActivity(){
        return this;
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
