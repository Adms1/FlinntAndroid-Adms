package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AllowDisallow;
import com.edu.flinnt.core.SelectUsers;
import com.edu.flinnt.protocol.AllowDisallowRequest;
import com.edu.flinnt.protocol.AllowDisallowResponse;
import com.edu.flinnt.protocol.SelectUserInfo;
import com.edu.flinnt.protocol.SelectUsersRequest;
import com.edu.flinnt.protocol.SelectUsersResponse;
import com.edu.flinnt.protocol.UserInfo;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;

/**
 * Fragment class to display learners list
 */
public class StudentsFragmentUsers extends Fragment {

	private ArrayList<SelectUserInfo> studentUsersList;
	private RecyclerView mRecyclerView;
	//	public CheckBox checkBoxSelectAllStudents;
	public static UsersAdapter mStudentAdapter;
	static Handler mHandler;
	private String mCourseID = "";

	Button buttonBottomOpener;

	AllowDisallow mAllowDisallow;
	AllowDisallowRequest mAllowDisallowRequest;

	RelativeLayout noUsersFoundLayout;

	static ProgressDialog mProgressDialog;

	String canManageMessage = Flinnt.DISABLED;
	String canManageComment = Flinnt.DISABLED;
	int canRemoveSubscription = Flinnt.FALSE;
	SelectUsers mSelectUsers;
	private String queryStrSFU = "";

	private static Context mContext;

	// Another constructor function, enable to pass them arguments.
	public static StudentsFragmentUsers newInstance(String courseID) {
		StudentsFragmentUsers fragment = new StudentsFragmentUsers(/*courseID*/);
		
		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putString("course_id", courseID);
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
        }
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("StudentsFragmentUser onCreate courseID : " + mCourseID);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.tab_select_students,container,false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_students);
		//		checkBoxSelectAllStudents = (CheckBox) v.findViewById(R.id.checkBoxSelectAllStudents);
		buttonBottomOpener = (Button) v.findViewById(R.id.bottom_opener);
		buttonBottomOpener.setVisibility(View.GONE);
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
					stopProgressDialog();
					//Helper.showToast("Success");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());

					if (message.obj instanceof SelectUsersResponse) {
						//					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("StudentList Size ::  : " + SelectUsersResponse.size() );
						UsersActivity.userProfileUrl = ( (SelectUsersResponse) message.obj ).getUserPictureUrl();
						updateStudentsList( (SelectUsersResponse) message.obj );
						updatePermissions( (SelectUsersResponse) message.obj );
						
					}
					else if ( message.obj instanceof AllowDisallowResponse ) {

						AllowDisallowResponse allowDisallowResponse =  (AllowDisallowResponse)  (message.obj);
						if (null != allowDisallowResponse) {

							if ( allowDisallowResponse.getRemoved() == 1 ) {
								Helper.showToast("Subscription removed successfully", Toast.LENGTH_SHORT);
								removeStudents(allowDisallowResponse.getUsers());
								( (UsersActivity) mContext ).onChecked() ;
							}
							else {
								Helper.showToast("Updated successfully", Toast.LENGTH_SHORT);
								updateStudentsList( allowDisallowResponse );
							}

						}
					}

					break;
				case Flinnt.FAILURE:
					stopProgressDialog();
					//Helper.showToast("Failure");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					if( message.obj instanceof SelectUsersResponse ) {
						SelectUsersResponse response = (SelectUsersResponse) message.obj;
						if(( mStudentAdapter != null && mStudentAdapter.getItemCount() == 0)) {
						noUsersFoundLayout.setVisibility(View.VISIBLE);
						}else{
							noUsersFoundLayout.setVisibility(View.GONE);
						}

						if(response.errorResponse != null){
							Helper.showAlertMessage(getActivity(), "Error", response.errorResponse.getMessage(), "CLOSE");
						}
					}else if (message.obj instanceof AllowDisallowResponse ){
						AllowDisallowResponse allowDisallowResponse =  (AllowDisallowResponse)  (message.obj);
						if(allowDisallowResponse.errorResponse != null){
							Helper.showAlertMessage(getActivity(), "Error", allowDisallowResponse.errorResponse.getMessage(), "CLOSE");
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

		mStudentAdapter = new UsersAdapter(getActivity(), studentUsersList, false);

		/*if(mAdapter == null) {
			Toast.makeText(getActivity(), "Adapter is null !", Toast.LENGTH_SHORT).show();
		}*/
		mRecyclerView.setAdapter(mStudentAdapter);
		mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager, false) {

			@Override
			public void onLoadMore(int current_page) {
				if(mSelectUsers != null && TextUtils.isEmpty(queryStrSFU)){
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("onLoadMore : HasMore " + mSelectUsers.getResponse().getHasMore());
					if (mSelectUsers.getResponse().getHasMore() > 0) {
						mSelectUsers.sendSelectUsersRequest();
					}
				}

			}
		});

		buttonBottomOpener.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BottomSheet.Builder bottomSheetBuilder= new BottomSheet.Builder(getContext(), R.style.BottomSheet_Dialog)
				.sheet(R.menu.user_bottom_menu)
				.title("With Selected Users")
				.listener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						if ( !Helper.isConnected() ) {
							Helper.showNetworkAlertMessage(getActivity());
						}
						else {

							mAllowDisallowRequest = new AllowDisallowRequest();
							final ArrayList<String> userIDs = new ArrayList<String>();
							final ArrayList<SelectUserInfo> selectedUserList = new ArrayList<SelectUserInfo>();

							for(SelectUserInfo user: studentUsersList) {
								if(user.getUserChecked() == 1) {
									userIDs.add(user.getUserID());
									selectedUserList.add(user);
								}
							}

							mAllowDisallowRequest.setUsers(userIDs);
							mAllowDisallowRequest.setCourseID(mCourseID);

							switch(position) {
							case R.id.allow_comment:
								mAllowDisallow = new AllowDisallow(mHandler, AllowDisallow.ALLOW_COMMENT, mAllowDisallowRequest);
								mAllowDisallow.sendAllowDisallowRequest();
								break;

							case R.id.disallow_comment:
								mAllowDisallow = new AllowDisallow(mHandler, AllowDisallow.DISALLOW_COMMENT, mAllowDisallowRequest);
								mAllowDisallow.sendAllowDisallowRequest();
								break;

							case R.id.allow_message:
								mAllowDisallow = new AllowDisallow(mHandler, AllowDisallow.ALLOW_MESSAGE, mAllowDisallowRequest);
								mAllowDisallow.sendAllowDisallowRequest();
								break;

							case R.id.disallow_message:
								mAllowDisallow = new AllowDisallow(mHandler, AllowDisallow.DISALLOW_MESSAGE, mAllowDisallowRequest);
								mAllowDisallow.sendAllowDisallowRequest();
								break;

							case R.id.remove_from_course:
								mAllowDisallow = new AllowDisallow(mHandler, AllowDisallow.REMOVE_FROM_COURSE, mAllowDisallowRequest);
								mAllowDisallow.sendAllowDisallowRequest();
								break;
							default:
								break;
							}
							checkAll(false);
							buttonBottomOpener.setVisibility(View.GONE);
						}
					}
				});
				
				if ( !canManageComment.equalsIgnoreCase(Flinnt.ENABLED )) {
					bottomSheetBuilder.remove(R.id.allow_comment);
					bottomSheetBuilder.remove(R.id.disallow_comment);
				}
				if ( !canManageMessage.equalsIgnoreCase(Flinnt.ENABLED ) ) {
					bottomSheetBuilder.remove(R.id.allow_message);
					bottomSheetBuilder.remove(R.id.disallow_message);
				}
				if ( canRemoveSubscription == Flinnt.FALSE ) {
					bottomSheetBuilder.remove(R.id.remove_from_course);
				}
				bottomSheetBuilder.show();
			}
		});

		SelectUsersRequest selectUsersRequest = new SelectUsersRequest();
		selectUsersRequest.setUserID(Config.getStringValue(Config.USER_ID));
		selectUsersRequest.setCourseID(mCourseID);
		selectUsersRequest.setRole(Flinnt.COURSE_ROLE_LEARNER);
		selectUsersRequest.setListSource(Flinnt.COURSE_USER_LIST_MYCOURSE);

		mSelectUsers = new SelectUsers(mHandler, selectUsersRequest);
		mSelectUsers.sendSelectUsersRequest();
		startProgressDialog();
	}

    /**
     * updates students list
     * @param selectUsersResponse
     */
	private void updateStudentsList(SelectUsersResponse selectUsersResponse) {
		for (int i = 0; i < selectUsersResponse.getSelectUserInfoList().size(); i++) {
			SelectUserInfo studentInfo = selectUsersResponse.getSelectUserInfoList().get(i);
			studentInfo.setIsTeacher(0);
			//			studentUsersList.add(studentInfo);
		}

		if(!TextUtils.isEmpty(queryStrSFU)){
			mStudentAdapter.addTempItems(selectUsersResponse.getSelectUserInfoList());
		}else{
			mStudentAdapter.addItems(selectUsersResponse.getSelectUserInfoList());
		}
		if( TextUtils.isEmpty(queryStrSFU) ){
			if(( mStudentAdapter.getItemCount() == 0)) {
				noUsersFoundLayout.setVisibility(View.VISIBLE);
			}
			else {
				noUsersFoundLayout.setVisibility(View.GONE);
			}
		}

	}

    /**
     * remove selected students
     * @param users selected users
     */
	private void removeStudents(ArrayList<String> users) {

		ArrayList<SelectUserInfo> toDeleteUsers = new ArrayList<SelectUserInfo>();

		for ( SelectUserInfo userInfo : studentUsersList ) {
			if( users.contains(userInfo.getUserID()))  {
				toDeleteUsers.add(userInfo);
			}
		}
		studentUsersList.removeAll(toDeleteUsers);
		mStudentAdapter.notifyDataSetChanged();
        if (!queryStrSFU.isEmpty()) mStudentAdapter.getFilter().filter(queryStrSFU);
    }

    /**
     * update students list from response
     * @param allowDisallowResponse allow disallow response
     */
	private void updateStudentsList(AllowDisallowResponse allowDisallowResponse) {
		ArrayList<UserInfo> mAllowDisallowResponseList = allowDisallowResponse.getUserInfoList();
		for ( int i=0; i < mAllowDisallowResponseList.size(); i++ ) {
			UserInfo userinfo = mAllowDisallowResponseList.get(i);
			for ( SelectUserInfo mUser : studentUsersList ) {
				if(userinfo.getUserID().equals(mUser.getUserID())) {
					if ( !TextUtils.isEmpty(userinfo.getAllowMessages()) && null != userinfo.getAllowMessages() ) 
						mUser.setCanAddMessage(userinfo.getAllowMessages());
					if ( !TextUtils.isEmpty(userinfo.getAllowComments()) && null != userinfo.getAllowComments() )
						mUser.setCanComment(userinfo.getAllowComments());
				}
			}
		}
		mStudentAdapter.notifyDataSetChanged();
		if( TextUtils.isEmpty(queryStrSFU) ){
			if(( mStudentAdapter.getItemCount() == 0)) {
				noUsersFoundLayout.setVisibility(View.VISIBLE);
			}
			else {
				noUsersFoundLayout.setVisibility(View.GONE);
			}
		}

	}

    /**
     * update users privileges
     * @param selectUsersResponse
     */
	private void updatePermissions(SelectUsersResponse selectUsersResponse) {
		canManageComment = selectUsersResponse.getCanManageComment();
		canManageMessage = selectUsersResponse.getCanManageMessage();
		canRemoveSubscription = selectUsersResponse.getCanRemoveSibscription();
		mStudentAdapter.setCanManageComment(canManageComment);
		mStudentAdapter.setCanManageMessage(canManageMessage);
		mStudentAdapter.setCanRemoveSubscription(canRemoveSubscription);
		mStudentAdapter.notifyDataSetChanged();
	}

    /**
     * set search query
     * @param searchText
     */
	public void queryResult(String searchText) {
		queryStrSFU = searchText;
		if(TextUtils.isEmpty(searchText)){
			if(null != mStudentAdapter)	mStudentAdapter.removeFilter();
		}
		if(null != mStudentAdapter)		mStudentAdapter.getFilter().filter(searchText);
	}

    /**
     * Check or uncheck all users
     * @param bool check status
     */
	public void checkAll (boolean bool) {
		for (SelectUserInfo user : studentUsersList) {
			UsersActivity.checkboxState.put( user.getUserID(), bool );
			user.setUserChecked(bool ? 1 : 0);
		}
		mStudentAdapter.notifyDataSetChanged();
	}

    /**
     * checks if any user is checked
     * @return true if one or more users are selected, false otherwise
     */
	public boolean isAnyChecked() {
		for(SelectUserInfo user : studentUsersList) {
			if(user.getUserChecked() == 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAttach(Context context) {
		mContext = context;
		super.onAttach(context);
	}

    /**
     *  Starts a circular progress dialog
     */
	public static void startProgressDialog(){
		mProgressDialog = Helper.getProgressDialog(mContext, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
		if( mProgressDialog != null ) mProgressDialog.show();
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
