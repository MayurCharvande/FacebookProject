package com.xplor.roastring;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.xplor.Model.EducatoreShift;
import com.xplor.adaptor.CalendarAdapter;
import com.xplor.adaptor.EducatorePastShiftAdapter;
import com.xplor.async_task.EducatorAllShiftAsyncTask;
import com.xplor.async_task.EducatorDateShiftAsyncTask;
import com.xplor.async_task.EducatorLeaveAsyncTask;
import com.xplor.common.Common;
import com.xplor.dev.R;
import com.xplor.interfaces.EducatorAllShiftCallBack;
import com.xplor.interfaces.EducatorLeaveCallBack;
import com.xplor.interfaces.EducatorShiftStartEndCallBack;

@SuppressLint("SimpleDateFormat") 
public class CalendarView extends Activity implements EducatorShiftStartEndCallBack,EducatorAllShiftCallBack,EducatorLeaveCallBack {

	private GregorianCalendar month;// calendar instances.
	private CalendarAdapter adapter;// adapter instance
	private Handler handler;// for grabbing some event values for showing the dot marker.
	private ArrayList<String> items; 
	private ListView listDateDetail;
	private ImageButton mBackBtn;
	private EducatorDateShiftAsyncTask myTask;
	private EducatorePastShiftAdapter mAdapter;
	
	//private boolean bolListScroll = true;
	private String selectedGridDate;
	private ArrayList<EducatoreShift> allShiftList = null;
	private ArrayList<EducatoreShift> leaveShiftArrayList = new ArrayList<EducatoreShift>();
	private GridView gridview;
	private TextView txtShiftDate = null;
	private View viewLine = null;
	private View viewLineBottom = null;
	private LinearLayout calender_leave_layout = null;
	private TextView txtLeaveType = null;
	private TextView txtLeaveDescription = null;
	private ArrayList<EducatoreShift> listData = null;
	private Activity mActivity = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		mActivity = CalendarView.this;
		Locale.setDefault(Locale.US);
        
		createsView();
		setClickListners();
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	mActivity = CalendarView.this;
    }
	
	private void createsView() {
		
		allShiftList = new ArrayList<EducatoreShift>();
		month = (GregorianCalendar) GregorianCalendar.getInstance();
		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month, allShiftList);
		
		viewLine = (View) findViewById(R.id.Center_Line);
		viewLine.setVisibility(View.GONE);
		viewLineBottom = (View) findViewById(R.id.Bottom_Line);
		viewLineBottom.setVisibility(View.GONE);
		txtShiftDate = (TextView) findViewById(R.id.calender_shift_date);
		txtShiftDate.setText("");
		
		listDateDetail = (ListView) findViewById(R.id.calender_shift_list);
		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);
		
		calender_leave_layout = (LinearLayout) findViewById(R.id.calender_leave_layout);
		txtLeaveType = (TextView) findViewById(R.id.calender_leave_type);
		txtLeaveDescription = (TextView) findViewById(R.id.calender_leave_description);
		calender_leave_layout.setVisibility(View.GONE);
		handler = new Handler();
		handler.post(calendarUpdater);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		
	}
	
	private void setClickListners() {
		
		RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
				txtShiftDate.setText("");
				viewLine.setVisibility(View.GONE);
				if(listData != null)
				   listData.clear();
				
				calender_leave_layout.setVisibility(View.GONE);
				listDateDetail.setVisibility(View.GONE);
			}
		});

		RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();
				txtShiftDate.setText("");
				viewLine.setVisibility(View.GONE);
				calender_leave_layout.setVisibility(View.GONE);
				listDateDetail.setVisibility(View.GONE);
				if(listData != null)
				   listData.clear();
			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
				
				
				LinearLayout linearLayout=(LinearLayout)gridview.getChildAt(position);
				LinearLayout linearLayoutInner=(LinearLayout)linearLayout.getChildAt(1);
				TextView textView=(TextView)linearLayoutInner.getChildAt(0);
				
				((CalendarAdapter) parent.getAdapter()).setSelected(v,false);
				
				selectedGridDate = CalendarAdapter.dayString.get(position);
				txtShiftDate.setText(Common.convertDateFormat(selectedGridDate));
				viewLine.setVisibility(View.VISIBLE);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*","");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
			  
			  if(textView.getText().toString().trim().equals("L")) {
				  calender_leave_layout.setVisibility(View.VISIBLE);
				  listDateDetail.setVisibility(View.GONE);
				  String strType = "",strDesc="";
				  for(int i=0;i<leaveShiftArrayList.size();i++) {
					  EducatoreShift educatoreShift=(EducatoreShift)leaveShiftArrayList.get(i);
					  if(educatoreShift.leave_start_date.equals(selectedGridDate)) {
						  strType = educatoreShift.leave_reason;
						  strDesc = educatoreShift.leave_discription;
						  break;
					  }
				  }
				  viewLineBottom.setVisibility(View.VISIBLE);
				  txtLeaveType.setText(strType);
				  txtLeaveDescription.setText(strDesc);
			  } else {
				calender_leave_layout.setVisibility(View.GONE);
				listDateDetail.setVisibility(View.VISIBLE);
				// callHomeListService(strTypes);
				myTask = new EducatorDateShiftAsyncTask(CalendarView.this,selectedGridDate);
				myTask.setCallBack(CalendarView.this);
				myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,selectedGridDate);
			  }
			}
		});

		listDateDetail.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

//			   if (listDateDetail.getLastVisiblePosition() == mAdapter.getCount() - 1 && bolListScroll) {
//					bolListScroll = false;
//					// callHomeListService(strTypes);
//					myTask = new EducatorDateShiftAsyncTask(CalendarView.this,"");
//					myTask.setCallBack(CalendarView.this);
//					myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,selectedGridDate);
//			    }
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			
			}
		});

		mBackBtn = (ImageButton) findViewById(R.id.calender_back_btn);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			}
		});
	
		 EducatorAllShiftAsyncTask myTask = new EducatorAllShiftAsyncTask(CalendarView.this);
		 myTask.setCallBack(CalendarView.this);
		 myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	@Override
	public void requestSetAllShift(ArrayList<EducatoreShift> arrAllShiftList) {
		
	    if(arrAllShiftList != null) {
		   allShiftList.clear(); 
		   allShiftList.addAll(arrAllShiftList);
	    } 
	  
	    EducatorLeaveAsyncTask myTask = new EducatorLeaveAsyncTask(CalendarView.this);
		myTask.setCallBack(CalendarView.this);
		myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void requestSetEducatorLeave(ArrayList<EducatoreShift> arrAllShiftList) {
		
		if(arrAllShiftList != null) {
			allShiftList.addAll(arrAllShiftList);
			leaveShiftArrayList.addAll(arrAllShiftList);
		} 
		adapter.notifyDataSetChanged();
	}

	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	protected void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	public void refreshCalendar() {
		
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items
	}

	public Runnable calendarUpdater = new Runnable() {
		@Override
		public void run() {
			items.clear();
			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void requestEducatorShiftStartEndList(ArrayList<EducatoreShift> arrAllShiftList) {
		
		if(arrAllShiftList != null) {
		  if (mAdapter == null) {
			listData = new ArrayList<EducatoreShift>();
			listData.addAll(arrAllShiftList);
			mAdapter = new EducatorePastShiftAdapter(CalendarView.this, listData);
			if(listData.size() > 0) {
			   viewLineBottom.setVisibility(View.VISIBLE);
			}
			listDateDetail.setAdapter(mAdapter);
		  } else {
			if(mAdapter.getCount() > 0) {
			   viewLineBottom.setVisibility(View.VISIBLE);
			}
			listData.clear();
			listData.addAll(arrAllShiftList);
			//mAdapter.addItems(listData);
			mAdapter.notifyDataSetChanged();
		 }
	   } else { 
		  if(listData != null) {
		    listData.clear();
		    mAdapter.notifyDataSetChanged();
		  }
		  viewLineBottom.setVisibility(View.GONE);
		  
		  Common.displayAlertSingle(mActivity, getResources().getString(R.string.str_alert),
				  getResources().getString(R.string.str_shift_not));
	   }
	}

	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}
}
