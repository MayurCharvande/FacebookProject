package com.xplor.async_task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.dev.R;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.CallBackEducatorChildList;
import com.xplor.local.syncing.download.SqlQuery;
import com.xplor.parsing.ChildDataParsing;

public class EducatorChildListSyncTask extends AsyncTask<String, Integer, String> {
	
	private Activity mActivity = null;
	private Adapter mDbHelper = null;
	private CallBackEducatorChildList mCallBackEducatorChildList = null;
	private Boolean isLoading = false;
	
	public EducatorChildListSyncTask(Activity activity,String search,ProgressDialog progress,Boolean _isLoading) {
		 this.mActivity = activity;
		 this.isLoading = _isLoading;
		 this.mDbHelper = new Adapter(mActivity);
		  mDbHelper.createDatabase();
		  mDbHelper.open();
	}
	
	public void setCallBack(CallBackEducatorChildList callBackEducatorChildList) {
		this.mCallBackEducatorChildList = callBackEducatorChildList;
	}
	
	@Override
	protected String doInBackground(String... param) {
		     
		     Cursor mCursor = mDbHelper.getSyncParentChildListData(SqlQuery.getEducatorChildList(Common.CENTER_ID, Common.ROOM_ID));
		     if(mCursor != null && mCursor.getCount()>0) {
		        try { 
		    	  if (mCursor != null) {
					   if (mCursor.moveToFirst()) {
						 do {  
							  ChildDataParsing child_data = new ChildDataParsing();
							  child_data.setSTR_CHILD_ID(mCursor.getString(mCursor.getColumnIndex("id")));
							  child_data.setSTR_CHILD_NAME(Common.capFirstLetter(mCursor.getString(mCursor.getColumnIndex("first_name"))+" "+mCursor.getString(mCursor.getColumnIndex("last_name"))));
							  child_data.setSTR_CHILD_FIRST_NAME(Common.capFirstLetter(mCursor.getString(mCursor.getColumnIndex("first_name"))));
							  child_data.setSTR_CHILD_LAST_NAME(Common.capFirstLetter(mCursor.getString(mCursor.getColumnIndex("last_name"))));
							  child_data.setSTR_CHILD_GENDER(mCursor.getString(mCursor.getColumnIndex("gender")));
							  child_data.setSTR_CHILD_IMAGE(mCursor.getString(mCursor.getColumnIndex("image")));
							  child_data.setSTR_CHILD_AGE(Common.getChildDOB(Common.convertDateFormatDOB(mCursor.getString(mCursor.getColumnIndex("dob")))));
							  child_data.setSTR_CHILD_CENTER_NAME(getCenterName(Common.CENTER_ID)); 
							  child_data.setSTR_CHILD_CENTER_ID(Common.CENTER_ID);  
							  child_data.setSTR_CHILD_CHECK_IN("0");
							  child_data.setSTR_CHILD_CHECK_OUT("0");
							  child_data.setVIEW_ONLY(true);
							  child_data.setSTR_USER_TYPE("1");
							  Common.arrChildData.add(child_data);
							  
							} while (mCursor.moveToNext());
					   }
					}
		    	   getCenterName();
		    	   return "Record";
		    	} catch (SQLiteException se) {
					Log.e(getClass().getSimpleName(),"Could not create or open the database.");
				} finally {
					if(mCursor != null)
					   mCursor.close();
					mDbHelper.close();	
				}
		      } else {
		    	  if(mCursor != null)
					   mCursor.close();
		    	   mDbHelper.close();
		      }
		  return "";
		
	}
	
	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		 LogConfig.logd("Educator child list responce =",""+responce);
		 
		 if(responce.length() == 0) {
			Common.isDisplayMessage_Called = false;
			Common.displayAlertButtonToFinish(mActivity, mActivity.getResources().getString(R.string.str_Message),
					   mActivity.getResources().getString(R.string.no_child_present), true);
		 } else {
		    mCallBackEducatorChildList.requestCallBackEducatorChildList(null,isLoading);
		 }
	}
	
	private void getCenterName() {
		 try { // get last modify date according to user id
			 Cursor cursor = mDbHelper.getExucuteQurey(SqlQuery.getEducatorCenterName(Common.CENTER_ID));
			 Common.CENTER_NAME = cursor.getString(cursor.getColumnIndex("name"));
		  } catch(IllegalStateException e) {
			 Common.CENTER_NAME = "";
		  } catch(IndexOutOfBoundsException e) {
		     Common.CENTER_NAME = "";
		  }
	}
	
	public String getCenterName(String centerId) {
		Cursor mCursor = null;
    	try {
		   if(centerId != null && centerId.length() > 0) {
		     mCursor = mDbHelper.getSyncParentChildListData(SqlQuery.getCenterNameQuery(centerId));
		     String center_name = mCursor.getString(mCursor.getColumnIndex("center_name"));
		     return center_name;
		   }
    	} catch(CursorIndexOutOfBoundsException e) {
    		// value index 
    		if(mCursor != null)
			   mCursor.close();
    	}
		return "";
	}
	
}
