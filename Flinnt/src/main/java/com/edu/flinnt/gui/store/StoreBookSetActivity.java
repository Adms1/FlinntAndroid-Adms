package com.edu.flinnt.gui.store;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.store.BrowseCourseCategoryAdapterNew;
import com.edu.flinnt.core.BrowseCourseCategory;
import com.edu.flinnt.core.store.CourseDescriptionNew;
import com.edu.flinnt.models.store.StoreBookSetResponse;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.protocol.BrowsableCourse;
import com.edu.flinnt.protocol.BrowseCoursesResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;

import static com.edu.flinnt.protocol.BrowsableCourse.BUNDLE_LIST_KEY;

public class StoreBookSetActivity extends AppCompatActivity {

    public static final String TAG = "BrowseCourseCategoryMoreActivity";
    private Toolbar mToolbar;
    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private BrowseCourseCategoryAdapterNew storeBookSetListAdapter;
    private ArrayList<BrowsableCourse> mCourseList = new ArrayList<>();
    private String categoryId = "";
    private String categoryName = "";
    private BrowseCourseCategory mBrowseCourseCategory;
    private ArrayList<StoreModelResponse.Course> bundleList = new ArrayList<>();
    public TextView mEmptyTxt;
    private String userId;
    private StoreBookSetResponse storeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_book_set);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }


        Bundle bundle = getIntent().getExtras();

        if (null != bundle) {
            if (bundle.containsKey(BrowsableCourse.CATEGORY_ID_KEY)) {
                categoryId = bundle.getString(BrowsableCourse.CATEGORY_ID_KEY);
            }
            if (bundle.containsKey(BrowsableCourse.CATEGORY_NAME_KEY)) {
                categoryName = bundle.getString(BrowsableCourse.CATEGORY_NAME_KEY);
            }

            try {
                if (bundle.containsKey(BrowsableCourse.BUNDLE_LIST_KEY)) {
                    bundleList = bundle.getParcelableArrayList(BUNDLE_LIST_KEY);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book set");

        mRecyclerView = (RecyclerView)findViewById(R.id.bookset_recycler);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                stopProgressDialog();
                switch (message.what) {
                    case Flinnt.SUCCESS: {

                        try {
                            //Helper.showToast("Success", Toast.LENGTH_SHORT);
                            if (LogWriter.isValidLevel(Log.INFO))
                                LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                            if (message.obj instanceof StoreBookSetResponse) {
                               // updateCourseList((StoreModelResponse.Datum) message.obj);
                                storeData = (StoreBookSetResponse)message.obj;
                                refreshView();
                            }
                        } catch (Exception e) {
                            LogWriter.err(e);
                        }
                    }
                    break;
                    case Flinnt.FAILURE: {
                        if (LogWriter.isValidLevel(Log.INFO))
                            LogWriter.write("FAILURE_RESPONSE : " +message.obj.toString());

                        if (message.obj instanceof BrowseCoursesResponse) {
                            BrowseCoursesResponse response = (BrowseCoursesResponse) message.obj;
                            if (response.errorResponse != null) {
                                Helper.showAlertMessage(StoreBookSetActivity.this,"Error",response.errorResponse.getMessage(), "CLOSE");
                            }
                        }
                    }
                    break;
                    default:
                        super.handleMessage(message);
                        break;
                }
            }
        };


        CourseDescriptionNew mCourseDescription = new CourseDescriptionNew(mHandler,Config.getStringValue(Config.USER_ID));
        mCourseDescription.sendBookSetListRequest();
        startProgressDialog();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(StoreBookSetActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        storeBookSetListAdapter = new BrowseCourseCategoryAdapterNew(StoreBookSetActivity.this,storeData.getData(),2);
        mRecyclerView.setAdapter(storeBookSetListAdapter);
    }

    private void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(this)) {
            mProgressDialog = Helper.getProgressDialog(this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(this))
                mProgressDialog.show();
        }
    }

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

}
