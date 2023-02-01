package com.xplor.roastring;

import java.util.ArrayList;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.xplor.Model.EducatoreShift;
import com.xplor.adaptor.EducatorePastShiftAdapter;
import com.xplor.async_task.UpcomingShiftAsyncTask;
import com.xplor.dev.R;
import com.xplor.interfaces.EducatorLeaveCallBack;

public class PastShiftScreen extends Activity implements EducatorLeaveCallBack {

	private ListView options;
	private ImageButton mBackBtn;
	private TextView titleTextView;
	private UpcomingShiftAsyncTask myTask;
	private EducatorePastShiftAdapter mAdapter;
	boolean bolListScroll = true;
	boolean isFirstTimeCall = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_break_shift);
		createViews();
	}

	private void createViews() {
		
		titleTextView = (TextView)findViewById(R.id.break_shift_title);
		titleTextView.setText("Past Shifts");
		mBackBtn = (ImageButton) findViewById(R.id.break_shift_back_btn);
		options = (ListView) findViewById(R.id.break_history_list);
		options.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if(options.getLastVisiblePosition() == mAdapter.getCount() - 1 && bolListScroll) {
//					bolListScroll = false;
//					myTask = new UpcomingShiftAsyncTask(PastShiftScreen.this,"Past");
//					myTask.setCallBack(PastShiftScreen.this);
//					myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
		
		myTask = new UpcomingShiftAsyncTask(this,"Past");
		myTask.setCallBack(this);
		myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}

	@Override
	public void requestSetEducatorLeave(ArrayList<EducatoreShift> allShiftList) {
		
		if (mAdapter == null) {
			ArrayList<EducatoreShift> list = new ArrayList<EducatoreShift>();
			list.addAll(allShiftList);
			mAdapter = new EducatorePastShiftAdapter(PastShiftScreen.this, list);
			options.setAdapter(mAdapter);
		} else {
			//mAdapter.addItems(allShiftList);
			mAdapter.notifyDataSetChanged();
		}
		bolListScroll = true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}

}
