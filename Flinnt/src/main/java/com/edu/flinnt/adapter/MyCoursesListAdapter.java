package com.edu.flinnt.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.CourseReviewRead;
import com.edu.flinnt.core.InviteUsers;
import com.edu.flinnt.core.MuteSetting;
import com.edu.flinnt.gui.BrowseCourseDetailActivity;
import com.edu.flinnt.gui.CourseSettingsActivity;
import com.edu.flinnt.gui.MyCoursesActivity;
import com.edu.flinnt.gui.SelectUsersActivity;
import com.edu.flinnt.gui.SignUpActivity.validateErrorCodes;
import com.edu.flinnt.gui.UsersActivity;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseReviewReadRequest;
import com.edu.flinnt.protocol.InviteUsersRequest;
import com.edu.flinnt.protocol.MuteSettingRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.edu.flinnt.Flinnt.courseColorList;

/**
 * mContentsAdapter for course list
 */
public class MyCoursesListAdapter extends RecyclerView.Adapter<MyCoursesListAdapter.ViewHolder> {
	
	public static final String TAG = MyCoursesListAdapter.class.getSimpleName();;
	public static final String NO_OF_STUDENTS = "No of Learners: ";
	public static final String USERS = "Users: ";

    private ArrayList<Course> mDataset;
    private ArrayList<Course> filteredDataset;
	private boolean isGridView, isSearchMode;
	OnItemClickListener mItemClickListener;
	private String coursPictureUrl = Config.getStringValue(Config.COURSE_PICTURE_URL);
	private Activity mActivity;

	private RadioButton mLearnerRadio = null;
	private RadioButton mTeacherRadio = null;
	private EditText mEditEmailNumberEdt = null;
	private Button mDontBtn = null;
	private Boolean isLearnerChecked = true;
	private Boolean isTeacherChecked = false;
	private String emailNumber = null;
	private Resources res = FlinntApplication.getContext().getResources();
	private InviteUsersRequest mInviteUsersRequest = new InviteUsersRequest();
	private InviteUsers mInviteUsers;
	int inviteRole = Flinnt.COURSE_ROLE_LEARNER;
	public static final String REGEX_NUMBER = "^[0-9]*$";

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		// each data item is just a string in this case
//		public SelectableRoundedCourseImageView courseImage;
		private ImageView courseImage;

		public TextView mUnreadCountTxt;
		public TextView mCourseNameTxt;
		public TextView mSchoolNameTxt;
		public TextView mTotalUserHeaderTxt;
		public TextView mTotalUserTxt;
		public Toolbar toolbar;

		public ViewHolder(View v) {
			super(v);


			courseImage = (ImageView)v.findViewById(R.id.course_image);

			if( isGridView ) {
				courseImage.setImageResource(R.drawable.default_course_image);
	    	} else {
				courseImage.setImageResource(R.drawable.default_course_image_list);
	    	}

			mCourseNameTxt = (TextView) v.findViewById(R.id.course_name_text);
			mSchoolNameTxt = (TextView) v.findViewById(R.id.course_school_name_text);
			mTotalUserHeaderTxt = (TextView) v.findViewById(R.id.course_total_user_header_text);
			mTotalUserTxt = (TextView) v.findViewById(R.id.course_total_user_text);
			mUnreadCountTxt = (TextView) v.findViewById(R.id.course_unread_count_text);
			toolbar = (Toolbar) v.findViewById(R.id.course_toolbar);
			toolbar.inflateMenu(R.menu.course_card_menu);
			
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
		}
	}

    public void update(int position, Course item) {

		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("isSearchMode : " + isSearchMode + " position_mDataset : " + position);
//		if (isSearchMode) {
			int searchPosition = filteredDataset.indexOf(item);
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("position_filteredDataset : " + searchPosition);
			if ( searchPosition > -1 ) {
				filteredDataset.set( searchPosition, item );
                notifyItemChanged(searchPosition);
            }
//		}

		int mainPosition = -1;
		for ( Course mCourse : mDataset) {
			if (item.getCourseID().equals(mCourse.getCourseID())) {
				mainPosition = mDataset.indexOf(mCourse);
				break;
			}
		}

		if (mainPosition > -1)	{
			if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("ItemName : " + mDataset.get(mainPosition).getCourseName() + "  NewItem : " + item.getCourseName());
			mDataset.set(mainPosition, item);
		}
//		notifyItemChanged(mainPosition);
	}

	public void remove(Course item) {
        int position2 = filteredDataset.indexOf(item);
        if ( position2 > -1 ) {
            filteredDataset.remove(position2);
            notifyItemRemoved(position2);
        }
		int position = mDataset.indexOf(item);
		if ( position > -1 ) {
			mDataset.remove(position);
//			notifyItemRemoved(position);
		}
	}

    public void remove(String id) {
        for (int i = 0; i < mDataset.size() ; i++) {
            if (mDataset.get(i).getCourseID().equals(id)) {
                mDataset.remove(i);
                break;
            }
        }
        for (int i = 0; i < filteredDataset.size() ; i++) {
            if (filteredDataset.get(i).getCourseID().equals(id)) {
                filteredDataset.remove(i);
                /*if(isSearchMode)*/ notifyItemRemoved(i);
                break;
            }
        }
	}

	public void addItems(ArrayList<Course> items) {
		final int positionStart = mDataset.size()+1;
        filteredDataset.addAll(items);
		mDataset.addAll(items);
		//notifyItemRangeInserted(positionStart, items.size());
		notifyDataSetChanged();
	}
	
	public void addItem(Course item) {
		mDataset.add(item);
        filteredDataset.add(item);
		notifyDataSetChanged();
	}

	public void clearData(){
        filteredDataset.clear();
        if (!isSearchMode) mDataset.clear();
        notifyDataSetChanged();
	}

    public void updateItems(ArrayList<Course> items, ArrayList<String> offlineCourseIDs) {
        ArrayList<String> courseIDs = new ArrayList<String>();
        for( int i=0; i < items.size(); i++ ) {
            Course course = items.get(i);
            courseIDs.add(course.getCourseID());
            for( int j=0; j < mDataset.size(); j++ ) {
                if( course.getCourseID().equals( mDataset.get(j).getCourseID() ) ) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mDataset index : " + j);
                    mDataset.set(j, course);
//					notifyItemChanged(j);
                    break;
                }
            }
            for( int j=0; j < filteredDataset.size(); j++ ) {
                if( course.getCourseID().equals( filteredDataset.get(j).getCourseID() ) ) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("filteredDataset index : " + j);
                    filteredDataset.set(j, course);
                    notifyItemChanged(j);
                    break;
                }
            }
        }

        Collection<String> offlineCourseList = offlineCourseIDs;
        offlineCourseList.removeAll(courseIDs);
        if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Unsubscribed offlineCourseList size : " + offlineCourseList.size());
        for( int i=0; i < offlineCourseIDs.size(); i++ ) {
            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Unsubscribed offline Course ID : " + offlineCourseIDs.get(i));
            for( int j=0; j < mDataset.size(); j++ ) {
                if( offlineCourseIDs.get(i).equals( mDataset.get(j).getCourseID() ) ) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("mDataset Unsubscribed index : " + j);
                    mDataset.remove(j);
//					notifyItemRemoved(j);
                    break;
                }
            }
            for( int j=0; j < filteredDataset.size(); j++ ) {
                if( offlineCourseIDs.get(i).equals( filteredDataset.get(j).getCourseID() ) ) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("filteredDataset Unsubscribed index : " + j);
                    filteredDataset.remove(j);
                    notifyItemRemoved(j);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }
	
	// Provide a suitable constructor (depends on the kind of dataset)
	public MyCoursesListAdapter(ArrayList<Course> myDataset, boolean isGrid, Activity activity) {
        isGridView = isGrid;

        filteredDataset = new ArrayList<>();
        mDataset = new ArrayList<>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);

		mActivity = activity;
	}
	
	public Course getItem(int position) {
		if ( position >= 0 && position < filteredDataset.size() ) {
			return filteredDataset.get(position);
		}
		else return null;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if( isGridView ) {
			v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.alert_list_item_new, parent, false);
		}
		else {
			v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.my_courses_cardview_list_item, parent, false);
		}
		
        return new ViewHolder(v);
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final Course course = filteredDataset.get(position);
		//Log.d("appcallll","id=>"+course.getCourseID());
		//Log.d("appcallll","id=>"+course.getCourseName());
		if( null != course ) {
			updateCourseMenu(holder.toolbar.getMenu(), course);

			String url = coursPictureUrl + Flinnt.COURSE_MEDIUM + File.separator + course.getCoursePicture();
			Glide.with(mActivity)
					.load(url)
					.placeholder(getRandomDrawbleColor())
					.bitmapTransform(new RoundedCornersTransformation(mActivity,0, 0))
					.into(holder.courseImage);

			if(course.getPublicType().equalsIgnoreCase(Flinnt.COURSE_TYPE_TIMEBOUND)){
				holder.mTotalUserHeaderTxt.setVisibility(View.GONE);
				holder.mTotalUserTxt.setText( course.getEventDateTime() ); // to display event date and time
				holder.mTotalUserTxt.setTextSize((float) 11.25);
			}else {
				holder.mTotalUserHeaderTxt.setVisibility(View.VISIBLE);
				holder.mTotalUserHeaderTxt.setText( USERS );
				holder.mTotalUserTxt.setText( course.getTotalUsers() );
				holder.mTotalUserHeaderTxt.setTextSize(13);
				holder.mTotalUserTxt.setTextSize(13);
			}


			holder.mCourseNameTxt.setText( course.getCourseName() );
			holder.mSchoolNameTxt.setText("by "+ course.getUserSchoolName() ); // to display school name
			
			if( course.getUnreadPostCount() > 0 ) {
				holder.mUnreadCountTxt.setVisibility( View.VISIBLE );
				holder.mUnreadCountTxt.setText( course.getUnreadPost() +"    ");
			}
			else {
				holder.mUnreadCountTxt.setVisibility( View.GONE );
			}
			
			if (holder.toolbar != null) {
				holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						switch (menuItem.getItemId()) {
							case R.id.course_card_menu_mute:
								if(Helper.isConnected()){
									MuteSettingRequest mMuteSettingRequest = new MuteSettingRequest();
									mMuteSettingRequest.setCourseID(course.getCourseID());
									mMuteSettingRequest.setMuteComment("");
									mMuteSettingRequest.setMuteSound("");
									MuteSetting mMuteSetting = new MuteSetting(((MyCoursesActivity) mActivity).mHandler, mMuteSettingRequest, MuteSetting.COURSE_SETTING);
									((MyCoursesActivity) mActivity).muteSettingRequestType = MuteSetting.GET_SETTING;
									mMuteSetting.setRequestType( MuteSetting.GET_SETTING);
							        mMuteSetting.sendMuteSettingRequest();
							        ((MyCoursesActivity) mActivity).startProgressDialog();
								}else{
									Helper.showNetworkAlertMessage(mActivity);
								}


							break;

							case R.id.course_card_menu_give_ratting:
								if (Helper.isConnected()) {
									LogWriter.write("Course Id :" + course.getCourseID());

									MyCoursesActivity.RATING_COURSE_ID = course.getCourseID();

									CourseReviewReadRequest mCourseReviewReadRequest = new CourseReviewReadRequest();
									mCourseReviewReadRequest.setCourseId(course.getCourseID());
									mCourseReviewReadRequest.setUserId(Config.USER_ID);

									CourseReviewRead mCourseReviewRead = new CourseReviewRead(((MyCoursesActivity) mActivity).mHandler, course.getCourseID());
									mCourseReviewRead.sendCourseReadRequest();
									((MyCoursesActivity) mActivity).startProgressDialog();

								} else {
									Helper.showNetworkAlertMessage(((MyCoursesActivity) mActivity));
								}
								break;

							case R.id.course_card_menu_course_info:
								if (Helper.isConnected()) {
									Intent courseDescriptionIntent = new Intent(((MyCoursesActivity) mActivity), BrowseCourseDetailActivity.class);
									courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, course.getCourseID());
									courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, course.getCoursePicture());
									courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, course.getCourseName());
									((MyCoursesActivity) mActivity).startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
								} else {
									Helper.showNetworkAlertMessage(((MyCoursesActivity) mActivity));
								}
								break;

							case R.id.course_card_menu_invite_users:

								if(!TextUtils.isEmpty(course.getAllowedRoles())){

									List<String> radioItems = new ArrayList<String>();
									String[] rItems = course.getAllowedRoles().split(",");
									for (int i = 0; i < rItems.length ; i++) {
										int role = Integer.parseInt(rItems[i]);
										if(role == Flinnt.COURSE_ROLE_TEACHER){
											radioItems.add("Teacher");
										}else if(role == Flinnt.COURSE_ROLE_LEARNER){
											radioItems.add("Learner");
										}
									}

									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
									// set view
									LayoutInflater inflater = mActivity.getLayoutInflater();
									View dialogView = inflater.inflate(R.layout.invite_dialog_design, null);
									alertDialogBuilder.setView(dialogView);

									TextView title = new TextView(mActivity);
									title.setText("Invite User");
									title.setPadding(40, 40, 40, 40);
									title.setGravity(Gravity.CENTER_VERTICAL);
									title.setTextColor(mActivity.getResources().getColor(R.color.ColorPrimary));
									title.setTextSize(20);
									title.setTypeface(Typeface.DEFAULT_BOLD);
									alertDialogBuilder.setCustomTitle(title);
									alertDialogBuilder.setPositiveButton("INVITE", null);

									final AlertDialog inviteDialog = alertDialogBuilder.create();
									inviteDialog.show();

									RelativeLayout radioLearnerLayout = (RelativeLayout) inviteDialog.findViewById(R.id.radio_learner_layout);
									RelativeLayout radioTeacherLayout = (RelativeLayout) inviteDialog.findViewById(R.id.radio_teacher_layout);

									mLearnerRadio = (RadioButton) inviteDialog.findViewById(R.id.radio_learners);
									mTeacherRadio = (RadioButton) inviteDialog.findViewById(R.id.radio_teachers);

									if(radioItems.contains("Teacher")){
										radioTeacherLayout.setVisibility(View.VISIBLE);
										mTeacherRadio.setChecked(isTeacherChecked);
									}else{
										radioTeacherLayout.setVisibility(View.GONE);
									}

									if(radioItems.contains("Learner")){
										radioLearnerLayout.setVisibility(View.VISIBLE);
										mLearnerRadio.setChecked(isLearnerChecked);
									}else{
										radioLearnerLayout.setVisibility(View.GONE);
									}

									mEditEmailNumberEdt = (EditText) inviteDialog.findViewById(R.id.et_review);
									mEditEmailNumberEdt.setTextColor(Color.DKGRAY);
									changeColor();

									mLearnerRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
										@Override
										public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
											mLearnerRadio.setChecked(isChecked);
											mTeacherRadio.setChecked(!isChecked);
											inviteRole = isChecked ? Flinnt.COURSE_ROLE_LEARNER : Flinnt.COURSE_ROLE_TEACHER;
											changeColor();
										}
									});
									mTeacherRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
										@Override
										public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
											mTeacherRadio.setChecked(isChecked);
											mLearnerRadio.setChecked(!isChecked);
											inviteRole = isChecked ? Flinnt.COURSE_ROLE_TEACHER : Flinnt.COURSE_ROLE_LEARNER;
											changeColor();
										}
									});

									mDontBtn = inviteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
									mDontBtn.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											isLearnerChecked = mLearnerRadio.isChecked();
											isTeacherChecked = mTeacherRadio.isChecked();
											emailNumber = mEditEmailNumberEdt.getText().toString();

											if(isValidEmailorNumber(emailNumber) == validateErrorCodes.VALID_FIELD) {

												((MyCoursesActivity) mActivity).invited_email_mob = emailNumber;

												mInviteUsersRequest.setUserId(Config.getStringValue(Config.USER_ID));
												mInviteUsersRequest.setCourseID(course.getCourseID());
												mInviteUsersRequest.setInviteRole(inviteRole);
												mInviteUsersRequest.setSendTo(emailNumber);

												mInviteUsers = new InviteUsers(((MyCoursesActivity) mActivity).mHandler);
												mInviteUsers.setInviteUsersRequest(mInviteUsersRequest);
												mInviteUsers.sendInviteUsersRequest();
												inviteDialog.dismiss();
												((MyCoursesActivity) mActivity).startProgressDialog();
											}
											else {
												Helper.showAlertMessage(mActivity, "Invite", res.getString(R.string.validate_email_mobile), "Close");
											}
										}
									});
									inviteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mActivity.getResources().getColor(R.color.ColorPrimary));
								}

								break;

							case R.id.course_card_menu_settings:
								
							Intent courseSettingIntent = new Intent(mActivity, CourseSettingsActivity.class);
							courseSettingIntent.putExtra(Course.COURSE_ID_KEY, course.getCourseID());
							mActivity.startActivity(courseSettingIntent);
							
							break;
							case R.id.course_card_menu_users:
								
								if(!TextUtils.isEmpty(course.getAllowedRoles())){
									CharSequence tabTitle[];
									List<String> tabItems = new ArrayList<String>();
									String[] items = course.getAllowedRoles().split(",");
									for (int i = 0; i < items.length ; i++) {
										int role = Integer.parseInt(items[i]);
										if(role == Flinnt.COURSE_ROLE_TEACHER){
											tabItems.add("Teachers");
										}else if(role == Flinnt.COURSE_ROLE_LEARNER){
											tabItems.add("Learners");
										}
									}
									tabTitle = tabItems.toArray(new CharSequence[tabItems.size()]);
									
									Intent userIntent = new Intent(mActivity, UsersActivity.class);
									userIntent.putExtra(Course.COURSE_ID_KEY, course.getCourseID());
									userIntent.putExtra(SelectUsersActivity.TAB_TITLE, tabTitle);
									mActivity.startActivity(userIntent);
								}
								
							break;
                            /*case R.id.course_card_menu_details:
                                if (Helper.isConnected()) {
                                    Intent courseDescriptionIntent = new Intent(mActivity, BrowseCourseDetailActivityNew.class);
                                    courseDescriptionIntent.putExtra(BrowsableCourse.ID_KEY, course.getCourseID());
                                    courseDescriptionIntent.putExtra(BrowsableCourse.PICTURE_KEY, course.getCoursePicture());
                                    courseDescriptionIntent.putExtra(BrowsableCourse.NAME_KEY, course.getCourseName());
                                    courseDescriptionIntent.putExtra(BrowsableCourse.INSTITUTE_NAME_KEY, course.getCourseCommmunity());
                                    courseDescriptionIntent.putExtra(BrowsableCourse.USER_COUNT_KEY, course.getTotalUsersCount());
                                    mActivity.startActivityForResult(courseDescriptionIntent, MyCoursesActivity.BROWSE_COURSE_SUBSCRIBE_CALLBACK);
                                } else {
                                    Helper.showNetworkAlertMessage(mActivity);
                                }

                            break;*/
							default:
							break;
						}
						return true;
					}
				});
			}
			

		}
	}

	protected void changeColor() {
		final int blue = Color.parseColor("#007DCD");

		if(mLearnerRadio.isChecked()){
			mLearnerRadio.setTextColor(blue);
			mTeacherRadio.setTextColor(Color.BLACK);
		}
		if(mTeacherRadio.isChecked()) {
			mTeacherRadio.setTextColor(blue);
			mLearnerRadio.setTextColor(Color.BLACK);
		}

	}
	
	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return filteredDataset.size();
	}

    /**
     * Set search results list and display it
     * @param queryText search query
     */
    public void setFilter(String queryText) {

		isSearchMode = true;
        filteredDataset = new ArrayList<Course>();
        queryText = queryText.toString().toLowerCase();
        for(Course item: mDataset) {
            if( item.getCourseName().toLowerCase().contains(queryText) || item.getUserSchoolName().toLowerCase().contains(queryText)  ) {
            	filteredDataset.add(item);
            }
        }
       notifyDataSetChanged();
    }
	
    public boolean getSearchMode() {
    	return isSearchMode;
    }
    
    public void setSearchMode(boolean mode) {
    	isSearchMode = mode;
    	filteredDataset = new ArrayList<Course>();
    }
    
    public void addSearchedItems(ArrayList<Course> items) {
    	filteredDataset.addAll(items);
    	notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
    	isSearchMode = false;
    	filteredDataset = new ArrayList<Course>();
    	filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
    }
    
	private void updateCourseMenu(Menu menu, Course course) {
		MenuItem courseInfoItem = menu.findItem(R.id.course_card_menu_course_info);
		MenuItem muteItem = menu.findItem(R.id.course_card_menu_mute);
		MenuItem giveRattingItem = menu.findItem(R.id.course_card_menu_give_ratting);

		MenuItem inviteUserItem = menu.findItem(R.id.course_card_menu_invite_users);
		MenuItem settingsItem = menu.findItem(R.id.course_card_menu_settings);
		MenuItem usersItem = menu.findItem(R.id.course_card_menu_users);

		if( course.getAllowCourseInfo().equals(Flinnt.ENABLED)  ) {
			courseInfoItem.setVisible(true);
		} else {
			courseInfoItem.setVisible(false);
		}

		if( course.getAllowMute().equals(Flinnt.ENABLED)  ) {
			muteItem.setVisible(true);
		} else {
			muteItem.setVisible(false);
		}

		if( course.getAllowRateCourse().equals(Flinnt.ENABLED)  ) {
			giveRattingItem.setVisible(true);
		} else {
			giveRattingItem.setVisible(false);
		}

		/*if( course.getAllowUnsubscribe().equals(Flinnt.ENABLED)  ) {
			unsubscribeItem.setVisible(true);
		} else {
			unsubscribeItem.setVisible(false);
		}*/

		if( course.getAllowInviteUsers().equals(Flinnt.ENABLED)  ) {
			inviteUserItem.setVisible(true);
			usersItem.setVisible(true);
		} else {
			inviteUserItem.setVisible(false);
			usersItem.setVisible(false);
		}
		
		if( course.getAllowChangeSettings().equals(Flinnt.ENABLED)  ) {
			settingsItem.setVisible(true);
		} else {
			settingsItem.setVisible(false);
		}
		
		/*if( !TextUtils.isEmpty(course.getAllowedRoles()) ) {
			usersItem.setVisible(true);
		} else {
			usersItem.setVisible(false);
		}*/
	}
    
    /**
     * Check if the entered email or number is valid or not
     * @return true if valid, false otherwise
     */
    private int isValidEmailorNumber(String emailMobile){
		int ret = validateErrorCodes.NOT_VALIDATE_EMAIL_MOBILE;
		if( emailMobile.matches(REGEX_NUMBER) ){
			if(emailMobile.length() == 10){
				return validateErrorCodes.VALID_FIELD;
			}else{
				return validateErrorCodes.NOT_VALID_NUMBER;
			}
		}else if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailMobile).matches()){
			return validateErrorCodes.VALID_FIELD;
		}
		return ret;
	}
    
	public String getCoursPictureUrl() {
		return coursPictureUrl;
	}

	public void setCoursPictureUrl(String coursPictureUrl) {
		this.coursPictureUrl = coursPictureUrl;
	}
	
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
	public ColorDrawable getRandomDrawbleColor() {
		int idx = new Random().nextInt(courseColorList.length);
		return courseColorList[idx];
	}
}