package com.xplor.async_task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.xplor.interfaces.EducatorLeaveCallBack;
import com.xplor.local.syncing.download.SqlQuery;

@SuppressLint("SimpleDateFormat") 
public class EducatorLeaveAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private Adapter mDBHelper = null;
	private EducatorLeaveCallBack callBackShift = null;
	private ArrayList<EducatoreShift> allShiftList = null;

	public EducatorLeaveAsyncTask(Activity activity) {
		this.mActivity = activity;
		// Local database create and open
        this.mDBHelper = new Adapter(mActivity);
        mDBHelper.createDatabase();
        mDBHelper.open();
	}
	
	public void setCallBack(EducatorLeaveCallBack callBack) {
		callBackShift = callBack;
	}

	@Override
	protected String doInBackground(String... param) {

		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryCalenderEducatorLeaves(Common.USER_ID));
		if(mCursor.getCount()>0) {
			  allShiftList = new ArrayList<EducatoreShift>();
			  //ArrayList<EducatoreShift> leaveShiftArrayList = new ArrayList<EducatoreShift>();
		  try {
			  if(mCursor.moveToFirst()) {
			    do {
			    	EducatoreShift obj = new EducatoreShift();
					obj.leave_id = mCursor.getString(mCursor.getColumnIndex("leave_id"));
					obj.center_id = mCursor.getInt(mCursor.getColumnIndex("center_id"));
					//obj.educator_name = mCursor.getString(mCursor.getColumnIndex("educator_name"));
					obj.leave_type = mCursor.getString(mCursor.getColumnIndex("leave_type"));
					obj.leave_start_date = mCursor.getString(mCursor.getColumnIndex("leave_from_date"));
					obj.setLeaveString(obj.leave_start_date);
					obj.leave_end_date = mCursor.getString(mCursor.getColumnIndex("leave_to_date"));
					obj.leave_discription = "Description: "+mCursor.getString(mCursor.getColumnIndex("leave_discription"));
					obj.leave_reason = getLeaveReasonType(mCursor.getString(mCursor.getColumnIndex("leave_reason")));
					//obj.short_form = mCursor.getString(mCursor.getColumnIndex("short_form"));
					obj.create_date = mCursor.getString(mCursor.getColumnIndex("create_date"));
					obj.update_date = mCursor.getString(mCursor.getColumnIndex("update_date"));
					obj.date_Diffrence = getDateDiffrence(
							mCursor.getString(mCursor.getColumnIndex("leave_from_date")),
							mCursor.getString(mCursor.getColumnIndex("leave_to_date")));
					allShiftList.add(obj);
					//leaveShiftArrayList.add(obj);
					//allShiftList.add(addData(mCursor));
					//leaveShiftArrayList.add(addData(mCursor));
					
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

	private String getLeaveReasonType(String leaveReasonId) {
		
		String strQuery = "SELECT type FROM leave_type WHERE leave_type_id = '"+leaveReasonId+"'";
		Cursor mCursor = mDBHelper.getExucuteQurey(strQuery);
		String type = "";
		if(mCursor.getCount()>0) {
			type = mCursor.getString(mCursor.getColumnIndex("type"));
		}
		mCursor.close();
		return type;
	}

	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		LogConfig.logd("Educator leave responce =",""+ responce);
		if(responce.length()>0)
		  callBackShift.requestSetEducatorLeave(allShiftList);
		else callBackShift.requestSetEducatorLeave(null);
	}
	
//	private EducatoreShift addData(Cursor mCursor) {
//		int j=0;
//		if(mCursor.moveToFirst()) {
//			EducatoreShift obj = null;
//		    do {
//		    	obj = new EducatoreShift();
//				obj.leave_id = mCursor.getString(mCursor.getColumnIndex("leave_id"));
//				obj.center_id = mCursor.getInt(mCursor.getColumnIndex("center_id"));
//				//obj.educator_name = mCursor.getString(mCursor.getColumnIndex("educator_name"));
//				obj.leave_type = mCursor.getString(mCursor.getColumnIndex("leave_type"));
//				obj.leave_start_date = mCursor.getString(mCursor.getColumnIndex("leave_from_date"));
//				obj.leave_start_date = setIncreaseDate(obj.leave_start_date, j+1);
//				obj.leave_end_date = mCursor.getString(mCursor.getColumnIndex("leave_to_date"));
//				obj.leave_discription = mCursor.getString(mCursor.getColumnIndex("leave_discription"));
//				obj.leave_reason = mCursor.getString(mCursor.getColumnIndex("leave_reason"));
//				//obj.short_form = mCursor.getString(mCursor.getColumnIndex("short_form"));
//				obj.create_date = mCursor.getString(mCursor.getColumnIndex("create_date"));
//				obj.update_date = mCursor.getString(mCursor.getColumnIndex("update_date"));
//				obj.date_Diffrence = getDateDiffrence(mCursor.getString(mCursor.getColumnIndex("leave_from_date")),
//						mCursor.getString(mCursor.getColumnIndex("leave_to_date")));
//		   } while(mCursor.moveToNext());
//		    // mCursor.close();
//		     return obj;
//		   }
//		return null;
//	}
	
//	private String setIncreaseDate(String date,int increment){
//
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//		Date result = null;
//		try {
//			result = df.parse(date);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(result);
//			cal.add(Calendar.DATE, increment);
//			result = cal.getTime();
//			Log.d("date", result.toString());
//		} catch (ParseException e) {
//			//e.printStackTrace();
//		}
//	
//		return (df.format(result));
//	}

   private int getDateDiffrence(String start_date, String end_date) {
		
		SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
		Date dateStart =null,dateEnd=null;
		try {  
		     dateStart = format.parse(start_date);  
		     dateEnd = format.parse(end_date);  
		} catch (ParseException e) {  
		   // e.printStackTrace();  
		}
		
        int diffInDays = (int) ((dateEnd.getTime() - dateStart.getTime()) / (1000 * 60 * 60 * 24));
        LogConfig.logd("diffInDays =",""+diffInDays);
        return diffInDays;
	}

}
