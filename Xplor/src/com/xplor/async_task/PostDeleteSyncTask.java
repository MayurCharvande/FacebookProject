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
import com.xplor.interfaces.CallBackPostDelete;

public class PostDeleteSyncTask extends AsyncTask<String, Integer, String> {
	
	private CallBackPostDelete mCallBackPostDelete = null;
	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ServiceHandler service = null;
	
	public PostDeleteSyncTask(Activity activity) {
		  this.mActivity = activity;
		  Common.HOME_PAGING = 1;
		 // create object web-service handler class.
		  this.service = new ServiceHandler(mActivity);
	}
	
	public void setCallBack(CallBackPostDelete callBackPostDelete) {
		this.mCallBackPostDelete = callBackPostDelete;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
		_ProgressDialog.setCancelable(false);
		_ProgressDialog.setContentView(R.layout.loading_view);
		WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
		wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;;
		_ProgressDialog.getWindow().setAttributes(wmlp);
	}
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("feed_id", Common.FEED_ID));
		params.add(new BasicNameValuePair("child_id", Common.CHILD_POST_ID));
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.DELETE_POST_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("News feed delete strResponce =",""+strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				return;
		    } 
			try {
				// Get value jsonObject 
				JSONObject jsonObject = new JSONObject(strResponce);
			   // get login response result
				JSONObject jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				  mCallBackPostDelete.requestCallBackPostDelete("Success");
			   }
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
	
}
