package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.SelectTemplate;
import com.edu.flinnt.protocol.SelectTemplateResponse;
import com.edu.flinnt.protocol.Template;
import com.edu.flinnt.util.DividerItemDecoration;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

@SuppressLint("NewApi")
/**
 * GUI class to show template list to pick one
 */
public class SelectTempleteActivity extends AppCompatActivity {

	Toolbar mToolbar;
	RecyclerView mRecyclerView;
	public Handler mHandler = null;
	ProgressDialog mProgressDialog = null;
	private SelectTemplateAdapter mSelectTemplateAdapter;
	//private ProgressBar progressBar;
	ArrayList<Template> mTemplateList;
	LinearLayoutManager mLinearLayoutManager;
//	SelectTemplateResponse mSelectTeplateResponse = new SelectTemplateResponse();
	
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.select_templete_activity);
		
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("Select a template");
		setSupportActionBar(mToolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_templete_list);
		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, Helper.getDip(0))); // here 0dp count from xml file
				
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				switch (message.what) {
				case Flinnt.SUCCESS:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if( message.obj instanceof SelectTemplateResponse) {
						updateTemplateList( (SelectTemplateResponse) message.obj );
					}


					break;
				case Flinnt.FAILURE:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if( message.obj instanceof SelectTemplateResponse) {
						SelectTemplateResponse response = (SelectTemplateResponse) message.obj;
						if(response.errorResponse != null){
						   Helper.showAlertMessage(SelectTempleteActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
						}
					}
					break;
				default:
					/*
					 * Pass along other messages from the UI
					 */
					super.handleMessage(message);
				}
			}
		};

		
		new SelectTemplate(mHandler).sendSelectTemplateRequest();
		startProgressDialog();
		
//		mTemplateList = mSelectTeplateResponse.getTemplateList();

		mLinearLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLinearLayoutManager);

		mTemplateList = new ArrayList<Template>();
		mSelectTemplateAdapter = new SelectTemplateAdapter(mTemplateList);
		mRecyclerView.setAdapter(mSelectTemplateAdapter);
		
		mSelectTemplateAdapter.setOnItemClickListener(new SelectTemplateAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if ( null != mSelectTemplateAdapter.getItem(position)) {
				Intent result = new Intent();
				result.putExtra(Template.POST_TEMPLATE_ID_KEY, mSelectTemplateAdapter.getItem(position).getTemplateId());
//				result.putExtra(Template.POST_CATEGORY_ID, mSelectTemplateAdapter.getItem(position).getCategoryId());
//				result.putExtra(Template.POST_TEMPLATE_CATEGORY_KEY, mSelectTemplateAdapter.getItem(position).getTemplateCategory());
				result.putExtra(Template.POST_TEMPLATE_NAME_KEY, mSelectTemplateAdapter.getItem(position).getTemplateName());
				result.putExtra(Template.POST_TEMPLATE_TITLE_KEY, mSelectTemplateAdapter.getItem(position).getTemplateTitle());
				result.putExtra(Template.POST_TEMPLATE_TAGS_KEY, mSelectTemplateAdapter.getItem(position).getTemplateTags());
				result.putExtra(Template.POST_TEMPLATE_DESCRIPTION_KEY, mSelectTemplateAdapter.getItem(position).getTemplateDescription());
				setResult(Activity.RESULT_OK, result);
				finish();
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Select Templete");
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

	private void updateTemplateList(SelectTemplateResponse mSelectTemplateResponse) {
		mSelectTemplateAdapter.addItems(mSelectTemplateResponse.getTemplateList());
	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(SelectTempleteActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(SelectTempleteActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
        }
	}

    /**
     * Stops the circular progress dialog
     */
	private void stopProgressDialog(){
		try {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
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
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}

}