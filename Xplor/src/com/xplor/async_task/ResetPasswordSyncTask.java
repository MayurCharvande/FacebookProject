package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;

public class ResetPasswordSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog;
	Activity mActivity = null;
	ServiceHandler service = null;
	public ResetPasswordSyncTask(Activity activity) {
		  this.mActivity = activity;
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
		params.add(new BasicNameValuePair("email", Common.EMAIL_ID));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		
		String strResponce = service.makeServiceCall(WebUrls.RESET_PASSWORD_URL, Common.POST, params);
		LogConfig.logd("Reset password strResponce =",""+strResponce);
		return strResponce;
	} 
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				return;
		    } 
			JSONObject jsonObject = null,jObjectResult = null;
			//{"result":{"status":"success","child_count":"0"}}
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				  DisplayAlertSingle(mActivity,"Message", jObjectResult.getString("message"));   
			   } else {
				  Common.displayAlertSingle(mActivity,"Message", jObjectResult.getString("message"));
			   }
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Show alert data submit successfully
	public void DisplayAlertSingle(Context mContext,String strTitle, String strSMS) {

			if (!Common.isDisplayMessage_Called) {
				Common.isDisplayMessage_Called = true;
				final Dialog dialog = new Dialog(mContext,android.R.style.Theme_Panel);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.alert_popup_single);
				dialog.setCancelable(false);
				WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
				wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
				wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
				wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
				dialog.getWindow().setAttributes(wmlp);
				
				TextView txtTitle = (TextView) dialog.findViewById(R.id.Popup_Title_Txt);
				txtTitle.setText(strTitle);

				TextView listView = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
				listView.setText(strSMS);
				if (!dialog.isShowing())
					dialog.show();

				Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
				btnOk.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Common.isDisplayMessage_Called = false;
						dialog.dismiss();
						mActivity.finish();
						mActivity.overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
					}
				});
			}
		}
}
