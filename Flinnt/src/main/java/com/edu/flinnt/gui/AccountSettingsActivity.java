package com.edu.flinnt.gui;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.MuteSetting;
import com.edu.flinnt.protocol.MuteSettingRequest;
import com.edu.flinnt.protocol.MuteSettingResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.MyCommFun;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * User's personal settings GUI
 */

public class AccountSettingsActivity extends AppCompatActivity implements OnClickListener {

	Toolbar toolbar;
	ProgressDialog mProgressDialog = null;
	/*	
	SwitchCompat switchCommentReply;
	SwitchCompat switchMuteAll;
	*/
	CheckBox switchCommentReply;
	CheckBox switchMuteAll;
	CheckBox switchAutoDownload;
	
	Handler mHandler;
	MuteSetting mMuteSetting;
	MuteSettingRequest mMuteSettingRequest = new MuteSettingRequest();
	MuteSettingResponse mMuteSettingResponse;
	
	final int mAccountType = MuteSetting.ACCOUNT_SETTING;
	int mRequestType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		setContentView(R.layout.account_settings_activity);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//toolbar.setNavigationIcon(R.drawable.ic_drawer);
		getSupportActionBar().setTitle("Settings");		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		switchCommentReply = (CheckBox) findViewById(R.id.account_comment_reply_cb);
		switchCommentReply.setOnClickListener(this);
		findViewById(R.id.layout_comment_reply).setOnClickListener(this);
		
		switchMuteAll = (CheckBox) findViewById(R.id.accound_sound_cb);
		switchMuteAll.setOnClickListener(this);
		findViewById(R.id.layout_mute_all).setOnClickListener(this);
		
		switchAutoDownload = (CheckBox) findViewById(R.id.cb_auto_download);
		switchAutoDownload.setOnClickListener(this);
		findViewById(R.id.layout_auto_download).setOnClickListener(this);
		
		mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // Gets the task from the incoming Message object.
                switch (message.what) {
                    case Flinnt.SUCCESS: {
                    	stopProgressDialog();
                    	if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("SUCCESS_RESPONSE : " + message.obj.toString());
                    	if( MuteSetting.GET_SETTING == mRequestType ) {
                    		mMuteSettingResponse = (MuteSettingResponse) message.obj;
                    		updateSettings();
                    	}
                    	else {
                    		Helper.showToast("Settings saved successfully", Toast.LENGTH_SHORT);
                    	}
                    }
                    break;
                    case Flinnt.FAILURE: {
                    	stopProgressDialog();
                    	if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FAILURE_RESPONSE : " + message.obj.toString());
                    	MuteSettingResponse response = (MuteSettingResponse) message.obj;
                    	if(response.errorResponse != null){
                    		Helper.showAlertMessage(AccountSettingsActivity.this, "Settings", response.errorResponse.getMessage(), "CLOSE");
                    	}
                    	else {
                    		Helper.showAlertMessage(AccountSettingsActivity.this, "Settings", "Settings could not be updated", "CLOSE");
                    	}
                    }	
                    break;
                    default:
                        super.handleMessage(message);
                }
            }

        };
        
        if( null == mMuteSetting ) {
        	mMuteSetting = new MuteSetting(mHandler, mMuteSettingRequest, mAccountType);
        }
        
        if( null ==  mMuteSettingResponse) {
        	sendGetSettingRequest();
        }
        else {
        	updateSettings();
        }

	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MyCommFun.sendTracker(this, "activity="+Flinnt.SETTINGS+"&user="+Config.getStringValue(Config.USER_ID));
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

	@Override
	public void onClick(View view) {
		if ( !Helper.isConnected() ) {
			Helper.showNetworkAlertMessage(this);
		}
		else {
			if ( view.getId() == R.id.layout_comment_reply ) {
				switchCommentReply.setChecked ( !switchCommentReply.isChecked() );
				sendSetSettingRequest ( R.id.account_comment_reply_cb );
			}
			else if ( view.getId() == R.id.layout_mute_all ) {
				switchMuteAll.setChecked ( !switchMuteAll.isChecked() );
				sendSetSettingRequest ( R.id.accound_sound_cb );
			}
			else if ( view.getId() == R.id.layout_auto_download) {
				switchAutoDownload.setChecked ( !switchAutoDownload.isChecked() );
				sendSetSettingRequest ( R.id.cb_auto_download );
			}
			else {
				sendSetSettingRequest(view.getId());
			}
		}
	}

    /**
     * Sends request to get the last saved settings
     */
	public void sendGetSettingRequest() {
		startProgressDialog();
		mRequestType = MuteSetting.GET_SETTING;
		mMuteSetting.setRequestType(mRequestType);
		mMuteSettingRequest.setMuteComment("");
		mMuteSettingRequest.setMuteSound("");
		mMuteSettingRequest.setAutoDownload("");
		
        mMuteSetting.sendMuteSettingRequest();
	}

    /**
     * Sends request to save changed settings
     */
	public void sendSetSettingRequest(int id) {
		startProgressDialog();
		mRequestType = MuteSetting.SET_SETTING;
		mMuteSetting.setRequestType(mRequestType);
		mMuteSettingRequest.setMuteComment(switchCommentReply.isChecked() ? Flinnt.ENABLED : Flinnt.DISABLED);
		mMuteSettingRequest.setMuteSound(switchMuteAll.isChecked() ? Flinnt.ENABLED : Flinnt.DISABLED);
		mMuteSettingRequest.setAutoDownload(switchAutoDownload.isChecked() ? Flinnt.ENABLED : Flinnt.DISABLED);

		mMuteSetting.sendMuteSettingRequest();
	}

    /**
     * Sets the settings to UI from Settings response
     */
    public void updateSettings() {
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("AccountMuteComment : " + mMuteSettingResponse.getAccountMuteComment());
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("AccountMuteSound : " +mMuteSettingResponse.getAccountMuteSound());
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Auto Download : " +mMuteSettingResponse.getAutoDownload());
		
		switchCommentReply.setChecked( mMuteSettingResponse.getAccountMuteComment().equals(Flinnt.ENABLED) ? true : false );
		switchMuteAll.setChecked( mMuteSettingResponse.getAccountMuteSound().equals(Flinnt.ENABLED) ? true : false );
		switchAutoDownload.setChecked( mMuteSettingResponse.getAutoDownload().equals(Flinnt.ENABLED) ? true : false );
	}

    /**
     *  Starts a circular progress dialog
     */
	private void startProgressDialog(){
        if (!Helper.isFinishingOrIsDestroyed(AccountSettingsActivity.this)) {
            mProgressDialog = Helper.getProgressDialog(AccountSettingsActivity.this, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if (mProgressDialog != null) mProgressDialog.show();
        }
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish(); //onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
