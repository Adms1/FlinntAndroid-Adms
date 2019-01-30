package com.edu.flinnt.util;

import android.util.Log;

import java.util.HashMap;

/**
 * Upload manager class to manage upload events
 */
public class UploadFileManager {

	static final String TAG = "UploadManager";
	static HashMap<Long, UploadMediaFile> map = new HashMap<Long, UploadMediaFile>();

    /**
     * Add file to upload
      * @param uf uplaod file
     */
	public static void add(UploadMediaFile uf)
	{
		map.put(uf.id, uf);
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("UploadFile: put"+ uf);
	}

    /**
     * removes file being uploaded
     * @param id file id
     */
	public static void remove(long id)
	{
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("UploadFile: remove"+ id);
		map.remove(id);
	}

    /**
     * get upload file by id
     * @param id file id
     * @return upload file
     */
	public static UploadMediaFile get(long id)
	{
		UploadMediaFile tmp = map.get(id);
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("UploadFile: get"+ tmp);
		return tmp;
	}
}
