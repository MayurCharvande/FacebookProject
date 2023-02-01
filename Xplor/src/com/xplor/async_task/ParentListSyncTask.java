package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.Model.ParentVariable;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.ParentCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class ParentListSyncTask extends AsyncTask<String, Integer, String> {

	//private ProgressDialog _ProgressDialog = null;
	private Activity mActivity = null;
	private ParentCallBack mParentCallBack = null;
	private Adapter mDBHelper = null;
	private String strElcID = "";
	private List<String> arrayFollow = null;

	public ParentListSyncTask(Activity activity) {
		this.mActivity = activity;
		getELCID();// get elcId to get parent record
		getFollowgetParentList();
		// Sqlite database create and open
		this.mDBHelper = new Adapter(mActivity);
		mDBHelper.createDatabase();
		mDBHelper.open();
	}

	// set call back interface class using value parsing
	public void setmParentCallBack(ParentCallBack parentCallBack) {
		this.mParentCallBack = parentCallBack;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		if (_ProgressDialog == null) {
//			_ProgressDialog = ProgressDialog.show(mActivity, "", "", true);
//			_ProgressDialog.setCancelable(false);
//			_ProgressDialog.setContentView(R.layout.loading_view);
//			WindowManager.LayoutParams wmlp = _ProgressDialog.getWindow().getAttributes();
//			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
//			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
//			_ProgressDialog.getWindow().setAttributes(wmlp);
//		}
	}

	@Override
	protected String doInBackground(String... param) {
		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryParentEducatorDetails(strElcID, Common.USER_ID));
		if (mCursor != null && mCursor.getCount() > 0) {
			if(Common.arrLeaderBoardParentList == null)
			   Common.arrLeaderBoardParentList = new ArrayList<ParentVariable>();
			   Common.arrLeaderBoardParentList.clear();
			try {
				if (mCursor.moveToFirst()) {
					do {
						ParentVariable mParentVariable = new ParentVariable();
						mParentVariable.setId(mCursor.getString(mCursor.getColumnIndex("id")));
						mParentVariable.setImage(mCursor.getString(mCursor.getColumnIndex("image")));
						mParentVariable.setName(mCursor.getString(mCursor.getColumnIndex("name")));
						mParentVariable.setFollow_status("0");
						Common.arrLeaderBoardParentList.add(mParentVariable);
					} while (mCursor.moveToNext());
				}
				
			} catch (SQLiteException se) {
				Log.e(getClass().getSimpleName(),"Could not create or Open the database");
			} finally {
				mDBHelper.close();
				mCursor.close();
			}
		}
		return "Record";
	}

	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		//_ProgressDialog.dismiss();
		LogConfig.logd("Parent list responce =", "" + responce);
		getFollowStatus();
	}
	
	private void getFollowStatus() {
		
		if(arrayFollow != null && arrayFollow.size() >0) {
		  for(int i=0;i<arrayFollow.size();i++) { 
	         for (int j=0;j<Common.arrLeaderBoardParentList.size();j++) {
	            if (arrayFollow.get(i).contains(Common.arrLeaderBoardParentList.get(j).getId())) {
	            	Common.arrLeaderBoardParentList.get(j).setFollow_status("1");
	            	break;
	            } 
	         }
		  }
		}
		mParentCallBack.requestUpdateParentCallBack();
	}

	private void getELCID() {

		Adapter mAdapter = new Adapter(mActivity);
		mAdapter.createDatabase();
		mAdapter.open();
		Cursor cursor = mAdapter.getExucuteQurey(SqlQuery.getQueryParentEducatorElcId(Common.USER_ID));
		strElcID = "";
		if (cursor != null) {// data?
			try { // Get child list form sql_database parent and educator tables
				if (cursor.moveToFirst()) {
					do {
						strElcID = cursor.getString(cursor.getColumnIndex("elc_id"));
					} while (cursor.moveToNext());
				}

			} catch (SQLiteException se) {
				Log.e(getClass().getSimpleName(),"Could not create or Open the database");
			} finally {
				cursor.close();
				mAdapter.close();
			}
		}
	}

	private void getFollowgetParentList() {

		Adapter mAdapter = new Adapter(mActivity);
		mAdapter.createDatabase();
		mAdapter.open();
		Cursor mCursor = mAdapter.getExucuteQurey(SqlQuery.getQueryFollowParent(Common.USER_ID));
		try {
			arrayFollow = new ArrayList<String>();
			if (mCursor.moveToFirst()) {
				do {
					arrayFollow.add(mCursor.getString(mCursor.getColumnIndex("followed_parent_id")));
				} while (mCursor.moveToNext());
			}
		} catch (SQLiteException se) {
			Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		} finally {
			mAdapter.close();
			mCursor.close();
		}
	}

}
