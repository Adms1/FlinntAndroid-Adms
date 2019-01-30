package com.edu.flinnt.gui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.protocol.CouponListResponse;

import java.util.ArrayList;

import static com.edu.flinnt.gui.CheckoutActivity.selectionList;

/**
 * To Display Coupan for Apply Promocode.
 */
public class CoupanAdapter extends RecyclerView.Adapter<CoupanAdapter.ViewHolder> {

    private ArrayList<CouponListResponse.Coupon> mCoupons;
    private String viewerPictureUrl;
    private Context mContext;

    OnItemClickListener mItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView coupanTxt;
        private ImageView checkedCouponImg;
        private LinearLayout coupanLinear;

        public ViewHolder(View v) {
            super(v);
            coupanTxt = (TextView) v.findViewById(R.id.coupanTxt);
            checkedCouponImg = (ImageView) v.findViewById(R.id.couponImg);
//            checkedCouponImg.setVisibility(View.GONE);

            coupanLinear = (LinearLayout) v.findViewById(R.id.coupanLinear);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


    public void addItems(ArrayList<CouponListResponse.Coupon> items) {
        mCoupons.addAll(items);
        if(selectionList.size() <= 0){
            selectionList.clear();
            for (int i = 0; i < mCoupons.size(); i++) {
                selectionList.add(false);
            }
        }

        notifyDataSetChanged();
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public CoupanAdapter(Context context, ArrayList<CouponListResponse.Coupon> coupons) {
        mContext = context;
        mCoupons = coupons;
        if(selectionList.size() <= 0){
            selectionList.clear();
            for (int i = 0; i < mCoupons.size(); i++) {
                selectionList.add(false);
            }
        }
    }


    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mCoupons.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupan_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String coupanStr = "";
        if(mCoupons.get(position).getCalculateAs().equalsIgnoreCase("P")){
            coupanStr = mContext.getResources().getString(R.string.coupon_pre_message1)+"<font color=#2FB274>"+mCoupons.get(position).getDiscountAmount()+"% </font> "+mContext.getResources().getString(R.string.coupon_pre_message2);
        }else if(mCoupons.get(position).getCalculateAs().equalsIgnoreCase("F")){
            coupanStr = mContext.getResources().getString(R.string.coupon_pre_message1)+"<font color=#2FB274> ₹ "+mCoupons.get(position).getDiscountAmount()+"</font> "+mContext.getResources().getString(R.string.coupon_pre_message2);
        }

        if (null != coupanStr) {
            holder.coupanTxt.setText(Html.fromHtml(coupanStr));
            if(selectionList.size() > position){
                if(selectionList.get(position)){
                    holder.checkedCouponImg.setImageResource(R.drawable.radioon);
                }else {
                    holder.checkedCouponImg.setImageResource(R.drawable.radiooff);
                }
            }

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}