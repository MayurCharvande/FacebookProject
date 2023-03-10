package com.xplor.common;

import java.util.Calendar;


import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class DatePickerDailog extends Dialog {

	private Context Mcontex;
	private int NoOfYear = 40; 
	
	public DatePickerDailog(Context context, Calendar calendar,
			final DatePickerListner dtp) {

		super(context);
		Mcontex = context;
		LinearLayout lytmain = new LinearLayout(Mcontex);
		lytmain.setOrientation(LinearLayout.VERTICAL);
		LinearLayout lytdate = new LinearLayout(Mcontex);
		LinearLayout lytbutton = new LinearLayout(Mcontex);

		Button btnset = new Button(Mcontex);
		Button btncancel = new Button(Mcontex);

		btnset.setText("Set");
		btncancel.setText("Cancel");

		final WheelView month = new WheelView(Mcontex);
		final WheelView year = new WheelView(Mcontex);
		final WheelView day = new WheelView(Mcontex);

		lytdate.addView(day, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
		lytdate.addView(month, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));
		lytdate.addView(year, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		lytbutton.addView(btnset, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

		lytbutton.addView(btncancel, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		lytbutton.setPadding(5, 5, 5, 5);
		lytmain.addView(lytdate);
		lytmain.addView(lytbutton);

		setContentView(lytmain);

		getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(year, month, day);
			}
		};
		int setday = 0,setmonth =0,setyear=0;
	   if(Common.CHILD_DOB.length() > 0) {
		  String [] arrDate = Common.CHILD_DOB.split("/");
		  setday = (Integer.parseInt(arrDate[0])-1);
		  setmonth = (Integer.parseInt(arrDate[1])-1);
		  setyear = Integer.parseInt(arrDate[2]);
		}
		 System.out.println("setday ="+setday+"setmonth ="+setmonth+"setyear ="+setyear);

		// month
		int curMonth = calendar.get(Calendar.MONTH);
		String months[] = new String[] { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
		month.setCurrentItem(setmonth);
		month.addChangingListener(listener);

		Calendar cal = Calendar.getInstance();
		// year
		//int curYear = calendar.get(Calendar.YEAR);
		int Year = cal.get(Calendar.YEAR);
		
		year.setViewAdapter(new DateNumericAdapter(context, Year - NoOfYear,Year + NoOfYear, NoOfYear));
		year.setCurrentItem(setyear-(Year-NoOfYear));
		//year.setCurrentItem(setyear);
		year.addChangingListener(listener);

		// day
		updateDays(year, month, day);
		day.setCurrentItem(setday);

		btnset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = updateDays(year, month, day);
				dtp.OnDoneButton(DatePickerDailog.this, c);
			}
		});
		btncancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dtp.OnCancelButton(DatePickerDailog.this);

			}
		});

	}

	Calendar updateDays(WheelView year, WheelView month, WheelView day) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR) + (year.getCurrentItem()-NoOfYear));
		calendar.set(Calendar.MONTH, month.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		day.setViewAdapter(new DateNumericAdapter(Mcontex, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
		calendar.set(Calendar.DAY_OF_MONTH, curDay);
		
		return calendar;

	}

	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem;
		int currentValue;

		public DateNumericAdapter(Context context, int minValue, int maxValue,
				int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	private class DateArrayAdapter extends ArrayWheelAdapter<String> {
		int currentItem;
		int currentValue;

		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	public interface DatePickerListner {
		public void OnDoneButton(Dialog datedialog, Calendar c);

		public void OnCancelButton(Dialog datedialog);
	}
}
