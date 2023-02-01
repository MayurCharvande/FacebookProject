package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.xplor.Model.ParentVariable;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.ParentCallBack;

public class ParentListServerSyncTask extends AsyncTask<String, Integer, String> {
	
	private ServiceHandler service = null;
	private Activity mActivity = null;
	private ParentCallBack mParentCallBack=null;
	
	public void setmParentCallBack(ParentCallBack parentCallBack) {
		this.mParentCallBack = parentCallBack;
	}
	
	public ParentListServerSyncTask(Activity activity) {
		 this.mActivity = activity;
		 // create object web-service handler class.
		  service = new ServiceHandler(mActivity);
	}
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.GET_CHALLBADGE_PARENT_URL, Common.POST, params);
		LogConfig.logd("Parent list strResponce =",""+strResponce);
		
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		//_ProgressDialog.dismiss();
		
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				///Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
		    } 
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   jObjectResult = jsonObject.getJSONObject("result");
			   if(Common.arrLeaderBoardParentList == null)
			      Common.arrLeaderBoardParentList = new ArrayList<ParentVariable>();
			      Common.arrLeaderBoardParentList.clear();
			      
			   if (jObjectResult.getString("status").equalsIgnoreCase("success")) {
				   
				   JSONArray jArrayPost= jObjectResult.getJSONArray("post_list");
				   for(int i=0;i<jArrayPost.length();i++) {
					     ParentVariable mParentVariable= new ParentVariable();  
					     JSONObject json_data = jArrayPost.getJSONObject(i);
					     mParentVariable.setId(json_data.getString("id"));
					     mParentVariable.setImage(json_data.getString("image"));
					     mParentVariable.setName(json_data.getString("name"));
					     mParentVariable.setFollow_status(json_data.getString("follow_status"));
					     Common.arrLeaderBoardParentList.add(mParentVariable);
				   }
				   mParentCallBack.requestUpdateParentCallBack();
			 }
			   
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
}
