package com.jozzee.mysurvey.analyze;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.adpter.AnalyzeResponsesChoiceAdapter;
import com.jozzee.mysurvey.bean.AnalyzeChoiceQuestionBean;
import com.jozzee.mysurvey.bean.AnalyzeTextQuestionBean;
import com.jozzee.mysurvey.bean.CreateAnalyzeBean;
import com.jozzee.mysurvey.bean.ResultAnalyzeChoiceQuestionBean;
import com.jozzee.mysurvey.event.BusProvider;
import com.jozzee.mysurvey.servicecore.ConnectionServiceCore;
import com.jozzee.mysurvey.support.Support;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnalyzeChoiceFragment extends Fragment {
    private static String TAG = AnalyzeChoiceFragment.class.getSimpleName();

    private static final int ORIENTATION_0 = 0; // Portrait
    private static final int ORIENTATION_90 = 3; // Landscape right
    private static final int ORIENTATION_180 = 2;
    private static final int ORIENTATION_270 = 1; // Landscape left
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CoordinatorLayout rootLayout;
    private CreateAnalyzeBean createAnalyzeBean;
    private TextView question;
    private LinearLayout layoutPieChart;
    private LinearLayout layoutChoiceView;
    private Support support;
    private Gson gson;
    private List<AnalyzeChoiceQuestionBean> analyzeList;
    private String backupData = "";
    private boolean connectionLost = false;
    private boolean onLoad = false;
    private ImageView retry;
    private ProgressBar progressBar;
    private RelativeLayout layoutNoAnyResponses;
    private View choiceItem;

    private PieChart pieChart;
    private ArrayList<Entry> value;
    private ArrayList<String> choice;
    private PieDataSet dataSet;
    private PieData data;
    private ArrayList<Integer> colorsSet;



    public static AnalyzeChoiceFragment newInstance(String createAnalyzeBeanAsJsonString) {
        AnalyzeChoiceFragment fragment = new AnalyzeChoiceFragment();
        Bundle args = new Bundle();
        args.putString("createAnalyzeBeanAsJsonString", createAnalyzeBeanAsJsonString);
        fragment.setArguments(args);
        return fragment;
    }

    public AnalyzeChoiceFragment() {
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
        rootView =  inflater.inflate(R.layout.fragment_analyze_choice, container, false);
        question = (TextView)rootView.findViewById(R.id.textView_question_analyzeChoice);
        question.setText(String.valueOf(createAnalyzeBean.getQuestionNumber()) + ". " + createAnalyzeBean.getQuestion());
        layoutPieChart = (LinearLayout)rootView.findViewById(R.id.linearLayout_pieChart);
        layoutPieChart.setVisibility(View.GONE);
        layoutChoiceView = (LinearLayout)rootView.findViewById(R.id.linearLayout_showChoice_analyzeChoice);
        layoutChoiceView.setVisibility(View.GONE);

        support = new Support();
        gson = new Gson();

        Display display = ((WindowManager)
                getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenOrientation = display.getRotation();
        Configuration configuration = getActivity().getResources().getConfiguration();
        int screenWidthDp = 0;
        if(screenOrientation == ORIENTATION_0 || screenOrientation == ORIENTATION_180){ // if Portrait
            screenWidthDp = configuration.screenWidthDp;
        }
        else{
            screenWidthDp = configuration.screenHeightDp;
        }


        //int screenHeightDp = configuration.screenHeightDp;
        //screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        //int smallestScreenWidthDp = configuration.smallestScreenWidthDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier
        Log.i(TAG, "screenWidthDp = " + screenWidthDp);
        float sizeWidth = (screenWidthDp -64)*getActivity().getResources().getDisplayMetrics().density; Log.i(TAG, "sizeWidth = " + sizeWidth);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((int)sizeWidth, (int)sizeWidth);
        param.weight = 1.0f;

        pieChart = (PieChart)rootView.findViewById(R.id.pieChart);
        pieChart.setLayoutParams(param); Log.i(TAG, "set sizeWidth to pieChart");
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelected() {
        });
        //pieChart.setVisibility(View.GONE);

        analyzeList = new ArrayList<AnalyzeChoiceQuestionBean>();

        retry = (ImageView)rootView.findViewById(R.id.imageView_retry_analyzeChoice);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar_analyzeChoice);
        layoutNoAnyResponses = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_notHaveAnswer_analyzeChoice);

        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {  Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){ Log.i(TAG, "get backUpData");
            backupData = savedInstanceState.getString("backupData","");
            Log.e(TAG,backupData);
        }
    }
    @Override
    public void onStart() { Log.i(TAG, "onStart");
        super.onStart();
        if(backupData.equals("") &&!(connectionLost) &&!(onLoad)){
            if(support.checkNetworkConnection(getActivity())){
                new AnalyzeTask().execute(String.valueOf(createAnalyzeBean.getQuestionID()));
            }
            else{
                showSnackBarNotConnectInternet(rootView);
            }
        }
        else if(connectionLost){

        }
        else if(onLoad){

        }
        else if(!(backupData.equals(""))){
            createView(backupData); Log.i(TAG, "createView");
        }
    }
    @Override
    public void onResume() { Log.i(TAG, "onResume");
        super.onResume();
        BusProvider.getInstance().register(this);
        pieChart.invalidate();

    }

    @Override
    public void onPause() { Log.i(TAG, "onPause");
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() { Log.i(TAG, "onDestroyView");
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
    public void onSaveInstanceState(Bundle outState) {  Log.i(TAG, "onSaveInstanceState");
        outState.putString("backupData",backupData);
    }

    public  void onAnalyzeTaskCallback(String result){ Log.i(TAG, "onAnalyzeTaskCallback");
        if(!(result.equals("connectionLost"))){
            createView(result);
        }
        else{
            pieChart.setVisibility(View.GONE);
            retry.setVisibility(View.VISIBLE);
            support.showSnackBarNotConnectToServer(rootView);
        }
    }
    private class OnChartValueSelected implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
            Log.e(TAG,"click values in pieChart, value = "+e.getXIndex());
        }

        @Override
        public void onNothingSelected() {

        }
    }
    public void createView(String result){
        ResultAnalyzeChoiceQuestionBean bean = new ResultAnalyzeChoiceQuestionBean(result);
        backupData = gson.toJson(bean);

        if(!(bean.isNotAnyResponses())){
            question.setText(String.valueOf(createAnalyzeBean.getQuestionNumber())
                    + ". " + createAnalyzeBean.getQuestion()
                    + " (" + bean.getNumberOffAnswer() + " Answer)");

            analyzeList  = bean.getAnalyzeList();

            colorsSet = new ArrayList<Integer>();
            value = new ArrayList<Entry>();
            choice = new ArrayList<String>();
            layoutChoiceView.removeAllViews();
            for(AnalyzeChoiceQuestionBean tempBean:analyzeList){
                int chanelColor = new Random().nextInt((16-0)+1)+0;
                value.add(new Entry(tempBean.getPercent(), (tempBean.getChoiceNumber()-1)));
                choice.add("");
                colorsSet.add(support.colorsSet[chanelColor]);

                final View choiceItem = LayoutInflater.from(getActivity()).inflate(R.layout.choice_item_analyze, layoutChoiceView, false);
                final ImageView colorType = (ImageView)choiceItem.findViewById(R.id.imageView_color_analyzeChoice);
                final TextView choice = (TextView)choiceItem.findViewById(R.id.textView_Choice_analyzeChoice);
                final TextView percent = (TextView)choiceItem.findViewById(R.id.textView_percent_analyzeChoice);
                final TextView numAnsOffChoice = (TextView)choiceItem.findViewById(R.id.textView_numberSelectThisChoice_analyzeChoice);

                colorType.setBackgroundColor(support.colorsSet[chanelColor]);
                choice.setText(tempBean.getChoice());
                percent.setText(String.valueOf(tempBean.getPercent()) +"%");
                numAnsOffChoice.setText(String.valueOf(tempBean.getNumberOffSelectThisChoice()));
                layoutChoiceView.addView(choiceItem);
            }
            analyzeList.clear();

            dataSet = new PieDataSet(value,"");
            dataSet.setSliceSpace(2f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colorsSet);

            data = new PieData(choice, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(16f);
            data.setValueTextColor(Color.WHITE);
            pieChart.setData(data);
            layoutPieChart.setVisibility(View.VISIBLE);
            layoutChoiceView.setVisibility(View.VISIBLE);
            pieChart.invalidate();
        }
        else{
            pieChart.setVisibility(View.GONE);
            layoutNoAnyResponses.setVisibility(View.VISIBLE);
        }
    }
    private class AnalyzeTask extends AsyncTask<String,Void,String> {
        private String TAG  = AnalyzeTask.class.getSimpleName();


        public AnalyzeTask() {

        }

        @Override
        protected void onPreExecute() {  Log.i(TAG, "onPreExecute");
            onLoad = true;
            retry.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }
        @Override
        protected String doInBackground(String... params) { Log.i(TAG, "doInBackground");
            return new ConnectionServiceCore().doOKHttpGetString(support.getURLLink()
                    +"?command=Analyze&questionid="+params[0]);
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
        // TODO: Update argument type and name
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
        //get dp from screen
        /*DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density; Log.e(TAG, "dpHeight = " + dpHeight);
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density; Log.e(TAG,"dpWidth = "+dpWidth);*/