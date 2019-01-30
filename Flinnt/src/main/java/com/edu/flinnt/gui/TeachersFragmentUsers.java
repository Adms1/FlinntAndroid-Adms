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
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.EndlessRecyclerOnScrollListener;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;

/**
 * Fragment class for displaying teachers
 */
public class TeachersFragmentUsers extends Fragment {

	private ArrayList<SelectUserInfo> teacherUsersList;
	private RecyclerView mRecyclerView;
	//	public CheckBox checkBoxSelectAllTeachers;
	public static UsersAdapter mTeacherAdapter;
	static Handler mHandler;
	static ProgressDialog mProgressDialog = null;
	private String mCourseID = "";

	Button buttonBottomOpener;

	RelativeLayout noUsersFoundLayout;
	
	AllowDisallow mAllowDisallow;
	AllowDisallowRequest mAllowDisallowRequest;

	SelectUsers mSelectUsers;
	private String queryStrTFU = "";
	private static Context mContext;

	int canRemoveSubscription = Flinnt.FALSE;

	// Another constructor function, enable to pass them arguments.
	public static TeachersFragmentUsers newInstance(String courseID) {
		TeachersFragmentUsers fragment = new TeachersFragmentUsers(/*courseID*/);
		
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
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("TeachersFragmentUser onCreate courseID : " + mCourseID);
    }

	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.tab_select_teachers,container,false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_teachers);
		//		checkBoxSelectAllTeachers = (CheckBox) v.findViewById(R.id.checkBoxSelectAllTeachers);
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
						UsersActivity.userProfileUrl = ( (SelectUsersResponse) message.obj ).getUserPictureUrl();
						updateTeachersList( (SelectUsersResponse) message.obj );
						updatePermissions( (SelectUsersResponse) message.obj );
					}
					else if ( message.obj instanceof AllowDisallowResponse ) {
						AllowDisallowResponse allowDisallowResponse =  (AllowDisallowResponse)  (message.obj);
						Helper.showToast("Subscription removed successfully", Toast.LENGTH_SHORT);
						removeTeachers(allowDisallowResponse.getUsers());
						( (UsersActivity) mContext ).onChecked() ;
					}
					break;
				case Flinnt.FAILURE:
					stopProgressDialog();
					//Helper.showToast("Failure");
					if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
					if( message.obj instanceof SelectUsersResponse ) {
						SelectUsersResponse response = (SelectUsersResponse) message.obj;
						if(( mTeacherAdapter != null && mTeacherAdapter.getItemCount() == 0)) {
						noUsersFoundLayout.setVisibility(View.VISIBLE);
						}else{
							noUsersFoundLayout.setVisibility(View.GONE);
						}
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

    /**
     * remove all selected users
     * @param users selected users list
     */
	private void removeTeachers(ArrayList<String> users) {

		ArrayList<SelectUserInfo> toDeleteUsers = new ArrayList<SelectUserInfo>();

		for ( SelectUserInfo userInfo : teacherUsersList ) {
			if( users.contains(userInfo.getUserID()))  {
				toDeleteUsers.add(userInfo);
			}
		}
		teacherUsersList.removeAll(toDeleteUsers);
		mTeacherAdapter.notifyDataSetChanged();
        if (!queryStrTFU.isEmpty()) mTeacherAdapter.getFilter().filter(queryStrTFU);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);

		teacherUsersList = new ArrayList<SelectUserInfo>();

		mTeacherAdapter = new UsersAdapter(getActivity(), teacherUsersList, true );
		mRecyclerView.setAdapter(mTeacherAdapter);

		mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager, false) {

			@Override
			public void onLoadMore(int current_page) {
				if (mSelectUsers != null && TextUtils.isEmpty(queryStrTFU)) {
					if (LogWriter.isValidLevel(Log.INFO))
						LogWriter.write("onLoadMore : HasMore " + mSelectUsers.getResponse().getHasMore());
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

								for(SelectUserInfo user: teacherUsersList) {
									if(user.getUserChecked() == 1) {
										userIDs.add(user.getUserID());
										selectedUserList.add(user);
									}
								}

								mAllowDisallowRequest.setUsers(userIDs);
								mAllowDisallowRequest.setCourseID(mCourseID);

								switch(position) {

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
				bottomSheetBuilder.remove(R.id.allow_comment);
				bottomSheetBuilder.remove(R.id.disallow_comment);
				bottomSheetBuilder.remove(R.id.allow_message);
				bottomSheetBuilder.remove(R.id.disallow_message);
				if ( canRemoveSubscription == Flinnt.FALSE) {
					bottomSheetBuilder.remove(R.id.remove_from_course);
				}
				bottomSheetBuilder.show();
			}
		});

		SelectUsersRequest selectUsersRequest = new SelectUsersRequest();
		selectUsersRequest.setUserID(Config.getStringValue(Config.USER_ID));
		selectUsersRequest.setCourseID(mCourseID);
		selectUsersRequest.setRole(Flinnt.COURSE_ROLE_TEACHER);
		selectUsersRequest.setListSource(Flinnt.COURSE_USER_LIST_MYCOURSE);

		mSelectUsers = new SelectUsers(mHandler, selectUsersRequest);
		mSelectUsers.sendSelectUsersRequest();
		startProgressDialog();
	}

    /**
     * update teachers list
     * @param selectUsersResponse
     */
	private void updateTeachersList(SelectUsersResponse selectUsersResponse) {
		for (int i = 0; i < selectUsersResponse.getSelectUserInfoList().size(); i++) {
			SelectUserInfo teacherInfo = selectUsersResponse.getSelectUserInfoList().get(i);
			teacherInfo.setIsTeacher(1);
		}

		if(!TextUtils.isEmpty(queryStrTFU)){
			mTeacherAdapter.addTempItems(selectUsersResponse.getSelectUserInfoList());
		}else{
			mTeacherAdapter.addItems(selectUsersResponse.getSelectUserInfoList());
		}
		if( TextUtils.isEmpty(queryStrTFU) ){
			if(( mTeacherAdapter.getItemCount() == 0)) {
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
		canRemoveSubscription = selectUsersResponse.getCanRemoveSibscription();
		mTeacherAdapter.setCanManageComment(Flinnt.DISABLED);
		mTeacherAdapter.setCanManageMessage(Flinnt.DISABLED);
		mTeacherAdapter.setCanRemoveSubscription(canRemoveSubscription);
		mTeacherAdapter.notifyDataSetChanged();
	}

    /***
     * set search query result
     * @param searchText
     */
	public void queryResult(String searchText) {
		queryStrTFU = searchText;
		if(TextUtils.isEmpty(searchText)){
			if(null != mTeacherAdapter)	mTeacherAdapter.removeFilter();
		}
		if(null != mTeacherAdapter)		mTeacherAdapter.getFilter().filter(searchText);
	}

    /**
     * change check status of all users to checked or unchecked
     * @param bool to check or to uncheck
     */
	public void checkAll (boolean bool) {
		for (SelectUserInfo user : teacherUsersList) {
			UsersActivity.checkboxState.put( user.getUserID(), bool );
			user.setUserChecked(bool ? 1 : 0);
		}
		mTeacherAdapter.notifyDataSetChanged();
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

    /**
     * checks for users check status
     * @return true if one or more users are selected, false otherwise
     */
	public boolean isAnyChecked() {
		for(SelectUserInfo user : teacherUsersList) {
			if(user.getUserChecked() == 1) {
				return true;
			}
		}
		return false;
	}
}
