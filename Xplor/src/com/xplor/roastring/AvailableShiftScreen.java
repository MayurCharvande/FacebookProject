package com.xplor.roastring;

import java.util.ArrayList;
import org.json.JSONArray;
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
import com.xplor.adaptor.EducatoreAvailableShiftAdapter;
import com.xplor.async_task.RequestShiftAsyncTask;
import com.xplor.async_task.UpcomingShiftAsyncTask;
import com.xplor.common.Common;
import com.xplor.dev.R;
import com.xplor.helper.AlertDialogManagerWithCloseActivity;
import com.xplor.interfaces.EducatorLeaveCallBack;
import com.xplor.interfaces.RequestShiftCallBack;

public class AvailableShiftScreen extends Activity implements EducatorLeaveCallBack, RequestShiftCallBack {

	private ListView listAvailableShift;
	private ImageButton mBackBtn;
	private UpcomingShiftAsyncTask myTask;
	private EducatoreAvailableShiftAdapter mAdapter;
	boolean bolListScroll = true;
	private TextView titleTextView;
	public final static int REQUEST_FOR_SHIFT = 1;
    boolean isFirstTimeCall = true;
	public int shift_id = -1, center_id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_break_shift);
		CreateViews();
	}

	private void CreateViews() {
		
		titleTextView = (TextView) findViewById(R.id.break_shift_title);
		listAvailableShift = (ListView) findViewById(R.id.break_history_list);
		listAvailableShift.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

//				if (listAvailableShift.getLastVisiblePosition() == mAdapter.getCount() - 1 && bolListScroll) {
//					bolListScroll = false;
//					myTask = new UpcomingShiftAsyncTask(AvailableShiftScreen.this,"Available");
//					myTask.setCallBack(AvailableShiftScreen.this);
//					myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mBackBtn = (ImageButton) findViewById(R.id.break_shift_back_btn);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			}
		});
		
		String strCancelResponce = getIntent().getStringExtra("CancelResponce");
		if(strCancelResponce == null)
			strCancelResponce = "";
		if(strCancelResponce.length() > 5) {
			titleTextView.setText("Shift Details");
			if (mAdapter == null) {
				ArrayList<EducatoreShift> list = new ArrayList<EducatoreShift>();
				list.addAll(parseNotificationHistoty(strCancelResponce));
				mAdapter = new EducatoreAvailableShiftAdapter(AvailableShiftScreen.this, list);
				listAvailableShift.setAdapter(mAdapter);
			} else {
				mAdapter.addItems(parseNotificationHistoty(strCancelResponce));
			}
		} else {
			titleTextView.setText("Available Shifts");
		    myTask = new UpcomingShiftAsyncTask(AvailableShiftScreen.this,"Available");
		    myTask.setCallBack(this);
		    myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}
	
	@Override
	public void requestSetEducatorLeave(ArrayList<EducatoreShift> allShiftList) {
		if (mAdapter == null) {
			mAdapter = new EducatoreAvailableShiftAdapter(AvailableShiftScreen.this, allShiftList);
			listAvailableShift.setAdapter(mAdapter);
		} else {
			//mAdapter.addItems(allShiftList);
			mAdapter.notifyDataSetChanged();
		}
		bolListScroll = true;
	}
	
	private ArrayList<EducatoreShift> parseNotificationHistoty(String responce) {

		ArrayList<EducatoreShift> tempList = new ArrayList<EducatoreShift>();

		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					JSONArray historyarray = resultObj.getJSONArray("shift_details");
					for (int i = 0; i < historyarray.length(); i++) {
						JSONObject historyObj = historyarray.getJSONObject(i);
						EducatoreShift obj = new EducatoreShift();
						obj.shift_id = historyObj.getInt("shifts_id");
						obj.shift_date = historyObj.getString("shift_date");
						obj.shift_start_time = historyObj.getString("shift_start_time");
						obj.shift_end_time = historyObj.getString("shift_end_time");
						obj.break_hours = historyObj.getString("break_hours");
						obj.shift_Hours = historyObj.getString("duration_hours");
						obj.shift_Minutes = historyObj.getString("duration_minutes");
						obj.shift_Room = historyObj.getString("shift_room");
						obj.status = historyObj.getString("status");
						obj.center_id = historyObj.getInt("center_id");
						obj.educator_id = historyObj.getInt("educator_id");
						tempList.add(obj);
					}
				} 
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		return tempList;
	}

//	long calculateTimeDifference(String startTime, String endTime) {
//
//		Date startDate = Common.convertStringDateTolocalDate(startTime);
//		Date endDate = Common.convertStringDateTolocalDate(endTime);
//		long diff = endDate.getTime() - startDate.getTime();
//		long seconds = diff / 1000;
//
//		return seconds;
//	}

	@Override
	public void requestSendResponce(String strResponce) {
		
		shift_id = -1;
		center_id = -1;

		try {
			JSONObject jObj = new JSONObject(strResponce);
			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status")&& resultObj.getString("status").equals("success")) {
					AlertDialogManagerWithCloseActivity mDialog = new AlertDialogManagerWithCloseActivity();
					mDialog.showAlertDialog(AvailableShiftScreen.this, "Message", resultObj.getString("message"), true);	
				} else {
					AlertDialogManagerWithCloseActivity mDialog = new AlertDialogManagerWithCloseActivity();
					mDialog.showAlertDialog(AvailableShiftScreen.this, "Message", resultObj.getString("message"), true);	
					// for unsucess
				}
			}
		} catch (JSONException e) {
			//e.printStackTrace();
		}

	}
	
	public void DisplayCancelAlert(Context mContext,String strTitle, String strSMS) {

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
					if ((shift_id != -1) && (center_id != -1)) {
						RequestShiftAsyncTask mRequestShiftAsyncTask = new RequestShiftAsyncTask(AvailableShiftScreen.this);
						mRequestShiftAsyncTask.setCallBack(AvailableShiftScreen.this);
						mRequestShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, center_id + "",shift_id + "");
					}
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
