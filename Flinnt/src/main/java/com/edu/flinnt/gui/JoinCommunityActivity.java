package com.edu.flinnt.gui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.edu.flinnt.R;
import com.edu.flinnt.adapter.JoinCommunityAdapter;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

/**
 * GUI class to help with joining community
 */
public class JoinCommunityActivity extends AppCompatActivity {

	public static final String TAG = JoinCommunityActivity.class.getSimpleName();
	private Toolbar mToolbar;

	RecyclerView mRecyclerView;
	JoinCommunityAdapter mJoinCommunityAdapter;
	LinearLayoutManager mLinearLayoutManager;
	ArrayList<JoinCommunityCardViewItem> mJoinCommunityList = new ArrayList<JoinCommunityCardViewItem>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(
					R.color.ColorPrimaryDark));
		}

		setContentView(R.layout.join_community_activity);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Join A Community");

		mRecyclerView = (RecyclerView) findViewById(R.id.join_community_recycler_view);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "Join Community");
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
		super.onResume();
		// for testing 
		for (int i = 0; i < 6; i++) {
			JoinCommunityCardViewItem joinCommunity = new JoinCommunityCardViewItem();
			mJoinCommunityList.add( joinCommunity );
		}

		refreshView();
	}

	public void refreshView() {
		// use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        
		mJoinCommunityAdapter = new JoinCommunityAdapter(mJoinCommunityList);
		mRecyclerView.setAdapter(mJoinCommunityAdapter);
	}
}
