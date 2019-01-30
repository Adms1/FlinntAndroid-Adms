package com.edu.flinnt.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.edu.flinnt.FlinntApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadMediaFile extends AsyncTask<Void, Long, Boolean> {

	private String mDirName, mFileName;
	//private File mFile;


	private long mFileLen;

	private String mErrorMsg = "";
	public long id = 0;

	private int percent = 0;
	private String mMediaURL;
	ProgressDialog mProgressDialog = null;
	private Handler mHandler;
	public static final int DOWNLOAD_COMPLETE = 111;
	public static final int DOWNLOAD_FAIL = 112;
	private boolean isCancel = false;
	public static String download_cancel_str = "Download cancelled";
	String TEMP_FILE = "temp_file";

	public DownloadMediaFile( Context context, String dirName, String fileName, Long postID, String url, Handler handler ) {
		mDirName = dirName;
		mFileName = fileName;
		id = postID;
		mMediaURL = url;
		mHandler = handler;

	}



	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//startProgressDialog();
		DownloadFileManager.add(this);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Boolean ret = false;
		Helper.lockCPU();
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		try{
			LogWriter.info("File name : " + mFileName);
			ret = downloadMedia(mDirName, mFileName);
		}catch(Exception e)
		{
			LogWriter.err(e);
		}
		finally
		{
			Helper.unlockCPU();
		}
		return ret;		
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
		updateProgess();
	}

	public void updateProgess()
	{	
		/*
		Log.i("UploadMedia","updateProgress: " +percent);
		if(mMsg != null)
         {
         	ProgressBar pb = mMsg.getProgressBar();
         	if(pb != null)
         	{
         		pb.setMax(100);
         		if(pb.getVisibility() != View.VISIBLE)
         			pb.setVisibility(View.VISIBLE);
                 pb.setProgress(percent);
         	}
         }*/
	}

	@Override
	protected void onPostExecute(Boolean result) {
		//stopProgressDialog();
		/* ProgressBar pb = mMsg.getProgressBar();
	    	if(pb != null)
	    	{
	    		pb.setMax(100);
	    		pb.setVisibility(View.GONE);
	            pb.setProgress(0);
	    	}*/
		if (result) {
			//Helper.showToast("Download Success", Toast.LENGTH_SHORT);
			sendMesssageToGUI(DOWNLOAD_COMPLETE);
			
			//showToast("successfully");
		} else {
			//Helper.showToast(mErrorMsg, Toast.LENGTH_SHORT);
			try {
				new File(mDirName, mFileName).delete();
				
			} catch (Exception e) {
				LogWriter.err(e); 
			}
			sendMesssageToGUI(DOWNLOAD_FAIL);
		}

		/*if(SmsBroadcastReceiver.registeredHandler != null)
	        	SmsBroadcastReceiver.registeredHandler.sendEmptyMessage(BlockedMSGListView.MESSAGE_NEWINCOMING);*/
		DownloadFileManager.remove(this.id);
	}

	HttpsURLConnection connection;
	private boolean downloadMedia( String dirName, String fileName )
	{
		boolean ret = false;

		if(TextUtils.isEmpty(fileName)){
			return ret;
		}

		File audiofile = new File(dirName, TEMP_FILE);
		mFileLen = audiofile.length();

		String strMyMediaPath = audiofile.getAbsolutePath();
		LogWriter.info("Media file path : " + strMyMediaPath);

		try{

			URL url = new URL(mMediaURL);
			connection = (HttpsURLConnection)url.openConnection();
			connection.connect();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(audiofile);

			byte data[] = new byte[1024];
			long total = 0;
			long updateCount = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				//LogWriter.info("isCancel : " + isCancel());
				if(isCancel()){
					mErrorMsg = download_cancel_str;
					mFileName = TEMP_FILE;
					break;
				}
				total += count;
				updateCount += count;
				if(updateCount > 10000)
				{
					// publishing the progress....
					publishProgress(total);
					updateCount = 0;
				}
				output.write(data, 0, count);
			}
			LogWriter.info("isCancel : " + isCancel());
			if( !isCancel() ) {
				audiofile.renameTo(new File(dirName, fileName));
				new SingleMediaScanner(FlinntApplication.getContext(), new File(dirName, fileName));
				//Helper.updateGallery();
				ret = true;
			}
			
			LogWriter.info("DownloadFile: result:"+ connection.getResponseCode());
			//File entryptFile = new File(mFile.getAbsoluteFile()+"$");
			//entryptFile = FileUtil.encryptFile(mFile.getAbsolutePath(),entryptFile);
			//mMsg.mFilePath = mFile.getAbsolutePath(); //entryptFile.getAbsolutePath();
			//mFile.delete();
			//mFile = null;
			output.flush();
			output.close();
			input.close();

		} catch (FileNotFoundException e) {
			// This session wasn't authenticated properly or user unlinked
			LogWriter.err(e);
			mErrorMsg = "File Not Found";
		}  catch (MalformedURLException e) {
			LogWriter.err(e);
			mErrorMsg = "Invalid link";
			LogWriter.err(e);
		} catch (IOException e) {
			LogWriter.err(e);
			mErrorMsg = "Network error";
		}

		/*FlinntApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));*/
		return ret;
	}

    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mErrorMsg;
			mHandler.sendMessage(msg);
		}
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public void setHandler(Handler handler) {
		LogWriter.info("set handler : " + handler );
		this.mHandler = handler;
	}

	
}
