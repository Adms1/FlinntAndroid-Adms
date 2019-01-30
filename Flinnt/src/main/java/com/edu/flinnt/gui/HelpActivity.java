package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.edu.flinnt.R;
import com.edu.flinnt.util.CirclePageIndicator;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

@SuppressLint("NewApi")
/**
 * GUI class to show help information
 */
public class HelpActivity extends FragmentActivity {

	int[] imageResourseArray = {  // total 5 images
			R.drawable.walkthrough_help_screen1,
			R.drawable.walkthrough_help_screen2,
			R.drawable.walkthrough_help_screen3,
			R.drawable.walkthrough_help_screen4,
			R.drawable.walkthrough_help_screen5,
	};
	
	HelpViewPagerAdapter mHelpViewPagerAdapter;
	ViewPager pager;
	CirclePageIndicator mIndicator;

	//	Set number of pages here	
	private final int PAGE_COUNT = 5; 
	
	Button buttonSkip;
	String type = "splashscreen";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.help_activity);
		
		Bundle b = getIntent().getExtras();
		if(null != b){
			type = b.getString("TYPE");
			if(LogWriter.isValidLevel(Log.INFO))  LogWriter.write("type :: "+ type);
		}

		buttonSkip = (Button) findViewById(R.id.button_skip);
		buttonSkip.setLongClickable(false);
		if(!type.equals("splashscreen")){
			buttonSkip.setText("CLOSE");
		}
		buttonSkip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(type.equals("splashscreen")){
					Intent i = new Intent(HelpActivity.this, LoginActivity.class);
					startActivity(i);
					Config.setStringValue(Config.FLINNT_FEATURES_STATS, "1");
				}
				finish();
			}
		});
		
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		pager = (ViewPager) findViewById(R.id.pager_help);
		
		mHelpViewPagerAdapter =  new HelpViewPagerAdapter(getSupportFragmentManager());
		
		pager.setAdapter(mHelpViewPagerAdapter);
		mIndicator.setViewPager(pager);
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Help");
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public class HelpViewPagerAdapter extends FragmentStatePagerAdapter {

		// Build a Constructor and assign the passed Values to appropriate values in the class
		public HelpViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		//This method return the fragment for the every position in the View Pager
		@Override
		public Fragment getItem(int position) {
			HelpFragment tab = HelpFragment.newInstance ( imageResourseArray [0], "View Courses & Manage the Settings" );	
			switch (position) {
			case 0:
				tab = HelpFragment.newInstance ( imageResourseArray [ 0 ], "View Courses & Manage the Settings" );	
				break;
			case 1:
				tab = HelpFragment.newInstance ( imageResourseArray [ 1 ], "View & Choose communication Type as per your Category." );	
				break;
			case 2:
				tab = HelpFragment.newInstance ( imageResourseArray [ 2 ], "Customize your profile & manage account settings." );	
				break;
			case 3:
				tab = HelpFragment.newInstance ( imageResourseArray [ 3 ], "Educators can manage learners within a course." );	
				break;
			case 4:
				tab = HelpFragment.newInstance ( imageResourseArray [ 4 ], "Administrators can manage course settings for each course." );	
				break;

			default:
				break;
			}
							// Help Screen Screenshot image Resource				

			return tab;
		}

		@Override
		public int getCount() {

			return PAGE_COUNT;
		}
	}
	
}
