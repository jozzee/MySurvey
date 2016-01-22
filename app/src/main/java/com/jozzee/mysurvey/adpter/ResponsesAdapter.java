package com.jozzee.mysurvey.adpter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.ResponsesBean;

import java.util.List;

/**
 * Created by Jozzee on 18/11/2558.
 */
public class ResponsesAdapter extends RecyclerView.Adapter {
    private static String TAG = ResponsesAdapter.class.getSimpleName();

    private int viewTypeOnload = 0;
    private int viewTypeContent = 1;
    private List<ResponsesBean> responsesList;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public ResponsesAdapter() {
    }

    public ResponsesAdapter(RecyclerView recyclerView, List<ResponsesBean> responsesList, Context context) {
        this.responsesList = responsesList;
        this.context = context;
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
        if(responsesList.get(position) != null)
            return viewTypeContent;
        else
            return  viewTypeOnload;
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == viewTypeContent){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_responses, parent, false);
            vh = new ResponsesViewHolder(v);
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_onload, parent, false);
            vh = new ProgressBarItemViewHolder(v);
        }
        return vh;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ResponsesViewHolder) {
            ResponsesBean bean = responsesList.get(position);
            ((ResponsesViewHolder) holder).answerBy.setText(bean.getAnswerBy());
            ((ResponsesViewHolder) holder).statusMember.setText(bean.getStatus());
            ((ResponsesViewHolder) holder).responsesDate.setText(conventDateTimeToShow(bean.getResponsesDate()));
        }
        else{

        }
    }
    @Override
    public int getItemCount() { //Log.i(TAG,"getItemCount");
        return responsesList.size();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setLoaded() {
        loading = false;
    }
    public void setListItem(List<ResponsesBean> responsesList){
        this.responsesList = responsesList;
    }
    private class ResponsesViewHolder extends RecyclerView.ViewHolder{

        public TextView answerBy;
        public TextView statusMember;
        public TextView responsesDate;

        public ResponsesViewHolder(View itemView) {
            super(itemView);
            answerBy = (TextView)itemView.findViewById(R.id.textView_showNameAnswer_cardViewResponses);
            statusMember = (TextView)itemView.findViewById(R.id.textView_showStatusMember_cardViewResponses);
            responsesDate = (TextView)itemView.findViewById(R.id.textView_showDateOffResponses_cardViewResponses);
        }
    }
    private class ProgressBarItemViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;
        public ProgressBarItemViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar_onLoad);

        }
    }
    public String conventDateTimeToShow(String oldDateTime){
        String[] dateTime= new String[5];
        dateTime[0] = oldDateTime.substring(0, 4); //years
        dateTime[1] = oldDateTime.substring(5, 7); //mont
        dateTime[2] = oldDateTime.substring(8, 10); //day
        dateTime[3] = oldDateTime.substring(11, 13); //hour
        dateTime[4] = oldDateTime.substring(14, 16); //min


        return dateTime[2] +"/"+dateTime[1] +"/"+dateTime[0] +", "+dateTime[3]+":"+dateTime[4];
    }


}
