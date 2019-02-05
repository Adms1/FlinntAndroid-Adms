package com.edu.flinnt.adapter.store;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.edu.flinnt.R;
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.models.store.StoreBookDetailResponse;
import java.util.ArrayList;
import java.util.List;

public class ShippingAddressListAdapter extends RecyclerView.Adapter<ShippingAddressListAdapter.CustomViewHolder> {

    private List<ShippingAdressModel.Datum> addressList = new ArrayList<ShippingAdressModel.Datum>();
    private Context context;

    public ShippingAddressListAdapter(Context context,List<ShippingAdressModel.Datum> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @Override
    public ShippingAddressListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_shipping_address, null);
        ShippingAddressListAdapter.CustomViewHolder viewHolder = new ShippingAddressListAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShippingAddressListAdapter.CustomViewHolder customViewHolder, int i) {

        ShippingAdressModel.Datum data = addressList.get(i);

        customViewHolder.tvName.setText(String.valueOf(data.getFullname()));
        customViewHolder.tvAddress.setText(String.valueOf(data.getAddress1()+","+data.getAddress2())+"\n"+data.getCity()+"\n"+"Gujarat"+"-"+data.getPin());
        customViewHolder.tvPhone.setText(String.valueOf(data.getPhone()));

    }


    public int getItemCount() {
        return (null != addressList ? addressList.size() : 0);
    }

    public void addItems(ArrayList<ShippingAdressModel.Datum> items) {
        addressList.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData(){
        addressList.clear();
        notifyDataSetChanged();
    }

    public ShippingAdressModel.Datum getItem(int position) {
        if ( position >= 0 && position < addressList.size() ) {
            return addressList.get(position);
        }
        else return null;
    }


    public void remove(String ID) {
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).getUserAddressId().equals(ID)) {
                addressList.remove(i);
//                if(!isSearchMode) notifyItemRemoved(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected TextView tvName,tvPhone,tvAddress;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);

        }

    }




}
