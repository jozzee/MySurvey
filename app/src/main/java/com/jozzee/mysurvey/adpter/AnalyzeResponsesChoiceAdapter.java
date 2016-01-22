package com.jozzee.mysurvey.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.AnalyzeChoiceQuestionBean;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Jozzee on 25/11/2558.
 */
public class AnalyzeResponsesChoiceAdapter extends  RecyclerView.Adapter {
    private static String TAG = AnalyzeResponsesChoiceAdapter.class.getSimpleName();

    private List<AnalyzeChoiceQuestionBean> analyzeList;


    public AnalyzeResponsesChoiceAdapter(List<AnalyzeChoiceQuestionBean> choiceList) {
        this.analyzeList = analyzeList;

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_analyze_choice_showchoice, parent, false);
        return new ChoiceViewHolder(v);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(analyzeList.get(position)!= null){
            AnalyzeChoiceQuestionBean bean = analyzeList.get(position);
            ((ChoiceViewHolder) holder).colorType.setBackgroundColor(bean.getColor());
            ((ChoiceViewHolder) holder).choice.setText(bean.getChoice());
            ((ChoiceViewHolder) holder).percent.setText(String.valueOf(bean.getPercent()) +"%");
            ((ChoiceViewHolder) holder).numberOffSelectThisChoice.setText(String.valueOf(bean.getNumberOffSelectThisChoice()));
        }

    }
    @Override
    public int getItemCount() { //Log.i(TAG,"getItemCount");
        return analyzeList.size();
    }

    public void setAnalyzeList(List<AnalyzeChoiceQuestionBean> analyzeList) {
        this.analyzeList = analyzeList;
    }

    public static String getTAG() {
        return TAG;
    }

    private static class ChoiceViewHolder extends RecyclerView.ViewHolder {

        public ImageView colorType;
        public TextView choice;
        public TextView percent;
        public TextView numberOffSelectThisChoice;
        public ChoiceViewHolder(View itemView) {
            super(itemView);
            colorType = (ImageView)itemView.findViewById(R.id.imageView_color_cardView_analyzeChoice);
            choice = (TextView)itemView.findViewById(R.id.textView_Choice_cardView_analyzeChoice);
            percent = (TextView)itemView.findViewById(R.id.textView_percent_analyzeChoice);
            numberOffSelectThisChoice = (TextView)itemView.findViewById(R.id.textView_numberSelectThisChoice_cardView_analyzeChoice);
        }
    }
}
