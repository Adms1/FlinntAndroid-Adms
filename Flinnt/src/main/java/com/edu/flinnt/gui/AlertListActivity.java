package com.edu.flinnt.gui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.AlertListAdapter;
import com.edu.flinnt.core.AlertList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.adapter.AlertListAdapter.OnItemClickListener;
import com.edu.flinnt.adapter.AlertListAdapter.OnItemLongClickListener;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.protocol.AlertListResponse;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

@SuppressLint("ResourceAsColor")
/**
 * GUI class to show alert list
 */
public class AlertListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AlertListAdapter mAdapter;
    private AlertList mAlertList;
    private TextView mEmptyView;
    private Handler mHandler;
    private ProgressDialog mProgressDialog = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final int ALERT_DETAILS_CALLBACK = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.alert_list_activity);


        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mEmptyView = (TextView) findViewById(R.id.empty_text_no_alert);
        mRecyclerView = (RecyclerView) findViewById(R.id.alert_list_recyclerView);
        setRecyclerViewLayoutManager();

        mAdapter = new AlertListAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(AlertListActivity.this);
                } else {

                    Intent intent = new Intent(AlertListActivity.this, AlertDetailActivity.class);
                    if (null != mAdapter.getItem(position)) {
                        intent.putExtra(Alert.ALERT_ID_KEY, mAdapter.getItem(position).getAlertID());
                        startActivityForResult(intent,ALERT_DETAILS_CALLBACK);
                    }


                }
            }
        });

        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                if (null != mAdapter.getItem(position)) {
                    if (mAdapter.getItem(position).getCanDelete().equalsIgnoreCase("1")) {
                        showDeleteAlertWarning(AlertListActivity.this, mAdapter.getItem(position).getAlertID(), mAdapter);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                swipeRefreshLayout.setRefreshing(false);

                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        if (message.obj instanceof AlertListResponse) {
                            updateAlertList((AlertListResponse) message.obj);
                        }
                        break;

                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (null != ((AlertListResponse) message.obj).errorResponse) {
                            Helper.showAlertMessage(AlertListActivity.this, "Error", ((AlertListResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                        }
                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(AlertListActivity.this);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshList();
                }
            }
        });

    }

    /**
     * Unfavorite Course confirmation dialog
     *
     * @param context  activity context
     * @param alertID  Alert id
     * @param mAdapter wish mContentsAdapter
     */
    public void showDeleteAlertWarning(final Context context, final String alertID, final AlertListAdapter mAdapter) {

        final Handler dialogHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        mAdapter.remove(alertID);
                        // Delete Item from offline database
                        Helper.showToast(context.getString(R.string.successfully_deleted), Toast.LENGTH_SHORT);
                        mEmptyView.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
                        break;

                    case Flinnt.FAILURE:
                        stopProgressDialog();
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        if (((AlertListResponse) message.obj).errorResponse != null) {
                            Helper.showAlertMessage(AlertListActivity.this, "Error", ((AlertListResponse) message.obj).errorResponse.getMessage(), "CLOSE");
                        }

                        break;
                    default:
                        super.handleMessage(message);
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set mResorceTitleTxt
        TextView titleText = new TextView(context);
        // You Can Customise your Title here
        titleText.setText("Delete Alert");
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(R.string.want_to_delete);
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    mAlertList = new AlertList(dialogHandler, AlertList.REQUEST_TYPE_ALERT_REMOVE);
                    mAlertList.setAlertID(alertID);
                    mAlertList.sendAlertListRequest();
                    startProgressDialog();
                } else {
                    Helper.showNetworkAlertMessage(AlertListActivity.this);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(AlertListActivity.this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));
        }
    }

    /**
     * setup recycler view to get new data while scrolling down
     */
    protected void setRecyclerViewLayoutManager() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(AlertListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager, false) {

            @Override
            public void onLoadMore(int current_page) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("onLoadMore : HasMore " + mAlertList.getAlertListResponse().getHasMore());
                if (mAlertList.getAlertListResponse().getHasMore() > 0) {
                    mAlertList.sendAlertListRequest();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        try {
            if (resultCode == RESULT_OK) {

                switch (requestCode) {

                    case ALERT_DETAILS_CALLBACK:
                        if (!Helper.isConnected()) {
                            Helper.showNetworkAlertMessage(AlertListActivity.this);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            refreshList();
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Alert List");
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
    public void onResume() {
        super.onResume();
        if (null == mAlertList) {
            mAlertList = new AlertList(mHandler, AlertList.REQUEST_TYPE_ALERT_LIST);
            mAlertList.sendAlertListRequest();
            startProgressDialog();
        }
    }

    /**
     * Sends a request to get alert list
     */
    private void sendRequest() {
        mAlertList = new AlertList(mHandler, AlertList.REQUEST_TYPE_ALERT_LIST);
        mAlertList.sendAlertListRequest();
        setRecyclerViewLayoutManager();
    }

    private void updateAlertList(AlertListResponse alertListResponse) {
        mAdapter.addItems(alertListResponse.getAlertList());
        mEmptyView.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(AlertListActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AlertListActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    /**
     * Clear and send updated album data
     */
    private void refreshList() {
        //if(Helper.isConnected()){
        Requester.getInstance().cancelPendingRequests(AlertList.TAG);
        mAdapter.clearData();
        sendRequest();
        /*}else{
			Helper.showNetworkAlertMessage(this);
		}*/
    }
}