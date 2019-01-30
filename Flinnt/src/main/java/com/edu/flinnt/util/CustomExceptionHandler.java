package com.edu.flinnt.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CustomExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultUEH;

    /* 
     * if any of the parameters is null, the respective functionality 
     * will not be used 
     */
    public CustomExceptionHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stackTrace = result.toString();
        
        if(!stackTrace.contains("OutOfMemoryError"))
        {
        	StackTraceElement[] arr = e.getStackTrace();

        	String report = e.toString() + "\n\n";
        	report += "uncaughtException : \n"+ stackTrace+"\n\n";

        	report += "--------- Stack trace ---------\n\n";
        	for (int i = 0; i < arr.length; i++) {
        		report += "    " + arr[i].toString() + "\n";
        	}
        	report += "-------------------------------\n\n";

        	// If the exception was thrown in a background thread inside
        	// AsyncTask, then the actual exception can be found with getCause
        	report += "--------- Cause ---------\n\n";
        	Throwable cause = e.getCause();
        	if (cause != null) {
        		report += cause.toString() + "\n\n";
        		arr = cause.getStackTrace();
        		for (int i = 0; i < arr.length; i++) {
        			report += "    " + arr[i].toString() + "\n";
        		}
        	}
        	report += "-------------------------------\n\n";

        	final String reportText=report;

        	LogWriter.write(reportText);

			Log.d("Custt","error is : "+reportText);
			//showConformationDialog(MyApplication.getContext(), reportText);
        }
        //SystemClock.sleep(15000);
        defaultUEH.uncaughtException(t, e);
        //Process.killProcess(Process.myPid());
        //System.exit(1);

    }
    
    /*
    public static void showConformationDialog(final Context context, final String crashStack)
    {	

    	//refresh list
    	String title = "PSB crash report";

    	StringBuilder builder = new StringBuilder();
    	builder.append("PSB version:"+ MainTabActivity.VERSION +"\n");
    	builder.append("Device Model:"+ android.os.Build.MODEL+"\n");
    	builder.append("OS Version:"+ android.os.Build.VERSION.SDK+"\n");
    	builder.append("Crash Report:"+ crashStack+"\n");

    	try {
    		Intent dialogIntent = new Intent(context, CrashReportDialog.class);
    		dialogIntent.putExtra("TEXT", builder.toString());
    		dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		context.getApplicationContext().startActivity(dialogIntent);

    	} catch (Exception e) {
    		LogWriter.err(e);
    	}

    }
    */
}
