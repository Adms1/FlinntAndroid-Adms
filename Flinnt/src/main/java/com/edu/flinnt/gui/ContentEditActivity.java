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
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.AddSection;
import com.edu.flinnt.core.ContentsEditList;
import com.edu.flinnt.protocol.AddContentResponse;
import com.edu.flinnt.protocol.AddSectionRequest;
import com.edu.flinnt.protocol.AddSectionResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.Content;
import com.edu.flinnt.protocol.ContentsDeleteResponse;
import com.edu.flinnt.protocol.ContentsEditResponse;
import com.edu.flinnt.protocol.ContentsShowHideResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.SectionDeleteResponse;
import com.edu.flinnt.protocol.SectionShowHideResponse;
import com.edu.flinnt.protocol.Sections;
import com.edu.flinnt.protocol.SendContentNotificationResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by flinnt-android-2 on 16/11/16.
 * Activity open when user click on edit icon from content list view.
 */

public class ContentEditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ContentEditActivity.class.getSimpleName();

    public Handler mHandler = null;
    public ProgressDialog mProgressDialog = null;
    private ContentsEditList mContentsEditList;
    private ContentsEditResponse mContentsEditResponse;
    private ContentsShowHideResponse mContentsShowHideResponse;
    private ContentsDeleteResponse mContentsDeleteResponse;
    private SectionShowHideResponse mSectionShowHideResponse;
    private SectionDeleteResponse mSectionDeleteResponse;
    private ContentEditAdapter mContentEditAdapter;
    private RecyclerView contentRecycle;
    private String mCourseId = "", isPublic = "";
    private ArrayList<Sections> mSectionList = new ArrayList<Sections>();
    private String mCourseName = "";
    TextView emptyTxt;
    LinearLayout tooltipEditContentLinear;

    public static final int ADD_CONTENT = 801;
    public static final int EDIT_CONTENT = 802;
    public static final String CONTENT_DATA = "data";
    public static int copyContentSectionPosition;

    int mSectionAddEdit;
    int mSectionPosition;
    Button btnShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.content_edit_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.edit_contents_title));

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            if (bundle.containsKey(Course.COURSE_ID_KEY))
                mCourseId = bundle.getString(Course.COURSE_ID_KEY);
            if (bundle.containsKey(Course.COURSE_NAME_KEY))
                mCourseName = bundle.getString(Course.COURSE_NAME_KEY);
            if (bundle.containsKey(BrowsableCourse.IS_PUBLIC_KEY))
                isPublic = bundle.getString(BrowsableCourse.IS_PUBLIC_KEY);
        }

        contentRecycle = (RecyclerView) findViewById(R.id.content_recycle);
        contentRecycle.setNestedScrollingEnabled(false);
        emptyTxt = (TextView) findViewById(R.id.empty_text);
        tooltipEditContentLinear = (LinearLayout) findViewById(R.id.tooltip_edit_content_linear);

        btnShow = new Button(this);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflowPrompt(v);
            }
        });


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("SUCCESS_RESPONSE : " + msg.obj.toString());
                        if (msg.obj instanceof ContentsEditResponse) {
                            mContentsEditResponse = (ContentsEditResponse) msg.obj;
                            updateContentsEditList(mContentsEditResponse);
                            if (!Config.getToolTipValue(Config.EDIT_CONTENT_PROMPT_DIALOG + Config.getStringValue(Config.USER_ID)).equals("1")) {
                                btnShow.performClick();
                            }

                        } else if (msg.obj instanceof ContentsShowHideResponse) {
                            LogWriter.write("ContentShowHide : " + msg.obj.toString());
                            mContentsShowHideResponse = (ContentsShowHideResponse) msg.obj;
                            hideShowContentsEditList(mContentsShowHideResponse);
                        } else if (msg.obj instanceof ContentsDeleteResponse) {
                            LogWriter.write("ContentDelete : " + msg.obj.toString());
                            mContentsDeleteResponse = (ContentsDeleteResponse) msg.obj;
                            deleteContentsEditList(mContentsDeleteResponse);
                        } else if (msg.obj instanceof SectionShowHideResponse) {
                            LogWriter.write("SectionShowHide : " + msg.obj.toString());
                            mSectionShowHideResponse = (SectionShowHideResponse) msg.obj;
                            hideShowSectionEditList(mSectionShowHideResponse);
                        } else if (msg.obj instanceof SectionDeleteResponse) {
                            LogWriter.write("SectionDelete : " + msg.obj.toString());
                            mSectionDeleteResponse = (SectionDeleteResponse) msg.obj;
                            deleteSectionEditList(mSectionDeleteResponse);
                        } else if (msg.obj instanceof SendContentNotificationResponse) {
                            LogWriter.write("SendContentNotificationResponse : " + msg.obj.toString());
                            mContentEditAdapter.updateNotificationStatus();
                            Helper.showToast(getResources().getString(R.string.send_notification_msg), Toast.LENGTH_SHORT);
                        } else if (msg.obj instanceof AddSectionResponse) {
                            LogWriter.write("AddSection : " + msg.obj.toString());
                            Sections mSection = ((AddSectionResponse) msg.obj).getData().getSection();
                            if (mSectionAddEdit == Flinnt.SECTION_ADD) {
                                mContentEditAdapter.addSection(mSection);
                                mContentEditAdapter.notifyDataSetChanged();
                                if (mContentEditAdapter.getItemCount() > 0) {
                                    emptyTxt.setVisibility(View.GONE);
                                }
                            } else {
                                mContentEditAdapter.editSection(mSection, mSectionPosition);
                                mContentEditAdapter.notifyDataSetChanged();
                            }
                        } else if (msg.obj instanceof AddContentResponse) {
                            AddContentResponse mAddCopntentResponse = (AddContentResponse) msg.obj;
                            Content responseAdd = mAddCopntentResponse.getData().getContent();
                            mContentEditAdapter.addContent(copyContentSectionPosition, responseAdd);
                            mContentEditAdapter.notifyDataSetChanged();
                            Helper.showToast(getResources().getString(R.string.content_copy), Toast.LENGTH_LONG);
                        }

                        break;
                    case Flinnt.FAILURE:
                        try {
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("FAILURE_RESPONSE : " + msg.obj.toString());
                            if (msg.obj instanceof ContentsShowHideResponse) {
                                mContentsShowHideResponse = (ContentsShowHideResponse) msg.obj;
                                Helper.showAlertMessage(ContentEditActivity.this, getString(R.string.error), mContentsShowHideResponse.errorResponse.getMessage(), "Close");

                            }
                            if (msg.obj instanceof ContentsDeleteResponse) {
                                mContentsDeleteResponse = (ContentsDeleteResponse) msg.obj;
                                Helper.showAlertMessage(ContentEditActivity.this, getString(R.string.error), mContentsDeleteResponse.errorResponse.getMessage(), "Close");
                            }
                            if (msg.obj instanceof SectionShowHideResponse) {
                                mSectionShowHideResponse = (SectionShowHideResponse) msg.obj;
                                Helper.showAlertMessage(ContentEditActivity.this, getString(R.string.error), mSectionShowHideResponse.errorResponse.getMessage(), "Close");

                            }
                            if (msg.obj instanceof SectionDeleteResponse) {
                                mSectionDeleteResponse = (SectionDeleteResponse) msg.obj;
                                Helper.showAlertMessage(ContentEditActivity.this, getString(R.string.error), mSectionDeleteResponse.errorResponse.getMessage(), "Close");
                            }

                            if (msg.obj instanceof SendContentNotificationResponse) {
                                Helper.showAlertMessage(ContentEditActivity.this, getString(R.string.error), ((SendContentNotificationResponse) msg.obj).errorResponse.getMessage(), "Close");
                            }

                            if (msg.obj instanceof AddContentResponse) {
                                Helper.showAlertMessage(ContentEditActivity.this, getString(R.string.error), ((AddContentResponse) msg.obj).errorResponse.getMessage(), "Close");
                            }

                        } catch (Exception e) {
                            LogWriter.err(e);
                        }

                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        if (null == mContentsEditList) {
            mContentsEditList = new ContentsEditList(mHandler, mCourseId);
            mContentsEditList.sendContentsEditListRequest();
            startProgressDialog();
        }
        refreshView();

    }

    private void refreshView() {
        try {
            mContentEditAdapter = new ContentEditAdapter(ContentEditActivity.this, mCourseId, mCourseName, mSectionList);
            GridLayoutManager manager = new GridLayoutManager(ContentEditActivity.this, 1);
            contentRecycle.setLayoutManager(manager);
            mContentEditAdapter.setLayoutManager(manager);
            contentRecycle.setAdapter(mContentEditAdapter);
            tooltipEditContentLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tooltipEditContentLinear.setVisibility(View.GONE);
                    Config.setToolTipValue(Config.FLINNT_TOOLTIP_EDIT_CONTENT + Config.getStringValue(Config.USER_ID), "1");
                }
            });

        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.content_add_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_section:
                showAddSection(Flinnt.SECTION_ADD, null, Flinnt.INVALID);
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
            MyCommFun.sendTracker(this, "activity=" + Flinnt.EDIT_CONTENT + "&user=" + Config.getStringValue(Config.USER_ID) + "&course=" + mCourseId);
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
     * Starts a circular progress dialog
     */
    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(ContentEditActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(ContentEditActivity.this, "Login", "Please wait...", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !mProgressDialog.isShowing()) mProgressDialog.show();
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


    /**
     * Update and display course list
     *
     * @param mContentsEditResponse cource course list response
     */
    public void updateContentsEditList(ContentsEditResponse mContentsEditResponse) {
        try {
            if (mContentsEditResponse.getData().getList().size() > 0) {
                LogWriter.write("Current Count: " + mContentsEditResponse.getData().getList().size());
                mContentEditAdapter.addItems(mContentsEditResponse.getData().getList(), mContentsEditResponse);
                emptyTxt.setVisibility(View.GONE);
                if (Config.getToolTipValue(Config.FLINNT_TOOLTIP_EDIT_CONTENT + Config.getStringValue(Config.USER_ID)).equals("1")) {
                    tooltipEditContentLinear.setVisibility(View.GONE);
                } else {
                    tooltipEditContentLinear.setVisibility(View.VISIBLE);
                }
            } else {
                emptyTxt.setVisibility(View.VISIBLE);
                tooltipEditContentLinear.setVisibility(View.GONE);
            }

            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("ItemCount : " + mContentEditAdapter.getItemCount());

        } catch (Exception e) {
            LogWriter.err(e);
        }
        mContentEditAdapter.notifyDataSetChanged();
    }

    public void hideShowContentsEditList(ContentsShowHideResponse mContentsShowHideResponse) {
        try {
            if (mContentsShowHideResponse.getData().getContent().getContentVisible().equals(Flinnt.ENABLED)) {
                mContentEditAdapter.contentShow();
                mContentEditAdapter.notifyDataSetChanged();
            } else {
                mContentEditAdapter.contentHide();
                mContentEditAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }
    /**
     * Delete content item from edit list
     */
    public void deleteContentsEditList(ContentsDeleteResponse mContentsDeleteResponse) {
        try {
            if (mContentsDeleteResponse.getData().getContent().getDeleted().equals(Flinnt.ENABLED)) {
                mContentEditAdapter.removeContent();
                mContentEditAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Hide and show section item from edit list
     */

    public void hideShowSectionEditList(SectionShowHideResponse mSectionShowHideResponse) {
        try {
            if (mSectionShowHideResponse.getData().getSection().getSectionVisible().equals(Flinnt.ENABLED)) {
                mContentEditAdapter.sectionShow();
                mContentEditAdapter.notifyDataSetChanged();
            } else {
                mContentEditAdapter.sectionHide();
                mContentEditAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Delete section item from edit list
     */
    public void deleteSectionEditList(SectionDeleteResponse mSectionDeleteResponse) {
        try {
            if (mSectionDeleteResponse.getData().getSection().getDeleted().equals(Flinnt.ENABLED)) {
                mContentEditAdapter.removeSection();
                mContentEditAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    /**
     * Displays the Add Section dialog
     *
     * @param sectionAddEdit
     * @param section
     * @param contentSectionPosition
     */
    public void showAddSection(final int sectionAddEdit, final Sections section, int contentSectionPosition) {
        mSectionAddEdit = sectionAddEdit;
        mSectionPosition = contentSectionPosition;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContentEditActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_section_dialog, null);
        alertDialogBuilder.setView(dialogView);

        TextView title = new TextView(this);
        if (mSectionAddEdit == Flinnt.SECTION_ADD) {
            title.setText(R.string.add_new_section);
            alertDialogBuilder.setPositiveButton(getString(R.string.add), null);
            alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button), null);
        } else {
            title.setText(R.string.edit_section);
            alertDialogBuilder.setPositiveButton(getString(R.string.edit_txt), null);
            alertDialogBuilder.setNegativeButton(getString(R.string.cancel_button), null);
        }
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(getResources().getColor(R.color.ColorPrimary));
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(title);


        final AlertDialog AddSectionDialog = alertDialogBuilder.create();
        AddSectionDialog.show();
        AddSectionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));
        AddSectionDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.ColorPrimary));

        final EditText sectionNameEdt = (EditText) AddSectionDialog.findViewById(R.id.section_name_edt);
        Button btnSubmit = AddSectionDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button btnCancel = AddSectionDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (mSectionAddEdit == Flinnt.SECTION_EDIT) {
            sectionNameEdt.setText(section.getTitle());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(ContentEditActivity.this);
                } else {
                    if (sectionNameEdt.getText().toString().trim().length() > 1) {
                        if (mSectionAddEdit == Flinnt.SECTION_ADD) {
                            AddSectionRequest addSectionRequest = new AddSectionRequest();
                            addSectionRequest.setUserID(Config.getStringValue(Config.USER_ID));
                            addSectionRequest.setCourseID(mCourseId);
                            addSectionRequest.setTitle(sectionNameEdt.getText().toString());
                            new AddSection(mHandler, addSectionRequest, Flinnt.SECTION_ADD).sendAddSectionRequest();

                        } else {
                            AddSectionRequest editSectionRequest = new AddSectionRequest();
                            editSectionRequest.setUserID(Config.getStringValue(Config.USER_ID));
                            editSectionRequest.setCourseID(mCourseId);
                            editSectionRequest.setTitle(sectionNameEdt.getText().toString());
                            editSectionRequest.setSectionID(section.getId());
                            new AddSection(mHandler, editSectionRequest, Flinnt.SECTION_EDIT).sendAddSectionRequest();
                        }
                        startProgressDialog();
                        AddSectionDialog.cancel();
                    } else {
                        Helper.showToast(getString(R.string.section_name_validation), Toast.LENGTH_LONG);
                    }

                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSectionDialog.cancel();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_more_txt:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void showOverflowPrompt(View view) {
        final MaterialTapTargetPrompt.Builder tapTargetPromptBuilder = new MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText(R.string.add_section)
                .setSecondaryText(getResources().getString(R.string.add_section_descr_msg))
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.tap_target_menu_max_width)
                .setIcon(R.drawable.ic_plus);
        final Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar);
        final View child = tb.getChildAt(2);
        if (child instanceof ActionMenuView) {
            final ActionMenuView actionMenuView = ((ActionMenuView) child);
            tapTargetPromptBuilder.setTarget(actionMenuView.getChildAt(actionMenuView.getChildCount() - 1));
        } else {

        }
        //@Nikhil 20062018

//        tapTargetPromptBuilder.setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
//            @Override
//            public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
//            }
//
//            @Override
//            public void onHidePromptComplete() {
//                Config.setToolTipValue(Config.EDIT_CONTENT_PROMPT_DIALOG + Config.getStringValue(Config.USER_ID), "1");
//            }
//        });

        //@Nikhil 20062018
        tapTargetPromptBuilder.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                if (state == MaterialTapTargetPrompt.STATE_FINISHED) {
                    Config.setToolTipValue(Config.EDIT_CONTENT_PROMPT_DIALOG + Config.getStringValue(Config.USER_ID), "1");
                }
            }
        });

        tapTargetPromptBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LogWriter.isValidLevel(Log.ERROR))
            LogWriter.write("Request code : " + requestCode + ", Result code : " + resultCode + "\nData Uri : " + data);

        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case ContentEditActivity.ADD_CONTENT:
                        Content responseAdd = (Content) data.getSerializableExtra(ContentEditActivity.CONTENT_DATA);
                        String postionAdd = data.getStringExtra(AddFileActivity.SECTION_POSTITION);
                        mContentEditAdapter.addContent(Integer.parseInt(postionAdd), responseAdd);
                        mContentEditAdapter.notifyDataSetChanged();
                        break;

                    case ContentEditActivity.EDIT_CONTENT:
                        Content responseEdit = (Content) data.getSerializableExtra(ContentEditActivity.CONTENT_DATA);
                        String postionEdit = data.getStringExtra(AddFileActivity.SECTION_POSTITION);
                        String postionContentEdit = data.getStringExtra(AddFileActivity.CONTENT_POSTITION);
                        mContentEditAdapter.editContent(Integer.parseInt(postionEdit), Integer.parseInt(postionContentEdit), responseEdit);
                        mContentEditAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
