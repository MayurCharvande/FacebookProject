package com.xplor.async_task;

import java.util.ArrayList;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.Model.EducatoreShift;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.EducatorShiftStartEndCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class EducatorDateShiftAsyncTask  extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private Adapter mDBHelper = null;
	private EducatorShiftStartEndCallBack mEducatorShiftStartEndCallBack = null;
	private ArrayList<EducatoreShift> tempList = null;
	private String unixDateStartTime ="";
	private String unixDateEndTime ="";
	
	public EducatorDateShiftAsyncTask(Activity activity,String strDate) {
		this.mActivity = activity;
		unixDateStartTime = Common.getCurrentStartDateToUnix(strDate);
		unixDateEndTime = Common.getCurrentEndDateToUnix(strDate);
		// Local database create and open
        this.mDBHelper = new Adapter(mActivity);
        mDBHelper.createDatabase();
        mDBHelper.open();
	}
	
	public void setCallBack(EducatorShiftStartEndCallBack educatorShiftStartEndCallBack) {
		this.mEducatorShiftStartEndCallBack = educatorShiftStartEndCallBack;
	}

	@Override
	protected String doInBackground(String... param) {

		   Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryCalenderEducatorDateShifts(Common.USER_ID,unixDateStartTime, unixDateEndTime));
		   if(mCursor.getCount()>0) {
				tempList = new ArrayList<EducatoreShift>();
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
				    	obj.shift_Room = mCursor.getString(mCursor.getColumnIndex("room_id"));//shift_room"));
				    	obj.status = mCursor.getString(mCursor.getColumnIndex("status"));
				    	obj.center_id = mCursor.getInt(mCursor.getColumnIndex("center_id"));
				    	obj.educator_id = mCursor.getInt(mCursor.getColumnIndex("educator_id"));
				    	tempList.add(obj);
						
				   } while(mCursor.moveToNext());
				     mCursor.close();
				   }
			    } catch (SQLiteException se) {
				    Log.e(getClass().getSimpleName(),"Could not create or Open the database");
				} finally {
					mDBHelper.close();
				}
			   return "Record";
			} else {
			   return "";
			}
	}

	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
	
		LogConfig.logd("Educator shift responce =",""+ responce);
		if(responce.length()>0)
		   mEducatorShiftStartEndCallBack.requestEducatorShiftStartEndList(tempList);
		else mEducatorShiftStartEndCallBack.requestEducatorShiftStartEndList(null);
	}

}

