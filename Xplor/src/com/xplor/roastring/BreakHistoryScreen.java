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
import com.xplor.Model.EducatoreBreakHistory;
import com.xplor.adaptor.BreakHistoryAdapter;
import com.xplor.async_task.BreakHistoryAsyncTask;
import com.xplor.dev.R;
import com.xplor.interfaces.BreakHistotyCallBack;

public class BreakHistoryScreen extends Activity implements BreakHistotyCallBack {

	private ListView options;
	private ImageButton mBackBtn;
	
	private BreakHistoryAdapter mAdapter;
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
		
		options = (ListView) findViewById(R.id.break_history_list);
		options.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

//				if (options.getLastVisiblePosition() == mAdapter.getCount() - 1 && bolListScroll) {
//					bolListScroll = false;
//					BreakHistoryAsyncTask myTask = new BreakHistoryAsyncTask(BreakHistoryScreen.this);
//					myTask.setCallBack(BreakHistoryScreen.this);
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

		BreakHistoryAsyncTask myTask = new BreakHistoryAsyncTask(this);
		myTask.setCallBack(this);
		myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void requestBreakHistoryList(ArrayList<EducatoreBreakHistory> arrayBreakHistory) {

		 if (mAdapter == null) {
			ArrayList<EducatoreBreakHistory> list = new ArrayList<EducatoreBreakHistory>();
			list.addAll(arrayBreakHistory);
			mAdapter = new BreakHistoryAdapter(BreakHistoryScreen.this, list);
			options.setAdapter(mAdapter);
		 } else {
			//mAdapter.addItems(arrayBreakHistory);
			 mAdapter.notifyDataSetChanged();
		 }
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}

}
