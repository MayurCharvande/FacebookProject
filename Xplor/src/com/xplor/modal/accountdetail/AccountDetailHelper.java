package com.xplor.modal.accountdetail;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.xplor.common.LogConfig;
import com.xplor.helper.DataBaseHelper;

public class AccountDetailHelper extends DataBaseHelper {

	public AccountDetailHelper(Context context) {
		super(context);
	}

	public ArrayList<HashMap<String, String>> getAccountImageByLastModifyDate() {
      
		openDataBase();
		String querySearchData = "SELECT id, upload_filename FROM news_feeds WHERE is_data_uploaded = 0 " +
				"AND status = 1 AND upload_filename != '' AND upload_file_type = '1'";
		
		ArrayList<HashMap<String, String>> _list = new ArrayList<HashMap<String, String>>();
		Cursor cursor = mDataBase.rawQuery(querySearchData, null);
		if (cursor.moveToFirst()) {
			do {

				HashMap<String, String> map = new HashMap<String, String>();
				String id = cursor.getString(0);
				map.put(LogConfig.KEY_IMAGE_ID, (id == null) ? "": id);
				String image = cursor.getString(1);
				map.put(LogConfig.KEY_IMAGE_NAME, (image == null) ? "": image);
				_list.add(map);
			} while (cursor.moveToNext());
		}

		if (cursor != null)
			cursor.close();
		mDataBase.close();
		return _list;
	}

	public void updateImageDownloadStatus(String tableName, String whereKey,long whereValue) {

		openDataBase();
		
		ContentValues values = new ContentValues();
		values.put("is_downloaded", 1);
		int update = mDataBase.update(tableName, values, whereKey + " = ?",new String[] { whereValue + "" });
        LogConfig.logd("updateImageDownloadStatus =", ""+update);
		mDataBase.close();
	}
}
