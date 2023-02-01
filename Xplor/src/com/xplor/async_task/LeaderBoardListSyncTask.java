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
import com.xplor.interfaces.LeaderBoardCallBack;
import com.xplor.local.syncing.download.SqlQuery;

public class LeaderBoardListSyncTask extends AsyncTask<String, Integer, List<ParentVariable>> {
	
	private LeaderBoardCallBack mLeaderBoardCallBack=null;
	private String strElcID = "";
	private Activity mActivity = null;
	private Adapter mDBHelper = null;

	public LeaderBoardListSyncTask(Activity activity) {
		 this.mActivity = activity;
		 getELCID();// get elc id to leader board records
		 //Sqlite database create and open
		 this.mDBHelper = new Adapter(mActivity);
		 mDBHelper.createDatabase();
		 mDBHelper.open();
	}
	
	public void setmLeaderBoardCallBack(LeaderBoardCallBack leaderBoardCallBack) {
		this.mLeaderBoardCallBack = leaderBoardCallBack;
	}
	
	@Override
	protected List<ParentVariable> doInBackground(String... param) {
		
		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryLeaderboardDetails(strElcID));
		if(mCursor != null && mCursor.getCount() > 0) {
			List<ParentVariable> _listArrayFollow = new ArrayList<ParentVariable>();
		   try { // Get child list form sql_database parent and educator tables  
			if (mCursor.moveToFirst()) {
				int j = 0;
		      do {
		    	  ParentVariable mChallengesBadgesParentVariable= new ParentVariable();
				  mChallengesBadgesParentVariable.setId(mCursor.getString(mCursor.getColumnIndex("parent_id")));
				  mChallengesBadgesParentVariable.setImage(mCursor.getString(mCursor.getColumnIndex("image")));
				  mChallengesBadgesParentVariable.setName(mCursor.getString(mCursor.getColumnIndex("name")));
				  String status = getFollowParentStatus(mCursor.getString(mCursor.getColumnIndex("parent_id")),Common.USER_ID);
				  mChallengesBadgesParentVariable.setFollow_status(status);
				  String strNoofBadges = mCursor.getString(mCursor.getColumnIndex("no_of_badges"));
				  if(strNoofBadges != null && strNoofBadges.length() >0)
				     mChallengesBadgesParentVariable.setBadges_Count(strNoofBadges);
				  else mChallengesBadgesParentVariable.setBadges_Count("0");
				  
				  mChallengesBadgesParentVariable.setList_Number(""+(j++));
				  _listArrayFollow.add(mChallengesBadgesParentVariable);
		        } while (mCursor.moveToNext());
			  }
		     } catch (SQLiteException se) {
			    Log.e(getClass().getSimpleName(),"Could not create or Open the database");
			 } finally {
				 mDBHelper.close();
			     mCursor.close();
		     }
		   return _listArrayFollow;
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(List<ParentVariable> _listArrayFollow) {
		super.onPostExecute(_listArrayFollow);
		LogConfig.logd("Leader board responce =",""+_listArrayFollow);
		
		if(_listArrayFollow != null)
		   mLeaderBoardCallBack.requestUpdateLeaderBoard(_listArrayFollow);
	}
	
	private String getFollowParentStatus(String parentId,String userId) {
		
		String status = "0";
		Cursor cursor = null;
		try { 
			cursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryFollowParentStatus(parentId,userId));
			if(cursor != null && cursor.getCount() > 0) {
				status = cursor.getString(cursor.getColumnIndex("status"));
			}	
			
		} finally {
		  if(cursor != null)
			 cursor.close();
		}
		return status;
	}
	
    private void getELCID() {
		
		Adapter mAdapter = new Adapter(mActivity);
		mAdapter.createDatabase();
		mAdapter.open();
	    Cursor cursor = mAdapter.getExucuteQurey(SqlQuery.getQueryParentEducatorElcId(Common.USER_ID));
	    strElcID = "";
	    if(cursor != null) {// data?
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
}
