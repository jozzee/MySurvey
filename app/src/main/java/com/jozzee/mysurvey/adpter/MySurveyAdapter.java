package com.jozzee.mysurvey.adpter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.SurveyBeanForMySurvey;

import java.util.List;
import java.util.UUID;

/**
 * Created by Jozzee on 5/11/2558.
 */
public class MySurveyAdapter extends RecyclerView.Adapter {
    private static String TAG = MySurveyAdapter.class.getSimpleName();
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<SurveyBeanForMySurvey> mySurveyList;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public MySurveyAdapter() {
    }

    public MySurveyAdapter(List<SurveyBeanForMySurvey> mySurveyList, RecyclerView recyclerView, final Context context) {
        this.mySurveyList = mySurveyList;
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
    public int getItemViewType(int position) {
        int viewType;
        if(mySurveyList.get(position) != null){
            viewType = VIEW_ITEM;
        }
        else{
            viewType = VIEW_PROG;
        }
        return viewType;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mysurvey_list, parent, false);
            vh = new MySurveyViewHolder(v);

        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_onload, parent, false);
            vh = new ProgressViewHolder(v);

        }
        return vh;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MySurveyViewHolder) {

            SurveyBeanForMySurvey bean = mySurveyList.get(position);

            ((MySurveyViewHolder) holder).surveyName.setText(bean.getSurveyName());
            ((MySurveyViewHolder) holder).numberOfResponses.setText(String.valueOf(bean.getNumberOfTested()) + " Total Responses");
            ((MySurveyViewHolder) holder).lastUpdate.setText(conventDateTimeToShow(bean.getLastUpdate()));
            ((MySurveyViewHolder) holder).numberOffQuestions.setText(bean.getNumberOfQuestions() +" Questions");
            if(!(bean.getCoverImage().equals("noImage"))){

                Glide.with(context)
                        .load(bean.getCoverImage())
                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                        //.placeholder(R.drawable.im_survey_form_blur)
                        //.centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(((MySurveyViewHolder) holder).coverImage);

            }
            else{
                ((MySurveyViewHolder) holder).coverImage.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.im_survey_form));
            }
            ((MySurveyViewHolder) holder).surveyID = bean.getSurveyID();
            ((MySurveyViewHolder) holder).surveyVersion = bean.getSurveyVersion();
            ((MySurveyViewHolder) holder).surveyType = bean.getSurveyType();
            ((MySurveyViewHolder) holder).surveyStatus = bean.getSurveyStatus();
            ((MySurveyViewHolder) holder).numberOfQuestions = bean.getNumberOfQuestions();
            ((MySurveyViewHolder) holder).createDate = bean.getCreteDate();
            ((MySurveyViewHolder) holder).lastResponses = bean.getLastResponses();
            ((MySurveyViewHolder) holder).link = bean.getLink();

        } else {

        }
    }
    @Override
    public int getItemCount() {
        return mySurveyList.size();
    }
    public void setLoaded() {
        loading = false;
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setMySurveyList(List<SurveyBeanForMySurvey> mySurveyList){
        this.mySurveyList = mySurveyList;
    }

    public static class MySurveyViewHolder extends RecyclerView.ViewHolder {
        private static String TAG = MySurveyViewHolder.class.getSimpleName();
        public TextView surveyName;
        public TextView numberOfResponses;
        public TextView lastUpdate;
        public ImageView coverImage;
        public TextView numberOffQuestions;

        public int surveyID;
        public int surveyVersion;
        public int surveyType;
        public int surveyStatus;
        public int numberOfQuestions;
        public String createDate;
        public String lastResponses;
        public String link;

        public MySurveyViewHolder(View itemView) {
            super(itemView);

            surveyName = (TextView)itemView.findViewById(R.id.surveyName_cardView_mySurvey_list);
            numberOfResponses = (TextView)itemView.findViewById(R.id.numberOfResponses_cardView_mySurvey_list);
            lastUpdate = (TextView)itemView.findViewById(R.id.lastUpdate_cardView_mySurvey_list);
            coverImage = (ImageView)itemView.findViewById(R.id.imageView_mySurvey_list);
            numberOffQuestions = (TextView) itemView.findViewById(R.id.textView_numberQuestion_cardView_mySurvey_list);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static String TAG = ProgressViewHolder.class.getSimpleName();
        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            //progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
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
