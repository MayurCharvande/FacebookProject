package com.xplor.async_task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.Adapter;
import com.xplor.local.syncing.download.SqlQuery;

public class EditChildProfileSyncTask extends AsyncTask<String, Integer, Cursor> {

	private ProgressDialog _ProgressDialog =null;
	private Activity mActivity = null;
	private Adapter mDBHelper = null;
	
	public EditChildProfileSyncTask(Activity activity) {
	    this.mActivity = activity;
	    // Create sqlite database and open
	    this.mDBHelper = new Adapter(mActivity);
	    mDBHelper.createDatabase();
	    mDBHelper.open();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(_ProgressDialog == null) {
			_ProgressDialog= new ProgressDialog(mActivity);
			_ProgressDialog.setIndeterminate(true);
			_ProgressDialog.setCancelable(false);
			_ProgressDialog.setMessage("Loading!\nPlease wait...");
			_ProgressDialog.show();
		}
	}
	
	@Override
	protected Cursor doInBackground(String... param) {

		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.updateQueryParentChildProfile(
				Common.USER_ID, Common.CHILD_FIRST_NAME, Common.CHILD_LAST_NAME, Common.CHILD_DOB, 
				Common.CHILD_GENDER, Common.CHILD_ALLERGIES, Common.CHILD_BIO, Common.CHILD_IMAGE, 
				Common.CENTER_ID, Common.ROOM_ID, "1", Common.CHILD_EMERG_NAME_1, Common.CHILD_EMERG_PHONENO_1, 
				Common.CHILD_EMERG_NAME_2, Common.CHILD_EMERG_PHONENO_2, 
				Common.CHILD_NEEDS, Common.CHILD_MEDICATION, Common.getCurrentDateTime(), 
				Common.deviceModal, Common.getAndroidVersion(), WebUrls.WEB_SERVICE_VERSION, 
				Common.generateMobileKey(), 0, Common.DEVICE_TYPE, Common.USER_ID, Common.CHILD_ID));
		
		return mCursor;
	}
	
	@Override
	protected void onPostExecute(Cursor mCursor) {
		super.onPostExecute(mCursor);
		_ProgressDialog.dismiss();
		LogConfig.logd("edit child profile responce =",""+mCursor.getCount());
		Common.displayAlertButtonToFinish(mActivity,"Message","Successfully update profile data.", true);
	}
}
