package com.edu.flinnt;


import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.edu.flinnt.downloads.CrashHandler;
import com.edu.flinnt.downloadsmultithread.DownloadConfiguration;
import com.edu.flinnt.downloadsmultithread.DownloadManager;
import com.edu.flinnt.services.LocationService;
import com.edu.flinnt.util.CustomExceptionHandler;
import com.edu.flinnt.util.LogWriter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;


/*
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
*/

/**
 * Base Application class
 */
public class FlinntApplication extends Application {

	public static final String TAG = FlinntApplication.class.getSimpleName();
	public static Context mContext = null;
	private Tracker mTracker;
	private static final String TRACKING_ID = "UA-46829088-2";
	private static boolean isInBackground = false;

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		mContext = getApplicationContext();
		//Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
		initDownloader();
		//CrashHandler.getInstance(mContext);
		Realm.init(mContext);

		RealmConfiguration config = new RealmConfiguration
				.Builder()
				.deleteRealmIfMigrationNeeded()
				.build();
				Realm.setDefaultConfiguration(config);


		/*Stetho.initialize(
				Stetho.newInitializerBuilder(this)
						.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
						.enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
						.build());*/

		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

			@Override
			public void onActivityStopped(Activity activity) {
			}

			@Override
			public void onActivityStarted(Activity activity) {
			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}

			@Override
			public void onActivityResumed(Activity activity) {

				if(isInBackground){
					LogWriter.write("app went to foreground");
					startLocationService();
					isInBackground = false;
				}
			}

			@Override
			public void onActivityPaused(Activity activity) {
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
			}

			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

			}
		});


	}
	private void startLocationService() {
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
	}
	/** Return application static context */
	public static Context getContext() { return mContext; }
	private void initDownloader() {
		DownloadConfiguration configuration = new DownloadConfiguration();
		configuration.setMaxThreadNum(10);
		configuration.setThreadNum(3);
		DownloadManager.getInstance().init(getApplicationContext(), configuration);
	}

	/**
	 * Gets the default {@link Tracker} for this {@link Application}.
	 * @return tracker
	 */
	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
			//mTracker = analytics.newTracker(R.xml.global_tracker);
			mTracker = analytics.newTracker(TRACKING_ID);
		}
		return mTracker;
	}
//	@Override
//	public void onTrimMemory(int level) {
//		if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
//			LogWriter.write("app went to background");
//			isInBackground = true;
//		}
//	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}