package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.SelectUsers;
import com.edu.flinnt.protocol.SelectUserInfo;
import com.edu.flinnt.protocol.SelectUsersRequest;
import com.edu.flinnt.protocol.SelectUsersResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Fragment class for displaying learners list
 */
public class StudentsFragment extends Fragment {
	
	private ArrayList<SelectUserInfo> studentUsersList;
	private RecyclerView mRecyclerView;
	public CheckBox checkBoxSelectAllStudents;
	public static SelectUsersAdapter mStudentAdapter;
	Handler mHandler;
	private String mCourseID = "", mStudentsIDs = "";
	
	ProgressDialog mProgressDialog = null;
	RelativeLayout noUsersFoundLayout;
	private int hasMoreInt = 0, adpTotleCount = 0, preIDsCount = 0;
	
	SelectUsers mSelectUsers;
	private String queryStrSF = "";

	// Another constructor function, enable to pass them arguments.
	public static StudentsFragment newInstance(String courseID, String studentsIDs) {
		StudentsFragment fragment = new StudentsFragment(/*courseID, studentsIDs*/);
		
		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putString("course_id", courseID);
		args.putString("student_ids", studentsIDs);
		fragment.setArguments(args);
		
		return fragment;
	} 
	
	/**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
        	mCourseID = getArguments().getString("course_id");
        	mStudentsIDs = getArguments().getString("student_ids");
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("StudentsFragment onCreate courseID : " + mCourseID);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.tab_select_students,container,false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_students);
//		checkBoxSelectAllStudents = (CheckBox) v.findViewById(R.id.checkBoxSelectAllStudents);
		noUsersFoundLayout = (RelativeLayout) v.findViewById(R.id.layout_no_users);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message message) {
				// Gets the task from the incoming Message object.
				switch (message.what) {
				case Flinnt.SUCCESS:
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Students list Size ::  : " + ((SelectUsersResponse) message.obj).getSelectUserInfoList().size() );
					if(TextUtils.isEmpty(SelectUsersActivity.userProfileUrl))
						SelectUsersActivity.userProfileUrl = ( (SelectUsersResponse) message.obj ).getUserPictureUrl();
					updateStudentsList( (SelectUsersResponse) message.obj );
					break;
				case Flinnt.FAILURE:
					stopProgressDialog();
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					if(( mStudentAdapter != null && mStudentAdapter.getItemCount() == 0)) {
						noUsersFoundLayout.setVisibility(View.VISIBLE);
					}else{
						noUsersFoundLayout.setVisibility(View.GONE);
					}

					if( message.obj instanceof SelectUsersResponse ) {
						SelectUsersResponse response = (SelectUsersResponse) message.obj;
						if(response.errorResponse != null){
						   Helper.showAlertMessage(getActivity(), "Error", response.errorResponse.getMessage(), "CLOSE");
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
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		
		studentUsersList = new ArrayList<SelectUserInfo>();

		mStudentAdapter = new SelectUsersAdapter(getContext(), studentUsersList);
		
		/*if(mAdapter == null) {
			Toast.makeText(getActivity(), "Adapter is null !", Toast.LENGTH_SHORT).show();
		}*/
		mRecyclerView.setAdapter(mStudentAdapter);
		mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager, false) {

			@Override
			public void onLoadMore(int current_page) {
				if (LogWriter.isValidLevel(Log.INFO))
					LogWriter.write("onLoadMore : HasMore " + mSelectUsers.getResponse().getHasMore() + ", queryStrSF : " + queryStrSF);
				if (mSelectUsers != null && TextUtils.isEmpty(queryStrSF)) {

					if (mSelectUsers.getResponse().getHasMore() > 0 && adpTotleCount >= preIDsCount) {
						mSelectUsers.sendSelectUsersRequest();
					}
				}

			}
		});

		SelectUsersRequest selectUsersRequest = new SelectUsersRequest();
		selectUsersRequest.setUserID(Config.getStringValue(Config.USER_ID));
		selectUsersRequest.setCourseID(mCourseID);
		selectUsersRequest.setRole(Flinnt.COURSE_ROLE_LEARNER);
		selectUsersRequest.setListSource(Flinnt.COURSE_USER_LIST_MESSAGE);
		if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mStudentsIDs : " + mStudentsIDs );
		if(!TextUtils.isEmpty(mStudentsIDs)){
			String[] sIDs = mStudentsIDs.split(",");
			if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mStudentsIDs size : " + sIDs.length );
			JSONArray sIDsJsonArray = new JSONArray();
			for (int i = 0; i < sIDs.length; i++) {
				String id = sIDs[i];
				if(!TextUtils.isEmpty(id))	sIDsJsonArray.put(id);
			}
			selectUsersRequest.setSelected(sIDsJsonArray);
			preIDsCount = sIDsJsonArray.length();
		}
		
		mSelectUsers = new SelectUsers(mHandler, selectUsersRequest);
		mSelectUsers.sendSelectUsersRequest();
		startProgressDialog();
	}

    /**
     * updates students list
     * @param selectUsersResponse select users response
     */
	private void updateStudentsList(SelectUsersResponse selectUsersResponse) {
		if(!TextUtils.isEmpty(queryStrSF)){
			mStudentAdapter.addTempItems(selectUsersResponse.getSelectUserInfoList());
		}else{
			mStudentAdapter.addItems(selectUsersResponse.getSelectUserInfoList());
		}

		for (int i = 0; i < selectUsersResponse.getSelectUserInfoList().size(); i++) {
			SelectUserInfo studentInfo = selectUsersResponse.getSelectUserInfoList().get(i);
			studentInfo.setIsTeacher(0);

			if(mStudentsIDs.contains(studentInfo.getUserID())){
				SelectUsersActivity.checkboxState.put(studentInfo.getUserID(), true);
				studentInfo.setUserChecked(Flinnt.TRUE);
			}

			SelectUsersActivity.mUsersListBoth.add(studentInfo);
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mUsersListBoth size : " + SelectUsersActivity.mUsersListBoth.size() + " student : " + studentInfo.getUserID()  + " checked : " + studentInfo.getUserChecked());
		}


		if(null != (SelectUsersActivity) getContext())
			( (SelectUsersActivity) getContext()).onChecked();

		if( TextUtils.isEmpty(queryStrSF) ){
			if(( mStudentAdapter.getItemCount() == 0)) {
				noUsersFoundLayout.setVisibility(View.VISIBLE);
			}
			else {
				noUsersFoundLayout.setVisibility(View.GONE);
			}
		}


		hasMoreInt = selectUsersResponse.getHasMore();
		adpTotleCount = mStudentAdapter.getTotalItemCount();
		if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("adpTotleCount : " + adpTotleCount + " , hasMoreInt : " + hasMoreInt);

		if(adpTotleCount < preIDsCount){
			mSelectUsers.sendSelectUsersRequest();
		}

		if(hasMoreInt == 0  || adpTotleCount >= preIDsCount ){
			stopProgressDialog();
		}

	}

    /**
     * Set search results
     * @param searchText
     */
	public void queryResult(String searchText) {
		queryStrSF = searchText;
		if(TextUtils.isEmpty(searchText)){
			if ( null != mStudentAdapter )  mStudentAdapter.removeFilter();
		}
		if ( null != mStudentAdapter ) mStudentAdapter.getFilter().filter(searchText);
	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            mProgressDialog = Helper.getProgressDialog(getActivity(), "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null)
                mProgressDialog.show();
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
