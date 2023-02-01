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
import com.xplor.interfaces.LikeStatusCallBack;

public class LikeCountSyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private LikeStatusCallBack mLikeStatusCallBack = null;
	private ProgressDialog _ProgressDialog = null;

	public LikeCountSyncTask(Activity activity) {
		this.mActivity = activity;
		// create object web-service handler class.
		service = new ServiceHandler(mActivity);
	}
	
	public void setLikeCallBack(LikeStatusCallBack likeStatusCallBack) {
		mLikeStatusCallBack = likeStatusCallBack;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_ProgressDialog = ProgressDialog.show(mActivity, "", "", true);
		_ProgressDialog.setCancelable(false);
		_ProgressDialog.setContentView(R.layout.loading_view);
		WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		_ProgressDialog.getWindow().setAttributes(wmlp);
	}

	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("feed_id", Common.FEED_ID));
		params.add(new BasicNameValuePair("id", Common.USER_ID));
		params.add(new BasicNameValuePair("like", "" + Common.MSG_LIKE));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.LIKE_URL, Common.POST,params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("New feeds like strResponce =",""+strResponce);
		if (strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
		    } 
			JSONObject jsonObject = null, jObjectResult = null;
			try {
				// Get value jsonObject
				jsonObject = new JSONObject(strResponce);
				// get login response result
				jObjectResult = jsonObject.getJSONObject("result");
				// check status true or false
				String strStatus = jObjectResult.getString("status");
				if (strStatus.equals("success")) {
					mLikeStatusCallBack.requestLikeStatusSuccess(true, jObjectResult.getString("message"));
				}
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
}