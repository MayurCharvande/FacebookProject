package com.xplor.async_task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.CallBackNewsFeedPreviousDate;

@SuppressLint("SimpleDateFormat") 
public class GetNewsFeedPreviousDate extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ServiceHandler service = null;
	private CallBackNewsFeedPreviousDate mCallBackNewsFeedPreviousDate = null;
	private String strFeedId = "";
	
	public GetNewsFeedPreviousDate(Activity activity,String feedId) {
		 this.mActivity = activity;
		 this.strFeedId = feedId;
		 this.service = new ServiceHandler(mActivity);
	}
	
	public void setCallBack(CallBackNewsFeedPreviousDate callBackNewsFeedPreviousDate) {
		this.mCallBackNewsFeedPreviousDate = callBackNewsFeedPreviousDate;
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
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userid", Common.USER_ID));
		params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
		params.add(new BasicNameValuePair("feed_id", strFeedId));
		params.add(new BasicNameValuePair("center_id",Common.CENTER_ID));
		params.add(new BasicNameValuePair("screen_mode",Common.SCREEN_MODE));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.SQL_NEWSFEED_PREVIOUS_DATE, Common.POST,params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		    LogConfig.logd("Newsfeed previousdata responce =",""+strResponce);
		    
		    if(strResponce != null && strResponce.length() > 0) {
		       if(strResponce.equals("ConnectTimeoutException")) {
		    	_ProgressDialog.dismiss();
				Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				return;
			   } else {
		        try {
				  // Get value jsonObject 
				  JSONObject jsonObject = new JSONObject(strResponce);
			       // check status true or false
				   if(Integer.parseInt(jsonObject.getString("status")) == 1) {
			          mCallBackNewsFeedPreviousDate.requestNewsFeedPreviousDate(
			              Integer.parseInt(jsonObject.getString("status")),
			    		  diffrenceTime(jsonObject.getString("date")),jsonObject.getString("id"), _ProgressDialog);
				   } else {
					  _ProgressDialog.dismiss();
					  String message = mActivity.getResources().getString(R.string.str_no_more_post);
					  Common.displayAlertSingle(mActivity, mActivity.getResources().getString(R.string.str_alert), message);
				   }
			      } catch (JSONException e) {
				   _ProgressDialog.dismiss();
			      }
			   }
		  } else _ProgressDialog.dismiss();
	}
	
	public String diffrenceTime(String strTime) {
	
        Date parsedDateStart = null;
        SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dateFormatStart.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			parsedDateStart = dateFormatStart.parse(strTime);
		} catch (ParseException e) {
			//e.printStackTrace();
		}
	    TimeZone tzStart = TimeZone.getDefault();
	    dateFormatStart.setTimeZone(tzStart);
	   
		return dateFormatStart.format(parsedDateStart.getTime());
	}
}
