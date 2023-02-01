package com.xplor.adaptor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import com.xplor.Model.EducatoreShift;
import com.xplor.dev.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class CalendarAdapter extends BaseAdapter {
	private Context mContext;

	private java.util.Calendar month;
	public GregorianCalendar pmonth; // calendar instance for previous month
	/**
	 * calendar instance for previous month for getting complete view
	 */
	public GregorianCalendar pmonthmaxset;
	private GregorianCalendar selectedDate;
	int firstDay;
	int maxWeeknumber;
	int maxP;
	int calMaxP;
	int lastWeekDay;
	int leftDays;
	int mnthlength;
	String itemvalue, curentDateString;
	DateFormat df;

	private ArrayList<String> items;
	public static List<String> dayString;
	private View previousView;
	private ArrayList<EducatoreShift> allShiftList;

	public CalendarAdapter(Context context, GregorianCalendar monthCalendar,ArrayList<EducatoreShift> shiftList) {
		CalendarAdapter.dayString = new ArrayList<String>();
		Locale.setDefault(Locale.US);
		month = monthCalendar;
		selectedDate = (GregorianCalendar) monthCalendar.clone();
		mContext = context;
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		this.items = new ArrayList<String>();
		df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		curentDateString = df.format(selectedDate.getTime());
		allShiftList = shiftList;
		refreshDays();
	}

	public ArrayList<EducatoreShift> getAllShiftList() {
		return allShiftList;
	}

	public void setAllShiftList(ArrayList<EducatoreShift> allShiftList) {
		this.allShiftList = allShiftList;
	}

	public void setItems(ArrayList<String> items) {
		for (int i = 0; i != items.size(); i++) {
			if (items.get(i).length() == 1) {
				items.set(i, "0" + items.get(i));
			}
		}
		this.items = items;
	}

	public int getCount() {
		return dayString.size();
	}

	public Object getItem(int position) {
		return dayString.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new view for each item referenced by the Adapter
	public View getView(int position, View view, ViewGroup parent) {
		View conView = view;
		TextView dayView;
		LinearLayout shiftIndicator;
		TextView txtshiftIndicator;
		if (view == null) { // if it's not recycled, initialize some attributes
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			conView = vi.inflate(R.layout.calendar_item, null);
		}
		dayView = (TextView) conView.findViewById(R.id.date);
		shiftIndicator = (LinearLayout) conView.findViewById(R.id.layout_Shift_Indicator);
		txtshiftIndicator = (TextView) conView.findViewById(R.id.txt_Shift_Indicator);
		// separates daystring into parts.
		String[] separatedTime = dayString.get(position).split("-");
		// taking last part of date. ie; 2 from 2012-12-02
		String gridvalue = separatedTime[2].replaceFirst("^0*", "");
		// checking whether the day is in current month or not.
		if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
			// setting offdays to white color.
			dayView.setTextColor(Color.LTGRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
			dayView.setTextColor(Color.LTGRAY);
			dayView.setClickable(false);
			dayView.setFocusable(false);
		} else {
			// setting current month's days in blue color.
			dayView.setTextColor(Color.BLACK);
		}

		if (dayString.get(position).equals(curentDateString)) {
			setSelected(conView,true);
		} else {
			conView.setBackgroundColor(Color.WHITE);
		}
		dayView.setText(gridvalue);

		// create date string for comparison
		String date = dayString.get(position);
		if (date.length() == 1) {
			date = "0" + date;
		}
		String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}
		shiftIndicator.setVisibility(View.INVISIBLE);
		txtshiftIndicator.setVisibility(View.INVISIBLE);
		
		for (EducatoreShift obj : allShiftList) {
			if (dayString.get(position).equals(obj.shift_date)) {
				shiftIndicator.setVisibility(View.VISIBLE);
				shiftIndicator.setBackgroundColor(mContext.getResources().getColor(R.color.calender_back_green));
				txtshiftIndicator.setBackgroundResource(R.drawable.light_blue_oval);
				txtshiftIndicator.setVisibility(View.VISIBLE);
				txtshiftIndicator.setText("S");
				break;
			} else if (dayString.get(position).equals(obj.leave_start_date)) {
				  shiftIndicator.setVisibility(View.VISIBLE);
				  shiftIndicator.setBackgroundColor(mContext.getResources().getColor(R.color.red_cal_leavebg_color));
				  txtshiftIndicator.setBackgroundResource(R.drawable.light_red_oval);
				  txtshiftIndicator.setVisibility(View.VISIBLE);
				  txtshiftIndicator.setText("L");
			}
		}
		 
		
		return conView;
	}

	public View setSelected(View view, boolean isTodayDate) {
		  if (previousView != null) {
		      previousView.setBackgroundColor(Color.WHITE);
		  }
		  if (!isTodayDate) {
		   previousView = view;
		   view.setBackgroundResource(R.color.Gray_Light_Calendar);
		  } else   
		  view.setBackgroundResource(R.color.Gray_Dark_Calendar);
		  return view;
		 }

	public void refreshDays() {
		// clear items
		items.clear();
		dayString.clear();
		Locale.setDefault(Locale.US);
		pmonth = (GregorianCalendar) month.clone();
		// month start day. ie; sun, mon, etc
		firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
		// finding number of weeks in current month.
		maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		// allocating maximum row number for the gridview.
		mnthlength = maxWeeknumber * 7;
		maxP = getMaxP(); // previous month maximum day 31,30....
		calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
		/**
		 * Calendar instance for getting a complete gridview including the three
		 * month's (previous,current,next) dates.
		 */
		pmonthmaxset = (GregorianCalendar) pmonth.clone();
		/**
		 * setting the start date as previous month's required date.
		 */
		pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

		/**
		 * filling calendar gridview.
		 */
		for (int n = 0; n < mnthlength; n++) {
			itemvalue = df.format(pmonthmaxset.getTime());
			pmonthmaxset.add(GregorianCalendar.DATE, 1);
			dayString.add(itemvalue);
		}
	}

	private int getMaxP() {
		int maxP;
		if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
			pmonth.set((month.get(GregorianCalendar.YEAR) - 1),month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			pmonth.set(GregorianCalendar.MONTH,month.get(GregorianCalendar.MONTH) - 1);
		}
		maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

		return maxP;
	}

}