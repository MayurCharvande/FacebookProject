package com.xplor.async_task;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.Adapter;
import com.xplor.local.syncing.download.SqlQuery;

public class EditUserProfileSyncTask extends AsyncTask<String, Integer, Cursor> {

	private Activity mActivity = null;
	private Adapter mAdapter = null;
	
	public EditUserProfileSyncTask(Activity activity) {
	    this.mActivity = activity;
	    // Local database create and open
        this.mAdapter = new Adapter(mActivity);
        mAdapter.createDatabase();
        mAdapter.open();
	}
	
	@Override
	protected Cursor doInBackground(String... param) {

		Cursor myCursor = mAdapter.getExucuteQurey(SqlQuery.updateQueryUserProfileSetting(
				Common.ADDRESS, Common.USER_NAME, Common.USER_PHONE_NO,Common.USER_ID));
		return myCursor;
	}
	
	@SuppressWarnings("unused")
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		LogConfig.logd("User profile strResponce =",""+mCursor.getCount());
		if(mCursor != null) {
			for(int i=0;i<Common.mySettingData.size();i++) {
			  mAdapter.getExucuteQurey(SqlQuery.updateQueryUserHealthSetting(Common.USER_ID, 
			  Common.mySettingData.get(i).getSTR_SETTING_NAME(), 
			  Common.mySettingData.get(i).getSTR_SETTING_VALUE(), 
			  Common.mySettingData.get(i).getSTR_CREATED_DATE(), 
			  Common.deviceModal, Common.getAndroidVersion(), WebUrls.WEB_SERVICE_VERSION, 
			  Common.generateMobileKey(), 0, Common.DEVICE_TYPE, 
			  Common.mySettingData.get(i).getSTR_SETTING_ID()));
		   }
		   Common.displayAlertButtonToFinish(mActivity,"Message", "Update user profile data.",true);
		} else {
		   Common.displayAlertSingle(mActivity,"Message", "Not update user profile data.");	
		}

	}
}
