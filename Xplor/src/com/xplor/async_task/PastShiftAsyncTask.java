package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import com.xplor.interfaces.TodayShiftCallBack;

public class PastShiftAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ProgressDialog _ProgressDialog = null;
	private ServiceHandler service = null;
	private TodayShiftCallBack mTodayShiftCallBack = null;

	public PastShiftAsyncTask(Activity activity) {
		this.mActivity = activity;
		// create object web-service handler class.
		service = new ServiceHandler(mActivity);
	}
	
	public void setCallBack(TodayShiftCallBack todayShiftCallBack) {
		this.mTodayShiftCallBack = todayShiftCallBack;
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

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("educator_id", Common.USER_ID));
		params.add(new BasicNameValuePair("type", param[0]));
		params.add(new BasicNameValuePair("page", param[1]));
		params.add(new BasicNameValuePair("timezone", TimeZone.getDefault().getID()));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.SHIFT_LIST_URL,Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Past shift responce =",""+ strResponce);
		if(strResponce.equals("ConnectTimeoutException")) {
			Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
			return;
	    } else 
		   mTodayShiftCallBack.requestTodayShiftCallBackList(strResponce);
	}

}
