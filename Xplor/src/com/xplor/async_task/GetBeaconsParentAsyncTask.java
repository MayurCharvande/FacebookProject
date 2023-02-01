package com.xplor.async_task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.estimote.sdk.Region;
import com.xplor.Model.MyBeaconsList;
import com.xplor.beacon.EstimoteManager;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.ServiceHandler;
import com.xplor.interfaces.BeaconServiceCallBack;

public class GetBeaconsParentAsyncTask extends AsyncTask<String, Integer, String> {

	Activity mActivity = null;
	ServiceHandler service = null;
	private BeaconServiceCallBack callBack = null;

	public GetBeaconsParentAsyncTask(Activity activity) {
		this.mActivity = activity;
		// create object web-service handler class.
		service = new ServiceHandler(mActivity);
	}

	public void setBeaconCallBack(BeaconServiceCallBack c) {
		callBack = c;
	}

	@Override
	protected String doInBackground(String... param) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("parent_id", Common.USER_ID));
		params.add(new BasicNameValuePair("device_type", Common.DEVICE_TYPE));
		String strResponce = service.makeServiceCall(WebUrls.PARENT_BEACON_DETAILS_URL, Common.POST, params);
		LogConfig.logd("Becons list strResponce =",""+strResponce);
		return strResponce;
	}

	@Override
	protected void onPostExecute(String strResponce) {
		super.onPostExecute(strResponce);
		parseBeaconDetail(strResponce);

	}

	private void parseBeaconDetail(String responce) {

		boolean success = false;
		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					if (resultObj.has("beacons_list")) {
						JSONArray beaconsArray = resultObj.getJSONArray("beacons_list");
						if (beaconsArray.length() > 0) {
							EstimoteManager.stop(Common.regionList);
							Common.regionList.clear();
							Common.beaconList.clear();
							SharedPreferences sPrefs = mActivity.getSharedPreferences(mActivity.getResources().getString(R.string.app_name), 0);
							SharedPreferences.Editor editor = sPrefs.edit();
							// first reset shared preference.
							editor.putString(Common.KEY_SHARED_PREF_BEACON_LIST, "");
							editor.commit();
							editor = sPrefs.edit();
							editor.putString(Common.KEY_SHARED_PREF_BEACON_LIST,responce);
							editor.commit();
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

							String regionId = beaconObj.center_id + ","+ beaconObj.center_name+ ","+major+ ","+minor; // id+name
							Region region_one = new Region(regionId, proxUID,major, minor);
							Common.regionList.add(region_one);
							Common.beaconList.add(beaconObj);
						}
						success = true;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		callBack.getBeacons(success);
	}
}
