package com.jozzee.mysurvey.adpter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ChoiceBean;
import com.jozzee.mysurvey.bean.QuestionsBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozzee on 27/10/2558.
 */
public class QuestionAdapter extends RecyclerView.Adapter {
    private static String TAG = QuestionAdapter.class.getSimpleName();

    private final int VIE_EMPTY = 0;
    private final int VIEW_TEXT = 1;
    private final int VIEW_CHOICE = 2;
    private OnLoadMoreListener onLoadMoreListener;
    private List<QuestionsBean> questionList;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private Context context;



    public QuestionAdapter(List<QuestionsBean> questionList, RecyclerView recyclerView,Context context) {
        this.questionList = questionList;
        this.context = context;



        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                Log.i(TAG, "onLoadMoreListener");
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }
    }

    @Override
    public long getItemId(int position) {
        //Log.i(TAG, "getItemId");
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        //Log.i(TAG, "getItemViewType");
        int viewType = 0;
        QuestionsBean bean = new QuestionsBean();
        if(questionList.get(position) != null){
            bean = questionList.get(position);
            if(bean.getAnswerType() == 1){// text questions
                viewType = VIEW_TEXT;
            }
            else if(bean.getAnswerType() == 2){ //choice questions
                viewType = VIEW_CHOICE;
            }
        }
        else{//not have any questions
            viewType = VIE_EMPTY;
        }
        return viewType;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_TEXT){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_question_text, parent, false);
            vh = new TextQuestionsViewHolder(v);

        }
        else if(viewType == VIEW_CHOICE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_question_choice, parent, false);
            vh = new ChoiceQuestionsViewHolder(v,context);
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_empty, parent, false);
            vh = new EmptyQuestionsViewHolder(v);
        }
        return vh;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof EmptyQuestionsViewHolder) {
            //Card view empty not any questions.
        }
        else{
            if(holder instanceof TextQuestionsViewHolder){
                Log.e(TAG, "holder instanceof TextQuestionsViewHolder");
                QuestionsBean bean = questionList.get(position);
                if(bean.getQuestionNumber() == 0){
                    bean.setQuestionNumber(position+1);
                }

                ((TextQuestionsViewHolder) holder).noQuestion.setText((bean.getQuestionNumber()) + ".");
                ((TextQuestionsViewHolder) holder).question.setText(bean.getQuestion());
                ((TextQuestionsViewHolder) holder).boxAnswer.setFocusable(false);
                ((TextQuestionsViewHolder) holder).boxAnswer.setEnabled(false);
                //((TextQuestionsViewHolder) holder).boxAnswer.setBackground(ContextCompat.getDrawable(context,R.color.transparent2));

            }
            else{
                Log.e(TAG, "holder instanceof ChoiceQuestionsViewHolder");
                QuestionsBean bean = questionList.get(position);
                if(bean.getQuestionNumber() == 0){
                    bean.setQuestionNumber(position + 1);
                }

                ((ChoiceQuestionsViewHolder) holder).noQuestion.setText((bean.getQuestionNumber()) + ".");
                ((ChoiceQuestionsViewHolder) holder).question.setText(bean.getQuestion());
                ((ChoiceQuestionsViewHolder) holder).addChoice(bean.getChoiceList());

            }
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setQuestionList(List<QuestionsBean> questionList){
        this.questionList = questionList;
    }
    public static class TextQuestionsViewHolder extends RecyclerView.ViewHolder{

        public TextView noQuestion;
        public TextView question;
        public EditText boxAnswer;
        public TextQuestionsViewHolder(View itemView) {
            super(itemView);
            noQuestion = (TextView)itemView.findViewById(R.id.textView_no_questionText);
            question = (TextView)itemView.findViewById(R.id.textView_question_questionText);
            boxAnswer = (EditText)itemView.findViewById(R.id.editText_answer_questionText);

        }
    }
    public static class ChoiceQuestionsViewHolder extends RecyclerView.ViewHolder {
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
        public void addChoice(List<ChoiceBean> choiceList){
            //Log.e(TAG,"addChoice");
            choiceGroup.removeAllViews();
            int i=1;
            for(ChoiceBean bean:choiceList){
                RadioButton choice = new RadioButton(context);
                if(bean.getChoiceNumber() == 0){
                    choice.setText(i+". " +bean.getChoiceData());
                    i++;
                }
                else{
                    choice.setText(bean.getChoiceNumber() +". " +bean.getChoiceData());
                }

                choice.setEnabled(false);
                choiceGroup.addView(choice);
            }
        }

        public List<ChoiceBean> getChoiceListFromJsonString (String choiceAsJsonString){
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(choiceAsJsonString, JsonArray.class);
            Type listType = new TypeToken<ArrayList<ChoiceBean>>(){}.getType();
            return gson.fromJson(jsonArray, listType);
        }

    }
    public static class EmptyQuestionsViewHolder extends RecyclerView.ViewHolder{

        public EmptyQuestionsViewHolder(View itemView) {
            super(itemView);

        }
    }
}
