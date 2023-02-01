package com.xplor.roastring;

import com.xplor.adaptor.RosterItemAdapter;
import com.xplor.dev.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


@SuppressLint("InflateParams") 
public class RosterFragment extends Fragment {

	private String[] lvMenuItems;
	private ListView options;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.roster_fragment, null);

		lvMenuItems = getResources().getStringArray(R.array.roster_items);
		options = (ListView) view.findViewById(R.id.roster_option_list);
		options.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				onMenuItemClick(parent, view, position, id);
			}

		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		RosterItemAdapter mRosterItemAdapter = new RosterItemAdapter(getActivity(), lvMenuItems);
		options.setAdapter(mRosterItemAdapter);

	}

	// Perform action when a menu item is clicked
	private void onMenuItemClick(AdapterView<?> parent, View view,int position, long id) {
		Intent in;
		String selectedItem = lvMenuItems[position];

		if (selectedItem.compareTo("Calendar") == 0) {
			in = new Intent(getActivity(), CalendarView.class);
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);
		} else if (selectedItem.compareTo("View Shifts") == 0) {

			in = new Intent(getActivity(), ViewShiftsScreen.class);
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);
		} else if (selectedItem.compareTo("Available Shifts") == 0) {

			in = new Intent(getActivity(), AvailableShiftScreen.class);
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);

		} else if (selectedItem.compareTo("Break History") == 0) {
			in = new Intent(getActivity(), BreakHistoryScreen.class);
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.slide_for_in,R.anim.slide_for_out);
		}
	}
}