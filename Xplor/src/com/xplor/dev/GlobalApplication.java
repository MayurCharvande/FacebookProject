package com.xplor.dev;

import java.util.List;
import com.xplor.async_task.Present_ShiftsAsyncTask;
import com.xplor.async_task.ServerTimeSyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;

public class GlobalApplication extends android.app.Application {
	
	public static Activity mActivity = null;
    
    public static void onActivityForground(Activity activity) {
    	mActivity = activity;
    	Common._isAPP_FORGROUND = !isApplicationSentToBackground(mActivity);
    	//System.out.println("onActivityForground ="+Common._isAPP_FORGROUND);
    	foregroundOrBackground();
    	if(!Common._isAPP_FORGROUND) {
			Common.bolCallMethod = true;
		}
    }

    public static boolean isApplicationSentToBackground(final Context context) {
		
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	    if (!tasks.isEmpty()) {
	        ComponentName topActivity = tasks.get(0).topActivity;
	        if (!topActivity.getPackageName().equals(context.getPackageName())) {
	            return true;
	        }
	    }
	    return false;
	}
    
    public static void foregroundOrBackground() {

    	Validation validation = new Validation(mActivity);
		if(validation.checkNetworkRechability() && Common._isAPP_FORGROUND && Common.bolCallMethod) {
			if(Common.USER_TYPE.equals("1") && Common.USER_ID.length() > 0) {
				Present_ShiftsAsyncTask mPresent_ShiftsAsyncTask = new Present_ShiftsAsyncTask(mActivity);
				mPresent_ShiftsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
			} 
			ServerTimeSyncTask mServerTimeSyncTask = new ServerTimeSyncTask(mActivity);
			mServerTimeSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
			Common.bolCallMethod = false;
		}
    }
    
}
