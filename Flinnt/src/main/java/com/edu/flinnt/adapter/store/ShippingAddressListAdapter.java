package com.edu.flinnt.adapter.store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.store.ManageUserAddress;
import com.edu.flinnt.gui.store.CheckoutActivityNew;
import com.edu.flinnt.gui.store.ShippingAddressActivity;
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;
import java.util.List;


public class ShippingAddressListAdapter extends RecyclerView.Adapter<ShippingAddressListAdapter.CustomViewHolder> {

    private List<ShippingAdressModel.Datum> addressList = new ArrayList<ShippingAdressModel.Datum>();
    private Context context;
    private static RadioButton lastChecked = null;
    private static int lastCheckedPos = 0;
    private ManageUserAddress addUserAddress;
    private Handler mHandler;
    public ProgressDialog mProgressDialog = null;
    private AddressSelectListener addressSelectListener;

    public ShippingAddressListAdapter(final Context context, List<ShippingAdressModel.Datum> addressList,AddressSelectListener addressSelectListener) {
        this.context = context;
        this.addressList = addressList;
        this.addressSelectListener = addressSelectListener;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                           // LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());
                        if(msg.arg1 == 200){
                            stopProgressDialog();
                            Helper.showAlertMessage(context,"Success","Deleted Successfully","Close");
                            notifyDataSetChanged();
                        }
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    @Override
    public ShippingAddressListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_shipping_address, null);
        ShippingAddressListAdapter.CustomViewHolder viewHolder = new ShippingAddressListAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ShippingAddressListAdapter.CustomViewHolder customViewHolder, final int i) {

        final ShippingAdressModel.Datum data = addressList.get(i);
        customViewHolder.tvName.setText(String.valueOf(data.getFullname()));
        customViewHolder.tvAddress.setText(String.valueOf(data.getAddress1()+","+data.getAddress2())+"\n"+data.getCity()+"\n"+data.getName()+"-"+data.getPin());
        customViewHolder.tvPhone.setText(String.valueOf(data.getPhone()));
        customViewHolder.rbAddress.setChecked(data.isIschecked());
        customViewHolder.rbAddress.setTag(i);

        customViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentShippingAddress = new Intent(context,ShippingAddressActivity.class);
                ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                bundleData.add(addressList.get(i));
                intentShippingAddress.putParcelableArrayListExtra("bundle_address_data",bundleData);
                ((Activity)context).startActivityForResult(intentShippingAddress,109);
            }
        });

        customViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressDialog();
                addressList.remove(i);
                notifyItemRemoved(i);
                addUserAddress = new ManageUserAddress(mHandler,String.valueOf(data.getUserAddressId()));
                addUserAddress.sendDeleteUserAddressRequest();
            }
        });



        if(i == 0){
            customViewHolder.rbAddress.setChecked(true);
            addressList.get(0).setIschecked(true);
        }
        if(i == 0 && addressList.get(0).isIschecked() && customViewHolder.rbAddress.isChecked()) {
            lastChecked = customViewHolder.rbAddress;
            lastCheckedPos = 0;
            ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
            bundleData.add(addressList.get(i));
            addressSelectListener.onAddressSelected(true,bundleData);
        }


        customViewHolder.cardListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!customViewHolder.rbAddress.isChecked()){
                    customViewHolder.rbAddress.setChecked(true);
                    lastChecked = customViewHolder.rbAddress;
                    lastCheckedPos = i;
                    addressList.get(lastCheckedPos).setIschecked(true);

                    ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                    bundleData.add(addressList.get(lastCheckedPos));
                    addressSelectListener.onAddressSelected(true,bundleData);

                }else{
                    customViewHolder.rbAddress.setChecked(false);
                    lastChecked = customViewHolder.rbAddress;
                    lastCheckedPos = i;
                    addressList.get(lastCheckedPos).setIschecked(false);

                    ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                    bundleData.add(addressList.get(lastCheckedPos));
                    addressSelectListener.onAddressSelected(false,bundleData);
                }
            }
        });

        customViewHolder.rbAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                RadioButton cb = (RadioButton) compoundButton;
                int clickedPos = ((Integer)cb.getTag()).intValue();

                if(cb.isChecked()) {
                    if(lastChecked != null) {

                        lastChecked.setChecked(false);
                        addressList.get(lastCheckedPos).setIschecked(false);
                        if(lastCheckedPos >= 0){

                            ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                            bundleData.add(addressList.get(clickedPos));
                            addressSelectListener.onAddressSelected(false,bundleData);
                        }else{
                            ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                            bundleData.add(addressList.get(lastCheckedPos));
                            addressSelectListener.onAddressSelected(false,bundleData);
                        }

                    }

                    lastChecked = cb;
                    lastCheckedPos = i;
                }
                else {
                    addressList.get(lastCheckedPos).setIschecked(false);
                    if(lastCheckedPos >= 0){

                        ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                        bundleData.add(addressList.get(lastCheckedPos));
                        addressSelectListener.onAddressSelected(false,bundleData);
                    }else{
                        ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
                        bundleData.add(addressList.get(lastCheckedPos));
                        addressSelectListener.onAddressSelected(false,bundleData);
                    }
                }

                addressList.get(i).setIschecked(cb.isChecked());

//                ArrayList<ShippingAdressModel.Datum> bundleData = new ArrayList<ShippingAdressModel.Datum>();
//                bundleData.add(addressList.get(lastCheckedPos));
//                addressSelectListener.onAddressSelected(true, bundleData);
            }
        });
//        customViewHolder.cardListItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });


    }



    @Override
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
        if (position >= 0 && position < addressList.size() ) {
            return addressList.get(position);
        }
        else return null;
    }

    public ShippingAdressModel.Datum getItemByAddressId(String addressId) {
        if (addressId != null) {

            for(ShippingAdressModel.Datum data :addressList){
                if(data.getUserAddressId().equals(addressId)){
                    return data;
                }
            }

        }
        return null;
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
    public void setCheckItem(int pos){
        addressList.get(pos).setIschecked(true);
        notifyDataSetChanged();
    }
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(((Activity)context))) {
           mProgressDialog = Helper.getProgressDialog(context,"","", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(((Activity)context)))mProgressDialog.show();
        }
    }

    public void stopProgressDialog(){
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        } finally {
            mProgressDialog = null;
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView tvName,tvPhone,tvAddress;
        private RadioButton rbAddress;
        private CardView cardListItem;
        private Button btnDelete,btnEdit;

        public CustomViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            rbAddress = (RadioButton)itemView.findViewById(R.id.rb_address);
            cardListItem = (CardView)itemView.findViewById(R.id.card_list_item);
            btnDelete = (Button)itemView.findViewById(R.id.btn_delete);
            btnEdit = (Button)itemView.findViewById(R.id.btn_edit);

        }
    }

    public interface AddressSelectListener{
        void onAddressSelected(boolean isSelected,ArrayList<ShippingAdressModel.Datum> bundleData);
    }

}
