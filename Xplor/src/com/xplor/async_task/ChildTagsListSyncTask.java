package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.ChildRoomTaggingFragment;
import com.xplor.dev.ChildTagsMainActivity;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.TagListCallBack;
import com.xplor.parsing.ChildDataParsing;

public class ChildTagsListSyncTask extends AsyncTask<String, Integer, String> {
	
	private ProgressDialog _ProgressDialog = null;
	private ServiceHandler service = null;
	private Activity mActivity = null;
	private TagListCallBack tagListCallBack = null;
	private ArrayList<ChildDataParsing> arrChildTagsData = null;
	String strType = "";
	
	public ChildTagsListSyncTask(Activity activity,String type) {
		this.mActivity = activity;
		this.strType = type;
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
	
	public void setCallBack(TagListCallBack callback) {
		tagListCallBack = callback;
	}
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
	
		String strResponce = "";
		if(Common.USER_TYPE.equals("2")) {
			params.add(new BasicNameValuePair("parent_id", Common.USER_ID));
			params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
			params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
			strResponce = service.makeServiceCall(WebUrls.CHILD_PERENT_TAGS_URL, Common.POST, params);
		} else {
			if(strType.length() == 0) {
			  params.add(new BasicNameValuePair("center_id", Common.CENTER_ID));
			  params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
			  params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
			  strResponce = service.makeServiceCall(WebUrls.CHILD_EDUCATOR_TAGS_URL, Common.POST, params);
			} else {
			  params.add(new BasicNameValuePair("center_id", Common.CENTER_ID));
			  params.add(new BasicNameValuePair("educator_id", Common.USER_ID));
			  params.add(new BasicNameValuePair("room_id",Common.ROOM_ID));
			  params.add(new BasicNameValuePair("child_id", Common.CHILD_ID));
			  params.add(new BasicNameValuePair("timezone", TimeZone.getDefault().getID()));
			  params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
			  strResponce = service.makeServiceCall(WebUrls.CHILD_CENTER_TAGS_URL, Common.POST, params);
			}
		}
		
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		_ProgressDialog.dismiss();
		LogConfig.logd("Tagging strResponce =",""+strResponce);
		
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				//Common.displayAlertSingle(mActivity, "Message", Common.TimeOut_Message);
				Toast.makeText(mActivity, Common.TimeOut_Message, Toast.LENGTH_LONG).show();
				return;
			} 
			JSONObject jsonObject = null,jObjectResult = null;
			arrChildTagsData = new ArrayList<ChildDataParsing>();
			try {
				// Get value jsonObject 
			   jsonObject = new JSONObject(strResponce);
			   // get login response result
			   jObjectResult = jsonObject.getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				  JSONArray jArray = jObjectResult.getJSONArray("child_list");
				  ChildDataParsing child_data = null;
				  for(int i=0;i<jArray.length();i++) {
					  JSONObject json_data = jArray.getJSONObject(i);
					  child_data = new ChildDataParsing();
					  child_data.setSTR_CHILD_ID(json_data.getString("child_id"));
					  child_data.setSTR_CHILD_NAME(Common.capFirstLetter(json_data.getString("child_name")));
					  child_data.setSTR_CHILD_FIRST_NAME(Common.capFirstLetter(json_data.getString("first_name")));
					  child_data.setSTR_CHILD_LAST_NAME(Common.capFirstLetter(json_data.getString("last_name")));
					  child_data.setSTR_CHILD_IMAGE(json_data.getString("child_image"));
					  child_data.setBOL_CHECKED(false);
					  arrChildTagsData.add(child_data);
				   }
				  tagListCallBack.requestTagListCallBack(arrChildTagsData);
			   } else {
				   Common.isDisplayMessage_Called = false;
				   String strMessage = mActivity.getResources().getString(R.string.no_child_available);
				   if(jObjectResult.has("message"))
					  strMessage = jObjectResult.getString("message");
				   
				   displayAlertSingle(mActivity, mActivity.getResources().
						   getString(R.string.str_Message), strMessage);
			   }
			} catch (JSONException e) {} 
			catch (NullPointerException e) {}
		}
	}
	
	public void displayAlertSingle(Context mContext, String strTitle,String strSMS) {

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

			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					if(strType.length() > 0)
					   callRoomScreen();
				}
			});

			if (!dialog.isShowing())
				dialog.show();
		}
	}
	
    public void callRoomScreen() {
		
		ChildTagsMainActivity.ChildTagTitleTxt.setText("Child by Room");
        Bundle mData = new Bundle();
		Fragment mFragment = new ChildRoomTaggingFragment();
		mData.putString("Title", "Child by Room");
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = mActivity.getFragmentManager();

		if (mActivity.getFragmentManager().getBackStackEntryCount() > 0) {
			Common.clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
		
	}
}
