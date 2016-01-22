package com.jozzee.mysurvey.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ViewAnswerBean;

import java.util.List;

/**
 * Created by Jozzee on 19/11/2558.
 */
public class ViewAnswerAdapter extends RecyclerView.Adapter{
    private static String TAG = ViewAnswerAdapter.class.getSimpleName();

    private int onLoadView = 0;
    private int answerTextView = 1;
    private int answerChoiceView =2;
    private Context context;
    private List<ViewAnswerBean> answerList;

    public ViewAnswerAdapter(List<ViewAnswerBean> answerList, Context context) {
        this.answerList = answerList;
        this.context = context;

    }

    @Override
    public long getItemId(int position) {  //Log.i(TAG, "getItemId");
        return position;
    }
    @Override
    public int getItemViewType(int position) {  //Log.i(TAG, "getItemViewType");
        int viewType = 0;
        ViewAnswerBean bean;
        if(answerList.get(position) != null){
            bean = answerList.get(position);
            if(bean.getAnswerType() == 1)// text questions
                viewType = answerTextView;
            else if(bean.getAnswerType() == 2) //choice questions
                viewType = answerChoiceView;
        }
        else{//on load a questions.
            viewType = onLoadView;
        }
        Log.e(TAG,"viewType "+viewType);
        return viewType;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if(viewType == answerTextView){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_answer_text, parent, false);
            vh = new AnswerTextViewHolder(v);
        }
        else if(viewType == answerChoiceView){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_answer_choice, parent, false);
            vh = new AnswerChoiceViewHolder(v);
        }
        else if(viewType == onLoadView){
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
        else if(holder instanceof AnswerTextViewHolder){
            ViewAnswerBean bean = answerList.get(position);
            ((AnswerTextViewHolder) holder).noQuestion.setText(String.valueOf(bean.getNoQuestion()));
            ((AnswerTextViewHolder) holder).question.setText(bean.getQuestion());
            ((AnswerTextViewHolder) holder).answer.setText(bean.getAnswer());

        }
        else if(holder instanceof AnswerChoiceViewHolder){
            ViewAnswerBean bean = answerList.get(position);
            ((AnswerChoiceViewHolder) holder).noQuestion.setText(String.valueOf(bean.getNoQuestion()));
            ((AnswerChoiceViewHolder) holder).question.setText(bean.getQuestion());
            ((AnswerChoiceViewHolder) holder).radioAnswer.setChecked(true);
            ((AnswerChoiceViewHolder) holder).radioAnswer.setText(String.valueOf(bean.getNoChoice()) +". " +bean.getAnswer());
            ((AnswerChoiceViewHolder) holder).radioAnswer.setEnabled(false);
        }
    }
    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public List<ViewAnswerBean> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<ViewAnswerBean> answerList) {
        this.answerList = answerList;
    }

    public static class AnswerTextViewHolder extends RecyclerView.ViewHolder {
        private static String TAG = AnswerTextViewHolder.class.getSimpleName();

        public TextView noQuestion;
        public TextView question;
        public TextView answer;

        public AnswerTextViewHolder(View itemView) {
            super(itemView);
            noQuestion = (TextView)itemView.findViewById(R.id.textView_noQuestion_viewAnswerText);
            question = (TextView)itemView.findViewById(R.id.textView_question_viewAnswerText);
            answer = (TextView)itemView.findViewById(R.id.textView_answer_viewAnswerText);
        }
    }
    public static class AnswerChoiceViewHolder extends RecyclerView.ViewHolder {
        private static String TAG = AnswerChoiceViewHolder.class.getSimpleName();

        public TextView noQuestion;
        public TextView question;
        public RadioButton radioAnswer;

        public AnswerChoiceViewHolder(View itemView) {
            super(itemView);
            noQuestion = (TextView)itemView.findViewById(R.id.textView_no_viewAnswerChoice);
            question = (TextView)itemView.findViewById(R.id.textView_question_viewAnswerChoice);
            radioAnswer = (RadioButton)itemView.findViewById(R.id.radioButton_answerChoice_cardView_viewAnswerChoice);
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
