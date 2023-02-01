package com.xplor.dev;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.estimote.sdk.Region;
import com.fx.myimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.fx.myimageloader.core.ImageLoader;
import com.fx.myimageloader.core.ImageLoaderConfiguration;
import com.fx.myimageloader.core.assist.QueueProcessingType;
import com.xplor.Model.MyBeacon;
import com.xplor.Model.MyBeaconsList;
import com.xplor.async_task.DeviceRegistrationIdSyncTask;
import com.xplor.async_task.LearningOutSyncTask;
import com.xplor.common.Common;
import com.xplor.common.GPSTracker;
import com.xplor.common.LogConfig;
import com.xplor.common.Validation;

public class SplashScreenActivity extends Activity {

	private static final int ANIM_TIME = 100;
	private Validation validation = null;
	private SharedPreferences sPrefs = null;
	private Activity mActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		mActivity = SplashScreenActivity.this;
		Common.bolCallMethod = true;
		Common.isDisplayMessage_Called = false;
		
		sPrefs = getSharedPreferences(getResources().getString(R.string.app_name), 0);
		String beaconList = sPrefs.getString(Common.KEY_SHARED_PREF_BEACON_LIST, "");
		if (!beaconList.isEmpty()) {
			parseBeaconDetail(beaconList);
		}
		
		initImageLoader(SplashScreenActivity.this);
		Common.NOTIFICATION_ID = sPrefs.getString("child_id", "");
         if(Common.NOTIFICATION_ID.length() > 0) {
        	   Common.CHILD_ID = sPrefs.getString("child_id", "");
			   Common.CENTER_ID = sPrefs.getString("center_id", "");
			   Common.ROOM_ID = sPrefs.getString("room_id", "");
			   Common.FEED_ID = sPrefs.getString("news_feed_id", "");
         }
         validation = new Validation(SplashScreenActivity.this);
         String strVersion = sPrefs.getString("crearte_version", "");
         if(strVersion.isEmpty()) {
        	Editor mEditor = sPrefs.edit();
        	mEditor.putString("crearte_version", "1.0");
        	mEditor.commit();
         }
         
         if(validation.checkNetworkRechability()) {
 			try {
 			  GPSTracker mGPSTracker = new GPSTracker(mActivity);
 			  Common.LATITUTE = mGPSTracker.getLocation().getLatitude();
 			  Common.LONGITUTE = mGPSTracker.getLocation().getLongitude();
 			  LogConfig.logd("Current Loaction =", " latitute ="+Common.LATITUTE+" longitute ="+Common.LONGITUTE);
 			} catch(NullPointerException e) {
 				Common.LATITUTE = 0.0;
 				Common.LONGITUTE = 0.0;
 				//e.printStackTrace();
 			}
 			DeviceRegistrationIdSyncTask mDeviceRegistrationIdSyncTask = new DeviceRegistrationIdSyncTask(mActivity);
 			mDeviceRegistrationIdSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
 		}
         
		try {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					getDeviceTokan();
					if (validation.checkNetworkRechability()) {
						LearningOutSyncTask mLearningOutSyncTask = new LearningOutSyncTask(SplashScreenActivity.this);
						mLearningOutSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						displayDialogSingleButton(SplashScreenActivity.this, getResources().getString(R.string.no_internet));
					}

				}
			}, ANIM_TIME);
		} catch (Exception e) {}
	}

	// Show alert data submit successfully
	public void displayDialogSingleButton(Context mContext, String strSMS) {

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
			TextView txtMessage = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtMessage.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
					finish();
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		getDeviceTokan();
	}
	
	private void getDeviceTokan() {
		
		// get SharedPreferences email and password
		Context context = SplashScreenActivity.this;
		GlobalApplication.onActivityForground(SplashScreenActivity.this);
		Common.DEVICE_TOKEN = sPrefs.getString("DEVICE_TOKEN", "");
		if (Common.DEVICE_TOKEN == null || Common.DEVICE_TOKEN.length() == 0) {
			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			registrationIntent.setPackage("com.google.android.gsf");
			registrationIntent.putExtra("app",PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender",getResources().getString(R.string.app_gcm_project_id));
			context.startService(registrationIntent);
		} // Register for push notifications
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		GlobalApplication.onActivityForground(SplashScreenActivity.this);
	}

	private void parseBeaconDetail(String responce) {

		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					if (resultObj.has("beacons_list")) {
						JSONArray beaconsArray = resultObj.getJSONArray("beacons_list");

						for (int i = 0; i < beaconsArray.length(); i++) {

							MyBeaconsList beaconObj = new MyBeaconsList();
							String proxUID = beaconsArray.getJSONObject(i).getString("proximity_UUID");
							beaconObj.proximity_UUID = proxUID;
							beaconObj.beacon_name = beaconsArray.getJSONObject(i).getString("beacon_name");
							String macAddress = beaconsArray.getJSONObject(i).getString("mac_address");
							beaconObj.mac_address = macAddress;

							int major = beaconsArray.getJSONObject(i).getInt("major");
							beaconObj.major = major;
							int minor = beaconsArray.getJSONObject(i).getInt("minor");
							beaconObj.minor = minor;
							beaconObj.secure_UUID = beaconsArray.getJSONObject(i).getString("secure_UUID");
							beaconObj.broadcasting_power = beaconsArray.getJSONObject(i).getString("broadcasting_power");
							beaconObj.advertising_interval = beaconsArray.getJSONObject(i).getString("advertising_interval");
							beaconObj.battery_life = beaconsArray.getJSONObject(i).getString("battery_life");
							beaconObj.basic_battery = beaconsArray.getJSONObject(i).getString("basic_battery");
							beaconObj.smart_battery = beaconsArray.getJSONObject(i).getString("smart_battery");
							beaconObj.firmware = beaconsArray.getJSONObject(i).getString("firmware");
							beaconObj.hardware = beaconsArray.getJSONObject(i).getString("hardware");
							beaconObj.elc_name = beaconsArray.getJSONObject(i).getString("elc_name");
							beaconObj.center_id = beaconsArray.getJSONObject(i).getString("center_id");
							beaconObj.center_name = beaconsArray.getJSONObject(i).getString("center_name");

							String regionId = beaconObj.center_id + ","+ beaconObj.center_name;
							Region region_one = new Region(regionId, proxUID,major, minor);
							Common.regionList.add(region_one);
							Common.beaconList.add(beaconObj);
						}

					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				// .threadPoolSize(5)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)

				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	/*
	 * First stop all beacon monitoring of all regions if monitoring in running.
	 */
	@SuppressWarnings("unused")
	private void stopBeaconMonitoring() {
		for (Region region : Common.regionList) {
			try {
				MyBeacon.getInstatnce().getBeaconManager().stopMonitoring(region);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
