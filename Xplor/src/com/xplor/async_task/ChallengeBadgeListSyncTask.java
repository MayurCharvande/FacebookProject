package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.ChallengeBadgesCallBack;
import com.xplor.local.syncing.download.SqlQuery;
import com.xplor.parsing.ChallengeParsing;

public class ChallengeBadgeListSyncTask extends AsyncTask<String, Integer, String> {
	
	private Activity mActivity = null;
	private ChallengeBadgesCallBack mChallengeBadgesCallBack=null;
	private Adapter mDBHelper = null;
	
	public ChallengeBadgeListSyncTask(Activity activity) {
		 this.mActivity = activity;
		 this.mDBHelper = new Adapter(mActivity);
		 mDBHelper.createDatabase();
		 mDBHelper.open();
	}
	
	public void setChallengesBadgesCallBack(ChallengeBadgesCallBack challengeBadgesCallBack) {
	     this.mChallengeBadgesCallBack = challengeBadgesCallBack;
    }
	
	@Override
	protected String doInBackground(String... param) {
		   Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryBadges());
		   if(mCursor != null && mCursor.getCount() > 0) {
			   try {
				  Common.arrBadges = new ArrayList<ChallengeParsing>();
				  Common.arrChallenges = new ArrayList<ChallengeParsing>();

				if (mCursor.moveToFirst()) {
				  do {
					  ChallengeParsing child_Top_Badge = new ChallengeParsing();
					  child_Top_Badge.setCHALLENGE_BADGE_NAME("Badges");
					  child_Top_Badge.setBADGE_ID(mCursor.getString(mCursor.getColumnIndex("id")));
					  child_Top_Badge.setBADGE_TITLE(mCursor.getString(mCursor.getColumnIndex("title")));
					  child_Top_Badge.setBADGE_TITLE_NAME(mCursor.getString(mCursor.getColumnIndex("title")));
					  child_Top_Badge.setBADGE_DESC(mCursor.getString(mCursor.getColumnIndex("description")));
					  child_Top_Badge.setBADGE_GRAPHICS_FRONT(mCursor.getString(mCursor.getColumnIndex("graphic_front")));
					  child_Top_Badge.setBADGE_GRAPHICS_BACK(mCursor.getString(mCursor.getColumnIndex("graphic_back")));
					  
					  String strId = getBadgeStatus(mCursor.getString(mCursor.getColumnIndex("id")));
					  if (strId != null && strId.length() > 0) {
						  child_Top_Badge.setBADGE_LOCK("No");
						  child_Top_Badge.setSHOW_TEXT_BUTTON("No");
					  } else {
						  child_Top_Badge.setBADGE_LOCK("Yes");
					
					   int vcId = getBadgeCriteria(mCursor.getInt(mCursor.getColumnIndex("id")));
					   if(vcId != 0) {
						  if(vcId == 1 || vcId == 2) {
							 child_Top_Badge.setSHOW_TEXT_BUTTON("Yes");
						  } else {
							 child_Top_Badge.setSHOW_TEXT_BUTTON("No");
						  } 
					   } else {
						 child_Top_Badge.setSHOW_TEXT_BUTTON("No"); 
					   }
				     } 
					 Common.arrBadges.add(child_Top_Badge);
				  } while(mCursor.moveToNext());  
				}
				mCursor.close();
				Cursor myCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryChallenges());
			    if (myCursor.moveToFirst()) {
			    	int value=0;
				  do {   
					  value = 0;
					  ChallengeParsing child_challenge = new ChallengeParsing();
					  child_challenge.setCHALLENGE_ID(myCursor.getString(myCursor.getColumnIndex("id")));
					  child_challenge.setCHALLENGE_TITLE(myCursor.getString(myCursor.getColumnIndex("title")));
					  child_challenge.setCHALLENGE_DESC(myCursor.getString(myCursor.getColumnIndex("description")));
					  child_challenge.setARR_BADGE(getBadgesOfChallenges(value,myCursor.getString(myCursor.getColumnIndex("id")),myCursor.getString(myCursor.getColumnIndex("title"))));
					  Common.arrChallenges.add(child_challenge);
				    } while(myCursor.moveToNext());
				 }
			      myCursor.close();
				} catch (SQLiteException se) {
				    Log.e(getClass().getSimpleName(),"Could not create or Open the database");
				} finally {
					mDBHelper.close();
			    }
			   
			} 
		return "Record";
	}
	
	@Override
	protected void onPostExecute(String responce) {
		super.onPostExecute(responce);
		LogConfig.logd("Badges responce =",""+responce);
		mChallengeBadgesCallBack.requestChallengeBadges();
	}
	
	public List<ChallengeParsing> getBadgesOfChallenges(int value,String id,String title) {
		
		  Cursor badgesCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryBadgesListForChallenges(id));
		  List<ChallengeParsing> arrBadge_Sub = new ArrayList<ChallengeParsing>();
	      if(badgesCursor.moveToFirst()) {
		    do {
			  ChallengeParsing child_badge_sub = new ChallengeParsing();
			  child_badge_sub.setCHALLENGE_BADGE_NAME("Challenges");
			  child_badge_sub.setBADGE_ID(badgesCursor.getString(badgesCursor.getColumnIndex("id")));
			  child_badge_sub.setBADGE_TITLE_NAME(badgesCursor.getString(badgesCursor.getColumnIndex("title")));
			   if(value==0){
				  child_badge_sub.setBADGE_TITLE(title);
				  value++;
			   } else {
				  child_badge_sub.setBADGE_TITLE("");
			   }
			   
			   String strId = getBadgeStatus(badgesCursor.getString(badgesCursor.getColumnIndex("id")));
				  if (strId != null && strId.length() > 0) {
					  child_badge_sub.setBADGE_LOCK("No");
					  child_badge_sub.setSHOW_TEXT_BUTTON("No");
				  } else {
					  child_badge_sub.setBADGE_LOCK("Yes");
				
				   int vcId = getBadgeCriteria(badgesCursor.getInt(badgesCursor.getColumnIndex("id")));
				   if(vcId != 0) {
					  if(vcId == 1 || vcId == 2) {
						  child_badge_sub.setSHOW_TEXT_BUTTON("Yes");
					  } else {
						  child_badge_sub.setSHOW_TEXT_BUTTON("No");
					  } 
				   } else {
					   child_badge_sub.setSHOW_TEXT_BUTTON("No"); 
				   }
			     } 
			   
//			   int vcId = getBadgeCriteria(badgesCursor.getInt(badgesCursor.getColumnIndex("id")));
//			   if(vcId > 0) {
//				  child_badge_sub.setSHOW_TEXT_BUTTON("Yes");
//				  child_badge_sub.setBADGE_LOCK("Yes");
//			   } else {
//				  child_badge_sub.setSHOW_TEXT_BUTTON("No");
//				  child_badge_sub.setBADGE_LOCK("No");
//			   }
			  child_badge_sub.setBADGE_DESC(badgesCursor.getString(badgesCursor.getColumnIndex("description")));
			  child_badge_sub.setBADGE_GRAPHICS_FRONT(badgesCursor.getString(badgesCursor.getColumnIndex("graphic_front")));
			  child_badge_sub.setBADGE_GRAPHICS_BACK(badgesCursor.getString(badgesCursor.getColumnIndex("graphic_back")));
			  arrBadge_Sub.add(child_badge_sub);
		    } while(badgesCursor.moveToNext());
		      badgesCursor.close();
	      }
	      return arrBadge_Sub;
	}
	
	public String getBadgeStatus(String id) {
		   String strId = "";
		   Cursor cursor = null;
		   try { 
				cursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryBadgesStatus(id,Common.CHILD_ID));
			    if(cursor != null && cursor.getCount() > 0) {
			       strId = cursor.getString(cursor.getColumnIndex("id"));
			    }
		    } finally {
			  if(cursor != null)
				 cursor.close();
			}
		   return strId;
	}
	
    public int getBadgeCriteria(int id) {
    	
		int vcId = 0;
		Cursor cursor = null;
		try { 
			cursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryBadgesCriteria(id));
			if(cursor != null && cursor.getCount() > 0) {
			  vcId = cursor.getInt(cursor.getColumnIndex("vc_id"));
			}	    
		} finally {
		  if(cursor != null)
			 cursor.close();
		}
		return vcId;
	}
}
