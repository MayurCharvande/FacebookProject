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
import android.widget.Toast;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;

public class AddMedicalSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ServiceHandler service = null;
	private String strEdit = "";
	public AddMedicalSyncTask(Activity activity,String edit) {
		  this.mActivity = activity;
		  this.strEdit = edit;
		  // create object web-service handler class.
		  this.service = new ServiceHandler(mActivity);
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
		params.add(new BasicNameValuePair("room_id", Common.ROOM_ID));
		params.add(new BasicNameValuePair("center_id", Common.CENTER_ID));
		params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("sender_type", Common.USER_TYPE));
		params.add(new BasicNameValuePair("medical_event", Common.MEDICAL_EVENT));
		params.add(new BasicNameValuePair("event_description", Common.MEDICAL_EVENT_DESC));
		params.add(new BasicNameValuePair("medication", ""+Common.MEDICATION_YES_NO));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("medication_description", Common.MEDIADD_MEDICATION_DESC));
		
		String strResponce = "";
		if(strEdit.equals("Edit")) {
			params.add(new BasicNameValuePair("madication_event_id", Common.MEDICATION_EVENT_ID));
			strResponce = service.makeServiceCall(WebUrls.EDIT_MEDICAL_URL, Common.POST, params);
		} else {
			strResponce = service.makeServiceCall(WebUrls.ADD_MEDICAL_URL, Common.POST, params);
		}
		
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		if(_ProgressDialog != null)
		   _ProgressDialog.dismiss();
		   LogConfig.logd("Medical responce =",""+strResponce);
		   
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
		    } 
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get medical response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				  Common.isDisplayMessage_Called = false; 
				  Common.displayAlertButtonToFinish(mActivity, 
				  mActivity.getResources().getString(R.string.str_Message),
				  jObjectResult.getString("message"), true);
			   } else {
				  Common.displayAlertSingle(mActivity,
				  mActivity.getResources().getString(R.string.str_Message), 
				  jObjectResult.getString("message"));
			   }
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
     }
	
}
