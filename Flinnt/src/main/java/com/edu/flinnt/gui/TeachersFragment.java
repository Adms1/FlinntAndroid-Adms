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
 * Fragment class for displaying teachers list
 */
public class TeachersFragment extends Fragment { // Implement onchecked if needed

	private ArrayList<SelectUserInfo> teacherUsersList;
	private RecyclerView mRecyclerView;
	public CheckBox checkBoxSelectAllTeachers;
	public static SelectUsersAdapter mTeacherAdapter;
	Handler mHandler;
	ProgressDialog mProgressDialog = null;
	private String mCourseID = "", mTeachersIDs = "";
	
	RelativeLayout noUsersFoundLayout;
	private int hasMoreInt = 0, adpTotleCount = 0, preIDsCount = 0;

	SelectUsers mSelectUsers;
	private String queryStrTF = "";

	// Another constructor function, enable to pass them arguments.
	public static TeachersFragment newInstance(String courseID, String teachersIDs) {
		TeachersFragment fragment = new TeachersFragment(/*courseID, teachersIDs*/);
		
		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putString("course_id", courseID);
		args.putString("teachers_ids", teachersIDs);
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
        	mTeachersIDs = getArguments().getString("teachers_ids");
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("TeachersFragment onCreate courseID : " + mCourseID);
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.tab_select_teachers,container,false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_teachers);
//		checkBoxSelectAllTeachers = (CheckBox) v.findViewById(R.id.checkBoxSelectAllTeachers);
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
					//stopProgressDialog(); // stop dialog after updateTeachersList...
					//Helper.showToast("Success");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Teachers list Size ::  : " + ((SelectUsersResponse) message.obj).getSelectUserInfoList().size() );
					if(TextUtils.isEmpty(SelectUsersActivity.userProfileUrl))
						SelectUsersActivity.userProfileUrl = ( (SelectUsersResponse) message.obj ).getUserPictureUrl();
					updateTeachersList( (SelectUsersResponse) message.obj );
				
					break;
				case Flinnt.FAILURE:
					stopProgressDialog();
					//Helper.showToast("Failure");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					if(( mTeacherAdapter != null && mTeacherAdapter.getItemCount() == 0)) {
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
		
		teacherUsersList = new ArrayList<SelectUserInfo>();

		mTeacherAdapter = new SelectUsersAdapter(getContext(), teacherUsersList);
		
		mRecyclerView.setAdapter(mTeacherAdapter);
		mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager, false) {

			@Override
			public void onLoadMore(int current_page) {
				if (mSelectUsers != null && TextUtils.isEmpty(queryStrTF)) {
					if (LogWriter.isValidLevel(Log.INFO))
						LogWriter.write("onLoadMore : HasMore " + mSelectUsers.getResponse().getHasMore());
					if (mSelectUsers.getResponse().getHasMore() > 0 && adpTotleCount >= preIDsCount ) {
						mSelectUsers.sendSelectUsersRequest();
					}
				}

			}
		});


		SelectUsersRequest selectUsersRequest = new SelectUsersRequest();
		selectUsersRequest.setUserID(Config.getStringValue(Config.USER_ID));
		selectUsersRequest.setCourseID(mCourseID);
		selectUsersRequest.setRole(Flinnt.COURSE_ROLE_TEACHER);
		selectUsersRequest.setListSource(Flinnt.COURSE_USER_LIST_MESSAGE);
		
		if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mTeachersIDs : " + mTeachersIDs );
		if(!TextUtils.isEmpty(mTeachersIDs)){
			String[] tIDs = mTeachersIDs.split(",");
			if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mTeachersIDs size : " + tIDs.length );
			JSONArray tIDsJsonArray = new JSONArray();
			for (int i = 0; i < tIDs.length; i++) {
				String id = tIDs[i];
				if(!TextUtils.isEmpty(id))	tIDsJsonArray.put(id);
			}
			selectUsersRequest.setSelected(tIDsJsonArray);
			preIDsCount = tIDsJsonArray.length();
		}
		
		mSelectUsers = new SelectUsers(mHandler, selectUsersRequest);
		mSelectUsers.sendSelectUsersRequest();
		startProgressDialog();
	}

    /**
     * update teachers list
     * @param selectUsersResponse select users response
     */
	private void updateTeachersList(SelectUsersResponse selectUsersResponse) {

		if(!TextUtils.isEmpty(queryStrTF)){
			mTeacherAdapter.addTempItems(selectUsersResponse.getSelectUserInfoList());
		}else{
			mTeacherAdapter.addItems(selectUsersResponse.getSelectUserInfoList());
		}
		for (int i = 0; i < selectUsersResponse.getSelectUserInfoList().size(); i++) {
			SelectUserInfo teacherInfo = selectUsersResponse.getSelectUserInfoList().get(i);
			teacherInfo.setIsTeacher(1);

			if(mTeachersIDs.contains(teacherInfo.getUserID())){
				SelectUsersActivity.checkboxState.put(teacherInfo.getUserID(), true);
				teacherInfo.setUserChecked(Flinnt.TRUE);
			}

			SelectUsersActivity.mUsersListBoth.add(teacherInfo);
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mUsersListBoth size : " + SelectUsersActivity.mUsersListBoth.size() + " teacher : " + teacherInfo.getUserID() + " checked : " + teacherInfo.getUserChecked());
		}



		if(null != (SelectUsersActivity) getContext())
			( (SelectUsersActivity) getContext()).onChecked();

		if( TextUtils.isEmpty(queryStrTF) ){
			if ( mTeacherAdapter.getItemCount() == 0 ) {
				noUsersFoundLayout.setVisibility(View.VISIBLE);
			}
			else {
				noUsersFoundLayout.setVisibility(View.GONE);
			}
		}

		hasMoreInt = selectUsersResponse.getHasMore();
		adpTotleCount = mTeacherAdapter.getTotalItemCount();
		if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("adpTotleCount : " + adpTotleCount + " , hasMoreInt : " + hasMoreInt + " , preIDsCount : " + preIDsCount);

		if(adpTotleCount < preIDsCount){
			mSelectUsers.sendSelectUsersRequest();
		}

		if(hasMoreInt == 0  || adpTotleCount >= preIDsCount ){
			stopProgressDialog();
		}
	}

    /**
     * sets search query result
     * @param searchText
     */
	public void queryResult(String searchText) {
		queryStrTF = searchText;
		if(TextUtils.isEmpty(searchText)){
			if ( null != mTeacherAdapter ) mTeacherAdapter.removeFilter();
		}
		if ( null != mTeacherAdapter ) mTeacherAdapter.getFilter().filter(searchText);
	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(getActivity())) {
            mProgressDialog = Helper.getProgressDialog(getActivity(), "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(getActivity()))
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
