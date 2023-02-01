package com.xplor.dev;

import com.xplor.common.Common;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NotificationPerentActivity extends Activity {

	private SharedPreferences sPrefs;
	private String strType = "";
	private Context mContext=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_popup_multiple);
		mContext = NotificationPerentActivity.this;
		CreateView();
	}

	private void CreateView() {
		
		TextView txtTitle = (TextView) findViewById(R.id.Popup_Title_Txt);
		TextView txtMessage = (TextView) findViewById(R.id.Popup_Message_Txt);
		txtTitle.setText("Message");
		txtMessage.setText(getIntent().getStringExtra("Message"));
		
		sPrefs = getSharedPreferences(getResources().getString(R.string.app_name), 0);
		TextView btnYes = (TextView) findViewById(R.id.Popup_Yes_Btn);
		TextView btnNo = (TextView) findViewById(R.id.Popup_No_Btn);
		
		btnYes.setText("Cancel");
		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
				editor.putString("message", "");
				editor.putString("subtitle", "");
				editor.putString("feed_id", "");
				editor.putString("child_id", "");
				editor.putString("center_id", "");
				editor.putString("room_id", "");
				editor.putString("child_name", "");
				editor.putString("child_gender", "");
				editor.putString("type", "");
				editor.putString("news_feed_id", "");
				editor.commit();
				Common._isNOTIFICATION_FORGROUND = false;
				finish();
			}
		});
		
		btnNo.setText("View");
		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				strType = sPrefs.getString("type", "");
				Common.DEVICE_TYPE = "android";
				if(strType.length() > 0) {
					Common.FEED_ID = sPrefs.getString("news_feed_id", "");
					//Common.USER_TYPE = "1";
					//Common.VIEW_ONLY = true;
					Common._isNOTIFICATION_FORGROUND = false;
				  if(!Common._isClassOpen) {
					Intent mIntent1 = new Intent(NotificationPerentActivity.this,CommentScreenActivity.class);
					mIntent1.putExtra("Position", 0);
					mIntent1.putExtra("Like", "");
					startActivity(mIntent1);
					finish();
				  } else {
					Intent mIntent1 = new Intent(NotificationPerentActivity.this,CommentScreenActivity.class);
					mIntent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				    mIntent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				    mIntent1.putExtra("Position", 0);
				    mIntent1.putExtra("Like", "");
				    startActivity(mIntent1);
					finish();
				  }
				} else {
					Common.HOME_PAGING = 1;
					Common.CHILD_ID = sPrefs.getString("child_id", "");
					Common.CENTER_ID = sPrefs.getString("center_id", "");
					Common.ROOM_ID = sPrefs.getString("room_id", "");
				   if(Common.USER_TYPE.equals("2"))
				      Common.VIEW_ONLY = false;
				   else Common.VIEW_ONLY = true;
				   Common._isNOTIFICATION_FORGROUND = true;
				  if(!Common._isClassOpen) {
				    Intent intent = new Intent(NotificationPerentActivity.this, MainScreenActivity.class);
			        startActivity(intent);
			        finish();
				  } else {
					Common._isNotificationVisible = true;
					Common.NOTIFICATION_ID = Common.CHILD_ID;
					Intent intent = new Intent(NotificationPerentActivity.this, MainScreenActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				    startActivity(intent);
				    finish();
				  }
				}
				 
			}
		});
	}
}
