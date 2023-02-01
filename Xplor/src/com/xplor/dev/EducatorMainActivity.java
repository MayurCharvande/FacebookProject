package com.xplor.dev;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.BeaconManager;
import com.xplor.async_task.CancelShiftNotificationAsyncTask;
import com.xplor.async_task.ContinueShiftNotificationAsyncTask;
import com.xplor.async_task.GetBeaconsEducatorAsyncTask;
import com.xplor.async_task.LogoutServiceSyncTask;
import com.xplor.async_task.Present_ShiftsAsyncTask;
import com.xplor.beacon.EstimoteManager;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.interfaces.BeaconRegionCallBack;
import com.xplor.interfaces.BeaconServiceCallBack;
import com.xplor.local.syncing.upload.BackgroungSyncing;
import com.xplor.roastring.RosterFragment;

public class EducatorMainActivity extends Activity implements OnClickListener,BeaconServiceCallBack,BeaconRegionCallBack {

	public static final int REQUEST_ENABLE_BT = 1234;
	public static ProgressBar headerProgressBar = null;
	private BeaconManager beaconManager;
	private BackgroungSyncing mBackgroungSyncing =null;
	private UpdateMenuListReciver mUpdateMenuListReciver = null;
	private ImageButton EducatorSettingBtn = null;
	private ImageButton EducatorRoomBtn = null;
	private ImageButton EducatorClockBtn = null;
	private TextView EducatorTitleTxt = null;
	private Button EducatorLogoutBtn = null;
	private Fragment mFragment = null;
	private Bundle mData = null;
	private Validation validation = null;
	private Activity mActivity = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.educator_main);
		mActivity = EducatorMainActivity.this;
		CreateViews();
		
		int apiLevel = android.os.Build.VERSION.SDK_INT;
		if (apiLevel > 17) {
			beaconManager = new BeaconManager(this);
			// MyBeacon.getInstatnce().setBeaconManager(this);
			GetBeaconsEducatorAsyncTask mGetBeaconsAsyncTask = new GetBeaconsEducatorAsyncTask(this);
			mGetBeaconsAsyncTask.setBeaconCallBack(this);
			mGetBeaconsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
		}
		
		mBackgroungSyncing = new BackgroungSyncing(mActivity);
        mBackgroungSyncing.startBGSyncing();
        
        mUpdateMenuListReciver = new UpdateMenuListReciver();
		mActivity.registerReceiver(mUpdateMenuListReciver, new IntentFilter("updateChildList"));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sPrefs = getSharedPreferences(getResources().getString(R.string.app_name), 0);
		validation = new Validation(EducatorMainActivity.this);
		if(Common.NOTIFICATION_ID != null && Common.NOTIFICATION_ID.length() > 0 && Common._isNOTIFICATION_FORGROUND) {
			String strType = sPrefs.getString("type", "");
			if(strType.equals("comment")) {
			   Common._isNOTIFICATION_FORGROUND = false;
			   Common.FEED_ID = sPrefs.getString("news_feed_id", "");
			   Editor editor = sPrefs.edit();
			   editor.putString("type", "");
			   editor.putString("news_feed_id", "");
			   editor.commit();
			   Intent mIntent1 = new Intent(EducatorMainActivity.this,CommentScreenActivity.class);
			   startActivity(mIntent1);
			   EducatorMainActivity.this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out); 
			} else if(strType.equals("cancel")) {
				if(validation.checkNetworkRechability()) {
			      String shift_id = sPrefs.getString("shift_id", "");
			      CancelShiftNotificationAsyncTask mCancelShiftAsyncTask = new CancelShiftNotificationAsyncTask(EducatorMainActivity.this);
				  mCancelShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, shift_id);
//				} else {
//				  Common.displayAlertSingle(EducatorMainActivity.this,"Message", getResources().getString(R.string.no_internet));
				}
			} else {
				if(validation.checkNetworkRechability()) {
				  String shift_id = sPrefs.getString("shift_id", "");
				  ContinueShiftNotificationAsyncTask mCancelShiftAsyncTask = new ContinueShiftNotificationAsyncTask(EducatorMainActivity.this,shift_id);
				  mCancelShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				} else {
//				  Common.displayAlertSingle(EducatorMainActivity.this,"Message", getResources().getString(R.string.no_internet));
				}
			}
		}
		
		if(validation.checkNetworkRechability()) {
		  Present_ShiftsAsyncTask mPresent_ShiftsAsyncTask = new Present_ShiftsAsyncTask(this);
		  mPresent_ShiftsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
//		} else {
//		  Common.displayAlertSingle(EducatorMainActivity.this,"Message", getResources().getString(R.string.no_internet));
		}
	}

	private void CreateViews() {

		EducatorTitleTxt = (TextView) findViewById(R.id.Educator_Title_Txt);
		EducatorSettingBtn = (ImageButton) findViewById(R.id.Educator_Setting_Btn);
		EducatorSettingBtn.setOnClickListener(this);

		EducatorRoomBtn = (ImageButton) findViewById(R.id.Educator_Room_Btn);
		EducatorRoomBtn.setOnClickListener(this);
		EducatorRoomBtn.setImageResource(R.drawable.room_selection_blue);

		EducatorClockBtn = (ImageButton) findViewById(R.id.Educator_Clock_Btn);
		EducatorClockBtn.setOnClickListener(this);
		EducatorClockBtn.setImageResource(R.drawable.clock_grey);

		EducatorLogoutBtn = (Button) findViewById(R.id.Educator_Logout_Btn);
		EducatorLogoutBtn.setOnClickListener(this);
		
		headerProgressBar = (ProgressBar) findViewById(R.id.Header_ProgressBar);
		headerProgressBar.setVisibility(View.GONE);

		validation = new Validation(EducatorMainActivity.this);
		mData = new Bundle();
		callRoomScreen();
	}
	
	// background list refresh list
    private class UpdateMenuListReciver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context mContext, Intent intent) {
    	  new Handler().post(new Runnable() {
			@Override
			public void run() {
				if(EducatorMainActivity.headerProgressBar != null && validation.checkNetworkRechability()) {
				   EducatorMainActivity.headerProgressBar.setVisibility(View.VISIBLE);
				}
			}
	   });
     }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Educator_Logout_Btn:
			btnClick_Logout();
			break;
		case R.id.Educator_Setting_Btn:
			Common.USER_TYPE = "1";
			Common.VIEW_ONLY = false;
			callSettingMethod();
			break;
		case R.id.Educator_Room_Btn:
			callRoomScreen();
			break;
		case R.id.Educator_Clock_Btn:
			callRosterScreen();
			break;
		default:
			break;
		}
	}

	public void btnClick_Logout() {

		if (!validation.checkNetworkRechability()) {
			Toast.makeText(mActivity, mActivity.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
		} else {
			// execute logout service
			LogoutServiceSyncTask mLogoutServiceSyncTask = new LogoutServiceSyncTask(EducatorMainActivity.this); 
			mLogoutServiceSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
		}
	}

	private void callSettingMethod() {

		Intent mIntent = new Intent(EducatorMainActivity.this, SettingsScreenActivity.class);
		startActivity(mIntent);
		this.overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
	}

	public void callRoomScreen() {
		
		EducatorTitleTxt.setText("Room Select");
		EducatorRoomBtn.setImageResource(R.drawable.room_selection_blue);
		EducatorClockBtn.setImageResource(R.drawable.clock_grey);

		mFragment = new EducatorRoomSelectionFragment();
		mData.putString("Title", "Room Select");
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		// Adding a fragment to the fragment transaction
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
	
	}

	public void callRosterScreen() {
		
		EducatorTitleTxt.setText("Roster");
		EducatorRoomBtn.setImageResource(R.drawable.room_selection_gray);
		EducatorClockBtn.setImageResource(R.drawable.clock_blue);

		mFragment = new RosterFragment();
		mData.putString("Title", "Roster");
		mFragment.setArguments(mData);
		// Getting reference to the Fragment Manager
		FragmentManager fragmentManager = getFragmentManager();

		if (getFragmentManager().getBackStackEntryCount() > 0) {
			clearBackStack(fragmentManager);
		}
		// Creating a fragment transaction
		FragmentTransaction fragmentTranjection = fragmentManager.beginTransaction();
		// fragmentTranjection.setCustomAnimations(R.animator.frg_slide_for_in,
		// R.animator.frg_slide_for_out);
		// Adding a fragment to the fragment transaction
		fragmentTranjection.replace(R.id.content_frame, mFragment).commit();
		// switchContent(mFragment);
	}

	public static void clearBackStack(FragmentManager manager) {
		int rootFragment = manager.getBackStackEntryAt(0).getId();
		manager.popBackStack(rootFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	GlobalApplication.onActivityForground(EducatorMainActivity.this);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	GlobalApplication.onActivityForground(EducatorMainActivity.this);
    }
	
	@Override
	public void onBackPressed() {
	  // super.onBackPressed();
	   Common.isDisplayMessage_Called = false;
	   GlobalApplication.onActivityForground(EducatorMainActivity.this);
	   Common.displayAlertBackFinish(EducatorMainActivity.this, 
				getResources().getString(R.string.exit_app_message), true);

	}

	@Override
	public void getBeacons(boolean success) {
		
		if (success) {
			// If Blue tooth is not enabled, let user enable it.
			try {
			  if (!beaconManager.isBluetoothEnabled()) {
				 Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			 	 startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			   } else {
				 alaramCallMethods();
			   }
			} catch(NoClassDefFoundError e) {
				//e.printStackTrace();
			} catch(Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void alaramCallMethods() {

		int apiLevel = android.os.Build.VERSION.SDK_INT;
		if (apiLevel > 17) {
			startService(new Intent(this,com.xplor.beacon.EstimoteService.class));
			EstimoteManager.Create((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),this,
			new Intent(EducatorMainActivity.this, EducatorMainActivity.class));
			EstimoteManager.setBeaconRegionCallBack(this);
			Intent startServiceIntent = new Intent(this, EstimoteManager.class);
			this.startService(startServiceIntent);
		}
	}

	@Override
	public void enterIntoBeaconRegion(boolean isEnter) {
		
		if(isEnter) {
			Common.EDUCATOR_ENTER_RANGE = true;
		} else {
			Common.EDUCATOR_ENTER_RANGE = false;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBackgroungSyncing != null)
		   mBackgroungSyncing.stopBGSyncing();
		
		    try { // this method is broadcast receiver class refresh 
			  //time line refresh according to downloading and uploading timer
			  mActivity.unregisterReceiver(mUpdateMenuListReciver);
			} catch(IllegalArgumentException e) {
			  // value argument exception
			}
	}

}
