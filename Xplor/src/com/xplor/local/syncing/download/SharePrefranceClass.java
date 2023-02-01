package com.xplor.local.syncing.download;

import com.xplor.dev.R;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrefranceClass {

	public static String CHILD_ID = "child_id";
	
	/** Call to get the AppGroupCode. */
	public static String getCHILD_ID_TO_LMD(Context context,String child_id) {
		SharePrefranceClass.CHILD_ID = child_id;
		return getInstance(context).getString(SharePrefranceClass.CHILD_ID, "");
	}
	
	/** Call to set the AppGroupCode. */
	public static void setCHILD_ID_TO_LMD(String child_id, String last_modifydate,Context context) {
		SharePrefranceClass.CHILD_ID = child_id;
		getInstance(context).edit().putString(SharePrefranceClass.CHILD_ID, last_modifydate.trim()).commit();
	} 
	
	public static SharedPreferences getInstance(Context context) {
		return context.getSharedPreferences(context.getResources().getString(R.string.app_name), 0);
	}
}
