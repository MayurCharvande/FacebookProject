package com.xplor.async_task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.Model.EducatoreShift;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.EducatorAllShiftCallBack;
import com.xplor.local.syncing.download.SqlQuery;

@SuppressLint("SimpleDateFormat") 
public class EducatorAllShiftAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private EducatorAllShiftCallBack callBackShift = null;
	private Adapter mDBHelper = null;
	private ArrayList<EducatoreShift> allShiftList = null;
	
	public EducatorAllShiftAsyncTask(Activity activity) {
		this.mActivity = activity;
		// Local database create and open
        this.mDBHelper = new Adapter(mActivity);
        mDBHelper.createDatabase();
        mDBHelper.open();
	}

	public void setCallBack(EducatorAllShiftCallBack callBack) {
		callBackShift = callBack;
	}

	@Override
	protected String doInBackground(String... param) {

		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryCalenderEducatorAllShifts(Common.USER_ID));
		if(mCursor.getCount()>0) {
			allShiftList = new ArrayList<EducatoreShift>();
		  try {
			  if(mCursor.moveToFirst()) {
			    do {
				    EducatoreShift obj = new EducatoreShift();
					obj.shift_id = mCursor.getInt(mCursor.getColumnIndex("id"));
					obj.center_id = mCursor.getInt(mCursor.getColumnIndex("center_id"));
					obj.educator_id = mCursor.getInt(mCursor.getColumnIndex("educator_id"));
					obj.shift_date = mCursor.getString(mCursor.getColumnIndex("shift_date"));
					obj.shift_start_time = mCursor.getString(mCursor.getColumnIndex("shift_start_time"));
					obj.shift_end_time = mCursor.getString(mCursor.getColumnIndex("shift_end_time"));
					obj.break_hours = mCursor.getString(mCursor.getColumnIndex("break_hours"));
					obj.status = mCursor.getString(mCursor.getColumnIndex("status"));
					
					String timeStart = Common.convert24HrsFormateTo12Hrs(mCursor.getString(mCursor.getColumnIndex("shift_start_time")));
					//String endStart = Common.convert24HrsFormateTo12Hrs(mCursor.getString(mCursor.getColumnIndex("shift_end_time")));
					String convertedDate = "";
					SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date parsed;
					try {
						parsed = sourceFormat.parse(mCursor.getString(mCursor.getColumnIndex("shift_date")) + " " + timeStart);
						TimeZone tz = TimeZone.getDefault();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
						formatter.setTimeZone(tz);
						LogConfig.logd("shift date :=",""+ formatter.format(parsed));
						convertedDate = formatter.format(parsed);
					} catch (java.text.ParseException e) { 
						// parser exception
					} 

					if (!convertedDate.isEmpty()) {
						String[] startTime = convertedDate.split(" ");
						obj.shift_date = startTime[0];
						allShiftList.add(obj);
					}
					
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

		 LogConfig.logd("Calender shift responce =",""+responce);
		 if(responce.length()>0)
		   callBackShift.requestSetAllShift(allShiftList);
		 else callBackShift.requestSetAllShift(null);
	}
	
}
