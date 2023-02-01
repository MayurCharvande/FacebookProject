package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.xplor.interfaces.TimerStatusCallBack;

public class TimerStartStopSyncTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog _ProgressDialog;
	private Activity mActivity = null;
	private ServiceHandler service;
	private String strMessage = "";
	private String strAction = "";
	private TimerStatusCallBack mTimerStatusCallBack = null;

	public TimerStartStopSyncTask(Activity activity,String action,String mesage) {
		this.mActivity = activity;
		this.strMessage = mesage;
		this.strAction = action;
		this.service = new ServiceHandler(mActivity);
	}
	
	public void setTitmerStatusCallBack(TimerStatusCallBack timerStatusCallBack) {
		this.mTimerStatusCallBack = timerStatusCallBack;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(_ProgressDialog == null) {
		  _ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
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
		params.add(new BasicNameValuePair("action", strAction));
		params.add(new BasicNameValuePair("action_by_id", Common.USER_ID));
		params.add(new BasicNameValuePair("center_id",Common.CENTER_ID));
		params.add(new BasicNameValuePair("news_feeds_id", Common.FEED_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("message", strMessage));
		
		String strResponce = service.makeServiceCall(WebUrls.TIMER_START_STOP_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		
		LogConfig.logd("Timer start stop strResponce =",""+strResponce);
		boolean success = false;
		String message= "";
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				return;
		    } 
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get logout response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status success
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				   success = true;
				   message = jObjectResult.getString("message");
				  //Success result
			   } else {
				   success = false;
				   message = strMessage;
			   }
			   mTimerStatusCallBack.requestTimerStatus(success, message,_ProgressDialog);
			} catch (JSONException e) {
				_ProgressDialog.dismiss();
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
				_ProgressDialog.dismiss();
			}
		  
      }
		
	}
	
 }
