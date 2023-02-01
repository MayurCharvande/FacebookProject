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
import com.xplor.interfaces.FavoriteStatusCallBack;

public class FavoriteAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private FavoriteStatusCallBack mFavoriteStatusCallBack = null;
	private ServiceHandler service = null;
	private ProgressDialog _ProgressDialog = null;

	public FavoriteAsyncTask(Activity activity) {
		this.mActivity = activity;
		this.service = new ServiceHandler(mActivity);
	}

	public void setFavoriteStatusCallBack(FavoriteStatusCallBack favoriteStatusCallBack) {
		mFavoriteStatusCallBack = favoriteStatusCallBack;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
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
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("feed_id", Common.FEED_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.ADD_FAVORITE_URL, Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Add favorite responce =",""+responce);
		boolean success = false;
		String message ="";
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
		mFavoriteStatusCallBack.requestFavoriteStatus(true,success, message);
	}

}

