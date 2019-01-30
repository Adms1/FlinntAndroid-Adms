package com.edu.flinnt.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.adapter.PickDocFolderAdapter;
import com.edu.flinnt.util.Common;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.edu.flinnt.util.MyConfig;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;
import java.util.ArrayList;

/**
 * GUI custom class to select foler from storage
 */
public class PickDocFolderActivity extends AppCompatActivity {
    //flinnt_app F;
    private Toolbar toolbar;
    private final ArrayList<String> docsPaths = new ArrayList<String>();
    private String MyClassName = "GridViewFolderActivity ";
    private PickDocFolderAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private String readTwitterFeed_Settings = "";
    private int File_Type = -1;
    private ActionBar actionBar;
    private Common com;
    private ProgressDialog dialog;
    private Boolean IsQuiz = false;
    private ProgressDialog dialogCh;
    private Context con;
    private static final int GALLERY_REQUEST = 9391;
    private static final int AUDIO_GALLERY_REQUEST_CODE = 300;
    private static final String KEY_IMAGE = "com.example.picasso:image";

    private File root;
    private String RootFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        this.setContentView(R.layout.pick_doc_folder_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Folder");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        this.con = this;
        this.com = new Common(this);
		
        this.gridView = (GridView) this.findViewById(R.id.grid_view);


        // Initilizing Grid View
        this.InitilizeGridLayout();
        this.GetAllList();

        this.adapter = new PickDocFolderAdapter(this, this.docsPaths, this.columnWidth);
        // setting grid view mContentsAdapter
        this.gridView.setAdapter(this.adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            MyCommFun.sendTracker(this, "Pick Folder");
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

    private void InitilizeGridLayout() {
        Display display = ((WindowManager) getSystemService(this.WINDOW_SERVICE)).getDefaultDisplay();
        //flinnt_app F = null;
        Resources r = this.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Flinnt.GRID_PADDING, r.getDisplayMetrics());
        //this.F = (flinnt_app) getApplicationContext();
        this.columnWidth = (int) ((Integer.valueOf(display.getWidth()) - (Flinnt.NUM_OF_COLUMNS + 1) * padding) / Flinnt.NUM_OF_COLUMNS);

        this.gridView.setNumColumns(Flinnt.NUM_OF_COLUMNS);
        this.gridView.setColumnWidth(this.columnWidth);
        this.gridView.setStretchMode(GridView.NO_STRETCH);
        this.gridView.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        this.gridView.setHorizontalSpacing((int) padding);
        this.gridView.setVerticalSpacing((int) padding);
    }

    private void GetAllList() {

            RootFolder = "mnt";

        root = new File("/" + RootFolder + "/");
        this.getfile_All(root);

        if ( this.docsPaths.size() == 0 ) {
            RootFolder = "storage";
            root = new File("/" + RootFolder + "/");
            this.getfile_All(root);
        }
    }


    @SuppressWarnings("unchecked")
    public void getfile_All(File dir) {

        if (dir.getName().matches("Android")) {
            return;
        }
        if (dir.getName().matches("flinnt")) {
            //return;
        }
        if (dir.getName().startsWith(".")) {
            return;
        }

        File listFile[] = dir.listFiles();

        //  com.LOG1(1,"Folder Read",dir.getAbsolutePath());
        boolean IsDocsFound = false;
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory() && listFile[i].isHidden() == false) {
                    //fileList.add(listFile[i]);
                    this.getfile_All(listFile[i]);

                } else {
                    if (listFile[i].length() > 0 && listFile[i].isHidden() == false) {
                        if (!IsDocsFound) {

                            if (listFile[i].getName().endsWith(".pdf") || listFile[i].getName().endsWith(".PDF")) {
                                //if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PDF file name : " + listFile[i].getName());
                                //this.com.LOG1(1, this.MyClassName + "DocsPaths", listFile[i].getPath().toString());
                                this.docsPaths.add(listFile[i].getPath().toString());
                                if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("PDF Path : " + listFile[i].getPath().toString());
                                IsDocsFound = true;
                            }
                        }
                    }

                    //if (IsImageFound && IsAudioFound && IsVideoFound && IsDocsFound)
                    if (IsDocsFound) {
                        return;
                    }

                }

            }
        }
        //  return fileList;
    }


    public void getfile_one(File dir, String Type) {
        //this.com.LOG1(1, this.MyClassName + "dir.getName()", dir.getAbsolutePath().toString());
        //this.com.LOG1(1, this.MyClassName + "dir.length()", String.valueOf(dir.length()));
        if (dir.getName().matches("Android")) {
            return;
        }
        if (dir.getName().matches("flinnt")) {
            return;
        }

        if (dir.getName().startsWith(".")) {
            return;
        }
        if (MyCommFun.IsFileExist(dir.getAbsolutePath() + "/" + MyConfig.NOMEDIA)) {

            return;
        }

        File listFile[] = dir.listFiles();
        boolean IsImageFound = false;
        boolean IsAudioFound = false;
        boolean IsVideoFound = false;
        boolean IsDocsFound = false;
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory() && listFile[i].isHidden() == false) {
                    //fileList.add(listFile[i]);
                    this.getfile_one(listFile[i], Type);

                } else {

                    if (listFile[i].length() > 0 && listFile[i].isHidden() == false) {
                        if (Type.matches("PDF")) {
                            if (!IsDocsFound) {

                                if (listFile[i].getName().endsWith(".pdf")) {
                                    //this.com.LOG1(1, this.MyClassName + "DocsPaths", listFile[i].getPath().toString());
                                    this.docsPaths.add(listFile[i].getPath().toString());
                                    IsDocsFound = true;
                                }
                            }
                        }
                    }

                    if (IsImageFound || IsAudioFound || IsVideoFound || IsDocsFound) {
                        return;
                    }

                }

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(mainmenu.this,item.toString(),Toast.LENGTH_SHORT).show();
        this.com.LOG(this.MyClassName + "Process", "2");
        switch (item.getItemId()) {

            case android.R.id.home:
                finish(); //onBackPressed();
                break;
            
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.com.LOG("onActivityResult result", requestCode + "  " + resultCode);
        String result = "";
        String File_Type = "";
        if (requestCode == 300) {
            if (resultCode == Activity.RESULT_OK) {
                result = data.getStringExtra("result");
                File_Type = data.getStringExtra("File_type");
                this.com.LOG("GridViewFolderActivity result", result + "  File_Type=" + File_Type);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("File_Type", File_Type);
                returnIntent.putExtra("result", result);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                result = "";
                //Write your code if there's no result
            }

        }
    }


}
