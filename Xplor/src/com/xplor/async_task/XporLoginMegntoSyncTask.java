package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.ServiceHandler;

public class XporLoginMegntoSyncTask extends AsyncTask<String, Integer, String> {
	
	private Activity mActivity = null;
	private ServiceHandler service;
	
	public XporLoginMegntoSyncTask(Activity activity) {
		
		this.mActivity = activity;
		this.service = new ServiceHandler(mActivity);
	}
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", Common.EMAIL_ID));
		params.add(new BasicNameValuePair("password", Common.PASSWORD));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		String strResponce = service.makeServiceCall(WebUrls.XPLOR_MEGNTO_LOGIN_URL,Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		LogConfig.logd("Xplor megnto login strResponce =",""+strResponce);
	}
 }
