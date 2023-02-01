package com.xplor.beacon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.Region;
import com.estimote.sdk.connection.BeaconConnection;
import com.xplor.Model.MyBeaconsList;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.dev.ChildAttendanceScreenActivity;
import com.xplor.dev.NotificationPopupActivity;
import com.xplor.dev.R;
import com.xplor.dev.SplashScreenActivity;
import com.xplor.interfaces.BeaconRegionCallBack;
import com.xplor.roastring.TodayShiftScreen;

@SuppressLint({ "ShowToast", "NewApi" })
@SuppressWarnings("unused")
public class EstimoteManager {
	
	private static final int NOTIFICATION_ID = 123;
	private static BeaconManager beaconManager;
	private static NotificationManager notificationManager;
	public static final String EXTRAS_BEACON = "extrasBeacon";
//  private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
//  private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
//  ESTIMOTE_PROXIMITY_UUID, null, null);
//	private static Region region_one = new Region("Fxbytes_HR_Department",
//			"B9407F30-F5F8-466E-AFF9-25556B57FE6D", 35994, 57871);// 41022,52006);//3544,																	// 10024);
//	private static Region region_two = new Region("Fxbytes_CEO_Office",
//			"B9407F30-F5F8-466E-AFF9-25556B57FE6D", 2480, 29349);
//	private static Region region_three = new Region("Fxbytes_Manager_Office",
//			"B9407F30-F5F8-466E-AFF9-25556B57FE6D", 56640, 5242);

	public static Integer bLevel;
    private static int beconsTime = 15;
	private static Context currentContext;
	private static ArrayList<Region> bregionList = new ArrayList<Region>();
	private static BeaconRegionCallBack mBeaconRegionCallBack = null;
	private static BeaconRegionCallBack mBeaconRegionCallBackAS = null;

	static BeaconConnection connection;

	public static void setBeaconRegionCallBack(BeaconRegionCallBack c) {
		mBeaconRegionCallBack = c;
	}

	public static void setBeaconRegionCallBackForAttandaceScreen(BeaconRegionCallBack c) {
		mBeaconRegionCallBackAS = c;
	}

	public static class batteryLevelofBeacon {
		public void setLvl(Integer lvl) {
			bLevel = lvl;
		}
	}

	private static BeaconConnection.ConnectionCallback createConnectionCallback() {
		return new BeaconConnection.ConnectionCallback() {

			@Override
			public void onAuthenticated(final BeaconConnection.BeaconCharacteristics chars) {
				new batteryLevelofBeacon().setLvl(chars.getBatteryPercent());
				connection.close();
				// MyProcess myJob = new MyProcess(beacon.getMajor(),
				// beacon.getMinor(), context, bLevel);
				// myJob.execute();
			}

			@Override
			public void onAuthenticationError() {
				Toast.makeText(currentContext,"OnBlutoothAuthenticationError()", 1000).show();
				// MyProcess myJob = new MyProcess(beacon.getMajor(),
				// beacon.getMinor(), context, null);
				// myJob.execute();
			}

			@Override
			public void onDisconnected() {
				// Log.v(TAG,"onDisconnected");
				Toast.makeText(currentContext, "OnBlutoothdisconnect()", 1000).show();
			}
		};
	}

	// Create everything we need to monitor the beacons
	public static void Create(NotificationManager notificationMngr,Context context, final Intent i) {
		try {

			notificationManager = notificationMngr;
			currentContext = context;
			bregionList.clear();

			SharedPreferences sPrefs = currentContext.getSharedPreferences(currentContext.getResources().getString(R.string.app_name),0);
			String beaconList = sPrefs.getString(Common.KEY_SHARED_PREF_BEACON_LIST, "");
			if (!beaconList.isEmpty()) {
				parseBeaconDetail(beaconList);
			}

			//long fiften_minutes_in_millis = 1 * 60 * 1000;
			long fiften_minutes_in_millis = 5000;
			// Create a beacon manager
			beaconManager = new BeaconManager(currentContext);
			// We want the beacons heart beat to be set at one second.
			beaconManager.setBackgroundScanPeriod(fiften_minutes_in_millis, 0);
			beaconManager.setForegroundScanPeriod(fiften_minutes_in_millis, 0);
			// connection = new BeaconConnection(context, beacon,
			// createConnectionCallback());
			// connection.authenticate();
			// Method called when a beacon gets...
			beaconManager.setMonitoringListener(new MonitoringListener() {
				@Override
				public void onEnteredRegion(Region region, List<Beacon> beacons) {
					Common.EDUCATOR_ENTER_RANGE = true;
					boolean showEnterRegionMessage = false;
					SharedPreferences sPrefs = currentContext.getSharedPreferences(currentContext.getResources().getString(R.string.app_name), 0);
					if (sPrefs.getString(Common.KEY_SHARED_PREF_REGION_ENTER_TIME, "").isEmpty()) {
						// first reset shared preference.
						SharedPreferences.Editor editor = sPrefs.edit();
						editor.putString(Common.KEY_SHARED_PREF_REGION_ENTER_TIME,Calendar.getInstance().getTimeInMillis() + "");
						editor.commit();
						showEnterRegionMessage = true;
					} else {
						long lastEnterTime = Long.parseLong(sPrefs.getString(Common.KEY_SHARED_PREF_REGION_ENTER_TIME, ""));
						if (calculateTimeDifference(lastEnterTime) >= beconsTime) {
							// first reset shared preference.
							SharedPreferences.Editor editor = sPrefs.edit();
							editor.putString(Common.KEY_SHARED_PREF_REGION_ENTER_TIME,Calendar.getInstance().getTimeInMillis()+ "");
							editor.commit();
							showEnterRegionMessage = true;
						}
					}

					Common.enteredRegionList.add(region.getIdentifier()); 
					if (mBeaconRegionCallBack != null)
						mBeaconRegionCallBack.enterIntoBeaconRegion(true);
					if (mBeaconRegionCallBackAS != null)
						mBeaconRegionCallBackAS.enterIntoBeaconRegion(true);
					if (showEnterRegionMessage) {
						String[] centerName = region.getIdentifier().split(",");
					   if(Common.USER_TYPE.equals("2")) {
						  postNotificationIntent(currentContext.getString(R.string.app_name),"Welcome to " + centerName[0]+ ". Please Sign in/out.");
						  //postNotificationIntent(currentContext.getString(R.string.app_name),"Welcome to " + centerName[0]+ ". Please Sign in/out."+"\n Major: "+region.getMajor()+"- Minor: "+region.getMinor());
					   } else if(!Common.SHIFT_STARTED && Common.USER_TYPE.equals("1")) {
						   postNotificationIntent(currentContext.getString(R.string.app_name),"Welcome to " + centerName[0]+ ".");
						   //postNotificationIntent(currentContext.getString(R.string.app_name),"Welcome to " + centerName[0]+ "."+"\n Major: "+region.getMajor()+"- Minor: "+region.getMinor());
					   }
					}
					
					if(!Common.SHIFT_STARTED && Common.USER_TYPE.equals("1") && !Common.REFRESH_SHIFT_LIST) {
						
						try {
						  Common.REFRESH_SHIFT_LIST = true;
						  ((TodayShiftScreen)currentContext).beaconsRangeToRefreshList();
						} catch(ClassCastException e) {
							//e.printStackTrace();
						}
					}
				}

				// ... far away from us.
				@Override
				public void onExitedRegion(Region region) {
					Common.enteredRegionList.remove(region.getIdentifier());
					String[] centerName = region.getIdentifier().split(",");
					postNotificationForExitRegion(currentContext.getString(R.string.app_name),"You have left " + centerName[0] + ".");
					Common.EDUCATOR_ENTER_RANGE = false;
					Common.REFRESH_SHIFT_LIST = false;
					if (mBeaconRegionCallBack != null)
						mBeaconRegionCallBack.enterIntoBeaconRegion(false);
					if (mBeaconRegionCallBackAS != null)
						mBeaconRegionCallBackAS.enterIntoBeaconRegion(false);
				}
			});

			// Connect to the beacon manager...
			beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
				@Override
				public void onServiceReady() {
					try {
						// ... and start the monitoring
						for (Region region : bregionList) {
							beaconManager.startMonitoring(region);
							break;
						}
					} catch (Exception e) {
						Log.d("Estimode ", "Error while starting monitoring "+ e.toString());
					}
				}
			});
		} catch (Exception e) {
			Log.d("Estimode ","Error while starting monitoring " + e.toString());
		}
	}

	public static long calculateTimeDifference(long enterTime) {

		long diff = Calendar.getInstance().getTimeInMillis() - enterTime;
		long seconds = diff / 1000;
		long minutes = seconds / 60;

		return minutes;
	}

	// Pops a notification in the task bar
	public static void postNotificationIntent(String title, String msg) {

		SharedPreferences sPrefs = currentContext.getSharedPreferences(
				currentContext.getResources().getString(R.string.app_name), 0);
		SharedPreferences.Editor editor = sPrefs.edit();

		// first reset shared preference.
		editor.putBoolean(Common.KEY_SHARED_PREF_HAS_NOTIFICATION, true);
		editor.commit();
		Intent i;

		if ((Common.regionList == null) || (Common.regionList.size() <= 0)) {
			i = new Intent(currentContext, SplashScreenActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivities(
					currentContext, 0, new Intent[] { i },
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification notification = new Notification.Builder(currentContext)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(title).setContentText(msg)
					.setAutoCancel(true).setContentIntent(pendingIntent)
					.build();
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notificationManager.notify(NOTIFICATION_ID, notification);
		} else {
			LogConfig.logd("postNotificationIntent =", ""+isApplicationSentToBackground(currentContext));
			if (isApplicationSentToBackground(currentContext) && Common.USER_TYPE.equals("2")) {
				i = new Intent(currentContext, ChildAttendanceScreenActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				i.putExtra("isEnterRegion", true);
				PendingIntent pendingIntent = PendingIntent.getActivities(currentContext, 0, new Intent[] { i },
						PendingIntent.FLAG_UPDATE_CURRENT);
				Notification notification = new Notification.Builder(
						currentContext).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title).setContentText(msg)
						.setAutoCancel(true).setContentIntent(pendingIntent)
						.build();
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.defaults |= Notification.DEFAULT_LIGHTS;
				notificationManager.notify(NOTIFICATION_ID, notification);
			} else {
				i = new Intent(currentContext, NotificationPopupActivity.class);
				i.putExtra("isEnterRegion", true);
				i.putExtra("regionMsg", msg);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				currentContext.startActivity(i);
			}
		}

	}

	public static void postNotificationForExitRegion(String title, String msg) {

		SharedPreferences sPrefs = currentContext.getSharedPreferences(currentContext.getResources().getString(R.string.app_name), 0);
		SharedPreferences.Editor editor = sPrefs.edit();

		// first reset shared preference.
		editor.putBoolean(Common.KEY_SHARED_PREF_HAS_NOTIFICATION, true);
		editor.commit();
		Intent i;

		if ((Common.regionList == null) || (Common.regionList.size() <= 0)) {
			i = new Intent(currentContext, SplashScreenActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivities(
					currentContext, 0, new Intent[] { i },
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification notification = new Notification.Builder(currentContext)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(title).setContentText(msg)
					.setAutoCancel(true).setContentIntent(pendingIntent)
					.build();
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notificationManager.notify(NOTIFICATION_ID, notification);
		} else {
			if (isApplicationSentToBackground(currentContext) && Common.USER_TYPE.equals("2")) {
				i = new Intent(currentContext, ChildAttendanceScreenActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				i.putExtra("isEnterRegion", false);
				PendingIntent pendingIntent = PendingIntent.getActivities(currentContext, 0,
						new Intent[] { i },PendingIntent.FLAG_UPDATE_CURRENT);
				Notification notification = new Notification.Builder(
						currentContext).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title).setContentText(msg)
						.setAutoCancel(true).setContentIntent(pendingIntent)
						.build();
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.defaults |= Notification.DEFAULT_LIGHTS;
				notificationManager.notify(NOTIFICATION_ID, notification);
			} else {
				i = new Intent(currentContext, NotificationPopupActivity.class);
				i.putExtra("isEnterRegion", false);
				i.putExtra("regionMsg", msg);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				currentContext.startActivity(i);
			}
		}
	}

	// Stop beacons monitoring, and closes the service
	public static void stop() {
		try {
			beaconManager.disconnect();
		} catch (Exception e) {
		}
	}

	public static void stop(ArrayList<Region> regions) {
		try {
		
			for (Region region : regions) {
				beaconManager.stopMonitoring(region);
			}
			beaconManager.disconnect();
		} catch (Exception e) {
		}
	}

	private static void parseBeaconDetail(String responce) {

		// {"result":
		// {"status":"success",
		// "beacons_list":
		// [{"beacon_name":"ice_1","mac_address":"cdfg-123","secure_UUID":"0","proximity_UUID":"B9407F30-F5F8-466E-AFF9-25556B57FE6D",
		// "major":"35994","minor":"57871","broadcasting_power":"Weak-(-12BDM)","advertising_interval":"950ms",
		// "battery_life":"36 month","basic_battery":"0","smart_battery":"0","firmware":"B2.1","hardware":"D3.4",
		// "elc_name":"Kidzee","center_id":"KidZee Indraprasht raipur","center_name":"42"},

		// {"beacon_name":"blueberry3","mac_address":"D1:5D:14:7A:DD:40","secure_UUID":"0","proximity_UUID":"B9407F30-F5F8-466E-AFF9-25556B57FE6D",
		// "major":"56640","minor":"5242","broadcasting_power":"","advertising_interval":"",
		// "battery_life":"","basic_battery":"0","smart_battery":"0","firmware":"","hardware":"",
		// "elc_name":"Kidzee","center_id":"KidZee Indraprasht raipur","center_name":"42"},

		// {"beacon_name":"Mint -123","mac_address":"C0:8F:72:A5:09:B0","secure_UUID":"0","proximity_UUID":"B9407F30-F5F8-466E-AFF9-25556B57FE6D",
		// "major":"2480","minor":"29349","broadcasting_power":"Weak (-12 dBm)","advertising_interval":"950 ms",
		// "battery_life":"35 months","basic_battery":"0","smart_battery":"0","firmware":"A2.1","hardware":"D3.4",
		// "elc_name":"Kidzee","center_id":"KidZee Indraprasht raipur","center_name":"42"}]}}

		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					if (resultObj.has("beacons_list")) {
						JSONArray beaconsArray = resultObj.getJSONArray("beacons_list");
						if (beaconsArray.length() > 0) {
							// Common.regionList.clear();
							bregionList.clear();
							Common.beaconList.clear();
						}
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

							String regionId = beaconObj.center_id + ","
									+ beaconObj.center_name + "," + major + ","
									+ minor;

							Region region_one = new Region(regionId, proxUID,major, minor);
							// Common.regionList.add(region_one);
							bregionList.add(region_one);
							Common.beaconList.add(beaconObj);
						}
					}
				}
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}

	}

	public static boolean isApplicationSentToBackground(final Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

}
