package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AddPost;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Alert;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity UI class to Add/Edit Alerts
 */
public class AddAlertActivity extends AppCompatActivity implements OnClickListener {

	Toolbar toolbar;
	ScrollView mScrollView;
	LinearLayout schedulePublishPostLineatLayout;
	RelativeLayout scheduleDateRelative, scheduleTimeRelative, publishRelative;

	TextView scheduleDateTxtview, scheduleTimeTxtview;
	EditText descTxtview;
	RadioGroup mRadioGroup;

	private String mPublishStats = Flinnt.PUBLISH_NOW;
	private int mScheduleYear = Flinnt.INVALID, mScheduleMonth = Flinnt.INVALID, mScheduleDay = Flinnt.INVALID,
			mScheduleHour = Flinnt.INVALID, mScheduleMinute = Flinnt.INVALID;

	private int mPostStat = Flinnt.INVALID;

	Resources res = FlinntApplication.getContext().getResources();
	final Calendar mCalendar = Calendar.getInstance();
	public Handler mHandler = null;
	ProgressDialog mProgressDialog = null;

	String alertID = "";
	String alertText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.add_alert);

		descTxtview = (EditText) findViewById(R.id.alert_description_addalert);
		descTxtview.setMaxLines(Integer.MAX_VALUE);

		Bundle bundle = getIntent().getExtras();
		if( null != bundle ) {
			mPostStat = bundle.getInt( Flinnt.POST_STATS_ACTION );
			if( bundle.containsKey(Alert.ALERT_ID_KEY )) alertID = bundle.getString(Alert.ALERT_ID_KEY);
			if( bundle.containsKey(Alert.ALERT_TEXT_KEY )) alertText = bundle.getString(Alert.ALERT_TEXT_KEY);
		}

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(mPostStat == Flinnt.POST_ALERT_EDIT){
			getSupportActionBar().setTitle("Edit Alert");
		}

		schedulePublishPostLineatLayout = (LinearLayout) findViewById(R.id.publish_linear_date_time_select_addalert);
		scheduleDateRelative = (RelativeLayout) findViewById(R.id.schedule_date_relative_addalert);
		scheduleTimeRelative = (RelativeLayout) findViewById(R.id.schedule_time_relative_addalert);
		publishRelative = (RelativeLayout) findViewById(R.id.publish_relative_main);

		mScrollView = (ScrollView) findViewById(R.id.scroll_addalert);
		scheduleDateTxtview = (TextView) findViewById(R.id.schedule_date_text_addalert);
		scheduleTimeTxtview = (TextView) findViewById(R.id.schedule_time_text_addalert);
		
		mRadioGroup = (RadioGroup) findViewById(R.id.publish_post_radiogroup_addalert);

		scheduleDateRelative.setOnClickListener(this);
		scheduleTimeRelative.setOnClickListener(this);

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				switch (message.what) {
				case Flinnt.SUCCESS:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if( message.obj instanceof AddPostResponse) {
						if( ((AddPostResponse) message.obj ).getData().getIsAdded() == Flinnt.TRUE ||
								((AddPostResponse) message.obj ).getData().getIsRepost() == Flinnt.TRUE)
						{
							Helper.showToast("Alert has been sent", Toast.LENGTH_LONG);
							Intent resultIntent = new Intent();
							resultIntent.putExtra(CourseDetailsActivity.ADDED_CONTENT_DATA,(AddPostResponse)message.obj);
							setResult(Activity.RESULT_OK, resultIntent);
							finish();
						}
						else if ( ((AddPostResponse) message.obj ).getData().getIsEdited() == Flinnt.TRUE ) {
							Helper.showToast("Alert has been updated", Toast.LENGTH_LONG);
							finish();
						}

					}

					break;
				case Flinnt.FAILURE:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					stopProgressDialog();
					if( message.obj instanceof AddPostResponse) {
						AddPostResponse response = (AddPostResponse) message.obj;
	                	if(response.errorResponse != null){
	                		Helper.showAlertMessage(AddAlertActivity.this, "Add Alert", response.errorResponse.getMessage(), "CLOSE");
	                	}
					}

					break;

				default:
					stopProgressDialog();
					super.handleMessage(message);
				}
			}
		};
		
		if(mPostStat == Flinnt.POST_ALERT_EDIT){
			publishRelative.setVisibility(View.GONE);
			descTxtview.setText(alertText);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Add Alert");
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

		case R.id.action_done:
			
			Helper.hideKeyboardFromWindow(AddAlertActivity.this);
			if ( !Helper.isConnected() ) {
				Helper.showNetworkAlertMessage(this);
			}
			else {
				if( validateAlert() ) {
					sendRequest();
				}
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * Sends request to add an alert
     */
	private void sendRequest() {

		String descStr = descTxtview.getText().toString();

		AddPostRequest addPostRequest = new AddPostRequest();
		addPostRequest.setUserID(Config.getStringValue(Config.USER_ID));
		addPostRequest.setAlertText(descStr);
		if(mPostStat == Flinnt.POST_ALERT_EDIT){
			addPostRequest.setAlertID(alertID);
		}
		

		if(mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)){
			addPostRequest.setPubYear(mScheduleYear);
			addPostRequest.setPubMonth(mScheduleMonth);
			addPostRequest.setPubDay(mScheduleDay);
			addPostRequest.setPubHour(mScheduleHour);
			addPostRequest.setPubMinute(mScheduleMinute);
		}
		new AddPost(mHandler, addPostRequest, mPostStat).sendAddPostRequest();
		startProgressDialog();
	}

    /**
     * Checks if the alert is valid to be sent
     * @return true if valid, false otherwise
     */
	private boolean validateAlert() {

		if(TextUtils.isEmpty(descTxtview.getText().toString())){
			Helper.showAlertMessage(AddAlertActivity.this, "Add Alert", "Add Description", "CLOSE");
			return false;
		}
		else if(mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)){
			if(mScheduleYear == Flinnt.INVALID){
				Helper.showAlertMessage(AddAlertActivity.this, "Add Alert", "Select Date", "CLOSE");
				return false;
			}
			else if(mScheduleHour == Flinnt.INVALID){
				Helper.showAlertMessage(AddAlertActivity.this, "Add Alert", "Select Time", "CLOSE");
				return false;
			}
			return true;
		}
		return true;
	}

    /**
     * Checks if to publish now or schedule
     * @param view publish now/schedule radio button
     */
	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {

		case R.id.publish_post_now_addalert:
			if (checked)
				mPublishStats = Flinnt.PUBLISH_NOW;
			schedulePublishPostLineatLayout.setVisibility(View.GONE);
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			scheduleDateTxtview.setText(res.getString(R.string.select_date));
			scheduleTimeTxtview.setText(res.getString(R.string.select_time));
			break;

		case R.id.publish_post_schedule_addalert:
            if (checked) {
                mPublishStats = Flinnt.PUBLISH_SCHEDULE;
                mScheduleYear = Flinnt.INVALID;
                mScheduleHour = Flinnt.INVALID;
            }
			schedulePublishPostLineatLayout.setVisibility(View.VISIBLE);
			focusOnView();
			break;
		}
	}

    /**
     * Scroll to the bottom
     */
	private final void focusOnView(){
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				mScrollView.scrollTo(0, mScrollView.getBottom());
			}
		});
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.schedule_date_relative_addalert:
			// open DatePickerDialog
			new DatePickerDialog(AddAlertActivity.this, datePicker,
					mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
					mCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.schedule_time_relative_addalert:
			new TimePickerDialog(AddAlertActivity.this, timePicker,
					mCalendar.get(Calendar.HOUR_OF_DAY), 
					mCalendar.get(Calendar.MINUTE), false).show();
			break;

		default:
			break;
		} 

	}

	DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, monthOfYear);
			mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			mScheduleYear = year;
			mScheduleMonth = (monthOfYear+1);
			mScheduleDay = dayOfMonth;

			String myFormat = "dd/MM/yy"; //In which you need put here
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
			scheduleDateTxtview.setText(sdf.format(mCalendar.getTime()));
		}
	};

	OnTimeSetListener timePicker = new OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			mCalendar.set(Calendar.MINUTE, minute);

			mScheduleHour = hourOfDay;
			mScheduleMinute = minute;

			String myFormat = "hh:mm a"; //In which you need put here
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
			scheduleTimeTxtview.setText(sdf.format(mCalendar.getTime()));
		}
	};

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(AddAlertActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AddAlertActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
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
