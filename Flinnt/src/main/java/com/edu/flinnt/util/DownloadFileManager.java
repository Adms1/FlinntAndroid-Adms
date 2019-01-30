package com.edu.flinnt.util;

import android.util.Log;

import java.util.HashMap;

/**
 * the class is to manage downloads
 */
public class DownloadFileManager {

	static final String TAG = "DownloadFileManager";
	static HashMap<Long, DownloadMediaFile> map = new HashMap<Long, DownloadMediaFile>();

    /**
     * Add file to download
     * @param uf file
     */
	public static void add(DownloadMediaFile uf)
	{
		map.put(uf.id, uf);
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("DownloadFileManager: put"+ uf);
	}

    /**
     * remove file from downloading
     * @param id file id
     */
	public static void remove(long id)
	{
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("DownloadFileManager: remove"+ id);
		map.remove(id);
	}

    /**
     * get the downloading file from id
     * @param id file id
     * @return download media file
     */
	public static DownloadMediaFile get(long id)
	{
		DownloadMediaFile tmp = map.get(id);
		if (LogWriter.isValidLevel(Log.INFO)) LogWriter.info("DownloadFileManager: get"+ tmp);
		return tmp;
	}

    /**
     * checks for availability of media file in download file list
     * @param id file id
     * @return true if id is existing, false otherwise
     */
	public static boolean isContainID(long id){
		return map.containsKey(id);
	}
}
