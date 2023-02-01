package com.xplor.async_task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.WindowManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.dev.MainScreenActivity;
import com.xplor.dev.R;
import com.xplor.helper.Adapter;
import com.xplor.local.syncing.download.SqlQuery;

public class InviteCountSyncTask extends AsyncTask<String, Integer, Cursor> {
	
	private ProgressDialog _ProgressDialog;
	private Activity mActivity = null;
	private Adapter mDbHelper = null;
	private String strId = "";
	
	public InviteCountSyncTask(Activity activity,ProgressDialog progressdialog) {
		  this.mActivity = activity;
		  this._ProgressDialog = progressdialog;
		  
		  try {
		    strId = Common.arrChild_Invite[Common.INVITE_COUNT];
		  } catch(ArrayIndexOutOfBoundsException e) {
	        // IndexOutOfBoundsException
		  }
		 // create object web-service handler class
		 mDbHelper = new Adapter(mActivity);
		 mDbHelper.createDatabase();
		 mDbHelper.open();
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
	protected Cursor doInBackground(String... param) {

		Cursor mCursor = mDbHelper.getExucuteQurey(SqlQuery.inviteCountQuery(strId));
		return mCursor;
	}
	
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		LogConfig.logd("child count strResponce =",""+mCursor.getCount());
		
		if(mCursor != null && mCursor.getCount() > 0) {
			
			try {
				  Common.INVITE_CHILD_COUNT = mCursor.getCount();
				  if(Common.INVITE_CHILD_COUNT == 0) {
				      if(Common.arrChild_Invite != null && Common.INVITE_COUNT < Common.arrChild_Invite.length) {
				    	  Common.INVITE_COUNT ++;
				    	     try {
							    strId = Common.arrChild_Invite[Common.INVITE_COUNT];
							    InviteCountSyncTask mInviteCountSyncTask = new InviteCountSyncTask(mActivity,_ProgressDialog);
								mInviteCountSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
							 } catch(ArrayIndexOutOfBoundsException e) {
						          _ProgressDialog.dismiss();
								  Intent intent = new Intent(mActivity, MainScreenActivity.class);
								  mActivity.startActivity(intent);
								  mActivity.finish();
								  mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
							 }
					  } else {
						  _ProgressDialog.dismiss();
						  // call class to get response
						  Intent intent = new Intent(mActivity, MainScreenActivity.class);
						  mActivity.startActivity(intent);
						  mActivity.finish();
						  mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
					  }
				  } else {
					  InviteListSyncTask mInviteListSyncTask = new InviteListSyncTask(mActivity,_ProgressDialog);
					  mInviteListSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
				  }
			} catch (NullPointerException e) {
				_ProgressDialog.dismiss();
			}
		} else {
			 _ProgressDialog.dismiss();
			  // call class to get response
			  Intent intent = new Intent(mActivity, MainScreenActivity.class);
			  mActivity.startActivity(intent);
			  mActivity.finish();
			  mActivity.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
		}
	}
}
