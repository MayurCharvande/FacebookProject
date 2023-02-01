package com.xplor.async_task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;

@SuppressLint("SimpleDateFormat") 
public class ServerTimeSyncTask extends AsyncTask<String, Integer, String> {
	
	Activity mActivity = null;
	ServiceHandler service = null;
	SharedPreferences sPrefs = null;
	Context mContext = null;
	
	public ServerTimeSyncTask(Context context) {
		 this.mContext = context;
		  sPrefs = context.getSharedPreferences(context.getResources().getString(R.string.app_name), 0);
		  service = new ServiceHandler(mActivity);
	}
	
	@Override
	protected String doInBackground(String... param) {
		
		String strResponce = service.makeServiceCall(WebUrls.SERVER_TIME_URL, Common.POST);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		    LogConfig.logd("Server time strResponce =",""+strResponce);
			
			try {
				// Get value jsonObject 
				JSONObject jsonObject = new JSONObject(strResponce);
			   // check status true or false
			   Common.DEVICE_TIME = diffrenceTime(jsonObject.getString("server_time"));	
			   SharedPreferences.Editor editor = sPrefs.edit();
			   editor.putLong("DEVICE_TIME",diffrenceTime(jsonObject.getString("server_time")));
			   editor.commit();
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
	}
	
	public long diffrenceTime(String strTime) {
		
		SimpleDateFormat dateCurrent = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date currentDate = new Date();
        TimeZone tzCurr = TimeZone.getDefault();
        dateCurrent.setTimeZone(tzCurr);
        Date parsedDateStart = null;
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dateFormatStart.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			parsedDateStart = dateFormatStart.parse(strTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    TimeZone tzStart = TimeZone.getDefault();
	    dateFormatStart.setTimeZone(tzStart);
	   
		return currentDate.getTime() - parsedDateStart.getTime();
	}
}
