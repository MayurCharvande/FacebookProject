package com.xplor.async_task;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.Model.EducatoreShift;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.dev.R;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.EducatorLeaveCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class UpcomingShiftAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private Adapter mDBHelper = null;
	private ProgressDialog _ProgressDialog = null;
	private EducatorLeaveCallBack mEducatorLeaveCallBack = null;
	private String unixDateStartTime = "",strShiftType = "";
	private ArrayList<EducatoreShift> allShiftList = null;
	
	public UpcomingShiftAsyncTask(Activity activity,String shiftType) {
		this.mActivity = activity;
		this.strShiftType = shiftType;
		// get current start and end date time to fix time add
		if(strShiftType.equals("Available")) {
		   unixDateStartTime = Common.getCurrentDate();
		} else if(strShiftType.equals("Future")) {
		  unixDateStartTime = Common.getCurrentStartDateToUnix("");
		} else {
	      unixDateStartTime = Common.getCurrentEndDateToUnix("");
		}
		this.allShiftList = new ArrayList<EducatoreShift>();
		// Local database create and open
        this.mDBHelper = new Adapter(mActivity);
        mDBHelper.createDatabase();
        mDBHelper.open();
	}
	
	public void setCallBack(EducatorLeaveCallBack educatorLeaveCallBack) {
		this.mEducatorLeaveCallBack = educatorLeaveCallBack;
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
	protected String doInBackground(String... param) {
		Cursor mCursor = null;
		if(strShiftType.equals("Available")) {
			mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryAvailableShift(unixDateStartTime,"0"));//Common.USER_ID));
		} else if(strShiftType.equals("Future")) {
			mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryFutureShift(unixDateStartTime,Common.USER_ID));
		} else {
			mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryPastShifts(unixDateStartTime,Common.USER_ID));
		}
		
		if(mCursor.getCount()>0) {
		 try {
			if(mCursor.moveToFirst()) {
			  do {
				    EducatoreShift obj = new EducatoreShift();
					obj.shift_id = mCursor.getInt(mCursor.getColumnIndex("id"));
					obj.shift_date = mCursor.getString(mCursor.getColumnIndex("shift_date"));
					obj.shift_start_time = mCursor.getString(mCursor.getColumnIndex("shift_start_time"));
					obj.shift_end_time = mCursor.getString(mCursor.getColumnIndex("shift_end_time"));
					obj.break_hours = mCursor.getString(mCursor.getColumnIndex("break_hours"));
					obj.shift_Hours = mCursor.getString(mCursor.getColumnIndex("duration_hours"));
					obj.shift_Minutes = mCursor.getString(mCursor.getColumnIndex("duration_minutes"));
					obj.shift_Room = getShiftRoomName(mCursor.getString(mCursor.getColumnIndex("room_id")));
					obj.status = mCursor.getString(mCursor.getColumnIndex("status"));
					obj.center_id = mCursor.getInt(mCursor.getColumnIndex("center_id"));
					obj.educator_id = mCursor.getInt(mCursor.getColumnIndex("educator_id"));
					allShiftList.add(obj);	
				 } while(mCursor.moveToNext());  
			   } 
		    } catch (SQLiteException se) {
			  Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		    } finally {
			  mDBHelper.close();
			  mCursor.close();
		    }
		}	
		
		return "Responce";
	}

	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		if(_ProgressDialog != null)
		   _ProgressDialog.dismiss();
		LogConfig.logd("Upcoming shift responce =",""+allShiftList.size());
		if(allShiftList != null && allShiftList.size()>0) {
		   mEducatorLeaveCallBack.requestSetEducatorLeave(allShiftList);
		} else {
		   Common.displayAlertButtonToFinish(mActivity, "Message", "No shifts available",true);
		}
	}
	
	private String getShiftRoomName(String roomId) {
		
    	Cursor cursor = null;
    	String name = "";
		try {
    	  cursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryRoomIdToRoomName(roomId));
		 if(cursor != null && cursor.getCount() > 0) {
			 name = cursor.getString(cursor.getColumnIndex("room_name"));
		 }
		} finally {
		  if(cursor != null)
		    cursor.close();
		}
		return name;
	}

}

