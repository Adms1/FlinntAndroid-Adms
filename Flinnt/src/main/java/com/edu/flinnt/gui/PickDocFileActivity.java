package com.edu.flinnt.gui;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.GridView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.PickDocFileAdapter;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

/**
 * GUI custom class to pick files from storage
 */
public class PickDocFileActivity extends AppCompatActivity {
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private PickDocFileAdapter adapter_audio;
    private GridView gridView;
    private int columnWidth;
    private Context con;
    private ActionBar actionBar;
    private Common com;
    private ProgressDialog dialog;
    private ProgressDialog dialogCh;
    private int Filetype = -1;
    private String folderuri;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        this.setContentView(R.layout.pick_doc_file_activity);

		  toolbar = (Toolbar) findViewById(R.id.toolbar);
	        toolbar.setTitle("Select File");
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        this.con = this;
        this.com = new Common(this);


        Bundle extras = this.getIntent().getExtras();
        this.folderuri = "/mnt/sdcard/";
        if (extras != null) {
            if (extras.getString("folderuri") != null) {
                this.folderuri = extras.getString("folderuri");
            }
        }


        this.gridView = (GridView) this.findViewById(R.id.grid_view);
        this.InitilizeGridLayout();
        this.imagePaths = com.getFolderPaths(this.folderuri);
        this.ShowList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Pick File");
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }catch (Exception e){
            LogWriter.err(e);
        }
    }

    private void ShowList() {
        this.gridView = (GridView) this.findViewById(R.id.grid_view);

        this.adapter_audio = new PickDocFileAdapter(this, this.imagePaths, 100);
        // setting grid view mContentsAdapter
        this.gridView.setAdapter(this.adapter_audio);
    }

    private void InitilizeGridLayout() {
        Display display = ((WindowManager) getSystemService(this.WINDOW_SERVICE)).getDefaultDisplay();
        Resources r = this.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Flinnt.GRID_PADDING, r.getDisplayMetrics());

        this.columnWidth = (int) ((Integer.valueOf(display.getWidth()) - (Flinnt.NUM_OF_COLUMNS + 1) * padding) / Flinnt.NUM_OF_COLUMNS);

        // gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        this.gridView.setNumColumns(1);
        this.gridView.setColumnWidth((int) (Integer.valueOf(display.getWidth()) - padding * 2));
        this.gridView.setStretchMode(GridView.NO_STRETCH);
        this.gridView.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        this.gridView.setHorizontalSpacing((int) padding);
        this.gridView.setVerticalSpacing((int) padding);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish(); //onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


}
