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
import android.widget.FrameLayout;
import android.widget.TextView;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.CartListItemAdapter;
import com.edu.flinnt.core.store.AddressResponse;
import com.edu.flinnt.core.store.CartItems;
import com.edu.flinnt.core.store.CartListItemResponse;
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.models.store.StoreBookDetailResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity implements CartListItemAdapter.onCartEmptyListner {

   private Toolbar toolbar;
   public static ProgressDialog mProgressDialog = null;
   private RecyclerView rvCartItems;
   private static TextView totalPriceCheckout;
   private static ArrayList<StoreBookDetailResponse.Data> bundleData = new ArrayList<StoreBookDetailResponse.Data>();
   private CartListItemAdapter cartListItemAdapter;
   private Handler mHandler = null;
   private CartListItemResponse cartListItemResponse;
   private FrameLayout flEmptyView;
   private Button btnContinue,btnContinue1,btnCheckout;
   private CartListItemAdapter.onCartEmptyListner onCartEmptyListnerRef;
   private ShippingAdressModel shippingAdressModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }

        onCartEmptyListnerRef = (CartListItemAdapter.onCartEmptyListner)this;
        setContentView(R.layout.activity_add_cart);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_drawer);

        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvCartItems = (RecyclerView)findViewById(R.id.shoppingCartList);
        totalPriceCheckout = (TextView)findViewById(R.id.totalPriceCheckout);
        flEmptyView = (FrameLayout)findViewById(R.id.fl_emptyview);
        btnContinue = (Button)findViewById(R.id.btn_contine);
        btnContinue1 = (Button)findViewById(R.id.btn_continue_shopping);
        btnCheckout = (Button)findViewById(R.id.btn_checkout);
        Bundle bundle = getIntent().getExtras();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("whichTab",2);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        btnContinue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnContinue.performClick();
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressDialog();
                AddressResponse shippingAddressRequest = new AddressResponse(mHandler,Config.getStringValue(Config.USER_ID));
                shippingAddressRequest.getAddressListRequest();
            }
        });

//        try {
//            bundleData = getIntent().getParcelableArrayListExtra("dataList");
//            bundleData = new ArrayList<>();
//            bundleData.add(browsableCourse);
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }


//        if (null != bundle) {
//            if (bundle.containsKey("couserHash"))
//                couserHash = bundle.getString("couserHash");
//
//            if (bundle.containsKey(BrowsableCourse.ID_KEY))
//                courseId = bundle.getString(BrowsableCourse.ID_KEY);
//
//            if (bundle.containsKey(BrowsableCourse.PICTURE_KEY)) {
//                coursePictureName = bundle.getString(BrowsableCourse.PICTURE_KEY);
////                coursePictureUrl = baseCoursePictureUrl + coursePictureName;
////                mCoursePictureImg.setImageUrl(coursePictureUrl, mImageLoader);
//            }
//
//            if (bundle.containsKey(Flinnt.STANDARD_ID))
//                standardId = bundle.getString(Flinnt.STANDARD_ID);
//
//            if (bundle.containsKey(BrowsableCourse.NAME_KEY))
//                courseName = bundle.getString(BrowsableCourse.NAME_KEY);
//            mCourseNameTxt.setText(courseName);
//
//            if (bundle.containsKey(BrowsableCourse.INSTITUTE_NAME_KEY))
//                mInstituteNameTxt.setText(bundle.getString(BrowsableCourse.INSTITUTE_NAME_KEY));
//            if (bundle.containsKey(BrowsableCourse.RATINGS_KEY))
//                mCourseRatingBar.setRating(Float.parseFloat(bundle.getString(BrowsableCourse.RATINGS_KEY)));
//            if (bundle.containsKey(JoinCourseResponse.JOINED_KEY))
//                addedAndRemovedCourses = (HashMap<Course, Boolean>) bundle.getSerializable(JoinCourseResponse.JOINED_KEY);
//
//            if (bundle.containsKey(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE))
//                isFromNotification = bundle.getInt(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE);
//
//            if (isFromNotification > Flinnt.FALSE && bundle.containsKey(Config.USER_ID))
//                userId = bundle.getString(Config.USER_ID);
//
//            if (null != userId && !userId.equals(Config.getStringValue(Config.USER_ID))) {
//                Helper.setCurrentUserConfig(userId);
//            }



        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());


                        if(msg.obj instanceof CartListItemResponse){

                            cartListItemResponse = (CartListItemResponse)msg.obj;

                            if(cartListItemResponse.getData().getCartItem().size() > 0) {

                                flEmptyView.setVisibility(View.GONE);
                                rvCartItems.setVisibility(View.VISIBLE);
                                findViewById(R.id.ll_total).setVisibility(View.VISIBLE);
                                findViewById(R.id.LL_cart_option_btn).setVisibility(View.VISIBLE);

                                cartListItemAdapter = new CartListItemAdapter(ShoppingCartActivity.this,cartListItemResponse.getData().getCartItem(),onCartEmptyListnerRef);
                                rvCartItems.setLayoutManager(new LinearLayoutManager(ShoppingCartActivity.this));
                                rvCartItems.setAdapter(cartListItemAdapter);

                                int totalPriceCount = 0;

                                for (int count = 0; count < cartListItemResponse.getData().getCartItem().size(); count++) {
                                    totalPriceCount += cartListItemResponse.getData().getCartItem().get(count).getPrice() * Integer.parseInt(cartListItemResponse.getData().getCartItem().get(count).getQty());
                                }
                                totalPriceCheckout.setText(String.valueOf(getString(R.string.currency) + totalPriceCount));

                            }else{
                                flEmptyView.setVisibility(View.VISIBLE);
                                rvCartItems.setVisibility(View.GONE);
                                findViewById(R.id.ll_total).setVisibility(View.GONE);
                                findViewById(R.id.LL_cart_option_btn).setVisibility(View.GONE);
                            }

                        }else if(msg.arg1 == 200){
                            stopProgressDialog();

                            flEmptyView.setVisibility(View.VISIBLE);
                            rvCartItems.setVisibility(View.GONE);
                            findViewById(R.id.ll_total).setVisibility(View.GONE);
                            findViewById(R.id.LL_cart_option_btn).setVisibility(View.GONE);

                        }else if(msg.obj instanceof ShippingAdressModel){
                            stopProgressDialog();
                            try {
                                shippingAdressModel = (ShippingAdressModel) msg.obj;
                                if(shippingAdressModel != null){

                                    if(shippingAdressModel.getData() != null){
                                        if(shippingAdressModel.getData().size() > 0){
                                            Intent intentAddressList = new Intent(ShoppingCartActivity.this,ShippingAdreessListActivity.class);
                                            startActivityForResult(intentAddressList,107);
                                        }else{
                                            Intent intentAddressList = new Intent(ShoppingCartActivity.this,ShippingAddressActivity.class);
                                            startActivityForResult(intentAddressList,107);
                                        }
                                    }else{
                                        Intent intentAddressList = new Intent(ShoppingCartActivity.this,ShippingAddressActivity.class);
                                        startActivityForResult(intentAddressList,107);
                                    }
                                }

                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        flEmptyView.setVisibility(View.VISIBLE);
                        rvCartItems.setVisibility(View.GONE);
                        findViewById(R.id.ll_total).setVisibility(View.GONE);
                        findViewById(R.id.LL_cart_option_btn).setVisibility(View.GONE);
                        break;

                    default:
                        super.handleMessage(msg);
                }
            }
        };

        startProgressDialog();
        new CartItems(mHandler,Config.getStringValue(Config.USER_ID)).sendListCartItemsRequest();

    }

    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(ShoppingCartActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(this))
                mProgressDialog.show();
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); //onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onQuntyChange(int price){
        totalPriceCheckout.setText(String.valueOf(getString(R.string.currency)+" "+String.valueOf(price)));
    }

    @Override
    public void onCartEmpty() {
        flEmptyView.setVisibility(View.VISIBLE);
        rvCartItems.setVisibility(View.GONE);
        findViewById(R.id.ll_total).setVisibility(View.GONE);
        findViewById(R.id.LL_cart_option_btn).setVisibility(View.GONE);
    }
}
