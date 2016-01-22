package com.jozzee.mysurvey.register;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


import com.google.gson.Gson;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.RegisterBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnAfterRegisterEvent;
import com.jozzee.mysurvey.event.OnBackPressedAfterRegisterEvent;
import com.jozzee.mysurvey.event.OnSendQuestionEvent;
import com.squareup.otto.Subscribe;

public class RegisterActivity extends FragmentActivity {
    public static  final String TAG = RegisterActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean refresh = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);// Register ourselves so that we can provide the initial value.
        sharedPreferences = getSharedPreferences("LOG", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_register);
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
*/
        RegisterEmailFragment emailFragment = new RegisterEmailFragment().newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_register, emailFragment);
        transaction.commit();


        /*RegisterBean bean = new RegisterBean();
        bean.setAccountID(11);
        RegisterProfileImageFragment test = new RegisterProfileImageFragment().newInstance(new Gson().toJson(bean));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_register, test);
        transaction.commit();*/
    }
    @Override
    protected void onStart(){ Log.i(TAG, "onStart");
        super.onStart();
    }
    @Override
    protected void onResume(){Log.i(TAG, "onResume");
        super.onResume();

    }
    @Override
    protected void onPause() {Log.i(TAG, "onPause");
        super.onPause();

    }

    @Override
    protected void onStop(){ Log.i(TAG, "onStop");
        super.onStop();
        if(refresh){
            refresh = false;
            new AfterRegisterTask().execute();
        }

    }
    @Override
    protected void onRestart(){  Log.i(TAG, "onRestart");
        super.onRestart();
    }
    @Override
    protected void onDestroy(){ Log.i(TAG, "onDestroy");
        super.onDestroy();
        BusProvider.getInstance().unregister(this);// Always unregister when an object no longer should be on the bus.
    }
    @Override
    public void onBackPressed() { Log.i(TAG, "onBackPressed");
        super.onBackPressed();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {  Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();  Log.i(TAG, "id = " + id);
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Subscribe
    public void onAfterRegisterEvent(OnAfterRegisterEvent event){ Log.i(TAG, "onAfterRegisterEvent");
        editor.putBoolean("loginOnApps", true);
        editor.putInt("accountID", event.getAccountID());
        editor.commit();
        refresh = true;
        onBackPressed();
    }
    private class AfterRegisterTask extends AsyncTask<String, Void, String> {
        private String TAG = AfterRegisterTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"AfterRegisterTask");
                    BusProvider.getInstance().post(new OnBackPressedAfterRegisterEvent(true));
                }
            });
            return null;
        }
    }

}
