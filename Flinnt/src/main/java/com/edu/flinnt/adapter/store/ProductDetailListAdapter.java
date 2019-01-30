package com.edu.flinnt.adapter.store;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.edu.flinnt.R;
import java.util.ArrayList;


public class ProductDetailListAdapter extends RecyclerView.Adapter<ProductDetailListAdapter.CustomViewHolder> {

    private ArrayList<String> keyList = new ArrayList<String>();
    private ArrayList<String> valueList = new ArrayList<String>();
    private Context context;

    public ProductDetailListAdapter(Context context,ArrayList<String> keyList,ArrayList<String> valueList) {
        this.keyList = keyList;
        this.valueList = valueList;
    }

    @Override
    public ProductDetailListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_product_details, null);
        ProductDetailListAdapter.CustomViewHolder viewHolder = new ProductDetailListAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductDetailListAdapter.CustomViewHolder customViewHolder, int i) {
        String key = keyList.get(i);
        String value  = valueList.get(i);
        customViewHolder.key.setText(key);
        customViewHolder.value.setText(String.valueOf(value));
    }

    /**
     * Counts total number of alerts
     * @return total number of alerts
     */
    public int getItemCount() {
        return (null != valueList ? valueList.size() : 0);
    }


    public void clearData(){
        valueList.clear();
        keyList.clear();
        notifyDataSetChanged();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected TextView key,value;

        public CustomViewHolder(View itemView) {
            super(itemView);
            key = (TextView) itemView.findViewById(R.id.key_txt);
            value = (TextView) itemView.findViewById(R.id.value_txt);
        }

    }



}
