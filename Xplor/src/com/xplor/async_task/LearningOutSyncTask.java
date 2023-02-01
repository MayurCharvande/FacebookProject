package com.xplor.async_task;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.dev.LoginScreenActivity;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;

public class LearningOutSyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private SharedPreferences sPrefs = null;
	
	public LearningOutSyncTask(Activity activity) {
	     this.mActivity = activity;
		 // create object web-service handler class.
		 this.service = new ServiceHandler(mActivity);
	}
	
	@Override
	protected String doInBackground(String... param) {

		String strResponce = service.makeServiceCall(WebUrls.LEARNING_OUTCOME_URL, Common.POST);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		//LogConfig.logd("LearningOutCome strResponce =",""+strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			
			if(strResponce.equals("ConnectTimeoutException")) {
				//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
			} 
			JSONObject jsonObject = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get SharedPreferences email and password 
				sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
				int version = sPrefs.getInt("LEARNING_VERSION", 0);
				if(jsonObject.getInt("version") > version) {
					SharedPreferences.Editor editor = sPrefs.edit();
					editor.putInt("LEARNING_VERSION",jsonObject.getInt("version"));
					editor.commit();
					SubLearningOutSyncTask mSubLearningOutSyncTask = new SubLearningOutSyncTask(mActivity);
					mSubLearningOutSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					// get SharedPreferences email and password 
				    sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
				    Common.DEVICE_TOKEN = sPrefs.getString("DEVICE_TOKEN", "");
					Common.CHECK_REMEBER = sPrefs.getBoolean("REMEMBER", false);
					Common.EMAIL_ID = sPrefs.getString("LOGIN_EMAIL", "");
					Common.PASSWORD = sPrefs.getString("PASSWORD", "");
				
					// check condition email and password than call login service
					if(Common.EMAIL_ID.length() > 0 && Common.PASSWORD.length() > 0) {
						Common.CHECK_REMEBER = true;
						btnClick_Login();
					} else {
					  Intent intent = new Intent(mActivity,LoginScreenActivity.class);
					  mActivity.startActivity(intent); 
					  mActivity.finish();
					}
				}
			   // get login response result

			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
	public void btnClick_Login() {
		   //check network validation 
		   Validation  validation = new Validation(mActivity);
		   if (!validation.checkNetworkRechability()) {
			   Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		   } else {
			// execute login service 
			 LoginServiceSyncTask mLoginServiceSyncTask = new LoginServiceSyncTask(mActivity,true); 
		     mLoginServiceSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		   }
	}	

}
