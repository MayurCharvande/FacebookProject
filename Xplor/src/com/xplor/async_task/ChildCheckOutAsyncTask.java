package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.CheckStatusCallBack;

public class ChildCheckOutAsyncTask extends AsyncTask<String, Integer, String> {

	Context mContext = null;
	private ServiceHandler service = null;
	private ProgressDialog _ProgressDialog = null;
	private CheckStatusCallBack mCheckStatusCallBack = null;

	public ChildCheckOutAsyncTask(Context conContext) {
		this.mContext = conContext;
		// create object web-service handler class.
		service = new ServiceHandler(mContext);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(_ProgressDialog == null) {
			  _ProgressDialog =  ProgressDialog.show(mContext, "", "",true);
			  _ProgressDialog.setCancelable(false);
			  _ProgressDialog.setContentView(R.layout.loading_view);
			  WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
			  wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
			  wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			  wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;;
			  _ProgressDialog.getWindow().setAttributes(wmlp);
			}
	}

	public void setCheckStatusCallBack(CheckStatusCallBack checkStatusCallBack) {
		mCheckStatusCallBack = checkStatusCallBack;
	}

	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("parent_id", Common.USER_ID));
		params.add(new BasicNameValuePair("child_id", param[0]));
		params.add(new BasicNameValuePair("status", "0"));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		String strResponce = service.makeServiceCall(WebUrls.CHECK_OUT_URL, Common.POST, params);
		
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Child checkout responce =",""+strResponce);
		parseResponce(strResponce);

	}

	private void parseResponce(String responce) {

		boolean success = false;
		String message= "";
		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					success = true;
					message = resultObj.getString("message");
				} else {
					success = false;
					message = resultObj.getString("message");
				}
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		mCheckStatusCallBack.requestCheckStatus(false, success, message);

	}

}