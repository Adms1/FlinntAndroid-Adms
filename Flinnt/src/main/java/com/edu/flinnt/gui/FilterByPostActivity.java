package com.edu.flinnt.gui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * GUI class to show post type filters
 */
public class FilterByPostActivity extends AppCompatActivity {

    public static final String TAG = FilterByPostActivity.class.getSimpleName();

    Toolbar mToolbar;
    String courseID = "", coursePicture = "", courseName = "", courseAllowedRoles = "";
    public static final String FILTER_POST_CONTENT_TYPE = "post_content_type";
    public static final String FILTER_POST_TYPE = "post_type";
    public static final String FILTER_BY = "filter_by";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        setContentView(R.layout.post_tags_list);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            courseID = bundle.getString(Course.COURSE_ID_KEY);
            coursePicture = bundle.getString(Course.COURSE_PICTURE_KEY);
            courseName = bundle.getString(Course.COURSE_NAME_KEY);
            courseAllowedRoles = bundle.getString(Course.ALLOWED_ROLES_KEY);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Filter By");
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


    private int filterPostContentType = Flinnt.INVALID;

    /**
     * Checks if to publish now or schedule
     *
     * @param view publish now/schedule radio button
     */
    public void onRadioButtonClicked(View view) {
        filterPostContentType = Helper.getPostContentTypeFromName(((RadioButton) view).getText().toString());
    }
    /**
     * @param postType
     */
    private void onSubmit(int postType) {
        if (!Helper.isConnected()) {
            Helper.showNetworkAlertMessage(FilterByPostActivity.this);
        } else {

            Intent filter = new Intent(this, CourseDetailsActivity.class);
            filter.putExtra(FilterByPostActivity.FILTER_BY, "");
            filter.putExtra(Course.COURSE_ID_KEY, courseID);
            filter.putExtra(Course.COURSE_PICTURE_KEY, coursePicture);
            filter.putExtra(Course.COURSE_NAME_KEY, courseName);
            filter.putExtra(Course.ALLOWED_ROLES_KEY, courseAllowedRoles);
            if (filterPostContentType != Flinnt.INVALID) {
                filter.putExtra(FilterByPostActivity.FILTER_POST_CONTENT_TYPE, filterPostContentType);
            }
            filter.putExtra(FilterByPostActivity.FILTER_POST_TYPE, postType);
            startActivity(filter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                onSubmit(Flinnt.POST_TYPE_BLOG);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
