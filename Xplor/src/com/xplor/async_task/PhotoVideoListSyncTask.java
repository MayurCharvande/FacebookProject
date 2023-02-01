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
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.ReceiverCallBack;

public class PhotoVideoListSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog;
	ServiceHandler service = null;
	Activity mActivity = null;
	String strType = "";
	ReceiverCallBack mContext;
	int mPaging = 0;
	
	public PhotoVideoListSyncTask(ReceiverCallBack context, Activity activity,String type,int paging) {
		this.mActivity = activity;
		this.strType = type;
		this.mContext = context;
		this.mPaging = paging;
		 // create object web-service handler class.
		  service = new ServiceHandler(mActivity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_ProgressDialog =  ProgressDialog.show(mActivity, "", "",true);
		_ProgressDialog.setCancelable(false);
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
		params.add(new BasicNameValuePair("center_id", Common.CENTER_ID));
		params.add(new BasicNameValuePair("room_id", Common.ROOM_ID));
		params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
		params.add(new BasicNameValuePair("page", ""+mPaging));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = "";	
	    if(strType.equals("photo")) {
			strResponce = service.makeServiceCall(WebUrls.GET_PHOTO_URL, Common.POST, params);
		} else {
			strResponce = service.makeServiceCall(WebUrls.GET_VIDEO_URL, Common.POST, params);
		}
	    LogConfig.logd("photovideo strResponce =",""+strResponce);
		
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		if(strResponce.equals("ConnectTimeoutException")) {
			Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
			return;
	    } 
		((ReceiverCallBack)mContext).requestFinish(strResponce);
	}
}
