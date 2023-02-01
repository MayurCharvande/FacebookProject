package com.xplor.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xplor.common.LogConfig;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("SdCardPath") 
public class DataBaseHelper extends SQLiteOpenHelper {
	
	private static String TAG = "DataBaseHelper";
	private static String DB_PATH = "";
	public static String DB_NAME = "MyXplor.sqlite";// Database name
	public static SQLiteDatabase mDataBase;
	private final Context mContext;  

	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);// 1 its Database Version
		DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		this.mContext = context;
	}

	public void createDataBase() throws IOException {
		// If database not exists copy it from the assets

		boolean mDataBaseExist = checkDataBase();
		if (!mDataBaseExist) {
			this.getWritableDatabase();
			this.close();
			try {
				// Copy the database from assets
				copyDataBase();
				//Log.e(TAG, "createDatabase database created");
			} catch (IOException mIOException) {
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}

	// Check that the database exists here: /data/data/your package/databases/Da
	// Name
	private boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	// Copy the database from assets
	private void copyDataBase() throws IOException {
		InputStream mInput = mContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = mInput.read(mBuffer)) > 0) {
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	// Open the database, so we can query it
	public boolean openDataBase() throws SQLException {
		String mPath = DB_PATH + DB_NAME;
		mDataBase = SQLiteDatabase.openDatabase(mPath, null,
				SQLiteDatabase.CREATE_IF_NECESSARY);
		return mDataBase != null;
	}
	
	public void insertSyncHistoryData(String data) {
		try {
            String strQuery = "INSERT INTO sync_history (user_id, user_type , sync_type, sync_date, is_deleted) values "+ data;
            
			openDataBase();
			mDataBase.execSQL(strQuery);
			close();
			//LogConfig.logd("insertSyncHistoryData =", strQuery);
		  } catch (Exception ex) {
			// LogConfig.logd("not insertSyncHistoryData =","data");
		  }
	}
	
	public void insertSyncLastFeedId(String data) {
		try {
            String strQuery = "INSERT OR REPLACE INTO save_last_feed_id (child_id, feed_id) values "+ data;
            
			openDataBase();
			mDataBase.execSQL(strQuery);
			close();
			LogConfig.logd("insertSyncHistoryData =", strQuery);
		  } catch (Exception ex) {
			// LogConfig.logd("not insertSyncHistoryData =","data");
		  }
	}
	
	public void updateTablesByUploadResponce(String tableName,String mobileKey, String primaryKey, String value) {
		try {
			Log.d(TAG, "updateTablesByUploadResponce call");

			openDataBase();
			ContentValues values = new ContentValues();
			values.put(primaryKey, value);

			int result = mDataBase.update(tableName, values, "mobile_key"+ " = ?", new String[] { mobileKey + "" });
			close();
			Log.d(TAG, "result"+result);
		} catch (Exception ex) {
			close();
			//ex.fillInStackTrace();
			//LogConfig.logd(TAG, "updateTablesByUploadResponce call in exception");
			//ex.printStackTrace();
		}
	}
	
	public void updateTablesByUploadResponceRefrancesString(String tableName,
			String mobileKey, String primaryKey, String value) {
		try {
		
			openDataBase();
			ContentValues values = new ContentValues();
			values.put(primaryKey, value);

			int result = mDataBase.update(tableName, values, "mobile_key"
					+ " = ?", new String[] { mobileKey + "" });
			Log.d(TAG, "result"+result);
			close();

		} catch (Exception ex) {
			close();
			//ex.fillInStackTrace();
			Log.d(TAG, "updateTablesByUploadResponce call in exception");
			//ex.printStackTrace();
		}
	}
	
	public void updateTablesByUploadResponceRefrancesLong(String tableName,
			String mobileKey, String primaryKey, String value) {
		try {
			Log.d(TAG, "updateTablesByUploadResponce call");
			openDataBase();
			// Cursor mCursor = myDataBase.rawQuery(querySearchData, null);
			ContentValues values = new ContentValues();
			values.put(primaryKey, value);

			int result = mDataBase.update(tableName, values, primaryKey
					+ " = ?", new String[] { mobileKey + "" });
			Log.d(TAG, "result"+result);
			close();

		} catch (Exception ex) {
			close();

			ex.fillInStackTrace();
			Log.d(TAG, "updateTablesByUploadResponce call in exception");
			ex.printStackTrace();
		}
	}
	
	public void updateIsDataUploaded(String tableName, String primaryKey,String value) {
		try {
			Log.d(TAG, "updateIsDataUploaded call");

			openDataBase();
			String[] ids = value.split(",");

			for (String id : ids) {
				ContentValues values = new ContentValues();
				values.put("is_uploaded", 1);
				int result = mDataBase.update(tableName, values, primaryKey
						+ " = ?", new String[] { id + "" });
				Log.d(TAG, "result"+result);
			}
			
			close();

		} catch (Exception ex) {
			close();
			//ex.fillInStackTrace();
			Log.d(TAG, "updateTablesByUploadResponce call in exception");
			ex.printStackTrace();
		}
	}
	
	public void insertNewsFeedSyncHistoryData(String data) {
		try {
            String strQuery = "INSERT INTO sync_history (child_id, user_id, user_type , sync_type, sync_date, is_deleted) values "+ data;
            //LogConfig.logd("insertNewsFeedSyncHistoryData =", strQuery);
			openDataBase();
			mDataBase.execSQL(strQuery);
			close();

		  } catch (Exception ex) {
			//ex.printStackTrace();
		  }
	}
	
	public void insertSyncChildListData(String data) {
		try {
			//LogConfig.logd("insertSyncChildListData =", data);
			openDataBase();
			mDataBase.execSQL(data);
			close();

		} catch (Exception ex) {
			//ex.printStackTrace();
		}
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public JSONArray getJsonOfTable(String table_name) {
		JSONArray json = null;
		try {

			openDataBase();
			String query = "SELECT * FROM " + table_name+ " WHERE  is_uploaded = 0";

			Log.e("json_table", query);
			Cursor mCursor = mDataBase.rawQuery(query, null);

			mCursor.moveToFirst();
			int dataCount = mCursor.getCount();
			json = new JSONArray();

			for (int i = 0; i < dataCount; i++) {

				JSONObject obj = new JSONObject();

				for (int j = 0; j < mCursor.getColumnCount(); j++) {
					String value = "";
					LogConfig.logd("curser file type =",""+ mCursor.getType(j) +" name ="+mCursor.getColumnName(j));
					 switch (mCursor.getType(j)) {
						case Cursor.FIELD_TYPE_STRING:
							    if(mCursor.getColumnName(j).equals("image_width")) {
								  
							    } else if(mCursor.getColumnName(j).equals("image_height")) {
								  
							    } else if(mCursor.getColumnName(j).equals("image")) {
								  
							    } else {
									 value = mCursor.getString(mCursor.getColumnIndex(mCursor.getColumnName(j)))+ "";
									 obj.put(mCursor.getColumnName(j), value);
								}
							break;
						case Cursor.FIELD_TYPE_FLOAT:
							
						    if(mCursor.getColumnName(j).equals("image_width")) {
							  
						    } else if(mCursor.getColumnName(j).equals("image_height")) {
							  
						    } else {
								 value = mCursor.getDouble(mCursor.getColumnIndex(mCursor.getColumnName(j)))+ "";
								 obj.put(mCursor.getColumnName(j), value);
							}
							break;
						case Cursor.FIELD_TYPE_INTEGER:
							    if(mCursor.getColumnName(j).equals("image_width")) {
								  
							    } else if(mCursor.getColumnName(j).equals("image_height")) {
								  
							    } else {
							    	value = mCursor.getLong(mCursor.getColumnIndex(mCursor.getColumnName(j)))+ "";
									obj.put(mCursor.getColumnName(j), value);
								}
							break;
						}
				}
				if(obj.length()>0)
				   json.put(obj);
				if (mCursor.moveToNext()) {
					continue;
				} else {
					break;
				}
			}
			mCursor.close();
			close();

		} catch (Exception e) {
			// handle exception
			close();
			return json;
		}

		return json;
	}
	
	// Update is_uploded column when resources upload
	public void contentUploadedForId(String contentIds, String tableName,String columnName) {

			openDataBase();
			String querySearchData = "UPDATE " + tableName
					+ " SET is_data_uploaded = 1 WHERE " + columnName + " IN ("+ contentIds + ")";

			Cursor cursor = mDataBase.rawQuery(querySearchData, null);
			cursor.getCount();
			cursor.close();
			close();

	}

}
