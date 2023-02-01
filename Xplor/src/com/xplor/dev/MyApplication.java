package com.xplor.dev;

import com.xplor.local.syncing.upload.BackgroungSyncing;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {
	public void onCreate() {
		super.onCreate();
	}

	private Activity mCurrentActivity = null;
	private BackgroungSyncing mBackgroungSyncing  = null;
	
	private static boolean appGetversionRunning = false;
	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	public void setCurrentActivity(Activity mCurrentActivity) {
		this.mCurrentActivity = mCurrentActivity;
	}

	/**
	 * @return the appGetversionRunning
	 */
	public static boolean isAppGetversionRunning() {
		return appGetversionRunning;
	}

	/**
	 * @param appGetversionRunning the appGetversionRunning to set
	 */
	public static void setAppGetversionRunning(boolean appGetversionRunning) {
		MyApplication.appGetversionRunning = appGetversionRunning;
	}

	/**
	 * @return the mBackgroungSyncing
	 */
	public BackgroungSyncing getmBackgroungSyncing() {
		return mBackgroungSyncing;
	}

	/**
	 * @param mBackgroungSyncing the mBackgroungSyncing to set
	 */
	public void setmBackgroungSyncing(BackgroungSyncing mBackgroungSyncing) {
		this.mBackgroungSyncing = mBackgroungSyncing;
	}
}
