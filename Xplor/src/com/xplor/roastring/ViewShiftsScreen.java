package com.xplor.roastring;

import com.xplor.adaptor.RosterItemAdapter;
import com.xplor.dev.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ViewShiftsScreen extends Activity {

	private String[] lvMenuItems = { "Upcoming Shifts", "Today's Shifts","Past Shifts" };
	private ListView options;
	private ImageButton mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_shift);

		options = (ListView) findViewById(R.id.view_shift_option_list);
		options.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				onMenuItemClick(parent, view, position, id);
			}

		});

		mBackBtn = (ImageButton) findViewById(R.id.view_shift_back_btn);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
			}
		});

	}

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
		RosterItemAdapter mRosterItemAdapter = new RosterItemAdapter(ViewShiftsScreen.this, lvMenuItems);
		options.setAdapter(mRosterItemAdapter);
	}

	// Perform action when a menu item is clicked
	private void onMenuItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		Intent in;
		String selectedItem = lvMenuItems[position];

		if (selectedItem.compareTo("Upcoming Shifts") == 0) {
			in = new Intent(ViewShiftsScreen.this, UpcomingShiftScreen.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
		} else if (selectedItem.compareTo("Today's Shifts") == 0) {

			in = new Intent(ViewShiftsScreen.this, TodayShiftScreen.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
			overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
		} else if (selectedItem.compareTo("Past Shifts") == 0) {
			in = new Intent(ViewShiftsScreen.this, PastShiftScreen.class);
			startActivity(in);
			overridePendingTransition(R.anim.slide_for_in, R.anim.slide_for_out);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
	}
}
