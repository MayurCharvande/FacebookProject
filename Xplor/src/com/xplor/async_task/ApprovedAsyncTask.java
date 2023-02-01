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
import com.xplor.helper.Adapter;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.ApprovedStatusCallBack;

public class ApprovedAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private ApprovedStatusCallBack mApprovedStatusCallBack = null;
	private ProgressDialog _ProgressDialog = null;

	public ApprovedAsyncTask(Activity activity) {
		this.mActivity = activity;
		// create object web-service handler class.
		this.service = new ServiceHandler(mActivity);
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

	public void setApprovedStatusCallBack(ApprovedStatusCallBack approvedStatusCallBack) {
		this.mApprovedStatusCallBack = approvedStatusCallBack;
	}

	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("feed_id", Common.FEED_ID));
		params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.APPROVE_BADGE_URL, Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Approved responce =",""+strResponce);
		
		if(strResponce.equals("ConnectTimeoutException")) {
			Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
			return;
	    } 
		parseResponce(strResponce);
	}

	private void parseResponce(String responce) {
        // Approved server response
		boolean success = false;
		String strMessage = "";
		try {
			 JSONObject jObj = new JSONObject(responce);
			 strMessage = jObj.getString("message");
				if (strMessage.equals("success")) {
					isApprovedData();
					success = true;
				} else {
					success = false;
				}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		mApprovedStatusCallBack.requestApprovedStatus(true,success, strMessage);
	}
	
	private void isApprovedData() {
	
		// Feed approved to call method local managed data
		Adapter mDBHelper = new Adapter(mActivity);
		mDBHelper.createDatabase();
		mDBHelper.open();
		String insertApproved = "INSERT INTO child_badges (id, feed_id, badge_id, challenge_id, child_id, " +
				"educator_id, created_at, updated_at, status) VALUES ('"+Common.generateMobileKey()+"', " +
				"'"+Common.FEED_ID+"', '"+Common.BADGE_ID+"', '"+Common.CHALLENGE_ID+"'," +
				" '"+Common.CHILD_POST_ID+"', '"+Common.USER_ID+"', '"+Common.getCurrentDateTime()+"'," +
				" '"+Common.getCurrentDateTime()+"', '1')";
		mDBHelper.getExucuteQurey(insertApproved);
		mDBHelper.close();
	}
	
}

