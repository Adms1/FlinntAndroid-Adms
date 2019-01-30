package com.edu.flinnt.util;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.protocol.Validation;

import java.io.File;

/**
 * custom recorder activity with limitation for recording
 */
public class Recorder extends AppCompatActivity implements OnClickListener, Runnable {

	Button record, /*play,*/ send, cancel;
	LinearLayout layout_record, layout_play; 
	TextView text_hint;
	Boolean recording;
	MediaRecorder mMediaRecorder;
	MediaPlayer mMediaPlayer;
	ProgressBar progressBar;
	File file; // = Helper.getOutputMediaFile(Flinnt.POST_CONTENT_AUDIO);
	String uploadFilePathString;
	//Toolbar toolbar;
	long maxFileSize = 10 * 1024 * 1024; // 10 MB
	String fileTypes = "mp3";
	
    private long startTime;
    long timeInMilliseconds;
    long timeSwapBuff;
    long updatedTime;
	private final Handler customHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		    Window window = getWindow();
		    window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
		}
		*/
		setContentView(R.layout.audio_record);
		
		Bundle bundle = getIntent().getExtras();
		if( null != bundle ) {
			uploadFilePathString =  bundle.getString("FilePath");
			file = new File(uploadFilePathString);
			
			if(bundle.containsKey(Validation.MAX_FILE_SIZE_KEY))
				maxFileSize =  bundle.getLong(Validation.MAX_FILE_SIZE_KEY);
			if(bundle.containsKey(Validation.FILE_TYPES_KEY))
				fileTypes = bundle.getString(Validation.FILE_TYPES_KEY);
			if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("maxFileSize " + maxFileSize + ", fileTypes : " + fileTypes);
		}
		/*
		toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle( Html.fromHtml( "<font color=#000000>" + getResources().getString(R.string.my_courses) + "</font>"  ) );
        */
		record = (Button) findViewById(R.id.record);
		record.setOnClickListener(this);
		record.setLongClickable(false);
		/*
		play = (Button) findViewById(R.id.play);
		play.setOnClickListener(this);
		*/
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(this);
		send.setLongClickable(false);
				
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		cancel.setLongClickable(false);
		
		layout_record = (LinearLayout) findViewById(R.id.layout_record);
		layout_record.setVisibility(View.VISIBLE);
		layout_play = (LinearLayout) findViewById(R.id.layout_play);
		layout_play.setVisibility(View.GONE);
		
		text_hint = (TextView) findViewById(R.id.text_hint);
		text_hint.setText(R.string.start_record);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		this.setFinishOnTouchOutside(false);
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.record:
			if( ((Button) view).getText().equals("Record") ) {
				startRecord();
				((Button) view).setText(R.string.stop_button);	
				text_hint.setText(R.string.recording);
			}
			else {
				stopRecord();
				((Button) view).setText(R.string.record);
				text_hint.setText(R.string.success_record);
				layout_record.setVisibility(View.GONE);
				layout_play.setVisibility(View.VISIBLE);
			}
			break;
		/*	
		case R.id.play:
			if( ((Button) view).getText().equals("Play") ) {
				text_hint.setText(R.string.playing);
				playRecord();
				progressBar.setVisibility(ProgressBar.VISIBLE);
		        progressBar.setProgress(0);
		        progressBar.setMax(mMediaPlayer.getDuration());
		        new Thread(this).start();
		        ((Button) view).setText(R.string.stop_button);
			}
			else {
				customHandler.removeCallbacks(updateTimerThread);
				text_hint.setText(R.string.stopped);
				((Button) view).setText(R.string.play_menu);
				progressBar.setVisibility(ProgressBar.GONE);
				if( null != mMediaPlayer ) {
					if( mMediaPlayer.isPlaying() ) {
						mMediaPlayer.stop();
					}
					mMediaPlayer.release();
				}
				mMediaPlayer = null;
			}
			break;
		 */
		case R.id.send:
			sendResult();
			break;
		case R.id.cancel:
			if( null != mMediaPlayer ) {
				if( mMediaPlayer.isPlaying() ) {
					mMediaPlayer.stop();
				}
				mMediaPlayer.release();
			}
			mMediaPlayer = null;
			
			if( null != mMediaRecorder ) {
				mMediaRecorder.release();
			}
			mMediaRecorder = null;

			finish();
			break;
		default:
			break;
		}
	}
	
	
	private void stopRecord() {
		try{
		if( null != mMediaRecorder ) {
			//mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			
			timeSwapBuff += timeInMilliseconds;
	        customHandler.removeCallbacks(updateTimerThread);
		}
		}catch(Exception e)
		{
			mMediaRecorder = null;
		}
	}

	void resetTimer() {
		startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        startTime = SystemClock.uptimeMillis();
	}
	
	void startRecord() {
		try {
			resetTimer();
	        customHandler.postDelayed(updateTimerThread, 0);
	        
			if ( null == mMediaRecorder ) {
				mMediaRecorder = new MediaRecorder();
			}
			
			if( file.exists() ) {
				file.delete();
			}
			file.createNewFile();
			
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			mMediaRecorder.setMaxFileSize(maxFileSize);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		}
		catch( Exception e ) {
			LogWriter.err(e);
		}
	}

	
	@Override
    public void run() {
        int currentPosition = 0;
        int total = mMediaPlayer.getDuration();
        while ( mMediaPlayer != null && currentPosition <= total ) {
        	try {
                Thread.sleep(100);
                currentPosition = mMediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                LogWriter.err(e);
            }            
            progressBar.setProgress(currentPosition);
        }
        //customHandler.removeCallbacks(updateTimerThread);
    }

	private void sendResult()
	{
		if(file.exists())
		{
			Intent data = new Intent();
			data.putExtra("output", file.getAbsolutePath());
			if (getParent() == null) {
				setResult(Activity.RESULT_OK, data);
			} else {
				getParent().setResult(Activity.RESULT_OK, data);
			}
		}
		finish();
	}
	
	
    private final Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            text_hint.setText("" + mins + ":" + String.format("%02d", secs));
            // + ":" + String.format("%03d", milliseconds));

            customHandler.postDelayed(this, 0);
        }
    };
    
    public void onBackPressed() {
    	stopRecord();
    	super.onBackPressed();
    };
}
