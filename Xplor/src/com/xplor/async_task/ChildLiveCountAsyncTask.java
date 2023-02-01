package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.RoomChildCountCallBack;

public class ChildLiveCountAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;	
	private RoomChildCountCallBack mRoomChildCountCallBack = null;

	public ChildLiveCountAsyncTask(Activity activity, Boolean isLoading) {
		this.mActivity = activity;
		// create object web-service handler class.
		this.service = new ServiceHandler(mActivity);
	}
	
	public void setCheckStatusCallBack(RoomChildCountCallBack callBack) {
		this.mRoomChildCountCallBack = callBack;
	}

	@Override
	protected String doInBackground(String... param) {
      
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("educator_id", Common.USER_ID));
		params.add(new BasicNameValuePair("timezone", TimeZone.getDefault().getID()));
		params.add(new BasicNameValuePair("center_id",Common.CENTER_ID));
		
		String strResponce = service.makeServiceCall(WebUrls.CHILD_LIVE_COUNT_URL, Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		LogConfig.logd("Child live count responce =",""+ responce);
		mRoomChildCountCallBack.roomChildCountCallBack(responce);
	}
}

