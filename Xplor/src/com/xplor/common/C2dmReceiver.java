package com.xplor.common;

import java.util.HashMap;
import java.util.Map;

import com.xplor.dev.CommentScreenActivity;
import com.xplor.dev.MainScreenActivity;
import com.xplor.dev.NotificationCancelShiftActivity;
import com.xplor.dev.NotificationPerentActivity;
import com.xplor.dev.NotificationRostringActivity;
import com.xplor.dev.R;
import com.xplor.dev.SplashScreenActivity;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class C2dmReceiver extends BroadcastReceiver {

	private Context context;
	String registration = "", message = "";

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			onRegistration(context, intent);
		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			onMessage(context, intent);
		}
	}

	private void onMessage(Context context, Intent intent) {
	
		message = intent.getStringExtra("message");
		LogConfig.logd("Notification message =","" + message);
		if(message == null) {
			return;
		}
		
		Editor editor = context.getSharedPreferences(context.getResources().getString(R.string.app_name),Context.MODE_PRIVATE).edit();
		editor.putString("message", intent.getStringExtra("message"));
		editor.putString("subtitle", intent.getStringExtra("subtitle"));
		editor.putString("feed_id", intent.getStringExtra("feed_id"));
		editor.putString("child_id", intent.getStringExtra("child_id"));
		editor.putString("center_id", intent.getStringExtra("center_id"));
		editor.putString("room_id", intent.getStringExtra("room_id"));
		editor.putString("child_name", intent.getStringExtra("child_name"));
		editor.putString("child_gender", intent.getStringExtra("child_gender"));
		editor.putString("type", intent.getStringExtra("type"));
		editor.putString("news_feed_id", intent.getStringExtra("news_feed_id"));
		editor.putString("shift_id", intent.getStringExtra("shift_id"));
		editor.commit();

		LogConfig.logd("notification type =",""+ intent.getStringExtra("type"));
		String strType = intent.getStringExtra("type");
		String strId = "";
		if(strType == null)
		   strType = "";
		try {
			
			if (strType.equals("complete")) {
				strId = intent.getStringExtra("shift_id");
			} else if (strType.equals("week_complete")) {
				strId = intent.getStringExtra("shift_id");
			} else if (strType.equals("cancel")) {
				strId = intent.getStringExtra("shift_id");
			} else if (strType.equals("comment")) {
				strId = intent.getStringExtra("news_feed_id");
			} else {
				strId = intent.getStringExtra("child_id");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			strId = intent.getStringExtra("child_id");
		}
		
		if (Common._isAPP_FORGROUND) {
			Common._isNOTIFICATION_FORGROUND = true;
			if (strType.equals("complete")) {
				Intent mIntent = new Intent(context, NotificationRostringActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra("Message", intent.getStringExtra("message"));
				context.startActivity(mIntent);
			} else if (strType.equals("week_complete")) {
				Intent mIntent = new Intent(context, NotificationRostringActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra("Message", intent.getStringExtra("message"));
				context.startActivity(mIntent);
			} else if (strType.equals("cancel")) {
				Intent mIntent = new Intent(context, NotificationCancelShiftActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra("Message", intent.getStringExtra("message"));
				context.startActivity(mIntent);
			} else if (strType.equals("comment")) {
				Intent mIntent = new Intent(context, NotificationPerentActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra("Message", intent.getStringExtra("message"));
				context.startActivity(mIntent);
			} else {
				Intent mIntent = new Intent(context, NotificationPerentActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mIntent.putExtra("Message", intent.getStringExtra("message"));
				context.startActivity(mIntent);
			}
		} else {
			generateNotification(context, intent.getStringExtra("message"),strId);
		}

	}

	private void onRegistration(Context context, Intent intent) {

		registration = intent.getStringExtra("registration_id");

		if (intent.getStringExtra("error") != null) {
			// Registration failed, should try again later.
			Log.d("c2dm", "registration failed");
			String error = intent.getStringExtra("error");

			if (error == "SERVICE_NOT_AVAILABLE") {
				Log.d("c2dm", "SERVICE_NOT_AVAILABLE");
			} else if (error == "ACCOUNT_MISSING") {
				Log.d("c2dm", "ACCOUNT_MISSING");
			} else if (error == "AUTHENTICATION_FAILED") {
				Log.d("c2dm", "AUTHENTICATION_FAILED");
			} else if (error == "TOO_MANY_REGISTRATIONS") {
				Log.d("c2dm", "TOO_MANY_REGISTRATIONS");
			} else if (error == "INVALID_SENDER") {
				Log.d("c2dm", "INVALID_SENDER");
			} else if (error == "PHONE_REGISTRATION_ERROR") {
				Log.d("c2dm", "PHONE_REGISTRATION_ERROR");
			}

		} else if (intent.getStringExtra("unregistered") != null) {

			// un_registration done, new messages from the authorized sender
			// will be rejected
			Log.d("c2dm", "unregistered");

		} else if (registration != null) {

			Editor editor = context.getSharedPreferences(context.getResources().getString(R.string.app_name),Context.MODE_PRIVATE).edit();
			Common.DEVICE_TOKEN = registration;
			editor.putString("DEVICE_TOKEN", Common.DEVICE_TOKEN);
			editor.commit();
			LogConfig.logd("C2DM Device tokan =", ""+Common.DEVICE_TOKEN);
		}
	}

	@SuppressWarnings("deprecation")
	private void generateNotification(Context context, String message,String childId) {

		// put an icon for your notification in your res/draw_able folder
		// and then get the icon as an
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis(); // can change this to a future
												// time if desired
		NotificationManager notificationManager = (NotificationManager) 
				context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, SplashScreenActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationIntent.setAction(childId);
		PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notificationManager.notify(0, notification);

	}
}
