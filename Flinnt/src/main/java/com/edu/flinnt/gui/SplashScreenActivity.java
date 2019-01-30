package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * GUI class to show splash screen
 */
public class SplashScreenActivity extends AppCompatActivity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 500;
	public static int DEVICE_DENSITY = DisplayMetrics.DENSITY_MEDIUM;
	private String couserHash;
	public Handler mHandler = null;
	ProgressDialog mProgressDialog = null;
	private String data = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.splash_screen);

		/*Log.i("TAG","ID: " + Build.ID);
		Log.i("TAG", "SERIAL: " + Build.SERIAL);
		Log.i("TAG","MODEL: " + Build.MODEL);
		Log.i("TAG","Manufacture: " + Build.MANUFACTURER);
		Log.i("TAG","brand: " + Build.BRAND);
		Log.i("TAG","Version Code: " + Build.VERSION.RELEASE);
		Log.i("TAG","SDK  " + Build.VERSION.SDK);
		Log.i("TAG","type: " + Build.TYPE);
		Log.i("TAG","BOARD: " + Build.BOARD);
		Log.i("TAG","user: " + Build.USER);
		Log.i("TAG","BASE: " + Build.VERSION_CODES.BASE);
		Log.i("TAG","INCREMENTAL " + Build.VERSION.INCREMENTAL);
		Log.i("TAG","HOST " + Build.HOST);
		Log.i("TAG","FINGERPRINT: "+Build.FINGERPRINT);
		Log.i("Display : ", Build.DISPLAY);*/
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Splash Screen");
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

	private String redirectURL = "";

	@Override
	protected void onResume() {
		super.onResume();

		String versionName = Helper.getAppVersionName(SplashScreenActivity.this);
		LogWriter.write("Flinnt Version Name : " + versionName);

		//Determine density
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int density = metrics.densityDpi;
		DEVICE_DENSITY = density;
		LogWriter.write("DEVICE_DENSITY : " + DEVICE_DENSITY);

		switch (density) {
			case DisplayMetrics.DENSITY_LOW:
				Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_LOW;
				Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_SMALL;
				break;

			case DisplayMetrics.DENSITY_MEDIUM:
				Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_MEDIUM;
				Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_SMALL;
				break;

			case DisplayMetrics.DENSITY_HIGH:
				Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_HIGH;
				Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_SMALL;
				break;

			case DisplayMetrics.DENSITY_XHIGH:
				Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_XHIGH;
				Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_MEDIUM;
				break;

			case DisplayMetrics.DENSITY_XXHIGH:
				Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_XXHIGH;
				Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_LARGE;
				break;

			default:
				Flinnt.SCREEN_DENSITY = DisplayMetrics.DENSITY_HIGH;
				Flinnt.PROFILE_IMAGE = Flinnt.PROFILE_MEDIUM;
				break;
		}


			// This method will be executed once the timer is over
			// Start your app main activity
			new Handler().postDelayed(new Runnable() {
				/*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */
				@Override
				public void run() {
					navigateToNextView();
				}
			}, SPLASH_TIME_OUT);
	}

	/**
	 * To navigate to respective screen.
	 */
	private void navigateToNextView(){
		Intent i = null;
		if(Config.getStringValue(Config.FLINNT_FEATURES_STATS).equals(Flinnt.DISABLED)){
			i = new Intent(SplashScreenActivity.this, HelpActivity.class);
			i.putExtra("TYPE", "splashscreen");
		}else{
			i = new Intent(SplashScreenActivity.this, LoginActivity.class);
		}

		if(i != null) startActivity(i);
		// close this activity
		finish();
	}

}