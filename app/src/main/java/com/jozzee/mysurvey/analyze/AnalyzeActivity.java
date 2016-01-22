package com.jozzee.mysurvey.analyze;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.adpter.PagerAdapterAnalyze;
import com.jozzee.mysurvey.bean.CreateAnalyzeBean;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class AnalyzeActivity extends AppCompatActivity {
    private static String TAG = AnalyzeActivity.class.getSimpleName();

    private Bundle bundle;
    private List<CreateAnalyzeBean> createAnalyzeList;
    private CoordinatorLayout rootLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private PagerAdapterAnalyze pagerAdapter;
    //private String createAnalyzeListAsJsonString;


    @Override
    protected void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        bundle =  getIntent().getExtras();

        if(bundle != null){  Log.i(TAG, "get createAnalyzeList from bundle");
            createAnalyzeList = getCreateAnalyzeListFromJsonString((String)bundle.get("createAnalyzeList"));
            //createAnalyzeListAsJsonString = (String)bundle.get("createAnalyzeList");
        }
        if(savedInstanceState != null){  Log.i(TAG, "get values from savedInstanceState");

        }

        //----------------------------------------------------------------------------------------------------
        rootLayout = (CoordinatorLayout)findViewById(R.id.rootLayout_analyze);
        toolbar = (Toolbar)findViewById(R.id.toolbar_analyze);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Analyze");

        viewPager = (ViewPager) findViewById(R.id.pager_analyze);
        setupViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { Log.i(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_analyze, menu);
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
    public void setupViewPager(ViewPager viewPager){
        pagerAdapter = new PagerAdapterAnalyze(getSupportFragmentManager());
        for(CreateAnalyzeBean bean:createAnalyzeList){
            if(bean.getAnswerType() == 1){
                pagerAdapter.addFragment(new AnalyzeTextFragment().newInstance(new Gson().toJson(bean))
                        ,String.valueOf(bean.getQuestionNumber()));
            }
            else{
                pagerAdapter.addFragment(new AnalyzeChoiceFragment().newInstance(new Gson().toJson(bean))
                        ,String.valueOf(bean.getQuestionNumber()));
            }
        }
        viewPager.setAdapter(pagerAdapter);

    }
    public List<CreateAnalyzeBean> getCreateAnalyzeListFromJsonString (String createAnalyzeAsJsonString){
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(createAnalyzeAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<CreateAnalyzeBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
}
