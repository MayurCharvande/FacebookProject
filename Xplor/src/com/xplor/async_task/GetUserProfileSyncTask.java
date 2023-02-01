package com.xplor.async_task;

import java.util.ArrayList;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.Model.SettingModal;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.NewsFeedRecordCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class GetUserProfileSyncTask extends AsyncTask<String, Integer, Cursor> {

	private Activity mActivity = null;
	private Adapter mDBHelper = null;
	private NewsFeedRecordCallBack mHomeListCallBack = null;
	
	public GetUserProfileSyncTask(Activity activity) {
	    this.mActivity = activity;
	    //Create and open sql_data base
	    this.mDBHelper = new Adapter(mActivity);
	    mDBHelper.createDatabase();
	    mDBHelper.open();
	}
	
	public void setCallBackUserProfile(NewsFeedRecordCallBack homeListCallBack) {
		this.mHomeListCallBack = homeListCallBack;
	}
	
	@Override
	protected Cursor doInBackground(String... param) {
		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryUserEducatorSetting(Common.USER_ID));
		return mCursor;
	}
	
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		
		LogConfig.logd("Get user profile responce =",""+mCursor.getCount());
		if(mCursor != null && mCursor.getCount() > 0) {
		   try { // Get child list form sql_database parent and educator tables 
			   Common.mySettingData = new ArrayList<SettingModal>();
			   if (mCursor.moveToFirst()) {
				 do {
				  SettingModal mSettingModal = new SettingModal();
				  mSettingModal.setSTR_USER_ID(mCursor.getString(mCursor.getColumnIndex("user_id")));
				  mSettingModal.setSTR_SETTING_ID(mCursor.getString(mCursor.getColumnIndex("user_setting_id")));
				  mSettingModal.setSTR_SETTING_NAME(mCursor.getString(mCursor.getColumnIndex("user_setting_name")));
				  mSettingModal.setSTR_SETTING_VALUE(mCursor.getInt(mCursor.getColumnIndex("user_setting_value")));
				  mSettingModal.setSTR_CREATED_DATE(mCursor.getString(mCursor.getColumnIndex("created_date")));
				  mSettingModal.setSTR_UPDATE_DATE(mCursor.getString(mCursor.getColumnIndex("updated_date")));
				  Common.mySettingData.add(mSettingModal);
				 } while (mCursor.moveToNext());
		       }
				  Cursor myCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryUserParentSetting(Common.USER_ID));
				  Common.EMAIL_ID = myCursor.getString(myCursor.getColumnIndex("email"));
				  Common.USER_NAME = myCursor.getString(myCursor.getColumnIndex("name"));
				  Common.ADDRESS = myCursor.getString(myCursor.getColumnIndex("address"));
				  Common.USER_PHONE_NO = myCursor.getString(myCursor.getColumnIndex("phone_no"));
				  Common.USER_IMAGE = myCursor.getString(myCursor.getColumnIndex("image"));
		     } catch (SQLiteException se) {
			    Log.e(getClass().getSimpleName(),"Could not create or Open the database");
			 } finally {
			    mDBHelper.close();
			    mCursor.close();
		     }
		    mHomeListCallBack.requestNewsFeedRecordCallBack("Success");
		} else {
			mHomeListCallBack.requestNewsFeedRecordCallBack("");
		}
	}
}
