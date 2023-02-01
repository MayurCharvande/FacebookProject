package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.os.AsyncTask;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.LikeStatusCallBack;

public class GetChilCheckinoutListSyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service;
	private LikeStatusCallBack mLikeStatusCallBack =null;
	
	public GetChilCheckinoutListSyncTask(Activity activity) {
		this.mActivity = activity;
		this.service = new ServiceHandler(mActivity);
	}
	
	public void setCallBack(LikeStatusCallBack likeStatusCallBack) {
		this.mLikeStatusCallBack = likeStatusCallBack;
	}
		
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("center_id", Common.CENTER_ID));
		params.add(new BasicNameValuePair("room_id", Common.ROOM_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.URL_CHILD_CHECKIN_OUT_LIST, Common.GET, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		 LogConfig.logd("Rooms child List responce =",""+strResponce);
		 mLikeStatusCallBack.requestLikeStatusSuccess(true, strResponce);
	
	}
	
 }
