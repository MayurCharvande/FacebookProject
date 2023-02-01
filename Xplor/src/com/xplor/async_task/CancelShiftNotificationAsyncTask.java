package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.roastring.AvailableShiftScreen;

public class CancelShiftNotificationAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private ProgressDialog _ProgressDialog = null;

	public CancelShiftNotificationAsyncTask(Activity activity) {
		this.mActivity = activity;
		// create object web-service handler class.
		this.service = new ServiceHandler(mActivity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(_ProgressDialog == null) {
		  _ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
		  _ProgressDialog.setCancelable(false);
		  _ProgressDialog.setContentView(R.layout.loading_view);
		  WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
		  wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
		  wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		  wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		  _ProgressDialog.getWindow().setAttributes(wmlp);
	    }
	}

	@Override
	protected String doInBackground(String... param) {
		//time zone
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("shift_id", param[0]));
		params.add(new BasicNameValuePair("device_type", Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("timezone", TimeZone.getDefault().getID()));
		
		String strResponce = service.makeServiceCall(WebUrls.EDUCATOR_SHIFT_CANCEL_VIEW_URL, Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Cancel shift notification strResponce =",""+ strResponce);
		if(!Common._isClassCommentOpen) {
			Intent mIntent = new Intent(mActivity,AvailableShiftScreen.class);
			mIntent.putExtra("CancelResponce", strResponce);
			mActivity.startActivity(mIntent);
			mActivity.finish();
		  } else {
			Intent mIntent = new Intent(mActivity,AvailableShiftScreen.class);
			mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		    mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    mIntent.putExtra("CancelResponce", strResponce);
		    mActivity.startActivity(mIntent);
		    mActivity.finish();
		  }
	}
}
