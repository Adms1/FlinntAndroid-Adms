package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.NotificationsAdapter;
import com.edu.flinnt.database.NotificationInterface;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.NotificationDataSet;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_BROWSECOURSE_DETAIL_RATTING_TA;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_BROWSECOURSE_DETAIL_SHARE_TA;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_BROWSECOURSE_DETAIL_TA;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_BROWSECOURSE_TA;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_MYCOURSE_TA;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_QUIZ_HELP_TA;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_TYPE_CC;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_TYPE_CI;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_TYPE_LCC;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_TYPE_P;
import static com.edu.flinnt.firebase.notification.FlinntFirebaseMessagingService.NOTIFICATION_TYPE_PC;

/**
 * GUI class to show bookmarks
 */
public class NotificationListActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressDialog mProgressDialog = null;
    RecyclerView mRecyclerView;
    ArrayList<NotificationDataSet> mNotificationList;
    NotificationsAdapter mAdapter;
    SearchView mSearchView = null;
    RelativeLayout noBookmarksFoundLayout;
    private TextView mEmptyView;
    private final int NOTIFICATION_CALL_BACK = 156;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.activity_notifications);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.notification_title));

        noBookmarksFoundLayout = (RelativeLayout) findViewById(R.id.layout_no_bookmarks);

        mRecyclerView = (RecyclerView) findViewById(R.id.bookmark_post_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mEmptyView = (TextView) findViewById(R.id.empty_text_no_alert);

        mNotificationList = new ArrayList<NotificationDataSet>();

        mAdapter = new NotificationsAdapter(NotificationListActivity.this, mNotificationList);
        mAdapter.setOnItemLongClickListener(new NotificationsAdapter.OnItemLongClickListener() {

            @Override
            public void onItemLongClick(View view, int position) {
                showDeleteConfirmDialog(position);
            }
        });

        mAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //Toast.makeText(getApplicationContext(),,Toast.LENGTH_SHORT).show();

//@Nikhil 2762018


                try {
                    String jsonStr = mNotificationList.get(position).getNotificationPayload().toString();
                    //Log.d("notificaxx", jsonStr);
                    LogWriter.write("Notification Payload : " + jsonStr);

                    JSONObject obj = new JSONObject(jsonStr);
                    Intent notificationIntent = null;


                    if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase("933")) {
                        JSONObject objData = new JSONObject(obj.getString("data"));

                        Intent inapp = new Intent(NotificationListActivity.this, InAppPreviewActivity.class);
                        inapp.putExtra(Flinnt.KEY_INAPP_TITLE, objData.getString("course_name").toString());
                        inapp.putExtra(Flinnt.KEY_INAPP_URL, objData.getString("story_url").toString());
                        inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                        inapp.putExtra(Flinnt.NOTIFICATION_SCREENID, 0);
                        view.getContext().startActivity(inapp);
                    } else {
//@nikhil


                        if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_TYPE_P)) {
                            int postType = Helper.getIntegerValue(obj.getString("post_type"));
                            if (postType == 18) {
                                if (Helper.isConnected()) {
                                    Intent inapp = new Intent(NotificationListActivity.this, InAppPreviewActivity.class);
                                    inapp.putExtra(Flinnt.KEY_INAPP_TITLE, obj.getString("course_name"));
                                    inapp.putExtra(Flinnt.KEY_INAPP_URL, obj.getString("story_url"));
                                    inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                                    inapp.putExtra(Flinnt.NOTIFICATION_SCREENID, 0);
                                    view.getContext().startActivity(inapp);
                                } else {
                                    Helper.showNetworkAlertMessage(NotificationListActivity.this.getApplicationContext());
                                }
                            } else {
                                notificationIntent = new Intent(FlinntApplication.getContext(), PostDetailActivity.class);
                                notificationIntent.putExtra(Course.COURSE_ID_KEY, obj.getString("course_id"));
                                notificationIntent.putExtra(Post.POST_ID_KEY, obj.getString("post_id"));
                                notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                                notificationIntent.putExtra("isFromNotification", Flinnt.FALSE);//@CHirag:27/08/2018
                                notificationIntent.putExtra(Course.POST_TYPE, postType);
                                notificationIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, obj.getString("course_picture_url"));
                                notificationIntent.putExtra(Course.USER_PICTURE_KEY, obj.getString("course_picture"));
                                notificationIntent.putExtra(Course.COURSE_NAME_KEY, obj.getString("course_name"));
                                notificationIntent.putExtra(Course.COURSE_CAN_COMMENT, obj.getString(Course.COURSE_CAN_COMMENT));
                            }
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_TYPE_PC)) {
                            notificationIntent = new Intent(FlinntApplication.getContext(), PostDetailActivity.class);
                            notificationIntent.putExtra(Course.COURSE_ID_KEY, obj.getString("course_id"));
                            notificationIntent.putExtra(Post.POST_ID_KEY, obj.getString("post_id"));
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra("isFromNotification", Flinnt.FALSE);//@Chirag:27/08/2018
                            notificationIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, obj.getString("course_picture_url"));
                            notificationIntent.putExtra(Course.USER_PICTURE_KEY, obj.getString("course_picture"));
                            notificationIntent.putExtra(Course.COURSE_NAME_KEY, obj.getString("course_name"));
                            notificationIntent.putExtra(Course.POST_TYPE, obj.getString("post_type"));
                            notificationIntent.putExtra(Course.COURSE_CAN_COMMENT, obj.getString(Course.COURSE_CAN_COMMENT));
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_TYPE_CI)) {
                            //onCourseInvitationReceived(remoteMessage.getData());
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_TYPE_CC)) {
                            notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
                            notificationIntent.putExtra("user_id", obj.getString("user_id"));
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_TYPE_LCC)) {
                            notificationIntent = new Intent(FlinntApplication.getContext(), ContentsDetailActivity.class);
                            notificationIntent.putExtra("course_id", obj.getString("course_id"));
                            notificationIntent.putExtra("content_id", obj.getString("content_id"));
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra("isFromNotification", Flinnt.FALSE);//@Chirag:27/08/2018
                            notificationIntent.putExtra(Course.COURSE_PICTURE_URL_KEY, obj.getString("course_picture_url"));
                            notificationIntent.putExtra(Course.USER_PICTURE_KEY, obj.getString("course_picture"));
                            notificationIntent.putExtra(Course.COURSE_NAME_KEY, obj.getString("course_name"));
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_MYCOURSE_TA)) {
                            notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra("isFromNotification", Flinnt.FALSE);//@Chirag:27/08/2018
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_TA)) {
                            notificationIntent = new Intent(FlinntApplication.getContext(), MyCoursesActivity.class);
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra(Config.IS_BROWSECOURSE, true);
                            notificationIntent.putExtra("isFromNotification", Flinnt.FALSE);//@Chirag:27/08/2018
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_DETAIL_TA)) {
                            JSONObject objData = new JSONObject(obj.getString("data"));
                            notificationIntent = new Intent(FlinntApplication.getContext(), BrowseCourseDetailActivity.class);
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra(BrowsableCourse.ID_KEY, objData.getString("course_id"));
                            notificationIntent.putExtra(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE, Flinnt.FALSE);//@Chirag:27/08/2018
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_DETAIL_SHARE_TA)) {
                            JSONObject objData = new JSONObject(obj.getString("data"));
                            notificationIntent = new Intent(FlinntApplication.getContext(), BrowseCourseDetailActivity.class);
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra(BrowsableCourse.ID_KEY, objData.getString("course_id"));
                            notificationIntent.putExtra(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE, Flinnt.FALSE);//@Chirag:27/08/2018
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_BROWSECOURSE_DETAIL_RATTING_TA)) {
                            JSONObject objData = new JSONObject(obj.getString("data"));
                            notificationIntent = new Intent(FlinntApplication.getContext(), BrowseCourseDetailActivity.class);
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra(BrowsableCourse.ID_KEY, objData.getString("course_id"));
                            notificationIntent.putExtra(Flinnt.BROWSECOURSE_NOTIFICATION_TYPE, Flinnt.FALSE);//@Chirag:27/08/2018
                        } else if (mNotificationList.get(position).getNotificationType().equalsIgnoreCase(NOTIFICATION_QUIZ_HELP_TA)) {
                            JSONObject objData = new JSONObject(obj.getString("data"));
                            notificationIntent = new Intent(FlinntApplication.getContext(), QuizHelpActivty.class);

                            notificationIntent.putExtra("isFromNotification", Flinnt.TRUE);
                            notificationIntent.putExtra(Config.USER_ID, obj.getString("user_id"));
                            notificationIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, objData.getString("course_id"));
                            notificationIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, objData.getString("quiz_id"));
                            notificationIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, objData.getString("content_id"));
                            notificationIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, objData.getString("quiz_name"));
                        }
                        if (notificationIntent != null) {
                            NotificationDataSet notificationDataSet = mNotificationList.get(position);
                            notificationDataSet.setNotificationReadStatus(Flinnt.ENABLED);
                            mNotificationList.set(position, notificationDataSet);

                            NotificationInterface.getInstance().updateNotificationReadingStatus(mNotificationList.get(position).getAutoID());
                            startActivityForResult(notificationIntent, NOTIFICATION_CALL_BACK);
                        }
                    }
                } catch (Exception e) {
                    LogWriter.err(e);
                    //Log.d("notificaxx", e.getMessage());
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mNotificationList = NotificationInterface.getInstance().getNotificationsByUser();
        mAdapter.addItems(mNotificationList);
        mEmptyView.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.GONE);
    }

    public void showDeleteConfirmDialog(final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NotificationListActivity.this);
        TextView titleText = new TextView(NotificationListActivity.this);
        titleText.setText(NotificationListActivity.this.getResources().getString(R.string.notification_title));
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        alertDialogBuilder.setMessage(getResources().getString(R.string.show_delete_dialog_title));
        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int rawDeleted = 0;
                rawDeleted = NotificationInterface.getInstance().deleteNotificationForUser(mNotificationList.get(position).getAutoID());
                LogWriter.write("Notification id : " + mNotificationList.get(position).getAutoID() + " , " + rawDeleted);
                if (rawDeleted > 0) {

                    mNotificationList = NotificationInterface.getInstance().getNotificationsByUser();
                    mAdapter.clearAllData();
                    mAdapter.addItems(mNotificationList);
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() < Flinnt.TRUE) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert mSectionDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(NotificationListActivity.this)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "activity=" + Flinnt.NOTIFICATIONLIST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}