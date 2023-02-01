package com.xplor.local.syncing.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.xplor.common.Common;
import com.xplor.common.LogConfig;
import com.xplor.common.WebUrls;
import com.xplor.helper.DataBaseHelper;
import com.xplor.local.syncing.download.SyncingUtil;
import com.xplor.parsing.UploadDatabaseJsonResponce;

public class UploadDatabase {

	private Context _context;
	private String TAG = "upload database";
	private File zipFile;

	public UploadDatabase(Context context) {
		_context = context;
	}

	public Boolean uploadDatabaseAction() {
		
		 DataBaseHelper mQh=null;
		try { 
			mQh = new DataBaseHelper(_context);
			File jsonFloder = new File(_context.getFilesDir().getAbsolutePath()+ File.separator + "json");
			if (!jsonFloder.exists())
				jsonFloder.mkdir();
			
			ArrayList<File> jsonFiles = new ArrayList<File>();

			for (int i = 0; i < SyncingUtil.UPLOAD_TABLES.length; i++) {

				JSONArray jSonArray = mQh.getJsonOfTable(SyncingUtil.UPLOAD_TABLES[i]);

				if (jSonArray.length() > 0) {
					File jSonFile = SyncingUtil.createFileAndWriteData(SyncingUtil.UPLOAD_TABLES[i] 
							+ ".json", jSonArray.toString(), _context.getFilesDir().getAbsolutePath());
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
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_MODEL, Common.deviceModal));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_TOCKEN,Common.DEVICE_TOKEN));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_DATE, Common.getCurrentDateTime()));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_DEVICE_REG_ID,""+Common.PERSIST_REGISTER_ID));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_OS_VERSION,Common.getAndroidVersion()));
				nameValuePairs.add(new BasicNameValuePair(SyncingUtil.KEY_LAT_LONG, Common.LATITUTE+" "+Common.LONGITUTE));

				String paramString = URLEncodedUtils.format(nameValuePairs,"utf-8");
				String uploadUrl = WebUrls.URL_UPLOAD_SYNC_DATABASE + "?"+ paramString;

				zipFile = SyncingUtil.createZipUtil("UploadDatabase",jsonFiles, _context.getFilesDir().getAbsolutePath());
				saveDataIntoSDCard(zipFile);
				upload(zipFile.getAbsolutePath(), uploadUrl);

				if (zipFile.exists()) {
				    zipFile.length();
					zipFile.delete();
					zipFile.deleteOnExit();
				}
			}
			mQh.close();
			return true;
		} catch(Exception e) {
			if(mQh != null)
			   mQh.close();
			return true;
		}
	}

	private void saveDataIntoSDCard(File zipFile2) {
		
		String Path=Environment.getExternalStorageDirectory().getPath().toString()+ "/MyXplor/TimeLine/";
		File sd = new File(Path);
		  if (!sd.exists()) {
			  sd.mkdir();
		  }
		  FileChannel source=null;
	      FileChannel destination=null;
		  String backupDBPath = "backup_xplor.zip";
		  File backupDB = new File(sd, backupDBPath);
		  try {
	            source = new FileInputStream(zipFile2).getChannel();
	            destination = new FileOutputStream(backupDB).getChannel();
	            destination.transferFrom(source, 0, source.size());
	            source.close();
	            destination.close();
	        } catch(IOException e) {
	        	//e.printStackTrace();
	        }
	}

//	private void exportDB(){
//		
//		Log.d("Call exportDB", "Data base store");
//		String Path=Environment.getExternalStorageDirectory().getPath().toString()+ "/MyXplor/";
////		File sd = Environment.getExternalStorageDirectory();
//		File sd = new File(Path);
//		  if (!sd.exists()) {
//			  sd.mkdir();
//		  }
//	      File data = Environment.getDataDirectory();
//	       FileChannel source=null;
//	       FileChannel destination=null;
//	       String currentDBPath = "/data/"+ "com.xplor.dev" +"/files/"+DataBaseHelper.DB_NAME;
//	       String backupDBPath = "backup_xplor.db";
//	       File currentDB = new File(data, currentDBPath);
//	       File backupDB = new File(sd, backupDBPath);
//	       try {
//	            source = new FileInputStream(currentDB).getChannel();
//	            destination = new FileOutputStream(backupDB).getChannel();
//	            destination.transferFrom(source, 0, source.size());
//	            source.close();
//	            destination.close();
//	        } catch(IOException e) {
//	        	//e.printStackTrace();
//	        }
//	}

	public Boolean upload(String filePath, String _uploadUrl) throws Exception {
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
		mpEntity.addPart("UploadDatabase", cbFile);
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
		LogConfig.logd("Upload Databse Response : =", Response);
		// Generate the array from the response
		// JSONArray jsonarray = new JSONArray("["+Response+"]");
		// JSONObject jsonobject = jsonarray.getJSONObject(0);
		// //Get the result variables from response
		// String result = (jsonobject.getString("result"));
		// String msg = (jsonobject.getString("msg"));
		// Close the connection
		client.getConnectionManager().shutdown();
		return true;
	}

}
