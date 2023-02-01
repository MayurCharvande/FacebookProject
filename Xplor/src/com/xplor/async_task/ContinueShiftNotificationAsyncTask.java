package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class ContinueShiftNotificationAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private ProgressDialog _ProgressDialog =null;
	private String strShiftId = "";

	public ContinueShiftNotificationAsyncTask(Activity activity,String shiftId) {
		this.mActivity = activity;
		this.strShiftId = shiftId;
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
		   wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;;
		   _ProgressDialog.getWindow().setAttributes(wmlp);
		}
	}

	@Override
	protected String doInBackground(String... param) {
		//parameters : educator_id, shift_id
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("shift_id", strShiftId));
		params.add(new BasicNameValuePair("educator_id", Common.USER_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.EDUCATOR_SHIFT_CONTINUE_URL, Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Shift Continue strResponce =","" + strResponce);
		if(strResponce.equals("ConnectTimeoutException")) {
			//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
			Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
			return;
		} 
		mActivity.finish();
	}
}
