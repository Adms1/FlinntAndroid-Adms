package com.edu.flinnt.gui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.protocol.QuizStartResponse;

import java.util.Calendar;
import java.util.List;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
public abstract class QuizQuestionOptionAdapter extends RecyclerView.Adapter<QuizQuestionOptionAdapter.ViewHolder> {

    QuizQuestionsActivity.OnAnswerChecked mOnAnswerChecked;
    List<QuizStartResponse.Option> mOptionsList ;
    Activity activity;
    int ascii = 65;
    public QuizQuestionOptionAdapter(Activity activity ,List<QuizStartResponse.Option> mOptionsList , QuizQuestionsActivity.OnAnswerChecked mOnAnswerChecked){
        this.activity = activity;
        this.mOptionsList = mOptionsList;
        this.mOnAnswerChecked = mOnAnswerChecked;
    }

    public abstract void onOptionClicked(String data , int position);
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    @Override
    public void onBindViewHolder(QuizQuestionOptionAdapter.ViewHolder holder, final int position) {
        final QuizStartResponse.Option option = mOptionsList.get(position);
        int charAscii = ascii + position;
        holder.optionDetailTextView.loadData(option.getText(),"text/html","UTF-8");
        holder.optionDetailTextView.setOnClickListener(null);
        holder.optionDetailTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            onOptionClicked(option.getText(),position);
                        }
                    }
                }
                return true;
            }
        });
        holder.optionLable.setText( new Character((char) charAscii).toString());
        if(option.getSelected() == 1){
            holder.optionCheckBox.setImageResource(R.drawable.radio_button_checked);
            mOnAnswerChecked.onAnswerCheckChanged(position,true,false);
        }else{
            holder.optionCheckBox.setImageResource(R.drawable.radio_button_unchecked);
        }

        holder.optionSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if( mOptionsList.get(position).getSelected() == 0){
                        mOnAnswerChecked.onAnswerCheckChanged(position,true, true);
                    }else{
                        mOnAnswerChecked.onAnswerCheckChanged(position,false , true);
                    }
            }
        });
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_question_answer_options, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mOptionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public WebView optionDetailTextView;
        public ImageView optionCheckBox;
        public TextView optionLable;
        public LinearLayout optionSelectLayout;
        public ViewHolder(View v) {
            super(v);
            optionDetailTextView = (WebView) v.findViewById(R.id.answer_text_webview);
            optionCheckBox = (ImageView) v.findViewById(R.id.answer_select_iv);
            optionLable = (TextView)v.findViewById(R.id.option_lable_tv);
            optionSelectLayout = (LinearLayout)v.findViewById(R.id.select_answer_layout);
        }

    }
}
