package com.xplor.roastring;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.xplor.Model.EducatoreShift;
import com.xplor.adaptor.EducatoreUpcomingShiftAdapter;
import com.xplor.async_task.CancelShiftAsyncTask;
import com.xplor.async_task.UpcomingShiftAsyncTask;
import com.xplor.common.Common;
import com.xplor.dev.R;
import com.xplor.helper.AlertDialogManagerWithCloseActivity;
import com.xplor.interfaces.EducatorLeaveCallBack;
import com.xplor.interfaces.RequestShiftCallBack;

public class UpcomingShiftScreen extends Activity implements EducatorLeaveCallBack, RequestShiftCallBack {

	private ListView options;
	private ImageButton mBackBtn;
	private UpcomingShiftAsyncTask myTask;
	private EducatoreUpcomingShiftAdapter mAdapter;
	boolean bolListScroll = true;
	boolean isFirstTimeCall = true;
	private TextView titleTextView;
	public int shift_id = -1, center_id = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_break_shift);
		createViews();
	}

	private void createViews() {
		
		titleTextView = (TextView) findViewById(R.id.break_shift_title);
		titleTextView.setText("Upcoming Shifts");
		mBackBtn = (ImageButton) findViewById(R.id.break_shift_back_btn);
		options = (ListView) findViewById(R.id.break_history_list);
		
		options.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			if (options.getLastVisiblePosition() == mAdapter.getCount() - 1 && bolListScroll) {
//				bolListScroll = false;
//				myTask = new UpcomingShiftAsyncTask(UpcomingShiftScreen.this,"Future");
//				myTask.setCallBack(UpcomingShiftScreen.this);
//				myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			  }
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

			}
		});

		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			}
		});

		myTask = new UpcomingShiftAsyncTask(UpcomingShiftScreen.this,"Future");
		myTask.setCallBack(UpcomingShiftScreen.this);
		myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void requestSetEducatorLeave(ArrayList<EducatoreShift> allShiftList) {
	
		//if (mAdapter == null) {
			ArrayList<EducatoreShift> list = new ArrayList<EducatoreShift>();
			list.addAll(allShiftList);
			mAdapter = new EducatoreUpcomingShiftAdapter(UpcomingShiftScreen.this, list);
			options.setAdapter(mAdapter);
//		} else {
//			mAdapter.addItems(allShiftList);
//		}
		bolListScroll = true;
	}

	@Override
	public void requestSendResponce(String responce) {

		shift_id = -1;
		center_id = -1;

		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					Common.isDisplayMessage_Called = false;
					displayAlertSingle(UpcomingShiftScreen.this, "Message",resultObj.getString("message"));
				} else {
					AlertDialogManagerWithCloseActivity alert = new AlertDialogManagerWithCloseActivity();
				    alert.showAlertDialog(UpcomingShiftScreen.this, "Message",
				    resultObj.getString("message"), isFirstTimeCall);
				     if(isFirstTimeCall)
				        isFirstTimeCall = false;
				}
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
	}
	
	public void displayCancelShiftAlert(Context mContext,String strTitle, String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_multiple);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(wmlp);
			
			TextView txtTitle = (TextView) dialog.findViewById(R.id.Popup_Title_Txt);
			txtTitle.setText(strTitle);

			TextView txtSMS = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSMS.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnYes.setText("NO");
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});

			Button btnNo = (Button) dialog.findViewById(R.id.Popup_No_Btn);
			btnNo.setText("YES");
			btnNo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					if((shift_id != -1) && (center_id != -1)) {
						CancelShiftAsyncTask mCancelShiftAsyncTask = new CancelShiftAsyncTask(UpcomingShiftScreen.this);
						mCancelShiftAsyncTask.setCallBack(UpcomingShiftScreen.this);
						mCancelShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, center_id + "",shift_id + "");
					 }
					dialog.dismiss();
				}
			});
		}
	}
	
	public void displayAlertSingle(Context mContext, String strTitle,String strSMS) {

		if (!Common.isDisplayMessage_Called) {
			Common.isDisplayMessage_Called = true;
			final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Panel);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_popup_single);
			dialog.setCancelable(false);
			WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
			wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
			wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setAttributes(wmlp);

			TextView txtTitle = (TextView) dialog.findViewById(R.id.Popup_Title_Txt);
			txtTitle.setText(strTitle);

			TextView txtSMS = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSMS.setText(strSMS);
			if (!dialog.isShowing())
				dialog.show();

			Button btnOk = (Button) dialog.findViewById(R.id.Popup_Ok_Btn);
			btnOk.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					mAdapter = null;
					myTask = new UpcomingShiftAsyncTask(UpcomingShiftScreen.this,"Future");
					myTask.setCallBack(UpcomingShiftScreen.this);
					myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					dialog.dismiss();
				}
			});
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}

}