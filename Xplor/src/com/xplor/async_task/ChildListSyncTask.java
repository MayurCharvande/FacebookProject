package com.xplor.async_task;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.dev.MainScreenActivity;
import com.xplor.dev.R;
import com.xplor.helper.Adapter;
import com.xplor.local.syncing.download.SqlQuery;
import com.xplor.parsing.ChildDataParsing;

public class ChildListSyncTask extends AsyncTask<String, Integer, Cursor> {
	
	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private Adapter mDbHelper = null;
	
	public ChildListSyncTask(Activity activity,ProgressDialog progressdialog) {
		 this.mActivity = activity;
		 this._ProgressDialog = progressdialog;
		 // create object sql data base open and create
		 mDbHelper = new Adapter(mActivity);
		 mDbHelper.createDatabase();
		 mDbHelper.open();	
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if(_ProgressDialog == null) {
		   _ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
		   _ProgressDialog.setCancelable(false);
		   _ProgressDialog.setContentView(R.layout.loading_view);
		   WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
		   wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
		   wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		   wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;;
		  _ProgressDialog.getWindow().setAttributes(wmlp);
		}
	}
	
	@Override
	protected Cursor doInBackground(String... param) {
        
		Cursor cursor = mDbHelper.getSyncParentChildListData(SqlQuery.getChildListQuery(Common.USER_ID));
		return cursor;
	}
	
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		LogConfig.logd("Child list mCursor =",""+mCursor);
		
		if(mCursor != null && mCursor.getCount()>0) {
		   
			_ProgressDialog.dismiss();
			Common.arrChildData = new ArrayList<ChildDataParsing>();
			Common.arrChildData.clear();
			try { // get child list form sql database parent and educator tables 
				   if (mCursor.moveToFirst()) {
					 do {  
						  ChildDataParsing child_data = new ChildDataParsing();
						  child_data.setSTR_CHILD_ID(mCursor.getString(mCursor.getColumnIndex("id")));
						  child_data.setSTR_CHILD_CENTER_ID(mCursor.getString(mCursor.getColumnIndex("center_id")));
						  child_data.setSTR_CHILD_CENTER_ROOM_ID(mCursor.getString(mCursor.getColumnIndex("room_id")));
						  child_data.setSTR_CHILD_NAME(Common.capFirstLetter(mCursor.getString(mCursor.getColumnIndex("first_name"))+" "+mCursor.getString(mCursor.getColumnIndex("last_name"))));
						  child_data.setSTR_CHILD_FIRST_NAME(Common.capFirstLetter(mCursor.getString(mCursor.getColumnIndex("first_name"))));
						  child_data.setSTR_CHILD_LAST_NAME(Common.capFirstLetter(mCursor.getString(mCursor.getColumnIndex("last_name"))));
						  child_data.setSTR_CHILD_GENDER(mCursor.getString(mCursor.getColumnIndex("gender")));
						  child_data.setSTR_CHILD_IMAGE(mCursor.getString(mCursor.getColumnIndex("image")));
						  child_data.setSTR_CHILD_CENTER_NAME(getCenterName(mCursor.getString(mCursor.getColumnIndex("center_id"))));
						  //child_data.setSTR_CHILD_CHECK_IN(c.getString(c.getColumnIndex("check_in")));
						  //child_data.setSTR_CHILD_CHECK_OUT(c.getString(c.getColumnIndex("check_out")));
						  child_data.setSTR_CHILD_AGE(Common.getChildDOB(Common.convertDateFormatDOB(mCursor.getString(mCursor.getColumnIndex("dob")))));
						  child_data.setSTR_USER_TYPE(Common.USER_TYPE);
						  child_data.setVIEW_ONLY(false);
						  Common.arrChildData.add(child_data);
						} while (mCursor.moveToNext());
				   }
				   
				   for(int i=0;i<Common.arrChildData.size();i++) {
					   
					   String strCheckinOut = getCheckInOut(Common.arrChildData.get(i).getSTR_CHILD_ID(),Common.STR_CURENT_START_DATE,Common.STR_CURENT_END_DATE);
					   if(strCheckinOut.length() > 0) {
						   String [] array = strCheckinOut.split(",");
						   Common.arrChildData.get(i).setSTR_CHILD_CHECK_IN(array[0]);
						   Common.arrChildData.get(i).setSTR_CHILD_CHECK_OUT(array[1]);
					   } 
				   }
				
				  if(Common.arrChild_Invite != null && Common.INVITE_COUNT <= Common.arrChild_Invite.length) {
				     InviteCountSyncTask mInviteCountSyncTask = new InviteCountSyncTask(mActivity,_ProgressDialog);
				     mInviteCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
			      } else {        
				     _ProgressDialog.dismiss();
			         // call class to get response
			         Intent intent = new Intent(mActivity, MainScreenActivity.class);
			         mActivity.startActivity(intent);
			         mActivity.finish();
			         mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
			      }
				
			} catch (SQLiteException se) {
				Log.e(getClass().getSimpleName(),"Could not create or Open the database");
			} finally {
				mDbHelper.close();
			}
        } else {
	   
	      if(Common.INVITE_TYPE.length()> 0) {
		     Common.arrChild_Invite = Common.INVITE_TYPE.split(",");
		     InviteCountSyncTask mInviteCountSyncTask = new InviteCountSyncTask(mActivity,_ProgressDialog);
		     mInviteCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
	       } else {
	         _ProgressDialog.dismiss();
	         Common.displayAlertSingle(mActivity,mActivity.getResources().getString(R.string.str_Message),
	        		 mActivity.getResources().getString(R.string.str_NoRecordFound));
	       }
        }
	}
	
    public String getCenterName(String centerId) {
    	try {
		 if(centerId != null && centerId.length() > 0) {
		   Cursor mCursor = mDbHelper.getSyncParentChildListData(SqlQuery.getCenterNameQuery(centerId));
		   String center_name = mCursor.getString(mCursor.getColumnIndex("center_name"));
		   return center_name;
		 }
    	} catch(CursorIndexOutOfBoundsException e) {
    		// value index 
    	}
		return "";
	}
    
    public String getCheckInOut(String childId, String startDate,String endDate) {
		try {
		   if(childId != null && childId.length() > 0) {
			  Cursor mCursor = mDbHelper.getSyncParentChildListData(SqlQuery.getChildCheckedInOut(childId, startDate, endDate));
			  String check_in = mCursor.getString(mCursor.getColumnIndex("checkin_time"));
			  if (check_in != null && check_in.length() != 0 && !check_in.equals("0000-00-00 00:00:00")) {
				  check_in = "1";
		      } else {
		    	  check_in = "0";
		      }
			  String check_out = mCursor.getString(mCursor.getColumnIndex("checkout_time"));
			  if (check_out != null && check_out.length() != 0 && !check_out.equals("0000-00-00 00:00:00")) {
				  check_out = "1";
		      } else {
		    	  check_out = "0";
		      }
			  return check_in+","+check_out;
		   }
		 } catch(CursorIndexOutOfBoundsException e) {
    		// value index 
    	 }
	     return "0,0";
	}
}
