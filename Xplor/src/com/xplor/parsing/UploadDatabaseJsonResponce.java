package com.xplor.parsing;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xplor.helper.DataBaseHelper;

import android.content.Context;
import android.util.Log;

public class UploadDatabaseJsonResponce {

	// JSON Node names
	public static final String TAG_STATUS = "status";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_DATA = "data";
	public static final String TAG_MAIN = "main";
	public static final String TAG_REFERENCE = "reference";
	public static final String TAG_UPDATE = "update";

	public static final String TAG_MOBILE_KEY = "mobile_key";
	public static final String TAG_VALUE = "value";
	public static final String TAG_PRIMARY_KEY = "primary_key";
	public static final String TAG_REFRENCE_KEY = "reference_key";
	String _jsonstr;
	DataBaseHelper mDataBaseHelper;

	public UploadDatabaseJsonResponce(Context context, String jsonStr) {
		_jsonstr = jsonStr;
		mDataBaseHelper = new DataBaseHelper(context);
	}

	@SuppressWarnings("unchecked")
	public void JsonResponce() {

		if (_jsonstr != null) {
			try {
				JSONObject jsonObj = new JSONObject(_jsonstr);

				if (jsonObj.getString(TAG_STATUS).equals("true")) {
					JSONObject jsonObjData = jsonObj.getJSONObject(TAG_DATA);

					if (jsonObjData.has(TAG_MAIN)) {

						JSONArray mainJsonArray = jsonObjData.getJSONArray(TAG_MAIN);
						for (int i = 0; i < mainJsonArray.length(); i++) {
							JSONObject tempObj = mainJsonArray.getJSONObject(i);
							String tableName = "";
							Iterator<String> iter = tempObj.keys();
							while (iter.hasNext()) {
								tableName = iter.next();

								JSONArray tableInfoArray = tempObj.getJSONArray(tableName);

								for (int j = 0; j < tableInfoArray.length(); j++) {
									JSONObject tableInfoObj = tableInfoArray.getJSONObject(j);
									String value = tableInfoObj.getString(TAG_VALUE);
									String mobileKey = tableInfoObj.getString(TAG_MOBILE_KEY);
									String primeryKey = tableInfoObj.getString(TAG_PRIMARY_KEY);

									mDataBaseHelper.updateTablesByUploadResponce(tableName, mobileKey,primeryKey, value);
									Log.e("Json", "Main: TableName: "+ tableName + ", value: " + value
											+ ", mobileKey: " + mobileKey
											+ ", primeryKey: " + primeryKey);
								}

							}
						}
					}

					if (jsonObjData.has(TAG_REFERENCE)) {
						JSONArray refJsonArray = jsonObjData.getJSONArray(TAG_REFERENCE);
						for (int i = 0; i < refJsonArray.length(); i++) {
							JSONObject tempObj = refJsonArray.getJSONObject(i);
							String tableName = "";
							Iterator<String> iter = tempObj.keys();
							while (iter.hasNext()) {
								tableName = iter.next();
								JSONArray tableInfoArray = tempObj.getJSONArray(tableName);

								for (int j = 0; j < tableInfoArray.length(); j++) {
									JSONObject tableInfoObj = tableInfoArray.getJSONObject(j);

									String value = tableInfoObj.getString(TAG_VALUE);
									String mobileKey = tableInfoObj.getString(TAG_MOBILE_KEY);
									String reference_key = tableInfoObj.getString(TAG_REFRENCE_KEY);

									if (tableInfoObj.get(TAG_VALUE).getClass().toString().contains("String")) {
										mDataBaseHelper.updateTablesByUploadResponceRefrancesString(
														tableName, mobileKey,reference_key, value);
									} else {
										mDataBaseHelper.updateTablesByUploadResponceRefrancesLong(
														tableName, mobileKey,reference_key, value);
									}

									Log.e("Json", "Refrence:  TableName: "
											+ tableName + ", value: " + value
											+ ", mobileKey: " + mobileKey
											+ ", reference_key: "
											+ reference_key);
								}

							}
						}
					}

					if (jsonObjData.has(TAG_UPDATE) && !jsonObjData.get(TAG_UPDATE).toString().endsWith("null")) {
						JSONArray updateJsonArray = jsonObjData.getJSONArray(TAG_UPDATE);

						for (int i = 0; i < updateJsonArray.length(); i++) {
							JSONObject tempObj = updateJsonArray.getJSONObject(i);
							String tableName = "";
							Iterator<String> iter = tempObj.keys();
							while (iter.hasNext()) {
								tableName = iter.next();
								JSONArray tableInfoArray = tempObj.getJSONArray(tableName);

								for (int j = 0; j < tableInfoArray.length(); j++) {
									JSONObject tableInfoObj = tableInfoArray.getJSONObject(j);
									String value = tableInfoObj.getString(TAG_VALUE);
									String primaryKey = tableInfoObj.getString(TAG_PRIMARY_KEY);

									mDataBaseHelper.updateIsDataUploaded(tableName, primaryKey, value);

									Log.e("Json", "Update:  TableName: "+ tableName + ", value: " + value
											+ ", primarykey: " + primaryKey);
								}

							}
						}
					}
				}

			} catch (JSONException e) {
				//e.printStackTrace();
			}
		} else {
			Log.e("ServiceHandler", "Couldn't get any data from the url");
		}

	}

}
