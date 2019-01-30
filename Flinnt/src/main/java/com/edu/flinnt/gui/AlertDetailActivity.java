package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AlertList;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.protocol.AlertListResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;

@SuppressLint("ResourceAsColor")
/**
 * GUI class to show alert details
 */
public class AlertDetailActivity extends AppCompatActivity {

	private String canEdit = "0";
	private String alertID;
	
	AlertList mAlertList;
	TextView alertDate, alertText; 
	Handler mHandler;
	String alertTextStr = "";
	ProgressDialog mProgressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.alert_detail_activity);

		Intent intent = getIntent();

		alertID = intent.getStringExtra(Alert.ALERT_ID_KEY);
		
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");
		
		alertDate = (TextView) findViewById(R.id.alert_date_alertdetails);
		alertText = (TextView) findViewById(R.id.alert_text_alertdetails);
		
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				stopProgressDialog();
				switch (message.what) {
					case Flinnt.SUCCESS:
						if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
						if( message.obj instanceof AlertListResponse) {
							updateAlertDetail( (AlertListResponse) message.obj );
						}
					break;

					case Flinnt.FAILURE:
						if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
						if( null != ((AlertListResponse) message.obj).errorResponse ) {
							showAlertMessage(AlertDetailActivity.this, "Error", ((AlertListResponse) message.obj).errorResponse.getMessage(), "CLOSE");
						}
					break;
					default:
						super.handleMessage(message);
				}
			}
		};
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "activity="+Flinnt.ALERT_DETAIL+"&user="+ Config.getStringValue(Config.USER_ID));
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

    /**
     * update and display alert list
     * @param alertListResponse alert list response
     */
	public void updateAlertDetail(AlertListResponse alertListResponse) {
		
		if( null != alertListResponse ) {
			alertTextStr = alertListResponse.getAlertDetail().getAlertText();

			if ((Long.parseLong(alertListResponse.getAlertDetail().getAlertDate())) > (System.currentTimeMillis() / 1000)) {
				alertDate.setText("not yet");
			} else {
				alertDate.setText(Helper.formateTimeMillis(Long.parseLong(alertListResponse.getAlertDetail().getAlertDate())));
			}
			
			alertText.setText( alertTextStr );
			canEdit = alertListResponse.getAlertDetail().getCanEdit();
			if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write( "AlertUser : " + alertListResponse.getAlertDetail().getAlertUser());
			if(getSupportActionBar() != null) getSupportActionBar().setTitle(alertListResponse.getAlertDetail().getAlertUser());
			
			String authourPicUrl = alertListResponse.getUserPictureUrl() + Flinnt.PROFILE_MEDIUM  + File.separator + alertListResponse.getAlertDetail().getUserPicture();
			Requester.getInstance().getImageLoader().get(authourPicUrl, new ImageLoader.ImageListener() {
		         @Override
		         public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

		        	 Bitmap responseBitmap = response.getBitmap();
		        	 if(null != responseBitmap){
		        		 responseBitmap = Bitmap.createScaledBitmap(responseBitmap, Helper.getDip(40), Helper.getDip(40), true);
		        		 Bitmap roundBitmap = Helper.getRoundedCornerBitmap(responseBitmap, Color.TRANSPARENT, 30, 0, FlinntApplication.getContext());
		        		 BitmapDrawable authorImage = new BitmapDrawable(getResources(),roundBitmap);
						 if(getSupportActionBar() != null) getSupportActionBar().setIcon(authorImage);
		        	 }
		         }

		         @Override
		         public void onErrorResponse(VolleyError error) {
		          
		         }
		     });
			
			invalidateOptionsMenu();
		}
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if( null == mAlertList ) {
			mAlertList = new AlertList(mHandler, AlertList.REQUEST_TYPE_ALERT_DETAIL);
		}
		mAlertList.setAlertID(alertID);
		mAlertList.sendAlertListRequest();
		startProgressDialog();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alerts_list_menu, menu);
		MenuItem editPost = menu.findItem(R.id.action_edit);
		
		if(canEdit.equals(Flinnt.ENABLED)) {
			editPost.setVisible(true);
		} else {
			editPost.setVisible(false);
		}
		return true;
	}

	/**
	 * Builds and display dialog
	 *
	 * @param context       activity context
	 * @param title         dialog resorceTitleTxt
	 * @param message       dialog message
	 * @param dialogBtnText dailog operation button text
	 */
	public void showAlertMessage(final Context context, String title, String message, String dialogBtnText) {

		if (context == null) {
			return;
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set resorceTitleTxt
		//alertDialogBuilder.setTitle(resorceTitleTxt);
		//alertDialogBuilder.setTitle("Change Password");
		TextView titleText = new TextView(context);
		// You Can Customise your Title here
		titleText.setText(title);
		titleText.setPadding(40, 40, 40, 0);
		titleText.setGravity(Gravity.CENTER_VERTICAL);
		titleText.setTextColor(context.getResources().getColor(R.color.ColorPrimary));
		titleText.setTextSize(20);
		titleText.setTypeface(Typeface.DEFAULT_BOLD);
		alertDialogBuilder.setCustomTitle(titleText);

		// set dialog message
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton(dialogBtnText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				onBackPressed();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it

		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.ColorPrimary));

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish(); // onBackPressed();
				break;
			case R.id.action_edit:
				if (!Helper.isConnected()) {
					Helper.showNetworkAlertMessage(this);
				} else {

					Intent alertEdit = new Intent(AlertDetailActivity.this, AddAlertActivity.class);
					alertEdit.putExtra(Flinnt.POST_STATS_ACTION, Flinnt.POST_ALERT_EDIT);
					alertEdit.putExtra(Alert.ALERT_ID_KEY, alertID);
					alertEdit.putExtra(Alert.ALERT_TEXT_KEY, alertTextStr);
					startActivity(alertEdit);

				}
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(AlertDetailActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AlertDetailActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
}