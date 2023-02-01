package com.xplor.adaptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xplor.Model.EducatoreShift;
import com.xplor.common.Common;
import com.xplor.dev.R;

@SuppressLint({ "InflateParams", "SimpleDateFormat" }) 
public class EducatorePastShiftAdapter extends BaseAdapter {

	Activity _context;
	private ArrayList<EducatoreShift> _listArray = new ArrayList<EducatoreShift>();
	
	public EducatorePastShiftAdapter(Activity _contex,ArrayList<EducatoreShift> _list) {
		_context = _contex;
		_listArray = _list;
	}
	
	public static class ViewHolder {
		public TextView mDay;
		public TextView mTime;
		public TextView mDuration;
		public TextView mBreakTime;
		public TextView mRoom;
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
		if (null == convertView) {
			LayoutInflater inflater = _context.getLayoutInflater();

			convertView = inflater.inflate(R.layout.break_history_item, null);
			final ViewHolder _viewHolder = new ViewHolder();
			_viewHolder.mDay = (TextView) convertView.findViewById(R.id.break_history_day);
			_viewHolder.mTime = (TextView) convertView.findViewById(R.id.break_history_time);
			_viewHolder.mDuration = (TextView) convertView.findViewById(R.id.break_history_duration);
			_viewHolder.mBreakTime = (TextView) convertView.findViewById(R.id.break_history_brack);
			_viewHolder.mRoom = (TextView) convertView.findViewById(R.id.break_history_room);
			convertView.setTag(_viewHolder);
		}

		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();
		String timeStart = Common.convert24HrsFormateTo12Hrs(_listArray.get(position).shift_start_time);
		String endStart = Common.convert24HrsFormateTo12Hrs(_listArray.get(position).shift_end_time);
		String[] startTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).shift_date + " " + timeStart).split(" ");
		String[] endTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).shift_date + " " + endStart).split(" ");

		_viewHolder.mDay.setText(startTime[0] + " " + startTime[1]);
		//_viewHolder.mTime.setText(startTime[2] + " " + startTime[3] + " - "+ endTime[2] + " " + endTime[3]);
		_viewHolder.mTime.setText("Shift Time : "+startTime[2] + " " + startTime[3]+ " to "+ endTime[2] + " " + endTime[3]);
		if(_listArray.get(position).shift_Hours.length() > 0)
		_viewHolder.mDuration.setText("Duration : "+_listArray.get(position).shift_Hours+":"+_listArray.get(position).shift_Minutes+ " (Hrs:Min)");
		else _viewHolder.mDuration.setText("Duration : -:- (Hrs:Min)");
		_viewHolder.mBreakTime.setText("Break Hours : "+ _listArray.get(position).break_hours);
		if(_listArray.get(position).shift_Room.trim().length() > 0)
		_viewHolder.mRoom.setText("Room : "+_listArray.get(position).shift_Room);
		else _viewHolder.mRoom.setText("Room : NA");

		return convertView;
	}

	public void addItems(ArrayList<EducatoreShift> _list) {
		_listArray.addAll(_list);
		notifyDataSetChanged();
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
			e.printStackTrace();
			return "";
		} // => Date is in UTC no

	}
}
