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
import com.xplor.interfaces.LeaderBoardCallBack;

public class LeaderBoardListServerSyncTask extends AsyncTask<String, Integer, String> {
	
	private ServiceHandler service = null;
	private Activity mActivity = null;
	private LeaderBoardCallBack mLeaderBoardCallBack=null;

	public LeaderBoardListServerSyncTask(Activity activity) {
		 this.mActivity = activity;
		 // create object web-service handler class.
		 this.service = new ServiceHandler(mActivity);
	}
	
	public void setmLeaderBoardCallBack(LeaderBoardCallBack leaderBoardCallBack) {
		this.mLeaderBoardCallBack = leaderBoardCallBack;
	}
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("logged_user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.LEADER_BOARD_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		LogConfig.logd("leaderBord responce =",""+strResponce);
		
		if(strResponce != null && strResponce.length() > 0) {
			
			if(strResponce.equals("ConnectTimeoutException")) {
				//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
			} 
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   
			   jObjectResult = jsonObject.getJSONObject("result");
			   
			   List<ParentVariable> _listArrayFollow = new ArrayList<ParentVariable>();
			   
			   if (jObjectResult.getString("status").equalsIgnoreCase("success")) {
				   
				   JSONArray jArrayPost= jObjectResult.getJSONArray("post_list");
				   int j=1;
				   for(int i=0;i<jArrayPost.length();i++) {
					   ParentVariable mChallengesBadgesParentVariable= new ParentVariable();
					   JSONObject json_data = jArrayPost.getJSONObject(i);
						  
						  mChallengesBadgesParentVariable.setId(json_data.getString("id"));
						  mChallengesBadgesParentVariable.setImage(json_data.getString("image"));
						  mChallengesBadgesParentVariable.setName(json_data.getString("name"));
						  mChallengesBadgesParentVariable.setFollow_status(json_data.getString("follow_status"));
						  mChallengesBadgesParentVariable.setBadges_Count(json_data.getString("total_badges_count"));
						  mChallengesBadgesParentVariable.setList_Number(""+(j++));
						  
						  _listArrayFollow.add(mChallengesBadgesParentVariable);
					  }
				   mLeaderBoardCallBack.requestUpdateLeaderBoard(_listArrayFollow);
			 }
			   
			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}
}
