package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.SelectUsers;
import com.edu.flinnt.gui.SelectUsersAdapter.onCheckedListener;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.PostDetailsResponse.MessageUsers;
import com.edu.flinnt.protocol.SelectUserInfo;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.HashMap;


@SuppressLint("NewApi")
/**
 * GUI class to show users list to select
 */
public class SelectUsersActivity extends AppCompatActivity implements onCheckedListener {

	static ArrayList<SelectUserInfo> mUsersListBoth;
	private ViewPager pager;
	private SelectUserViewPagerAdapter selectUsersViewPagerAdapter;
	private TabLayout tabs;
	private CharSequence tabTitles[]={""};
	//int Numboftabs = 2;

	private Button buttonBottomOpener;

	private int selectedTeachersCount;
	private int selectedStudentsCount;
	private String mCourseID = "", teacherIDs = "", studentIDs = "";

	public static HashMap<String, Boolean> checkboxState;
	public static String userProfileUrl = "";

	public static String TAB_TITLE = "tab_title";
	public static String SELECTED_TEACHERS_IDS = "selected_teachers_ids";
	public static String SELECTED_STUDENTS_IDS = "selected_students_ids";
	public static String SELECTED_TEACHERS_COUNT = "selected_teacher_count";
	public static String SELECTED_STUDENTS_COUNT = "selected_students_count";

	private boolean isTeachersTab = false;

	private StudentsFragment tabStudents;
	private TeachersFragment tabTeachers;

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
		setContentView(R.layout.select_users_activity);
		checkboxState = new HashMap<String, Boolean>();
		userProfileUrl = "";

		buttonBottomOpener = (Button) findViewById(R.id.bottom_opener);
		buttonBottomOpener.setLongClickable(false);
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		toolBar.setTitle("Select Users");
		setSupportActionBar(toolBar);	
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle bundle = getIntent().getExtras();
		if( null != bundle ) {
			mCourseID = bundle.getString(Course.COURSE_ID_KEY);
			tabTitles = bundle.getCharSequenceArray(TAB_TITLE);
			if(bundle.containsKey(MessageUsers.MESSAGE_USER_TEACHER))
				teacherIDs = bundle.getString(MessageUsers.MESSAGE_USER_TEACHER);
			if(bundle.containsKey(MessageUsers.MESSAGE_USER_LEARNER))
				studentIDs = bundle.getString(MessageUsers.MESSAGE_USER_LEARNER);
		}

		mUsersListBoth = new ArrayList<SelectUserInfo>();

		buttonBottomOpener.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkedUsers = "";


				new BottomSheet.Builder(SelectUsersActivity.this, R.style.BottomSheet_Dialog)
				.sheet(R.menu.select_users_bottom_menu)
				.listener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						for(SelectUserInfo user: mUsersListBoth) {
							if ( isTeachersTab && user.getIsTeacher() == 1) {
								user.setUserChecked(0);
								checkboxState.put(user.getUserID(), false);
								TeachersFragment.mTeacherAdapter.notifyDataSetChanged();
							}
							else if ( !isTeachersTab && user.getIsTeacher() == 0 ) {
								user.setUserChecked(0);
								checkboxState.put(user.getUserID(), false);
								StudentsFragment.mStudentAdapter.notifyDataSetChanged();
							}
							onChecked();
						}
					}
				}).show();
			}
		});


		selectUsersViewPagerAdapter =  new SelectUserViewPagerAdapter(getSupportFragmentManager());

		// Assigning ViewPager View and setting the selectUsersViewPagerAdapter
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(selectUsersViewPagerAdapter);

		// Assigning the Sliding Tab Layout View
		tabs = (TabLayout) findViewById(R.id.tabs);
		//		tabs.setDistributeEvenly(true); 
		// To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width


		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int pageIndex) {
				String pageTitle = (String) selectUsersViewPagerAdapter.getPageTitle(pageIndex);

				isTeachersTab = pageTitle.equalsIgnoreCase("Teachers") ? true : false;

				buttonBottomOpener.setVisibility(isAnyChecked(isTeachersTab) ? View.VISIBLE : View.GONE);

				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("\nisTeachersTab : " + isTeachersTab +
										     "\n isAnyChecked : " + isAnyChecked(isTeachersTab));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		// Setting the ViewPager For the SlidingTabsLayout
		tabs.setupWithViewPager(pager);
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Select Users (Message)");
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
		MenuItem searchItem = menu.findItem(R.id.action_search);

		SearchManager searchManager = (SearchManager) SelectUsersActivity.this.getSystemService(Context.SEARCH_SERVICE);

		SearchView searchView = null;

		if (searchItem != null) {
			searchView = (SearchView) searchItem.getActionView();
		}
		if (searchView != null) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(SelectUsersActivity.this.getComponentName()));
			searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>"+"Search..."+"</font>"));

			SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
			{
				public boolean onQueryTextChange(String searchText)
				{
                    if (null != tabStudents) tabStudents.queryResult(searchText);
                    if (null != tabTeachers) tabTeachers.queryResult(searchText);
					//					mAdapter.getFilter().filter(searchText);
					return true;
				}

				public boolean onQueryTextSubmit(String queryText)
				{
                    if (null != tabStudents) tabStudents.queryResult(queryText);
                    if (null != tabTeachers) tabTeachers.queryResult(queryText);
					//					mAdapter.getFilter().filter(queryText);
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
		int id = item.getItemId();
		if (id == R.id.action_done) {
			if(isAnyChecked(true) || isAnyChecked(false)) { // true = teachers tab and false = students tab here
				ArrayList<String> selectedTeachersIDs = new ArrayList<String>();
				ArrayList<String> selectedStudentsIDs = new ArrayList<String>();

				selectedTeachersCount = 0;
				selectedStudentsCount = 0;

				//selectedTeachersIDs = "Selected Teachers' IDs are : ";
				//selectedStudentsIDs = "Selected Students' IDs are : ";

				ArrayList<String> allIDs = new ArrayList<String>();
				ArrayList<String> selectedIDs = new ArrayList<String>();

				for(SelectUserInfo user : mUsersListBoth) {

					allIDs.add(user.getUserID());

					if(user.getUserChecked()==1) {
						//userIDs.add(user.getUserID());
						selectedIDs.add(user.getUserID());

						if(user.getIsTeacher()==1) {
							//selectedTeachersIDs += user.getUserID() + ", ";
							selectedTeachersIDs.add(user.getUserID());
							selectedTeachersCount += 1;
						}
						else {
							//selectedStudentsIDs += user.getUserID() + ", ";
							selectedStudentsIDs.add(user.getUserID());
							selectedStudentsCount += 1;
						}
					}
				}

				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("allIDs size : " + allIDs.size() + " allIDs : " + allIDs);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("selected IDs size : " + selectedIDs.size() + " SelectedIDs : " + selectedIDs);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("selected students size : " + selectedStudentsIDs.size() + " selectedStudents : " + selectedStudentsIDs);
				if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("selected teachers size : " + selectedTeachersIDs.size() + " selectedTeachers : " + selectedTeachersIDs);

				Intent resultIntent = new Intent();
				resultIntent.putExtra( SELECTED_TEACHERS_IDS, selectedTeachersIDs );
				resultIntent.putExtra( SELECTED_STUDENTS_IDS, selectedStudentsIDs );
				resultIntent.putExtra( SELECTED_TEACHERS_COUNT, selectedTeachersCount );
				resultIntent.putExtra( SELECTED_STUDENTS_COUNT, selectedStudentsCount );
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				// Cancel all pending request before exit
				Requester.getInstance().cancelPendingRequests(SelectUsers.TAG);

				return true;
			}
			else {
				Toast.makeText(this, "Please select at least one user", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else if (id == android.R.id.home) {
            finish(); //onBackPressed();
        }

		return super.onOptionsItemSelected(item);
	}

    /**
     * Checks if any of the user is checked or not
     * @param isTeacher user role
     * @return true if one or more users are checked, false otherwise
     */
	public boolean isAnyChecked(boolean isTeacher) {
		for(SelectUserInfo user : mUsersListBoth) {
			int role;
			role = isTeacher? 1 : 0 ;
			
			if ( isTeachersTab == isTeacher && user.getUserChecked() == 1 && user.getIsTeacher() == role ) {
				return true;
			}
			else if ( isTeachersTab != isTeacher && user.getUserChecked() == 1 && user.getIsTeacher() == role ) {
				return true;
			}
		}
		return false;
	}

    /**
     * Whenever users get checked
     */
	public void onChecked() {
		//		invalidateOptionsMenu();
		if( isAnyChecked (isTeachersTab) ) {
			buttonBottomOpener.setVisibility(View.VISIBLE);
		}
		else {
			buttonBottomOpener.setVisibility(View.GONE);			
		}
	}

	public class SelectUserViewPagerAdapter extends FragmentStatePagerAdapter {

		// Build a Constructor and assign the passed Values to appropriate values in the class
		public SelectUserViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		//This method return the fragment for the every position in the View Pager
		@Override
		public Fragment getItem(int position) {

			if(getPageTitle(position).toString().equalsIgnoreCase("Learners")){
				tabStudents = StudentsFragment.newInstance(mCourseID, studentIDs);
				return tabStudents;
			}
			else if(getPageTitle(position).toString().equalsIgnoreCase("Teachers")){
				tabTeachers = TeachersFragment.newInstance(mCourseID, teacherIDs);
				return tabTeachers;
			}
			else return null;
		}

		// This method return the titles for the Tabs in the Tab Strip

		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitles[position];
		}

		// This method return the Number of tabs for the tabs Strip

		@Override
		public int getCount() {
			return tabTitles.length;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
	}
}
