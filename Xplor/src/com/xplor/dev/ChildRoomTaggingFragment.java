package com.xplor.dev;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xplor.Model.ParentVariable;
import com.xplor.adaptor.EducatorRoomTaggingAdapter;
import com.xplor.adaptor.GridViewWithHeaderAndFooter;
import com.xplor.async_task.ChildLiveCountAsyncTask;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;
import com.xplor.helper.Adapter;
import com.xplor.interfaces.CallBackRoomTaggingItem;
import com.xplor.interfaces.RoomChildCountCallBack;
import com.xplor.local.syncing.download.SqlQuery;

@SuppressLint("HandlerLeak") 
public class ChildRoomTaggingFragment extends Fragment implements RoomChildCountCallBack,CallBackRoomTaggingItem {
 
	private View convertView =null;
	private Activity mActivity =null;
	private TextView txtChildCount;
	private int totalCount = 0;
	private EducatorRoomTaggingAdapter mEducatorRoomAdapter = null;
	private GridViewWithHeaderAndFooter gridView = null;
	private ArrayList<ParentVariable> roomArrayCount = null;
	private ArrayList<ParentVariable> roomArray = null;
	private Timer timer = null;
	private Validation mValidation = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(convertView == null) {
		  convertView = inflater.inflate(R.layout.educator_dynamic_room_selection, container, false);
		  mActivity = getActivity();
		  CreateViews();
		}
		return convertView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//call method to get child rooms with check-in/out count
		callLiveChildCount(true);
		startTimers();
		mActivity = getActivity();
		ChildTagsMainActivity.ChildTagCancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Common.hideKeybord(v, getActivity());
				if(Common.TAG_CHILD_NAME_LIST != null && Common.TAG_CHILD_NAME_LIST.size() == 0 
						&& Common.arrTEMP_TAG_CHILD_ID != null)
				   Common.arrTEMP_TAG_CHILD_ID.clear();
				getActivity().finish();
				getActivity().overridePendingTransition(R.anim.slide_down_in,R.anim.slide_down_out);
			}
		});
		if(Common.arrTEMP_TAG_CHILD_ID != null && Common.arrTEMP_TAG_CHILD_ID.size() > 0) {
		   ChildTagsMainActivity.ChildTagTitleTxt.setText(Common.arrTEMP_TAG_CHILD_ID.size()+" Selected");
		} else {
			ChildTagsMainActivity.ChildTagTitleTxt.setText("Child by Room");
		}
		
		ChildTagsMainActivity.ChildTagDoneBtn.setVisibility(View.INVISIBLE);
	}

	private void CreateViews() {
		
		// Find all using views
		mValidation = new Validation(getActivity());
	    roomArrayCount = new ArrayList<ParentVariable>();
	  	roomArray = new ArrayList<ParentVariable>();
		gridView = (GridViewWithHeaderAndFooter) convertView.findViewById(R.id.educator_room_grid_view);
		txtChildCount = (TextView) convertView.findViewById(R.id.Room_TotalChild_Checked_Txt);
	}
	
	// Child check in count check
	public void callLiveChildCount(Boolean _isLoading) {
		getEducatorRoom();
		if(mValidation.checkNetworkRechability()) {
		   ChildLiveCountAsyncTask mChildLiveCountAsyncTask = new ChildLiveCountAsyncTask(mActivity,_isLoading);
		   mChildLiveCountAsyncTask.setCheckStatusCallBack(ChildRoomTaggingFragment.this);
		   mChildLiveCountAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    }
	}
	
	@Override
	public void roomChildCountCallBack(String responce) {
		
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
						txtChildCount.setText(totalCount + "No Child Checked-in");
					else if (totalCount == 1)
						txtChildCount.setText(totalCount + " Child Checked-in");
					else
						txtChildCount.setText(totalCount+ " Children Checked-in");
				}
			}
			
			if(totalCount == 0)
				displayAlertTotalChecked_in(mActivity, 
				mActivity.getResources().getString(R.string.str_Message), 
			    mActivity.getResources().getString(R.string.no_child_checked_in_any_room));
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
			
		    mEducatorRoomAdapter = new EducatorRoomTaggingAdapter(getActivity(), roomArray);
		    mEducatorRoomAdapter.setCallBack(ChildRoomTaggingFragment.this);
		    gridView.setAdapter(mEducatorRoomAdapter);
		
	}
    
    // Get rooms checkinout record from local data base
    private void getChildCheckedIn() {
 	   
     	Common.getCurrentDateToStartEndDate();
     	Adapter mDBHelper = new Adapter(mActivity);
 		mDBHelper.createDatabase();
 		mDBHelper.open();
 		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getRoomCheckinCount(Common.STR_CURENT_START_DATE,Common.STR_CURENT_END_DATE, Common.CENTER_ID));
 		
 		  try { 
 			  if(mCursor.getCount()>0) {
 			    roomArrayCount.clear();
 			    totalCount = 0;
 			    if(mCursor.moveToFirst()) {
 					do{ //room_id, room_name
 						
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
    
    @Override
	public void requestRoomTaggingItem(String name, String message) {
    	callChildTagList(name, message);
	}
    
    public void displayAlertTotalChecked_in(Context mContext, String strTitle,String strSMS) {

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

			TextView txtSms = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSms.setText(strSMS);

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					ChildTagsMainActivity.btnAllChild.setBackgroundResource(R.drawable.allchild_selected);
					ChildTagsMainActivity.btnByRoom.setBackgroundResource(R.drawable.byroom_unselected);
					callChildTagList("All Child", "");
				}
			});

			if (!dialog.isShowing())
				dialog.show();
		}
	}
    
    public void callChildTagList(String strTitle,String type) {
	     
		ChildTagsMainActivity.ChildTagTitleTxt.setText("All Child");
		Bundle mData = new Bundle();
		Fragment mFragment = new ChildPostTagsFragment();
		mData.putString("Title", strTitle);
		mData.putString("Type", type);
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();

		if (getFragmentManager().getBackStackEntryCount() > 0) {
			Common.clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		// Adding a fragment to the fragment transaction
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
      
	}
    
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
    	   timer = null;
    	}
    	   LogConfig.logd("Room child list loading..... ", "Timer stop...");
    }
    
    final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			callLiveChildCount(false); 
		}
	};

}
