package com.edu.flinnt.gui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.QuizStartResponse;

import java.util.List;

/**
 * Created by flinnt-android-1 on 1/2/17.
 */

public abstract class QuizFilterAdapter extends RecyclerView.Adapter<QuizFilterAdapter.ViewHolder> {

    List<QuizStartResponse.Quiz> quiz;
    Activity mActivity;
    public QuizFilterAdapter(Activity activity, List<QuizStartResponse.Quiz> quiz){
        super();
        this.mActivity = activity;
        this.quiz = quiz;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_filter_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.questionNoTxt.setText(position+1+"");
         if (!quiz.get(position).getMarkedForReview().equals("0")){
             holder.questionNoTxt.setBackgroundColor(mActivity.getResources().getColor(R.color.pending_review_background_color));
         } else if(quiz.get(position).getAnswered() == 1) {
             holder.questionNoTxt.setBackgroundColor(mActivity.getResources().getColor(R.color.attempted_background_color));
         } else if (quiz.get(position).getAnswered() == 0 && quiz.get(position).getViewed() == 1){
             holder.questionNoTxt.setBackgroundColor(mActivity.getResources().getColor(R.color.unanswered_background_color));
         } else {
             holder.questionNoTxt.setBackgroundColor(mActivity.getResources().getColor(R.color.unattempted_background_color));
         }

        holder.questionNoTxt.setOnClickListener(null);
        holder.questionNoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position);
            }
        });
    }

    public abstract void onItemClick(int position);

    @Override
    public int getItemCount() {
        return quiz.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView questionNoTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            questionNoTxt = (TextView) itemView.findViewById(R.id.question_no_txt);

        }
    }
}