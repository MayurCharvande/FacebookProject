package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.EducatorMainActivity;
import com.xplor.dev.R;
import com.xplor.helper.Adapter;
import com.xplor.helper.ServiceHandler;
import com.xplor.local.syncing.download.SqlQuery;
import com.xplor.local.syncing.download.SyncingUtil;
import com.xplor.parsing.ChildDataParsing;

public class LoginServiceSyncTask extends AsyncTask<String, Integer, String> {

	private ProgressDialog _ProgressDialog=null;
	private Activity mActivity = null;
	private ServiceHandler service = null;
	private SharedPreferences sPrefs = null;
	private Boolean _isFinish = false;
	private String android_id ="";
	
	public LoginServiceSyncTask(Activity activity,Boolean bol) {
	    this.mActivity = activity;
	    this._isFinish = bol;
	    android_id = Secure.getString(mActivity.getContentResolver(),Secure.ANDROID_ID);
	    LogConfig.logd("Android","Android ID :- "+android_id);
		 // create object web-service handler class.
	    this.service = new ServiceHandler(mActivity);
	    this.sPrefs = activity.getSharedPreferences(activity.getResources().getString(R.string.app_name), 0);
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
			 wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			 _ProgressDialog.getWindow().setAttributes(wmlp);
		   }
	}
	
	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_app","main_app"));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("device_token",Common.DEVICE_TOKEN));
		params.add(new BasicNameValuePair("email",Common.EMAIL_ID));
		params.add(new BasicNameValuePair("password",Common.PASSWORD));
		params.add(new BasicNameValuePair("device_type",Common.DEVICE_TYPE));
		params.add(new BasicNameValuePair("device_id",android_id));
		
		String strResponce = service.makeServiceCall(WebUrls.LOGIN_URL, Common.POST, params);
		return strResponce;
	}
	
	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		
		if(strResponce != null && strResponce.length() > 0) {
			if(strResponce.equals("ConnectTimeoutException")) {
				_ProgressDialog.dismiss();
				Common.displayAlertButtonToFinish(mActivity, mActivity.getResources().getString(R.string.str_Message), Common.TimeOut_Message,true);
				return;
		    } 
			
			Common.arrChild_Invite = null;
			Common.INVITE_TYPE = "";
			Common.INVITE_COUNT = 0;
			try {
			   // Get login response result
			   JSONObject jObjectResult = new JSONObject(strResponce).getJSONObject("result");
			   // check status true or false
			   String status = jObjectResult.getString("status");	
			   if(status.equals("success")) {
				 JSONObject jObjUserInfo = jObjectResult.getJSONObject("userinfo");
				 savedLoginCreadantial();
				if(!jObjUserInfo.getString("invite_by").equals("null")) {
				   JSONArray jArray =  jObjUserInfo.getJSONArray("invite_by");
				   if(jArray.length() > 0) {
					 for(int i=0;i<jArray.length();i++) {
					   JSONObject json_data = jArray.getJSONObject(i);
					   Common.INVITE_TYPE += json_data.getString("id")+",";
					 }
				   }
				 }
			
				 Common.USER_ID = jObjUserInfo.getString("user_id");
				 Common.USER_TOKEN = jObjUserInfo.getString("user_token");
				 Common.USER_PHONE_NO = jObjUserInfo.getString("phone_no");
				 Common.USER_TYPE = jObjUserInfo.getString("user_type");
				 if(jObjUserInfo.has("elc_id"))
					Common.ELC_ID = jObjUserInfo.getString("elc_id");
				 LogConfig.logd("Login responce invite user =",""+strResponce+" Invite ="+Common.INVITE_TYPE);	 
				 
				 if(Common.USER_TYPE.equals("1")) {
					 // call class to get response
					 JSONObject jsonTypeDetail = jObjUserInfo.getJSONObject("type_detail");
					 Common.CENTER_ID = jsonTypeDetail.getString("center_id");
				 } 
				 // create object sql data base open and create
				 Cursor cursor = null;	
				 Adapter mDbHelper = new Adapter(mActivity);
				 mDbHelper.createDatabase();
				 mDbHelper.open();
				 cursor = mDbHelper.getExucuteQurey(SqlQuery.getQueryLastModifyDate("",Common.USER_ID,Common.CHILD_DOWNLOAD));
				 if(cursor != null && cursor.getCount() > 0) { // data?
				    Common.CHILD_LAST_MODIFY_DATE = cursor.getString(cursor.getColumnIndex("sync_date"));
				 } 
				 mDbHelper.close();
				 
				 if(Common.CHILD_LAST_MODIFY_DATE == null)
					Common.CHILD_LAST_MODIFY_DATE = "";
			     if(Common.CHILD_LAST_MODIFY_DATE.length() == 0) {
				    int callService = 0;
				    Boolean _isExecute = sPrefs.getBoolean("EXECUTE_SERVICE", false);
				    Common._isCallChildList = false;
				    if(!_isExecute) {
					   callService = SyncingUtil.CHILD_DATA_DOWNLOAD;//2
				     } else {
					   callService = SyncingUtil.UPLOAD_DATABASE_ACTION;//5 
				     }
				    Common.isDatabaseSyncingInProgress = false;
				    Common._isDatabaseSyncingFromBackground = false;
				    GetSqlTableZipFileSyncTask mGetSqlTableZipFileSyncTask = new GetSqlTableZipFileSyncTask(mActivity, _ProgressDialog);
				    mGetSqlTableZipFileSyncTask.setAction(callService);
				    mGetSqlTableZipFileSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			     } else {
			    	if(_ProgressDialog != null)
					   _ProgressDialog.dismiss();
			    	Common._isCallChildList = true;
			    	callParentEducatorScreen();
			     }
			   } else {
				   if(_ProgressDialog != null)
					  _ProgressDialog.dismiss();
				 SharedPreferences.Editor editor = sPrefs.edit();
				 editor.putString("PASSWORD","");
				 editor.commit();
				 Common.dialogToLogin(mActivity,mActivity.getResources().getString(R.string.str_Message), jObjectResult.getString("message"),_isFinish);	
			   }
			} catch (JSONException e) {
				if(_ProgressDialog != null)
				   _ProgressDialog.dismiss();
				//e.printStackTrace();
			} catch (NullPointerException e) {
				if(_ProgressDialog != null)
				   _ProgressDialog.dismiss();
				//e.printStackTrace();
			}
		}
	}
	
	 public void callParentEducatorScreen() {
		  
		   // Syncing thread flags to manage local syncing and server syncing database
		   Common.isDatabaseSyncingInProgress = true;
		   Common._isDatabaseSyncingFromBackground = true;
		   if(Common.USER_TYPE.equals("1")) {
				 // call class to get response
				 sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
				 if(Common.INVITE_TYPE.length() > 0) {
					 Common.USER_TYPE = sPrefs.getString("USER_TYPE", "");
					 Common.VIEW_ONLY = sPrefs.getBoolean("VIEW_ONLY", false);
					 if(Common.USER_TYPE.equals("3")) {
						Common.arrChildData = new ArrayList<ChildDataParsing>();
						Common.arrChild_Invite = Common.INVITE_TYPE.split(",");
						InviteCountSyncTask mInviteCountSyncTask = new InviteCountSyncTask(mActivity,_ProgressDialog);
						mInviteCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					 } else {
						 String strLogin = sPrefs.getString("LOGIN_USER", "");
					   if(strLogin.length() > 1) {
						   Common.USER_TYPE = sPrefs.getString("USER_TYPE", "");
						   Common.VIEW_ONLY = sPrefs.getBoolean("VIEW_ONLY", false);
						   if(Common.USER_TYPE.equals("3")) {
							    SharedPreferences.Editor editor = sPrefs.edit();
								editor.putBoolean("VIEW_ONLY",true);
								editor.putString("USER_TYPE","3");
								editor.putString("LOGIN_USER","Xplorer");
								editor.commit();
								Common.arrChildData = new ArrayList<ChildDataParsing>();
								Common.arrChild_Invite = Common.INVITE_TYPE.split(",");
								InviteCountSyncTask mInviteCountSyncTask = new InviteCountSyncTask(mActivity,null);
								mInviteCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						   } else {
							    Common.VIEW_ONLY = true;
								Common.USER_TYPE = "1";
								SharedPreferences.Editor editor = sPrefs.edit();
								editor.putBoolean("VIEW_ONLY",true);
								editor.putString("USER_TYPE","1");
								editor.putString("LOGIN_USER","Educator");
								editor.commit();
								Intent intent = new Intent(mActivity, EducatorMainActivity.class);
								mActivity.startActivity(intent);
								mActivity.finish();
								mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
						   }
					   } else { //"Would you like to login as Educator or Xplorer?"
						   displayAlertMultiple(mActivity, mActivity.getResources().getString(R.string.str_like_educator_parent));
					   } 
					 }
				 } else {
				   Intent intent = new Intent(mActivity, EducatorMainActivity.class);
				   mActivity.startActivity(intent);
				   mActivity.finish();
				   mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
				 }
			 } else {
			 
		     if(Common.arrChildData != null)
		    	 Common.arrChildData.clear();
		     
			 if(Common.INVITE_TYPE.length() > 0)
			    Common.arrChild_Invite = Common.INVITE_TYPE.split(",");
			 
			    ChildListSyncTask mChildListSyncTask = new ChildListSyncTask(mActivity, _ProgressDialog);
			    mChildListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			 } 
	}
	
	public void displayAlertMultiple(Context mContext, String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_multiple);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			
			dialog.getWindow().setAttributes(wmlp);
			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnYes.setText("Xplorer");
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					Common.VIEW_ONLY = true;
					Common.USER_TYPE = "3";
					SharedPreferences.Editor editor = sPrefs.edit();
					editor.putBoolean("VIEW_ONLY",true);
					editor.putString("USER_TYPE","3");
					editor.putString("LOGIN_USER","Xplorer");
					editor.commit();
					Common.arrChildData = new ArrayList<ChildDataParsing>();
					Common.arrChild_Invite = Common.INVITE_TYPE.split(",");
					InviteCountSyncTask mInviteCountSyncTask = new InviteCountSyncTask(mActivity,null);
					mInviteCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					dialog.dismiss();
				}
			});

			Button btnNo = (Button) dialog.findViewById(R.id.Popup_No_Btn);
			btnNo.setText("Educator");
			btnNo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					Common.VIEW_ONLY = true;
					Common.USER_TYPE = "1";
					SharedPreferences.Editor editor = sPrefs.edit();
					editor.putBoolean("VIEW_ONLY",true);
					editor.putString("USER_TYPE","1");
					editor.putString("LOGIN_USER","Educator");
					editor.commit();
					Intent intent = new Intent(mActivity, EducatorMainActivity.class);
					mActivity.startActivity(intent);
					mActivity.finish();
					mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
					dialog.dismiss();
				}
			});
		}
	}
	
	// Saved login record
	private void savedLoginCreadantial() {
		
		if(Common.CHECK_REMEBER) {
			SharedPreferences.Editor editor = sPrefs.edit();
			editor.putString("LOGIN_EMAIL",Common.EMAIL_ID);
			editor.putString("REMEBER_EMAIL",Common.EMAIL_ID);
			editor.putString("PASSWORD",Common.PASSWORD);
			editor.putBoolean("REMEMBER",Common.CHECK_REMEBER);
			editor.commit();
		} else {
			SharedPreferences.Editor editor = sPrefs.edit();
			editor.putString("LOGIN_EMAIL",Common.EMAIL_ID);
			editor.putString("PASSWORD",Common.PASSWORD);
			editor.putString("REMEBER_EMAIL","");
			editor.putBoolean("REMEMBER",Common.CHECK_REMEBER);
			editor.commit();
		}
	}

}
