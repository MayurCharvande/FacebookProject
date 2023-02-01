package com.xplor.local.syncing.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import com.xplor.common.Common;
import com.xplor.common.WebUrls;
import com.xplor.local.syncing.download.SyncingUtil;
import com.xplor.parsing.UploadDatabaseJsonResponce;

import android.content.Context;
import android.util.Log;

public class MigrateDatabase {

	Context _context;

	String TAG = "Migrate database";
	public File zipFile;

	public MigrateDatabase(Context context) {
		_context = context;
	}

	public void migrateDatabaseAction() throws Exception {
		
//		DataBaseHelper mQh = new DataBaseHelper(_context);
//		SyncingUtil.LAST_MODIFIED_DATE = mQh.getLastModifiedDateFromDatabase(SyncingUtil.DATABSE_JSON_UPLOAD);
//
//		if ((SyncingUtil.LAST_MODIFIED_DATE == null) || (SyncingUtil.LAST_MODIFIED_DATE.length() <= 0)) {
//			Log.d(TAG, "Date found null for upload.");
//
//		} else {

			File jsonFloder = new File(_context.getFilesDir().getAbsolutePath()+ File.separator + "json");
			if (!jsonFloder.exists())
				jsonFloder.mkdir();
			ArrayList<File> jsonFiles = new ArrayList<File>();

			for (int i = 0; i < SyncingUtil.UPLOAD_TABLES.length; i++) {

				JSONArray jSonArray = new JSONArray();
				// if previous app version is 1.0, than upload data using LMD
				// else upload data using is_data_upload.
				double oldAppVeriosn = Double.parseDouble(Common.getAppVersion(_context));

				if (oldAppVeriosn < 1.1) {
					//jSonArray = mQh.getJsonOfTableUsingLMD(SyncingUtil.UPLOAD_TABLES[i],Common.CHILD_LAST_MODIFY_DATE);
				} else {
					//jSonArray = mQh.getJsonOfTable(SyncingUtil.UPLOAD_TABLES[i],Common.CHILD_LAST_MODIFY_DATE);
				}

				if (jSonArray.length() > 0) {
					File jSonFile = SyncingUtil.createFileAndWriteData(SyncingUtil.UPLOAD_TABLES[i] + ".json", 
							jSonArray.toString(), _context.getFilesDir().getAbsolutePath());
					jsonFiles.add(jSonFile);
					Log.e(TAG, "Create Json of " + SyncingUtil.UPLOAD_TABLES[i]+ " table");
				} else {
					continue;
				}

			}

			if (jsonFiles.size() > 0) {
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_USER_ID, Common.USER_ID));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_TYPE, Common.DEVICE_TYPE));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_MODEL, android.os.Build.MODEL));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_TOCKEN,Common.DEVICE_TOKEN));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_DATE, Common.getCurrentDateTime()));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_REG_ID,""+Common.PERSIST_REGISTER_ID));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_OS_VERSION,android.os.Build.VERSION.RELEASE + ""));
				//nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_LAT_LONG, Config.DEVICE_LAT_LONG));
				String paramString = URLEncodedUtils.format(nameValuePairs,"utf-8");
				String uploadUrl = WebUrls.URL_SYNC_MIGRATE_DATA + "?"+ paramString;

				zipFile = SyncingUtil.createZipUtil("migrateData", jsonFiles,_context.getFilesDir().getAbsolutePath());

				upload(zipFile.getAbsolutePath(), uploadUrl);

				if (zipFile.exists())
					zipFile.delete();
			}

	//	}
	}

	public void upload(String filePath, String _uploadUrl) throws Exception {
		// Url of the server
		//DataBaseHelper mQh = new DataBaseHelper(_context);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(_uploadUrl);
		MultipartEntity mpEntity = new MultipartEntity();
		// Path of the file to be uploaded
		// String filepath = "";
		File file = new File(filePath);
		ContentBody cbFile = new FileBody(file, "application/zip");

		// Add the data to the multipart entity
		mpEntity.addPart("migrateData", cbFile);
		// mpEntity.addPart("name", new StringBody("Test",
		// Charset.forName("UTF-8")));2
		// mpEntity.addPart("data", new StringBody("This is test report",
		// Charset.forName("UTF-8")));
		post.setEntity(mpEntity);
		// Execute the post request
		HttpResponse response1 = client.execute(post);
		// Get the response from the server
		HttpEntity resEntity = response1.getEntity();
		String Response = EntityUtils.toString(resEntity);

		UploadDatabaseJsonResponce mResponce = new UploadDatabaseJsonResponce(_context, Response);
		mResponce.JsonResponce();
		Log.d("Migrate Databse Response:", Response);
		// Generate the array from the response
		// JSONArray jsonarray = new JSONArray("["+Response+"]");
		// JSONObject jsonobject = jsonarray.getJSONObject(0);
		// //Get the result variables from response
		// String result = (jsonobject.getString("result"));
		// String msg = (jsonobject.getString("msg"));
		// Close the connection
		client.getConnectionManager().shutdown();
	}

}
