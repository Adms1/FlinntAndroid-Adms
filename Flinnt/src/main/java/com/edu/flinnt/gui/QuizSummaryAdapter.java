package com.edu.flinnt.gui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.protocol.QuizResultAnalysisResponse;

import java.util.ArrayList;

/**
 * Created by flinnt-android-1 on 1/2/17.
 */

public class QuizSummaryAdapter extends RecyclerView.Adapter<QuizSummaryAdapter.ViewHolder> {

    ArrayList<QuizResultAnalysisResponse.Summary> mSummaryList;

    public QuizSummaryAdapter(ArrayList<QuizResultAnalysisResponse.Summary> summaryList){
        super();
        mSummaryList = summaryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_result_summary_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.mQuizSummaryLinear = (LinearLayout)v.findViewById(R.id.quiz_summary_ll);
        viewHolder.mSectionNameTxt = (TextView)v.findViewById(R.id.section_name_txt);
        viewHolder.mTotalQueTxt = (TextView)v.findViewById(R.id.total_que_tv);
        viewHolder.mCorrectTxt = (TextView)v.findViewById(R.id.correct_tv);
        viewHolder.mWrongTxt = (TextView)v.findViewById(R.id.wrong_tv);
        viewHolder.mUnAnsweredTxt = (TextView)v.findViewById(R.id.unanswer_tv);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSectionNameTxt.setText(mSummaryList.get(position).getSectionName());
        holder.mTotalQueTxt.setText(""+mSummaryList.get(position).getTotalQuestions());
        holder.mCorrectTxt.setText(""+mSummaryList.get(position).getTotalCorrect());
        holder.mWrongTxt.setText(""+mSummaryList.get(position).getTotalWrong());
        holder.mUnAnsweredTxt.setText(""+mSummaryList.get(position).getTotalUnanswered());


        if(position%2 == 0){
            holder.mQuizSummaryLinear.setBackgroundColor(Color.WHITE);
        }else {
            holder.mQuizSummaryLinear.setBackgroundColor(Color.parseColor("#dde1e4"));
        }
    }

    @Override
    public int getItemCount() {
        return mSummaryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mSectionNameTxt,mTotalQueTxt,mCorrectTxt,mWrongTxt,mUnAnsweredTxt;
        private LinearLayout mQuizSummaryLinear;

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}