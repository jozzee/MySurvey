package com.jozzee.mysurvey.adpter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.AnalyzeTextQuestionBean;

import java.util.List;

/**
 * Created by Jozzee on 23/11/2558.
 */
public class AnalyzeResponsesTextAdapter extends  RecyclerView.Adapter {
    private static String TAG = AnalyzeResponsesTextAdapter.class.getSimpleName();

    private int onLoadView = 0;
    private int contentView = 1;
    private List<AnalyzeTextQuestionBean> analyzeList;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public AnalyzeResponsesTextAdapter() {
    }
    public AnalyzeResponsesTextAdapter(RecyclerView recyclerView, List<AnalyzeTextQuestionBean> analyzeList) {
        this.analyzeList = analyzeList;
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
    @Override
    public int getItemViewType(int position) {
        return (analyzeList.get(position) != null)?contentView:onLoadView;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == onLoadView){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_onload, parent, false);
            vh = new OnLoadViewHolder(v);
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_analyze_text, parent, false);
            vh = new AnalyzeTextViewHolder(v);
        }
        return vh;

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AnalyzeTextViewHolder) {
            AnalyzeTextQuestionBean bean = analyzeList.get(position);
            ((AnalyzeTextViewHolder) holder).answer.setText(bean.getAnswer());
        }
        else{

        }
    }
    @Override
    public int getItemCount() { //Log.i(TAG,"getItemCount");
        return analyzeList.size();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setLoaded() {
        loading = false;
    }

    public List<AnalyzeTextQuestionBean> getAnalyzeList() {
        return analyzeList;
    }

    public void setAnalyzeList(List<AnalyzeTextQuestionBean> analyzeList) {
        this.analyzeList = analyzeList;
    }
    private class AnalyzeTextViewHolder extends RecyclerView.ViewHolder{

        public TextView answer;
        public AnalyzeTextViewHolder(View itemView) {
            super(itemView);
            answer = (TextView)itemView.findViewById(R.id.textView_answer_cardView_analyzeText);
        }
    }
    private class OnLoadViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public OnLoadViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar_onLoad);

        }
    }

}
