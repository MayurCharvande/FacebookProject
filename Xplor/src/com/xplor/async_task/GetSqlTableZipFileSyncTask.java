package com.xplor.async_task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.TextView;

import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.EducatorMainActivity;
import com.xplor.dev.MainScreenActivity;
import com.xplor.dev.R;
import com.xplor.helper.DataBaseHelper;
import com.xplor.local.syncing.download.ChallengeAndBadgesZipFileOnSdcard;
import com.xplor.local.syncing.download.ChildRecordZipFileOnSdcard;
import com.xplor.local.syncing.download.RoastringZipFileOnSdcard;
import com.xplor.local.syncing.download.SyncingUtil;
import com.xplor.local.syncing.upload.UploadDatabase;
import com.xplor.parsing.ChildDataParsing;

public class GetSqlTableZipFileSyncTask extends AsyncTask<Integer, Integer, Boolean> {
	
    private ProgressDialog _ProgressDialog = null;
	private SharedPreferences sPrefs = null;
	private Activity mActivity = null;
	private Boolean _isYes = false;
	private Boolean _isSuccess = false;
	private int mAction = 0;
	private Boolean _isExecute = false;
	
	public GetSqlTableZipFileSyncTask(Activity activity, ProgressDialog progressdialog) {
		  this.mActivity = activity;
		  this._ProgressDialog = progressdialog;
		  // Call method get current date time
		  Common.getCurrentDateToStartEndDate();
		  sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
		  _isExecute = sPrefs.getBoolean("EXECUTE_SERVICE", false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		  if(_ProgressDialog == null && !Common.isDatabaseSyncingInProgress) {
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
	
    public void setAction(int action) {
    	this.mAction = action;
	}
	
	@Override
	protected Boolean doInBackground(Integer... param) {
	   try {
		
		switch (mAction) {
		case SyncingUtil.CHILD_DATA_DOWNLOAD:
			// call class sqlite_zip file compress then store application folder then insert sql_database.
			ChildRecordZipFileOnSdcard mStoreZipFileOnSdcard = new ChildRecordZipFileOnSdcard(mActivity);
			mStoreZipFileOnSdcard.setDirctoryPath(LogConfig.PATH_LOGIN_CHILD_LIST);
			mStoreZipFileOnSdcard.setUserId(Common.USER_ID);
			mStoreZipFileOnSdcard.setCenterId(Common.CENTER_ID);
			mStoreZipFileOnSdcard.setUserType(Common.USER_TYPE);
			mStoreZipFileOnSdcard.setGetLMDType(Common.CHILD_DOWNLOAD);
			_isSuccess = mStoreZipFileOnSdcard.sendDatabaseRequest(WebUrls.SQL_CHILD_RECORD);
			break;
        case SyncingUtil.CHALLENGES_BADGES_DOWNLOAD:
        	// call class sqlite_zip file compress then store application folder then insert sql_database.
        	ChallengeAndBadgesZipFileOnSdcard mChallengeAndBadgesZipFileOnSdcard = new ChallengeAndBadgesZipFileOnSdcard(mActivity);
        	mChallengeAndBadgesZipFileOnSdcard.setDirctoryPath(LogConfig.PATH_LEADER_BOARD_LIST);
        	mChallengeAndBadgesZipFileOnSdcard.setUserId(Common.USER_ID);
        	mChallengeAndBadgesZipFileOnSdcard.setUserType(Common.USER_TYPE);
        	mChallengeAndBadgesZipFileOnSdcard.setGetLMDType(Common.LEADER_BOARD_DOWNLOAD);
        	_isSuccess = mChallengeAndBadgesZipFileOnSdcard.sendDatabaseRequest(WebUrls.SQL_LEADER_BOARD_RECORD);
			break;
        case SyncingUtil.ROASTRING_DOWNLOAD:
        	
        	// call class sqlite_zip file compress then store application folder then insert sql_database.
        	RoastringZipFileOnSdcard mRoastringZipFileOnSdcard = new RoastringZipFileOnSdcard(mActivity);
        	mRoastringZipFileOnSdcard.setDirctoryPath(LogConfig.PATH_ROASTRING_LIST);
        	mRoastringZipFileOnSdcard.setUserId(Common.USER_ID);
        	mRoastringZipFileOnSdcard.setUserType(Common.USER_TYPE);
        	mRoastringZipFileOnSdcard.setGetLMDType(Common.ROSTER_DOWNLOAD);
        	_isSuccess = mRoastringZipFileOnSdcard.sendDatabaseRequest(WebUrls.SQL_ROASTRING_RECORD);
			break;
        case SyncingUtil.UPLOAD_DATABASE_ACTION:
        	 Log.e("MyXplor", "upload database running Action");
			 UploadDatabase mUploadDatabase = new UploadDatabase(mActivity);
			 try {
				 _isSuccess = mUploadDatabase.uploadDatabaseAction();
			 } catch (Exception e) {
				Log.e("MyXplor","zip Currupt , upload database running Action: "+ e.toString());
			    mAction = SyncingUtil.CHILD_DATA_DOWNLOAD;
			 }
			
			break;
        case SyncingUtil.NONE_DATA_ACTION: {
        	
        }
        	break;
		default:
			break;
		}
			return _isSuccess;
		} catch (Exception e) {
			if(_ProgressDialog != null) {
			   _ProgressDialog.dismiss();
			   _ProgressDialog = null;
			}
		}
		return _isSuccess;
	}
	
	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		LogConfig.logd("Data base file responce =",""+success);
		if(success) {
			if(mAction == SyncingUtil.CHILD_DATA_DOWNLOAD) //2
				mAction = SyncingUtil.CHALLENGES_BADGES_DOWNLOAD;//3
			else if(mAction == SyncingUtil.CHALLENGES_BADGES_DOWNLOAD) //3
				mAction = SyncingUtil.ROASTRING_DOWNLOAD; //4
			else if(mAction == SyncingUtil.ROASTRING_DOWNLOAD) { //4
				if(_isExecute) {
				   mAction = SyncingUtil.NONE_DATA_ACTION;
				} else {
				   mAction = SyncingUtil.UPLOAD_DATABASE_ACTION;
				}
				if(MainScreenActivity.headerProgressBar != null) {
				   MainScreenActivity.headerProgressBar.setVisibility(View.GONE);
				}
				if(EducatorMainActivity.headerProgressBar != null) {
				   EducatorMainActivity.headerProgressBar.setVisibility(View.GONE);
				}
				_isYes = true;
			} else if(mAction == SyncingUtil.UPLOAD_DATABASE_ACTION) {
				if(!_isExecute) {
				     mAction = SyncingUtil.NONE_DATA_ACTION;
				} else {
				     mAction = SyncingUtil.CHILD_DATA_DOWNLOAD;
				}
			}

			 final GetSqlTableZipFileSyncTask mGetSqlTableZipFileSyncTask = new GetSqlTableZipFileSyncTask(mActivity, _ProgressDialog);
			 mGetSqlTableZipFileSyncTask.setAction(mAction);
			 mGetSqlTableZipFileSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			
			 if(!Common._isCallChildList && _isYes) {
			    _isYes = false;
			   Common._isCallChildList = true;
			   exportDB();
			   callParentEducatorScreen();
			 }
	    } else {
	    	if(_ProgressDialog != null) {
			   _ProgressDialog.dismiss();
			   _ProgressDialog = null;
	    	}
	    	if(MainScreenActivity.headerProgressBar != null) {
			   MainScreenActivity.headerProgressBar.setVisibility(View.GONE);
			}
			if(mAction == SyncingUtil.CHILD_DATA_DOWNLOAD && !Common.isDatabaseSyncingInProgress) {
				SharedPreferences.Editor editor = sPrefs.edit();
				editor.putString("PASSWORD","");
				editor.commit();
				try {
	                 Common.dialogToLogin(mActivity,mActivity.getResources().getString(R.string.str_Message),
	        		 mActivity.getResources().getString(R.string.str_No_Child_Reg),true);
				} catch(BadTokenException e) {
					// dialog token bad
				}
			} 
		}
		
	}
	
   public void callParentEducatorScreen() {
	   
	       Editor editor1 = sPrefs.edit();
		   editor1.putBoolean("EXECUTE_SERVICE", true);
		   editor1.commit();
		   // Syncing thread flags to manage local syncing and server syncing database
		   Common.isDatabaseSyncingInProgress = true;
		   Common._isDatabaseSyncingFromBackground = true;
		   if(Common.USER_TYPE.equals("1")) {
				 // call class to get response
			   if(_ProgressDialog != null)
				  _ProgressDialog.dismiss();
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
	
	private void exportDB() {
		
		   Log.d("Call exportDB", "Data base store");
		    File sd = new File(Environment.getExternalStorageDirectory() + "/MyXplor/");
		    if (!sd.exists()) {
		     sd.mkdir();
		    }
		        File data = Environment.getDataDirectory();
		        FileChannel source=null;
		        FileChannel destination=null;
		        String currentDBPath = "/data/" + mActivity.getPackageName() + "/databases/"+DataBaseHelper.DB_NAME;
		        String backupDBPath = "backup_xplor.db";
		        File currentDB = new File(data, currentDBPath);
		        File backupDB = new File(sd, backupDBPath);
		        
		        try {
		             source = new FileInputStream(currentDB).getChannel();
		             destination = new FileOutputStream(backupDB).getChannel();
		             destination.transferFrom(source, 0, source.size());
		             source.close();
		             destination.close();
		         } catch(IOException e) {
		           // e.printStackTrace();
		         }
	 }
}
