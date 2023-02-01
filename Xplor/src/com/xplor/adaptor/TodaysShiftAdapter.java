package com.xplor.adaptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.xplor.Model.EducatoreShift;
import com.xplor.async_task.TodayShiftActionsAsyncTask;
import com.xplor.common.Common;
import com.xplor.common.WebUrls;
import com.xplor.dev.R;
import com.xplor.roastring.TodayShiftScreen;

@SuppressLint({ "InflateParams", "SimpleDateFormat" }) 
public class TodaysShiftAdapter extends BaseAdapter {

	Activity _context;
	private ArrayList<EducatoreShift> _listArray = new ArrayList<EducatoreShift>();

	public TodaysShiftAdapter(Activity _contex, ArrayList<EducatoreShift> _list) {
		_context = _contex;
		_listArray = _list;
	}
	
	public class ViewHolder {

		public TextView mDay;
		public TextView mTime;
		public TextView mDuration;
		public TextView mBreakTime;
		public TextView mRoom;
		public Button cancelShiftBtn;
		public Button shiftStartBtn;
		public Button shiftEndBtn;
		public Button startBreakBtn;
		public Button endBreakBtn;
	}

	@Override
	public int getCount() {
		return _listArray.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder _viewHolder = null;
		if (null == convertView) {
			LayoutInflater inflater = _context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.today_shift_item, null);

			_viewHolder = new ViewHolder();
			_viewHolder.mDay = (TextView) convertView.findViewById(R.id.today_shift_day);
			_viewHolder.mTime = (TextView) convertView.findViewById(R.id.today_shift_time);
			_viewHolder.mDuration = (TextView) convertView.findViewById(R.id.today_shift_duration);
			_viewHolder.mBreakTime = (TextView) convertView.findViewById(R.id.today_shift_break);
			_viewHolder.mRoom = (TextView) convertView.findViewById(R.id.today_shift_room);
			
			_viewHolder.cancelShiftBtn = (Button) convertView.findViewById(R.id.today_shift_cancelBtn);
			_viewHolder.shiftStartBtn = (Button) convertView.findViewById(R.id.today_shift_startBtn);
			_viewHolder.shiftEndBtn = (Button) convertView.findViewById(R.id.today_shift_end_shiftBtn);
			_viewHolder.startBreakBtn = (Button) convertView.findViewById(R.id.today_shift_start_breakBtn);
			_viewHolder.endBreakBtn = (Button) convertView.findViewById(R.id.today_shift_end_breakBtn);

			convertView.setTag(_viewHolder);
		}

		 _viewHolder = (ViewHolder) convertView.getTag();
		String timeStart = Common.convert24HrsFormateTo12Hrs(_listArray.get(position).shift_start_time);
		String endStart = Common.convert24HrsFormateTo12Hrs(_listArray.get(position).shift_end_time);
		String tempStartTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).shift_date+ " " + timeStart);
		String tempEndTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).shift_date + " " + endStart);
		String[] startTime = tempStartTime.split(" ");
		String[] endTime = tempEndTime.split(" ");
		//_viewHolder.mDay.setText(startTime[0] + " " + startTime[1]);
		//_viewHolder.mTime.setText(startTime[2] + " " + startTime[3] + " - "+ endTime[2] + " " + endTime[3]);
		//_viewHolder.mBreakTime.setText("Break Interval : "+ _listArray.get(position).break_hours);
		
		_viewHolder.mDay.setText(startTime[0] + " " + startTime[1]);
		_viewHolder.mTime.setText("Shift Time : "+startTime[2] + " " + startTime[3]+" to "+endTime[2]+" "+endTime[3]);
		if(_listArray.get(position).shift_Hours.length() > 0)
		_viewHolder.mDuration.setText("Duration : "+_listArray.get(position).shift_Hours+":"+_listArray.get(position).shift_Minutes+ " (Hrs:Min)");
		else _viewHolder.mDuration.setText("Duration : -:- (Hrs:Min)");
		_viewHolder.mBreakTime.setText("Break Hours : "+ _listArray.get(position).break_hours);
		if(_listArray.get(position).shift_Room.trim().length() > 0)
		_viewHolder.mRoom.setText("Room : "+_listArray.get(position).shift_Room);
		else _viewHolder.mRoom.setText("Room : NA");
		
		_viewHolder.cancelShiftBtn.setVisibility(View.GONE);
		_viewHolder.shiftStartBtn.setVisibility(View.GONE);
		_viewHolder.shiftEndBtn.setVisibility(View.GONE);
		_viewHolder.startBreakBtn.setVisibility(View.GONE);
		_viewHolder.endBreakBtn.setVisibility(View.GONE);

		//Date shiftStartDate = getDate(tempStartTime);
		//Date shiftEndDate = getDate(tempEndTime);

		if (_listArray.get(position).shift_status.trim().equals("Started")
				&& _listArray.get(position).break_status.trim().equals("Not On Break")) {
			_viewHolder.startBreakBtn.setVisibility(View.VISIBLE);
		} else if (_listArray.get(position).shift_status.trim().equals("Started")
				&& _listArray.get(position).break_status.trim().equals("Break Started")) {
			_viewHolder.endBreakBtn.setVisibility(View.VISIBLE);
		}
		
		if (_listArray.get(position).shift_status.trim().equals("Not Start")) {
			System.out.println("Common.EDUCATOR_ENTER_RANGE ="+Common.EDUCATOR_ENTER_RANGE);
			if (Common.EDUCATOR_ENTER_RANGE) {
				_viewHolder.shiftStartBtn.setVisibility(View.VISIBLE);
			} else {
				_viewHolder.shiftStartBtn.setVisibility(View.GONE);
			}
			_viewHolder.cancelShiftBtn.setVisibility(View.GONE);

		} else if (_listArray.get(position).shift_status.trim().equals("Started")) {
			_viewHolder.shiftStartBtn.setVisibility(View.GONE);
			_viewHolder.cancelShiftBtn.setVisibility(View.GONE);
			_viewHolder.shiftEndBtn.setVisibility(View.VISIBLE);
		} else {

//			Calendar cal = Calendar.getInstance();
//			cal.setTime(shiftStartDate);
//			cal.add(Calendar.MINUTE, -5);
			System.out.println("Common.EDUCATOR_ENTER_RANGE ="+Common.EDUCATOR_ENTER_RANGE);
			if (Common.EDUCATOR_ENTER_RANGE) {//(calculateTimeDifference(Calendar.getInstance().getTime(),cal.getTime()) / 60 < 5) {
				_viewHolder.shiftStartBtn.setVisibility(View.VISIBLE);
			} else {
				_viewHolder.shiftStartBtn.setVisibility(View.GONE);
			}
		}

//		if (!_listArray.get(position).shift_status.trim().equals("Started")) {
//			if (calculateTimeDifference(shiftEndDate, Calendar.getInstance().getTime()) > 0) {
//				_viewHolder.cancelShiftBtn.setVisibility(View.GONE);
//				_viewHolder.shiftStartBtn.setVisibility(View.GONE);
//				_viewHolder.shiftEndBtn.setVisibility(View.GONE);
//				_viewHolder.startBreakBtn.setVisibility(View.GONE);
//				_viewHolder.endBreakBtn.setVisibility(View.GONE);
//			}
//		}

		if (_listArray.get(position).shift_status.trim().equals("Completed")) {
			_viewHolder.cancelShiftBtn.setVisibility(View.GONE);
			_viewHolder.shiftStartBtn.setVisibility(View.GONE);
			_viewHolder.shiftEndBtn.setVisibility(View.GONE);
			_viewHolder.startBreakBtn.setVisibility(View.GONE);
			_viewHolder.endBreakBtn.setVisibility(View.GONE);
		}

		_viewHolder.cancelShiftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((TodayShiftScreen) _context).shift_id = _listArray.get(position).shift_id;
				((TodayShiftScreen) _context).center_id = _listArray.get(position).center_id;
				((TodayShiftScreen) _context).displaySwapShiftAlert(((TodayShiftScreen) _context),"message", "Do you want to swap this shift ?");
			}
		});

		_viewHolder.shiftStartBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			  if(shiftStart()) {
				Common.displayAlertSingle(_context, "Message", "Cannot attend multiple shift at same time.");
		      } else {
				TodayShiftActionsAsyncTask mTodayShistActionsasyncTask = new TodayShiftActionsAsyncTask(_context,"No");
				mTodayShistActionsasyncTask.setCallBack(((TodayShiftScreen) _context));
				mTodayShistActionsasyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebUrls.EDUCATOR_SHIFT_START_URL, ""+_listArray.get(position).shift_id);
		      }
			}
		});

		_viewHolder.shiftEndBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				TodayShiftActionsAsyncTask mTodayShistActionsasyncTask = new TodayShiftActionsAsyncTask(_context,"No");
				mTodayShistActionsasyncTask.setCallBack(((TodayShiftScreen) _context));
				mTodayShistActionsasyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebUrls.EDUCATOR_SHIFT_END_URL, ""+_listArray.get(position).shift_id);
			}
		});

		_viewHolder.startBreakBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TodayShiftActionsAsyncTask mTodayShistActionsasyncTask = new TodayShiftActionsAsyncTask(_context,"No");
				mTodayShistActionsasyncTask.setCallBack(((TodayShiftScreen) _context));
				mTodayShistActionsasyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebUrls.SHIFTS_BREAK_START_URL, _listArray.get(position).shift_id + "");
			}
		});

		_viewHolder.endBreakBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TodayShiftActionsAsyncTask mTodayShistActionsasyncTask = new TodayShiftActionsAsyncTask(_context,"No");
				mTodayShistActionsasyncTask.setCallBack(((TodayShiftScreen) _context));
				mTodayShistActionsasyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebUrls.SHIFT_BREAK_END_URL, _listArray.get(position).shift_id + "");
			}
		});

		return convertView;
	}

	public void addItems(ArrayList<EducatoreShift> _list) {
		_listArray.addAll(_list);
		notifyDataSetChanged();
	}
	
	private boolean shiftStart() {
		
		for(int i=0;i<_listArray.size();i++) {
			if (_listArray.get(i).shift_status.equals("Started")) {
				return true;
			}
		}
		return false;
	}

	String convertDateTolocalFormate(String givenDateString) {

		// String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
		// SimpleDateFormat sdf = new
		// SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parsed;
		try {
			parsed = sourceFormat.parse(givenDateString);

			TimeZone tz = TimeZone.getDefault();
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM/yyyy");
			formatter.setTimeZone(tz);

			System.out.println("current date :: =" + formatter.format(parsed));
			return formatter.format(parsed);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}

	public Date getDate(String dateStr) {
		SimpleDateFormat sourceFormat = new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm aa");
		TimeZone tz = TimeZone.getDefault();
		sourceFormat.setTimeZone(tz);
		Date parsed = null;
		try {
		   parsed = sourceFormat.parse(dateStr);
		} catch (java.text.ParseException e) {
			//e.printStackTrace();
		} // => Date is in UTC no

		return parsed;
	}

	long calculateTimeDifference(Date startTime, Date endTime) {

		long diff = endTime.getTime() - startTime.getTime();
		long seconds = diff / 1000;
		return seconds;
	}

}
