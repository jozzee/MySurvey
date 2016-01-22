package com.jozzee.mysurvey.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ResultRefreshSurveyForm;
import com.jozzee.mysurvey.bean.ResultRequestSurveyForm;
import com.jozzee.mysurvey.bean.SurveyBean;
import com.jozzee.mysurvey.adpter.RecyclerItemClickListener;
import com.jozzee.mysurvey.dosurvey.DoSurveyActivity;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnSetTypeRecycleViewEvent;
import com.jozzee.mysurvey.adpter.OnLoadMoreListener;
import com.jozzee.mysurvey.adpter.SurveyAdapter;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.ClearDiskCacheGlide;
import com.jozzee.mysurvey.support.ManageJson;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SurveyPrivateFragment extends Fragment {
    private static String TAG = SurveyPrivateFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CoordinatorLayout rootLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SurveyAdapter adapter;
    private List<SurveyBean> surveyList;
    private SwipeRefreshLayout refreshLayout;
    private ImageView retry;
    private RelativeLayout layoutNotHaveSurvey;
    private ProgressBar progressBar;
    private String backupData = "";
    private boolean allSurvey = false;
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private boolean notAnySurvey = false;
    private int typeOffRecycleView = 0; // type 0 is listView , type 1 is cardView
    private int accountID;
    private SurveyBean surveyBean;
    private Support support;
    private Gson gson;
    private ManageJson manageJson;

    public static SurveyPrivateFragment newInstance(int accountID, int typeOffRecycleView) {
        SurveyPrivateFragment fragment = new SurveyPrivateFragment();
        Bundle args = new Bundle();
        args.putInt("accountID",accountID);
        args.putInt("typeOffRecycleView", typeOffRecycleView);
        fragment.setArguments(args);
        return fragment;
    }

    public SurveyPrivateFragment() {

    }
    @Override
    public void onAttach(Context context) {  Log.i(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); Log.i(TAG, "setRetainInstance");
        BusProvider.getInstance().register(this);
        if (getArguments() != null) {Log.i(TAG, "getArguments");
            accountID = getArguments().getInt("accountID",1);
            typeOffRecycleView = getArguments().getInt("typeOffRecycleView",0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { Log.i(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_survey_public, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id.rootLayout_surveyPublic);

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout_surveyPublic);
        refreshLayout.setVisibility(View.GONE);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_surveyPublic);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        surveyList = new ArrayList<SurveyBean>();
        surveyList.add(null);
        adapter = new SurveyAdapter(surveyList, getActivity(), recyclerView, typeOffRecycleView,false);
        recyclerView.setAdapter(adapter);

        retry = (ImageView)rootView.findViewById(R.id.imageView_retry_surveyPublic);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_surveyPublic);
        layoutNotHaveSurvey = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_notHaveSurvey_surveyPublic);

        support = new Support();
        gson = new Gson();
        manageJson = new ManageJson();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {
                Log.i(TAG, "onItemClick RecycleView, " + "position = " + String.valueOf(position));

                surveyBean = surveyList.get(position);
                if(surveyBean.getNumberOfQuestions()>= 1){
                    if(accountID == 1){ //this guest
                        dialogDoSurveyForGuest(view);
                    }
                    else if(accountID != 1){ //this member
                        new CheckDoRepeatSurvey(view).execute(String.valueOf(surveyBean.getSurveyID()),String.valueOf(surveyBean.getSurveyVersion()));
                    }
                }
                else{
                    support.showSnackBarNoQuestion(rootView);
                }
            }
        }));

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore");

                if (!allSurvey) {
                    String rowStart = String.valueOf(surveyList.size() + 1);
                    String rowEnd = String.valueOf(surveyList.size() + 20);
                    Log.i(TAG, "rowStart = " + rowStart + ", rowEnd" + rowEnd);

                    if (support.checkNetworkConnection(getActivity())) {
                        surveyList.add(null);
                        adapter.notifyItemInserted(surveyList.size() - 1);
                        new RequestSurveyFormTask(true).execute("2", rowStart, rowEnd);
                    } else {
                        showSnackBarNotConnectInternet(rootView);
                        adapter.setLoaded();
                    }

                } else {
                    //support.showSnackBarAllSurvey(rootView);
                    adapter.setLoaded();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh");

                if (support.checkNetworkConnection(getActivity())) {
                    String lastDateFromClient = surveyList.get(0).getLastUpdate();
                    new RefreshSurveyFromTask().execute(lastDateFromClient);
                } else {
                    refreshLayout.setRefreshing(false);
                    showSnackBarNotConnectInternet(rootView);
                }
            }
        });
        layoutNotHaveSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestSurveyFormTask(false).execute("2", "1", "20");
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetry();
            }
        });
        return  rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){ Log.i(TAG, "restore savedInstanceState");//has rotation screen , destroy fragment
            backupData = savedInstanceState.getString("backupData","");
            allSurvey = savedInstanceState.getBoolean("allSurvey", false);
            connectionLost = savedInstanceState.getBoolean("connectionLost",false);
            onLoad = savedInstanceState.getBoolean("onLoad",false);
            notAnySurvey = savedInstanceState.getBoolean("notAnySurvey",false);

            if(savedInstanceState.getBoolean("visibleRefreshLayout",false)){
                refreshLayout.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleRetry",false)){
                retry.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleProgressBar",false)){
                progressBar.setVisibility(View.VISIBLE);
            }
            if(savedInstanceState.getBoolean("visibleLayoutNotHaveSurvey",false)){
                layoutNotHaveSurvey.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();

        if(backupData.equals("") && !(connectionLost) &&!(onLoad) &&!(notAnySurvey)){ //Log.i(TAG, "Not have backup data, have connect server, and not downloading data from server");
            if(support.checkNetworkConnection(getActivity())){
                new RequestSurveyFormTask(false).execute("2", "1", "20");
            }
            else{
                retry.setVisibility(View.VISIBLE);
                showSnackBarNotConnectInternet(rootView);
            }
        }
        else if(connectionLost){ //Log.i(TAG, "Not connect to server");
            if(backupData.equals("")){
                retry.setVisibility(View.VISIBLE);
            }
            else{
                surveyList = manageJson.getSurveyListFromJsonString(backupData);
                adapter.setSurveyList(surveyList);
                adapter.notifyDataSetChanged();
                refreshLayout.setVisibility(View.VISIBLE);
            }
        }
        else if(onLoad && backupData.equals("")){ //Log.i(TAG, "Downloading and not have backup data");
            progressBar.setVisibility(View.VISIBLE); //Log.e(TAG,"set Visibility VISIBLE on progressBar");
        }
        else if(onLoad && !backupData.equals("")){  //Log.i(TAG,"Downloading and  have backup data");
            surveyList = manageJson.getSurveyListFromJsonString(backupData);
            surveyList.add(null);
            adapter.setSurveyList(surveyList);
            adapter.notifyDataSetChanged();
            refreshLayout.setVisibility(View.VISIBLE);
        }
        else if(notAnySurvey){
            refreshLayout.setVisibility(View.GONE);
            layoutNotHaveSurvey.setVisibility(View.VISIBLE);
        }
        else if(!backupData.equals("")){ //Log.i(TAG,"have backup data ,not downloading, have connect server");
            surveyList = manageJson.getSurveyListFromJsonString(backupData);
            adapter.setSurveyList(surveyList);
            adapter.notifyDataSetChanged();
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
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
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("backupData", backupData);
        outState.putBoolean("allSurvey", allSurvey);
        outState.putBoolean("connectionLost", connectionLost);
        outState.putBoolean("onLoad",onLoad);
        outState.putBoolean("notAnySurvey",notAnySurvey);
        if(refreshLayout.getVisibility() == View.VISIBLE){
            outState.putBoolean("visibleRefreshLayout",true);
        }
        if(retry.getVisibility() == View.VISIBLE){
            outState.putBoolean("visibleRetry",true);
        }
        if(progressBar.getVisibility() == View.VISIBLE){
            outState.putBoolean("visibleProgressBar",true);
        }
        if(layoutNotHaveSurvey.getVisibility() == View.VISIBLE){
            outState.putBoolean("visibleLayoutNotHaveSurvey",true);
        }

    }
    public void onRetry(){ Log.i(TAG, "onRetry");
        connectionLost = false;
        onLoad = false;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }
    @Subscribe
    public void onSelectTypeRecycleView(OnSetTypeRecycleViewEvent event){ Log.i(TAG, "onSelectTypeRecycleView");
        typeOffRecycleView = event.getTypeOffRecycleView();
        adapter.setTypeOffRecycleView(typeOffRecycleView);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction(); //test convent view
        fragmentTransaction.detach(this).attach(this).commit();
    }


    public void onRequestSurveyPublicTaskCallBack(String result){ Log.i(TAG, "onRequestSurveyPublicTaskLoaded");

        if(!(result.equals("connectionLost"))){
            ResultRequestSurveyForm bean = new ResultRequestSurveyForm(result);
            if(bean.isAllSurvey()){
                allSurvey = true;
                Log.i(TAG,"set loadedAllData = true");
                support.showSnackBarAllSurvey(rootView);
            }
            if(bean.isSurveyListIsEmpty()){ Log.i(TAG,"Not have any survey");//Not have any survey.
                refreshLayout.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);
                layoutNotHaveSurvey.setVisibility(View.VISIBLE);
                notAnySurvey = true;
            }
            else {
                surveyList.remove(surveyList.size()-1);//remove  progress bar
                adapter.notifyItemRemoved(surveyList.size());
                List<SurveyBean> tempList = bean.getSurveyList();
                for(SurveyBean tempBean:tempList){
                    surveyList.add(tempBean);
                }
                adapter.notifyDataSetChanged(); //set new render adapter
                adapter.setLoaded();
                layoutNotHaveSurvey.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                backupData = manageJson.getJsonStringFromSurveyList(surveyList); // backup data
                notAnySurvey = false;
            }
        }
        else{
            connectionLost = true;
            if(backupData.equals("")){
                refreshLayout.setVisibility(View.GONE);
                layoutNotHaveSurvey.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                adapter.setLoaded();
                support.showSnackBarNotConnectToServer(rootView);

            }else{
                surveyList.remove(surveyList.size() - 1);//remove  progress bar
                adapter.notifyItemRemoved(surveyList.size());
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
                support.showSnackBarNotConnectToServer(rootView);
            }
        }
    }
    public void onRefreshSurveyFromTaskCallBack(String result){ Log.i(TAG,"onRefreshSurveyFromTaskCallBack");
        if(!(result.equals("connectionLost"))){
            ResultRefreshSurveyForm bean = new ResultRefreshSurveyForm(result);
            if(bean.isAllSurvey()){
                allSurvey = true;
                support.showSnackBarAllSurvey(rootView);
            }
            if(bean.isHaveNewUpdate()){ Log.i(TAG,"have new update.");
                if(bean.isNotAnySurvey()){ Log.i(TAG,"not have any survey.");
                    surveyList = new ArrayList<SurveyBean>();
                    surveyList.add(null);
                    adapter.setSurveyList(surveyList);
                    adapter.notifyDataSetChanged();
                    refreshLayout.setVisibility(View.GONE);
                    layoutNotHaveSurvey.setVisibility(View.VISIBLE);
                    backupData = "";
                    notAnySurvey = true;
                }
                else {
                    surveyList = bean.getSurveyList();
                    adapter.setSurveyList(surveyList);
                    adapter.notifyDataSetChanged();
                    backupData = manageJson.getJsonStringFromSurveyList(surveyList);

                }
            }
            else{
                support.showSnackBarNoNewSurvey(rootView);
            }
        }
        else{
            connectionLost = true;
            support.showSnackBarNotConnectToServer(rootView);
        }
    }

    //--------------------------------------------------------------------------------------
    private class RequestSurveyFormTask extends AsyncTask<String, Void, String> {
        private String TAG  = RequestSurveyFormTask.class.getSimpleName();
        private boolean onLoadMore = false;

        public RequestSurveyFormTask(boolean onLoadMore) {
            this.onLoadMore = onLoadMore;
        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            if(onLoadMore){
                retry.setVisibility(View.GONE);
                layoutNotHaveSurvey.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            else{
                retry.setVisibility(View.GONE);
                layoutNotHaveSurvey.setVisibility(View.GONE);
                support.setProgressBarColor(progressBar, support.accentClolr());
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            String url = support.getURLLink()+"?command=RequestSurveyForm&surveyType=" +params[0] +"&rowStart="+params[1] +"&rowEnd=" +params[2];
            Log.i(TAG,"URL = "+url);
            return new ConnectionServiceCore().doOKHttpGetString(url);
        }
        @Override
        protected void onPostExecute(String result) { Log.i(TAG, "onPostExecute");
            onLoad = false;
            progressBar.setVisibility(View.GONE); //Log.e(TAG, "setVisibility GONE on progressBar");
            onRequestSurveyPublicTaskCallBack(result);
        }
    }
    //--------------------------------------------------------------------------------------------------------
    private class RefreshSurveyFromTask extends AsyncTask<String, Void, String> {
        private String TAG  = RefreshSurveyFromTask.class.getSimpleName();

        public  RefreshSurveyFromTask() {

        }
        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            String url = support.getURLLink() +"?command=RefreshSurveyFrom&surveyType=2&lastDate=" +params[0];
            return new ConnectionServiceCore().doOKHttpGetString(url);
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            refreshLayout.setRefreshing(false);
            onRefreshSurveyFromTaskCallBack(result);
        }
    }
    private class CheckDoRepeatSurvey extends AsyncTask<String, Void, String> {
        private String TAG  = CheckDoRepeatSurvey.class.getSimpleName();

        private ProgressDialog progressDialog;
        private View view;

        public  CheckDoRepeatSurvey(View view) {
            this.view = view;
        }
        @Override
        protected void onPreExecute() { Log.i(TAG, "onPreExecute");

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("in process...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {  Log.i(TAG, "doInBackground");
            String url = support.getURLLink() +"?command=CheckDoRepeatSurvey&accountID=" +accountID
                    +"&surveyID="+params[0] +"&surveyVersion="+params[1];
            return new ConnectionServiceCore().doOKHttpGetString(url);
        }
        @Override
        protected void onPostExecute(String result) {  Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            if(!(result.equals("connectionLost")) && (result.equals("1"))){
                Intent doSurveyIntent = new Intent(view.getContext(), DoSurveyActivity.class);
                doSurveyIntent.putExtra("surveyBean",new Gson().toJson(surveyBean));
                startActivity(doSurveyIntent);

            }
            else if(!(result.equals("connectionLost")) && (result.equals("0"))){
                Snackbar.make(rootView, "You made this survey.", Snackbar.LENGTH_LONG).show();
            }
            else if((result.equals("connectionLost"))){
                Snackbar.make(rootView, "Can't connect to Server.", Snackbar.LENGTH_LONG).show();
            }


        }
    }
    private Context getContextFromActivity(){
        return getActivity();
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
    public void dialogDoSurveyForGuest(final View view){
        new MaterialDialog.Builder(getActivity())
                .title("Enter Your Name.")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Your Name", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        Intent doSurveyIntent = new Intent(view.getContext(), DoSurveyActivity.class);
                        doSurveyIntent.putExtra("nameGuest", charSequence.toString());
                        doSurveyIntent.putExtra("surveyBean", new Gson().toJson(surveyBean));
                        startActivity(doSurveyIntent);
                    }
                })
                .negativeText("Cancel")
                .show();
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
