/*
 * Copyright (c) 2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


package com.edu.flinnt.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.protocol.FileUploadResponse;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
//import javax.activation.MimeType;


/**
 * Here we show uploading a file in a background thread, trying to show
 * typical exception handling and flow of control for an app that uploads a
 * file from Dropbox.
 */
public class UploadMediaFile extends AsyncTask<Void, Long, Boolean> {
	
    private String mPath;
    private File mFile;

    public long id = 0;
    private long mFileLen;
    
    private Context mContext;
    Handler mHandler;
    int mResourceType = Flinnt.INVALID;
    int mResourceID = Flinnt.INVALID;
    public static final int UPLOAD_SUCCESS = 100;
    public static final int UPLOAD_FAILURE = 101;
    
    String mUrl = "";
    
    public UploadMediaFile(Context context, Handler handler, String filePath, int resType) {
    	if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UploadMediaFile filePath : " + filePath + ", resType : " + resType );
        mContext = context.getApplicationContext();
        mHandler = handler;
        mFile = new File(filePath);

        if(mFile != null)
        	mFileLen = mFile.length();
        mPath = filePath;
        id = System.currentTimeMillis();
        mResourceType = resType;

        mUrl = Flinnt.API_URL + Flinnt.URL_RESOURCE_UPLOAD + Config.getStringValue(Config.USER_ID) + File.separator + mResourceType + File.separator; 
        if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UploadMediaFile filePath : " + mFileLen + ",  mResourseType : " + mResourceType);
    }
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	UploadFileManager.add(this);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
    	Boolean ret = false;
    	Helper.lockCPU();
    	Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    	try{
    		if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UploadMedia Thread: mFile : " + mFile );
            ret = uploadFile();
    	}
    	catch(Exception e)
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
       // percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
       updateProgess();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        /*
        ProgressBar pb = mMsg.getProgressBar();
    	if(pb != null)
    	{
    		pb.setMax(100);
    		pb.setVisibility(View.GONE);
            pb.setProgress(0);
    	}
    	if (result) {
        	//showToast("successfully");
        } else {
        	//showToast(mErrorMsg);
        }
        */
        UploadFileManager.remove(this.id);
        
        
        if( Flinnt.INVALID != mResourceID ) {
        	sendMesssageToGUI(UPLOAD_SUCCESS);
        }
        else {
        	sendMesssageToGUI(UPLOAD_FAILURE);
        }
    }

    /**
     * Sends response to handler
     * @param messageID response ID
     */
    public void sendMesssageToGUI(int messageID) {
		if( null != mHandler) {
			Message msg = new Message();
			msg.what = messageID;
			msg.obj = mResourceID;
			mHandler.sendMessage(msg);
		}
	}
    
    public void updateProgess()
    {
    	//if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UploadMedia updateProgress: " +percent);
    	/*
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

    
    public boolean uploadFile() {
    	boolean ret = false;
        try {
            if( !Helper.isConnected() )
            {
            	if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("UploadToServer : Not connected to internet. so return.");
            	return ret;
            }
            
            // final String uploadFilePath = MyAllVal[9] + "/flinnt/upload";
            // final String sourceFileUri = uploadFilePath + "/" +
            // UploadFileName;
            // final String sourceFileUri = mFile; //UploadFileName;
            // //comm.LOG1(4, MyClassName + "uploadFile Strart", sourceFileUri);

            // final String upLoadServerUri = serverUri; //MyConfig.domainname.toString() + "" + MyConfig.subdomainname.toString() + "upload_file.php?action=1" + "&VER=" + MyConfig.PHPVER + "&mtype=1&user_id=" + F.getUserId("0") + "&course_id=" + this.F.getCourseId("0") + "&format=json&post_content_type=" + post_content_type;
            // final String upLoadServerUri =
            // "http://www.flinnt.com/mobile_v3/test_upload_android.php?acti on=1&VER="
            // + MyConfig.PHPVER + "&mtype=1&user_id=" + MyAllVal[5].toString()
            // +
            // "&format=json&post_content_type=" + post_content_type;
            int serverResponseCode = 0;
            // ProgressDialog dialog = null;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            // int maxBufferSize = 1 * 1024 * 1024;
            int maxBufferSize = 256 * 1024;
            File sourceFile = new File(mPath);

            if (!sourceFile.isFile()) {
                //dialog_close("4");
            	if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("uploadFile : Source File not exist : " + mPath);
            	/*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        comm.ShowMessage(activity, " Source File not exist :" + sourceFileUri);
                        // messageText.setText("Source File not exist :"
                        // sourceFileUri);

                    }
                });
            	 */
                return ret;

            } 
            else {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(mUrl);

                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("old url : " + mUrl + ",  new url : " + url);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    // conn.setChunkedStreamingMode(1024);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("upload_file", mPath);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"upload_file\";filename=\"" + mPath + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();
                    //comm.LOG1(1, MyClassName + "upload flie bytesAvailable", String.valueOf(bytesAvailable));
                    //comm.LOG1(1, MyClassName + "upload flie maxBufferSize", String.valueOf(maxBufferSize));
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        //comm.LOG1(  1, MyClassName + "upload file ", String.valueOf(buffer) + " /  " + String.valueOf(bufferSize));
                        try {
                            dos.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                        	if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Could not upload file, pl. try again.");
                            return false;
                            // LogWriter.err(e);
                            // response = "outofmemoryerror";
                            // return response;
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        // onProgressUpdate(String.valueOf(100 * bytesAvailable/ maxBufferSize));
                    }
                    // updateProgressInfo(100 * bufferSize / maxBufferSize);
                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("upload flie write finish");
                    
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // close file input stream
                    fileInputStream.close();
                    
                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    InputStream is = null;
                    if (serverResponseCode >= 400) {
                        is = conn.getErrorStream();
                    } else {
                        is = conn.getInputStream();
                    }
                    // respuploadFile = serverResponseCode + "\n" +
                    // serverResponseMessage + "\n>" + Util.streamToString(is) +
                    // "<\n";
                    String stringResponse = Helper.InputStreamToString(is);

                    /*
                    try {
                        // comm.WriteMessage(respuploadFile,"UrvishTest.txt","document");
                    } catch (Exception e) {

                    }
                    */
                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("uploadFile serverResponseCode : " + serverResponseCode + ", Response Message : " + serverResponseMessage );
                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("uploadFile server Response : " + stringResponse);
                    
                    if (serverResponseCode == 200) {
                        FileUploadResponse fileUploadResponse = new FileUploadResponse();
                       
                        JSONObject response = new JSONObject(stringResponse);
                        
                    	if(fileUploadResponse.isSuccessResponse(response)) {

							JSONObject jsonData = fileUploadResponse.getJSONData(response);
							if (null != jsonData) {
								boolean success = fileUploadResponse.parseJSONObject(jsonData);
		                        if( success ) {
		                        	mResourceID = fileUploadResponse.getResourceID();
		                        	ret =  true;
		                        }	
							}
						}
                    	
                    	/*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // String msg =
                                // "File Upload Completed.\n\n See uploaded file here : \n\n"
                                // +uploadFilePath + "" +uploadFileName;
                                // comm.ShowMessage(con,FileType +
                                // " file Upload Complete.");
                                // Toast.makeText(UploadToServer.this,
                                // "File Upload Complete.",
                                // Toast.LENGTH_SHORT).show();

                                JSONObject json = null;

                                String s_status = "";
                                try {

                                    json = new JSONObject(respuploadFile);
                                    //comm.LOG1(4, MyClassName                                                    + "respuploadFile 123",                                            respuploadFile);
                                    JSONArray jArray = json.getJSONArray("list");
                                    JSONObject e = jArray.getJSONObject(0);
                                    String s = e.getString("interest");
                                    JSONObject jObject = new JSONObject(s);
                                    s_status = jObject.getString("resource_id");
                                    if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("s_status : " + s_status);
                                } catch (JSONException e) {

                                    LogWriter.err(e);
                                }
                                if (s_status.matches("") == false) {

                                    //btn_PostSubmit_click(s_status);
                                }
                            }
                        });
                        */
                    }

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (OutOfMemoryError ex) {
                    //dialog_close("3");
                    ex.printStackTrace();
                    // //comm.LOG1(1, "upload file err",
                    // ex.getMessage().toString());
                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // messageText.setText("MalformedURLException Exception : check script url.");
                            // Toast.makeText(UploadToServer.this,
                            // "MalformedURLException",
                            // Toast.LENGTH_SHORT).show();

                            comm.ShowMessage(activity, "Could not upload file, pl. try again.");
                        }
                    });
                    */
                } catch (NullPointerException ex) {
                    //dialog_close("5");
                    ex.printStackTrace();
                    // //comm.LOG1(1, "upload file err",
                    // ex.getMessage().toString());
                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // messageText.setText("MalformedURLException Exception : check script url.");
                            // Toast.makeText(UploadToServer.this,
                            // "MalformedURLException",
                            // Toast.LENGTH_SHORT).show();

                            comm.ShowMessage(activity, "Could not upload file, pl. try again.");
                        }
                    });
                    */
                } catch (MalformedURLException ex) {

                    //dialog_close("6");
                    ex.printStackTrace();
                    // //comm.LOG1(1, "upload file err",
                    // ex.getMessage().toString());
                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // messageText.setText("MalformedURLException Exception : check script url.");
                            // Toast.makeText(UploadToServer.this,
                            // "MalformedURLException",
                            // Toast.LENGTH_SHORT).show();

                            comm.ShowMessage(activity, "Could not upload file, pl. try again.");
                        }
                    });
                    */

                    //comm.LOG1(1, MyClassName + "Upload file to server",                            "error: " + ex.getMessage());
                } catch (final Exception e) {

                    //dialog_close("7");
                    LogWriter.err(e);
                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // messageText.setText("Got Exception : see logcat ");
                            // Toast.makeText(UploadToServer.this,
                            // "Got Exception : see logcat ",
                            // Toast.LENGTH_SHORT).show();

                            comm.ShowMessage(activity, "Could not upload file, pl. try again.");
                        }
                    });*/
                    //comm.LOG1(4, MyClassName                            + "Upload file to server Exception", "Exception : "                            + e.getMessage());
                }
                //dialog_close("8");

            } // End else block

        } catch (Exception e) {
        	LogWriter.err(e);
        }
        return ret;
    }
    
    private boolean downloadFile(String filePath)
    {
    	boolean ret = false;
    	try{
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to

    		String fileName = new File(filePath).getName();

    		/*
    		mPath = Registrar.getLastResponse(mContext).downloadurl+"?filename="+URLEncoder.encode(fileName)+
    				"&number="+Registrar.getLastResponse(mContext).getXmpp().getUser()+"&authid="+Registrar.getLastResponse(mContext).getAuth_id()+(isThumbnelOnly?"&thumb=1":"");
    		*/
    		//mPath = FamilyContant.FP_DOWNLOAD_FILE_HOST+"?filename="+URLEncoder.encode(fileName)+"&authid="+Config.getValue(Config.AUTH_ID_FP)+"&familyid="+mMsg.mFamilyId;
    		//mFileLen = mMsg.mMediaSize;
    		Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);

            String extention = MimeTypeMap.getSingleton().getExtensionFromMimeType(Helper.getExtension(new File(filePath).getAbsolutePath()));
            if(TextUtils.isEmpty(extention))
            {
            	return ret;
            }
            
            String newPicFile = "Flinnt_"+df.format(date) +"."+ extention;
            String outPath = Helper.getFlinntImagePath() + newPicFile;
            mFile = new File(outPath);
            
            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("Final Download URL : "+ mPath);
    		URL url = new URL(mPath);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            //mFileLen = connection.getContentLength();
            
            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(mFile);

            byte data[] = new byte[1024];
            long total = 0;
            long updateCount = 0;
            int count;
            while ((count = input.read(data)) != -1) {
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
            ret = true;
            if(LogWriter.isValidLevel(Log.INFO)) LogWriter.write("DownloadFile: result : "+ connection.getResponseCode());
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
    }  catch (MalformedURLException e) {
    	LogWriter.err(e);
		LogWriter.err(e);
	} catch (IOException e) {
		LogWriter.err(e);
	}

    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
    		Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    return ret;
    }
    
/*    
    private boolean downloadThumbFile()
    {
    	boolean ret = false;
    	try{
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
    		
    		mPath = Registrar.getLastResponse(mContext).downloadurl+"?filename="+URLEncoder.encode(new File(mMsg.mFilePath).getName())+
    				"&number="+Registrar.getLastResponse(mContext).getXmpp().getUser()+"&authid="+Registrar.getLastResponse(mContext).getAuth_id()+"&thumb=1";
    		
    		mPath = FamilyContant.FP_DOWNLOAD_FILE_HOST+"?filename="+URLEncoder.encode(new File(mMsg.mFilePath).getName())+"&authid="+Config.getValue(Config.AUTH_ID_FP)+"&familyid="+mMsg.mFamilyId+"&thumb=1";
    		
    		Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.US);

            String extention = MimeTypeMap.getSingleton().getExtensionFromMimeType(mMsg.mMMSCT);
            if(TextUtils.isEmpty(extention))
            {
            	return ret;
            }
            String newPicFile = df.format(date) +"."+ extention;
            //String outPath = Helper.getImageBackupDir() + newPicFile;
            
            mFile = File.createTempFile(df.format(date), extention);
            mFile.deleteOnExit();
            LogWriter.info("Final Download URL: "+ mPath);
            LogWriter.info("Output file path: "+ mFile);
    		URL url = new URL(mPath);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            //mFileLen = connection.getContentLength();
            
            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(mFile);

            byte data[] = new byte[1024];
            long total = 0;
            long updateCount = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                updateCount += count;
                if(updateCount > 2000)
                {
                	// publishing the progress....
                	publishProgress(total);
                	updateCount = 0;
                }
                output.write(data, 0, count);
            }
            ret = true;
            LogWriter.info("DownloadFile: result:"+ connection.getResponseCode());
            output.flush();
            output.close();
            input.close();
            
    		InputStream is = new FileInputStream(mFile);
    		LogWriter.info("Fileinput stream: "+ is);
            
            Bitmap bitmap = MMSHelper.reSizeBitmap(is,150, 2);
            if(bitmap != null)
            {
            	MMSHelper.addThumbImageToTable(mContext, mMsg.mDatabaseID, bitmap);
                if(!bitmap.isRecycled())
                	bitmap.recycle();
                bitmap = null;
            }
            is.close();
            mFile.delete();
            
    } catch (FileNotFoundException e) {
        // This session wasn't authenticated properly or user unlinked
    	LogWriter.err(e);
        mErrorMsg = "Invalid link";
    }  catch (MalformedURLException e) {
    	LogWriter.err(e);
    	mErrorMsg = "Invalid link";
		LogWriter.err(e);
	} catch (IOException e) {
		LogWriter.err(e);
		mErrorMsg = "Network error";
	}
    
    return ret;
    }
   
    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
*/ 

}
