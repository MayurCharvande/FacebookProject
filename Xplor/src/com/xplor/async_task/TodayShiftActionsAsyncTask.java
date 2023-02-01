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
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.TodayShiftActionCallBack;

public class TodayShiftActionsAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;
	private ProgressDialog _ProgressDialog = null;
	private TodayShiftActionCallBack mTodayShiftActionCallBack = null;
	String strYesNo = "";
	
	public TodayShiftActionsAsyncTask(Activity activity,String type) {
		this.mActivity = activity;
		this.strYesNo = type;
		// create object web-service handler class.
		service = new ServiceHandler(mActivity);
	}
	
	// set call back interface class of responce parsing
	public void setCallBack(TodayShiftActionCallBack callBack) {
		mTodayShiftActionCallBack = callBack;
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
		params.add(new BasicNameValuePair("shift_id", param[1]));
		params.add(new BasicNameValuePair("timezone", TimeZone.getDefault().getID()));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		if(strYesNo.equals("Yes")) {
		   params.add(new BasicNameValuePair("continue","1"));
		   params.add(new BasicNameValuePair("donotcheck","1"));
		}
		String strResponce = service.makeServiceCall(param[0],Common.POST, params);

		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("TodayShift actions strResponce =",""+strResponce);
		mTodayShiftActionCallBack.requestTodayShiftActionResponce(strResponce);
	}

}
