package com.jozzee.mysurvey.analyze;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.adpter.AnalyzeResponsesTextAdapter;
import com.jozzee.mysurvey.adpter.OnLoadMoreListener;
import com.jozzee.mysurvey.bean.AnalyzeTextQuestionBean;
import com.jozzee.mysurvey.bean.CreateAnalyzeBean;
import com.jozzee.mysurvey.bean.ResponsesBean;
import com.jozzee.mysurvey.bean.ResultAnalyzeTextQuestionBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.main.SurveyPublicFragment;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AnalyzeTextFragment extends Fragment {
    private static String TAG = AnalyzeTextFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CoordinatorLayout rootLayout;
    private CreateAnalyzeBean createAnalyzeBean;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AnalyzeResponsesTextAdapter adapter;
    private List<AnalyzeTextQuestionBean> analyzeList;
    private String backupData = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private boolean allData = false;
    private ImageView retry;
    private ProgressBar progressBar;
    private RelativeLayout layoutNoAnyResponses;
    private Support support;
    private int numberOffAnswer;
    private TextView question;
    private ManageJson manageJson;

    public static AnalyzeTextFragment newInstance(String createAnalyzeBeanAsJsonString) {
        AnalyzeTextFragment fragment = new AnalyzeTextFragment();
        Bundle args = new Bundle();
        args.putString("createAnalyzeBeanAsJsonString", createAnalyzeBeanAsJsonString);
        fragment.setArguments(args);
        return fragment;
    }

    public AnalyzeTextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) { Log.i(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); Log.i(TAG, "setRetainInstance");
        if (getArguments() != null) {
            createAnalyzeBean = new CreateAnalyzeBean(getArguments().getString("createAnalyzeBeanAsJsonString"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { Log.i(TAG, "onCreateView");
        rootView =  inflater.inflate(R.layout.fragment_analyze_text, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_analyzeText);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        analyzeList = new ArrayList<AnalyzeTextQuestionBean>();
        analyzeList.add(null);
        adapter = new AnalyzeResponsesTextAdapter(recyclerView,analyzeList);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.GONE);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_analyzeText);
        retry = (ImageView)rootView.findViewById(R.id.imageView_retry_analyzeText);
        layoutNoAnyResponses = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_notHaveAnswer_analyzeText);
        support = new Support();
        manageJson = new ManageJson();

        question = (TextView)rootView.findViewById(R.id.textView_question_analyzeText);
        question.setText(createAnalyzeBean.getQuestionNumber() +". " +createAnalyzeBean.getQuestion());



        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(!allData){
                    if(support.checkNetworkConnection(getActivity())){
                        String rowStart = String.valueOf((analyzeList.size()+1));
                        String rowEnd = String.valueOf((analyzeList.size()+50));
                        new AnalyzeTask(true).execute(String.valueOf(createAnalyzeBean.getQuestionID()),rowStart,rowEnd);
                    }
                    else{
                        showSnackBarNotConnectInternet(rootView);
                    }
                }
                else{
                    Snackbar.make(rootView, "All answer.", Snackbar.LENGTH_LONG).show();
                }
            }
        });



        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {  Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            backupData = savedInstanceState.getString("backupData", "");
            connectionLost = savedInstanceState.getBoolean("connectionLost", false);
            allData = savedInstanceState.getBoolean("allData",false);
            onLoad = savedInstanceState.getBoolean("onLoad",false);
            numberOffAnswer = savedInstanceState.getInt("numberOffAnswer",0);
            if(savedInstanceState.getBoolean("visibleRecyclerView",false)){
                recyclerView.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleProgressBar",false)){
                progressBar.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleRetry",false)){
                retry.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleLayoutNoAnswer",false)){
                layoutNoAnyResponses.setVisibility(View.VISIBLE);
            }

        }
    }
    @Override
    public void onStart() { Log.i(TAG, "onStart");
        super.onStart();
        if(backupData.equals("") &&!(connectionLost) &&!(onLoad)){
            if(support.checkNetworkConnection(getActivity())){
                new AnalyzeTask(false).execute(String.valueOf(createAnalyzeBean.getQuestionID()),"1","50");
            }
            else{
                showSnackBarNotConnectInternet(rootView);
            }
        }
        else if(connectionLost){

        }
        else if(onLoad &&(backupData.equals(""))){

        }
        else if(onLoad && !(backupData.equals(""))){

        }
        else if(!(backupData.equals(""))){
            analyzeList.remove(analyzeList.size() - 1);//remove  progress bar
            adapter.notifyItemRemoved(analyzeList.size());
            List<AnalyzeTextQuestionBean> tempList = manageJson.getAnalyzeTextListFromJsonString(backupData);
            for(AnalyzeTextQuestionBean tempBean:tempList){
                analyzeList.add(tempBean);
            }
            adapter.notifyDataSetChanged();
            adapter.setLoaded();
            backupData = manageJson.getJsonStringFromAnalyzeTextList(analyzeList);
            recyclerView.setVisibility(View.VISIBLE);
            question.setText(createAnalyzeBean.getQuestionNumber() +". "
                    +createAnalyzeBean.getQuestion()+" (" +numberOffAnswer +" Answer)");
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
    public void onStop() { Log.i(TAG, "onStop");
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
    public void onSaveInstanceState(Bundle outState) {  Log.i(TAG, "onSaveInstanceState");

        outState.putString("backupData", backupData);
        outState.putBoolean("connectionLost", connectionLost);
        outState.putBoolean("allData",allData);
        outState.putBoolean("onLoad",onLoad);
        outState.putInt("numberOffAnswer",numberOffAnswer);

        if(recyclerView.getVisibility() == View.VISIBLE){
            outState.getBoolean("visibleRecyclerView",true);
        }
        if(progressBar.getVisibility() == View.VISIBLE){
            outState.getBoolean("visibleProgressBar",true);
        }
        if(retry.getVisibility() == View.VISIBLE){
            outState.putBoolean("visibleRetry",true);
        }
        if(layoutNoAnyResponses.getVisibility() == View.VISIBLE){
            outState.putBoolean("visibleLayoutNoAnswer",true);
        }
    }

    public void onAnalyzeTaskCallback(String result){ Log.i(TAG, "onAnalyzeTaskCallback");
        if(!(result.equals("connectionLost"))){
            ResultAnalyzeTextQuestionBean bean = new ResultAnalyzeTextQuestionBean(result);
            if(bean.isAllData()){
               allData = true;
            }
            if(!(bean.isAnalyzeListIsEmpty())){
                analyzeList.remove(analyzeList.size() - 1);//remove  progress bar
                adapter.notifyItemRemoved(analyzeList.size());
                List<AnalyzeTextQuestionBean> tempList = bean.getAnalyzeList();
                for(AnalyzeTextQuestionBean tempBean:tempList){
                    analyzeList.add(tempBean);
                }
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
                backupData = manageJson.getJsonStringFromAnalyzeTextList(analyzeList);
                recyclerView.setVisibility(View.VISIBLE);
                numberOffAnswer = bean.getNumberOffAnswer();
                question.setText(createAnalyzeBean.getQuestionNumber() +". "
                        +createAnalyzeBean.getQuestion()+" (" +numberOffAnswer +" Answer)");

            }
            else{
                if(analyzeList.get(0) == null) {
                    recyclerView.setVisibility(View.GONE);
                    layoutNoAnyResponses.setVisibility(View.VISIBLE);
                }
                else{
                    analyzeList.remove(analyzeList.size() - 1);//remove  progress bar
                    adapter.notifyItemRemoved(analyzeList.size());
                    Snackbar.make(rootLayout, "All Answer.", Snackbar.LENGTH_LONG).show();
                }
                adapter.setLoaded();
            }
        }
        else{
            support.showSnackBarNotConnectToServer(rootView);
        }
    }

    private class AnalyzeTask extends AsyncTask<String,Void,String>{
        private String TAG  = AnalyzeTask.class.getSimpleName();
        boolean onLoadMore = false;

        public AnalyzeTask(boolean onLoadMore) {
            this.onLoadMore = onLoadMore;
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            if(onLoadMore){
                retry.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                layoutNoAnyResponses.setVisibility(View.GONE);

            }
            else{
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpGetString(support.getURLLink()
                    +"?command=Analyze&questionid="+params[0]
                    +"&rowstart="+params[1]
                    +"&rowend="+params[2]);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE);
            onAnalyzeTaskCallback(result);
        }
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
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
