package com.xplor.dev;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xplor.Model.ParentVariable;
import com.xplor.adaptor.EducatorRoomAdapter;
import com.xplor.adaptor.GridViewWithHeaderAndFooter;
import com.xplor.async_task.ChildLiveCountAsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.RoomChildCountCallBack;
import com.xplor.local.syncing.download.SqlQuery;

@SuppressLint({ "InflateParams", "HandlerLeak" }) 
public class EducatorRoomSelectionFragment extends Fragment implements RoomChildCountCallBack {

	private View convertView = null;
	private Activity mActivity = null;
	private TextView txtChildCount = null;
	private EducatorRoomAdapter mEducatorRoomAdapter = null;
	private GridViewWithHeaderAndFooter gridView = null;
	private ArrayList<ParentVariable> roomArrayCount = null;
	private ArrayList<ParentVariable> roomArray = null;
	private int totalCount = 0;
	private Timer timer = null;
	private Validation mValidation = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.educator_dynamic_room_selection, container, false);
		mActivity = getActivity();
		createViews();
		
		return convertView;
	}

	@Override
	public void onResume() {
		super.onResume();
		callLiveChildCount(true);
		startTimers();
	}

	private void createViews() {
       // Find all using views
       roomArrayCount = new ArrayList<ParentVariable>();
  	   roomArray = new ArrayList<ParentVariable>();
	   gridView = (GridViewWithHeaderAndFooter) convertView.findViewById(R.id.educator_room_grid_view);
	   txtChildCount = (TextView) convertView.findViewById(R.id.Room_TotalChild_Checked_Txt);
	   mValidation = new Validation(mActivity);
	   
	}

	// Child check in count check
	public void callLiveChildCount(Boolean _isLoading) {
		  getEducatorRoom(); 
		  if(mValidation.checkNetworkRechability()) {
			 ChildLiveCountAsyncTask mChildLiveCountAsyncTask = new ChildLiveCountAsyncTask(mActivity,_isLoading);
			 mChildLiveCountAsyncTask.setCheckStatusCallBack(EducatorRoomSelectionFragment.this);
			 mChildLiveCountAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		  }
	}

	@Override
	public void roomChildCountCallBack(String responce) {
        // get result child check-in/out count
		try {
			JSONObject jObj = new JSONObject(responce);
			if (jObj.has("result")) {
				totalCount = 0;
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					roomArrayCount.clear();
					JSONArray mJSONArray = resultObj.getJSONArray("room_wise_count");
					for (int i = 0; i < mJSONArray.length(); i++) {
						ParentVariable mParentVariable = new ParentVariable();
						mParentVariable.setCount(""+mJSONArray.getJSONObject(i).getInt("count"));
						mParentVariable.setName(mJSONArray.getJSONObject(i).getString("room_name"));
						roomArrayCount.add(mParentVariable);
					    totalCount += mJSONArray.getJSONObject(i).getInt("count");	
					}
					
					if (totalCount == 0)
						txtChildCount.setText("No Child Checked-in");
					else if (totalCount == 1)
						txtChildCount.setText(totalCount + " Child Checked-in");
					else
						txtChildCount.setText(totalCount+ " Children Checked-in");
				}
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		setValues();
	}
	
    public void getEducatorRoom() {
		// get rooms from sql database 
		Adapter mDBHelper = new Adapter(mActivity);
		mDBHelper.createDatabase();
		mDBHelper.open();
		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryEducatorRoom(Common.ELC_ID, Common.CENTER_ID));
		  try { 
			  roomArray.clear();
			if(mCursor.moveToFirst()) {
				do{ //room_id, room_name
					
					ParentVariable mParentVariable = new ParentVariable();
					mParentVariable.setId(mCursor.getString(mCursor.getColumnIndex("room_id")));
					mParentVariable.setName(mCursor.getString(mCursor.getColumnIndex("room_name")));
					mParentVariable.setCount("0");
					roomArray.add(mParentVariable);
				} while(mCursor.moveToNext());
			}
		   } catch (SQLiteException se) {
			   Log.e(getClass().getSimpleName(),"Could not create or Open the database");
		   } finally {
			   mCursor.close();
			   mDBHelper.close();
		   } 
		  getChildCheckedIn();
    }
    
    // Get rooms checkinout record from local data base
    private void getChildCheckedIn() {
 	   
     	Common.getCurrentDateToStartEndDate();
     	Adapter mDBHelper = new Adapter(mActivity);
 		mDBHelper.createDatabase();
 		mDBHelper.open();
 		String strQuery = SqlQuery.getRoomCheckinCount(Common.STR_CURENT_START_DATE,Common.STR_CURENT_END_DATE, Common.CENTER_ID);
 		LogConfig.logd("Rooms getChildCheckedIn =",""+strQuery);
 		Cursor mCursor = mDBHelper.getExucuteQurey(strQuery);
 		  try { 
 			  if(mCursor.getCount()>0) {
 			    roomArrayCount.clear();
 			    totalCount = 0;
 			    if(mCursor.moveToFirst()) {
 					do { 
 						ParentVariable mParentVariable = new ParentVariable();
 						mParentVariable.setId(mCursor.getString(mCursor.getColumnIndex("room_id")));
 						mParentVariable.setName(getRoomName(mCursor.getString(mCursor.getColumnIndex("room_id"))));
 						mParentVariable.setCount(""+mCursor.getInt(mCursor.getColumnIndex("count")));
 						totalCount += mCursor.getInt(mCursor.getColumnIndex("count"));
 						roomArrayCount.add(mParentVariable);
 					} while(mCursor.moveToNext());
 				}
 			    
 			    if (totalCount == 0)
					txtChildCount.setText(totalCount + "No Child Checked-in");
				else if (totalCount == 1)
					txtChildCount.setText(totalCount + " Child Checked-in");
				else
					txtChildCount.setText(totalCount+ " Children Checked-in");
 			  }
 		   } catch (SQLiteException se) {
 			   Log.e(getClass().getSimpleName(),"Could not create or Open the database");
 		   } finally {
 			   mCursor.close();
 			   mDBHelper.close();
 		   } 
 		 setValues();
     }
    
 // set list values checkin/out and rooms
    private void setValues() {

   	       try {
			   for (int i = 0;i<roomArray.size();i++) {
	             for (int j = 0;j<roomArrayCount.size();j++) {
	                String count = roomArrayCount.get(j).getCount();
	                String roomName = roomArrayCount.get(j).getName();
	                if(roomName.equals(roomArray.get(i).getName())) {
	                	roomArray.get(i).setName(roomName);
	                	roomArray.get(i).setCount(count);
	                    break;
	                }
	            }
	          }
			} catch(NullPointerException e) {
				//e.printStackTrace();
			} catch(ArrayIndexOutOfBoundsException e) {
				//e.printStackTrace();
			}
		    mEducatorRoomAdapter = new EducatorRoomAdapter(getActivity(), roomArray);
		    gridView.setAdapter(mEducatorRoomAdapter);
		
	}
   
   // Get room name from local data base to roomId
    private String getRoomName(String roomId) {
    	
    	Adapter mDBHelper = new Adapter(mActivity);
		mDBHelper.createDatabase();
		mDBHelper.open();
    	Cursor mRoomCursor = mDBHelper.getExucuteQurey(SqlQuery.getRoomCheckRoomName(Common.CENTER_ID,roomId));
    	String roomName = "";
    	if(mRoomCursor.getCount()>0) {
    		roomName = mRoomCursor.getString(mRoomCursor.getColumnIndex("room_name"));
    		mRoomCursor.close();
    		mDBHelper.close();
    		return roomName;
    	}
    	return roomName;
    }
    
    // Refresh rooms count 
    public void startTimers() {

     if(timer == null) {
		// Declare the timer
		timer = new Timer();
		// Set the schedule function and rate
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Called each time when 1000 milliseconds (1 second) (the period parameter)
				LogConfig.logd("Room child list loading..... ", "Timer start...");
				handler.sendEmptyMessage(0);

			} // Set how long before to start calling the TimerTask (in milliseconds)
		},120000,// Set the amount of time between each execution (in milliseconds)
		 120000); // 120000 run after every 15 minutes 900000; 2 min
      }
	}
    
    public void stopTimers() {
    	if(timer != null) {
    	   timer.cancel();
    	   timer=null;
    	}
    	LogConfig.logd("Room child list loading..... ", "Timer stop...");
    }
    
    final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			callLiveChildCount(false); 
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimers();
	}
	
	@Override
	public void onStop() {
    	super.onStop();
    	stopTimers();
    }
    
    // call method to Educator child list screen
	public void callEducatorList(String strTitle, String id) {

		Common.ROOM_ID = id;
		Intent in = new Intent(getActivity(), EducatorChildListActivity.class);
		in.putExtra("Title", strTitle);
		startActivity(in);
		getActivity().overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);
	}

}
