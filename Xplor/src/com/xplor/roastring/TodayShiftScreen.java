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
import android.widget.Toast;

import com.xplor.Model.EducatoreShift;
import com.xplor.adaptor.TodaysShiftAdapter;
import com.xplor.async_task.CancelShiftAsyncTask;
import com.xplor.async_task.PastShiftAsyncTask;
import com.xplor.async_task.TodayShiftActionsAsyncTask;
import com.xplor.common.Common;
import com.xplor.common.Validation;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.helper.AlertDialogManagerWithCloseActivity;
import com.xplor.interfaces.RequestShiftCallBack;
import com.xplor.interfaces.TodayShiftActionCallBack;
import com.xplor.interfaces.TodayShiftCallBack;

public class TodayShiftScreen extends Activity implements TodayShiftCallBack,RequestShiftCallBack, TodayShiftActionCallBack {

	private ListView shiftList;
	private ImageButton mBackBtn;
	private PastShiftAsyncTask myTask;
	private TodaysShiftAdapter mAdapter;
	private int totalPages;
	private int currentPage = 1;
	private boolean bolListScroll = true;
	private TextView titleTextView;
    public int shift_id = -1, center_id = -1;
    private boolean isFirstTimeCall = true;
	private Validation validation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_break_shift);
		Common._isClassCommentOpen = true;
		createViews();
			
	}

	private void createViews() {
		validation = new Validation(TodayShiftScreen.this);
		titleTextView = (TextView) findViewById(R.id.break_shift_title);
		titleTextView.setText("Today's Shifts");
		shiftList = (ListView) findViewById(R.id.break_history_list);
		mBackBtn = (ImageButton) findViewById(R.id.break_shift_back_btn);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			}
		});
		
		shiftList.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			 if (shiftList.getLastVisiblePosition() == mAdapter.getCount() - 1 && bolListScroll && currentPage <= totalPages) {
				  bolListScroll = false;
				  currentPage++;
				  callTodayShiftService();
			   }
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

			}
		});
		callTodayShiftService();

	}
	
	public void callTodayShiftService() {
	
		if (!validation.checkNetworkRechability()) {
		   Common.displayAlertButtonToFinish(TodayShiftScreen.this,"Message", 
				   getResources().getString(R.string.no_internet),true);
		} else {
		   myTask = new PastShiftAsyncTask(this);
		   myTask.setCallBack(this);
		   myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "present",currentPage + "");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Common._isClassCommentOpen = false;
		
		if(Common.EDUCATOR_ENTER_RANGE && mAdapter != null)
			mAdapter.notifyDataSetChanged();
		else {
			if(mAdapter != null)
			   mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void requestTodayShiftCallBackList(String strResponce) {
		
		if (mAdapter == null) {
			ArrayList<EducatoreShift> list = new ArrayList<EducatoreShift>();
			list.addAll(parseBreakHistoty(strResponce));
			mAdapter = new TodaysShiftAdapter(TodayShiftScreen.this, list);
			shiftList.setAdapter(mAdapter);
		} else {
			mAdapter.addItems(parseBreakHistoty(strResponce));
		}
	}
	
	public void beaconsRangeToRefreshList() {
		
		if(Common.REFRESH_SHIFT_LIST && mAdapter != null) {
			Common.REFRESH_SHIFT_LIST = false;
			mAdapter.notifyDataSetChanged();
		}
	}

	private ArrayList<EducatoreShift> parseBreakHistoty(String responce) {

		ArrayList<EducatoreShift> tempList = new ArrayList<EducatoreShift>();
		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					totalPages = resultObj.getInt("total_pages");
					JSONArray historyarray = resultObj.getJSONArray("educator_shift");

					for (int i = 0; i < historyarray.length(); i++) {
						JSONObject historyObj = historyarray.getJSONObject(i);
						EducatoreShift obj = new EducatoreShift();
						obj.shift_id = historyObj.getInt("shift_id");
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
						obj.break_status = historyObj.getString("break_status");
						obj.shift_status = historyObj.getString("shift_status");
						tempList.add(obj);
					}
					bolListScroll = true;
				} else {
					AlertDialogManagerWithCloseActivity alert = new AlertDialogManagerWithCloseActivity();
				     alert.showAlertDialog(TodayShiftScreen.this, "Message",resultObj.getString("message"), isFirstTimeCall);
				     if(isFirstTimeCall)
				        isFirstTimeCall = false;
					// for unsucess
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tempList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xplor.interfaces.RequestShiftCallBack#SendResponce(java.lang.String)
	 */
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
					displayAlertPastShift(TodayShiftScreen.this, "Message",resultObj.getString("message"));
				} else {
					AlertDialogManagerWithCloseActivity alert = new AlertDialogManagerWithCloseActivity();
				     alert.showAlertDialog(TodayShiftScreen.this, "Message",
				       resultObj.getString("message"), isFirstTimeCall);
				     if(isFirstTimeCall)
				      isFirstTimeCall = false;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void displayAlertPastShift(Context mContext, String strTitle,String strSMS) {

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
					currentPage = 1;
					mAdapter = null;
				  if(!validation.checkNetworkRechability()) {
					  Toast.makeText(TodayShiftScreen.this, TodayShiftScreen.this.getResources().getString(R.string.no_internet), 1500).show();
				  } else {
					 myTask = new PastShiftAsyncTask(TodayShiftScreen.this);
					 myTask.setCallBack(TodayShiftScreen.this);
				     myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "present",currentPage + "");
				  }
					dialog.dismiss();
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xplor.interfaces.TodayShiftActionCallBack#SendTodayShiftActionResponce
	 * (java.lang.String)
	 */
	@Override
	public void requestTodayShiftActionResponce(String responce) {
	
		try {
			JSONObject jObj = new JSONObject(responce);

			if (jObj.has("result")) {
				JSONObject resultObj = jObj.getJSONObject("result");
				if (resultObj.has("status") && resultObj.getString("status").equals("success")) {
					Common.displayAlertSingle(TodayShiftScreen.this, "Message", resultObj.getString("message"));
					currentPage = 1;
					mAdapter = null;
					myTask = new PastShiftAsyncTask(this);
					myTask.setCallBack(this);
					myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"present", currentPage + "");

				} else {
					int confirmation = resultObj.getInt("confirmation");
					if(confirmation == 1) {
						Common.isDisplayMessage_Called = false;
						displayCancelShiftAlert(TodayShiftScreen.this,"Message",resultObj.getString("message"),resultObj.getString("shift_id"));
					} else {
						Common.displayAlertSingle(TodayShiftScreen.this, "Message", resultObj.getString("message"));
					}
				  }
		   }
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void displayCancelShiftAlert(final Context mContext,String strTitle, String strSMS,final String strShift_Id) {

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
			
			TextView txtSms = (TextView) dialog.findViewById(R.id.Popup_Message_Txt);
			txtSms.setText(strSMS);
			
			if (!dialog.isShowing())
				dialog.show();

			Button btnYes = (Button) dialog.findViewById(R.id.Popup_Yes_Btn);
			btnYes.setText("Cancel");
			btnYes.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				}
			});

			Button btnNo = (Button) dialog.findViewById(R.id.Popup_No_Btn);
			btnNo.setText("Start");
			btnNo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Common.isDisplayMessage_Called = false;
					dialog.dismiss();
				  if(!validation.checkNetworkRechability()) {
				     Toast.makeText(TodayShiftScreen.this, TodayShiftScreen.this.getResources().getString(R.string.no_internet), 1500).show();
				  } else {
					TodayShiftActionsAsyncTask mTodayShistActionsasyncTask = new TodayShiftActionsAsyncTask(TodayShiftScreen.this,"Yes");
					mTodayShistActionsasyncTask.setCallBack(((TodayShiftScreen) mContext));
					mTodayShistActionsasyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebUrls.EDUCATOR_SHIFT_START_URL, strShift_Id);
				  }
				}
			});
		}
	}
	
	public void displaySwapShiftAlert(Context mContext,String strTitle, String strSMS) {

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
				      if(!validation.checkNetworkRechability()) {
						Toast.makeText(TodayShiftScreen.this, TodayShiftScreen.this.getResources().getString(R.string.no_internet), 1500).show();
					  } else {
						CancelShiftAsyncTask mCancelShiftAsyncTask = new CancelShiftAsyncTask(TodayShiftScreen.this);
						mCancelShiftAsyncTask.setCallBack(TodayShiftScreen.this);
						mCancelShiftAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, center_id + "",shift_id + "");
					  }
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
