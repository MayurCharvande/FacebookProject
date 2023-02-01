package com.xplor.async_task;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.ReceiverCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class GetChildProfileSyncTask extends AsyncTask<String, Integer, Cursor> {

	private Activity mActivity = null;
	private Adapter mDBHelper = null;
	private ReceiverCallBack mReceiver = null;
	
	public GetChildProfileSyncTask(Activity activity) {
	    this.mActivity = activity;
	    this.mDBHelper = new Adapter(mActivity);
	    mDBHelper.createDatabase();
	    mDBHelper.open();
	}
	
	public void setCallBackReciver(ReceiverCallBack receiver) {
		this.mReceiver = receiver;
	}
	
	@Override
	protected Cursor doInBackground(String... param) {

		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryParentChildProfileChildId(Common.CHILD_ID));
		return mCursor;
	}
	
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		 LogConfig.logd("User profile responce =",""+mCursor.getCount());
		 
		if(mCursor != null && mCursor.getCount() > 0) {
		  try {
			 
			  Common.CHILD_ID = mCursor.getString(mCursor.getColumnIndex("id"));
			  Common.CENTER_ID = mCursor.getString(mCursor.getColumnIndex("center_id"));
			  Common.CHILD_FIRST_NAME = mCursor.getString(mCursor.getColumnIndex("first_name"));
			  Common.CHILD_LAST_NAME = mCursor.getString(mCursor.getColumnIndex("last_name"));
			  
			  if(Common.CHILD_FIRST_NAME == null) Common.CHILD_FIRST_NAME ="";
			  if(Common.CHILD_LAST_NAME == null) Common.CHILD_LAST_NAME ="";
			  
			  Common.CHILD_NAME = Common.CHILD_FIRST_NAME+" "+Common.CHILD_LAST_NAME;
			  Common.CHILD_GENDER = mCursor.getString(mCursor.getColumnIndex("gender"));
			  Common.CHILD_IMAGE = mCursor.getString(mCursor.getColumnIndex("image"));
			  Common.CHILD_ALLERGIES = mCursor.getString(mCursor.getColumnIndex("allergies"));
			  Common.CHILD_DOB = Common.convertDateFormatDOB(mCursor.getString(mCursor.getColumnIndex("dob")));
			  Common.CHILD_AGE = Common.getChildDOB(Common.CHILD_DOB);
			  Common.CHILD_BIO = mCursor.getString(mCursor.getColumnIndex("bio"));
			  Common.CHILD_EMERG_PHONENO_1 = mCursor.getString(mCursor.getColumnIndex("emergency_phone"));
			  Common.CHILD_EMERG_PHONENO_2 = mCursor.getString(mCursor.getColumnIndex("emergency_phone2"));
			  Common.CHILD_EMERG_NAME_1 = mCursor.getString(mCursor.getColumnIndex("emergency_name"));
			  Common.CHILD_EMERG_NAME_2 = mCursor.getString(mCursor.getColumnIndex("emergency_name2"));
			  Common.CHILD_MEDICATION = mCursor.getString(mCursor.getColumnIndex("medication"));
			  Common.CHILD_NEEDS = mCursor.getString(mCursor.getColumnIndex("needs"));
//			  Common.CHILD_LATLONG = mCursor.getString(mCursor.getColumnIndex("latlng"));
			  getMultipleParentsDetails();
			  getCenterDetails();
		  } catch (SQLiteException se) {
			  Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		  } finally {
			 mDBHelper.close();
			 mCursor.close();
		  }
		  mReceiver.requestFinish("Success");
	    }
	}

	public void getMultipleParentsDetails() {
		
		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryParentChildProfile(Common.CHILD_ID));
		if(mCursor != null && mCursor.getCount() > 0) {
			Common.CHILD_PERENT_NAME = "";
		  try {
			  if(mCursor.moveToFirst()) {
				do {
					Common.CHILD_PERENT_NAME += mCursor.getString(mCursor.getColumnIndex("name"))+",";
					//Common.CHILD_PERENT_ADDRESS += mCursor.getString(mCursor.getColumnIndex("address"));
				    //Common.CHILD_PERENT_PHONE_NO += mCursor.getString(mCursor.getColumnIndex("phone_no"));
				} while(mCursor.moveToNext());
			  }
		  } catch (SQLiteException se) {
			  Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		  } finally {
			// mDBHelper.close();
			 mCursor.close();
		  }
		}
	}
    
    public void getCenterDetails() {
    	
    	if(Common.CENTER_ID != null && Common.CENTER_ID.length()>0) {
    	  Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryParentChildCenter(Common.CENTER_ID));
		  if(mCursor != null && mCursor.getCount() > 0) {
		    try { 
			  if(mCursor.moveToFirst()) {
				do {
					  Common.CHILD_CENTER_ADDRESS = mCursor.getString(mCursor.getColumnIndex("address"));
					  Common.CHILD_CENTER_PHONENO = mCursor.getString(mCursor.getColumnIndex("phone_no"));
					  Common.CHILD_CENTER_LATLONG = mCursor.getString(mCursor.getColumnIndex("center_latlng"));
				} while(mCursor.moveToNext());
			  }
		  } catch (SQLiteException se) {
			  Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		  } finally {
			 mCursor.close();
		  }
		}
      }
	}
	
}
