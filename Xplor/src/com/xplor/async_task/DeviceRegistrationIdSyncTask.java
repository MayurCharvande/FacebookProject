package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.ServiceHandler;

public class DeviceRegistrationIdSyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service;
	
	public DeviceRegistrationIdSyncTask(Activity activity) {
		
		this.mActivity = activity;
		this.service = new ServiceHandler(mActivity);
	}
	
	@Override
	protected String doInBackground(String... param) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("longitude", ""+Common.LONGITUTE));
		params.add(new BasicNameValuePair("latitude", ""+Common.LATITUTE));
		params.add(new BasicNameValuePair("device_name", Common.deviceName));
		params.add(new BasicNameValuePair("device_platform",Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("device_model", Common.deviceModal));
		params.add(new BasicNameValuePair("device_version",Common.getAndroidVersion()));
		
		String strResponce = service.makeServiceCall(WebUrls.DEVICE_REGISTRATION_URL, Common.GET, params);
		
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		LogConfig.logd("device registrtion responce =",""+strResponce);
		try { 
			   // Get value jsonObject 
			   JSONObject jsonObject = new JSONObject(strResponce);
			   // check status true or false
			   Common.PERSIST_REGISTER_ID = Integer.parseInt(jsonObject.getString("device_registration_id"));
			   Common.generateMobileKey();
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
