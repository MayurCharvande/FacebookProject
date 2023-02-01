package com.xplor.async_task;

import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.Model.EducatoreBreakHistory;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.BreakHistotyCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class BreakHistoryAsyncTask extends AsyncTask<String, Integer, Cursor> {

	private Activity mActivity = null;
	private BreakHistotyCallBack mBreakHistotyCallBack = null;
	private Adapter mDBHelper = null;
	
	public BreakHistoryAsyncTask(Activity activity) {
		this.mActivity = activity;
		// Local database create and open
		this.mDBHelper = new Adapter(mActivity);
		mDBHelper.createDatabase();
		mDBHelper.open();
	}
	
	public void setCallBack(BreakHistotyCallBack breakHistotyCallBack) {
		this.mBreakHistotyCallBack = breakHistotyCallBack;
	}

	@Override
	protected Cursor doInBackground(String... param) {
		
		Cursor myCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryBreakHistory(Common.USER_ID));
		return myCursor;
	}

	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		LogConfig.logd("Break History responce =",""+mCursor.getCount());
		
		if(mCursor.getCount()>0) {
			ArrayList<EducatoreBreakHistory> arrBreakHistory = new ArrayList<EducatoreBreakHistory>();
		 try {
			if(mCursor.moveToFirst()) {
			  do { 
				  EducatoreBreakHistory obj = new EducatoreBreakHistory();
					obj.break_id = mCursor.getInt(mCursor.getColumnIndex("id"));
					obj.start_break_time = mCursor.getString(mCursor.getColumnIndex("start_break_time"));
					obj.end_break_time = mCursor.getString(mCursor.getColumnIndex("end_break_time"));
					obj.status = mCursor.getString(mCursor.getColumnIndex("status"));
					//obj.date = Common.convertDateTolocalFormate(mCursor.getString(mCursor.getColumnIndex("date")));
					obj.educator_id = mCursor.getInt(mCursor.getColumnIndex("educator_id"));
					obj.shift_id = mCursor.getInt(mCursor.getColumnIndex("shift_id"));

					// Show only those records whose start, end break time and break time is not 0
					if (Common.isValidDate(mCursor.getString(mCursor.getColumnIndex("start_break_time")))
							&& Common.isValidDate(mCursor.getString(mCursor.getColumnIndex("end_break_time")))
							&& (calculateTimeDifference(mCursor.getString(mCursor.getColumnIndex("start_break_time")),
									mCursor.getString(mCursor.getColumnIndex("end_break_time"))) > 0)) {
						arrBreakHistory.add(obj);
				    }
				  
				 } while(mCursor.moveToNext());
			     mCursor.close();
			   } 
		    } catch (SQLiteException se) {
			  Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		    } finally {
			  mDBHelper.close();
		    }
		   mBreakHistotyCallBack.requestBreakHistoryList(arrBreakHistory);
		}	
	}
	
	long calculateTimeDifference(String startTime, String endTime) {

		Date startDate = Common.convertStringDateTolocalDate(startTime);
		Date endDate = Common.convertStringDateTolocalDate(endTime);
		long diff = endDate.getTime() - startDate.getTime();
		long seconds = diff / 1000;
		return seconds;
	}
 }
