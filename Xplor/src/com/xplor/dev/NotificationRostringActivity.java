package com.xplor.dev;

import com.xplor.async_task.CancelShiftNotificationAsyncTask;
import com.xplor.async_task.ContinueShiftNotificationAsyncTask;
import com.xplor.common.Common;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NotificationRostringActivity extends Activity {

	private SharedPreferences sPrefs;
	private String strType = "",shift_id;
	private Context mContext=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_popup_multiple);
		mContext = NotificationRostringActivity.this;
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
		TextView btnYes = (TextView) findViewById(R.id.Popup_Yes_Btn);
		TextView btnNo = (TextView) findViewById(R.id.Popup_No_Btn);
		
		if(strType.equals("cancel")) {
			btnYes.setText("Cancel");
			btnNo.setText("View");
		} else {
			btnYes.setText("Cancel");
			btnNo.setText("Continue");
		}
	
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Common._isNOTIFICATION_FORGROUND = false;
				Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
				editor.putString("message", "");
				editor.putString("type", "");
				editor.putString("shift_id", "");
				editor.commit();
				finish();
			}
		});
		
		
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Common.DEVICE_TYPE = "android";
				Common.USER_TYPE = "1";
				Common._isNOTIFICATION_FORGROUND = true;
				if(strType.equals("cancel")) { // Shift Cancel
					CancelShiftNotificationAsyncTask mCancelShiftAsyncTask = new CancelShiftNotificationAsyncTask(NotificationRostringActivity.this);
					mCancelShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, shift_id);
				} else { //Shift Week Complete
					ContinueShiftNotificationAsyncTask mContinueShiftAsyncTask = new ContinueShiftNotificationAsyncTask(NotificationRostringActivity.this,shift_id);
					mContinueShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} 
			}
		});
	}

}
