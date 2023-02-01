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

public class AddInviteSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog;
	Activity mActivity = null;
	ServiceHandler service = null;
	public AddInviteSyncTask(Activity activity) {
		  this.mActivity = activity;
		 // create object web-service handler class.
		  service = new ServiceHandler(mActivity);
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
		params.add(new BasicNameValuePair("invite_by", Common.USER_ID));
		params.add(new BasicNameValuePair("type", "3"));
		params.add(new BasicNameValuePair("first_name", Common.INVITE_FNAME));
		params.add(new BasicNameValuePair("last_name", Common.INVITE_LNAME));
		params.add(new BasicNameValuePair("email", Common.INVITE_EMAIL));
		params.add(new BasicNameValuePair("relation_id", Common.INVITE_RELATION_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));

		String strResponce = service.makeServiceCall(WebUrls.CREATE_INVITE_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Create invite strResponce =",""+strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
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
			   Common.isDisplayMessage_Called = false; 
			   if(status.equals("success")) {
				  Common.displayAlertButtonToFinish(mActivity,"Message", jObjectResult.getString("message"),true);   
			   } else {
				  Common.displayAlertButtonToFinish(mActivity,"Message", "You have already invited this user.",true);
			   }
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
}
