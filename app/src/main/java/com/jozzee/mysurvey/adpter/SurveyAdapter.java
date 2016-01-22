package com.jozzee.mysurvey.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.jozzee.mysurvey.bean.SurveyBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jozzee on 19/10/2558.
 */
public class SurveyAdapter extends RecyclerView.Adapter {
    private static String TAG = SurveyAdapter.class.getSimpleName();

    private final int viewOnLoad = 0; //view on load data to recyclerView.
    private final int viewContent = 1; //view on show data in recyclerView.
     int typeOffRecycleView = 0; // type 0 is listView , type 1 is cardView
    private String[] month =  new String[]  {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private List<SurveyBean> surveyList;
    private Context context;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean search = false;
    //Bitmap bitmap;

    public SurveyAdapter(){

    }

    public SurveyAdapter(List<SurveyBean> surveyList, Context context, RecyclerView recyclerView,
                         int typeOffRecycleView,boolean search) {
        this.surveyList = surveyList;
        this.context = context;
        this.typeOffRecycleView = typeOffRecycleView;
        this.search = search;
       // bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.im_survey_form);


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
                        if (onLoadMoreListener != null) {  Log.i(TAG,"onLoadMoreListener");

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
        return(surveyList.get(position) != null)?viewContent:viewOnLoad;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == viewContent) {
            if(typeOffRecycleView == 0){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_surveyform_list, parent, false);
                vh = new SurveyViewHolder(v,typeOffRecycleView);
            }
            else{
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_surveyform, parent, false);
                vh = new SurveyViewHolder(v,typeOffRecycleView);
            }
        }
        else if(viewType == viewOnLoad){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_onload, parent, false);
            vh = new OnLoadViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { //Log.i(TAG,"onBindViewHolder");

        if (holder instanceof SurveyViewHolder) {
            SurveyBean bean = surveyList.get(position);

            ((SurveyViewHolder) holder).surveyName.setText(bean.getSurveyName());
            ((SurveyViewHolder) holder).creator.setText(bean.getCreator());
            ((SurveyViewHolder) holder).numberOfTested.setText(String.valueOf(bean.getNumberOfTested()) + " Responses");
            ((SurveyViewHolder) holder).lastUpdate.setText(getDateTime(bean.getLastUpdate()));
            if(typeOffRecycleView == 0){
                ((SurveyViewHolder) holder).surveyType.setText(bean.getSurveyType());
            }

            if(search){
                ((SurveyViewHolder) holder).surveyType.setVisibility(View.VISIBLE);
            }

            if(!(bean.getCoverImage().equals("noImage"))){
                //Glide.with(fragment).resumeRequests();
                Glide.with(context)
                        .load(bean.getCoverImage())
                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                        //.placeholder(R.drawable.im_survey_form_blur)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(((SurveyViewHolder) holder).coverImage);
            }
            else{
                ((SurveyViewHolder) holder).coverImage.setImageDrawable(
                        ContextCompat.getDrawable(context,R.drawable.im_survey_form));
            }
            ((SurveyViewHolder) holder).surveyID = bean.getSurveyID();
            ((SurveyViewHolder) holder).surveyVersion = bean.getSurveyVersion();
            ((SurveyViewHolder) holder).numberOfQuestions = bean.getNumberOfQuestions();
            ((SurveyViewHolder) holder).link = bean.getLink();

        }
        else if(holder instanceof OnLoadViewHolder){

        }
    }
    @Override
    public int getItemCount() { //Log.i(TAG,"getItemCount");

        return surveyList.size();
    }
    public void setTypeOffRecycleView(int typeOffRecycleView){
        this.typeOffRecycleView = typeOffRecycleView;
    }

    public void setLoaded() {
        loading = false;
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setSurveyList(List<SurveyBean> surveyList){
        this.surveyList = surveyList;
    }

    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        int typeOffRecycleView;
        public TextView surveyName;
        public TextView creator;
        public TextView numberOfTested;
        public TextView lastUpdate;
        public ImageView coverImage;
        public TextView surveyType;

        public int surveyID;
        public int surveyVersion;
        public int numberOfQuestions;
        public String link;

        public SurveyViewHolder(View itemView ,int type ) {
            super(itemView);
            this.typeOffRecycleView = type;

            surveyName = (TextView)itemView.findViewById(R.id.surveyName_cardView);
            creator = (TextView)itemView.findViewById(R.id.creator_cardView);
            numberOfTested = (TextView)itemView.findViewById(R.id.numberOfTests_cardView);
            lastUpdate = (TextView)itemView.findViewById(R.id.lastUpdate_cardView);
            coverImage = (ImageView)itemView.findViewById(R.id.imageView_cardView);
            if(typeOffRecycleView == 0){
                surveyType = (TextView)itemView.findViewById(R.id.textView_surveyType_cardView);
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
    public String getDateTime(String oldDate){
        Log.e(TAG,"oldDate = " +oldDate);
        String currentDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String dateTime = currentDate;
        int[] arrayCurrentDate= new int[5];
        int[] arrayOldDate= new int[5];

        arrayOldDate[0] = Integer.parseInt(oldDate.substring(0, 4));
        arrayOldDate[1] = Integer.parseInt(oldDate.substring(5, 7));
        arrayOldDate[2] = Integer.parseInt(oldDate.substring(8, 10));
        arrayOldDate[3] = Integer.parseInt(oldDate.substring(11, 13));
        arrayOldDate[4] = Integer.parseInt(oldDate.substring(14, 16));
        arrayCurrentDate[0] = Integer.parseInt(currentDate.substring(0, 4));
        arrayCurrentDate[1] = Integer.parseInt(currentDate.substring(5, 7));
        arrayCurrentDate[2] = Integer.parseInt(currentDate.substring(8, 10));
        arrayCurrentDate[3] = Integer.parseInt(currentDate.substring(11, 13));
        arrayCurrentDate[4] = Integer.parseInt(currentDate.substring(14, 16));

        if(arrayOldDate[0] == arrayCurrentDate[0]){
            if(arrayOldDate[1] == arrayCurrentDate[1]){
                if(arrayOldDate[2] == arrayCurrentDate[2]){
                    if(arrayOldDate[3] == arrayCurrentDate[3]){
                        if(arrayOldDate[4] == arrayCurrentDate[4]){
                            dateTime = "1 minute ago";
                        }
                        else{
                            int settlement = (arrayCurrentDate[4] - arrayOldDate[4]);
                            dateTime = String.valueOf(settlement) +"m";
                        }
                    }
                    else{
                        int settlement = (arrayCurrentDate[3] - arrayOldDate[3]);
                        if(settlement == 1){
                            int min2 = arrayCurrentDate[4] + (60-arrayOldDate[4]);
                            if(min2<60){
                                dateTime = String.valueOf(min2) +"m";
                            }
                        }
                        else{
                            dateTime = String.valueOf(settlement) +"h";
                        }
                    }
                }
                else{
                    if((arrayCurrentDate[2] - arrayOldDate[2]) == 1 ){
                        int hour = arrayCurrentDate[3] + (24-arrayOldDate[3]);
                        if(hour<=1){
                            int min = arrayCurrentDate[4] + (60-arrayOldDate[4]);
                            if(min<60){
                                dateTime = String.valueOf(min) +"m";
                            }
                            else{
                                dateTime = String.valueOf(min/60) +"h";
                            }
                        }
                        else{
                            dateTime = String.valueOf(hour) +"h";
                        }
                    }
                    else{
                        dateTime = String.valueOf(arrayOldDate[2]) +" " +month[arrayOldDate[1]-1];
                    }
                }
            }
            else{
                dateTime = String.valueOf(arrayOldDate[2]) +" " +month[arrayOldDate[1]-1];
            }
        }
        else{
            dateTime = month[arrayOldDate[1]-1] +" " +arrayOldDate[0];
        }
        return dateTime;
    }
}
//--------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------
                   /* totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    //Log.e(TAG,"loading = "+String.valueOf(loading) +", totalItemCount = "+ String.valueOf(totalItemCount) +", lastVisibleItem = " +String.valueOf(lastVisibleItem));

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) &&!(allDataFromServer)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        //loading = true;
                    }
                    else if(allDataFromServer){
                       //bus to fragment to crate snakeBar !!
                    }*/