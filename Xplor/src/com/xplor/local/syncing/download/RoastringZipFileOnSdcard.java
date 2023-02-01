package com.xplor.local.syncing.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xplor.common.Common;
import com.xplor.helper.Adapter;
import com.xplor.helper.DataBaseHelper;

public class RoastringZipFileOnSdcard {

	Context _context;
	String url;
	String getLMDType;
	String UserId;
	String UserType;
	String dirctoryPath;
	int offset;
	int limit;
	
	public RoastringZipFileOnSdcard(Context context) {
		_context = context;
		Adapter mDBHelper = new Adapter(_context);
		mDBHelper.createDatabase();
		mDBHelper.open();
		Cursor mCursor = mDBHelper.getExucuteQurey(SqlQuery.getQueryLastModifyDate("",Common.USER_ID,Common.ROSTER_DOWNLOAD));
		if(mCursor != null && mCursor.getCount() > 0) { // data?
			Common.ROASTRING_LAST_MODIFY_DATE = mCursor.getString(mCursor.getColumnIndex("sync_date"));
		 } 
		if(Common.ROASTRING_LAST_MODIFY_DATE == null)
		   Common.ROASTRING_LAST_MODIFY_DATE = "";
		mDBHelper.close();
		mCursor.close();
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getDirctoryPath() {
		return dirctoryPath;
	}

	public void setDirctoryPath(String dirctoryPath) {
		this.dirctoryPath = dirctoryPath;
	}

	public String getGetLMDType() {
		return getLMDType;
	}

	public void setGetLMDType(String getLMDType) {
		this.getLMDType = getLMDType;
	}
	
	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		this.UserId = userId;
	}
	
	public String getUserType() {
		return UserType;
	}
	
	public void setUserType(String uSER_TYPE) {
		this.UserType = uSER_TYPE;
	}

	public Boolean sendDatabaseRequest(String mUrl)throws Exception {
	
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", Common.USER_ID));
		params.add(new BasicNameValuePair("lmd", Common.ROASTRING_LAST_MODIFY_DATE));
		params.add(new BasicNameValuePair("device_type", Common.DEVICE_TYPE));
		
		GetHTTPData mService = new GetHTTPData();
		HttpEntity entity = mService.makeServiceCall(mUrl, Common.GET, params);

		File myFile = null;
		myFile = new File(_context.getFilesDir().getAbsolutePath() + dirctoryPath);
		
		if (!myFile.exists())
			myFile.mkdirs();
		else
			delete(myFile);

		//long fileLength = entity.getContentLength();
		
		int len = 0;
		InputStream in = new BufferedInputStream(entity.getContent(), 1024);
		File zip = File.createTempFile("child_name", ".zip", myFile);

		OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));

		byte[] buffer = new byte[1024];

		//long total = 0;
		while (len >= 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
			//total += len;
		}

		out.close();
		in.close();

		UnzipFiles _decompress = new UnzipFiles(zip.getAbsolutePath(),myFile.getAbsolutePath());
		_decompress.unzipfiles();
		Boolean _isSuccess = getDataAndFillInDataBaseAtSync();
		
		zip.delete();
		System.gc();
		
		return _isSuccess;
	}
	
	public Boolean getDataAndFillInDataBaseAtSync() {
		
		File myFile = null;
		DataBaseHelper qh = new DataBaseHelper(_context);
		try {  
			
			myFile = new File(_context.getFilesDir().getAbsolutePath()+ dirctoryPath);
			File files[] = myFile.listFiles(); 
			
			for (int i = 0; i < files.length; i++) {
				Log.e("Files Child list =", "FILE =" + files[i].getName());
				if (files[i].getName().equals("last_modified_date.txt")) { //5

					// Set the last modified date for the second sync process
					String serverDateFetched = SyncingUtil.getLastModifiedDateFromServer(files[i].getAbsolutePath());
					qh.insertSyncHistoryData("('"+UserId+"', '"+UserType+"', '"+ getLMDType + "',"+ serverDateFetched.trim() + ",'0')");
				} else {
				
				int endIndex = files[i].getName().lastIndexOf(".zip");
			    if (endIndex != -1) {
			    	//
			    } else {
			
				  FileInputStream fin = new FileInputStream(files[i].getAbsoluteFile());
				  String strFile = SyncingUtil.inputStreamToString(fin).toString();

				  Object json = new JSONTokener(strFile).nextValue();

				  if (json instanceof JSONObject) {
				    //you have an object
				    JSONObject actor = new JSONObject(strFile);
				    String mQuery = actor.getString("sqlData");
				    qh.insertSyncChildListData(mQuery);
				  } else if (json instanceof JSONArray) {
					 JSONArray jsonArray = new JSONArray(strFile);
					 for (int z = 0; z < jsonArray.length(); z++) {
						JSONObject actor = jsonArray.getJSONObject(z);
						String mQuery = actor.getString("sqlData");
						qh.insertSyncChildListData(mQuery);
					 }
				  }
			    }
				
			  }
				files[i].delete();
			}
			qh.close();
			return true;
		} catch (JSONException ex) {
			qh.close();
			return true;
			//ex.printStackTrace();
		} catch (Exception ex) {
			qh.close();
			return true;
			//ex.printStackTrace();
		} catch(OutOfMemoryError e) {
			qh.close();
			return true;
			// Memory error
		}
	}

	void delete(File file) throws IOException {
		
		if (file.isDirectory()) {
			for (File c : file.listFiles()) {
				delete(c);
			}
		} else if (file.getAbsolutePath().endsWith(".zip")) {
			if (!file.delete()) {
				new FileNotFoundException("Failed to delete file: " + file);
			}
		}
	}
	
	public String getLastModifiedDateFromServer(String filePath) {

		FileInputStream fin;
		String fileData = null;
		try {
			fin = new FileInputStream(filePath);
			fileData = inputStreamToString(fin).toString();
			fileData = "'" + fileData.trim() + "'";
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		}
		return fileData;
	}
	
	public StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is), 8192);
		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			// Auto-generated catch block
		}
		// Return full string
		return total;
	}

	
}
