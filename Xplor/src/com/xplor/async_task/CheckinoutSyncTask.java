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
import com.xplor.interfaces.CheckStatusCallBack;

public class CheckinoutSyncTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ServiceHandler service;
	private CheckStatusCallBack mCheckStatusCallBack = null;
	
	public CheckinoutSyncTask(Activity activity) {
		this.mActivity = activity;
		this.service = new ServiceHandler(mActivity);
	}
	
	public void setCheckinoutCallBack(CheckStatusCallBack checkStatusCallBack) {
		this.mCheckStatusCallBack = checkStatusCallBack;
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
		 params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
		 params.add(new BasicNameValuePair("educator_id", Common.USER_ID));
		 params.add(new BasicNameValuePair("status", ""+Common.CHILD_STATUS));
		 params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		 String strResponce = service.makeServiceCall(WebUrls.CHECKIN_OUT_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Checkinout strResponce =",""+strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get logout response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status success
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				   if(Common.CHILD_STATUS == 1) {
					  mCheckStatusCallBack.requestCheckStatus(true, true, jObjectResult.getString("message"));
					} else {
					  mCheckStatusCallBack.requestCheckStatus(false, true, jObjectResult.getString("message"));
				   }
			   } else {
				   Common.displayAlertSingle(mActivity,"Alert",jObjectResult.getString("message"));
			   }
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}

 }



