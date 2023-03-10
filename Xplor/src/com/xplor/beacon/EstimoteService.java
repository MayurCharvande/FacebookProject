package com.xplor.beacon;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class EstimoteService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			EstimoteManager.Create((NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE), this,intent);
		} catch (Exception e) {
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EstimoteManager.stop();
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}
}