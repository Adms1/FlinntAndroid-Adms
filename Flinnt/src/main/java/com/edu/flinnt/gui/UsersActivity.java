package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.edu.flinnt.R;
import com.edu.flinnt.core.AllowDisallow;
import com.edu.flinnt.gui.UsersAdapter.OnCheckedListener;
import com.edu.flinnt.protocol.AllowDisallowRequest;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.HashMap;

@SuppressLint("NewApi")
/**
 * GUI class to show users list and select to change privileges
 */
public class UsersActivity extends AppCompatActivity implements OnCheckedListener {

	ViewPager pager;
	UserViewPagerAdapter usersViewPagerAdapter;
	TabLayout tabs;
	CharSequence Titles[]={""};
	
	public static Handler mHandler = null;

	AllowDisallow mAllowDisallow;
	AllowDisallowRequest mAllowDisallowRequest;

	public static HashMap<String, Boolean> checkboxState;
	public static String userProfileUrl = "";

	public static int TEACHER = 1;
	public static int LEARNER = 0;
	public static int CHECKED = 1;

	static String mCourseID = "";
	public static String TAB_TITLE = "tab_title";
	public static String SELECTED_USERS_IDS = "selected_users_ids";
	String pageTitle = "";

	TeachersFragmentUsers tabTeachers;
	StudentsFragmentUsers tabStudents;
	private SearchView searchView = null;

	@SuppressWarnings("deprecation")
	@SuppressLint({ "ResourceAsColor", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.user_activity);
		checkboxState = new HashMap<String, Boolean>();
		userProfileUrl = "";

		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		toolBar.setTitle("Users");
		setSupportActionBar(toolBar);	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle bundle = getIntent().getExtras();
		if( null != bundle ) {
			mCourseID = bundle.getString(Course.COURSE_ID_KEY);
			Titles = bundle.getCharSequenceArray(TAB_TITLE);
		}

		usersViewPagerAdapter =  new UserViewPagerAdapter(getSupportFragmentManager());

		// Assigning ViewPager View and setting the usersViewPagerAdapter
		pager = (ViewPager) findViewById(R.id.pager);
		//		pager.setPagingEnabled(true);
		pager.setAdapter(usersViewPagerAdapter);

		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				searchView.setQuery("",false);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		pageTitle = (String) usersViewPagerAdapter.getPageTitle(0);

		// Assigning the Sliding Tab Layout View
		tabs = (TabLayout) findViewById(R.id.tabs);
		tabs.setPadding(30, 0, 30, 0);
		tabs.setupWithViewPager(pager);

	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Users List");
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

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_users_menu, menu);
		MenuItem doneItem = menu.findItem(R.id.action_done);
		doneItem.setVisible(false);
		MenuItem searchItem = menu.findItem(R.id.action_search);

		SearchManager searchManager = (SearchManager) UsersActivity.this.getSystemService(Context.SEARCH_SERVICE);

		searchView = null;

		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
		}
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(UsersActivity.this.getComponentName()));

			searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>"+"Search..."+"</font>"));

			SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
			{
				public boolean onQueryTextChange(String searchText)
				{
                    if (null != tabTeachers) tabTeachers.queryResult(searchText);
                    if (null != tabStudents) tabStudents.queryResult(searchText);
					return true;
				}

				public boolean onQueryTextSubmit(String queryText)
				{
                    if (null != tabTeachers) tabTeachers.queryResult(queryText);
                    if (null != tabStudents) tabStudents.queryResult(queryText);
					return true;
				}
			};
			searchView.setOnQueryTextListener(queryTextListener);
		}
		return true;
	}

	String checkedUsers;

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

	public class UserViewPagerAdapter extends FragmentStatePagerAdapter {

		// Build a Constructor and assign the passed Values to appropriate values in the class
		public UserViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		//This method return the fragment for the every position in the View Pager
		@Override
		public Fragment getItem(int position) {

			if(getPageTitle(position).toString().equalsIgnoreCase("Teachers")){
				tabTeachers = TeachersFragmentUsers.newInstance(mCourseID);
				return tabTeachers;
			}else{
				tabStudents = StudentsFragmentUsers.newInstance(mCourseID);
				return tabStudents;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return Titles[position];
		}

		@Override
		public int getCount() {
			return Titles.length;
		}
	}

	@Override
	public void onChecked() {
		if(null != tabStudents) {
			tabStudents.buttonBottomOpener.setVisibility(tabStudents.isAnyChecked() ? View.VISIBLE : View.GONE);
		}
		if(null != tabTeachers) {
			tabTeachers.buttonBottomOpener.setVisibility(tabTeachers.isAnyChecked() ? View.VISIBLE : View.GONE);
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
	}
}
