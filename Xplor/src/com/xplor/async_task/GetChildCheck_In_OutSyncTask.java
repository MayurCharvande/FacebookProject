package com.xplor.async_task;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.local.syncing.download.SqlQuery;

public class GetChildCheck_In_OutSyncTask extends AsyncTask<String, Integer, Cursor> {
	
	private Activity mActivity = null;
	private Adapter mDbHelper = null;
	private String strChildId = "";
	private int mPosition = 0;
	
	public GetChildCheck_In_OutSyncTask(Activity activity,String childId,int position) {
		  this.mActivity = activity;
		  this.strChildId = childId;
		  this.mPosition = position;
		  // create object web-service handler class
		  this.mDbHelper = new Adapter(mActivity);
		  mDbHelper.createDatabase();
		  mDbHelper.open();
		  Common.getCurrentDateToStartEndDate();
	}
	
	@Override
	protected Cursor doInBackground(String... param) {

		Cursor mCursor = mDbHelper.getExucuteQurey(SqlQuery.getQueryChildCheckinout
				(strChildId, Common.STR_CURENT_START_DATE, Common.STR_CURENT_END_DATE));

		return mCursor;
	}
	
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		LogConfig.logd("Child checkin_out strResponce =",""+mCursor.getCount());
		
		if(mCursor != null && mCursor.getCount() > 0) {
			try {	
				if (mCursor != null) {
				   if (mCursor.moveToFirst()) {
					  do {  
						  Common.arrChildData.get(mPosition).setSTR_CHILD_CHECK_IN(mCursor.getString(mCursor.getColumnIndex("check_in")));
						  Common.arrChildData.get(mPosition).setSTR_CHILD_CHECK_OUT(mCursor.getString(mCursor.getColumnIndex("check_out")));
						} while (mCursor.moveToNext());
					 }
			    }
			} catch (NullPointerException e) {
				// null responce
			}
		}
	}
}
