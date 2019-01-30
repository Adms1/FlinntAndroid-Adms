package com.edu.flinnt.gui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.protocol.QuizViewResultResponse;

import java.util.Calendar;
import java.util.List;

/**
 * Created by flinnt-android-3 on 3/2/17.
 */
//@Nikhil 16072018  as I checked image is wel set for answer
public abstract class QuizResultAnswerOptionAdapter extends RecyclerView.Adapter<QuizResultAnswerOptionAdapter.ViewHolder> {

    List<QuizViewResultResponse.Option> mOptionsList ;
    int ascii = 65;
    public QuizResultAnswerOptionAdapter(List<QuizViewResultResponse.Option> mOptionsList ){
        this.mOptionsList = mOptionsList;
    }
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    public abstract void onOptionClicked(String data , int position);
    @Override
    public void onBindViewHolder(QuizResultAnswerOptionAdapter.ViewHolder holder, final int position) {
        final QuizViewResultResponse.Option option = mOptionsList.get(position);
        int charAscii = ascii + position;
        holder.mWebViewOptionDetail.loadData(option.getText(),"text/html","UTF-8");
        holder.mWebViewOptionDetail.setOnClickListener(null);
        holder.mWebViewOptionDetail.setOnTouchListener(new View.OnTouchListener() {
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
                            onOptionClicked(option.getText() , position);
                        }
                    }
                }
                return true;
            }
        });

        holder.mImgOptionLabel.setText( new Character((char) charAscii).toString());

        Log.d("qlogg",option.getIsCorrect() +"->"+option.getSelected());

        holder.mImgOptionLabel.setTextColor(Color.parseColor("#000000"));
        if(option.getIsCorrect() == 1 && option.getSelected() == 1 ){
            holder.mImgOptionSelect.setImageResource(R.drawable.green_correct_small);
            holder.mImgOptionLabel.setTextColor(Color.parseColor("#2c953c"));
            holder.mImgOptionSelect.setVisibility(View.VISIBLE);

        }else if(option.getIsCorrect() == 1 ){
            holder.mImgOptionSelect.setImageResource(R.drawable.blue_correct_small);
            holder.mImgOptionLabel.setTextColor(Color.parseColor("#007dcd"));
            holder.mImgOptionSelect.setVisibility(View.VISIBLE);

        }else if(option.getSelected() == 1){
            holder.mImgOptionSelect.setImageResource(R.drawable.wrong_small);
            holder.mImgOptionLabel.setTextColor(Color.parseColor("#fd0000"));
            holder.mImgOptionSelect.setVisibility(View.VISIBLE);
        }else {
            holder.mImgOptionSelect.setVisibility(View.INVISIBLE);
        }
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
        public WebView mWebViewOptionDetail;
        public ImageView mImgOptionSelect;
        public TextView mImgOptionLabel;
        public ViewHolder(View v) {
            super(v);
            mWebViewOptionDetail = (WebView) v.findViewById(R.id.answer_text_webview);
            mImgOptionSelect = (ImageView) v.findViewById(R.id.answer_select_iv);
            mImgOptionLabel = (TextView)v.findViewById(R.id.option_lable_tv);
        }

    }

}
