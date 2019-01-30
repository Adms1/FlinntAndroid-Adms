package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.AddPostRequest;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by flinnt-android-2 on 22/12/16.
 * This activity is use for schedule the post for future point of time while add Communication.
 */

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    LinearLayout schedulePublishPostLineatLayout;
    RelativeLayout scheduleDateRelative, scheduleTimeRelative, publishRelative;

    TextView scheduleDateTxtview, scheduleTimeTxtview;
    RadioGroup mRadioGroup;
    RadioButton mPublishPostNowRadio;
    RadioButton mPublishPostScheduleRadio;

    private String mPublishStats = Flinnt.PUBLISH_NOW;
    private int mScheduleYear = Flinnt.INVALID, mScheduleMonth = Flinnt.INVALID, mScheduleDay = Flinnt.INVALID,
            mScheduleHour = Flinnt.INVALID, mScheduleMinute = Flinnt.INVALID;

    private int mPostStat = Flinnt.INVALID;

    Resources res = FlinntApplication.getContext().getResources();
    final Calendar mCalendar = Calendar.getInstance();
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;

    String myFormatDate = "dd/MM/yy"; //In which you need put here
    SimpleDateFormat sdfDate = new SimpleDateFormat(myFormatDate, Locale.US);

    String myFormat = "hh:mm a"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_schedule);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey("mScheduleStatus"))
                mPublishStats = bundle.getString(AddPostRequest.PUB_YEAR_KEY);
            if (bundle.containsKey(AddPostRequest.PUB_YEAR_KEY))
                mScheduleYear = Integer.parseInt(bundle.getString(AddPostRequest.PUB_YEAR_KEY));
            if (bundle.containsKey(AddPostRequest.PUB_MONTH_KEY))
                mScheduleMonth = Integer.parseInt(bundle.getString(AddPostRequest.PUB_MONTH_KEY));
            if (bundle.containsKey(AddPostRequest.PUB_DAY_KEY))
                mScheduleDay = Integer.parseInt(bundle.getString(AddPostRequest.PUB_DAY_KEY));
            if (bundle.containsKey(AddPostRequest.PUB_HOUR_KEY))
                mScheduleHour = Integer.parseInt(bundle.getString(AddPostRequest.PUB_HOUR_KEY));
            if (bundle.containsKey(AddPostRequest.PUB_MINUTE_KEY))
                mScheduleMinute = Integer.parseInt(bundle.getString(AddPostRequest.PUB_MINUTE_KEY));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mPostStat == Flinnt.POST_ALERT_EDIT) {
            getSupportActionBar().setTitle(R.string.add_schedule);
        }

        schedulePublishPostLineatLayout = (LinearLayout) findViewById(R.id.publish_date_time_select_linear);
        scheduleDateRelative = (RelativeLayout) findViewById(R.id.schedule_date_relative);
        scheduleTimeRelative = (RelativeLayout) findViewById(R.id.schedule_time_relative);
        publishRelative = (RelativeLayout) findViewById(R.id.publish_relative_main);


        scheduleDateTxtview = (TextView) findViewById(R.id.schedule_date_text);
        scheduleTimeTxtview = (TextView) findViewById(R.id.schedule_time_text);

        mRadioGroup = (RadioGroup) findViewById(R.id.publish_post_radiogroup);
        mPublishPostNowRadio = (RadioButton) findViewById(R.id.publish_post_now_radio);
        mPublishPostScheduleRadio = (RadioButton) findViewById(R.id.publish_post_schedule_radio);

        scheduleDateRelative.setOnClickListener(this);
        scheduleTimeRelative.setOnClickListener(this);

        if (mPostStat == Flinnt.POST_ALERT_EDIT) {
            publishRelative.setVisibility(View.GONE);

        }
        if(mPublishStats.equals(Flinnt.PUBLISH_NOW)){
            mPublishPostNowRadio.setChecked(true);
        }

        if (mScheduleYear != Flinnt.INVALID) {
            mPublishPostScheduleRadio.setChecked(true);
            schedulePublishPostLineatLayout.setVisibility(View.VISIBLE);

            mCalendar.set(Calendar.YEAR, mScheduleYear);
            mCalendar.set(Calendar.MONTH, mScheduleMonth - 1);
            mCalendar.set(Calendar.DAY_OF_MONTH, mScheduleDay);

            mCalendar.set(Calendar.HOUR_OF_DAY, mScheduleHour);
            mCalendar.set(Calendar.MINUTE, mScheduleMinute);

            scheduleDateTxtview.setText(sdfDate.format(mCalendar.getTime()));
            scheduleTimeTxtview.setText(sdf.format(mCalendar.getTime()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Schedule Activity");
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
        getMenuInflater().inflate(R.menu.schedule_menu, menu);
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
                Helper.hideKeyboardFromWindow(ScheduleActivity.this);
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(this);
                } else {
                    if (mPublishPostScheduleRadio.isChecked()) {
                        if (validateAlert()) {
                            Intent result = new Intent();
                            result.putExtra("mScheduleStatus",Flinnt.PUBLISH_SCHEDULE);
                            result.putExtra("mScheduleYear", Integer.toString(mScheduleYear));
                            result.putExtra("mScheduleMonth", Integer.toString(mScheduleMonth));
                            result.putExtra("mScheduleDay", Integer.toString(mScheduleDay));
                            result.putExtra("mScheduleHour", Integer.toString(mScheduleHour));
                            result.putExtra("mScheduleMinute", Integer.toString(mScheduleMinute));
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        }
                    }else{
                        Intent result = new Intent();
                        result.putExtra("mScheduleStatus",Flinnt.PUBLISH_NOW);
                        result.putExtra("mScheduleYear", Integer.toString(Flinnt.INVALID));
                        result.putExtra("mScheduleMonth", Integer.toString(Flinnt.INVALID));
                        result.putExtra("mScheduleDay", Integer.toString(Flinnt.INVALID));
                        result.putExtra("mScheduleHour", Integer.toString(Flinnt.INVALID));
                        result.putExtra("mScheduleMinute", Integer.toString(Flinnt.INVALID));
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks if the alert is valid to be sent
     *
     * @return true if valid, false otherwise
     */
    private boolean validateAlert() {

        if (mPublishStats.equals(Flinnt.PUBLISH_SCHEDULE)) {
            if (mScheduleYear == Flinnt.INVALID) {
                Helper.showAlertMessage(ScheduleActivity.this, getString(R.string.alert), getString(R.string.select_date), getString(R.string.close_txt));
                return false;
            } else if (mScheduleHour == Flinnt.INVALID) {
                Helper.showAlertMessage(ScheduleActivity.this, getString(R.string.alert), getString(R.string.select_time), getString(R.string.close_txt));
                return false;
            }

            Date  date = new Date();
            date.setYear(mScheduleYear -1900);
            date.setMonth(mScheduleMonth - 1);
            date.setDate(mScheduleDay );
            date.setHours(mScheduleHour);
            date.setMinutes(mScheduleMinute);

            if(date.getTime() < System.currentTimeMillis()){
                Helper.showAlertMessage(ScheduleActivity.this, getString(R.string.alert), getString(R.string.cannot_select_past_time), getString(R.string.close_txt));
                return false;
            }

            return true;
        }
        return true;
    }

    /**
     * Checks if to publish now or schedule
     *
     * @param view publish now/schedule radio button
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {

            case R.id.publish_post_now_radio:
                if (checked)
                    mPublishStats = Flinnt.PUBLISH_NOW;
                schedulePublishPostLineatLayout.setVisibility(View.GONE);
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                scheduleDateTxtview.setText(res.getString(R.string.select_date));
                scheduleTimeTxtview.setText(res.getString(R.string.select_time));
                break;

            case R.id.publish_post_schedule_radio:
                if (checked) {
                    mPublishStats = Flinnt.PUBLISH_SCHEDULE;
                    mScheduleYear = Flinnt.INVALID;
                    mScheduleHour = Flinnt.INVALID;
                }
                schedulePublishPostLineatLayout.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.schedule_date_relative:
                // open DatePickerDialog
                DatePickerDialog dialog = new DatePickerDialog(ScheduleActivity.this, datePicker,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
                dialog.show();
                break;
            case R.id.schedule_time_relative:
                new TimePickerDialog(ScheduleActivity.this, timePicker,
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
            mScheduleMonth = (monthOfYear + 1);
            mScheduleDay = dayOfMonth;

            scheduleDateTxtview.setText(sdfDate.format(mCalendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);

            mScheduleHour = hourOfDay;
            mScheduleMinute = minute;

            if(mCalendar.getTime().getTime()<System.currentTimeMillis()){
                Helper.showAlertMessage(ScheduleActivity.this,getString(R.string.select_time),getString(R.string.cannot_select_past_time),getString(R.string.close_txt));
                return;
            }

            scheduleTimeTxtview.setText(sdf.format(mCalendar.getTime()));
        }
    };

}
