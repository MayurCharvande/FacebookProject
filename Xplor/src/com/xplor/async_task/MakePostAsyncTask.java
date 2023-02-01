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
import com.xplor.interfaces.CallBackMakePost;

public class MakePostAsyncTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity = null;
	private ServiceHandler service = null;	
	private String strType = "";
	private String center_id, child_id, room_id, logged_user_id, 
	        sender_type,standMess_Type,custom_msg, center_name;
	private CallBackMakePost mCallBackMakePost = null;
	
	public MakePostAsyncTask(Activity activity,String type) {
		this.mActivity = activity;
		this.strType = type;
		// create object web-service handler class.
		this.service = new ServiceHandler(mActivity);
	}

	public void setData(String centerId, String childId,  String roomId, String loggedUserId, String senderType,String standMessType, String customMsg, String centerName) {
		this.center_id = centerId;
		this.child_id = childId;
		this.logged_user_id = loggedUserId;
		this.sender_type = senderType;
		this.standMess_Type = standMessType;
		this.custom_msg = customMsg;
		this.center_name = centerName;
		this.room_id = roomId;
	}
	
	public void setMakePostCallBack(CallBackMakePost callBackMakePost) {
		this.mCallBackMakePost = callBackMakePost;
	}	
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("center_id", center_id));
		params.add(new BasicNameValuePair("child_id", child_id));
		params.add(new BasicNameValuePair("room_id", room_id));		
		params.add(new BasicNameValuePair("logged_user_id", logged_user_id));
		params.add(new BasicNameValuePair("sender_type", sender_type));
		params.add(new BasicNameValuePair("standard_msg_type", standMess_Type));
		params.add(new BasicNameValuePair("custom_msg", custom_msg+" "+center_name));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.MAKE_POST_URL, Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		LogConfig.logd("Make post responce =",""+ strResponce);
		if(strResponce != null) {
			mCallBackMakePost.requestMakePost(strResponce, strType);
		}
	}
}
