package com.xplor.dev;

import com.xplor.common.Common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationPopupActivity extends Activity implements OnClickListener {

	private TextView messageText;
	private Button yesBtn, noBtn, okBtn;
	private RelativeLayout twoBtnLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		setContentView(R.layout.notification_popup);
		messageText = (TextView) findViewById(R.id.notification_popup_Popup_Message_Txt);

		yesBtn = (Button) findViewById(R.id.notification_popup_Popup_Yes_Btn);
		yesBtn.setOnClickListener(this);

		noBtn = (Button) findViewById(R.id.notification_popup_Popup_No_Btn);
		noBtn.setOnClickListener(this);

		okBtn = (Button) findViewById(R.id.notification_popup_Popup_Ok_Btn);
		okBtn.setOnClickListener(this);

		twoBtnLayout = (RelativeLayout) findViewById(R.id.notification_popup_Bottom_Layout_TwoButton);

		if (getIntent().getBooleanExtra("isEnterRegion", false) && Common.USER_TYPE.equals("2")) {
			twoBtnLayout.setVisibility(View.VISIBLE);
			okBtn.setVisibility(View.GONE);
		} else {
			twoBtnLayout.setVisibility(View.GONE);
			okBtn.setVisibility(View.VISIBLE);
		}

		messageText.setText(getIntent().getStringExtra("regionMsg"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notification_popup_Popup_Yes_Btn: {
			Intent i = new Intent(NotificationPopupActivity.this,ChildAttendanceScreenActivity.class);
			i.putExtra("isEnterRegion", true);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			finish();
		}
			break;

		case R.id.notification_popup_Popup_No_Btn: {
			finish();
		}
			break;

		case R.id.notification_popup_Popup_Ok_Btn: {
			if (Common.childAttandanceActivity != null) {
				Common.childAttandanceActivity.finish();
				Common.childAttandanceActivity = null;
			}
			finish();
		}
			break;

		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
