package com.jozzee.mysurvey.adpter;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ChoiceBean;
import com.jozzee.mysurvey.bean.QuestionsBeanForDoSurvey;
import com.jozzee.mysurvey.support.Validate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 16/11/2558.
 */
public class DoSurveyAdapter extends RecyclerView.Adapter {
    private static String TAG = DoSurveyAdapter.class.getSimpleName();

    private int onLoadView = 0;
    private int questionTextView = 1;
    private int questionChoiceView =2;
    private Context context;
    private List<QuestionsBeanForDoSurvey> questionList;
    private Validate validate;

    public DoSurveyAdapter(List<QuestionsBeanForDoSurvey> questionList, Context context) {
        this.questionList = questionList;
        this.context = context;
        validate = new Validate();

    }
    @Override
    public long getItemId(int position) {  //Log.i(TAG, "getItemId");
        return position;
    }
    @Override
    public int getItemViewType(int position) {  //Log.i(TAG, "getItemViewType");
        int viewType = 0;
        QuestionsBeanForDoSurvey bean;
        if(questionList.get(position) != null){
            bean = questionList.get(position);
            if(bean.getAnswerType() == 1)// text questions
                viewType = questionTextView;
            else if(bean.getAnswerType() == 2) //choice questions
                viewType = questionChoiceView;
        }
        else{//on load a questions.
            viewType = onLoadView;
        }
        return viewType;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == questionTextView){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_question_text_dosurvey, parent, false);
            vh = new TextQuestionsViewHolder(v);

        }
        else if(viewType == questionChoiceView){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_question_choice, parent, false);
            vh = new ChoiceQuestionsViewHolder(v,context);
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_onload, parent, false);
            vh = new OnLoadViewHolder(v);
        }
        return vh;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OnLoadViewHolder) {
            //((OnLoadViewHolder) holder).progressBar.setVisibility(View.VISIBLE);

        }
        //---------------------------------------------------------------------------------------------------------------------------------
        else if(holder instanceof TextQuestionsViewHolder){ //Log.e(TAG, "holder instanceof TextQuestionsViewHolder");
            final QuestionsBeanForDoSurvey bean = questionList.get(position);
            if(bean.getQuestionNumber() == 0){
                bean.setQuestionNumber(position+1);
            }
            ((TextQuestionsViewHolder) holder).noQuestion.setText((bean.getQuestionNumber()) + ".");
            ((TextQuestionsViewHolder) holder).question.setText(bean.getQuestion());
            ((TextQuestionsViewHolder) holder).answer.setSelected(false);

            if(bean.getAnswerData() != null){
                ((TextQuestionsViewHolder) holder).answer.setText(bean.getAnswerData());
                ((TextQuestionsViewHolder) holder).answerLayout.setHint("Answer");
            }

            ((TextQuestionsViewHolder) holder).answer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ((TextQuestionsViewHolder) holder).answerLayout.setHint("Answer");

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (validate.validAnswer(((TextQuestionsViewHolder) holder).answer,
                            ((TextQuestionsViewHolder) holder).answerLayout)) {

                        bean.setAnswerData(((TextQuestionsViewHolder) holder).answer.getText().toString().trim());
                        questionList.set(position,bean);
                    }
                    else{
                        bean.setAnswerData(null);
                        questionList.set(position, bean);
                    }

                }
            });
            ((TextQuestionsViewHolder) holder).answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (validate.validAnswer(((TextQuestionsViewHolder) holder).answer,
                                ((TextQuestionsViewHolder) holder).answerLayout)) {

                            bean.setAnswerData(((TextQuestionsViewHolder) holder).answer.getText().toString().trim());
                            questionList.set(position,bean);
                        }
                        else{
                            bean.setAnswerData(null);
                            questionList.set(position,bean);
                        }
                    }
                    return false;

                }
            });

        }
        //---------------------------------------------------------------------------------------------------------------------------
        else if(holder instanceof ChoiceQuestionsViewHolder){ //Log.e(TAG, "holder instanceof ChoiceQuestionsViewHolder");
            final QuestionsBeanForDoSurvey bean = questionList.get(position);
            if(bean.getQuestionNumber() == 0){
                bean.setQuestionNumber(position + 1);
            }
            ((ChoiceQuestionsViewHolder) holder).noQuestion.setText((bean.getQuestionNumber()) + ".");
            ((ChoiceQuestionsViewHolder) holder).question.setText(bean.getQuestion());
            ((ChoiceQuestionsViewHolder) holder).addChoice(bean.getChoiceList());

            if(bean.getChoiceIDAsAnswer() != 0){
                ((ChoiceQuestionsViewHolder) holder).choiceGroup.check(bean.getChoiceIDAsAnswer());
            }

            ((ChoiceQuestionsViewHolder) holder).choiceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    List<ChoiceBean> choiceList = bean.getChoiceList();
                    for(ChoiceBean choiceBean:choiceList){
                        if(choiceBean.getChoiceID() == checkedId){
                            bean.setAnswerData(choiceBean.getChoiceData());
                        }
                    }
                    bean.setChoiceIDAsAnswer(checkedId);
                    questionList.set(position, bean);

                }
            });
        }
        //----------------------------------------------------------------------------------------------------------------------------------
    }
    @Override
    public int getItemCount() {
        return questionList.size();
    }
    public void setQuestionList(List<QuestionsBeanForDoSurvey> questionList){
        this.questionList = questionList;
    }
    public List<QuestionsBeanForDoSurvey> getQuestionList() {
        return questionList;
    }

    public static class TextQuestionsViewHolder extends RecyclerView.ViewHolder{
        private static String TAG = TextQuestionsViewHolder.class.getSimpleName();
        public TextView noQuestion;
        public TextView question;
        public EditText answer;
        public TextInputLayout answerLayout;

        public TextQuestionsViewHolder(View itemView) {
            super(itemView);
            noQuestion = (TextView)itemView.findViewById(R.id.textView_no_questionText_doSurvey);
            question = (TextView)itemView.findViewById(R.id.textView_question_questionText_doSurvey);
            answer = (EditText)itemView.findViewById(R.id.editText_answer_questionText_doSurvey);
            answerLayout = (TextInputLayout)itemView.findViewById(R.id.TextInputLayout_answer_questionText_doSurvey);

            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer.setCursorVisible(true);
                }
            });
        }
    }
    public static class ChoiceQuestionsViewHolder extends RecyclerView.ViewHolder {
        private static String TAG = ChoiceQuestionsViewHolder.class.getSimpleName();
        public TextView noQuestion;
        public TextView question;
        public RadioGroup choiceGroup;
        private Context context;

        public ChoiceQuestionsViewHolder(View itemView,Context context) {//set paamiter for choice and set choice in to tableRow
            super(itemView);
            this.context = context;
            noQuestion = (TextView)itemView.findViewById(R.id.textView_no_questionChoice);
            question = (TextView)itemView.findViewById(R.id.textView_question_questionChoice);
            choiceGroup = (RadioGroup)itemView.findViewById(R.id.radioGroup_choice);


        }
        public void addChoice( List<ChoiceBean> choiceList){
            choiceGroup.removeAllViews();
            for(ChoiceBean bean:choiceList){
                RadioButton choice = new RadioButton(context);
                choice.setText(bean.getChoiceNumber() +". " +bean.getChoiceData());
                //choice.setTextSize(R.dimen.textSizeChoice);
                choice.setId(bean.getChoiceID());
                choiceGroup.addView(choice);
            }
        }
    }
    public static class OnLoadViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;


        public OnLoadViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar_onLoad);

        }

    }
}
