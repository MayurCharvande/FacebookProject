package com.xplor.local.syncing.download;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class SyncingUtil {

	public static final int SYNC_STATUS_START = 1;
	public static final int SYNC_STATUS_END = 2;
	public static int SYNC_STATUS; 
	//public static boolean isDatabaseMigrate = false;
	
//	public static final int NEWS_FEED_DOWNLOAD = 1;
	public static final int CHILD_DATA_DOWNLOAD = 2;
	public static final int CHALLENGES_BADGES_DOWNLOAD = 3;
	public static final int ROASTRING_DOWNLOAD = 4;
	public static final int UPLOAD_DATABASE_ACTION = 5;
	public static final int NONE_DATA_ACTION = 6;
	public static final int UPLOAD_VIDEO_ACTION = 7;
	public static final int UPLOAD_IMAGE_ACTION = 8;
	public static final int ZIPCURUPT_ACTION = 9;

	public static final String DATABSE_JSON_UPLOAD = "'android_to_web_database'";
	public static final String DATABASE_JSON_DOWNLOAD = "'web_to_android_database'";

	public static final String VIDEO_UPLOAD = "'android_to_web_video'";
	public static final String VIDEO_DOWNLOAD = "'web_to_android_video'";

	public static final String IMAGE_UPLOAD = "'android_to_web_user_profile_image'";
	public static final String IMAGE_DOWNLOAD = "'web_to_android_user_profile_image'";

	public static final String KEY_LAST_MODIFY_DATE = "lmd";
	public static final String KEY_USER_ID = "userid";
	public static final String KEY_DEVICE_TYPE = "device_type";
	public static final String KEY_OFFSET = "offset";
	public static final String KEY_LIMIT = "limit";
	public static final String KEY_DEVICE_MODEL = "device_model";
	public static final String KEY_DEVICE_TOCKEN = "device_token";
	
	public static final String KEY_DEVICE_DATE = "device_date";
	public static final String KEY_DEVICE_REG_ID = "device_registration_id";
	
	public static final String KEY_OS_VERSION = "os_version";
	public static final String KEY_LAT_LONG = "latitude_longitude";
	
	//public static final String KEY_IMAGE_NAME = "image_name";
	//public static final String KEY_IMAGE_ID = "id";
	public static final int BUNCH_SIZE_VIDEO = 2; //3
	public static final int BUNCH_SIZE_IMAGE = 2; //5
	public static final int BUNCH_SIZE_DOCUMENT = 2; //5

	public static int ImageCount;
	public static int TotalNoOfRequest;
	public static int offset;
	public static int currentRequestNumber;

	public static int globalSyncAction;
	public static int globalSyncActionForDatabase;
	public static int globalSyncActionForResources;
	
//	public static String[] UPLOAD_TABLES = {"comment_news_feeds", "feed_like", "feed_tag", 
//		"medication_events", "news_feeds", "parent_and_educator_detail", "parent_child_detail", 
//		"parent_like_feed", "parent_share_feed", "sleep_timer", "user_setting" };
	
	public static String[] UPLOAD_TABLES = { "parent_and_educator_detail", "parent_child_detail", "user_setting" };
	
	public static String getLastModifiedDateFromServer(String filePath) {

		// Log.d(TAG, "getLastModifiedDate called for " + filePath);
		FileInputStream fin;
		String fileData = null;
		try {
			fin = new FileInputStream(filePath);
			fileData = inputStreamToString(fin).toString();
			fileData = "'" + fileData.trim() + "'";
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			//e.printStackTrace();
		}
		return fileData;
	}
	
	public static String getLastFeedIdFromServer(String filePath) {

		// Log.d(TAG, "getLastModifiedDate called for " + filePath);
		FileInputStream fin;
		String fileData = null;
		try {
			fin = new FileInputStream(filePath);
			fileData = inputStreamToString(fin).toString();
			//fileData = "'" + fileData.trim() + "'";
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			//e.printStackTrace();
		}
		return fileData.trim();
	}

	public static StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			// Auto-generated catch block
			//e.printStackTrace();
		}

		// Return full string
		return total;
	}

	public static boolean checkNetworkRechability(Context vContext) {
		Boolean bNetwork = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) 
				vContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		for (NetworkInfo networkInfo : connectivityManager.getAllNetworkInfo()) {
			int netType = networkInfo.getType();
			int netSubType = networkInfo.getSubtype();

			if (netType == ConnectivityManager.TYPE_WIFI) {
				bNetwork = networkInfo.isConnected();
				if (bNetwork == true)
					break;
			} else if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubType != TelephonyManager.NETWORK_TYPE_UNKNOWN) {
				bNetwork = networkInfo.isConnected();
				if (bNetwork == true)
					break;
			} else {
				bNetwork = false;
			}
		}
		if (!bNetwork) {
			// Toast.makeText(vContext.getApplicationContext(),
			// "You are not in network", Toast.LENGTH_SHORT).show();
		}
		return bNetwork;
	}

	public static File createFileAndWriteData(String name, String data,String fileDirPath) {
		File newFile = null;

		newFile = new File(fileDirPath + File.separator + "json", name);
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(newFile));
			writer.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {}
		}

		return newFile;

	}

	public static File createZipUtil(String zipFileName,ArrayList<File> fileList, String baseFileDirPath)
			throws ZipException {

		try {
			// Log.d(TAG, zipFileName);
			File fone = new File(baseFileDirPath, zipFileName + ".zip");
			FileOutputStream fos = new FileOutputStream(fone.getAbsolutePath());
			if (fileList.size() > 0) {
				CheckedOutputStream chkos = new CheckedOutputStream(fos,new CRC32());
				ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(chkos));
				// This sets the compression level to STORED, ie, uncompressed
				// zos.setLevel(ZipOutputStream.STORED);
				zos.setLevel(Deflater.DEFAULT_COMPRESSION);
				System.out.println("Checksum: "+ chkos.getChecksum().getValue());
				for (int i = 0; i < fileList.size(); i++) {
					addToZipFile(fileList.get(i), zipFileName, zos);
				}
				zos.close();
			}
			fos.close();
			return fone;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;

		}

	}

	public static void addToZipFile(File fileName, String fileFolder,
			ZipOutputStream zos) throws FileNotFoundException, IOException {

		// Log.d(TAG, "addToZipFile FILE NAME IS " + fileName);
		try {
			if (!fileName.exists()) {
				// Log.d(TAG, "File not found");
				// fileName.delete();
				return;
			}

			FileInputStream fis;
			ZipEntry zipEntry;
			if (!fileName.isDirectory()) {
				fis = new FileInputStream(fileName);
				// BufferedReader in = new BufferedReader(new
				// FileReader(fileName));
				zipEntry = new ZipEntry(fileFolder + "/" + fileName.getName());

				zos.putNextEntry(zipEntry);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zos.write(bytes, 0, length);
				}
				zos.closeEntry();
				fis.close();
			} else {
				zipEntry = new ZipEntry(fileName.getName());
				zos.putNextEntry(zipEntry);
				zos.closeEntry();

			}
		} catch (Exception ex) {
			//ex.printStackTrace();
		}

	}

	@SuppressWarnings("resource")
	public static void zip(File directory, File zipfile) throws IOException {
		
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(zipfile);
		Closeable res = out;
		try {
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while (!queue.isEmpty()) {
				directory = queue.pop();
				for (File kid : directory.listFiles()) {
					String name = base.relativize(kid.toURI()).getPath();
					if (kid.isDirectory()) {
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					} else {
						zout.putNextEntry(new ZipEntry(name));
						copy(kid, zout);
						zout.closeEntry();
					}
				}
			}
		} finally {
			res.close();
		}
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	@SuppressWarnings("unused")
	private static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}

}
