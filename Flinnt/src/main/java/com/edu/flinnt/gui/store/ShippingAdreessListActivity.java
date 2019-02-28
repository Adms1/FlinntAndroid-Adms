package com.edu.flinnt.gui.store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.ShippingAddressListAdapter;
import com.edu.flinnt.core.store.AddUserAddressResponse;
import com.edu.flinnt.core.store.AddressResponse;
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.HashMap;

public class ShippingAdreessListActivity extends AppCompatActivity implements ShippingAddressListAdapter.AddressSelectListener {

    private Toolbar toolbar;
    private ShippingAddressListAdapter shippingAddressListAdapter;
    private Handler mHandler;
    public static ProgressDialog mProgressDialog = null;
    private ShippingAdressModel shippingAdressModel;
    private RecyclerView rvAddressList;
    private Button btnAddAddress,btnDelivery;
    private ShippingAddressListAdapter.AddressSelectListener addressSelectListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        setContentView(R.layout.activity_shipping_adreess_list);



        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setTitle("Select Shipping Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvAddressList = (RecyclerView)findViewById(R.id.rv_user_address_list);
        btnAddAddress = (Button)findViewById(R.id.btn_add_new);
        btnDelivery = (Button)findViewById(R.id.btn_delivery);

        addressSelectListener = (ShippingAddressListAdapter.AddressSelectListener)this;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());

                        if(msg.obj instanceof ShippingAdressModel){
                            shippingAdressModel = (ShippingAdressModel) msg.obj;
                            shippingAddressListAdapter = new ShippingAddressListAdapter(ShippingAdreessListActivity.this,shippingAdressModel.getData(),addressSelectListener);
                            rvAddressList.setLayoutManager(new LinearLayoutManager(ShippingAdreessListActivity.this));
                            rvAddressList.setAdapter(shippingAddressListAdapter);
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



        AddressResponse shippingAddressRequest = new AddressResponse(mHandler,Config.getStringValue(Config.USER_ID));
        shippingAddressRequest.getAddressListRequest();

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddress = new Intent(ShippingAdreessListActivity.this,ShippingAddressActivity.class);
                startActivityForResult(intentAddress,107);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity="+Flinnt.SETTINGS+"&user="+Config.getStringValue(Config.USER_ID));
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    public static void stopProgressDialog(){
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
        catch (Exception e) {
            LogWriter.err(e);
        }
        finally {
            mProgressDialog = null;
        }
    }
    //onBackPressed();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 109:
                if(resultCode == Activity.RESULT_OK){
                    AddressResponse shippingAddressRequest = new AddressResponse(mHandler,Config.getStringValue(Config.USER_ID));
                    shippingAddressRequest.getAddressListRequest();
                }
            case 110:
                if(resultCode == Activity.RESULT_OK){

                    try {


                        String userAddressId = data.getStringExtra("user_address_id");

                        if (userAddressId != null) {

                            for (int count = 0; count < shippingAdressModel.getData().size(); count++) {
                                if (shippingAdressModel.getData().get(count).getUserAddressId() == Integer.parseInt(userAddressId)) {
                                    shippingAddressListAdapter.setCheckItem(count);
                                    break;
                                }
                            }
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            break;
        }
    }

    @Override
    public void onAddressSelected(boolean isSelected, final ArrayList<ShippingAdressModel.Datum> bundleData2) {

            btnDelivery.setEnabled(true);
            btnDelivery.setAlpha(1);

            btnDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentCheckout = new Intent(ShippingAdreessListActivity.this,CheckoutActivityNew.class);
                    ArrayList<ShippingAdressModel.Datum> bundleData1 = new ArrayList<ShippingAdressModel.Datum>();
                    bundleData1.addAll(bundleData2);
                    intentCheckout.putParcelableArrayListExtra("address_data",bundleData1);
                    startActivityForResult(intentCheckout,110);
                }
            });


    }
}
