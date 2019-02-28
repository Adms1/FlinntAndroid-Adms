package com.edu.flinnt.gui.store;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.CartListItemAdapter;
import com.edu.flinnt.adapter.store.Checkout_CartListItemAdapter;
import com.edu.flinnt.ccavenue.ServiceUtility;
import com.edu.flinnt.ccavenue.WebViewActivity;
import com.edu.flinnt.core.CheckoutStatusUpdate;
import com.edu.flinnt.core.CouponList;
import com.edu.flinnt.core.FreeCheckout;
import com.edu.flinnt.core.GeneratePublicKey;
import com.edu.flinnt.core.RedeemCoupon;
import com.edu.flinnt.core.RemoveCoupon;
import com.edu.flinnt.core.store.CartItems;
import com.edu.flinnt.core.store.CartListItemResponse;
import com.edu.flinnt.gui.BrowseCourseDetailActivity;
import com.edu.flinnt.gui.CoupanAdapter;
import com.edu.flinnt.gui.CourseDetailsActivity;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.models.store.ShippingAdressModel;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.CheckoutResponse;
import com.edu.flinnt.protocol.CouponListResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.FreeCheckoutResponse;
import com.edu.flinnt.protocol.GeneratePublicKeyResponse;
import com.edu.flinnt.protocol.JoinCourseResponse;
import com.edu.flinnt.protocol.RedeemCouponResponse;
import com.edu.flinnt.protocol.RemoveCouponResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.edu.flinnt.gui.BrowseCourseDetailActivity.CHECKOUT_RESPONSE;
import static com.edu.flinnt.protocol.BrowsableCourse.PRICE_KEY;

/**
 * This Activity is for display baseAmount, Discount and final Amount for the check out, here also user can redeem promocode.
 */

//https://developer.android.com/reference/android/app/DialogFragment
//@Nikhil 2962018
public class CheckoutActivityNew extends AppCompatActivity implements View.OnClickListener,Checkout_CartListItemAdapter.onCartEmptyListner  {
    public static ArrayList<Boolean> selectionList = new ArrayList<>();
    private String courseID;
    private int transectionID;
    private TextView courseNameTxt, subTotalTxt, discountTxt, totalTxt, continueTxt, applyHereTxt, totalAmountTxt;
    private ImageView backImg, deletePromoImg;
    private LinearLayout deletePromoLinear;
    BrowsableCourse.Price price;
    private ProgressDialog mProgressDialog;
    Handler mHandler = null;
    private CouponListResponse mCouponListResponse;
    private GeneratePublicKeyResponse mGeneratePublicKeyResponse;
    private RecyclerView coupanRecycle;
    private EditText enterPromoEdit;
    private ArrayList<CouponListResponse.Coupon> couponList;
    private Dialog dialog;
    private TextView invalidMsgTxt;
    private CheckoutResponse mCheckoutResponse;
    private Course joinedCourse;
    private HashMap<Course, Boolean> addedAndRemovedCourses = new HashMap<>();  // true if added course, false if removed course
    private String currentCouponStr = "";
    private String totalAmount = "";
    BottomSheetDialog mdialog;
    private CartListItemResponse cartListItemResponse;
    private Checkout_CartListItemAdapter checkout_cartListItemAdapter;
    private RecyclerView rvCartItems;
    private Checkout_CartListItemAdapter.onCartEmptyListner onCartEmptyListnerRef;
    private ArrayList<ShippingAdressModel.Datum> bundleData;
    private TextView tv_addressLine,tvName,tv_total_itemsTxt;
    private Button btnChangeAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_checkout_new);

        findAndInitializeViews();
        onCartEmptyListnerRef = (Checkout_CartListItemAdapter.onCartEmptyListner)this;
        try {
            Bundle bundle = getIntent().getExtras();
            if (null != bundle) {
                if (bundle.containsKey(BrowsableCourse.ID_KEY))
                    courseID = bundle.getString(BrowsableCourse.ID_KEY);
                if (bundle.containsKey(BrowsableCourse.NAME_KEY))
                    courseNameTxt.setText(bundle.getString(BrowsableCourse.NAME_KEY));

                if (bundle.containsKey(CHECKOUT_RESPONSE))
                    mCheckoutResponse = (CheckoutResponse) getIntent().getSerializableExtra(CHECKOUT_RESPONSE);
                transectionID = mCheckoutResponse.getData().getTransactionId();
                LogWriter.write("Transaction id : " + transectionID);

                if (bundle.containsKey(PRICE_KEY))
                    price = (BrowsableCourse.Price) bundle.getSerializable(PRICE_KEY);
                LogWriter.write("Base Price : " + price.getBuy());
            }
            updateData("0", "" + price.getBuy(), "" + mCheckoutResponse.getData().getPayload().getAmount());
        }catch (Exception ex){
            ex.printStackTrace();
        }


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                          //  LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());

                            if(msg.obj instanceof CartListItemResponse) {

                                cartListItemResponse = (CartListItemResponse) msg.obj;

                                if (cartListItemResponse.getData().getCartItem().size() > 0) {


                                    rvCartItems.setVisibility(View.VISIBLE);

                                    checkout_cartListItemAdapter = new Checkout_CartListItemAdapter(CheckoutActivityNew.this, cartListItemResponse.getData().getCartItem(), onCartEmptyListnerRef);
                                    rvCartItems.setLayoutManager(new LinearLayoutManager(CheckoutActivityNew.this));
                                    rvCartItems.setAdapter(checkout_cartListItemAdapter);

                                    int totalPriceCount = 0;

                                    for (int count = 0; count < cartListItemResponse.getData().getCartItem().size(); count++) {
                                        totalPriceCount += cartListItemResponse.getData().getCartItem().get(count).getPrice() * Integer.parseInt(cartListItemResponse.getData().getCartItem().get(count).getQty());
                                    }
                                    subTotalTxt.setText(String.valueOf(getString(R.string.currency)+totalPriceCount));
                                    totalTxt.setText(String.valueOf(getString(R.string.currency)+totalPriceCount));
                                    discountTxt.setText(String.valueOf(getString(R.string.currency) + "0"));
                                    NumberFormat formatter = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                                        String moneyString = formatter.format(totalPriceCount);
                                        System.out.println(moneyString);
                                        totalAmountTxt.setText(String.valueOf(moneyString.replace("Rs.",getString(R.string.currency))));
                                    }else{
                                        totalAmountTxt.setText(String.valueOf(getString(R.string.currency)+getIndianCurrencyFormat(String.valueOf(totalPriceCount))));
                                    }
                                    tv_total_itemsTxt.setText(""+cartListItemResponse.getData().getCartItem().size()+" "+"Items");

                                } else {
                                    rvCartItems.setVisibility(View.GONE);

                                }
                            }


//                        if (msg.obj instanceof CouponListResponse) {
//                            mCouponListResponse = (CouponListResponse) msg.obj;
//                            couponList = new ArrayList<>();
//                            couponList = mCouponListResponse.getData().getCoupons();
//                        }
//                        if (msg.obj instanceof RedeemCouponResponse) {
//                            RedeemCouponResponse mRedeemCouponResponse = (RedeemCouponResponse) msg.obj;
//                            updateData(mRedeemCouponResponse.getData().getDiscountAmount(), "" + price.getBuy(), mRedeemCouponResponse.getData().getNetAmount());
//                            deletePromoLinear.setVisibility(View.VISIBLE);
//                            currentCouponStr = enterPromoEdit.getText().toString();
//                            dismissDialog();
//                        }
//                        if (msg.obj instanceof RemoveCouponResponse) {
//                            RemoveCouponResponse mRemoveCouponResponse = (RemoveCouponResponse) msg.obj;
//                            updateData(mRemoveCouponResponse.getData().getDiscountAmount(), "" + price.getBuy(), mRemoveCouponResponse.getData().getNetAmount());
//                            currentCouponStr = "";
//                            selectionList.clear();
//                        }
//                        if (msg.obj instanceof FreeCheckoutResponse) {
//                            FreeCheckoutResponse mFreeCheckoutResponse = (FreeCheckoutResponse) msg.obj;
//
//                            continueTxt.setEnabled(true);
//                            if (mFreeCheckoutResponse.getData().getJoined() == Flinnt.TRUE) {
//                                joinedCourse = mFreeCheckoutResponse.getData().getCourse();
//                                // flag sets if user has un-subscribed a course and joined the same course
//                                boolean isCourseRemovedAndAddedInCurrentSession = false;
//                                for (Course course : addedAndRemovedCourses.keySet()) {
//                                    if (course.getCourseID().equals(joinedCourse.getCourseID())) {
//                                        addedAndRemovedCourses.remove(course);
//                                        isCourseRemovedAndAddedInCurrentSession = true;
//                                        break;
//                                    }
//                                }
//                                if (!isCourseRemovedAndAddedInCurrentSession) {
//                                    addedAndRemovedCourses.put(joinedCourse, true);
//                                    Helper.showToast(getResources().getString(R.string.checkout_successfully_joined) + "'" + joinedCourse.getCourseName() + "'", Toast.LENGTH_LONG);
//                                }
////                                onBackPressed();
//                                Intent intent = new Intent(getApplicationContext(),CourseDetailsActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.putExtra(Course.COURSE_ID_KEY, courseID);
//                                intent.putExtra(Course.COURSE_NAME_KEY, courseNameTxt.getText().toString());
//                                intent.putExtra(MyCoursesActivity.IS_JOIN, true);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
                        if (msg.obj instanceof GeneratePublicKeyResponse) {
                            mGeneratePublicKeyResponse = (GeneratePublicKeyResponse)msg.obj;
                            callCCAvenue();
                        }
                        break;
                    case Flinnt.FAILURE:
                        try {
                            if (msg.obj instanceof JoinCourseResponse) {
                                JoinCourseResponse mFreeCheckoutResponse = (JoinCourseResponse) msg.obj;
                                Helper.showAlertMessage(CheckoutActivityNew.this, getResources().getString(R.string.checkout), mFreeCheckoutResponse.errorResponse.getMessage(), "Close");
                            }
                            if (msg.obj instanceof RedeemCouponResponse) {
                                RedeemCouponResponse mRedeemCouponResponse = (RedeemCouponResponse) msg.obj;
//                                Helper.showAlertMessage(CheckoutActivity.this, "Checkout", mRedeemCouponResponse.errorResponse.getMessage(), "Close");
                                invalidMsgTxt.setVisibility(View.VISIBLE);
                                invalidMsgTxt.setText(mRedeemCouponResponse.errorResponse.getMessage());
                                invalidMsgTxt.invalidate();
                            }

                            if (msg.obj instanceof FreeCheckoutResponse) {
                                FreeCheckoutResponse mFreeCheckoutResponse = (FreeCheckoutResponse) msg.obj;
                                Helper.showAlertMessage(CheckoutActivityNew.this, getResources().getString(R.string.checkout), mFreeCheckoutResponse.errorResponse.getMessage(), "Close");
                            }

                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + msg.obj.toString());
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        try {
            bundleData  = new ArrayList<ShippingAdressModel.Datum>();
            bundleData = getIntent().getParcelableArrayListExtra("address_data");

            if(bundleData != null){
                if(bundleData.size() > 0){
                    tv_addressLine.setText(bundleData.get(0).getAddress1()+","+bundleData.get(0).getAddress2()+"\n"+bundleData.get(0).getCity()+"\n"+bundleData.get(0).getName()+"-"+bundleData.get(0).getPin());
                    tvName.setText(bundleData.get(0).getFullname());
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }


        startProgressDialog();
        new CartItems(mHandler,Config.getStringValue(Config.USER_ID)).sendListCartItemsRequest();

        //new CouponList(mHandler,transectionID).sendCouponListRequest();
    }


    private void dismissDialog() {

        try {

            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception d) {
        }
        try {
            if (mdialog != null) {
                mdialog.dismiss();
            }
        } catch (Exception d) {
        }
    }

    /**
     * Initialize UI views and their visibility
     */
    private void findAndInitializeViews() {
        courseNameTxt = (TextView) findViewById(R.id.courseNameTxt);
        subTotalTxt = (TextView) findViewById(R.id.subTotalTxt);

        discountTxt = (TextView) findViewById(R.id.discountTxt);
        totalTxt = (TextView) findViewById(R.id.totalTxt);

        continueTxt = (TextView) findViewById(R.id.continueTxt);
        continueTxt.setOnClickListener(this);

        applyHereTxt = (TextView) findViewById(R.id.applyHereTxt);
        applyHereTxt.setOnClickListener(this);

        totalAmountTxt = (TextView) findViewById(R.id.totalAmountTxt);

        backImg = (ImageView) findViewById(R.id.backImg);
        backImg.setOnClickListener(this);

        deletePromoImg = (ImageView) findViewById(R.id.deletePromoImg);
        deletePromoImg.setOnClickListener(this);

        deletePromoLinear = (LinearLayout) findViewById(R.id.deletePromoLinear);

        rvCartItems = (RecyclerView)findViewById(R.id.rv_cartitem_list);
        tv_addressLine= (TextView)findViewById(R.id.tv_address);
        tvName = (TextView)findViewById(R.id.tv_name);
        tv_total_itemsTxt = (TextView)findViewById(R.id.total_itemsTxt);

        btnChangeAddress = (Button)findViewById(R.id.btn_change_address);


        btnChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("user_address_id",String.valueOf(bundleData.get(0).getUserAddressId()));
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });


    }

    private void updateData(String discountAmount, String subTital, String total) {
        discountTxt.setText(getResources().getString(R.string.currency) + discountAmount);
        subTotalTxt.setText(getResources().getString(R.string.currency) + subTital);
        totalTxt.setText(getResources().getString(R.string.currency) + total);
        totalAmountTxt.setText(getResources().getString(R.string.currency) + total);
        totalAmount = "" + total;
    }

    @Override
    public void onCartEmpty() {
        finish();
    }

    @SuppressLint("ValidFragment")
    public class CoupanCodeFDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            dialog = new Dialog(getActivity());

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.apply_promocode);

            enterPromoEdit = (EditText) dialog.findViewById(R.id.enterPromoEdit);
            if (!currentCouponStr.equalsIgnoreCase("")) {
                enterPromoEdit.setText(currentCouponStr);
                enterPromoEdit.setEnabled(false);
            } else {
                selectionList.clear();
            }

            enterPromoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        if (enterPromoEdit.getText().toString().equalsIgnoreCase("")) {
                            Helper.showAlertMessage(CheckoutActivityNew.this,getResources().getString(R.string.couponcode_blank_title_msg),getResources().getString(R.string.couponcode_blank_msg), "Close");
                        } else {
                            invalidMsgTxt.setVisibility(View.GONE);
                            startProgressDialog();
                            new RedeemCoupon(mHandler, transectionID, enterPromoEdit.getText().toString()).redeemCouponRequest();
                        }
                        dismissDialog();
                    }
                    return false;
                }
            });

            invalidMsgTxt = (TextView) dialog.findViewById(R.id.invalidMsgTxt);

            coupanRecycle = (RecyclerView) dialog.findViewById(R.id.coupanRecycle);

            coupanRecycle.setLayoutManager(new LinearLayoutManager(CheckoutActivityNew.this));
            LogWriter.warn("Coupon list : " + couponList.size());
            Display display = getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            if (couponList.size() == 0 || couponList == null) {
                coupanRecycle.setVisibility(View.GONE);
            } else if (couponList.size() > 2) {
                ViewGroup.LayoutParams params = coupanRecycle.getLayoutParams();
                params.height = (int) getResources().getDimension(R.dimen.coupon_dialog_height);
                params.width = width - (int) getResources().getDimension(R.dimen.coupon_width_minus);
                coupanRecycle.setLayoutParams(params);
            }

            final CoupanAdapter mCoupanAdapter = new CoupanAdapter(CheckoutActivityNew.this, couponList);
            mCoupanAdapter.setOnItemClickListener(new CoupanAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    invalidMsgTxt.setVisibility(View.GONE);
                    if (!selectionList.get(position)) {
                        enterPromoEdit.setText(couponList.get(position).getCouponCode());
                        enterPromoEdit.setEnabled(false);
                    } else {
                        enterPromoEdit.setText("");
                        enterPromoEdit.setEnabled(true);
                    }
                    enterPromoEdit.invalidate();
                    for (int i = 0; i < selectionList.size(); i++) {
                        if (i == position) {
                            if (!selectionList.get(position)) {
                                selectionList.set(i, true);
                            } else {
                                selectionList.set(i, false);
                            }
                        } else {
                            selectionList.set(i, false);
                        }
                    }
                    mCoupanAdapter.notifyDataSetChanged();
                }
            });
            coupanRecycle.setAdapter(mCoupanAdapter);

            Button redeemBtn = (Button) dialog.findViewById(R.id.redeemBtn);
            Button cancelBtn = (Button) dialog.findViewById(R.id.cencelBtn);
            redeemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (enterPromoEdit.getText().toString().equalsIgnoreCase("")) {
                        Helper.showAlertMessage(CheckoutActivityNew.this, getResources().getString(R.string.couponcode_blank_title_msg), getResources().getString(R.string.couponcode_blank_msg), "Close");
                    } else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterPromoEdit.getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        invalidMsgTxt.setVisibility(View.GONE);
                        startProgressDialog();
                        new RedeemCoupon(mHandler, transectionID, enterPromoEdit.getText().toString()).redeemCouponRequest();
                    }

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(enterPromoEdit.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            });
            return dialog;
        }

    }

    /*Passing billing information and require parameter and calling payment page of CCAvenue*/
    private void callCCAvenue() {
        //Mandatory parameters. Other parameters can be added if required.
        String vAccessCode = ServiceUtility.chkNull(mCheckoutResponse.getData().getPayload().getAccessCode()).toString().trim(); // for live environment

        String vMerchantId = ServiceUtility.chkNull(mCheckoutResponse.getData().getPayload().getAccessCode()).toString().trim();
        String vCurrency = ServiceUtility.chkNull("INR").toString().trim();
        String vAmount = ServiceUtility.chkNull("1").toString().trim();
        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {
            Intent intent = new Intent(CheckoutActivityNew.this, WebViewActivity.class);
            intent.putExtra(CHECKOUT_RESPONSE, mCheckoutResponse);
            intent.putExtra(BrowsableCourse.ID_KEY, courseID);
            intent.putExtra(BrowsableCourse.NAME_KEY, courseNameTxt.getText().toString());
            intent.putExtra(BrowsableCourse.PRICE_KEY, totalAmount);
            intent.putExtra("PublicKey", mGeneratePublicKeyResponse.getData().getPublicKey());
            startActivityForResult(intent, BrowseCourseDetailActivity.CHECKOUT_CALLBACK);
            finish();
        } else {
            Toast.makeText(this, "Toast: " + getResources().getString(R.string.checkout_validation_msg), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.CHECKOUT + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + courseID);
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(this))
                mProgressDialog.show();
        }
    }

    /**
     * Stops the circular progress dialog
     */
    private void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        } finally {
            mProgressDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == continueTxt) {
            double totalAmount = Double.parseDouble(totalAmountTxt.getText().toString().replaceAll(getResources().getString(R.string.currency), ""));
            LogWriter.write("Total Amount : " + totalAmount);
            if (totalAmount > 0) {
                startProgressDialog();
                new GeneratePublicKey(mHandler,transectionID).sendGeneratePublicKey();
                new CheckoutStatusUpdate(mHandler,transectionID,Flinnt.CHECKOUT_TRANSECTION_STATUS).sendCheckoutStatusUpdateRequest();
//                callCCAvenue();
            } else {
                startProgressDialog();
                new FreeCheckout(mHandler, transectionID).sendFreeCheckoutRequest();
            }
        }

        if (v == applyHereTxt) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View view = getLayoutInflater().inflate(R.layout.apply_promocode, null);
                mdialog = new BottomSheetDialog(CheckoutActivityNew.this);
                mdialog.setContentView(view);


                enterPromoEdit = (EditText) view.findViewById(R.id.enterPromoEdit);
                if (!currentCouponStr.equalsIgnoreCase("")) {
                    enterPromoEdit.setText(currentCouponStr);
                    enterPromoEdit.setEnabled(false);
                } else {
                    selectionList.clear();
                }

                enterPromoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            if (enterPromoEdit.getText().toString().equalsIgnoreCase("")) {
                                Helper.showAlertMessage(CheckoutActivityNew.this, getResources().getString(R.string.couponcode_blank_title_msg), getResources().getString(R.string.couponcode_blank_msg), "Close");
                            } else {
                                invalidMsgTxt.setVisibility(View.GONE);
                                startProgressDialog();
                                new RedeemCoupon(mHandler,transectionID, enterPromoEdit.getText().toString()).redeemCouponRequest();
                            }
                            dismissDialog();
                        }
                        return false;
                    }
                });

                invalidMsgTxt = (TextView) view.findViewById(R.id.invalidMsgTxt);

                coupanRecycle = (RecyclerView) view.findViewById(R.id.coupanRecycle);

                coupanRecycle.setLayoutManager(new LinearLayoutManager(CheckoutActivityNew.this));
                LogWriter.warn("Coupon list : " + couponList.size());
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                if (couponList.size() == 0 || couponList == null) {
                    coupanRecycle.setVisibility(View.GONE);
                } else if (couponList.size() > 2) {
                    ViewGroup.LayoutParams params = coupanRecycle.getLayoutParams();
                    params.height = (int) getResources().getDimension(R.dimen.coupon_dialog_height);
                    params.width = width - (int) getResources().getDimension(R.dimen.coupon_width_minus);
                    coupanRecycle.setLayoutParams(params);
                }

                final CoupanAdapter mCoupanAdapter = new CoupanAdapter(CheckoutActivityNew.this, couponList);
                mCoupanAdapter.setOnItemClickListener(new CoupanAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        invalidMsgTxt.setVisibility(View.GONE);
                        if (!selectionList.get(position)) {
                            enterPromoEdit.setText(couponList.get(position).getCouponCode());
                            enterPromoEdit.setEnabled(false);
                        } else {
                            enterPromoEdit.setText("");
                            enterPromoEdit.setEnabled(true);
                        }
                        enterPromoEdit.invalidate();
                        for (int i = 0; i < selectionList.size(); i++) {
                            if (i == position) {
                                if (!selectionList.get(position)) {
                                    selectionList.set(i, true);
                                } else {
                                    selectionList.set(i, false);
                                }
                            } else {
                                selectionList.set(i, false);
                            }
                        }
                        mCoupanAdapter.notifyDataSetChanged();
                    }
                });
                coupanRecycle.setAdapter(mCoupanAdapter);

                Button redeemBtn = (Button) view.findViewById(R.id.redeemBtn);
                Button cancelBtn = (Button) view.findViewById(R.id.cencelBtn);
                redeemBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (enterPromoEdit.getText().toString().equalsIgnoreCase("")) {
                            Helper.showAlertMessage(CheckoutActivityNew.this, getResources().getString(R.string.couponcode_blank_title_msg), getResources().getString(R.string.couponcode_blank_msg), "Close");
                        } else {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(enterPromoEdit.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            invalidMsgTxt.setVisibility(View.GONE);
                            startProgressDialog();
                            new RedeemCoupon(mHandler, transectionID, enterPromoEdit.getText().toString()).redeemCouponRequest();
                        }

                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.cancel();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(enterPromoEdit.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                });

                mdialog.show();

            } else {

                FragmentManager fm = getFragmentManager();
                CoupanCodeFDialog dialogFragment = new CoupanCodeFDialog();
                dialogFragment.show(getFragmentManager(), "Sample Fragment");
            }
        }

        if (v == backImg) {
            onBackPressed();
        }

        if (v == deletePromoImg) {
            deletePromoLinear.setVisibility(View.GONE);
            startProgressDialog();
            new RemoveCoupon(mHandler, transectionID).sendRemoveCouponRequest();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case BrowseCourseDetailActivity.CHECKOUT_CALLBACK:
                        finish();
                        break;

                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public void onBackPressed() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            //NavUtils.navigateUpTo(this, upIntent);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(JoinCourseResponse.JOINED_KEY, addedAndRemovedCourses);
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("addedAndRemovedCourses : " + addedAndRemovedCourses.toString());
//            }
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    public String getIndianCurrencyFormat(String amount) {
        StringBuilder stringBuilder = new StringBuilder();
        char amountArray[] = amount.toCharArray();
        int a = 0, b = 0;
        for (int i = amountArray.length - 1; i >= 0; i--) {
            if (a < 3) {
                stringBuilder.append(amountArray[i]);
                a++;
            } else if (b < 2) {
                if (b == 0) {
                    stringBuilder.append(",");
                    stringBuilder.append(amountArray[i]);
                    b++;
                } else {
                    stringBuilder.append(amountArray[i]);
                    b = 0;
                }
            }
        }
        return stringBuilder.reverse().toString();
    }
}