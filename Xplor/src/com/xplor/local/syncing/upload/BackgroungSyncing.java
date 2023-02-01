package com.xplor.local.syncing.upload;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.xplor.async_task.GetSqlTableZipFileSyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;
import com.xplor.dev.R;
import com.xplor.local.syncing.download.SyncingUtil;

@SuppressLint("HandlerLeak") 
public class BackgroungSyncing {

	private Context mContext;
	private Activity mActivity = null;
	private Timer timer = null;
	private Validation mValidation = null;

	public BackgroungSyncing(Activity activity) {
		this.mContext = activity;
		this.mActivity = activity;
		// Start Database Syncing
	    this.mValidation = new Validation(mContext);
	}

	public void startBGSyncing() {
       if(timer == null) {
		  // Declare the timer
		  timer = new Timer();
		  // Set the schedule function and rate
		  timer.scheduleAtFixedRate(new TimerTask() {
			 @Override
			 public void run() {
				// Called each time when 1000 milliseconds (1 second) (the period parameter)
				LogConfig.logd("Timer scheduler ", "BackgroungSyncing syncing.........");
				handler.sendEmptyMessage(0);

			} // Set how long before to start calling the TimerTask (in milliseconds)
		},0,// Set the amount of time between each execution (in milliseconds)
		 120000); // 120000 run after every 15 minutes 900000; 2 min
       }
	}
	
	public void stopBGSyncing() {
		if(timer != null) {
		   timer.cancel();
		   timer = null;
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			  if(mValidation.checkNetworkRechability()) {
				if (Common.isDatabaseSyncingInProgress && Common._isDatabaseSyncingFromBackground) {
					LogConfig.logd("BackGroundSync", "Data BackGroundSync Start");
					GetSqlTableZipFileSyncTask mSyncDatabaseTask = new GetSqlTableZipFileSyncTask(mActivity,null);
					SharedPreferences sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
					Boolean _isExecute = sPrefs.getBoolean("EXECUTE_SERVICE", false);
					if (!_isExecute) {
						mSyncDatabaseTask.setAction(SyncingUtil.CHILD_DATA_DOWNLOAD);
					} else {
						mSyncDatabaseTask.setAction(SyncingUtil.UPLOAD_DATABASE_ACTION);
					}
					mSyncDatabaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					mActivity.sendBroadcast(new Intent("updateNewsFeed"));
					mActivity.sendBroadcast(new Intent("updateChildList"));
				} else {
					LogConfig.logd("BackGroundSync", "Allready DataBase BackGroundSync running");
				}
				
			} else {
				LogConfig.logd("BackGroundSync", "No network connection");
				Common._isDatabaseSyncingFromBackground = true;
			}
		}
	};

}
