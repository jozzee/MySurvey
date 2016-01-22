package com.jozzee.mysurvey.create;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.QuestionsBean;
import com.jozzee.mysurvey.adpter.QuestionAdapter;
import com.jozzee.mysurvey.adpter.RecyclerItemClickListener;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.event.OnSendQuestionEvent;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;
import com.squareup.otto.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CreateQuestionsFragment extends Fragment {
    private static String TAG = CreateQuestionsFragment.class.getSimpleName();
    private View rootView;
    private FloatingActionButton floatingActionButton;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private QuestionAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private List<QuestionsBean> questionList;
    private int surveyID;
    private int surveyVersion;
    private String questionListAsJsonString = "noData";
    private TextView notHaveQuestiond;

    public static CreateQuestionsFragment newInstance(int surveyID, int surveyVersion) {
        CreateQuestionsFragment fragment = new CreateQuestionsFragment();
        Bundle args = new Bundle();
        args.putInt("surveyID",surveyID);
        args.putInt("surveyVersion",surveyVersion);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateQuestionsFragment() {
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
        if (getArguments() != null) {
            surveyID = getArguments().getInt("surveyID");
            surveyVersion = getArguments().getInt("surveyVersion");
        }
        if(savedInstanceState != null){
            if(savedInstanceState.get("questionListAsJsonString") != null){
                questionListAsJsonString = savedInstanceState.getString("questionListAsJsonString","noData");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        rootView =  inflater.inflate(R.layout.fragment_create_questions, container, false);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.rootLayout_CreateQuestions);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_CreateQuestions);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        floatingActionButton = (FloatingActionButton)rootView.findViewById(R.id.fabBtn_CreateQuestions);
        notHaveQuestiond = (TextView)rootView.findViewById(R.id.textView_notHaveQuestion_CreateQuestions);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick RecycleView, " + "position = " + String.valueOf(position));
                QuestionsBean questionsBean = questionList.get(position);
                String questionsBeanAsJsonString = new Gson().toJson(questionsBean);
                Intent manageQuestion = new Intent(view.getContext(), ManageQuestionActivity.class);
                manageQuestion.putExtra("action", "update");
                manageQuestion.putExtra("questionsBean", questionsBeanAsJsonString);
                startActivity(manageQuestion);
            }
        }));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick floatingActionButton");
                Intent manageQuestion = new Intent(v.getContext(), ManageQuestionActivity.class);
                manageQuestion.putExtra("action", "add");
                manageQuestion.putExtra("surveyID", surveyID);
                manageQuestion.putExtra("surveyVersion", surveyVersion);
                startActivity(manageQuestion);
            }
        });
        questionList = new ArrayList<QuestionsBean>();
        //restore values
        if(!(questionListAsJsonString.equals("noData"))){
            questionList = getQuestionListFromJsonString(questionListAsJsonString);
        }

        //-----------------------------------------------------------------------------------------------
        if(questionList.isEmpty()){
            questionList.add(null);
            adapter = new QuestionAdapter(questionList,recyclerView,getActivity());
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.GONE);
            notHaveQuestiond.setVisibility(View.VISIBLE);
        }
        else{
            adapter = new QuestionAdapter(questionList,recyclerView,getActivity());
            recyclerView.setAdapter(adapter);

        }



        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        //SendQuestionBus.getInstance().register(receivedQuestion);
    }
    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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
        //SendQuestionBus.getInstance().unregister(receivedQuestion);
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
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if((!questionList.isEmpty())){
            Log.i(TAG, "save questionList to questionListAsJsonString");
            outState.putString("questionListAsJsonString",getJsonStringFromQuestionList(questionList));
        }
        else{
            Log.i(TAG, "questionList.isEmpty");
        }

    }

    @Subscribe
    public void onReceivedQuestionEvent(OnSendQuestionEvent event){
        Log.i(TAG, "onReceivedQuestionEvent");
        QuestionsBean bean = event.getQuestionsBean();
        String action = bean.getAction();
        Log.i(TAG, "action = " + action);

        if(action.equals("add")){
            if(questionList.get(0) == null){
                questionList.remove((questionList.size()-1));
                adapter.notifyDataSetChanged();
                questionList.add(bean);
                adapter.notifyDataSetChanged();
            }
            else{
                questionList.add(bean);
                adapter.notifyDataSetChanged();

            }
            notHaveQuestiond.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else if(action.equals("update")){
            questionList.set((bean.getQuestionNumber() - 1), bean);
            adapter.notifyDataSetChanged();

        }
        else if(action.equals("delete")){
            int sizeQuestionList = questionList.size(); Log.i(TAG,"sizeQuestionList = " +sizeQuestionList);
            for(int i = 0 ;i<sizeQuestionList;i++){
                QuestionsBean tempBean = new QuestionsBean();
                tempBean = questionList.get(i);
                if(tempBean.getQuestionNumber() > bean.getQuestionNumber()){
                    Log.i(TAG,"updateQuestionNumber from no. " +tempBean.getQuestionNumber() +" to no. " +i);
                    tempBean.setQuestionNumber(i);
                    questionList.set(i,tempBean);
                }
            }
            questionList.remove((bean.getQuestionNumber()-1));
            adapter.notifyDataSetChanged();
            if(questionList.isEmpty()){
                questionList.add(null);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.GONE);
                notHaveQuestiond.setVisibility(View.VISIBLE);
            }
        }

    }

    /*@Subscribe
    public void onReceivedQuestionFromManageQuestion(String event) {
        Log.e(TAG, "onReceivedQuestionFromManageQuestion");

        QuestionsBean bean = new QuestionsBean(event);
        String action = bean.getAction();
        Log.e(TAG, "action = " + action);

        if(action.equals("add")){
            if(questionList.get(0) == null){
                questionList.remove((questionList.size()-1));
                adapter.notifyDataSetChanged();
                questionList.add(bean);
                adapter.notifyDataSetChanged();
            }
            else{
                questionList.add(bean);
                adapter.notifyDataSetChanged();

            }
        }
        else if(action.equals("update")){

        }
        else if(action.equals("delete")){

        }

       *//* ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.e(TAG,"55555555555555555555555555555555555555");
            }
        });*//*

    }*/

    public List<QuestionsBean> getQuestionListFromJsonString (String questionListAsJsonString){
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(questionListAsJsonString, JsonArray.class);
        Type listType = new TypeToken<ArrayList<QuestionsBean>>(){}.getType();
        return gson.fromJson(jsonArray, listType);
    }
    public String getJsonStringFromQuestionList(List<QuestionsBean> questionList){
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(questionList);
        return gson.toJson(element);
    }
    public void onRequestQuestionTaskCallBack(String result){
        if(!(result.equals("connectionLost")) && !(result.equals("0"))){


        }
        else if(!(result.equals("connectionLost")) && (result.equals("0"))){
            //not have any questions in this surveyID and surveyVersion
            questionList.add(null);
            adapter = new QuestionAdapter(questionList,recyclerView,getActivity());
            recyclerView.setAdapter(adapter);

        }
        else{
            //server down
        }

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    //--------------------------------------------------------------------------------------------------------------
    private class RequestQuestionTask extends AsyncTask<String, Void, String>{
        private String TAG = RequestQuestionTask.class.getSimpleName();
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Load Questions");
            progressDialog.setMessage("Loading....");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            String result = "";
            String surveyID = params[0];
            String surveyVersion = params[1];
            String url = new Support().getURLLink() +"?command=RequestQuestions&surveyID=" +surveyID +"&surveyVersion=" +surveyVersion;
            result = new ConnectionServiceCore().doOKHttpGetString(url);

            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            progressDialog.dismiss();
            onRequestQuestionTaskCallBack(result);
        }
    }

}
