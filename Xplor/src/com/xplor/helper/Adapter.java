package com.xplor.helper;

import java.io.IOException;
import com.xplor.common.LogConfig;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Adapter {
	protected static final String TAG = "DataAdapter";

	private final Context mContext;
	private SQLiteDatabase mDb;
	private DataBaseHelper mDbHelper;

	public Adapter(Context context) {
		this.mContext = context;
		mDbHelper = new DataBaseHelper(mContext);
	}

	public Adapter createDatabase() throws SQLException {
		try {
			mDbHelper.createDataBase();
		} catch (IOException mIOException) {
			//LogConfig.logd(TAG, mIOException.toString() + "  UnableToCreateDatabase");
			throw new Error("UnableToCreateDatabase");
		}
		return this;
	}

	public Adapter open() throws SQLException {
		try {
			mDbHelper.openDataBase();
			mDbHelper.close();
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException mSQLException) {
			//LogConfig.logd(TAG, "open >>" + mSQLException.toString());
			throw mSQLException;
		}
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public boolean saveLearningOutcome (ContentValues cv) {

		try {
			mDb.insert("learningOutcome", null, cv);
			//Log.d("saveLearningOutcome =", "informationsaved");
			return true;

		} catch (SQLException ex) {
			LogConfig.logd("NotsaveLearningOutcome =", ex.getMessage());
			return false;
		}
	}
	
	public Cursor getLearningOutcome () {
		try {
			//SELECT dialCode,countryID,countyName FROM countriesimport ORDER BY countyName
			String sql = "SELECT * FROM learningOutcome";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToLast();
			}
			return mCur;
		} catch (SQLException mSQLException) {
			//LogConfig.logd(TAG, "getImageSave >>" + mSQLException.toString());
			throw mSQLException;
		}
	}
	public Cursor getLearningOutcomeId (String strId) {
		try {
			//SELECT dialCode,countryID,countyName FROM countriesimport ORDER BY countyName
			String sql = "SELECT * FROM learningOutcome where catId = '"+strId+"'";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToLast();
			}
			return mCur;
		} catch (SQLException mSQLException) {
			LogConfig.logd(TAG, "getImageSave >>" + mSQLException.toString());
			throw mSQLException;
		}
	}
	
	public boolean deleteLearningOutcome() {
		try {
			// mDb.delete("DetectNumber", "newDate"+"="+date, null);
			 String sql = "DELETE FROM learningOutcome"; 
			 mDb.execSQL(sql);
			 LogConfig.logd("deleteAllSate =","" + sql);
				
			 return true;
		} catch (SQLException mSQLException) {
			LogConfig.logd(TAG, "notdeleteAllSate >>" + mSQLException.toString());
			return false;
		}
	}
	
	public boolean saveSubLearningOutcome (ContentValues cv) {

		try {
			mDb.insert("learningOutcomeSubCat", null, cv);
			//Log.d("saveSubLearningOutcome =", "informationsaved");
			return true;

		} catch (SQLException ex) {
			LogConfig.logd("NotsaveSubLearningOutcome =", ex.getMessage());
			return false;
		}
	}
	
	public Cursor getSubLearningOutcome() {
		try {
			//SELECT dialCode,countryID,countyName FROM countriesimport ORDER BY countyName
			String sql = "SELECT * FROM learningOutcomeSubCat";
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToLast();
			}
			return mCur;
		} catch (SQLException mSQLException) {
			LogConfig.logd(TAG, "getSubLearningOutcome >>" + mSQLException.toString());
			throw mSQLException;
		}
	}
	
	public Cursor getSubLearningOutcomeId(String catId) {
		try {
			String sql = "SELECT * FROM learningOutcomeSubCat WHERE catId = '"+ catId + "'";
			LogConfig.logd("getSubLearningOutcomeId sql =",""+sql);
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToLast();
			}
			return mCur;
		} catch (SQLException mSQLException) {
			LogConfig.logd(TAG, "getSubLearningOutcomeId >>" + mSQLException.toString());
			throw mSQLException;
		}
	}
	
	public boolean deleteSubLearningOutcome() {
		try {
			// mDb.delete("DetectNumber", "newDate"+"="+date, null);
			 String sql = "DELETE FROM learningOutcomeSubCat"; 
			 mDb.execSQL(sql);
			// LogConfig.logd("deletelearningOutcomeSubCat =","" + sql);
				
			 return true;
		} catch (SQLException mSQLException) {
			//Log.e(TAG, "notdeletelearningOutcomeSubCat >>" + mSQLException.toString());
			return false;
		}
	}
	
	public Cursor getSyncParentChildListData(String sql) {
		try {
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToLast();
			}
			//LogConfig.logd(TAG,"getSyncChildListData ="+sql);
			return mCur;
		} catch (SQLException mSQLException) {
			//LogConfig.logd(TAG, "getSyncChildListData >>" + mSQLException.toString());
			throw mSQLException;
		}
	}
	
	public Cursor getExucuteQurey(String sql) {
		try {
			Cursor mCur = mDb.rawQuery(sql, null);
			if (mCur != null) {
				mCur.moveToLast();
			}
			LogConfig.logd(TAG,"getExucuteQurey ="+sql);
			return mCur;
		} catch (SQLException mSQLException) {
			//LogConfig.logd(TAG, "Not getExucuteQurey >>" + mSQLException.toString());
			throw mSQLException;
		}
	}
}
