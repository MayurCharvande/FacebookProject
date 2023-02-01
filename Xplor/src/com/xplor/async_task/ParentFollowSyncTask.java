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
import com.xplor.interfaces.ParentFollowCallBack;

public class ParentFollowSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private ServiceHandler service = null;
	private Activity mActivity = null;
	private int position;
	private ParentFollowCallBack mParentFollowCallBack=null;

	public ParentFollowSyncTask(Activity activity,int mposition) {
		 this.mActivity = activity;
		 // create object web-service handler class.
		 this.service = new ServiceHandler(mActivity);
		 this.position=mposition;
	}
	
	public void setParentFollowCallBack(ParentFollowCallBack parentFollowCallBack) {
		this.mParentFollowCallBack= parentFollowCallBack;
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
		params.add(new BasicNameValuePair("followed_parent_id", param[0]));
		params.add(new BasicNameValuePair("follower_parent_id", Common.USER_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.LEADER_BOARD_PARENT_FOLLOW_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Perent follow responce =",""+strResponce);
		
		if(strResponce != null && strResponce.length() > 0) {
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   jObjectResult = jsonObject.getJSONObject("result");
			   
			   if (jObjectResult.getString("status").equalsIgnoreCase("success")) {
				   Common.arrLeaderBoardParentList.get(position).setFollow_status("1");
				   mParentFollowCallBack.requestParentFollowCallBack();
			    }
			   
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
}
