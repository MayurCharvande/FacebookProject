package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.xplor.beacon.EstimoteManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.LoginScreenActivity;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;

public class LogoutServiceSyncTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog _ProgressDialog = null;
	private SharedPreferences sPrefs = null;
	private Activity mActivity = null;
	private ServiceHandler service;
	
	public LogoutServiceSyncTask(Activity activity) {
		
		this.mActivity = activity;
		sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
		service = new ServiceHandler(mActivity);
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
		params.add(new BasicNameValuePair("device_token", Common.DEVICE_TOKEN));
		params.add(new BasicNameValuePair("device_type", Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.LOGOUT_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		if(_ProgressDialog != null)
		   _ProgressDialog.dismiss();
		LogConfig.logd("Logout responce =",""+strResponce);
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
			   // get logout response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status success
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				   EstimoteManager.stop(Common.regionList);
				   SharedPreferences.Editor editor = sPrefs.edit();
				   editor.putString("LOGIN_EMAIL","");
				   editor.putString("PASSWORD","");
				   editor.putBoolean("VIEW_ONLY",false);
				   editor.putString("USER_TYPE","");
				   editor.putString("LOGIN_USER","");
				   editor.commit();
				    Intent intent = new Intent(mActivity,LoginScreenActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mActivity.startActivity(intent); 
					mActivity.finish();
					mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
				}
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
 }
