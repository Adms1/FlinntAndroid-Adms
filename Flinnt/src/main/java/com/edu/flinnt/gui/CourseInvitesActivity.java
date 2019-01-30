package com.edu.flinnt.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.CourseInvities;
import com.edu.flinnt.core.Invitation;
import com.edu.flinnt.core.ProfileGet;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService;
import com.edu.flinnt.protocol.AddPostResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.CourseInvitiesResponse;
import com.edu.flinnt.protocol.InvitationItem;
import com.edu.flinnt.protocol.InvitationResponse;
import com.edu.flinnt.protocol.Profile;
import com.edu.flinnt.protocol.ProfileResponse;
import com.edu.flinnt.protocol.ResendorVerifiedRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.SelectableRoundedImageView;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * GUI class to show new course invitations
 */
public class CourseInvitesActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    TextView mEmptyView;
    public Handler mHandler = null;
    ProgressDialog mProgressDialog = null;
    ArrayList<InvitationItem> mCourseInvitationItems = new ArrayList<InvitationItem>();
    CourseInvitesAdapter mCourseInvitesAdapter;
    LinearLayoutManager mLinearLayoutManager;
    private int positionToRemove = Flinnt.INVALID;
    private ResendorVerifiedRequest mProfileGetRequest;
    private ProfileResponse mProfileResponse;
    private String firstName, lastName, emailID, mobileNum, cityName = "", instituteName = "", userMessage = "", userPictureUrl = "", courseName = "", requestUserId = "";
    private int dobYear = 0, dobMonth = 0, dobDay = 0;
    private String MALE = "Male";
    private String FEMALE = "Female";
    private String genderStr = "";
    final Calendar mCalendar = Calendar.getInstance();
    private String dobFormat = "dd/MM/yyyy"; //In which you need put here
    public static final String NAME_KEY = "name";
    public static final String DESCRIPTION_KEY = "description";
    private int hasMore = 0;
    private int offset = 0;
    private int max = 20;

    BottomSheetDialog bottomSheetDialog;

    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Course> acceptedCoursesList = new ArrayList<Course>();

    private String actionInvitationID = "";
    private ImageLoader mImageLoader;
    int lineCount;
    int visibleItemCount, totalItemCount,lastVisibleItem;
    private int visibleThreshold = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Coo","onCreate() CourseInvitesActivity()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.course_invites_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mEmptyView = (TextView) findViewById(R.id.empty_text_courseinvites);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_courseinvites);
        mRecyclerView.setHasFixedSize(true);




        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Config.USER_ID)) {
                String userId = bundle.getString(Config.USER_ID);
                if (null != userId && !userId.equals(Config.getStringValue(Config.USER_ID))) {
                    Helper.setCurrentUserConfig(userId);
                }
            }
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof CourseInvitiesResponse) {
                            mCourseInvitesAdapter.setCoursePicUrl(((CourseInvitiesResponse) message.obj).getCoursePictureUrl());
                            mCourseInvitesAdapter.setmUserPicUrl(((CourseInvitiesResponse) message.obj).getUserPictureUrl());
                            mCourseInvitesAdapter.setCourseProfilePictureUrl(((CourseInvitiesResponse) message.obj).getCourseProfilePictureUrl());
                            updateInvitesList((CourseInvitiesResponse) message.obj);
                            hasMore =((CourseInvitiesResponse) message.obj).getHasMore();

                            swipeRefreshLayout.setRefreshing(false);
                        } else if (message.obj instanceof InvitationResponse) {

                            if (FlinntFirebaseMessagingService.inviteNotifications.containsKey(actionInvitationID)) {
                                int notificationId = FlinntFirebaseMessagingService.inviteNotifications.get(actionInvitationID);
                                Helper.cancelNotification(notificationId);
                                FlinntFirebaseMessagingService.inviteNotifications.remove(actionInvitationID);
                            }

                            InvitationResponse invitationResponse = ((InvitationResponse) message.obj);
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("Subscribed : " + invitationResponse.getSubscribed() + "\nRejected : " + invitationResponse.getRejected());
                            if (invitationResponse.getSubscribed() == Flinnt.TRUE && invitationResponse.getRejected() == Flinnt.INVALID) {
                                mCourseInvitesAdapter.remove(mCourseInvitesAdapter.getItem(positionToRemove));
//                    			Helper.showAlertMessage(CourseInvitesActivity.this, "Accept", "Course subscribed", "CLOSE");
                                Helper.showToast(getResources().getString(R.string.accepted_msg), Toast.LENGTH_SHORT);

                                Course course = invitationResponse.getAcceptedCourse();
                                if (!acceptedCoursesList.contains(course))
                                    acceptedCoursesList.add(course);

                                invitationResponse.setSubscribed(Flinnt.INVALID);
                            } else if (invitationResponse.getRejected() == Flinnt.TRUE && invitationResponse.getSubscribed() == Flinnt.INVALID) {
                                mCourseInvitesAdapter.remove(mCourseInvitesAdapter.getItem(positionToRemove));
//                    			Helper.showAlertMessage(CourseInvitesActivity.this, "Reject", "Course rejected", "CLOSE");
                                Helper.showToast(getResources().getString(R.string.rejected_msg), Toast.LENGTH_SHORT);
                                invitationResponse.setRejected(Flinnt.INVALID);
                            }
                            if(mCourseInvitesAdapter.getItemCount() < 1){
                                onBackPressed();
                            }
                        } else if (message.obj instanceof ProfileResponse) {
                            mProfileResponse = (ProfileResponse) message.obj;
                            fillData(mProfileResponse);
                        }


                        break;
                    case Flinnt.FAILURE:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                        stopProgressDialog();
                        if (message.obj instanceof InvitationResponse) {
                            InvitationResponse response = (InvitationResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(CourseInvitesActivity.this, "Error", response.errorResponse.getMessage(), "CLOSE");
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);

                        break;
                    default:
                        /*
                         * Pass along other messages from the UI
                         */
                        super.handleMessage(message);
                }
            }
        };


        mCourseInvitationItems = new ArrayList<InvitationItem>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mCourseInvitesAdapter = new CourseInvitesAdapter(CourseInvitesActivity.this, mCourseInvitationItems);
        mRecyclerView.setAdapter(mCourseInvitesAdapter);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                visibleItemCount = mLinearLayoutManager.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                if ( totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (hasMore == 1) {
                        offset = max + offset;
                        sendRequest();
                        hasMore = 0;
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseInvitesActivity.this);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    refreshList();
                }
            }
        });

        startProgressDialog();
        sendRequest();

        mCourseInvitesAdapter.setOnButtonClickListener(new CourseInvitesAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(View view, int position) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(CourseInvitesActivity.this);
                } else {
                    if (position >= 0 && position < mCourseInvitationItems.size()) {
                        positionToRemove = position;
                        actionInvitationID = mCourseInvitesAdapter.getItem(position).getInvitationID();
                        if (view.getId() == R.id.accept_invited_course_btn) {
                            new Invitation(mHandler, actionInvitationID, Invitation.ACCEPT, Config.getStringValue(Config.USER_ID)).sendInvitationRequest();
                            startProgressDialog();
                        } else if (view.getId() == R.id.reject_invited_course_btn) {
                            String messageType = mCourseInvitesAdapter.getItem(position).getIsRequest().equals("1") ? "request" : "invitation";
                            showRejectDialog(mCourseInvitesAdapter.getItem(position).getUserName(), messageType);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity="+Flinnt.USER_REQUEST_OR_COURSE_INVITATION+"&user="+Config.getStringValue(Config.USER_ID));
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Sends a course invitation request
     */
    private void sendRequest() {
        new CourseInvities(mHandler, offset).sendCourseInvitiesRequest();
//		startProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateInvitesList(CourseInvitiesResponse courseInvitiesResponse) {
        mCourseInvitesAdapter.addItems(courseInvitiesResponse.getInvitationList());
        mEmptyView.setVisibility((mCourseInvitesAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
    }

    /**
     * Starts a circular progress dialog
     */
    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(CourseInvitesActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(CourseInvitesActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
        }
    }

    /**
     * Stops the circular progress dialog
     */
    private void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        } finally {
            mProgressDialog = null;
        }
    }

	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_menu, menu);
		return true;
	}*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(CourseInvitesActivity.this);
                onBackPressed();
                break;

		/*case R.id.action_refresh:
            refreshList();
			break;*/

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            //NavUtils.navigateUpTo(this, upIntent);
            Intent resultIntent = new Intent();
            // accepted course
            if (acceptedCoursesList.size() > 0) {
                Bundle mBundle = new Bundle();
                mBundle.putParcelableArrayList(AddPostResponse.COURSE_KEY, acceptedCoursesList);
                resultIntent.putExtras(mBundle);
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    /**
     * Clears the data and make new request
     */
    private void refreshList() {
        //if(Helper.isConnected()){
        Requester.getInstance().cancelPendingRequests(CourseInvities.TAG);
        mCourseInvitesAdapter.clearData();
        offset = 0;
        sendRequest();
        /*}else{
            Helper.showNetworkAlertMessage(this);
		}*/
    }

    public void bottomSheetClickEvent(String userPicture, String message, String coursName, String requestUserID) {
        userPictureUrl = userPicture;
        userMessage = message;
        courseName = coursName;
        requestUserId = requestUserID;
        mProfileGetRequest = new ResendorVerifiedRequest();
        new ProfileGet(mHandler, mProfileGetRequest, requestUserId).sendProfileGetRequest();
        startProgressDialog();
//        new CustomBottomSheetUserDialogFragment().show(getSupportFragmentManager(), "Dialog");
    }

    /**
     * Fill data in views
     *
     * @param profileResponse profile response
     */
    public void fillData(ProfileResponse profileResponse) {

        mImageLoader = Requester.getInstance().getImageLoader();

        bottomSheetDialog = new BottomSheetDialog(CourseInvitesActivity.this);
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_user_profile, null);
        bottomSheetDialog.setContentView(v);
        SelectableRoundedImageView courseImage = (SelectableRoundedImageView) v.findViewById(R.id.round_image_drawer);
        TextView userNametxt = (TextView) v.findViewById(R.id.text_user_name);
        TextView instituteNametxt = (TextView) v.findViewById(R.id.text_institute_name);
        final TextView userMessagetxt = (TextView) v.findViewById(R.id.text_user_message);
        final TextView readMoreTxt = (TextView) v.findViewById(R.id.readMoreTxt);
     //   TextView userDobtxt = (TextView) v.findViewById(R.id.text_user_dob);
        TextView userCitytxt = (TextView) v.findViewById(R.id.text_user_city);
        TextView userEmailtxt = (TextView) v.findViewById(R.id.text_user_email);
        TextView userPhonetxt = (TextView) v.findViewById(R.id.text_user_phone);
        ImageView userGenderImg = (ImageView) v.findViewById(R.id.user_gender_img);
        LinearLayout userEmailLinear = (LinearLayout) v.findViewById(R.id.linear_email);
        LinearLayout userPhoneLinear = (LinearLayout) v.findViewById(R.id.linear_phone);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) v.getParent());
        mBehavior.setPeekHeight((int) getResources().getDimension(R.dimen.bottomSheetDialogHeight));

        bottomSheetDialog.show();

        Profile profile = profileResponse.getProfileList().get(profileResponse.getCount() - 1);

        mImageLoader.get(userPictureUrl, ImageLoader.getImageListener(courseImage, R.drawable.default_course_image, R.drawable.default_course_image));
        firstName = profile.getFirstName();
        lastName = profile.getLastName();
        instituteName = profile.getInstituteName();
        genderStr = profile.getGender();
        dobDay = Integer.valueOf(profile.getBirthDay());
        dobMonth = Integer.valueOf(profile.getBirthMonth());
        dobYear = Integer.valueOf(profile.getBirthYear());
        cityName = profile.getCity();
        mobileNum = profile.getMobile();
        emailID = profile.getEmail();

        userNametxt.setText(firstName + " " + lastName);
        instituteNametxt.setText(instituteName);
        if (genderStr.equalsIgnoreCase(MALE)) {
            userGenderImg.setImageResource(R.drawable.ic_male);
        } else if (genderStr.equalsIgnoreCase(FEMALE)) {
            userGenderImg.setImageResource(R.drawable.ic_female);
        } else {
            userGenderImg.setVisibility(View.GONE);
        }
        userMessagetxt.setText(userMessage);
        userCitytxt.setText(cityName);
        userEmailtxt.setText(emailID);
        userPhonetxt.setText(mobileNum);
        if (dobYear != 0 || dobDay != 0) {
            mCalendar.set(dobYear, dobMonth - 1, dobDay); // January starts from 0, not from 1
            SimpleDateFormat sdf = new SimpleDateFormat(dobFormat, Locale.US);
          //  userDobtxt.setText(sdf.format(mCalendar.getTime()));
        }

        userMessagetxt.post(new Runnable() {
            @Override
            public void run() {
                lineCount = userMessagetxt.getLineCount();
                if (LogWriter.isValidLevel(Log.DEBUG))
                    LogWriter.write("lineCount : " + lineCount);
                readMoreTxt.setVisibility(lineCount > 3 ? View.VISIBLE : View.GONE);
            }
        });

        readMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CourseInvitesActivity.this, CourseDescriptionActivity.class)
                        .putExtra(NAME_KEY, "Course Request")
                        .putExtra("ComeFrom","CourseInvitesActivity")
                        .putExtra(DESCRIPTION_KEY, userMessage));
            }
        });

        userEmailLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailID});
                email.putExtra(Intent.EXTRA_SUBJECT, firstName + " " + lastName + getResources().getString(R.string.email_subject));
                email.putExtra(Intent.EXTRA_TEXT, "Dear " + firstName + " " + lastName + ",\n\n" + getResources().getString(R.string.email_text_first_line) + courseName + ".\n\n" + getResources().getString(R.string.email_text_second_line));
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, getResources().getString(R.string.email_client)));
            }
        });

        userPhoneLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_text_first_line) + courseName + ". " + getResources().getString(R.string.email_text_second_line));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }


    public void showRejectDialog(String userName, String messageType) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        //alertDialogBuilder.setTitle("Delete Post");
        TextView titleText = new TextView(this);
        // You Can Customise your Title here
        titleText.setText("Reject " + userName + "'s " + messageType);

        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(this.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        // set dialog message
        alertDialogBuilder.setMessage(getResources().getString(R.string.conformation_msg));
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new Invitation(mHandler, actionInvitationID, Invitation.REJECT, Config.getStringValue(Config.USER_ID)).sendInvitationRequest();
                startProgressDialog();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        alertDialogBuilder.setCancelable(false);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }
    }
}
