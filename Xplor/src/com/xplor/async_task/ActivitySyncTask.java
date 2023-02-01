package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.LearningActvities;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.parsing.ChildDataParsing;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

public class ActivitySyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	ServiceHandler service = null;
	Activity mActivity = null;
	int activityId=0,stMessTypeId =0;
	String stMessage ="",activtyName="";
	
	//WebUrls.Listning_Id,strChildName+" is listening",mChildArray[position]
	public ActivitySyncTask(Activity activity,int stTypeId,String stSMS,int acId,String acName) {
		 this.mActivity = activity;
		 this.stMessTypeId = stTypeId;
		 this.stMessage = stSMS;
		 this.activityId = acId;
		 this.activtyName = acName;
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
		params.add(new BasicNameValuePair("cat_id", ""+activityId));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.LEARNING_ACTIVITY_URL, Common.POST, params);
		
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Activity strResponce =",""+strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
		    } 
			JSONObject jsonObject = null;

			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get login response result check status true or false
			   String status = jsonObject.getString("status");	
			   if(status.equals("1")) {
				  JSONArray jArray = jsonObject.getJSONArray("data");
				  Common.arrActivityData = new ArrayList<ChildDataParsing>();
				  Common.arrActivityData.clear();
				  ChildDataParsing child_data = null;
				  for(int i=0;i<jArray.length();i++) {
					  JSONObject json_data = jArray.getJSONObject(i);
					  child_data = new ChildDataParsing();
					  child_data.setSTR_CHILD_ID(json_data.getString("product_id"));
					  child_data.setSTR_CHILD_NAME(json_data.getString("product_name"));
					  child_data.setSTR_CHILD_FIRST_NAME(json_data.getString("product_desc"));
					  child_data.setSTR_CHILD_IMAGE(json_data.getString("product_image"));
					  child_data.setSTR_CHILD_LAST_NAME(json_data.getString("product_quantity"));
					  child_data.setSTR_CHILD_GENDER(json_data.getString("product_price")); 
					  Common.arrActivityData.add(child_data);
				  }
				     
				    // call class to get response
				    Intent intent = new Intent(mActivity, LearningActvities.class);
				    intent.putExtra("ST_TYPE_Id", stMessTypeId);
				    intent.putExtra("ST_MESSAGE", stMessage);
				    intent.putExtra("ACT_ID", activityId);
				    intent.putExtra("ACT_NAME", activtyName);
				    mActivity.startActivity(intent);
				    mActivity.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
			   }
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
}
