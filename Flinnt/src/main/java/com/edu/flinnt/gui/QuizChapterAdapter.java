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

public class QuizChapterAdapter extends RecyclerView.Adapter<QuizChapterAdapter.ViewHolder> {

    ArrayList<QuizResultAnalysisResponse.Result> mResultList;


    public QuizChapterAdapter(ArrayList<QuizResultAnalysisResponse.Result> resultArrayList){
        super();
        mResultList = resultArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_result_analysis_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.srNoTxt = (TextView) v.findViewById(R.id.sr_no_txt);
        viewHolder.sectionNameTxt = (TextView) v.findViewById(R.id.section_name_txt);
        viewHolder.selectedAnswerTxt = (TextView) v.findViewById(R.id.selected_answer_txt);
        viewHolder.currectAnswerTxt = (TextView) v.findViewById(R.id.currect_answer_txt);
        viewHolder.currectAnswerCourseTxt = (TextView) v.findViewById(R.id.currect_answer_course_txt);
        viewHolder.markTxt = (TextView) v.findViewById(R.id.mark_txt);

        viewHolder.resultSectionLinear = (LinearLayout) v.findViewById(R.id.result_section_ll);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.srNoTxt.setText(""+mResultList.get(position).getNo());
        holder.sectionNameTxt.setText(mResultList.get(position).getSectionName());
        holder.selectedAnswerTxt.setText(mResultList.get(position).getSelectedAnswer());
        holder.currectAnswerTxt.setText(mResultList.get(position).getCorrectAnswer());
        holder.currectAnswerCourseTxt.setText(""+mResultList.get(position).getCourseCorrect());
        holder.markTxt.setText(mResultList.get(position).getScore());

        if(position%2 == 0){
            holder.resultSectionLinear.setBackgroundColor(Color.WHITE);
        }else {
            holder.resultSectionLinear.setBackgroundColor(Color.parseColor("#dde1e4"));
        }
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout resultSectionLinear;
        public TextView srNoTxt,sectionNameTxt, selectedAnswerTxt,currectAnswerTxt,currectAnswerCourseTxt,markTxt;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}