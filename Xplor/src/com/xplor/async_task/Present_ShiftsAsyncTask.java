package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.ServiceHandler;

public class Present_ShiftsAsyncTask extends AsyncTask<String, Integer, String> {

	private Context mContext = null;
	private ServiceHandler service = null;
	
	public Present_ShiftsAsyncTask(Context context) {
		this.mContext = context;
		// create object web-service handler class.
		this.service = new ServiceHandler(mContext);
	}

	@Override
	protected String doInBackground(String... param) {
        //Parameters :  educator_id, @"present", @"type" , timezone
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("educator_id", Common.USER_ID));
		params.add(new BasicNameValuePair("type", "present"));
		params.add(new BasicNameValuePair("device_type", Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("timezone", TimeZone.getDefault().getID()));
		
		String strResponce = service.makeServiceCall(WebUrls.EDUCATOR_PRESENT_SHIFT,Common.POST, params);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		LogConfig.logd("Present shift strResponce =",""+ strResponce);
		if(strResponce != null && strResponce.length() > 0) {
			
			if(strResponce.equals("ConnectTimeoutException")) {
				return;
		    } 
			JSONObject jsonObject = null,jObjectResult = null;
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				   JSONArray jArray = jObjectResult.getJSONArray("educator_shift");
				   Common.SHIFT_STARTED = false; 
				   for(int i=0;i<jArray.length();i++) {
					   JSONObject json_data = jArray.getJSONObject(i);
					   if(json_data.getString("shift_status").equals("Started")) {
						   Common.SHIFT_STARTED = true;
						   break;
					   } 
				   }
				   
				}

			} catch (JSONException e) {
				//e.printStackTrace();
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
	}

}
