package com.xplor.async_task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.dev.LoginScreenActivity;
import com.xplor.dev.R;
import com.xplor.helper.Adapter;
import com.xplor.helper.ServiceHandler;

public class SubLearningOutSyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private SharedPreferences sPrefs = null;
	
	public SubLearningOutSyncTask(Activity activity) {
	    this.mActivity = activity;
		 // create object web-service handler class.
		  service = new ServiceHandler(mActivity);
	}
	
	@Override
	protected String doInBackground(String... param) {

		String strResponce = service.makeServiceCall(WebUrls.SUB_LEARNING_OUTCOME_URL, Common.POST);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		//LogConfig.logd("SubLearningOutCome strResponce =",""+strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			
			if(strResponce.equals("ConnectTimeoutException")) {
				Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				return;
		    } 
			JSONObject jsonObject = null;
			Common.arrChild_Invite = null;
			
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			    Adapter mDbHelper = new Adapter(mActivity);
				mDbHelper.createDatabase();
				mDbHelper.open();
				mDbHelper.deleteLearningOutcome();
				mDbHelper.deleteSubLearningOutcome();
				JSONArray jArrayCat = jsonObject.getJSONArray("categories"); //
			   for(int i=0;i<jArrayCat.length();i++) {
				  JSONObject json_data = jArrayCat.getJSONObject(i);
				  ContentValues cv = new ContentValues();
				  cv.put("catId",json_data.getString("cat_id"));
				  cv.put("learningOutcomesCat", json_data.getString("cat_name").replace("\n", "").replace("\r", ""));
				  mDbHelper.saveLearningOutcome(cv);			
			     }
			   JSONArray jArraySubCat = jsonObject.getJSONArray("sub_categories");
			        for(int i=0;i<jArraySubCat.length();i++) {
					  JSONObject json_data = jArraySubCat.getJSONObject(i);
					  ContentValues cv = new ContentValues();
					  cv.put("catId",json_data.getString("cat_id"));
					  cv.put("subCatId",json_data.getString("sub_cat_id"));
					  cv.put("learningOutcomesSubCat",json_data.getString("sub_cat_name").replace("\n", " ").replace("\r", " "));
					  mDbHelper.saveSubLearningOutcome(cv);			
				     }
				    mDbHelper.close();	
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
			 mLoginServiceSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
		   }
	}
}
