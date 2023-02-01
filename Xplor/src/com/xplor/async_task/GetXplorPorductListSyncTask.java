package com.xplor.async_task;

import java.util.ArrayList;

import org.json.JSONArray;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.xplor.adaptor.XporProductAdaptor;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.parsing.ChildDataParsing;

public class GetXplorPorductListSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private ServiceHandler service = null;
	private Activity mActivity = null;
	private ListView listView = null;
	private Dialog dialog = null;
	
	public GetXplorPorductListSyncTask(Activity activity) {
		this.mActivity = activity;
		displayChildPopup(mActivity);
		 // create object web-service handler class.
		  service = new ServiceHandler(mActivity);
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
	    
	    String strResponce = service.makeServiceCall(WebUrls.GET_XPOR_PRODUCT_URL, Common.POST);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("MyXplor product responce =",""+strResponce);
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
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   
			   // check status true or false
			   Common.arrXporProductList = new ArrayList<ChildDataParsing>();
			   Common.arrXporProductList.clear();
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				  JSONArray jArray = jObjectResult.getJSONArray("store_product");
				  ChildDataParsing child_data = null;
				  for(int i=0;i<jArray.length();i++) {
					  JSONObject json_data = jArray.getJSONObject(i);
					  child_data = new ChildDataParsing();
					  child_data.setSTR_CHILD_ID(json_data.getString("id"));
					  child_data.setSTR_CHILD_NAME(json_data.getString("name"));
					  child_data.setSTR_CHILD_IMAGE(json_data.getString("pimage"));
					  Common.arrXporProductList.add(child_data);
				  }
				  if(listView != null && dialog != null)
				     listView.setAdapter(new XporProductAdaptor(mActivity,dialog));
			   }
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void displayChildPopup(Context mContext) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.smily_popup);
			dialog.setCancelable(false);
			
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL; 
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		    dialog.getWindow().setAttributes(wmlp);
			
			ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.Smile_Close_Btn);
			listView = (ListView) dialog.findViewById(R.id.Smile_Popup_Listview);

			if (!dialog.isShowing())
				dialog.show();
			
			btnClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});
		}
	}
}
