package com.edu.flinnt.adapter.store;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.AlertListAdapter;
import com.edu.flinnt.customviews.store.MySpannable;
import com.edu.flinnt.models.store.StoreBookDetailResponse;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;
import java.util.List;

public class AuthorListAdapter extends RecyclerView.Adapter<AuthorListAdapter.CustomViewHolder> {

    private List<StoreBookDetailResponse.Author> authorList = new ArrayList<StoreBookDetailResponse.Author>();;
    private Context context;

    public AuthorListAdapter(Context context,List<StoreBookDetailResponse.Author> authorList) {
        this.authorList = authorList;
    }

    @Override
    public AuthorListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_authorlist, null);
        AuthorListAdapter.CustomViewHolder viewHolder = new AuthorListAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorListAdapter.CustomViewHolder customViewHolder, int i) {

        StoreBookDetailResponse.Author  authorData = authorList.get(i);

        customViewHolder.tvAuthorname.setText(authorData.getAuthorName());

        if(authorData.getAboutAuthor() != null) {
            customViewHolder.tvAuthorDesc.setText(String.valueOf(authorData.getAboutAuthor()));

        }else{
            customViewHolder.tvAuthorDesc.setText("");
        }

        if(authorData.getAboutAuthor() != null) {
            makeTextViewResizable(customViewHolder.tvAuthorDesc, 2, "View More", true);
        }

    }

    /**
     * Counts total number of alerts
     * @return total number of alerts
     */
    public int getItemCount() {
        return (null != authorList ? authorList.size() : 0);
    }

    public void addItems(ArrayList<StoreBookDetailResponse.Author> items) {
        authorList.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData(){
        authorList.clear();
        notifyDataSetChanged();
    }

    public StoreBookDetailResponse.Author getItem(int position) {
        if ( position >= 0 && position < authorList.size() ) {
            return authorList.get(position);
        }
        else return null;
    }


    public void remove(String ID) {
        for (int i = 0; i < authorList.size(); i++) {
            if (authorList.get(i).getAuthorId().equals(ID)) {
                authorList.remove(i);
//                if(!isSearchMode) notifyItemRemoved(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected TextView tvAuthorname,tvAuthorDesc;



        public CustomViewHolder(View itemView) {
            super(itemView);
            tvAuthorname = (TextView) itemView.findViewById(R.id.author_name_txt);
            tvAuthorDesc = (TextView) itemView.findViewById(R.id.about_author_txt);
        }

    }

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });

    }

//    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
//                                                                     final int maxLine, final String spanableText, final boolean viewMore) {
//        String str = strSpanned.toString();
//        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
//
//        if (str.contains(spanableText)) {
//
//
//            ssb.setSpan(new MySpannable(false){
//                @Override
//                public void onClick(View widget) {
//                    tv.setLayoutParams(tv.getLayoutParams());
//                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
//                    tv.invalidate();
//                    if (viewMore) {
//                        makeTextViewResizable(tv, -1, "View Less", false);
//                    } else {
//                        makeTextViewResizable(tv, 3, "View More", true);
//                    }
//                }
//            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
//
//        }
//        return ssb;
//
//    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        makeTextViewResizable(tv, 3, "View More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}
