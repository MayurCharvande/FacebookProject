package com.xplor.dev;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.xplor.async_task.CancelShiftNotificationAsyncTask;
import com.xplor.common.Common;

@SuppressWarnings("unused")
public class NotificationCancelShiftActivity extends Activity {

	private SharedPreferences sPrefs;
	private String strType = "",shift_id;
	private Context mContext=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_popup_single);
		mContext = NotificationCancelShiftActivity.this;
		CreateView();
	}

	private void CreateView() {
		
		TextView txtTitle = (TextView) findViewById(R.id.Popup_Title_Txt);
		TextView txtMessage = (TextView) findViewById(R.id.Popup_Message_Txt);
		txtTitle.setText("Message");
		txtMessage.setText(getIntent().getStringExtra("Message"));
		
		sPrefs = getSharedPreferences(getResources().getString(R.string.app_name), 0);
		strType = sPrefs.getString("type", "");
		shift_id = sPrefs.getString("shift_id", "");
		TextView btnOk = (TextView) findViewById(R.id.Popup_Ok_Btn);
		btnOk.setText("Ok");
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Common.DEVICE_TYPE = "android";
				Common.USER_TYPE = "1";
				Common._isNOTIFICATION_FORGROUND = true;
				CancelShiftNotificationAsyncTask mCancelShiftAsyncTask = new CancelShiftNotificationAsyncTask(NotificationCancelShiftActivity.this);
				mCancelShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, shift_id);
			}
		});
	}

}
