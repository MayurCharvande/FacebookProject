package com.xplor.adaptor;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xplor.Model.EducatoreBreakHistory;
import com.xplor.common.Common;
import com.xplor.dev.R;

@SuppressLint("InflateParams") 
public class BreakHistoryAdapter extends BaseAdapter {

	Activity _context;
	private ArrayList<EducatoreBreakHistory> _listArray = new ArrayList<EducatoreBreakHistory>();

	static class ViewHolder {

		public TextView mDay;
		public TextView mTime;
		public TextView mTotalTime;
		public TextView mDuration;
		public TextView mRoom;
	}

	public BreakHistoryAdapter(Activity _contex,ArrayList<EducatoreBreakHistory> _list) {
		_context = _contex;
		_listArray = _list;
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
			convertView = inflater.inflate(R.layout.break_history_item, null,true);

			final ViewHolder _viewHolder = new ViewHolder();
			_viewHolder.mDay = (TextView) convertView.findViewById(R.id.break_history_day);
			_viewHolder.mTime = (TextView) convertView.findViewById(R.id.break_history_time);
			_viewHolder.mDuration = (TextView) convertView.findViewById(R.id.break_history_duration);
			_viewHolder.mTotalTime = (TextView) convertView.findViewById(R.id.break_history_brack);
			_viewHolder.mRoom = (TextView) convertView.findViewById(R.id.break_history_room);
			convertView.setTag(_viewHolder);
		}

		final ViewHolder _viewHolder = (ViewHolder) convertView.getTag();
		_viewHolder.mDuration.setVisibility(View.GONE);
		_viewHolder.mRoom.setVisibility(View.GONE);
		
		String[] startTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).start_break_time).split(" ");
		String[] endTime = Common.convertDateAndTimeTolocalFormat(_listArray.get(position).end_break_time).split(" ");
		_viewHolder.mDay.setText(startTime[0] + " " + startTime[1]);
		_viewHolder.mTime.setText(startTime[2] + " " + startTime[3] + " - "+ endTime[2] + " " + endTime[3]);
		_viewHolder.mTotalTime.setText("Total Time : "+ calculateTimeDifference(
						_listArray.get(position).start_break_time,_listArray.get(position).end_break_time));

		return convertView;
	}

	String calculateTimeDifference(String startTime, String endTime) {

		String strDiff="";
		Date startDate = Common.convertStringDateTolocalDate(startTime);
		Date endDate = Common.convertStringDateTolocalDate(endTime);
		long diff = endDate.getTime() - startDate.getTime();

		int SECOND = 1000;
		int MINUTE = 60 * SECOND;
		int HOUR = 60 * MINUTE;
		long ms = diff;
		StringBuffer text = new StringBuffer("");
		if (ms > HOUR) {
		  text.append(ms / HOUR).append(" hrs ");
		  ms %= HOUR;
		}
		if (ms > MINUTE) {
		  text.append(ms / MINUTE).append(" mins ");
		  ms %= MINUTE;
		}
		if (ms > SECOND) {
		  text.append(ms / SECOND).append(" secs");
		}
		strDiff = text.toString();
		
		return strDiff;
	}
	
	public void addItems(ArrayList<EducatoreBreakHistory> _list)
	{
		_listArray.addAll(_list);
		notifyDataSetChanged();
	}

}
