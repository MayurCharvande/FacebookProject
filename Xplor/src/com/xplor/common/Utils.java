package com.xplor.common;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	
    public static void CopyStream(InputStream is, OutputStream os) {
    	
        final int buffer_size=1024;
        try {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
    }
    

	public static void print(String message) {
		Log.i("TAG", message);
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static boolean isOnline(Context context) {
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	public static void showNetworkDialog(final Activity activity) {
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
		alertBuilder.setTitle("Enable Connectivity");
		alertBuilder.setMessage("Turning on Wi-Fi/ Network Connectivity will improve location accuracy.");
				//.setMessage("Enable your Wifi/3G connectivity to move forward ...");
		alertBuilder.setPositiveButton("Enable Wifi",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.startActivity(new Intent(
								Settings.ACTION_WIFI_SETTINGS));
					}
				});
		alertBuilder.setNeutralButton("Enable Network Connectivity",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
						final Intent intent = new Intent(
								Settings.ACTION_DATA_ROAMING_SETTINGS);
						intent.addCategory(Intent.CATEGORY_LAUNCHER);
						final ComponentName cn = new ComponentName(
								"com.android.phone",
								"com.android.phone.Settings");
						intent.setComponent(cn);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						activity.startActivity(intent);
					}
				});
		alertBuilder.setNegativeButton("Exit",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				});
		alertBuilder.setCancelable(false);
		alertBuilder.create().show();
	}

}