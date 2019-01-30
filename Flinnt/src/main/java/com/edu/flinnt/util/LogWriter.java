package com.edu.flinnt.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Writes and configure logs, set on/off logs
 */
public class LogWriter {
    static LogWriter logWriter = null;// new LogWriter();
    static DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
    static DateFormat logFileFormat = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss", Locale.US);
    static int MaxLineCount = 10000;
    static int MaxFiles = 5;

    private PrintWriter pw = null;
    private int iLineCount = 0;

    //default Log Level DEBUG
    public static int mLogLevel = Log.DEBUG;

    // Keep it false when play store release
    // Set true to get logs in files & LogCat
    private static boolean mIsDebug = false;

    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String SEPERATOR = "|";
    public static final String NEW_LINE = "\n";
    public static final String FILE_NAME = "LogWriter.java";

    public static boolean isApplicationDebug(Context context) {
        boolean ret = false;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0) {
                ret = true;
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
        return ret;
    }

//	public static LogWriter getObj()
//	{		
//		   
//		return logWriter;
//	}

    public static String getLogFileName() {
        //String strFileName = getLogPath() + "s" + System.currentTimeMillis()  + ".log";
        String strFileName = getLogPath() + "Flinnt_" + logFileFormat.format(new Date(System.currentTimeMillis())) + ".log";
        try {
            //System.out.print("Filename is " + strFileName);
            File file = new File(strFileName);
            file.createNewFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strFileName;
    }

    private synchronized void Initialize() {
        if (pw != null)
            pw.close();
        iLineCount = 0;
        UpdateFileList();
        try {
            pw = new PrintWriter(new File(LogWriter.getLogFileName()));
        } catch (FileNotFoundException e) {
            pw = null;
            if (LogWriter.isValidLevel(Log.ERROR)) LogWriter.err(e);
        }
    }


    private synchronized void UpdateFileList() {
        try {
            String path = getLogPath();
			/*
			final String ext = Filter;
			FilenameFilter filter = new FilenameFilter() {
	        	public boolean accept(File dir, String name) {
	            	return (name.indexOf(ext) != -1) ;
	        	}
	    	};
	    	*/
            File dir = new File(path);
            dir.mkdirs();
            String[] children = dir.list();
            // String[] children = dir.list(filter);
            // only recent last file replaced instead of most last file.
            java.util.Arrays.sort(children);
            for (int i = 0; i < children.length - 25; i++) {
                if (LogWriter.isValidLevel(Log.INFO))
                    LogWriter.write("Deleting file is " + children[i]);
                File file = new File(path + children[i]);
                file.delete();
            }
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }


    private LogWriter() {
        //System.out.println("LogWriter == >>");
        //mIsDebug = isApplicationDebug(FamilyContant.getContext());
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        //System.out.println("LogWriter << ==");
        //setLogLevel(Registrar.getLastResponse().mLogger.mLevel);
    }


    public static LogWriter getObject() {
        if (LogWriter.logWriter == null) {
            logWriter = new LogWriter();
            if (mIsDebug) {
                logWriter.Initialize();
            }
        }
        return logWriter;
    }


    public static void write(int level, String log) {
        // Don't print logs in files & LogCat when play store release
        if (!mIsDebug) {
            return;
        }

        //requested logStrig's level is compared with current LogLevel set by user
        //if mLogLevel<=level satisfies, then Log in file takes place.
        //else return without doing any thing.
        if (mLogLevel > level) {
            return;
        }
        //String strCompleteLogString = log;
        //synchronized (syncObject) {

        try {
            int index = 3;
            if (FILE_NAME.equals(Thread.currentThread().getStackTrace()[3].getFileName()))
                index = 4;
            /*
             * Insert date and time in above string
             */
            //if(LogWriter.isValidLevel(Log.INFO)) LogWriter.info(log + "\n");
            log = dateFormat.format(new Date(System.currentTimeMillis())) + SEPERATOR
                    + Thread.currentThread().getId() + SEPERATOR
                    + Thread.currentThread().getStackTrace()[index].getFileName() + COLON
                    + Thread.currentThread().getStackTrace()[index].getLineNumber() + SEPERATOR
                    + log;
            nativeLog(level, log);

            getObject().pw.write(log + NEW_LINE);
            getObject().pw.flush();
            if (getObject().iLineCount++ > MaxLineCount) {
                getObject().iLineCount = 0;
                getObject().Initialize();
            }
        } catch (Exception e) {
            //if(LogWriter.isValidLevel(Log.INFO)) LogWriter.info(log);
            nativeLog(level, log);
        }
    }

    public static void err(Exception e) {
		/*
		if(getObject() != null && getObject().pw != null) {
			e.printStackTrace(getObject().pw)
		}
		*/
        if (mIsDebug) {
            if (getObject() != null && getObject().pw != null) {
                e.printStackTrace(getObject().pw);
            }
            e.printStackTrace();
        }

    }

    public static void info(String str) {
        write(Log.INFO, str);
    }

    public static void debug(String str) {
        write(Log.DEBUG, str);
    }

    public static void warn(String str) {
        write(Log.WARN, str);
    }

    public static void err(String str) {
        write(Log.ERROR, str);
    }

    public static void write(String log) {

        write(Log.DEBUG, log);

    }

    public static String getLogPath() {
        //return "/sdcard/Flinnt/logs";
        //File tmp = new File("/data/data/net.tifamily.android/logs/");
        //File tmp = new File(FlinntApplication.getContext().getFilesDir().getPath()+"/logs");
        File tmp = new File(Environment.getExternalStorageDirectory() + "/Flinnt/logs");
        //Log.d("Logwriter", "getLogPath :: " + tmp.getPath());
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        return tmp.getAbsolutePath() + "/";
    }

    //sets new log level.
    public static int setLogLevel(int newLevel) {
        //write("setLogLevel :: " + newLevel);
        mLogLevel = newLevel;
        if (mIsDebug == true) {
            mLogLevel = Log.DEBUG;
        }
        return mLogLevel;
    }

    public static boolean isValidLevel(int level) {
        if (mLogLevel > level) {
            return false;
        }
        return true;
    }

    public static void nativeLog(int level, String str) {
        if (mIsDebug)
            Log.println(level, "Flinnt", str);
//		switch(level)
//		{
//		case Log.DEBUG:
//			break;
//		case Log.INFO:
//			break;
//		case Log.
//		}
//		if(level == Log.DEBUG)
//		{
//			Log.
//		}
    }

    public static String getExceptionStackTraceAsString(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
