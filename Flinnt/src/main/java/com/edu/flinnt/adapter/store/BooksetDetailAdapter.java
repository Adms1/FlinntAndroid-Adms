package com.edu.flinnt.adapter.store;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.models.store.StoreBookSetDetailModel;

import java.util.ArrayList;
import java.util.List;

public class BooksetDetailAdapter extends RecyclerView.Adapter<BooksetDetailAdapter.CustomViewHolder> {

    private List<StoreBookSetDetailModel.Booklist> valueList = new ArrayList<StoreBookSetDetailModel.Booklist>();
    private Context context;
    private ImageLoader mImageLoader;
    private int totalCount;


    public BooksetDetailAdapter(Context context,List<StoreBookSetDetailModel.Booklist> valueList) {
        this.valueList = valueList;
        this.context = context;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    @Override
    public BooksetDetailAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_bookset_detail, null);
        BooksetDetailAdapter.CustomViewHolder viewHolder = new BooksetDetailAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BooksetDetailAdapter.CustomViewHolder customViewHolder, final int i) {
        final StoreBookSetDetailModel.Booklist item  = valueList.get(i);
        customViewHolder.tv_bookname.setText(item.getBookName());
        customViewHolder.tv_price.setText(context.getString(R.string.currency)+""+String.valueOf(item.getSalePrice()));
        customViewHolder.tv_subject.setText(item.getSubjectName());


    }




    public int getItemCount() {
        return (null != valueList ? valueList.size() : 0);
    }


    public void clearData(){
        valueList.clear();
        notifyDataSetChanged();
    }




    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView tv_subject,tv_bookname,tv_price;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tv_subject = (TextView) itemView.findViewById(R.id.tv_subject);
            tv_bookname = (TextView) itemView.findViewById(R.id.tv_bookname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
        }
    }

}
